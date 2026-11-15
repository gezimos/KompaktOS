package com.kompakt.service

import android.app.NotificationManager
import android.app.Service
import android.os.Looper
import android.os.Handler
import android.telephony.TelephonyManager
import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import android.util.Log
import java.io.DataInputStream
import java.io.File
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

/** The hardware offline switch. */
class OfflineWatcherService : Service() {

    private companion object {
        const val DEV = "/dev/input/event0"
        const val NOTIF_ID = 4711

        // struct input_event on a 64-bit kernel: struct timeval (2 x 8 bytes), then __u16 type, __u16 code, __s32 value.
        const val EVENT_SIZE = 24
        const val OFF_TYPE = 16
        const val EV_KEY = 1
        const val KEY_F6 = 64      // to online
        const val KEY_F7 = 65      // to offline
        /** MuditaOS 1.6.1 gives the SIM power-down the same 3 s at shutdown. */
        const val SHUTDOWN_SIM_TIMEOUT_S = 3L
    }

    @Volatile private var running = false

    /** Unregistered in onDestroy; null whenever we are not registered. */
    private var screenReceiver: BroadcastReceiver? = null
    private var shutdownReceiver: BroadcastReceiver? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!running) {
            running = true
            TetherOffload.keepOff(this)
            registerScreenOnWatcher()
            registerShutdownWatcher()
            EinkAppWatcher.start(this)
            OfflineExtras.guardCamera(this)
            // Sync to the real position first.
            Sysfs.onIo { onFlipped(OfflineSwitch.engaged()) }
            startReader()
        }
        return START_STICKY
    }

    /** Re-apply the e-ink mode on screen on; read the sensor only while on. */
    private fun registerScreenOnWatcher() {
        if (screenReceiver != null) return
        val r = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                FingerScroll.sync(context)
                val locked = context.getSystemService(KeyguardManager::class.java)?.isKeyguardLocked == true
                // The system's top task now, not the last one seen: a missed change cannot carry over.
                val pkg = EinkAppWatcher.topNow() ?: EinkAppWatcher.foreground
                // Kompakt: invert is off while locked and back for the app in front on unlock.
                AppInvert.apply(context, if (locked || intent.action == Intent.ACTION_SCREEN_OFF) null else pkg)
                if (intent.action != Intent.ACTION_SCREEN_ON &&
                    intent.action != Intent.ACTION_USER_PRESENT) return
                // Kompakt: a fingerprint unlock sends USER_PRESENT before SCREEN_ON, so both ask where we are.
                val mode = if (!locked && pkg != null && Prefs.perAppEnabled(context))
                    Prefs.appMode(context, pkg) else Prefs.mode(context)
                Log.i(Sysfs.TAG, "eink: ${intent.action?.substringAfterLast('.')} locked=$locked front=$pkg -> $mode")
                Eink.usePattern(context, if (locked) null else pkg)
                // setMode already hops to the IO thread.
                Eink.setMode(mode)
            }
        }
        screenReceiver = r
        registerReceiver(r, IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_USER_PRESENT)
        })
        FingerScroll.sync(this)
    }

    /** Kompakt: power both SIMs down at shutdown, as MuditaOS 1.6.1 does, so every boot starts with a locked SIM. */
    private fun registerShutdownWatcher() {
        if (shutdownReceiver != null) return
        val r = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val pending = goAsync()
                thread(name = "sim-power-down") {
                    try { simsDown(context) } finally { pending.finish() }
                }
            }
        }
        shutdownReceiver = r
        registerReceiver(r, IntentFilter(Intent.ACTION_SHUTDOWN))
    }

    private fun simsDown(c: Context) {
        val tm = c.getSystemService(TelephonyManager::class.java) ?: return
        val slots = tm.supportedModemCount
        val done = CountDownLatch(slots)
        for (slot in 0 until slots) {
            try {
                tm.setSimPowerStateForSlot(slot, TelephonyManager.CARD_POWER_DOWN, Runnable::run) { result ->
                    Log.i(Sysfs.TAG, "shutdown: SIM $slot power down -> $result")
                    done.countDown()
                }
            } catch (t: Throwable) {
                Log.w(Sysfs.TAG, "shutdown: SIM $slot power down failed (${t.javaClass.simpleName})")
                done.countDown()
            }
        }
        if (!done.await(SHUTDOWN_SIM_TIMEOUT_S, TimeUnit.SECONDS)) Log.w(Sysfs.TAG, "shutdown: SIM power down timed out")
    }

    override fun onDestroy() {
        screenReceiver?.let { runCatching { unregisterReceiver(it) } }
        screenReceiver = null
        shutdownReceiver?.let { runCatching { unregisterReceiver(it) } }
        shutdownReceiver = null
        FingerScroll.stop()
        TetherOffload.stop(this)
        running = false
    }

    private fun startReader() = thread(name = "offline-switch", isDaemon = true) {
        val f = File(DEV)
        if (!f.canRead()) {
            // Almost certainly the missing sepolicy rule rather than the node being absent, so say which it is.
            Log.e(Sysfs.TAG, "cannot read $DEV (exists=${f.exists()}); check sepolicy")
            return@thread
        }
        try {
            DataInputStream(FileInputStream(f)).use { input ->
                val buf = ByteArray(EVENT_SIZE)
                while (running) {
                    input.readFully(buf)
                    val bb = ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN)
                    val type = bb.getShort(OFF_TYPE).toInt() and 0xFFFF
                    val code = bb.getShort(OFF_TYPE + 2).toInt() and 0xFFFF
                    val value = bb.getInt(OFF_TYPE + 4)
                    // Act on the press only.
                    if (type == EV_KEY && value == 1 && (code == KEY_F6 || code == KEY_F7)) {
                        onFlipped(OfflineSwitch.engaged())
                    }
                }
            }
        } catch (t: Throwable) {
            Log.e(Sysfs.TAG, "input reader stopped", t)
        }
    }

    @Volatile private var last: Boolean? = null

    private fun onFlipped(offline: Boolean) {
        val changed = last != offline
        last = offline
        // Kompakt: init starts the modem on 1; booted with the switch on, it waits here (vendor kompakt-modem.rc).
        try {
            android.os.SystemProperties.set("vendor.kompakt.modem", if (offline) "0" else "1")
        } catch (t: Throwable) {
            Log.e(Sysfs.TAG, "offline: vendor.kompakt.modem write failed", t)
        }
        setRadioPower(!offline)
        // Only buzz on a real change.
        if (changed) {
            try {
                Settings.Global.putInt(contentResolver, "HWSwitch_lock", if (offline) 1 else 0)
            } catch (t: Throwable) {
                Log.e(Sysfs.TAG, "offline: HWSwitch_lock write failed", t)
            }
        }
        // Wi-Fi, Bluetooth, camera and mic, as chosen in the Offline tab.
        OfflineExtras.apply(this, offline)
        // An install that predates this may still hold our old ongoing notification.
        getSystemService(NotificationManager::class.java)?.cancel(NOTIF_ID)
    }

    /** Power the modem down, without engaging airplane mode. */
    private fun setRadioPower(on: Boolean) {
        // Airplane mode turns the radio off with the same reason, so powering on here would undo it.
        if (on && Settings.Global.getInt(contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0) != 0) {
            Log.i(Sysfs.TAG, "offline: airplane mode is on, radio stays off")
            return
        }
        try {
            val tm = getSystemService(TelephonyManager::class.java)
            if (tm == null) {
                Log.w(Sysfs.TAG, "offline: no TelephonyManager")
                return
            }
            tm.setRadioPower(on)
            Log.i(Sysfs.TAG, "offline: radio power -> $on")
        } catch (t: Throwable) {
            Log.e(Sysfs.TAG, "offline: radio power failed", t)
        }
    }

    /** Distinguishable without looking: one pulse for offline, two for online. */
    private fun buzz(offline: Boolean) {
        val v = getSystemService(Vibrator::class.java) ?: return
        val effect = if (offline) {
            VibrationEffect.createOneShot(120, VibrationEffect.DEFAULT_AMPLITUDE)
        } else {
            VibrationEffect.createWaveform(longArrayOf(0, 60, 90, 60), -1)
        }
        v.vibrate(effect)
    }
}

