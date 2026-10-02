package com.kompakt.service.link

import android.content.Context
import android.os.Environment
import android.os.StatFs
import android.os.storage.StorageManager
import android.provider.MediaStore
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/** The file manager: what is on the phone, and how much room it takes. */
object FileManager {

    const val INTERNAL = "mc-file-manager-internal"
    const val EXTERNAL = "mc-file-manager-external"

    /** One row of the storage bar and one tab of the list. */
    private class Category(
        val id: String,
        val icon: String,
        val markerColor: String,
        val label: String,
        val directory: String,
        val extensions: List<String>,
    )

    private val CATEGORIES = listOf(
        Category(
            "audioFiles", "music-note", "#E38577", "Audio",
            "Music/", listOf("mp3", "m4a", "aac", "flac", "ogg", "opus", "wav", "wma"),
        ),
        Category(
            "imageFiles", "photo-catalog", "#0E7490", "Images",
            "Pictures/", listOf("jpg", "jpeg", "png", "gif", "webp", "bmp", "heic"),
        ),
        Category(
            "ebookFiles", "book", "#A8DADC", "E-books",
            "Books/", listOf("epub", "mobi", "azw3", "fb2", "pdf", "txt"),
        ),
        Category(
            "applicationFiles", "file-copy", "#AEBEC9", "Apps",
            "Download/", listOf("apk"),
        ),
    )

    private fun isInternal(feature: String) = feature == INTERNAL

    // ------------------------------------------------------------------ config

    fun config(context: Context, feature: String): JSONObject? {
        if (feature != INTERNAL && feature != EXTERNAL) return null
        val internal = isInternal(feature)
        val root = if (internal) internalRoot() else externalRoot(context)
        return JSONObject().apply {
            put("main", JSONObject().apply {
                put("screenTitle", if (internal) "Internal storage" else "SD card")
                put("component", "mc-file-manager-view")
                put("config", JSONObject().apply {
                    put("storages", JSONArray().put(JSONObject().apply {
                        put("label", if (internal) "Internal storage" else "SD card")
                        // The schema is endsWith("/"), and a path that does not is rejected outright.
                        put("path", root ?: PLACEHOLDER_EXTERNAL)
                    }))
                    put("categories", JSONArray().apply {
                        CATEGORIES.forEach { c ->
                            put(JSONObject().apply {
                                put("icon", c.icon)
                                put("markerColor", c.markerColor)
                                put("label", c.label)
                                put(
                                    "fileListEmptyStateDescription",
                                    "No ${c.label.lowercase()} here yet",
                                )
                                put("directoryPath", (root ?: PLACEHOLDER_EXTERNAL) + c.directory)
                                put("entityType", c.id)
                                put("supportedFileTypes", JSONArray(c.extensions))
                            })
                        }
                    })
                })
            })
        }
    }

    // -------------------------------------------------------------------- data

    fun data(context: Context, feature: String): JSONObject? {
        if (feature != INTERNAL && feature != EXTERNAL) return null
        val internal = isInternal(feature)
        val root = if (internal) internalRoot() else externalRoot(context)
        return JSONObject().apply {
            put("storageInformation", JSONArray().put(storage(root, internal)))
        }
    }

    /** How full one volume is. */
    private fun storage(root: String?, internal: Boolean): JSONObject {
        var total = 0L
        var free = 0L
        if (root != null) {
            try {
                val fs = StatFs(root)
                total = fs.totalBytes
                free = fs.availableBytes
            } catch (t: Throwable) {
                Log.w(TAG, "files: cannot stat $root (${t.javaClass.simpleName})")
            }
        }
        val used = maxOf(0L, total - free)
        return JSONObject().apply {
            put("path", root ?: PLACEHOLDER_EXTERNAL)
            put("totalSpaceBytes", total)
            put("usedSpaceBytes", used)
            put("freeSpaceBytes", free)
            put("totalSpaceString", human(total))
            put("usedSpaceString", human(used))
            put("freeSpaceString", human(free))
            put("categoriesSpaceInformation", JSONObject().apply {
                CATEGORIES.forEach { c ->
                    val bytes = if (root == null) 0L else categoryBytes(root, c)
                    put(c.id, JSONObject().apply {
                        put("spaceUsedBytes", bytes)
                        put("spaceUsedString", human(bytes))
                        put("storageCategory", c.id)
                    })
                }
                // Center reads categoriesSpaceInformation["otherFiles"] directly for the grey part of the bar.
                val counted = CATEGORIES.sumOf { if (root == null) 0L else categoryBytes(root, it) }
                val other = maxOf(0L, used - counted)
                put("otherFiles", JSONObject().apply {
                    put("spaceUsedBytes", other)
                    put("spaceUsedString", human(other))
                    put("storageCategory", "otherFiles")
                })
            })
        }
    }

