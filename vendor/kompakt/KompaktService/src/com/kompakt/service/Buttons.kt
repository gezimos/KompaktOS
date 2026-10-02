package com.kompakt.service

import android.os.SystemClock
import android.util.Log

/** Fingerprint sensor gesture keys on their own uinput device. See jni/buttons.c. */
object Buttons {
    private const val LIB = "kompaktbuttons"
    private const val CLICK_MS = 50L

    const val TAP = 0
    const val HOLD = 1
    const val TRIPLE = 2
    const val SWIPE_UP = 3
    const val SWIPE_DOWN = 4

    private val loaded = try {
        System.loadLibrary(LIB)
        true
    } catch (t: Throwable) {
        Log.w(Sysfs.TAG, "buttons: $LIB not loaded (${t.javaClass.simpleName})")
        false
    }

    private external fun create(): Int
    private external fun press(fd: Int, index: Int, down: Boolean): Int

    /** The device, made on first use and kept for the life of the process. */
    private var fd = -1

    @Synchronized
    private fun device(): Int {
        if (fd >= 0) return fd
        if (!loaded) return -1
        val r = create()
        if (r < 0) {
            Log.w(Sysfs.TAG, "buttons: uinput device not created ($r)")
            return -1
        }
        fd = r
        // InputReader has to open the new device before its first key counts.
        SystemClock.sleep(200)
        return fd
    }

    fun down(index: Int) {
        val d = device()
        if (d >= 0) press(d, index, true)
    }

    fun up(index: Int) {
        val d = device()
        if (d >= 0) press(d, index, false)
    }

    fun click(index: Int) {
        down(index)
        SystemClock.sleep(CLICK_MS)
        up(index)
    }
}
