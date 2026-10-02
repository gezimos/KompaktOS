package com.kompakt.service.link

import android.content.Context
import android.os.Build
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject

/** The endpoint half of the Mudita Center protocol. */
object Api {

    /** Advertised to Center in API_CONFIGURATION. */
    private const val FEATURE_OVERVIEW = "mc-overview"

    /** Both memories, always. */
    private val FEATURES = listOf(
        FEATURE_OVERVIEW,
        FileManager.INTERNAL,
        FileManager.EXTERNAL,
        Contacts.FEATURE,
    )

    /** Reported straight back to Center, which stores them but never checks them against the USB descriptors it. */
    private const val VENDOR_ID = "3310"
    private const val PRODUCT_ID = "200a"

    /** Neither is parsed or compared anywhere in Center; both are free strings. */
    private const val API_VERSION = "1.0"

    fun handle(context: Context, request: String): String {
        val req = try {
            JSONObject(request)
        } catch (t: Throwable) {
            Log.w(TAG, "unparseable request: ${request.take(200)}")
            return error(null, "UNKNOWN", 500)
        }

        val endpoint = req.optString("endpoint", "")
        val method = req.optString("method", "GET")
        // rid may legitimately be a number or a string; keep whatever came in.
        val rid = if (req.has("rid")) req.get("rid") else null

        Log.i(TAG, "-> $method $endpoint rid=$rid body=${req.opt("body")}")

        val response = when (endpoint) {
            "API_CONFIGURATION" -> if (method == "GET") {
                ok(rid, endpoint, apiConfiguration(context))
            } else {
                error(rid, endpoint, 404)
            }

            "MENU_CONFIGURATION" -> if (method == "GET") {
                ok(rid, endpoint, menuConfiguration())
            } else {
                error(rid, endpoint, 404)
            }

            "FEATURE_CONFIGURATION" -> {
                val feature = req.optJSONObject("body")?.optString("feature").orEmpty()
                val body = featureConfiguration(context, feature)
                if (method == "GET" && body != null) ok(rid, endpoint, body)
                else error(rid, endpoint, 404)
            }

            "FEATURE_DATA" -> {
                val feature = req.optJSONObject("body")?.optString("feature").orEmpty()
                val body = featureData(context, feature)
                if (method == "GET" && body != null) ok(rid, endpoint, body)
                else error(rid, endpoint, 404)
            }

            "OUTBOX" -> if (method == "GET") {
                ok(rid, endpoint, outbox())
            } else {
                error(rid, endpoint, 404)
            }

            "ENTITIES_CONFIGURATION" -> {
                val type = req.optJSONObject("body")?.optString("entityType").orEmpty()
                val body = FileManager.entitiesConfig(type) ?: Contacts.entitiesConfig(type)
                if (method == "GET" && body != null) ok(rid, endpoint, body)
                else error(rid, endpoint, 404)
            }

            // DELETE lives here rather than on an endpoint of its own: buildDeleteEntitiesRequest posts to ENTITIES_DATA.
            "ENTITIES_DATA" -> {
                val b = req.optJSONObject("body")
                val type = b?.optString("entityType").orEmpty()
                val body = when (method) {
                    "GET" ->
                        FileManager.entitiesData(context, type)
                            ?: Contacts.entitiesData(context, type)
                    "DELETE" -> {
                        val ids = b?.optJSONArray("ids")
                        val list = (0 until (ids?.length() ?: 0))
                            .mapNotNull { ids?.optString(it)?.takeIf { s -> s.isNotEmpty() } }
                        if (list.isEmpty()) null else Contacts.delete(context, type, list)
                    }
                    else -> null
                }
                if (body != null) ok(rid, endpoint, body) else error(rid, endpoint, 404)
            }

            "PRE_FILE_TRANSFER" -> {
                val b = req.optJSONObject("body")
                val body = when (method) {
                    "GET" -> Transfers.pre(b?.optString("filePath").orEmpty())
                    "POST" -> Uploads.begin(
                        b?.optString("filePath").orEmpty(),
                        b?.optLong("fileSize", -1L) ?: -1L,
                        b?.optString("crc32").orEmpty(),
                    )
                    else -> null
                }
                if (body != null) ok(rid, endpoint, body) else error(rid, endpoint, 404)
            }

            "FILE_TRANSFER" -> {
                val b = req.optJSONObject("body")
                val body = when (method) {
                    "GET" -> Transfers.chunk(
                        b?.optInt("transferId", -1) ?: -1,
                        b?.optInt("chunkNumber", -1) ?: -1,
                    )
                    "POST" -> Uploads.receive(
                        b?.optInt("transferId", -1) ?: -1,
                        b?.optInt("chunkNumber", -1) ?: -1,
                        b?.optString("data").orEmpty(),
                    )
                    else -> null
                }
                if (body != null) ok(rid, endpoint, body) else error(rid, endpoint, 404)
            }

            "PRE_BACKUP" -> {
                val b = req.optJSONObject("body")
                val id = b?.optInt("backupId", -1) ?: -1
                val body = when (method) {
                    "POST" -> Backup.begin(context, id, b?.optJSONArray("features"))
                    "GET" -> Backup.status(id)
                    else -> null
                }
                if (body != null) ok(rid, endpoint, body) else error(rid, endpoint, 404)
            }

            "POST_BACKUP" -> {
                val id = req.optJSONObject("body")?.optInt("backupId", -1) ?: -1
                val body = if (method == "POST") Backup.finish(id) else null
                if (body != null) ok(rid, endpoint, body) else error(rid, endpoint, 404)
            }

            "PRE_RESTORE" -> {
                val b = req.optJSONObject("body")
                val id = b?.optInt("restoreId", -1) ?: -1
                val body = if (method == "POST") {
                    Backup.prepare(context, id, b?.optJSONArray("features"))
                } else {
                    null
                }
                if (body != null) ok(rid, endpoint, body) else error(rid, endpoint, 404)
            }

            "RESTORE" -> {
                val id = req.optJSONObject("body")?.optInt("restoreId", -1) ?: -1
                val body = when (method) {
                    // GET is the progress poll.
                    "POST", "GET" -> Backup.apply(context, id)
                    "DELETE" -> Backup.discard(id)
                    else -> null
                }
                if (body != null) ok(rid, endpoint, body) else error(rid, endpoint, 404)
            }

            "PRE_DATA_TRANSFER" -> {
                val b = req.optJSONObject("body")
                val id = b?.optInt("dataTransferId", -1) ?: -1
                val body = if (method == "POST") {
                    Backup.stage(context, id, b?.optJSONArray("domains"))
                } else {
                    null
                }
                if (body != null) ok(rid, endpoint, body) else error(rid, endpoint, 404)
            }

            "DATA_TRANSFER" -> {
                val id = req.optJSONObject("body")?.optInt("dataTransferId", -1) ?: -1
                val body = when (method) {
                    // GET is the progress poll; the work runs inline, so it has finished by the time anything can ask.
                    "POST", "GET" -> Backup.migrate(context, id)
                    "DELETE" -> Backup.discard(id)
                    else -> null
                }
                if (body != null) ok(rid, endpoint, body) else error(rid, endpoint, 404)
            }

            "APP_INSTALL" -> {
                val b = req.optJSONObject("body")
                val body = when (method) {
                    "POST" -> Uploads.install(context, b?.optString("filePath").orEmpty())
                    "GET" -> Uploads.installProgress(b?.optInt("installationId", -1) ?: -1)
                    else -> null
                }
                if (body != null) ok(rid, endpoint, body) else error(rid, endpoint, 404)
            }

            "SYSTEM" -> if (method == "POST") {
                system(req.optJSONObject("body")?.optString("action").orEmpty(), rid, endpoint)
            } else {
                error(rid, endpoint, 404)
            }

            else -> {
                // Not a failure worth shouting about: this is the normal path for every feature we have not written yet, and.
                Log.i(TAG, "no handler for $method $endpoint")
                error(rid, endpoint, 404)
            }
        }

        Log.i(TAG, "<- ${response.take(400)}")
        return response
    }

