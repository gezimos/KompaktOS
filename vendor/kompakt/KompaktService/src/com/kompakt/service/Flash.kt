package com.kompakt.service

import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.content.res.Configuration
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.WindowManager

/** The ghosting flush: drive every pixel to one rail and back. */
object Flash {

    /** Long enough for the waveform to complete and short enough not to read as a glitch. */
    private const val FLASH_MS = 180L

    private val handler = Handler(Looper.getMainLooper())

    @Volatile private var showing: View? = null

    fun show(context: Context) {
        // Must run on a Looper thread: WindowManager.addView posts to the view root's handler and needs one on the.
        handler.post {
            if (showing != null) return@post   // already flashing; nothing to add
            val wm = context.getSystemService(WindowManager::class.java) ?: return@post

            val night = (context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

            val view = View(context).apply {
                setBackgroundColor(if (night) Color.WHITE else Color.BLACK)
            }

            val lp = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                // Not focusable and not touchable: the flash must never take input, or a long press that triggers it would.
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.OPAQUE,
            ).apply {
                // Zero disables the window animation entirely.
                windowAnimations = 0
                layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
                title = "KompaktEinkFlash"
            }

            try {
                wm.addView(view, lp)
                showing = view
            } catch (t: Throwable) {
                Log.w(Sysfs.TAG, "flash: cannot add overlay (${t.javaClass.simpleName})")
                return@post
            }

            handler.postDelayed({ hide(context) }, FLASH_MS)
        }
    }

    private fun hide(context: Context) {
        val view = showing ?: return
        showing = null
        try {
            context.getSystemService(WindowManager::class.java)?.removeViewImmediate(view)
        } catch (t: Throwable) {
            Log.w(Sysfs.TAG, "flash: cannot remove overlay (${t.javaClass.simpleName})")
        }
    }
}
