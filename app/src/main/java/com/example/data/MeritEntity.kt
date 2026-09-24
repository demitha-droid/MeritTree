package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

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