    private fun apiConfiguration(context: Context): JSONObject = JSONObject().apply {
        put("apiVersion", API_VERSION)
        put("osVersion", Build.DISPLAY ?: Build.ID ?: "unknown")
        put("vendorId", VENDOR_ID)
        put("productId", PRODUCT_ID)
        serial().takeIf { it.isNotEmpty() }?.let { put("serialNumber", it) }
        put("features", JSONArray(FEATURES))
    }

    /** The left hand menu. */
    private fun menuConfiguration(): JSONObject = JSONObject().apply {
        put("title", "Kompakt")
        put("menuItems", JSONArray().apply {
            put(JSONObject().apply {
                put("feature", FEATURE_OVERVIEW)
                put("displayName", "Overview")
                put("icon", "overview")
            })
            // Only the internal memory gets a menu entry; the card is a tab inside that screen rather than a separate one.
            put(JSONObject().apply {
                put("feature", FileManager.INTERNAL)
                put("displayName", "Files")
                put("icon", "file-manager")
            })
            put(JSONObject().apply {
                put("feature", Contacts.FEATURE)
                put("displayName", "Contacts")
                put("icon", "contacts-book")
            })
        })
    }

    /** The About list: the key, its label, and how to read its value. */
    private val ABOUT_FIELDS: List<Triple<String, String, (Context) -> String>> =
        listOf(
            Triple("serialNumber", "Serial number") { _ -> serial() },
            Triple("deviceVersion", "Android version") { _ ->
                Build.VERSION.RELEASE ?: ""
            },
            Triple("securityPatch", "Security patch") { _ ->
                Build.VERSION.SECURITY_PATCH ?: ""
            },
            Triple("buildNumber", "Build") { _ -> Build.DISPLAY ?: Build.ID ?: "" },
        )

