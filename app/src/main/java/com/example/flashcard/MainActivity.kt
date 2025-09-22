package com.example.flashcard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import kotlin.random.Random

// 1. Data Class for a Flashcard
// This data class will represent a single flashcard.
data class Flashcard(val question: String, val answer: String)

// 2. Class to manage flashcard data and review sessions.
// This is the core logic, adapted for use with Compose.
class FlashcardApp {

    // A list to store our flashcards.
    private val flashcards = mutableListOf<Flashcard>()

    init {
        // Add some sample flashcards upon initialization.
        addFlashcard("What is the capital of France?", "Paris")
        addFlashcard("What is the main function of the heart?", "To pump blood throughout the body.")
        addFlashcard("What does 'val' mean in Kotlin?", "It declares a read-only (immutable) variable.")
        addFlashcard("What is the largest planet in our solar system?", "Jupiter")
    }

    // Adds a new flashcard to the list.
    fun addFlashcard(question: String, answer: String) {
        val newFlashcard = Flashcard(question, answer)
        flashcards.add(newFlashcard)
    }

    // Returns a shuffled list of all flashcards for a review session.
    fun getShuffledFlashcards(): List<Flashcard> {
        return flashcards.shuffled(Random)
    }
}

// 3. The main activity for your app, using Compose.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlashcardTheme {
                FlashcardScreen()
            }
        }
    }
}

// 4. Composable function to display the flashcard UI.
@Composable
fun FlashcardScreen() {
    // Initialize the FlashcardApp instance.
    val flashcardApp = remember { FlashcardApp() }

    // State to hold our current list of cards and the current card index.
    var flashcards by remember { mutableStateOf(flashcardApp.getShuffledFlashcards()) }
    var currentCardIndex by remember { mutableStateOf(0) }

    // State to toggle the visibility of the answer.
    var isAnswerVisible by remember { mutableStateOf(false) }

    // A check to handle the case where there are no flashcards.
    if (flashcards.isEmpty()) {
        Text("No flashcards to review. Please add some.")
        return
    }

    val currentCard = flashcards[currentCardIndex]

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Display the flashcard question
            Text(
                text = currentCard.question,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Display the answer, which is only visible when the button is clicked.
            if (isAnswerVisible) {
                Text(
                    text = currentCard.answer,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Button to show/hide the answer.
            Button(
                onClick = { isAnswerVisible = !isAnswerVisible },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isAnswerVisible) "Hide Answer" else "Show Answer")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Button to move to the next flashcard.
            Button(
                onClick = {
                    isAnswerVisible = false // Hide answer for the new card
                    currentCardIndex = (currentCardIndex + 1) % flashcards.size // Cycle through cards
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Next Card")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FlashcardScreenPreview() {
    FlashcardTheme {
        FlashcardScreen()
    }
}
