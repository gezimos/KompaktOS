package com.kompakt.service.link

import android.content.ContentProviderOperation
import android.content.Context
import android.provider.ContactsContract
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/** Backup and restore. */
object Backup {

    /** The feature names Center may ask to back up, and what they mean here. */
    private const val FEATURE_CONTACTS = "mc-contacts"

    private class Job(
        val features: List<String>,
        var progress: Int = 0,
        val results: MutableMap<String, String> = LinkedHashMap(),
        var done: Boolean = false,
    )

    private val backups = HashMap<Int, Job>()
    private val restores = HashMap<Int, MutableMap<String, String>>()

    // -------------------------------------------------------------- backup

    /** Start a backup. */
    fun begin(context: Context, backupId: Int, features: JSONArray?): JSONObject? {
        if (backupId <= 0) return null
        val wanted = (0 until (features?.length() ?: 0))
            .mapNotNull { features?.optString(it)?.takeIf { s -> s.isNotEmpty() } }
        if (wanted.isEmpty()) return null

        val job = Job(wanted)
        backups[backupId] = job
        for (feature in wanted) {
            val path = write(context, backupId, feature)
            if (path != null) job.results[feature] = path
        }
        job.progress = 100
        job.done = true
        Log.i(TAG, "backup $backupId: ${job.results.size}/${wanted.size} features")
        // Center accepts a 202 here and polls; there is nothing left to wait for, so it is told so straight away.
        return status(backupId)
    }

    fun status(backupId: Int): JSONObject? {
        val job = backups[backupId] ?: return null
        return JSONObject().apply {
            if (!job.done) {
                put("_status", 202)
                put("backupId", backupId)
                put("progress", job.progress)
            } else {
                put("_status", 200)
                put("backupId", backupId)
                put("progress", 100)
                put("features", JSONObject().apply {
                    job.results.forEach { (feature, path) -> put(feature, path) }
                })
            }
        }
    }

    /** Center has taken what it wanted; drop the files and the record. */
    fun finish(backupId: Int): JSONObject? {
        val job = backups.remove(backupId) ?: return null
        job.results.values.forEach { runCatching { File(it).delete() } }
        Log.i(TAG, "backup $backupId: cleaned up")
        return JSONObject()
    }

    private fun write(context: Context, backupId: Int, feature: String): String? {
        if (feature != FEATURE_CONTACTS) {
            Log.i(TAG, "backup: nothing to save for $feature")
            return null
        }
        val body = Contacts.entitiesData(context, Contacts.ENTITY_TYPE) ?: return null
        val source = File(body.optString("filePath"))
        val target = File(context.cacheDir, "backup-$backupId-$feature.json")
        return try {
            source.copyTo(target, overwrite = true)
            target.absolutePath
        } catch (t: Throwable) {
            Log.w(TAG, "backup $backupId: cannot stage $feature (${t.javaClass.simpleName})")
            null
        }
    }

    // ------------------------------------------------------------- restore

    /** Say where each feature's file should be uploaded to. */
    fun prepare(context: Context, restoreId: Int, features: JSONArray?): JSONObject? {
        if (restoreId <= 0) return null
        val paths = LinkedHashMap<String, String>()
        for (i in 0 until (features?.length() ?: 0)) {
            val entry = features?.optJSONObject(i) ?: continue
            val feature = entry.optString("feature")
            val key = entry.optString("key")
            if (feature.isEmpty() || key.isEmpty()) continue
            paths[key] = File(context.cacheDir, "restore-$restoreId-$key").absolutePath
        }
        if (paths.isEmpty()) return null
        restores[restoreId] = paths
        return JSONObject().apply {
            put("restoreId", restoreId)
            put("features", JSONObject().apply {
                paths.forEach { (key, path) -> put(key, path) }
            })
        }
    }

