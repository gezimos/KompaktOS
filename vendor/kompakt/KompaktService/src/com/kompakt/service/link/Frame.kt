package com.kompakt.service.link

import android.util.Log
import java.io.EOFException
import java.io.InputStream
import java.io.OutputStream

/** The wire format Mudita Center speaks over the USB serial port. */
object Frame {

    const val HEADER_DIGITS = 9
    private const val MARK = '#'.code.toByte()

    /** Longest frame we will accept. Guards against a bad length. */
    private const val MAX_PAYLOAD = 16 * 1024 * 1024

    /** Block until a whole frame arrives, and return its JSON payload. */
    fun read(input: InputStream): String {
        while (true) {
            // Hunt for the frame mark.
            var b = input.read()
            while (b != -1 && b.toByte() != MARK) {
                b = input.read()
            }
            if (b == -1) throw EOFException("port closed while looking for a frame")

            val digits = ByteArray(HEADER_DIGITS)
            if (!readFully(input, digits)) throw EOFException("port closed inside a header")

            val length = String(digits, Charsets.US_ASCII).toIntOrNull()
            if (length == null || length < 0 || length > MAX_PAYLOAD) {
                // Not a header after all -- most likely a '#' inside a payload we were not aligned to.
                Log.w(TAG, "ignoring a bad frame header: ${String(digits, Charsets.US_ASCII)}")
                continue
            }

            val payload = ByteArray(length)
            if (!readFully(input, payload)) throw EOFException("port closed inside a payload")
            return String(payload, Charsets.UTF_8)
        }
    }

    /** Write one frame. The length is of the encoded bytes, not the string. */
    fun write(output: OutputStream, json: String) {
        val body = json.toByteArray(Charsets.UTF_8)
        val header = body.size.toString().padStart(HEADER_DIGITS, '0')
        if (header.length != HEADER_DIGITS) {
            throw IllegalArgumentException("payload too large to frame: ${body.size}")
        }
        // One write so a reader on the other end never sees a torn header.
        val out = ByteArray(1 + HEADER_DIGITS + body.size)
        out[0] = MARK
        System.arraycopy(header.toByteArray(Charsets.US_ASCII), 0, out, 1, HEADER_DIGITS)
        System.arraycopy(body, 0, out, 1 + HEADER_DIGITS, body.size)
        output.write(out)
        output.flush()
    }

    /** True once [buf] is full; false if the stream ended first. */
    private fun readFully(input: InputStream, buf: ByteArray): Boolean {
        var off = 0
        while (off < buf.size) {
            val n = input.read(buf, off, buf.size - off)
            if (n < 0) return false
            off += n
        }
        return true
    }
}
