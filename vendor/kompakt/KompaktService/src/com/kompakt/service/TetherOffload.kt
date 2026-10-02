package com.kompakt.service

import android.content.Context
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log

/** Keeps the tethering offload HAL off, or the hotspot gives no internet. */
object TetherOffload {
    private const val TAG = "KompaktTether"
    /** Settings.Global.TETHER_OFFLOAD_DISABLED, which is hidden API. */
    private const val KEY = "tether_offload_disabled"

    private var observer: ContentObserver? = null

    fun keepOff(c: Context) {
        val ctx = c.applicationContext
        enforce(ctx)
        if (observer != null) return
        val o = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) = enforce(ctx)
        }
        observer = o
        ctx.contentResolver.registerContentObserver(Settings.Global.getUriFor(KEY), false, o)
    }

    fun stop(c: Context) {
        observer?.let { c.applicationContext.contentResolver.unregisterContentObserver(it) }
        observer = null
    }

    private fun enforce(ctx: Context) {
        val cr = ctx.contentResolver
        if (Settings.Global.getInt(cr, KEY, 0) == 1) return
        runCatching { Settings.Global.putInt(cr, KEY, 1) }
            .onSuccess { Log.i(TAG, "tethering offload turned off") }
            .onFailure { Log.e(TAG, "cannot turn tethering offload off", it) }
    }
}