    /** power-off, reboot, lock and serial-port-setup, per Center's own union. */
    private fun system(action: String, rid: Any?, endpoint: String): String =
        when (action) {
            "serial-port-setup" -> ok(rid, endpoint, JSONObject())
            else -> {
                Log.i(TAG, "SYSTEM action not implemented: $action")
                error(rid, endpoint, 404)
            }
        }

    /** How Center should lay the feature out. */
    private fun featureConfiguration(context: Context, feature: String): JSONObject? {
        FileManager.config(context, feature)?.let { return it }
        Contacts.config(feature)?.let { return it }
        if (feature != FEATURE_OVERVIEW) return null
        return JSONObject().apply {
            put("title", "Overview")
            put("summary", JSONObject().apply {
                put("show", true)
                put("showImg", true)
                put("showSerialNumber", true)
                put("serialNumberLabel", "Serial number")
                put("showDeviceVersion", true)
                put("deviceVersionLabel", "Android version")
                put("showAbout", true)
                put("aboutTitle", "About this device")
                put("aboutFields", JSONArray().apply {
                    ABOUT_FIELDS.forEach { (key, title, _) ->
                        put(aboutField(key, title))
                    }
                })
            })
            // The backup card, and the only way into the backup endpoints from the UI -- Center draws the buttons from this.
            put("sections", JSONArray().put(JSONObject().apply {
                put("type", "mc-overview-backup")
                put("title", "Backup")
                put("dataKey", "backup")
                put("backupFeatures", JSONArray().put(JSONObject().apply {
                    put("label", "Contacts")
                    put("key", "contacts")
                }))
                put("restoreFeatures", JSONArray().put(JSONObject().apply {
                    put("label", "Contacts")
                    put("feature", Contacts.FEATURE)
                    put("keys", JSONArray().put("contacts"))
                }))
            }))
        }
    }

    private fun aboutField(key: String, title: String): JSONObject =
        JSONObject().apply {
            put("dataKey", key)
            put("type", "detail-list-text")
            put("title", title)
        }

    /** The values behind the layout above. */
    private fun featureData(context: Context, feature: String): JSONObject? {
        FileManager.data(context, feature)?.let { return it }
        Contacts.data(feature)?.let { return it }
        if (feature != FEATURE_OVERVIEW) return null
        // Every key, always, even when its value is empty. See ABOUT_FIELDS.
        val about = JSONObject().apply {
            ABOUT_FIELDS.forEach { (key, _, read) ->
                val value = try {
                    read(context)
                } catch (t: Throwable) {
                    Log.w(TAG, "about: $key unreadable (${t.javaClass.simpleName})")
                    ""
                }
                put(key, JSONObject().put("text", value))
            }
        }
        return JSONObject().apply {
            put("summary", JSONObject().put("about", about))
        }
    }

    /** What changed since Center last asked. */
    private fun outbox(): JSONObject = JSONObject().apply {
        put("features", JSONArray())
        put("data", JSONArray())
        put("entities", JSONArray())
    }

    /** The serial number, or an empty string. */
    private fun serial(): String {
        try {
            val v = Build.getSerial()
            if (!v.isNullOrBlank() && v != Build.UNKNOWN) return v
        } catch (t: Throwable) {
            Log.w(TAG, "serial: getSerial refused (${t.javaClass.simpleName})")
        }
        return try {
            android.os.SystemProperties.get("ro.serialno", "").orEmpty()
        } catch (t: Throwable) {
            ""
        }
    }

    private fun ok(rid: Any?, endpoint: String, body: JSONObject): String =
        JSONObject().apply {
            if (rid != null) put("rid", rid)
            put("endpoint", endpoint)
            put("status", 200)
            put("body", body)
        }.toString()

    /** Status values are Center's ApiDeviceErrorType: 404, 423, 500, 507. */
    private fun error(rid: Any?, endpoint: String, status: Int): String =
        JSONObject().apply {
            if (rid != null) put("rid", rid)
            put("endpoint", endpoint)
            put("status", status)
        }.toString()
}
