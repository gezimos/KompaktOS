package com.kompakt.service

import android.content.Context
import android.provider.Settings
import android.util.Log

/** Colour inversion while a chosen app is in front, for apps that only have a dark theme. */
object AppInvert {

    private const val SETTING = Settings.Secure.ACCESSIBILITY_DISPLAY_INVERSION_ENABLED
    private const val OURS = "invert_ours"

    private fun prefs(c: Context) = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    private fun key(pkg: String) = "invert_" + pkg

    fun wanted(c: Context, pkg: String): Boolean = prefs(c).getBoolean(key(pkg), false)

    fun setWanted(c: Context, pkg: String, on: Boolean) =
        prefs(c).edit().apply {
            if (on) putBoolean(key(pkg), true) else remove(key(pkg))
        }.apply()

    /** Bring the inversion in line with the app now in front. Only ever undoes an inversion we made. */
    fun apply(context: Context, pkg: String) {
        val c = context.applicationContext
        val cr = c.contentResolver
        val on = Settings.Secure.getInt(cr, SETTING, 0) == 1
        val ours = prefs(c).getBoolean(OURS, false)
        try {
            if (wanted(c, pkg)) {
                if (!on) {
                    Settings.Secure.putInt(cr, SETTING, 1)
                    prefs(c).edit().putBoolean(OURS, true).apply()
                    Log.i(Sysfs.TAG, "invert: on for $pkg")
                }
            } else if (ours) {
                if (on) Settings.Secure.putInt(cr, SETTING, 0)
                prefs(c).edit().putBoolean(OURS, false).apply()
                Log.i(Sysfs.TAG, "invert: off for $pkg")
            }
        } catch (t: Throwable) {
            Log.w(Sysfs.TAG, "invert: cannot switch (${t.javaClass.simpleName})")
        }
    }
}
