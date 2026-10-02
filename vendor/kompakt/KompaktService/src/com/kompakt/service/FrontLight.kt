package com.kompakt.service

import android.content.Context
import android.hardware.display.DisplayManager
import android.provider.Settings
import android.util.Log
import android.view.Display
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.roundToInt
import kotlin.math.sqrt

/** Front light steps for Brightness mode, a tenth of the perceptual slider each. */
object FrontLight {
    private const val TAG = "KompaktLight"
    private const val STEPS = 10

    /** Moves the light [by] steps, brighter when positive, and stops at either end. */
    fun step(ctx: Context, by: Int) {
        val dm = ctx.getSystemService(DisplayManager::class.java) ?: return
        val info = dm.getDisplay(Display.DEFAULT_DISPLAY)?.brightnessInfo ?: return
        val min = info.brightnessMinimum
        val max = info.brightnessMaximum
        val level = (toSlider(info.brightness, min, max) * STEPS).roundToInt()
        val next = (level + by).coerceIn(0, STEPS)
        if (next == level) return
        // A step by hand takes the light out of auto, as moving the slider in
        // manual mode would; otherwise auto would pull it straight back.
        Settings.System.putInt(ctx.contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE,
            Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL)
        dm.setBrightness(Display.DEFAULT_DISPLAY, fromSlider(next.toFloat() / STEPS, min, max))
        Log.i(TAG, "brightness step %d -> %d of %d".format(level, next, STEPS))
    }

    // BrightnessUtils: a square root curve for the bottom twelfth of the range,
    // a logarithmic one above it.
    private const val R = 0.5f
    private const val A = 0.17883277f
    private const val B = 0.28466892f
    private const val C = 0.55991073f

    /** Light, from [min] to [max], to its place on the slider, 0 to 1. */
    private fun toSlider(value: Float, min: Float, max: Float): Float {
        val n = if (max > min) ((value - min) / (max - min)).coerceIn(0f, 1f) * 12f else 0f
        return (if (n <= 1f) sqrt(n) * R else A * ln(n - B) + C).coerceIn(0f, 1f)
    }

    /** A place on the slider, 0 to 1, to light from [min] to [max]. */
    private fun fromSlider(pos: Float, min: Float, max: Float): Float {
        val n = if (pos <= R) (pos / R) * (pos / R) else exp((pos - C) / A) + B
        return min + (max - min) * (n.coerceIn(0f, 12f) / 12f)
    }
}
