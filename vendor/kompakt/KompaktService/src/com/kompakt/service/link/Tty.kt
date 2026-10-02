package com.kompakt.service.link

import android.os.ParcelFileDescriptor
import android.util.Log

/** Opening the serial port so it behaves like a pipe rather than a terminal. */
object Tty {

    private const val TAG = "KompaktSerial"
    private const val LIB = "kompaktserial"

    private val loaded: Boolean = try {
        System.loadLibrary(LIB)
        true
    } catch (t: Throwable) {
        Log.e(TAG, "lib$LIB.so did not load (${t.javaClass.simpleName}); " +
            "falling back to stty")
        false
    }

    /** @return the fd, or a negative errno. */
    private external fun openRaw(path: String): Int

    /** Open [path] raw and wrap the descriptor. */
    fun open(path: String): ParcelFileDescriptor? {
        if (loaded) {
            val fd = try {
                openRaw(path)
            } catch (t: Throwable) {
                Log.e(TAG, "openRaw threw (${t.javaClass.simpleName})")
                -1
            }
            if (fd >= 0) return ParcelFileDescriptor.adoptFd(fd)
            Log.w(TAG, "openRaw($path) failed: errno ${-fd}")
            return null
        }
        return openViaStty(path)
    }

    /** Fallback for a library that did not package: open first, then have toybox stty set termios on the tty we are. */
    private fun openViaStty(path: String): ParcelFileDescriptor? {
        val pfd = try {
            ParcelFileDescriptor.open(
                java.io.File(path),
                ParcelFileDescriptor.MODE_READ_WRITE,
            )
        } catch (t: Throwable) {
            Log.w(TAG, "open($path) failed: ${t.javaClass.simpleName}: ${t.message}")
            return null
        }
        try {
            val p = ProcessBuilder("/system/bin/stty", "-F", path, "raw", "-echo")
                .redirectErrorStream(true)
                .start()
            val said = p.inputStream.bufferedReader().use { it.readText() }.trim()
            val rc = p.waitFor()
            Log.i(TAG, "stty raw -echo rc=$rc${if (said.isEmpty()) "" else " $said"}")
            if (rc != 0) {
                // Worth saying loudly: without raw mode the port is useless, so this is a failure to connect, not a degraded.
                Log.e(TAG, "the port is still in canonical mode; Center cannot work")
            }
        } catch (t: Throwable) {
            Log.e(TAG, "could not run stty (${t.javaClass.simpleName}); " +
                "the port is still cooked")
        }
        return pfd
    }
}