    // ---------------------------------------------------------------- entities

    /** The shape of one file, so Center knows what it is reading. */
    fun entitiesConfig(entityType: String): JSONObject? {
        if (CATEGORIES.none { it.id == entityType }) return null
        return JSONObject().apply {
            put("fields", JSONObject().apply {
                put("id", JSONObject().put("type", "id"))
                put("name", JSONObject().put("type", "string"))
                put("type", JSONObject().put("type", "string"))
                put("path", JSONObject().put("type", "string"))
                put("mimeType", JSONObject().put("type", "string"))
                put("size", JSONObject().put("type", "number"))
                put("isInternal", JSONObject().put("type", "boolean"))
            })
        }
    }

    /** The files, written to a file, whose path is the answer. */
    fun entitiesData(context: Context, entityType: String): JSONObject? {
        val category = CATEGORIES.firstOrNull { it.id == entityType } ?: return null
        val internal = internalRoot()
        val external = externalRoot(context)

        val files = JSONArray()
        listFiles(internal, category, true).forEach { files.put(it) }
        external?.let { root -> listFiles(root, category, false).forEach { files.put(it) } }

        val out = File(context.cacheDir, "entities-$entityType.json")
        return try {
            // Wrapped in { "data": ...
            out.writeText(JSONObject().put("data", files).toString())
            Log.i(TAG, "files: $entityType -> ${files.length()} items in ${out.path}")
            JSONObject().apply {
                put("_status", 200)
                put("filePath", out.absolutePath)
            }
        } catch (t: Throwable) {
            Log.w(TAG, "files: cannot write $entityType (${t.javaClass.simpleName})")
            null
        }
    }

    /** Everything under a volume with one of the category's extensions. */
    private fun listFiles(
        root: String?,
        category: Category,
        internal: Boolean,
    ): List<JSONObject> {
        if (root == null) return emptyList()
        val out = ArrayList<JSONObject>()
        walk(File(root), category.extensions, 0) { f ->
            out.add(JSONObject().apply {
                put("id", f.absolutePath)
                put("name", f.name)
                put("type", f.extension.lowercase())
                put("path", f.absolutePath)
                put("mimeType", f.extension.lowercase())
                put("size", f.length())
                put("isInternal", internal)
            })
            out.size < MAX_FILES
        }
        if (out.size >= MAX_FILES) {
            Log.w(TAG, "files: ${category.id} hit the $MAX_FILES item cap under $root")
        }
        return out
    }

    /** Depth first, skipping the places nothing interesting lives. */
    private fun walk(
        dir: File,
        extensions: List<String>,
        depth: Int,
        each: (File) -> Boolean,
    ) {
        if (depth > MAX_DEPTH) return
        val entries = dir.listFiles() ?: return
        for (f in entries) {
            if (f.isDirectory) {
                // Android's own caches and thumbnails, and per app sandboxes, are never what someone is looking for in a file.
                if (f.name.startsWith(".") || f.name == "Android") continue
                walk(f, extensions, depth + 1, each)
            } else if (f.extension.lowercase() in extensions) {
                if (!each(f)) return
            }
        }
    }

    /** Rough bytes for the storage bar, without listing every file twice. */
    private fun categoryBytes(root: String, category: Category): Long {
        var total = 0L
        walk(File(root), category.extensions, 0) { f ->
            total += f.length()
            true
        }
        return total
    }

    // ----------------------------------------------------------------- volumes

    /** Always there, and always where "external storage" points on Android. */
    private fun internalRoot(): String =
        Environment.getExternalStorageDirectory().absolutePath.trimEnd('/') + "/"

    /** The card, if one is in and mounted. */
    private fun externalRoot(context: Context): String? = try {
        context.getSystemService(StorageManager::class.java)
            ?.storageVolumes
            ?.firstOrNull { it.isRemovable && it.state == Environment.MEDIA_MOUNTED }
            ?.directory
            ?.absolutePath
            ?.trimEnd('/')
            ?.plus("/")
    } catch (t: Throwable) {
        Log.w(TAG, "files: cannot list volumes (${t.javaClass.simpleName})")
        null
    }

    /** A path to name the card by when there is no card. */
    private const val PLACEHOLDER_EXTERNAL = "/storage/sdcard1/"

    private const val MAX_DEPTH = 6
    private const val MAX_FILES = 2000

    private fun human(bytes: Long): String {
        if (bytes < 1024) return "$bytes B"
        val units = listOf("kB", "MB", "GB", "TB")
        var value = bytes.toDouble() / 1024
        var unit = 0
        while (value >= 1024 && unit < units.size - 1) {
            value /= 1024
            unit++
        }
        return String.format("%.1f %s", value, units[unit])
    }
}
