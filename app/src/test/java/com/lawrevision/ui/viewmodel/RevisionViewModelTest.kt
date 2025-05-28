package com.lawrevision.ui.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.lawrevision.data.models.Flashcard
import com.lawrevision.data.models.UserPreferences
import com.lawrevision.data.repository.RevisionRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class RevisionViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var application: Application
    private lateinit var repository: RevisionRepository
    private lateinit var viewModel: RevisionViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        application = mockk(relaxed = true)
        repository = mockk(relaxed = true)
        viewModel = RevisionViewModel(application)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when no preferences exist, shows exam board selection`() = runTest {
        // Given
        coEvery { repository.getPreferences() } returns flowOf(null)

        // When
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assert(viewModel.uiState.value is UiState.SelectExamBoard)
    }

    @Test
    fun `when preferences exist, loads flashcards`() = runTest {
        // Given
        val preferences = UserPreferences(examBoard = "AQA")
        val flashcards = listOf(
            Flashcard(
                examBoard = "AQA",
                unit = "Criminal Law",
                topic = "Murder",
                subtopic = "Actus Reus",
                contentSummary = "Test",
                keyLegislation = emptyList(),
                keyCases = emptyList(),
                question = "Test?",
                answer = "Test",
                difficulty = 1,
                cognitiveType = "Test",
                spacingTag = "Test",
                tags = emptyList()
            )
        )
        coEvery { repository.getPreferences() } returns flowOf(preferences)
        coEvery { repository.getFlashcardsForBoard("AQA") } returns flashcards

        // When
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assert(viewModel.uiState.value is UiState.ShowingFlashcards)
        assert((viewModel.uiState.value as UiState.ShowingFlashcards).flashcards == flashcards)
    }

    @Test
    fun `marking answer updates progress and moves to next card`() = runTest {
        // Given
        val flashcard = Flashcard(
            examBoard = "AQA",
            unit = "Criminal Law",
            topic = "Murder",
            subtopic = "Actus Reus",
            contentSummary = "Test",
            keyLegislation = emptyList(),
            keyCases = emptyList(),
            question = "Test?",
            answer = "Test",
            difficulty = 1,
            cognitiveType = "Test",
            spacingTag = "Test",
            tags = emptyList()
        )
        viewModel.setCurrentFlashcard(flashcard)

        // When
        viewModel.markAnswer(true)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { repository.updateProgress(any()) }
        assert(!viewModel.isAnswerVisible.value)
    }
} 