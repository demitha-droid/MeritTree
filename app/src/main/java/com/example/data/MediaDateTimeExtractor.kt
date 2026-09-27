package com.example.data

import android.content.Context
import android.media.ExifInterface
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object MediaDateTimeExtractor {

    private val exifDateFormats = listOf(
        SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.US),
        SimpleDateFormat("yyyy:MM:dd HH:mm", Locale.US),
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US),
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US),
        SimpleDateFormat("yyyyMMdd'T'HHmmss.SSS'Z'", Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC") },
        SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'", Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC") },
        SimpleDateFormat("yyyyMMdd'T'HHmmss", Locale.US),
        SimpleDateFormat("yyyy:MM:dd", Locale.US),
        SimpleDateFormat("yyyy-MM-dd", Locale.US)
    )

    /**
     * Extracts the date and time when the photo/video was originally taken.
     * Checks EXIF metadata (DateTimeOriginal, DateTimeDigitized, DateTime),
     * MediaStore DATE_TAKEN / DATE_MODIFIED, video metadata, and file attributes.
     */
    fun extractMediaTimestamp(context: Context, uriOrPath: String?): Long? {
        if (uriOrPath.isNullOrBlank() || uriOrPath.startsWith("preset:")) return null

        // 1. If it's a content:// Uri
        if (uriOrPath.startsWith("content://")) {
            val uri = Uri.parse(uriOrPath)

            // Try MediaStore query first (super fast and accurate for gallery items)
            try {
                val projection = arrayOf(
                    MediaStore.MediaColumns.DATE_TAKEN,
                    MediaStore.MediaColumns.DATE_MODIFIED
                )
                context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val dateTakenIdx = cursor.getColumnIndex(MediaStore.MediaColumns.DATE_TAKEN)
                        if (dateTakenIdx != -1) {
                            val dateTaken = cursor.getLong(dateTakenIdx)
                            if (dateTaken > 100000000000L) { // Valid millisecond timestamp
                                return dateTaken
                            }
                        }
                        val dateModifiedIdx = cursor.getColumnIndex(MediaStore.MediaColumns.DATE_MODIFIED)
                        if (dateModifiedIdx != -1) {
                            val dateModified = cursor.getLong(dateModifiedIdx)
                            if (dateModified > 100000000L) {
                                return dateModified * 1000L
                            }
                        }
                    }
                }
            } catch (_: Exception) {}

            // Try EXIF from InputStream (for image content URIs)
            try {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val exif = ExifInterface(stream)
                    val exifTimestamp = parseExifDate(exif)
                    if (exifTimestamp != null && exifTimestamp > 0) {
                        return exifTimestamp
                    }
                }
            } catch (_: Exception) {}

            // Try Video metadata if video
            try {
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(context, uri)
                val dateStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE)
                retriever.release()
                if (!dateStr.isNullOrBlank()) {
                    val parsed = parseDateString(dateStr)
                    if (parsed != null && parsed > 0) return parsed
                }
            } catch (_: Exception) {}
        }

        // 2. If it's a file path or file URI
        val filePath = if (uriOrPath.startsWith("file://")) {
            uriOrPath.removePrefix("file://")
        } else {
            uriOrPath
        }

        val file = File(filePath)
        if (file.exists() && file.canRead()) {
            val lowerName = file.name.lowercase()
            val isVideo = lowerName.endsWith(".mp4") || lowerName.endsWith(".mov") ||
                    lowerName.endsWith(".3gp") || lowerName.endsWith(".mkv") || lowerName.endsWith(".webm")

            if (isVideo) {
                try {
                    val retriever = MediaMetadataRetriever()
                    retriever.setDataSource(file.absolutePath)
                    val dateStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE)
                    retriever.release()
                    if (!dateStr.isNullOrBlank()) {
                        val parsed = parseDateString(dateStr)
                        if (parsed != null && parsed > 0) return parsed
                    }
                } catch (_: Exception) {}
            } else {
                // Try EXIF from File path
                try {
                    val exif = ExifInterface(file.absolutePath)
                    val exifTimestamp = parseExifDate(exif)
                    if (exifTimestamp != null && exifTimestamp > 0) {
                        return exifTimestamp
                    }
                } catch (_: Exception) {}
            }
        }

        return null
    }

    /**
     * Given a list of media URIs or file paths, extracts the timestamp from the first valid image/video,
     * or the earliest timestamp if multiple exist.
     */
    fun extractEarliestMediaTimestamp(context: Context, uris: List<String>): Long? {
        val validTimestamps = uris.mapNotNull { extractMediaTimestamp(context, it) }
        return validTimestamps.minOrNull()
    }

    private fun parseExifDate(exif: ExifInterface): Long? {
        val tags = listOf(
            ExifInterface.TAG_DATETIME_ORIGINAL,
            ExifInterface.TAG_DATETIME_DIGITIZED,
            ExifInterface.TAG_DATETIME
        )

        for (tag in tags) {
            val dateStr = exif.getAttribute(tag)
            if (!dateStr.isNullOrBlank()) {
                val parsed = parseDateString(dateStr)
                if (parsed != null && parsed > 0) {
                    return parsed
                }
            }
        }

        // Check GPS Date & Time stamp as additional fallback
        val gpsDate = exif.getAttribute(ExifInterface.TAG_GPS_DATESTAMP)
        val gpsTime = exif.getAttribute(ExifInterface.TAG_GPS_TIMESTAMP)
        if (!gpsDate.isNullOrBlank() && !gpsTime.isNullOrBlank()) {
            val combined = "$gpsDate $gpsTime"
            val parsed = parseDateString(combined)
            if (parsed != null && parsed > 0) {
                return parsed
            }
        }

        return null
    }

    fun parseDateString(rawDate: String): Long? {
        val trimmed = rawDate.trim()
        for (format in exifDateFormats) {
            try {
                synchronized(format) {
                    val date = format.parse(trimmed)
                    if (date != null && date.time > 100000000L) {
                        return date.time
                    }
                }
            } catch (_: Exception) {}
        }
        return null
    }
}
