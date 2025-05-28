package com.lawrevision.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lawrevision.ui.theme.LawRevisionTheme
import com.lawrevision.ui.viewmodel.RevisionViewModel
import com.lawrevision.ui.viewmodel.UiState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LawRevisionTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    RevisionApp()
                }
            }
        }
    }
}

@Composable
fun RevisionApp(
    viewModel: RevisionViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentFlashcard by viewModel.currentFlashcard.collectAsState()
    val isAnswerVisible by viewModel.isAnswerVisible.collectAsState()

    when (uiState) {
        is UiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator()
            }
        }
        is UiState.SelectExamBoard -> {
            ExamBoardSelectionScreen(
                onExamBoardSelected = { board ->
                    viewModel.setExamBoard(board)
                }
            )
        }
        is UiState.ShowingFlashcards -> {
            FlashcardScreen(
                flashcard = currentFlashcard,
                isAnswerVisible = isAnswerVisible,
                onToggleAnswer = { viewModel.toggleAnswer() },
                onMarkAnswer = { correct -> viewModel.markAnswer(correct) },
                onToggleExamMode = { viewModel.toggleExamMode() }
            )
        }
    }
}

@Composable
fun ExamBoardSelectionScreen(
    onExamBoardSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Select Your Exam Board",
            style = MaterialTheme.typography.h4,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        ExamBoardButton("AQA", onExamBoardSelected)
        ExamBoardButton("OCR", onExamBoardSelected)
        ExamBoardButton("Edexcel", onExamBoardSelected)
    }
}

@Composable
fun ExamBoardButton(
    board: String,
    onExamBoardSelected: (String) -> Unit
) {
    Button(
        onClick = { onExamBoardSelected(board) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(text = board)
    }
}

@Composable
fun FlashcardScreen(
    flashcard: com.lawrevision.data.models.Flashcard?,
    isAnswerVisible: Boolean,
    onToggleAnswer: () -> Unit,
    onMarkAnswer: (Boolean) -> Unit,
    onToggleExamMode: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top bar with exam mode toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = flashcard?.topic ?: "",
                style = MaterialTheme.typography.h6
            )
            Switch(
                checked = false,
                onCheckedChange = { onToggleExamMode() },
                modifier = Modifier.padding(8.dp)
            )
        }

        // Flashcard content
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp),
            elevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                if (isAnswerVisible) {
                    Text(
                        text = flashcard?.answer ?: "",
                        style = MaterialTheme.typography.body1
                    )
                } else {
                    Text(
                        text = flashcard?.question ?: "",
                        style = MaterialTheme.typography.body1
                    )
                }
            }
        }

        // Bottom buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = onToggleAnswer,
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            ) {
                Text(text = if (isAnswerVisible) "Show Question" else "Show Answer")
            }
            
            if (isAnswerVisible) {
                Button(
                    onClick = { onMarkAnswer(false) },
                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.error
                    )
                ) {
                    Text("Got it wrong")
                }
                
                Button(
                    onClick = { onMarkAnswer(true) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.primary
                    )
                ) {
                    Text("Got it right")
                }
            }
        }
    }
} 