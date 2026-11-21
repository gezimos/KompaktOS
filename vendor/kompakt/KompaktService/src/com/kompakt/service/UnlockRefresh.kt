package com.kompakt.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/** The unlock refresh: the window manager draws it between the lock screen and home, from the places the user chose. */
object UnlockRefresh {

    const val AOD = "aod"
    const val SLEEP = "sleep"
    const val LOCKSCREEN = "lockscreen"
    val SOURCES = listOf(AOD, SLEEP, LOCKSCREEN)

    /** The framework sends this once the unlocked screen is drawn, with "from" set to a source. */
    const val ACTION_UNLOCKED = "com.kompakt.service.action.UNLOCKED"

    /** The sources that are on, as "aod,sleep", read by KompaktUnlockHold (fb 0165). */
    private const val PROP = "persist.sys.kompakt.unlock_flash"

    private fun key(source: String) = "unlock_refresh_$source"

    fun enabled(c: Context, source: String) =
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(key(source), false)

    fun setEnabled(c: Context, source: String, on: Boolean) {
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean(key(source), on).apply()
        sync(c)
    }

    /** Hands the choices to the framework; also at start, for settings saved before 1.3. */
    fun sync(c: Context) {
        val on = SOURCES.filter { enabled(c, it) }.joinToString(",")
        try {
            android.os.SystemProperties.set(PROP, on)
        } catch (t: Throwable) {
            Log.w(Sysfs.TAG, "unlock refresh: cannot set $PROP (${t.javaClass.simpleName})")
        }
    }

    fun onUnlocked(c: Context, from: String?) {
        if (from in SOURCES && enabled(c, from!!)) Log.i(Sysfs.TAG, "unlock from $from: refreshed by the framework")
    }
}

/** Receives the framework's unlocked broadcast; only holders of STATUS_BAR_SERVICE can send it. */
class UnlockReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != UnlockRefresh.ACTION_UNLOCKED) return
        UnlockRefresh.onUnlocked(context, intent.getStringExtra("from"))
    }
}
