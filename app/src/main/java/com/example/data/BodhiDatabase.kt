package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [MeritEntity::class], version = 1, exportSchema = false)
abstract class BodhiDatabase : RoomDatabase() {

    abstract fun meritDao(): MeritDao

    companion object {
        @Volatile
        private var INSTANCE: BodhiDatabase? = null

        fun getDatabase(context: Context): BodhiDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BodhiDatabase::class.java,
                    "bodhi_merit_database"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Pre-populate with initial serene merits so the tree starts with blossoming leaves
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialMerits(database.meritDao())
                    }
                }
            }

            private suspend fun populateInitialMerits(dao: MeritDao) {
                val now = System.currentTimeMillis()
                val merits = listOf(
                    MeritEntity(
                        title = "Morning Loving-Kindness Meditation",
                        category = MeritCategory.BHAVANA.name,
                        description = "Sat in stillness at dawn for 30 minutes cultivating goodwill towards all living beings. Mind rested in peace, breathing gently under the morning sky.",
                        dedication = "May all beings near and far be safe, peaceful, and free from suffering.",
                        imageUri = "preset:ic_merit_meditation",
                        timestamp = now - 86400000L * 3,
                        branchIndex = 1,
                        leafOffsetRatio = 0.35f,
                        leafAngleOffset = -15f
                    ),
                    MeritEntity(
                        title = "Offering Warm Food & Care",
                        category = MeritCategory.DANA.name,
                        description = "Prepared nutritious meals and offered care to neighborhood elders and the community shelter. Joy filled the heart when seeing their warm smiles.",
                        dedication = "Dedicated to my parents, teachers, and all who have supported my path.",
                        imageUri = "preset:ic_merit_dana",
                        timestamp = now - 86400000L * 2,
                        branchIndex = 2,
                        leafOffsetRatio = 0.65f,
                        leafAngleOffset = 20f
                    ),
                    MeritEntity(
                        title = "Rescued & Fed Stray Birds",
                        category = MeritCategory.KINDNESS.name,
                        description = "Cleaned a water basin, refilled seeds in the courtyard, and sheltered a fledgling bird away from danger until it regained strength.",
                        dedication = "May all animals and gentle creatures find sanctuary and kindness.",
                        imageUri = "preset:ic_merit_kindness",
                        timestamp = now - 86400000L,
                        branchIndex = 0,
                        leafOffsetRatio = 0.8f,
                        leafAngleOffset = 10f
                    ),
                    MeritEntity(
                        title = "Offered Lotus & Lit Evening Lamps",
                        category = MeritCategory.TEMPLE.name,
                        description = "Offered fresh lotus blossoms and lit butter lamps at twilight. Reflected on the impermanence of all things and the radiant clarity of wisdom.",
                        dedication = "May the light of Dhamma dispel the darkness of ignorance in the world.",
                        imageUri = "preset:ic_merit_lotus",
                        timestamp = now - 18000000L,
                        branchIndex = 3,
                        leafOffsetRatio = 0.5f,
                        leafAngleOffset = -25f
                    )
                )
                for (merit in merits) {
                    dao.insertMerit(merit)
                }
            }
        }
    }
}
