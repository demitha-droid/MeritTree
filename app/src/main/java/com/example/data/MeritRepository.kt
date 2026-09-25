package com.example.data

import kotlinx.coroutines.flow.Flow

class MeritRepository(private val meritDao: MeritDao) {

    val allMerits: Flow<List<MeritEntity>> = meritDao.getAllMerits()
    val allMeritsDescending: Flow<List<MeritEntity>> = meritDao.getAllMeritsDescending()
    val meritCount: Flow<Int> = meritDao.getMeritCount()

    fun getMeritById(id: Long): Flow<MeritEntity?> = meritDao.getMeritById(id)

    suspend fun insert(merit: MeritEntity): Long = meritDao.insertMerit(merit)

    suspend fun insertAll(merits: List<MeritEntity>): List<Long> = meritDao.insertMerits(merits)

    suspend fun getAllMeritsSync(): List<MeritEntity> = meritDao.getAllMeritsSync()

    suspend fun clearAll() = meritDao.clearAllMerits()

    suspend fun update(merit: MeritEntity) = meritDao.updateMerit(merit)

    suspend fun delete(merit: MeritEntity) = meritDao.deleteMerit(merit)

    suspend fun deleteById(id: Long) = meritDao.deleteMeritById(id)
}