/** Restarts the watcher after boot and after an APK update kills it. */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_MY_PACKAGE_REPLACED) {
            context.startService(Intent(context, OfflineWatcherService::class.java))
            context.startService(
                Intent(context, com.kompakt.service.link.LinkService::class.java))
            return
        }
        // Kompakt: before the first unlock only the sleep screen, from device storage; the rest waits for BOOT_COMPLETED.
        if (intent.action == Intent.ACTION_LOCKED_BOOT_COMPLETED) {
            val done = goAsync()
            Sysfs.onIo { try { SleepImage.restore(context) } finally { done.finish() } }
            return
        }
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        context.startService(Intent(context, OfflineWatcherService::class.java))
        dropOldWatcher(context)
        ensureUncertifiedNoticeEnabled(context)
        grantLauncherDefaultsOnce(context)
        LedMigration.run(context)
        // A curve saved in the steps of the old front light map would be near dark on kernel 0041's map.
        LightCurve.migrate(context)
        context.startService(
            Intent(context, com.kompakt.service.link.LinkService::class.java))
        // The offset lives in meink and the fast count in the loader; both reset at boot. Set before the mode loads.
        Prefs.migrateSpeeds(context)
        Eink.setSpeeds(Prefs.fastSteps(context), Prefs.textSteps(context))
        // The dither pattern is the loader's, reset at boot.
        Eink.setPattern(Prefs.pattern(context))
        Eink.setDitherLight(Prefs.ditherLight(context))
        // The full refresh interval is a meink_hal parameter, reset at boot.
        Eink.setCleanEvery(Prefs.cleanEvery(context))
        // Re-apply the e-ink mode: the panel keeps whatever the last writer left, and nothing else restores it across a.
        Eink.setMode(Prefs.mode(context))
        // The sleep screen is an array in meink.ko, reloaded from vendor at every boot.
        Sysfs.onIo { SleepImage.restore(context) }
        // The clearing flash at every lock is a meink_hal module parameter, reset at boot.
        LockFlash.apply()
        UnlockRefresh.sync(context)
        // The LEDs start off; the lock screen's warm flashlight shortcut reads this.
        Sysfs.onIo { Torch.publish(context) }
    }
}

