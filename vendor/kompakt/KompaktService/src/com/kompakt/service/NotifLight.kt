package com.kompakt.service

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.provider.Settings
import android.service.notification.NotificationListenerService
import android.service.notification.NotificationListenerService.Ranking
import android.service.notification.NotificationListenerService.RankingMap
import android.service.notification.StatusBarNotification
import android.util.Log
import org.lineageos.internal.notification.LedValues
import org.lineageos.internal.notification.LineageNotificationLights

/** Long enough for the sysfs writes, short enough that a missed release costs nothing. */
private const val WAKE_MS = 5_000L

/** Drives the LED from notifications, coloured by the stock Lineage settings. See NOTIFICATION-LED.md. */
class NotifLight : NotificationListenerService() {

    private var screenReceiver: BroadcastReceiver? = null
    private var lights: LineageNotificationLights? = null
    private var zenWatch: ContentObserver? = null

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.i(Sysfs.TAG, "leds: notification listener connected")
        if (screenReceiver == null) {
            val r = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) = refresh()
            }
            screenReceiver = r
            registerReceiver(r, IntentFilter().apply {
                addAction(Intent.ACTION_SCREEN_ON)
                addAction(Intent.ACTION_SCREEN_OFF)
            })
        }
        // Calls back from inside the constructor, so refresh() tolerates null.
        if (lights == null) {
            lights = LineageNotificationLights(this) { refresh() }
        }
        val main = Handler(Looper.getMainLooper())
        if (zenWatch == null) {
            val o = object : ContentObserver(main) {
                override fun onChange(selfChange: Boolean) = readZen()
            }
            zenWatch = o
            contentResolver.registerContentObserver(
                Settings.Global.getUriFor(Settings.Global.ZEN_MODE), false, o,
            )
        }
        readZen()
        refresh()
    }

    override fun onListenerDisconnected() {
        screenReceiver?.let { runCatching { unregisterReceiver(it) } }
        screenReceiver = null
        zenWatch?.let { runCatching { contentResolver.unregisterContentObserver(it) } }
        zenWatch = null
        super.onListenerDisconnected()
    }

    /** calcLights does not read this itself; without it ZEN_ALLOW_LIGHTS never fires. */
    private fun readZen() {
        val zen = Settings.Global.getInt(contentResolver, Settings.Global.ZEN_MODE, 0)
        lights?.setZenMode(zen)
        refresh()
    }

    /** GMS's "uncertified" notice is permanent and undismissable here, so drop it. */
    private fun dropUncertifiedNotice(sbn: StatusBarNotification?): Boolean {
        val n = sbn?.notification ?: return false
        if (sbn.packageName != "com.google.android.gms") return false
        if (n.channelId != "uncertified_device") return false
        runCatching { cancelNotification(sbn.key) }
        return true
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (dropUncertifiedNotice(sbn)) return
        refresh()
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) = refresh()

    /** Held across the handover to the IO thread, or AOD suspends before it runs. */
    private val wake: PowerManager.WakeLock? by lazy {
        (getSystemService(Context.POWER_SERVICE) as? PowerManager)
            ?.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "kompakt:led-update")
            ?.apply { setReferenceCounted(false) }
    }

    /** Queue the LED work with the SoC held awake until it has actually run. */
    private fun onIoAwake(block: () -> Unit) {
        val lock = wake
        runCatching { lock?.acquire(WAKE_MS) }
        Sysfs.onIo {
            try {
                block()
            } finally {
                runCatching { lock?.release() }
            }
        }
    }

    /** Recomputed from the whole active set rather than tracked incrementally. */
    private fun refresh() {
        val lights = this.lights ?: return
        if (!provisioned()) {
            onIoAwake { Leds.off() }
            return
        }
        val ranks = runCatching { currentRanking }.getOrNull()
        val ranking = Ranking()
        val active = runCatching { activeNotifications }.getOrNull() ?: emptyArray()

        // The picker's live preview is an ongoing notification; it outranks everything.
        val forced = active.firstOrNull { sbn ->
            sbn.notification?.let { lights.isForcedOn(it) } == true
        }
        val chosen = forced ?: worthLighting(active, ranks, ranking).maxByOrNull { it.postTime }

        Log.i(
            Sysfs.TAG,
            "leds: refresh active=${active.size} forced=${forced != null} " +
                "chose=${chosen?.packageName}",
        )

        if (chosen == null) {
            onIoAwake { Leds.off() }
            return
        }

        val channel = if (ranks != null && ranks.getRanking(chosen.key, ranking)) {
            ranking.channel
        } else {
            null
        }
        val led = LedValues(channel?.lightColor ?: 0, defaultOnMs(), defaultOffMs())
        lights.calcLights(
            led,
            chosen.packageName,
            chosen.notification,
            interactive(),
            if (ranks != null && ranks.getRanking(chosen.key, ranking)) {
                ranking.suppressedVisualEffects
            } else {
                0
            },
        )

        if (!led.isEnabled) {
            Log.i(Sysfs.TAG, "leds: lineage says off for ${chosen.packageName}")
            onIoAwake { Leds.off() }
            return
        }
        val colour = led.color
        val onMs = led.onMs
        val offMs = led.offMs
        Log.i(
            Sysfs.TAG,
            "leds: -> ${chosen.packageName} colour=#${Integer.toHexString(colour)} " +
                "on=$onMs off=$offMs",
        )
        onIoAwake { Leds.apply(colour, onMs, offMs) }
    }

    private fun interactive(): Boolean =
        (getSystemService(Context.POWER_SERVICE) as? PowerManager)?.isInteractive ?: true

    private fun sysInt(name: String, fallback: Int): Int {
        val res = android.content.res.Resources.getSystem()
        val id = res.getIdentifier(name, "integer", "android")
        return if (id != 0) runCatching { res.getInteger(id) }.getOrDefault(fallback) else fallback
    }

    private fun defaultOnMs(): Int = sysInt("config_defaultNotificationLedOn", 500)

    private fun defaultOffMs(): Int = sysInt("config_defaultNotificationLedOff", 2_000)

    /** Setup posts its own notifications; the light has no business flashing then. */
    private fun provisioned(): Boolean =
        Settings.Global.getInt(contentResolver, Settings.Global.DEVICE_PROVISIONED, 0) == 1 &&
            Settings.Secure.getInt(contentResolver, "user_setup_complete", 0) == 1

    /** Drops ongoing, foreground-service, DND-silenced, and lights-off channels. */
    private fun worthLighting(
        active: Array<StatusBarNotification>,
        ranks: RankingMap?,
        into: Ranking,
    ): List<StatusBarNotification> = active.filter { sbn ->
        val n = sbn.notification
        n != null &&
            !sbn.isOngoing &&
            (n.flags and Notification.FLAG_FOREGROUND_SERVICE) == 0 &&
            (n.flags and Notification.FLAG_ONGOING_EVENT) == 0 &&
            passesDnd(ranks, into, sbn) &&
            channelWantsLights(ranks, into, sbn)
    }

    private fun channelWantsLights(
        ranks: RankingMap?,
        into: Ranking,
        sbn: StatusBarNotification,
    ): Boolean {
        if (ranks == null) return true
        if (!ranks.getRanking(sbn.key, into)) return true
        return into.channel?.shouldShowLights() ?: true
    }

    /** DND decides, via matchesInterruptionFilter(); unknown means light it. */
    private fun passesDnd(ranks: RankingMap?, into: Ranking, sbn: StatusBarNotification): Boolean {
        if (ranks == null) return true
        if (!ranks.getRanking(sbn.key, into)) return true
        return into.matchesInterruptionFilter()
    }
}
