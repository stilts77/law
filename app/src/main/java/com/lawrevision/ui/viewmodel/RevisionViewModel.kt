package com.lawrevision.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lawrevision.data.models.Flashcard
import com.lawrevision.data.models.UserPreferences
import com.lawrevision.data.models.UserProgress
import com.lawrevision.data.repository.RevisionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RevisionViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = RevisionRepository(application)
    
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _currentFlashcard = MutableStateFlow<Flashcard?>(null)
    val currentFlashcard: StateFlow<Flashcard?> = _currentFlashcard.asStateFlow()

    private val _isAnswerVisible = MutableStateFlow(false)
    val isAnswerVisible: StateFlow<Boolean> = _isAnswerVisible.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getPreferences().collect { preferences ->
                if (preferences == null) {
                    _uiState.value = UiState.SelectExamBoard
                } else {
                    loadFlashcards(preferences)
                }
            }
        }
    }

    private fun loadFlashcards(preferences: UserPreferences) {
        viewModelScope.launch {
            if (preferences.isExamMode) {
                val flashcards = repository.getFlashcardsForBoard(preferences.examBoard)
                _uiState.value = UiState.ShowingFlashcards(flashcards)
                if (flashcards.isNotEmpty()) {
                    _currentFlashcard.value = flashcards.first()
                }
            } else {
                repository.getDueFlashcards(preferences.examBoard).collect { flashcards ->
                    _uiState.value = UiState.ShowingFlashcards(flashcards)
                    if (flashcards.isNotEmpty()) {
                        _currentFlashcard.value = flashcards.first()
                    }
                }
            }
        }
    }

    fun setExamBoard(examBoard: String) {
        viewModelScope.launch {
            repository.setExamBoard(examBoard)
        }
    }

    fun toggleExamMode() {
        viewModelScope.launch {
            repository.getPreferences().collect { preferences ->
                preferences?.let {
                    repository.setExamMode(!it.isExamMode)
                }
            }
        }
    }

    fun toggleAnswer() {
        _isAnswerVisible.value = !_isAnswerVisible.value
    }

    fun markAnswer(correct: Boolean) {
        viewModelScope.launch {
            val flashcard = _currentFlashcard.value ?: return@launch
            val progress = UserProgress(
                flashcardId = flashcard.toString(),
                examBoard = flashcard.examBoard,
                lastReviewed = System.currentTimeMillis(),
                reviewStage = if (correct) 1 else 0,
                correctCount = if (correct) 1 else 0,
                incorrectCount = if (correct) 0 else 1
            )
            repository.updateProgress(progress)
            _isAnswerVisible.value = false
            // Move to next flashcard
            when (val state = _uiState.value) {
                is UiState.ShowingFlashcards -> {
                    val currentIndex = state.flashcards.indexOf(flashcard)
                    if (currentIndex < state.flashcards.size - 1) {
                        _currentFlashcard.value = state.flashcards[currentIndex + 1]
                    }
                }
                else -> {}
            }
        }
    }
}

sealed class UiState {
    object Loading : UiState()
    object SelectExamBoard : UiState()
    data class ShowingFlashcards(val flashcards: List<Flashcard>) : UiState()
} 