    /** Apply what was uploaded. */
    fun apply(context: Context, restoreId: Int): JSONObject? {
        val paths = restores[restoreId] ?: return null
        var restored = 0
        for ((key, path) in paths) {
            val f = File(path)
            if (!f.isFile) {
                Log.w(TAG, "restore $restoreId: $key never arrived")
                continue
            }
            restored += importContacts(context, f)
        }
        Log.i(TAG, "restore $restoreId: $restored contacts")
        return JSONObject().apply {
            put("_status", 200)
            put("progress", 100)
            put("rebootRequired", false)
            put("message", "Restored $restored contacts")
        }
    }

    /** The migration form of prepare(): domains rather than {feature, key} pairs. */
    fun stage(context: Context, transferId: Int, domains: JSONArray?): JSONObject? {
        if (transferId <= 0) return null
        val paths = LinkedHashMap<String, String>()
        for (i in 0 until (domains?.length() ?: 0)) {
            val domain = domains?.optString(i).orEmpty()
            if (domain.isEmpty()) continue
            paths[domain] = File(context.cacheDir, "migrate-$transferId-$domain").absolutePath
        }
        if (paths.isEmpty()) return null
        restores[transferId] = paths
        return JSONObject().apply {
            put("dataTransferId", transferId)
            put("domains", JSONObject().apply {
                paths.forEach { (domain, path) -> put(domain, path) }
            })
        }
    }

    /** Apply a migration. */
    fun migrate(context: Context, transferId: Int): JSONObject? {
        val paths = restores[transferId] ?: return null
        var restored = 0
        for ((domain, path) in paths) {
            if (domain != "contacts") {
                Log.i(TAG, "migrate $transferId: nothing to import for $domain")
                continue
            }
            val f = File(path)
            if (!f.isFile) {
                Log.w(TAG, "migrate $transferId: $domain never arrived")
                continue
            }
            restored += importContacts(context, f)
        }
        Log.i(TAG, "migrate $transferId: $restored contacts")
        return JSONObject().apply {
            put("_status", 200)
            put("progress", 100)
            put("rebootRequired", false)
            put("message", "Transferred $restored contacts")
        }
    }

    fun discard(restoreId: Int): JSONObject? {
        val paths = restores.remove(restoreId) ?: return null
        paths.values.forEach { runCatching { File(it).delete() } }
        return JSONObject()
    }

    /** Insert contacts from a file this class wrote, or one Center packed from it. */
    private fun importContacts(context: Context, file: File): Int {
        val list = try {
            JSONArray(file.readText())
        } catch (t: Throwable) {
            Log.w(TAG, "restore: ${file.name} is not a contact list (${t.javaClass.simpleName})")
            return 0
        }
        var count = 0
        for (i in 0 until list.length()) {
            val c = list.optJSONObject(i) ?: continue
            val ops = ArrayList<ContentProviderOperation>()
            ops.add(
                ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                    .build()
            )
            val first = c.optString("firstName")
            val last = c.optString("lastName")
            if (first.isNotEmpty() || last.isNotEmpty()) {
                ops.add(
                    ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(
                            ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE,
                        )
                        .withValue(
                            ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, first
                        )
                        .withValue(
                            ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, last
                        )
                        .build()
                )
            }
            val phones = c.optJSONArray("phoneNumbers")
            for (p in 0 until (phones?.length() ?: 0)) {
                val number = phones?.optJSONObject(p)?.optString("phoneNumber").orEmpty()
                if (number.isEmpty()) continue
                ops.add(
                    ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(
                            ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                        )
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                        .withValue(
                            ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
                        )
                        .build()
                )
            }
            val emails = c.optJSONArray("emailAddresses")
            for (e in 0 until (emails?.length() ?: 0)) {
                val address = emails?.optJSONObject(e)?.optString("emailAddress").orEmpty()
                if (address.isEmpty()) continue
                ops.add(
                    ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(
                            ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE,
                        )
                        .withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, address)
                        .withValue(
                            ContactsContract.CommonDataKinds.Email.TYPE,
                            ContactsContract.CommonDataKinds.Email.TYPE_HOME,
                        )
                        .build()
                )
            }
            try {
                context.contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
                count++
            } catch (t: Throwable) {
                Log.w(TAG, "restore: one contact failed (${t.javaClass.simpleName})")
            }
        }
        return count
    }
}
