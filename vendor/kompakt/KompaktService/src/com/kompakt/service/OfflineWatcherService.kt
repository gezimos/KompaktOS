package com.kompakt.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.os.Looper
import android.os.Handler
import android.telephony.TelephonyManager
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
import kotlin.concurrent.thread

/** The hardware offline switch. */
class OfflineWatcherService : Service() {

    private companion object {
        const val DEV = "/dev/input/event0"
        const val CHANNEL = "offline_switch"
        const val NOTIF_ID = 4711

        // struct input_event on a 64-bit kernel: struct timeval (2 x 8 bytes), then __u16 type, __u16 code, __s32 value.
        const val EVENT_SIZE = 24
        const val OFF_TYPE = 16
        const val EV_KEY = 1
        const val KEY_F6 = 64      // to online
        const val KEY_F7 = 65      // to offline
    }

    @Volatile private var running = false

    /** Unregistered in onDestroy; null whenever we are not registered. */
    private var screenReceiver: BroadcastReceiver? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!running) {
            running = true
            Sysfs.onIo { Leds.probe() }
            TetherOffload.keepOff(this)
            registerScreenOnWatcher()
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
                if (intent.action != Intent.ACTION_SCREEN_ON) return
                // setMode already hops to the IO thread.
                Eink.setMode(Prefs.mode(context))
            }
        }
        screenReceiver = r
        registerReceiver(r, IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
        })
        FingerScroll.sync(this)
    }

    override fun onDestroy() {
        screenReceiver?.let { runCatching { unregisterReceiver(it) } }
        screenReceiver = null
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

    /** A persistent, silent notification while offline, removed when it clears. */
    private fun showState(offline: Boolean) {
        val nm = getSystemService(NotificationManager::class.java) ?: return
        if (!offline) {
            nm.cancel(NOTIF_ID)
            return
        }
        nm.createNotificationChannel(
            NotificationChannel(CHANNEL, getString(R.string.chan_offline), NotificationManager.IMPORTANCE_LOW)
                .apply { setShowBadge(false); enableVibration(false); setSound(null, null) }
        )
        val n = Notification.Builder(this, CHANNEL)
            .setSmallIcon(R.drawable.ic_offline)
            .setContentTitle(getString(R.string.offline_on))
            .setContentText(getString(R.string.offline_on_desc))
            .setOngoing(true)
            .setShowWhen(false)
            .build()
        nm.notify(NOTIF_ID, n)
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
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        context.startService(Intent(context, OfflineWatcherService::class.java))
        ensureWatcherEnabled(context)
        ensureNotifLightEnabled(context)
        grantLauncherDefaultsOnce(context)
        LedMigration.run(context)
        context.startService(
            Intent(context, com.kompakt.service.link.LinkService::class.java))
        // Re-apply the e-ink mode: the panel keeps whatever the last writer left, and nothing else restores it across a.
        Eink.setMode(Prefs.mode(context))
        // The sleep screen is an array in meink.ko, reloaded from vendor at every boot.
        Sysfs.onIo { SleepImage.restore(context) }
    }
}


/** Fires the LED self test on demand: adb shell am broadcast -a com.kompakt.service.action.LED_TEST Exported so. */
class LedTestReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Sysfs.onIo { Leds.selfTest() }
    }
}


/** Add EinkAppWatcher to the enabled accessibility services if it is not there. */
private fun ensureWatcherEnabled(context: Context) {
    val component = "com.kompakt.service/com.kompakt.service.EinkAppWatcher"
    try {
        val cr = context.contentResolver
        val current = Settings.Secure.getString(
            cr, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES).orEmpty()
        if (current.split(':').contains(component)) return
        val updated = if (current.isEmpty()) component else "$current:$component"
        Settings.Secure.putString(cr, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, updated)
        Settings.Secure.putInt(cr, Settings.Secure.ACCESSIBILITY_ENABLED, 1)
        Log.i(Sysfs.TAG, "eink: enabled the per-app watcher")
    } catch (t: Throwable) {
        Log.w(Sysfs.TAG, "eink: cannot enable the watcher (${t.javaClass.simpleName})")
    }
}


/** Enable NotifLight, through the API that owns the setting. */
private fun ensureNotifLightEnabled(context: Context) {
    try {
        val component = ComponentName(context, NotifLight::class.java)
        context.getSystemService(NotificationManager::class.java)
            ?.setNotificationListenerAccessGranted(component, true)
        Log.i(Sysfs.TAG, "leds: notification listener access granted")
    } catch (t: Throwable) {
        Log.w(Sysfs.TAG, "leds: cannot grant listener access (${t.javaClass.simpleName})")
    }
}
