package com.kompakt.service

import android.content.Context
import android.hardware.display.BrightnessConfiguration
import android.hardware.display.DisplayManager
import android.util.Log
import kotlin.math.abs
import kotlin.math.ln
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * The auto brightness curve: the front light for each kind of light, kept as backlight
 * steps (1 to 255, 1 off) and shown as a percent of full light. It is the framework's
 * brightness configuration, kept per user and applied at boot; its "nits" are these
 * steps, as the overlay curve's (frameworks_base 0155).
 *
 * The kernel scales the 255 steps by CONFIG_LIGHTNESS_MAPPING_VALUE, 127, before the
 * PWM (duty = step * 127 / 255, at least 1 from step 2), so every other step is a
 * repeat, and neighbouring duties look the same: a tap moves along LADDER, the lowest
 * step of each duty that looks brighter than the one before and rounds to its own percent.
 *
 * Each kind of light is a band of lux, and inside a band the light is exactly the
 * row's step: the saved curve is a staircase, two points per row, so the row in
 * bold on the page is the light the user gets, never a blend of two rows.
 */
object LightCurve {
    private const val TAG = "KompaktLight"
    /** The steps a tap moves between: off, then duties 1, 2, 4, 5, 6, 7, 10, 11, 13 and about four per doubling up to 127. */
    private val LADDER = intArrayOf(1, 2, 5, 9, 11, 13, 15, 21, 23, 27, 33, 39, 47, 55,
        65, 77, 91, 109, 129, 153, 183, 217, 255)
    /** How far below a band's start the previous band's last point sits, in lux. */
    private const val EDGE = 0.1f

    /** The next ladder step above [step], or the top. */
    fun up(step: Int): Int = LADDER.firstOrNull { it > step } ?: LADDER.last()

    /** The last ladder step below [step], or off. */
    fun down(step: Int): Int = LADDER.lastOrNull { it < step } ?: LADDER.first()

    /** The kernel's PWM duty for [step], 0 off, as mt65xx_led_set computes it. */
    private fun duty(step: Int): Int = if (step <= 1) 0 else (step * 127 / 255).coerceAtLeast(1)

    /** What the row shows for [step]: Off, or the duty as a percent of full light. */
    fun label(ctx: Context, step: Int): String =
        if (step <= 1) ctx.getString(R.string.light_curve_off)
        else ctx.getString(R.string.light_curve_percent, (duty(step) * 100 + 63) / 127)

    /** The lux of each row: 0, then the overlay's levels. */
    fun lux(ctx: Context): IntArray {
        val res = ctx.resources
        return intArrayOf(0) +
            res.getIntArray(res.getIdentifier("config_autoBrightnessLevels", "array", "android"))
    }

    /** Where band [i] starts: 0, then halfway between two rows on a log scale, as nearest() divides them. */
    private fun bandStart(lux: IntArray, i: Int): Float =
        if (i == 0) 0f else sqrt((lux[i - 1] + 1f) * (lux[i] + 1f)) - 1f

    /** The curve the image ships, one step per row. */
    fun defaults(ctx: Context): IntArray {
        val res = ctx.resources
        val id = res.getIdentifier("config_autoBrightnessLcdBacklightValues", "array", "android")
        return res.getIntArray(id)
    }

    /** True while the saved curve is a staircase for these rows and edges; otherwise the framework blends the overlay rows. */
    fun matches(ctx: Context): Boolean {
        val curve = saved(ctx)?.curve ?: return false
        val lux = lux(ctx)
        return curve.second.size == 2 * lux.size - 1 && abs(curve.first[2] - bandStart(lux, 1)) < 0.01f
    }

    /** The steps in use: the saved staircase's rows, or the defaults. */
    fun levels(ctx: Context): IntArray {
        val defaults = defaults(ctx)
        val nits = saved(ctx)?.curve?.second ?: return defaults
        if (nits.size != 2 * defaults.size - 1) return defaults
        return IntArray(defaults.size) { nits[2 * it].roundToInt().coerceIn(1, 255) }
    }

    /** Saves [levels], one step per row, as a staircase; the framework applies it at once and after every boot. */
    fun save(ctx: Context, levels: IntArray) {
        val lux = lux(ctx)
        if (levels.size != lux.size) return
        val x = ArrayList<Float>()
        val y = ArrayList<Float>()
        for (i in lux.indices) {
            val step = levels[i].coerceIn(1, 255).toFloat()
            x.add(bandStart(lux, i))
            y.add(step)
            if (i + 1 < lux.size) {
                x.add(bandStart(lux, i + 1) - EDGE)
                y.add(step)
            }
        }
        val config = BrightnessConfiguration.Builder(x.toFloatArray(), y.toFloatArray()).build()
        ctx.getSystemService(DisplayManager::class.java)?.setBrightnessConfiguration(config)
        Log.i(TAG, "curve " + levels.joinToString(" "))
    }

    /** Back to the image's curve, as a staircase. */
    fun reset(ctx: Context) {
        save(ctx, defaults(ctx))
        Log.i(TAG, "curve reset")
    }

    private fun saved(ctx: Context): BrightnessConfiguration? =
        ctx.getSystemService(DisplayManager::class.java)?.brightnessConfiguration

    /** The row nearest [lux] by ratio, so 50 lux is the lit room's, not the bright room's. */
    fun nearest(ctx: Context, lux: Int): Int {
        val points = lux(ctx)
        val l = ln((lux + 1).toDouble())
        return points.indices.minByOrNull { abs(ln((points[it] + 1).toDouble()) - l) } ?: 0
    }
}
