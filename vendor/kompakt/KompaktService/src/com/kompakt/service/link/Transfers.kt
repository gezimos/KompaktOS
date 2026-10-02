package com.kompakt.service.link

import android.util.Base64
import android.util.Log
import org.json.JSONObject
import java.io.File
import java.io.RandomAccessFile
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.zip.CRC32

/** Sending a file to Mudita Center, a chunk at a time. */
object Transfers {

    /** 14334 bytes: Center's advertised 14336, rounded down to a multiple of three. */
    private const val CHUNK_SIZE = 3 * 4778

    private class Open(val file: File, val size: Long, val crc32: String)

    private val open = ConcurrentHashMap<Int, Open>()

    /** From one, because zero is not a positive int on Center's side. */
    private val nextId = AtomicInteger(1)

    /** Announce a file. */
    fun pre(path: String): JSONObject? {
        val f = File(path)
        if (!f.isFile) {
            Log.w(TAG, "transfer: not a file: $path")
            return null
        }
        if (!f.canRead()) {
            Log.w(TAG, "transfer: cannot read: $path")
            return null
        }
        val size = f.length()
        if (size <= 0L) {
            // fileSize is z.int().positive(); an empty file has no valid response.
            Log.w(TAG, "transfer: empty file, nothing to offer: $path")
            return null
        }
        val crc = crc32OfBase64(f) ?: return null
        val id = nextId.getAndIncrement()
        open[id] = Open(f, size, crc)
        Log.i(TAG, "transfer $id: $path, $size bytes, crc32=$crc")
        return JSONObject().apply {
            put("transferId", id)
            put("chunkSize", CHUNK_SIZE)
            put("fileSize", size)
            put("crc32", crc)
        }
    }

    /** One chunk, base64. Null for an unknown transfer or a chunk past the end. */
    fun chunk(transferId: Int, chunkNumber: Int): JSONObject? {
        val t = open[transferId]
        if (t == null) {
            Log.w(TAG, "transfer: no such transfer: $transferId")
            return null
        }
        if (chunkNumber < 1) {
            Log.w(TAG, "transfer $transferId: chunk numbers start at 1, got $chunkNumber")
            return null
        }
        val offset = (chunkNumber - 1).toLong() * CHUNK_SIZE
        if (offset >= t.size) {
            Log.w(TAG, "transfer $transferId: chunk $chunkNumber is past the end")
            return null
        }
        val length = minOf(CHUNK_SIZE.toLong(), t.size - offset).toInt()
        val buf = ByteArray(length)
        try {
            RandomAccessFile(t.file, "r").use { raf ->
                raf.seek(offset)
                raf.readFully(buf)
            }
        } catch (e: Throwable) {
            Log.w(TAG, "transfer $transferId: read failed at $offset (${e.javaClass.simpleName})")
            return null
        }
        // Last chunk: let it go.
        if (offset + length >= t.size) {
            open.remove(transferId)
            Log.i(TAG, "transfer $transferId: sent the last chunk")
        }
        return JSONObject().apply {
            put("transferId", transferId)
            put("chunkNumber", chunkNumber)
            put("data", Base64.encodeToString(buf, Base64.NO_WRAP))
        }
    }

    /** Eight lowercase hex digits over the base64 text, not over the file's bytes. */
    private fun crc32OfBase64(f: File): String? = try {
        val crc = CRC32()
        f.inputStream().use { input ->
            val buf = ByteArray(CHUNK_SIZE)
            while (true) {
                var filled = 0
                while (filled < buf.size) {
                    val n = input.read(buf, filled, buf.size - filled)
                    if (n < 0) break
                    filled += n
                }
                if (filled == 0) break
                val block = if (filled == buf.size) buf else buf.copyOf(filled)
                crc.update(Base64.encodeToString(block, Base64.NO_WRAP).toByteArray(Charsets.US_ASCII))
                if (filled < buf.size) break
            }
        }
        String.format("%08x", crc.value)
    } catch (t: Throwable) {
        Log.w(TAG, "transfer: checksum failed for ${f.path} (${t.javaClass.simpleName})")
        null
    }
}
