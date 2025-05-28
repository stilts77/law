package com.lawrevision.data.repository

import android.content.Context
import com.lawrevision.data.database.AppDatabase
import com.lawrevision.data.database.UserPreferencesDao
import com.lawrevision.data.database.UserProgressDao
import com.lawrevision.data.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONObject
import java.io.IOException

class RevisionRepository(context: Context) {
    private val userProgressDao: UserProgressDao
    private val userPreferencesDao: UserPreferencesDao
    private val contentBank: ContentBank

    init {
        val database = AppDatabase.getDatabase(context)
        userProgressDao = database.userProgressDao()
        userPreferencesDao = database.userPreferencesDao()
        contentBank = loadContentBank(context)
    }

    private fun loadContentBank(context: Context): ContentBank {
        return try {
            val jsonString = context.assets.open("content.json").bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(jsonString)
            
            // Parse the JSON into ContentBank object
            // This is a simplified version - you'll need to implement the full parsing logic
            ContentBank(
                spacing_schedule = SpacingSchedule(
                    days = listOf(1, 2, 4, 7, 14, 30),
                    activities = listOf("Review", "Practice", "Test")
                ),
                content_bank = emptyList(), // Parse from JSON
                pathways = Pathways(
                    AQA = emptyList(),
                    OCR = emptyList(),
                    Edexcel = emptyList()
                )
            )
        } catch (e: IOException) {
            throw RuntimeException("Could not load content bank", e)
        }
    }

    fun getPreferences(): Flow<UserPreferences?> = userPreferencesDao.getPreferences()

    suspend fun setExamBoard(examBoard: String) {
        userPreferencesDao.insertPreferences(UserPreferences(examBoard = examBoard))
    }

    suspend fun setExamMode(isExamMode: Boolean) {
        userPreferencesDao.getPreferences().collect { preferences ->
            preferences?.let {
                userPreferencesDao.updatePreferences(it.copy(isExamMode = isExamMode))
            }
        }
    }

    fun getFlashcardsForBoard(examBoard: String): List<Flashcard> {
        return contentBank.content_bank.filter { it.examBoard == examBoard }
    }

    fun getProgressForBoard(examBoard: String): Flow<List<UserProgress>> {
        return userProgressDao.getProgressForBoard(examBoard)
    }

    suspend fun updateProgress(progress: UserProgress) {
        userProgressDao.insertProgress(progress)
    }

    fun getDueFlashcards(examBoard: String): Flow<List<Flashcard>> {
        return userProgressDao.getProgressForBoard(examBoard).map { progressList ->
            val now = System.currentTimeMillis()
            val dueFlashcards = mutableListOf<Flashcard>()
            
            for (progress in progressList) {
                val flashcard = contentBank.content_bank.find { it.examBoard == progress.examBoard }
                if (flashcard != null) {
                    val daysSinceLastReview = (now - progress.lastReviewed) / (1000 * 60 * 60 * 24)
                    val nextReviewDay = contentBank.spacing_schedule.days[progress.reviewStage]
                    
                    if (daysSinceLastReview >= nextReviewDay) {
                        dueFlashcards.add(flashcard)
                    }
                }
            }
            
            dueFlashcards
        }
    }
} 