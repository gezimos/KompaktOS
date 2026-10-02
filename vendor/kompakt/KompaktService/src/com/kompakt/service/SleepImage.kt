package com.kompakt.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.Paint
import android.graphics.RectF
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import kotlin.math.roundToInt

/** Kompakt: the sleep screen meink paints at power down, via /sys/einkinfo/sleep_image. */
object SleepImage {
    private const val NODE = "/sys/einkinfo/sleep_image"
    /** The frame built into meink.ko (ours, from the vendor image). The kernel keeps it aside before the first write. */
    private const val STOCK_NODE = "/sys/einkinfo/sleep_image_stock"

    private const val W = 480
    private const val H = 800
    private const val BYTES = W * H * 4

    /** The frame BootReceiver writes back. */
    private const val FRAME = "sleep_image.frame"
    /** The picture it was made from, so Fit and Fill can be switched afterwards. */
    private const val SOURCE = "sleep_image_source.png"

    private const val KEY_FILL = "sleep_fill"

    /** Needs our vendor image: the stock meink_loader has no such node. */
    fun available(): Boolean = Sysfs.writable(NODE)

    fun isCustom(c: Context): Boolean = File(c.filesDir, FRAME).length() == BYTES.toLong()

    fun fill(c: Context): Boolean =
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_FILL, false)

    fun setFill(c: Context, v: Boolean) =
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean(KEY_FILL, v).apply()

    /** BootReceiver: meink has just loaded Mudita's picture again. */
    fun restore(c: Context) {
        val f = File(c.filesDir, FRAME)
        if (!available() || f.length() != BYTES.toLong()) return
        if (write(f.readBytes())) Log.i(Sysfs.TAG, "sleep: picture restored")
    }

    /** A new picture from the chooser. Blocking: decodes and dithers. */
    fun set(c: Context, uri: Uri): Boolean {
        val src = try {
            decode(c, uri)
        } catch (t: Throwable) {
            Log.w(Sysfs.TAG, "sleep: cannot read the picture (${t.javaClass.simpleName})")
            return false
        }
        save(File(c.filesDir, SOURCE)) { src.compress(Bitmap.CompressFormat.PNG, 100, it) }
        src.recycle()
        return apply(c)
    }

    /** Make the frame from the kept picture again, after Fit or Fill changed. */
    fun apply(c: Context): Boolean {
        val src = BitmapFactory.decodeFile(File(c.filesDir, SOURCE).path) ?: return false
        val frame = render(src, fill(c))
        src.recycle()
        if (!write(frame)) return false
        save(File(c.filesDir, FRAME)) { it.write(frame) }
        return true
    }

    /** Mudita's picture back, now and after every later boot. */
    fun reset(c: Context): Boolean {
        File(c.filesDir, FRAME).delete()
        File(c.filesDir, SOURCE).delete()
        val stock = read(STOCK_NODE) ?: return false
        return write(stock)
    }

    /** What the phone shows when it sleeps, upright, or null if unreadable. */
    fun current(): Bitmap? {
        val frame = read(NODE) ?: return null
        val px = IntArray(W * H) { i ->
            // Rotated 180 degrees: the first pixel of the picture is the last in the frame.
            val g = frame[(W * H - 1 - i) * 4].toInt() and 0xff
            Color.rgb(g, g, g)
        }
        return Bitmap.createBitmap(px, W, H, Bitmap.Config.ARGB_8888)
    }

    // ------------------------------------------------------------ the image

    /** Decode at most twice the panel size; camera photos are far larger. */
    private fun decode(c: Context, uri: Uri): Bitmap {
        val bmp = ImageDecoder.decodeBitmap(ImageDecoder.createSource(c.contentResolver, uri)) {
                d, info, _ ->
            d.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            val sample = minOf(info.size.width / (W * 2), info.size.height / (H * 2))
            if (sample > 1) d.setTargetSampleSize(sample)
        }
        val scale = maxOf(W * 2f / bmp.width, H * 2f / bmp.height)
        if (scale >= 1f) return bmp
        val out = Bitmap.createScaledBitmap(
            bmp, (bmp.width * scale).roundToInt(), (bmp.height * scale).roundToInt(), true)
        bmp.recycle()
        return out
    }

    /** Fit on white or crop to fill, then Floyd-Steinberg down to 16 greys. */
    private fun render(src: Bitmap, fill: Boolean): ByteArray {
        val canvasBmp = Bitmap.createBitmap(W, H, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(canvasBmp)
        canvas.drawColor(Color.WHITE)
        val scale = if (fill) maxOf(W.toFloat() / src.width, H.toFloat() / src.height)
                    else minOf(W.toFloat() / src.width, H.toFloat() / src.height)
        val w = src.width * scale
        val h = src.height * scale
        val left = (W - w) / 2f
        val top = (H - h) / 2f
        canvas.drawBitmap(src, null, RectF(left, top, left + w, top + h),
            Paint(Paint.FILTER_BITMAP_FLAG))
        val px = IntArray(W * H)
        canvasBmp.getPixels(px, 0, W, 0, 0, W, H)
        canvasBmp.recycle()

        val grey = FloatArray(W * H) { i ->
            val p = px[i]
            0.299f * ((p shr 16) and 0xff) + 0.587f * ((p shr 8) and 0xff) + 0.114f * (p and 0xff)
        }
        val frame = ByteArray(BYTES)
        for (y in 0 until H) {
            for (x in 0 until W) {
                val i = y * W + x
                val old = grey[i].coerceIn(0f, 255f)
                val level = (old * 15f / 255f).roundToInt() * 17
                val err = old - level
                if (x + 1 < W) grey[i + 1] += err * 7f / 16f
                if (y + 1 < H) {
                    if (x > 0) grey[i + W - 1] += err * 3f / 16f
                    grey[i + W] += err * 5f / 16f
                    if (x + 1 < W) grey[i + W + 1] += err / 16f
                }
                val o = (W * H - 1 - i) * 4
                val b = level.toByte()
                frame[o] = b
                frame[o + 1] = b
                frame[o + 2] = b
                frame[o + 3] = 0xff.toByte()
            }
        }
        return frame
    }

    // ------------------------------------------------------------- the node

    /** One open from offset 0; the kernel swaps the picture after the last page. */
    private fun write(frame: ByteArray): Boolean = try {
        FileOutputStream(NODE).use { it.write(frame) }
        true
    } catch (t: Throwable) {
        Log.w(Sysfs.TAG, "sleep: write failed (${t.javaClass.simpleName}: ${t.message})")
        false
    }

    private fun read(path: String): ByteArray? = try {
        val buf = ByteArray(BYTES)
        var n = 0
        FileInputStream(path).use { s ->
            while (n < BYTES) {
                val r = s.read(buf, n, BYTES - n)
                if (r <= 0) break
                n += r
            }
        }
        if (n == BYTES) buf else null
    } catch (t: Throwable) {
        null
    }

    /** Through a temporary file, so a crash never leaves half a frame to restore at boot. */
    private fun save(f: File, body: (FileOutputStream) -> Unit) {
        val tmp = File(f.path + ".tmp")
        FileOutputStream(tmp).use(body)
        tmp.renameTo(f)
    }
}
