package com.kompakt.service.link

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import android.util.Base64
import android.util.Log
import org.json.JSONObject
import java.io.File
import java.io.RandomAccessFile
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.zip.CRC32

/** Files arriving from the desktop, and apks being installed. */
object Uploads {

    private const val CHUNK_SIZE = 14 * 1024

    private class Incoming(
        val file: File,
        val size: Long,
        val crc32: String,
        var received: Long = 0L,
    )

    private val incoming = ConcurrentHashMap<Int, Incoming>()
    private val nextId = AtomicInteger(1)

    /** Accept a file. */
    fun begin(filePath: String, fileSize: Long, crc32: String): JSONObject? {
        if (filePath.isEmpty() || fileSize <= 0L || crc32.length != 8) {
            Log.w(TAG, "upload: refused, path=$filePath size=$fileSize crc=$crc32")
            return null
        }
        val f = File(filePath)
        try {
            f.parentFile?.mkdirs()
            // Truncate anything already there to the final length, so every chunk can be written at its own offset without.
            RandomAccessFile(f, "rw").use { it.setLength(fileSize) }
        } catch (t: Throwable) {
            Log.w(TAG, "upload: cannot open $filePath (${t.javaClass.simpleName}: ${t.message})")
            return null
        }
        val id = nextId.getAndIncrement()
        incoming[id] = Incoming(f, fileSize, crc32.lowercase())
        Log.i(TAG, "upload $id: $filePath, $fileSize bytes")
        return JSONObject().apply {
            put("transferId", id)
            put("chunkSize", CHUNK_SIZE)
        }
    }

    /** One chunk in. Null on anything that means the transfer is not sound. */
    fun receive(transferId: Int, chunkNumber: Int, data: String): JSONObject? {
        val t = incoming[transferId]
        if (t == null) {
            Log.w(TAG, "upload: no such transfer: $transferId")
            return null
        }
        if (chunkNumber < 1) {
            Log.w(TAG, "upload $transferId: chunk numbers start at 1, got $chunkNumber")
            return null
        }
        val bytes = try {
            Base64.decode(data, Base64.DEFAULT)
        } catch (e: Throwable) {
            Log.w(TAG, "upload $transferId: chunk $chunkNumber is not base64")
            return null
        }
        val offset = (chunkNumber - 1).toLong() * CHUNK_SIZE
        if (offset + bytes.size > t.size) {
            Log.w(TAG, "upload $transferId: chunk $chunkNumber runs past the declared size")
            return null
        }
        try {
            RandomAccessFile(t.file, "rw").use { raf ->
                raf.seek(offset)
                raf.write(bytes)
            }
        } catch (e: Throwable) {
            Log.w(TAG, "upload $transferId: write failed (${e.javaClass.simpleName})")
            return null
        }
        t.received += bytes.size

        if (t.received >= t.size) {
            incoming.remove(transferId)
            val actual = crc32(t.file)
            if (actual != t.crc32) {
                // Deleted rather than kept: a file that does not match what was sent is worse sitting in the gallery than not.
                Log.w(TAG, "upload $transferId: crc32 $actual != ${t.crc32}, discarding")
                runCatching { t.file.delete() }
                return null
            }
            Log.i(TAG, "upload $transferId: complete, ${t.size} bytes, crc32 ok")
        }

        return JSONObject().apply {
            put("transferId", transferId)
            put("chunkNumber", chunkNumber)
        }
    }

    // ------------------------------------------------------------- app install

    private class Install(var progress: Int)

    private val installs = ConcurrentHashMap<Int, Install>()
    private val nextInstallId = AtomicInteger(1)

    /** Install an apk already on the phone. */
    fun install(context: Context, filePath: String): JSONObject? {
        val apk = File(filePath)
        if (!apk.isFile) {
            Log.w(TAG, "install: not a file: $filePath")
            return null
        }
        val id = nextInstallId.getAndIncrement()
        val state = Install(0)
        installs[id] = state
        return try {
            val installer = context.packageManager.packageInstaller
            val params = PackageInstaller.SessionParams(
                PackageInstaller.SessionParams.MODE_FULL_INSTALL
            )
            val sessionId = installer.createSession(params)
            installer.openSession(sessionId).use { session ->
                session.openWrite("apk", 0, apk.length()).use { out ->
                    apk.inputStream().use { input ->
                        val buf = ByteArray(64 * 1024)
                        var written = 0L
                        while (true) {
                            val n = input.read(buf)
                            if (n < 0) break
                            out.write(buf, 0, n)
                            written += n
                            state.progress = ((written * 100) / apk.length()).toInt()
                        }
                    }
                    session.fsync(out)
                }
                // No status receiver: nothing here acts on the result, and the progress Center polls is the copy, which is the.
                session.commit(
                    android.app.PendingIntent.getBroadcast(
                        context,
                        id,
                        Intent("com.kompakt.service.INSTALL_RESULT").setPackage(context.packageName),
                        android.app.PendingIntent.FLAG_IMMUTABLE
                            or android.app.PendingIntent.FLAG_UPDATE_CURRENT,
                    ).intentSender
                )
            }
            state.progress = 100
            Log.i(TAG, "install $id: committed $filePath")
            JSONObject().put("installationId", id)
        } catch (t: Throwable) {
            Log.w(TAG, "install $id: failed (${t.javaClass.simpleName}: ${t.message})")
            installs.remove(id)
            null
        }
    }

    fun installProgress(installationId: Int): JSONObject? {
        val state = installs[installationId] ?: return null
        return JSONObject().apply {
            put("installationId", installationId)
            put("progress", state.progress)
        }
    }

    private fun crc32(f: File): String = try {
        val crc = CRC32()
        f.inputStream().use { input ->
            val buf = ByteArray(64 * 1024)
            while (true) {
                val n = input.read(buf)
                if (n < 0) break
                crc.update(buf, 0, n)
            }
        }
        String.format("%08x", crc.value)
    } catch (t: Throwable) {
        Log.w(TAG, "upload: checksum failed for ${f.path} (${t.javaClass.simpleName})")
        ""
    }
}
