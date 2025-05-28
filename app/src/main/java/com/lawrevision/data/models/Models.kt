package com.lawrevision.data.models

data class SpacingSchedule(
    val days: List<Int>,
    val activities: List<String>
)

data class Flashcard(
    val examBoard: String,
    val unit: String,
    val topic: String,
    val subtopic: String,
    val contentSummary: String,
    val keyLegislation: List<String>,
    val keyCases: List<String>,
    val question: String,
    val answer: String,
    val difficulty: Int,
    val cognitiveType: String,
    val spacingTag: String,
    val tags: List<String>
)

data class Pathways(
    val AQA: List<String>,
    val OCR: List<String>,
    val Edexcel: List<String>
)

data class ContentBank(
    val spacing_schedule: SpacingSchedule,
    val content_bank: List<Flashcard>,
    val pathways: Pathways
)

// Room Database Entities
data class UserProgress(
    val id: Int = 0,
    val flashcardId: String,
    val examBoard: String,
    val lastReviewed: Long,
    val reviewStage: Int,
    val correctCount: Int,
    val incorrectCount: Int
)

data class UserPreferences(
    val examBoard: String,
    val isExamMode: Boolean = false,
    val lastNotificationTime: Long = 0
) 