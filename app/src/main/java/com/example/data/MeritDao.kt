package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MeritDao {
    @Query("SELECT * FROM merits ORDER BY timestamp ASC")
    fun getAllMerits(): Flow<List<MeritEntity>>

    @Query("SELECT * FROM merits ORDER BY timestamp DESC")
    fun getAllMeritsDescending(): Flow<List<MeritEntity>>

    @Query("SELECT * FROM merits WHERE id = :id LIMIT 1")
    fun getMeritById(id: Long): Flow<MeritEntity?>

    @Query("SELECT COUNT(*) FROM merits")
    fun getMeritCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMerit(merit: MeritEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMerits(merits: List<MeritEntity>): List<Long>

    @Query("SELECT * FROM merits ORDER BY timestamp ASC")
    suspend fun getAllMeritsSync(): List<MeritEntity>

    @Update
    suspend fun updateMerit(merit: MeritEntity)

    @Delete
    suspend fun deleteMerit(merit: MeritEntity)

    @Query("DELETE FROM merits WHERE id = :id")
    suspend fun deleteMeritById(id: Long)

    @Query("DELETE FROM merits")
    suspend fun clearAllMerits()
}
