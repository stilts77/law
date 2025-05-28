package com.lawrevision.data.database

import androidx.room.*
import com.lawrevision.data.models.UserProgress
import com.lawrevision.data.models.UserPreferences
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProgressDao {
    @Query("SELECT * FROM user_progress WHERE examBoard = :examBoard")
    fun getProgressForBoard(examBoard: String): Flow<List<UserProgress>>

    @Query("SELECT * FROM user_progress WHERE flashcardId = :flashcardId")
    suspend fun getProgressForFlashcard(flashcardId: String): UserProgress?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: UserProgress)

    @Update
    suspend fun updateProgress(progress: UserProgress)
}

@Dao
interface UserPreferencesDao {
    @Query("SELECT * FROM user_preferences LIMIT 1")
    fun getPreferences(): Flow<UserPreferences?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreferences(preferences: UserPreferences)

    @Update
    suspend fun updatePreferences(preferences: UserPreferences)
}

@Database(
    entities = [UserProgress::class, UserPreferences::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userProgressDao(): UserProgressDao
    abstract fun userPreferencesDao(): UserPreferencesDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "law_revision_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
} 