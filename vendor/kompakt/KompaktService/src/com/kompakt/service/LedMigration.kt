package com.kompakt.service

import android.content.Context
import android.provider.Settings
import android.util.Log
import lineageos.providers.LineageSettings

/** Carries our old LED preferences into the stock Lineage settings, once. See NOTIFICATION-LED.md. */
object LedMigration {

    private const val DONE = "led_migrated_v2"

    /** What v1 wrote for every migrated app, wrongly. See repairV1. */
    private const val V1_TIMING = ";1000;2000"

    /** Negative on/off means "use the default", so a migrated entry carries only colour. */
    private const val USE_DEFAULT_TIMING = ";-1;-1"

    private val COLOURS = mapOf(
        "white" to 0xFFFFFFFF.toInt(),
        "red" to 0xFFFF0000.toInt(),
        "green" to 0xFF00FF00.toInt(),
        "blue" to 0xFF0000FF.toInt(),
        "yellow" to 0xFFFFFF00.toInt(),
        "cyan" to 0xFF00FFFF.toInt(),
        "magenta" to 0xFFFF00FF.toInt(),
    )

    fun run(context: Context) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (prefs.getBoolean(DONE, false)) return
        runCatching { repairV1(context) }
            .onFailure { Log.w(Sysfs.TAG, "leds: v1 repair failed (${it.javaClass.simpleName})") }
        runCatching { migrate(context, prefs) }
            .onFailure { Log.w(Sysfs.TAG, "leds: migration failed (${it.javaClass.simpleName})") }
        prefs.edit().putBoolean(DONE, true).apply()
    }

    /** Undo v1's hardcoded timing where it already ran. */
    private fun repairV1(context: Context) {
        val current = LineageSettings.System.getString(
            context.contentResolver,
            LineageSettings.System.NOTIFICATION_LIGHT_PULSE_CUSTOM_VALUES,
        ) ?: return
        if (!current.contains(V1_TIMING)) return
        val fixed = current.replace(V1_TIMING, USE_DEFAULT_TIMING)
        LineageSettings.System.putString(
            context.contentResolver,
            LineageSettings.System.NOTIFICATION_LIGHT_PULSE_CUSTOM_VALUES,
            fixed,
        )
        Log.i(Sysfs.TAG, "leds: repaired v1 per-app timings to follow the default")
    }

    private fun migrate(context: Context, prefs: android.content.SharedPreferences) {
        Settings.System.putInt(context.contentResolver, "notification_light_pulse", 1)

        // Raw values: Prefs.ledColour would write defaults for every unset package.
        val ours = prefs.all
            .filterKeys { it.startsWith("led_colour_") }
            .mapNotNull { (key, value) ->
                val pkg = key.removePrefix("led_colour_")
                val argb = COLOURS[value as? String ?: return@mapNotNull null]
                if (pkg == "__system__" || argb == null) null else "$pkg=$argb$USE_DEFAULT_TIMING"
            }

        if (ours.isNotEmpty()) {
            val existing = runCatching {
                LineageSettings.System.getString(
                    context.contentResolver,
                    LineageSettings.System.NOTIFICATION_LIGHT_PULSE_CUSTOM_VALUES,
                )
            }.getOrNull()
            // Anything already in the stock settings wins: the user has been in
            // the new UI and that is a later decision than ours.
            val have = existing.orEmpty().split("|")
                .mapNotNull { it.substringBefore("=").takeIf { p -> p.isNotBlank() } }
                .toSet()
            val merged = (existing.orEmpty().split("|").filter { it.isNotBlank() } +
                ours.filterNot { it.substringBefore("=") in have }).joinToString("|")
            LineageSettings.System.putString(
                context.contentResolver,
                LineageSettings.System.NOTIFICATION_LIGHT_PULSE_CUSTOM_VALUES,
                merged,
            )
            LineageSettings.System.putInt(
                context.contentResolver,
                LineageSettings.System.NOTIFICATION_LIGHT_PULSE_CUSTOM_ENABLE,
                1,
            )
        }

        prefs.edit().apply {
            prefs.all.keys.filter { it.startsWith("led_colour_") }.forEach { remove(it) }
            remove("led")
        }.apply()

        Log.i(Sysfs.TAG, "leds: migrated ${ours.size} per-app colours to lineage settings")
    }
}
