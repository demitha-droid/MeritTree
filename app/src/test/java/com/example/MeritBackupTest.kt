package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.BodhiDatabase
import com.example.data.MeritEntity
import com.example.data.MeritRepository
import com.example.data.backup.MeritBackupManager
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File

@RunWith(RobolectricTestRunner::class)
class MeritBackupTest {

    private lateinit var context: Context
    private lateinit var database: BodhiDatabase
    private lateinit var repository: MeritRepository
    private lateinit var backupManager: MeritBackupManager

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(context, BodhiDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = MeritRepository(database.meritDao())
        backupManager = MeritBackupManager(context, repository)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testBackupAndRestore_withMedia() = runBlocking {
        // Create a dummy image file
        val dummyPhoto = File(context.filesDir, "test_merit_photo.jpg").apply {
            writeBytes("DUMMY_IMAGE_DATA_12345".toByteArray())
        }

        // Insert a test merit pointing to the image
        val testMerit = MeritEntity(
            title = "Meditation in Forest",
            category = "BHAVANA",
            description = "30 mins mindful breath",
            dedication = "For all beings",
            imageUri = dummyPhoto.absolutePath,
            timestamp = 1700000000000L,
            branchIndex = 2
        )
        repository.insert(testMerit)

        // 1. Create Backup
        val baos = ByteArrayOutputStream()
        val backupResult = backupManager.createBackup(baos)
        assertTrue("Backup should succeed: ${backupResult.exceptionOrNull()}", backupResult.isSuccess)
        val backupSummary = backupResult.getOrThrow()
        assertEquals(1, backupSummary.meritsCount)
        assertEquals(1, backupSummary.mediaCount)

        // 2. Inspect Backup
        val inspectResult = backupManager.inspectBackup(ByteArrayInputStream(baos.toByteArray()))
        assertTrue("Inspect should succeed: ${inspectResult.exceptionOrNull()}", inspectResult.isSuccess)
        val inspected = inspectResult.getOrThrow()
        assertEquals(1, inspected.meritsCount)
        assertEquals(1, inspected.mediaCount)

        // 3. Clear database to simulate new install / data wipe
        repository.clearAll()
        assertEquals(0, repository.getAllMeritsSync().size)

        // 4. Restore Backup
        val restoreResult = backupManager.restoreBackup(
            inputStream = ByteArrayInputStream(baos.toByteArray()),
            replaceAll = true
        )
        assertTrue("Restore should succeed: ${restoreResult.exceptionOrNull()}", restoreResult.isSuccess)
        val restoredSummary = restoreResult.getOrThrow()
        assertEquals(1, restoredSummary.meritsCount)
        assertEquals(1, restoredSummary.mediaCount)

        // Verify merit is in DB
        val restoredMerits = repository.getAllMeritsSync()
        assertEquals(1, restoredMerits.size)
        val restored = restoredMerits.first()
        assertEquals("Meditation in Forest", restored.title)
        assertEquals("BHAVANA", restored.category)
        assertTrue("Restored image should exist on disk", File(restored.imageUri!!).exists())
    }
}
