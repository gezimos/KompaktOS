package com.kompakt.service.link

import android.content.ContentUris
import android.content.Context
import android.provider.ContactsContract
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/** Contacts, read out of the platform provider and handed to Mudita Center. */
object Contacts {

    const val FEATURE = "mc-contacts"
    const val ENTITY_TYPE = "contacts"

    // ------------------------------------------------------------------ config

    fun config(feature: String): JSONObject? {
        if (feature != FEATURE) return null
        return JSONObject().apply {
            put("main", JSONObject().apply {
                put("screenTitle", "Contacts")
                put("component", "mc-contacts-view")
                put("config", JSONObject().apply {
                    put("entityTypes", JSONArray().put(ENTITY_TYPE))
                })
            })
        }
    }

    /** Nothing to say, but it has to be said. */
    fun data(feature: String): JSONObject? =
        if (feature == FEATURE) JSONObject() else null

    // ---------------------------------------------------------------- entities

    fun entitiesConfig(entityType: String): JSONObject? {
        if (entityType != ENTITY_TYPE) return null
        return JSONObject().apply {
            put("fields", JSONObject().apply {
                put("contactId", JSONObject().put("type", "id"))
                put("firstName", JSONObject().put("type", "string"))
                put("lastName", JSONObject().put("type", "string"))
                put("displayName1", JSONObject().put("type", "string"))
                put("displayName2", JSONObject().put("type", "string"))
                put("searchName", JSONObject().put("type", "string"))
                put("sortField", JSONObject().put("type", "string"))
                put("entityType", JSONObject().put("type", "string"))
                put("accountName", JSONObject().put("type", "string"))
                put("starred", JSONObject().put("type", "boolean"))
                put("phoneNumbers", JSONObject().put("type", "array"))
                put("emailAddresses", JSONObject().put("type", "array"))
            })
        }
    }

    /** Every contact, written to the cache, path returned. */
    fun entitiesData(context: Context, entityType: String): JSONObject? {
        if (entityType != ENTITY_TYPE) return null
        val list = try {
            read(context)
        } catch (t: Throwable) {
            Log.w(TAG, "contacts: cannot read (${t.javaClass.simpleName}: ${t.message})")
            return null
        }
        val out = File(context.cacheDir, "entities-contacts.json")
        return try {
            // An object with a `data` key, not the bare array.
            out.writeText(JSONObject().put("data", list).toString())
            Log.i(TAG, "contacts: ${list.length()} written to ${out.path}")
            JSONObject().apply {
                put("_status", 200)
                put("filePath", out.absolutePath)
            }
        } catch (t: Throwable) {
            Log.w(TAG, "contacts: cannot write (${t.javaClass.simpleName})")
            null
        }
    }

    /** Delete by contactId, reporting the ones that would not go. */
    fun delete(context: Context, entityType: String, ids: List<String>): JSONObject? {
        if (entityType != ENTITY_TYPE) return null
        val failed = ArrayList<String>()
        for (id in ids) {
            val gone = try {
                context.contentResolver.delete(
                    ContactsContract.RawContacts.CONTENT_URI,
                    "${ContactsContract.RawContacts.CONTACT_ID} = ?",
                    arrayOf(id),
                ) > 0
            } catch (t: Throwable) {
                Log.w(TAG, "contacts: delete $id failed (${t.javaClass.simpleName})")
                false
            }
            if (!gone) failed.add(id)
        }
        return JSONObject().apply {
            if (failed.isEmpty()) {
                put("_status", 200)
            } else {
                put("_status", 207)
                put("failedIds", JSONArray(failed))
            }
        }
    }

    // ------------------------------------------------------------------ reading

