
package com.example.flashcard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcard.ui.theme.FlashcardTheme

class MainActivity : ComponentActivity() {
    // The viewModels() delegate provides an instance of the ViewModel.
    // This instance will survive configuration changes (like screen rotation).
    private val viewModel: FlashcardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlashcardTheme {
                FlashcardScreen(viewModel)
            }
        }
    }
}

// 4. Composable function to display the flashcard UI.
// This function now takes the ViewModel as a parameter.
@Composable
fun FlashcardScreen(viewModel: FlashcardViewModel) {

    // State to manage the visibility of the "add new card" form.
    var isAddingCard by remember { mutableStateOf(false) }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Check if we are in "add card" mode.
            if (isAddingCard) {
                // If we are, display the input form.
                AddCardForm(
                    onAddCard = { question, answer ->
                        viewModel.addFlashcard(question, answer)
                        isAddingCard = false // Hide the form after adding
                    },
                    onCancel = { isAddingCard = false }
                )
            } else {
                // Otherwise, display the flashcard review UI.
                FlashcardReviewUI(viewModel)

                Spacer(modifier = Modifier.height(16.dp))

                // Button to switch to the "add card" mode.
                Button(
                    onClick = { isAddingCard = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add New Card")
                }
            }
        }
    }
}

@Composable
fun FlashcardReviewUI(viewModel: FlashcardViewModel) {
    // A check to handle the case where there are no flashcards.
    if (!viewModel.hasFlashcards) {
        Text("No flashcards to review. Please add some.")
        return
    }

    // Display the flashcard question
    Text(
        text = viewModel.currentCard.question,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 16.dp)
    )

    // Display the answer, which is only visible when the button is clicked.
    if (viewModel.isAnswerVisible) {
        Text(
            text = viewModel.currentCard.answer,
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )
    } else {
        Spacer(modifier = Modifier.height(16.dp))
    }

    // Button to show/hide the answer.
    Button(
        onClick = { viewModel.toggleAnswerVisibility() },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(if (viewModel.isAnswerVisible) "Hide Answer" else "Show Answer")
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Button to move to the next flashcard.
    Button(
        onClick = { viewModel.nextCard() },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Next Card")
    }
}

@Composable
fun AddCardForm(onAddCard: (String, String) -> Unit, onCancel: () -> Unit) {
    var questionText by remember { mutableStateOf("") }
    var answerText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Add a New Flashcard", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = questionText,
            onValueChange = { questionText = it },
            label = { Text("Question") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = answerText,
            onValueChange = { answerText = it },
            label = { Text("Answer") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { onAddCard(questionText, answerText) },
                enabled = questionText.isNotBlank() && answerText.isNotBlank()
            ) {
                Text("Save Card")
            }
            Button(
                onClick = onCancel
            ) {
                Text("Cancel")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FlashcardScreenPreview() {
    FlashcardTheme {
        FlashcardScreen(viewModel = FlashcardViewModel())
    }
}
