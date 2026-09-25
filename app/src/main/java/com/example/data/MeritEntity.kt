package com.example.data

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Immutable
@Entity(tableName = "merits")
data class MeritEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: String, // from MeritCategory.name
    val description: String,
    val dedication: String = "",
    val imageUri: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val branchIndex: Int = 0,
    val leafOffsetRatio: Float = 0.5f,
    val leafAngleOffset: Float = 0f
)

fun MeritEntity.getMediaUris(): List<String> {
    if (imageUri.isNullOrBlank()) return emptyList()
    return imageUri.split("|").map { it.trim() }.filter { it.isNotBlank() }
}

fun MeritEntity.getFirstMediaUri(): String? {
    return getMediaUris().firstOrNull()
}

fun List<String>.toMediaUriString(): String? {
    val filtered = this.map { it.trim() }.filter { it.isNotBlank() }
    return if (filtered.isEmpty()) null else filtered.joinToString("|")
}