    private fun read(context: Context): JSONArray {
        val out = JSONArray()
        val cr = context.contentResolver
        val projection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
            ContactsContract.Contacts.STARRED,
        )
        cr.query(
            ContactsContract.Contacts.CONTENT_URI,
            projection,
            null,
            null,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " ASC",
        )?.use { c ->
            val idCol = c.getColumnIndexOrThrow(ContactsContract.Contacts._ID)
            val nameCol = c.getColumnIndexOrThrow(
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
            )
            val starCol = c.getColumnIndexOrThrow(ContactsContract.Contacts.STARRED)
            while (c.moveToNext()) {
                val id = c.getLong(idCol)
                val display = c.getString(nameCol).orEmpty()
                val (first, last) = structuredName(context, id, display)
                val phones = phones(context, id)
                val emails = emails(context, id)
                out.put(JSONObject().apply {
                    put("contactId", id.toString())
                    put("entityType", ENTITY_TYPE)
                    put("firstName", first)
                    put("lastName", last)
                    // Center shows these two side by side; giving it the parts rather than the joined name lets it lay them out.
                    put("displayName1", first)
                    put("displayName2", last)
                    // Not optional in Contact, so always present even when there is no name at all to build them from.
                    put("searchName", searchName(display, phones))
                    put("sortField", display.ifEmpty { id.toString() })
                    put("accountName", accountName(context, id))
                    put("starred", c.getInt(starCol) != 0)
                    put("phoneNumbers", phones)
                    put("emailAddresses", emails)
                })
            }
        }
        return out
    }

    /** First and last name, falling back to splitting the display name. */
    private fun structuredName(
        context: Context,
        contactId: Long,
        display: String,
    ): Pair<String, String> {
        context.contentResolver.query(
            ContactsContract.Data.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
                ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME,
            ),
            "${ContactsContract.Data.CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?",
            arrayOf(
                contactId.toString(),
                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE,
            ),
            null,
        )?.use { c ->
            if (c.moveToFirst()) {
                val given = c.getString(0).orEmpty()
                val family = c.getString(1).orEmpty()
                if (given.isNotEmpty() || family.isNotEmpty()) return given to family
            }
        }
        val cut = display.indexOf(' ')
        return if (cut > 0) {
            display.substring(0, cut) to display.substring(cut + 1)
        } else {
            display to ""
        }
    }

    private fun phones(context: Context, contactId: Long): JSONArray {
        val out = JSONArray()
        context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER,
                ContactsContract.CommonDataKinds.Phone.TYPE,
            ),
            "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
            arrayOf(contactId.toString()),
            null,
        )?.use { c ->
            while (c.moveToNext()) {
                val number = c.getString(1).orEmpty()
                if (number.isEmpty()) continue
                out.put(JSONObject().apply {
                    put("id", c.getLong(0).toString())
                    put("phoneNumber", number)
                    put("unifiedPhoneNumber", c.getString(2) ?: number)
                    put("phoneType", phoneType(c.getInt(3)))
                })
            }
        }
        return out
    }

    /** Center's PhoneNumberType is four values; everything else is "other". */
    private fun phoneType(type: Int): String = when (type) {
        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE -> "mobile"
        ContactsContract.CommonDataKinds.Phone.TYPE_HOME -> "home"
        ContactsContract.CommonDataKinds.Phone.TYPE_WORK -> "work"
        else -> "other"
    }

    private fun emails(context: Context, contactId: Long): JSONArray {
        val out = JSONArray()
        context.contentResolver.query(
            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Email._ID,
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.TYPE,
            ),
            "${ContactsContract.CommonDataKinds.Email.CONTACT_ID} = ?",
            arrayOf(contactId.toString()),
            null,
        )?.use { c ->
            while (c.moveToNext()) {
                val address = c.getString(1).orEmpty()
                if (address.isEmpty()) continue
                out.put(JSONObject().apply {
                    put("id", c.getLong(0).toString())
                    put("emailAddress", address)
                    put("emailType", emailType(c.getInt(2)))
                })
            }
        }
        return out
    }

    private fun emailType(type: Int): String = when (type) {
        ContactsContract.CommonDataKinds.Email.TYPE_HOME -> "home"
        ContactsContract.CommonDataKinds.Email.TYPE_WORK -> "work"
        else -> "other"
    }

    /** Which account the contact lives in, for Center's source column. */
    private fun accountName(context: Context, contactId: Long): String {
        context.contentResolver.query(
            ContactsContract.RawContacts.CONTENT_URI,
            arrayOf(ContactsContract.RawContacts.ACCOUNT_NAME),
            "${ContactsContract.RawContacts.CONTACT_ID} = ?",
            arrayOf(contactId.toString()),
            null,
        )?.use { c ->
            if (c.moveToFirst()) return c.getString(0).orEmpty().ifEmpty { "Phone" }
        }
        return "Phone"
    }

    /** What Center searches against: the name and every number, lowercased. */
    private fun searchName(display: String, phones: JSONArray): String {
        val sb = StringBuilder(display.lowercase())
        for (i in 0 until phones.length()) {
            sb.append(' ').append(phones.optJSONObject(i)?.optString("phoneNumber").orEmpty())
        }
        return sb.toString().trim()
    }

    @Suppress("unused")
    private fun uriFor(id: Long) =
        ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id)
}
