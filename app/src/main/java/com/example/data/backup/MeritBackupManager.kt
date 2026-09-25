package com.example.data.backup

import android.content.Context
import com.example.data.MeritEntity
import com.example.data.MeritRepository
import com.example.data.getMediaUris
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

data class BackupSummary(
    val meritsCount: Int,
    val mediaCount: Int,
    val createdAt: Long,
    val version: Int = 1
)

class MeritBackupManager(
    private val context: Context,
    private val repository: MeritRepository
) {

    /**
     * Creates a complete backup package (.bodhibackup / ZIP) written to the provided output stream.
     * Includes all merit posts and all attached full-resolution photographs.
     */
    suspend fun createBackup(outputStream: OutputStream): Result<BackupSummary> = withContext(Dispatchers.IO) {
        try {
            val merits = repository.getAllMeritsSync()
            var mediaCount = 0

            ZipOutputStream(outputStream.buffered()).use { zipOut ->
                val jsonMeritsArray = JSONArray()

                for (merit in merits) {
                    val meritObj = JSONObject().apply {
                        put("id", merit.id)
                        put("title", merit.title)
                        put("category", merit.category)
                        put("description", merit.description)
                        put("dedication", merit.dedication)
                        put("timestamp", merit.timestamp)
                        put("branchIndex", merit.branchIndex)
                        put("leafOffsetRatio", merit.leafOffsetRatio.toDouble())
                        put("leafAngleOffset", merit.leafAngleOffset.toDouble())
                    }

                    val imageUri = merit.imageUri
                    if (imageUri != null) {
                        val uris = merit.getMediaUris()
                        val mediaEntryNames = org.json.JSONArray()
                        for (singleUri in uris) {
                            if (singleUri.startsWith("preset:")) {
                                meritObj.put("presetUri", singleUri)
                            } else {
                                val imgFile = File(singleUri)
                                if (imgFile.exists() && imgFile.canRead()) {
                                    val entryName = "media/img_${merit.id}_${System.currentTimeMillis()}_${imgFile.name}"
                                    zipOut.putNextEntry(ZipEntry(entryName))
                                    FileInputStream(imgFile).use { fileIn ->
                                        fileIn.copyTo(zipOut)
                                    }
                                    zipOut.closeEntry()
                                    mediaEntryNames.put(entryName)
                                    mediaCount++
                                }
                            }
                        }
                        if (mediaEntryNames.length() > 0) {
                            meritObj.put("mediaEntryNames", mediaEntryNames)
                            meritObj.put("mediaEntryName", mediaEntryNames.getString(0))
                        } else if (!meritObj.has("presetUri")) {
                            meritObj.put("originalUri", imageUri)
                        }
                    }

                    jsonMeritsArray.put(meritObj)
                }

                val manifestObj = JSONObject().apply {
                    put("appName", "Bodhi Merit")
                    put("version", 1)
                    put("createdAt", System.currentTimeMillis())
                    put("meritsCount", merits.size)
                    put("mediaCount", mediaCount)
                    put("merits", jsonMeritsArray)
                }

                zipOut.putNextEntry(ZipEntry("manifest.json"))
                zipOut.write(manifestObj.toString(2).toByteArray(Charsets.UTF_8))
                zipOut.closeEntry()
                zipOut.flush()
            }

            Result.success(
                BackupSummary(
                    meritsCount = merits.size,
                    mediaCount = mediaCount,
                    createdAt = System.currentTimeMillis()
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Inspects a backup package without modifying database or storage.
     * Returns metadata summary (count of merits, photos, date created).
     */
    suspend fun inspectBackup(inputStream: InputStream): Result<BackupSummary> = withContext(Dispatchers.IO) {
        try {
            var manifestContent: String? = null

            ZipInputStream(inputStream.buffered()).use { zipIn ->
                var entry = zipIn.nextEntry
                while (entry != null) {
                    if (entry.name == "manifest.json") {
                        val baos = ByteArrayOutputStream()
                        zipIn.copyTo(baos)
                        manifestContent = baos.toString(Charsets.UTF_8.name())
                        break
                    }
                    entry = zipIn.nextEntry
                }
            }

            if (manifestContent == null) {
                return@withContext Result.failure(IllegalArgumentException("Invalid backup file: manifest.json not found."))
            }

            val json = JSONObject(manifestContent!!)
            val meritsCount = json.optInt("meritsCount", 0)
            val mediaCount = json.optInt("mediaCount", 0)
            val createdAt = json.optLong("createdAt", System.currentTimeMillis())
            val version = json.optInt("version", 1)

            Result.success(
                BackupSummary(
                    meritsCount = meritsCount,
                    mediaCount = mediaCount,
                    createdAt = createdAt,
                    version = version
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Restores merits and unpacked photos from the backup package.
     * @param replaceAll If true, clears existing merits. If false, merges with existing merits.
     */
    suspend fun restoreBackup(
        inputStream: InputStream,
        replaceAll: Boolean
    ): Result<BackupSummary> = withContext(Dispatchers.IO) {
        try {
            val photosDir = File(context.filesDir, "merit_photos").apply { if (!exists()) mkdirs() }
            val mediaPathMap = mutableMapOf<String, String>()
            var manifestContent: String? = null

            ZipInputStream(inputStream.buffered()).use { zipIn ->
                var entry = zipIn.nextEntry
                while (entry != null) {
                    if (entry.name == "manifest.json") {
                        val baos = ByteArrayOutputStream()
                        zipIn.copyTo(baos)
                        manifestContent = baos.toString(Charsets.UTF_8.name())
                    } else if (entry.name.startsWith("media/") && !entry.isDirectory) {
                        val rawFileName = entry.name.substringAfterLast('/')
                        val safeFileName = "restored_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}_$rawFileName"
                        val targetFile = File(photosDir, safeFileName)
                        FileOutputStream(targetFile).use { fileOut ->
                            zipIn.copyTo(fileOut)
                        }
                        mediaPathMap[entry.name] = targetFile.absolutePath
                    }
                    entry = zipIn.nextEntry
                }
            }

            if (manifestContent == null) {
                return@withContext Result.failure(IllegalArgumentException("Invalid backup: manifest.json missing"))
            }

            val manifest = JSONObject(manifestContent!!)
            val meritsArray = manifest.getJSONArray("merits")
            val maxSlots = com.example.ui.components.BodhiTreeGeometry.LEAF_SLOTS.size

            val currentMerits = if (replaceAll) {
                repository.clearAll()
                emptyList()
            } else {
                repository.getAllMeritsSync()
            }

            val usedSlots = currentMerits.map { it.branchIndex }.toMutableSet()
            val newEntities = mutableListOf<MeritEntity>()

            for (i in 0 until meritsArray.length()) {
                val item = meritsArray.getJSONObject(i)
                val originalBranchIndex = item.optInt("branchIndex", 0)

                // Assign slot: keep original if free, else find next available slot
                val assignedSlot = if (originalBranchIndex in 0 until maxSlots && originalBranchIndex !in usedSlots) {
                    usedSlots.add(originalBranchIndex)
                    originalBranchIndex
                } else {
                    val freeSlot = (0 until maxSlots).firstOrNull { it !in usedSlots } ?: (usedSlots.size % maxSlots)
                    usedSlots.add(freeSlot)
                    freeSlot
                }

                // Resolve imageUri (supports multiple media entries)
                val imageUri = when {
                    item.has("mediaEntryNames") -> {
                        val arr = item.getJSONArray("mediaEntryNames")
                        val paths = mutableListOf<String>()
                        for (idx in 0 until arr.length()) {
                            mediaPathMap[arr.getString(idx)]?.let { paths.add(it) }
                        }
                        if (paths.isNotEmpty()) paths.joinToString("|") else null
                    }
                    item.has("presetUri") -> item.getString("presetUri")
                    item.has("mediaEntryName") -> mediaPathMap[item.getString("mediaEntryName")]
                    item.has("originalUri") -> item.getString("originalUri")
                    else -> null
                }

                newEntities.add(
                    MeritEntity(
                        title = item.optString("title", "Wholesome Deed"),
                        category = item.optString("category", "DANA"),
                        description = item.optString("description", ""),
                        dedication = item.optString("dedication", ""),
                        imageUri = imageUri,
                        timestamp = item.optLong("timestamp", System.currentTimeMillis()),
                        branchIndex = assignedSlot,
                        leafOffsetRatio = item.optDouble("leafOffsetRatio", 0.5).toFloat(),
                        leafAngleOffset = item.optDouble("leafAngleOffset", 0.0).toFloat()
                    )
                )
            }

            repository.insertAll(newEntities)

            Result.success(
                BackupSummary(
                    meritsCount = newEntities.size,
                    mediaCount = mediaPathMap.size,
                    createdAt = manifest.optLong("createdAt", System.currentTimeMillis())
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Creates a temporary backup file in app cache directory for instant sharing.
     */
    suspend fun createBackupTempFile(): Result<File> = withContext(Dispatchers.IO) {
        try {
            val backupDir = File(context.cacheDir, "backups").apply { if (!exists()) mkdirs() }
            val dateFormat = SimpleDateFormat("yyyyMMdd_HHmm", Locale.US).format(Date())
            val tempFile = File(backupDir, "Bodhi_Merit_Backup_$dateFormat.bodhibackup")

            FileOutputStream(tempFile).use { fos ->
                val result = createBackup(fos)
                if (result.isFailure) {
                    tempFile.delete()
                    return@withContext Result.failure(result.exceptionOrNull() ?: Exception("Unknown error"))
                }
            }

            Result.success(tempFile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
