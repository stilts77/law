package com.lawrevision.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lawrevision.data.models.Flashcard
import com.lawrevision.ui.viewmodel.RevisionViewModel
import com.lawrevision.ui.viewmodel.UiState
import io.mockk.every
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun examBoardSelectionScreen_showsAllBoards() {
        // Given
        val viewModel = mockk<RevisionViewModel>()
        every { viewModel.uiState } returns UiState.SelectExamBoard

        // When
        composeTestRule.setContent {
            RevisionApp(viewModel = viewModel)
        }

        // Then
        composeTestRule.onNodeWithText("AQA").assertExists()
        composeTestRule.onNodeWithText("OCR").assertExists()
        composeTestRule.onNodeWithText("Edexcel").assertExists()
    }

    @Test
    fun flashcardScreen_showsQuestionAndAnswer() {
        // Given
        val flashcard = Flashcard(
            examBoard = "AQA",
            unit = "Criminal Law",
            topic = "Murder",
            subtopic = "Actus Reus",
            contentSummary = "Test",
            keyLegislation = emptyList(),
            keyCases = emptyList(),
            question = "Test Question?",
            answer = "Test Answer",
            difficulty = 1,
            cognitiveType = "Test",
            spacingTag = "Test",
            tags = emptyList()
        )
        val viewModel = mockk<RevisionViewModel>()
        every { viewModel.uiState } returns UiState.ShowingFlashcards(listOf(flashcard))
        every { viewModel.currentFlashcard } returns flashcard
        every { viewModel.isAnswerVisible } returns false

        // When
        composeTestRule.setContent {
            RevisionApp(viewModel = viewModel)
        }

        // Then
        composeTestRule.onNodeWithText("Test Question?").assertExists()
        composeTestRule.onNodeWithText("Show Answer").assertExists()
    }
} 