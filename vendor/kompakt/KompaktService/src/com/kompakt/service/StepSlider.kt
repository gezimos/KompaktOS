package com.kompakt.service

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.view.MotionEvent
import android.view.View
import kotlin.math.roundToInt

/**
 * A slider that stops only at its labels. Ink on paper, the thumb an outlined
 * paper circle, so it never fills a shape. A tap picks; a swipe across it belongs to the page.
 */
@SuppressLint("ViewConstructor")
class StepSlider(
    context: Context,
    private val labels: List<String>,
    private var chosen: Int,
    private val ink: Int,
    private val paper: Int,
    private val onPick: (Int) -> Unit,
) : View(context) {

    private val d = resources.displayMetrics.density
    private val line = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ink
        strokeWidth = 2 * d
        style = Paint.Style.STROKE
    }
    private val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = paper
        style = Paint.Style.FILL
    }
    private val text = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ink
        textSize = 14 * resources.displayMetrics.scaledDensity
        textAlign = Paint.Align.CENTER
    }
    private val thumb = 12 * d
    private val tick = 4 * d
    private val side = 28 * d
    private var dragging = -1

    init {
        isClickable = true
        contentDescription = labels.getOrNull(chosen)
    }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        setMeasuredDimension(MeasureSpec.getSize(widthSpec), resolveSize((64 * d).toInt(), heightSpec))
    }

    private fun stopX(i: Int): Float {
        val span = width - 2 * side
        return side + if (labels.size > 1) span * i / (labels.size - 1) else span / 2
    }

    private fun nearest(x: Float): Int {
        if (labels.size < 2) return 0
        val step = (width - 2 * side) / (labels.size - 1)
        return ((x - side) / step).roundToInt().coerceIn(0, labels.size - 1)
    }

    override fun onDraw(c: Canvas) {
        val y = 20 * d
        val shown = if (dragging >= 0) dragging else chosen
        c.drawLine(stopX(0), y, stopX(labels.size - 1), y, line)
        for (i in labels.indices) {
            val x = stopX(i)
            c.drawLine(x, y - tick, x, y + tick, line)
            text.typeface = if (i == shown) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
            c.drawText(labels[i], x, y + thumb + 22 * d, text)
        }
        val tx = stopX(shown)
        c.drawCircle(tx, y, thumb, fill)
        c.drawCircle(tx, y, thumb, line)
    }

    private val slop = android.view.ViewConfiguration.get(context).scaledTouchSlop
    private var downX = 0f
    private var downY = 0f
    private var swiped = false

    /** Kompakt: a tap picks the stop under the finger; a swipe across the slider is the page's and picks nothing. */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(e: MotionEvent): Boolean {
        when (e.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                downX = e.x
                downY = e.y
                swiped = false
                dragging = nearest(e.x)
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                if (!swiped && (kotlin.math.abs(e.x - downX) > slop || kotlin.math.abs(e.y - downY) > slop)) {
                    swiped = true
                    dragging = -1
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                val i = nearest(e.x)
                dragging = -1
                if (!swiped && i != chosen) {
                    chosen = i
                    contentDescription = labels[i]
                    onPick(i)
                }
                invalidate()
            }
            MotionEvent.ACTION_CANCEL -> {
                dragging = -1
                invalidate()
            }
        }
        return true
    }
}
