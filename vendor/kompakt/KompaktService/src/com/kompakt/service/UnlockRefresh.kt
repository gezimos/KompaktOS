package com.kompakt.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log

/** The long-press refresh, run by itself after an unlock from the places the user chose. */
object UnlockRefresh {

    const val AOD = "aod"
    const val SLEEP = "sleep"
    const val LOCKSCREEN = "lockscreen"
    val SOURCES = listOf(AOD, SLEEP, LOCKSCREEN)

    /** The framework sends this once the unlocked screen is drawn, with "from" set to a source. */
    const val ACTION_UNLOCKED = "com.kompakt.service.action.UNLOCKED"

    /** A frame for the panel to finish painting the unlocked screen before the flush. */
    private const val DELAY_MS = 150L

    private val handler = Handler(Looper.getMainLooper())

    private fun key(source: String) = "unlock_refresh_$source"

    fun enabled(c: Context, source: String) =
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(key(source), false)

    fun setEnabled(c: Context, source: String, on: Boolean) =
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean(key(source), on).apply()

    fun onUnlocked(c: Context, from: String?) {
        if (from !in SOURCES || !enabled(c, from!!)) return
        Log.i(Sysfs.TAG, "unlock from $from: refreshing")
        val app = c.applicationContext
        handler.postDelayed({ Flash.show(app) }, DELAY_MS)
    }
}

/** Receives the framework's unlocked broadcast; only holders of STATUS_BAR_SERVICE can send it. */
class UnlockReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != UnlockRefresh.ACTION_UNLOCKED) return
        UnlockRefresh.onUnlocked(context, intent.getStringExtra("from"))
    }
}
