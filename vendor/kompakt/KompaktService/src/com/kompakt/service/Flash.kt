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

    /** Two panel paints (each about 230 ms apart), so one always catches it; 180 ms fell between them. */
    private const val FLASH_MS = 480L

    private val handler = Handler(Looper.getMainLooper())

    @Volatile private var showing: View? = null

    /** While the overlay is up; the per-app watcher must not take it for an app switch. */
    val active: Boolean get() = showing != null

    fun show(context: Context) {
        // A Fast speed below 7 cannot reach black; the flush runs at 7 and the speed comes back after.
        Sysfs.onIo {
            val boosted = Eink.flushSpeed(context, true)
            handler.post { add(context, boosted) }
        }
    }

    private fun add(context: Context, boosted: Boolean) {
        // Must run on a Looper thread: WindowManager.addView posts to the view root's handler and needs one on the.
        if (showing != null) {             // already flashing; nothing to add
            if (boosted) Sysfs.onIo { Eink.flushSpeed(context, false) }
            return
        }
        val wm = context.getSystemService(WindowManager::class.java) ?: return

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
            if (boosted) Sysfs.onIo { Eink.flushSpeed(context, false) }
            return
        }

        handler.postDelayed({ hide(context) }, FLASH_MS)
        // The page comes back at 7 too; the speed returns once it is drawn.
        if (boosted) handler.postDelayed({ Sysfs.onIo { Eink.flushSpeed(context, false) } }, FLASH_MS + FLASH_MS)
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