/** Kompakt: the lock screen shortcuts (SystemUI) and the power button toggle the LEDs here. */
class TorchReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (!Torch.available(Torch.WARM)) return
        when (intent.action) {
            Torch.ACTION_TOGGLE_WARM -> Torch.set(context, Torch.WARM, !Torch.isOn(Torch.WARM))
            Torch.ACTION_TOGGLE_BOTH -> Torch.toggleBoth(context)
        }
    }
}


/** The per-app watcher was an accessibility service once; take it out of the enabled list. */
private fun dropOldWatcher(context: Context) {
    val component = "com.kompakt.service/com.kompakt.service.EinkAppWatcher"
    try {
        val cr = context.contentResolver
        val current = Settings.Secure.getString(
            cr, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES).orEmpty()
        val list = current.split(':').filter { it.isNotEmpty() }
        if (!list.contains(component)) return
        val rest = list.filter { it != component }
        Settings.Secure.putString(cr, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, rest.joinToString(":"))
        // Only ours was on: accessibility was switched on for it, so switch it off again.
        if (rest.isEmpty()) Settings.Secure.putInt(cr, Settings.Secure.ACCESSIBILITY_ENABLED, 0)
        Log.i(Sysfs.TAG, "eink: removed the old accessibility watcher")
    } catch (t: Throwable) {
        Log.w(Sysfs.TAG, "eink: cannot remove the old watcher (${t.javaClass.simpleName})")
    }
}


/** Enable UncertifiedNotice, through the API that owns the setting. */
private fun ensureUncertifiedNoticeEnabled(context: Context) {
    try {
        val component = ComponentName(context, UncertifiedNotice::class.java)
        context.getSystemService(NotificationManager::class.java)
            ?.setNotificationListenerAccessGranted(component, true)
        Log.i(Sysfs.TAG, "notices: listener access granted")
    } catch (t: Throwable) {
        Log.w(Sysfs.TAG, "notices: cannot grant listener access (${t.javaClass.simpleName})")
    }
}
