package com.kompakt.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log

/** The GSF id the uncertified device registration page asks for, kept as text. */
object GsfId {
    const val TAG = "KompaktGsfId"
    private val URI = Uri.parse("content://com.google.android.gsf.gservices")

    /** The id as the page takes it, or null and the reason. */
    class Result(val decimal: String?, val why: String)

    fun read(ctx: Context): Result {
        val raw = runCatching {
            ctx.contentResolver.query(URI, null, null, arrayOf("android_id"), null).use { c ->
                when {
                    c == null -> return Result(null, "no provider")
                    !c.moveToFirst() -> return Result(null, "no row")
                    c.columnCount < 2 -> return Result(null, "columns=${c.columnCount}")
                    else -> c.getString(1)?.trim()
                }
            }
        }.getOrElse { return Result(null, "${it.javaClass.simpleName}: ${it.message}") }
        return when {
            raw.isNullOrEmpty() -> Result(null, "empty value")
            raw.matches(Regex("[0-9]{1,20}")) -> Result(raw.trimStart('0').ifEmpty { "0" }, "ok")
            raw.matches(Regex("[0-9a-fA-F]{16}")) ->
                Result(java.lang.Long.toUnsignedString(java.lang.Long.parseUnsignedLong(raw, 16)), "ok")
            else -> Result(null, "unexpected value \"$raw\"")
        }
    }
}

/** The GSF id for adb, as broadcast result data; behind DUMP. */
class GsfIdReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val pending = goAsync()
        Thread {
            val r = GsfId.read(context.applicationContext)
            Log.i(GsfId.TAG, r.decimal?.let { "GSF id $it" } ?: "no GSF id: ${r.why}")
            pending.resultData = r.decimal ?: "no GSF id: ${r.why}"
            pending.finish()
        }.start()
    }
}
