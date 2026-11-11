package com.kompakt.service

import android.content.Context
import android.hardware.display.BrightnessConfiguration
import android.hardware.display.DisplayManager
import android.util.Log
import kotlin.math.abs
import kotlin.math.ln
import kotlin.math.roundToInt

/**
 * The auto brightness curve, learned from the brightness slider. It is the framework's
 * brightness configuration, kept per user and applied at boot; its "nits" are Android's
 * brightness numbers (1 to 255, 1 off), as the overlay curve's (frameworks_base 0155).
 *
 * It starts as the image's curve (kompakt_brightness.xml). Each time the slider is let go
 * with auto on, frameworks_base 0167 sends the ambient lux and the level chosen, and
 * [learn] writes that point into the curve: points for about the same light (NEAR, on a
 * log scale) are replaced, so at that light the curve gives that level from then on, and
 * between points the light slides from one to the next. The last choice wins.
 *
 * Kernel patch 0041 maps a number to the front light's PWM duty by a square law, 510 *
 * (n / 255)^2 of 1023; a percent is that duty over 510.
 */
object LightCurve {
    private const val TAG = "KompaktLight"
    /** Marks a saved curve as this one; a curve from an older page is replaced once at boot. */
    private const val CURVE_ID = "kompakt-learned"
    /** The image's curve, as the overlay: lux and numbers, off from 300 lux. */
    private val BASE_LUX = floatArrayOf(0f, 1f, 9f, 25f, 63f, 173f, 300f)
    private val BASE_STEP = floatArrayOf(31f, 31f, 40f, 48f, 40f, 18f, 1f)
    /** Two lights within this of each other on ln(lux + 1), about one and a half times, are the same light. */
    private const val NEAR = 0.4

    /** [step] as a percent of full light: its duty over 510. */
    fun percent(step: Int): Int = (duty(step) * 100 + 255) / 510

    /** The front light's PWM duty for [step], 0 off, as kernel patch 0041 computes it. */
    fun duty(step: Int): Int = if (step <= 1) 0 else maxOf(1, (step * step * 510 + 32512) / 65025)

    /** Off, or the percent of full light. */
    fun label(ctx: Context, step: Int): String =
        if (step <= 1) ctx.getString(R.string.light_curve_off)
        else ctx.getString(R.string.light_curve_percent, percent(step))

    /** The curve in use: lux and numbers. */
    fun points(ctx: Context): Pair<FloatArray, FloatArray> {
        val config = saved(ctx)
        if (config == null || config.description != CURVE_ID) return Pair(BASE_LUX.copyOf(), BASE_STEP.copyOf())
        val curve = config.curve
        return Pair(curve.first, curve.second)
    }

    /** The number the curve gives at [lux], straight between points, as the framework's spline. */
    fun at(ctx: Context, lux: Float): Int {
        val (x, y) = points(ctx)
        if (lux <= x.first()) return y.first().roundToInt()
        for (i in 1 until x.size) {
            if (lux <= x[i]) {
                val f = (lux - x[i - 1]) / (x[i] - x[i - 1])
                return (y[i - 1] + f * (y[i] - y[i - 1])).roundToInt()
            }
        }
        return y.last().roundToInt()
    }

    /** Writes the slider's point, [brightness] (0 to 1) at [lux], into the curve and saves it. */
    fun learn(ctx: Context, lux: Float, brightness: Float) {
        if (lux.isNaN() || lux < 0 || brightness.isNaN()) return
        val step = (1 + brightness.coerceIn(0f, 1f) * 254).roundToInt().coerceIn(1, 255).toFloat()
        val (x, y) = points(ctx)
        val t = ln(lux + 1.0)
        val keep = x.indices.filter { abs(ln(x[it] + 1.0) - t) >= NEAR }
        val px = ArrayList<Float>()
        val py = ArrayList<Float>()
        keep.forEach { px.add(x[it]); py.add(y[it]) }
        val at = px.indexOfFirst { it > lux }.let { if (it < 0) px.size else it }
        px.add(at, lux)
        py.add(at, step)
        // The dark end: the curve starts at 0 lux, so a point near it stands for it.
        if (px.first() > 0f) {
            px.add(0, 0f)
            py.add(0, py.first())
        }
        // The bright end: past the last point the light falls to off, unless it was chosen off.
        if (py.last() > 1f) {
            px.add(maxOf(px.last() * 1.5f, px.last() + 50f))
            py.add(1f)
        }
        save(ctx, px.toFloatArray(), py.toFloatArray())
        Log.i(TAG, "learned " + step.roundToInt() + " at " + lux + " lux")
    }

    private fun save(ctx: Context, x: FloatArray, y: FloatArray) {
        val config = BrightnessConfiguration.Builder(x, y).setDescription(CURVE_ID).build()
        ctx.getSystemService(DisplayManager::class.java)?.setBrightnessConfiguration(config)
        Log.i(TAG, "curve " + x.indices.joinToString(" ") { "" + x[it].roundToInt() + ":" + y[it].roundToInt() })
    }

    /** Back to the image's curve: everything learned is forgotten. */
    fun reset(ctx: Context) {
        save(ctx, BASE_LUX.copyOf(), BASE_STEP.copyOf())
        Log.i(TAG, "curve reset")
    }

    /** At boot: a curve from an older page, set by hand, is replaced by the image's once. */
    fun migrate(ctx: Context) {
        val config = saved(ctx) ?: return
        if (config.description == CURVE_ID) return
        reset(ctx)
        Log.i(TAG, "curve from an older page replaced")
    }

    private fun saved(ctx: Context): BrightnessConfiguration? =
        ctx.getSystemService(DisplayManager::class.java)?.brightnessConfiguration
}
