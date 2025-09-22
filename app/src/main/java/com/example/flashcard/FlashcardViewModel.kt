package com.example.flashcard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlin.random.Random

// 1. Data Class for a Flashcard
// Represents a single flashcard.
data class Flashcard(val question: String, val answer: String)

// 2. ViewModel to manage flashcard data and state
class FlashcardViewModel : ViewModel() {

    // A list to store our flashcards. Using 'var' and 'mutableStateOf'
    // allows the UI to automatically recompose when this list changes.
    private var flashcards by mutableStateOf(getSampleFlashcards().shuffled(Random).toMutableList())

    // State to hold our current card index.
    // The UI will observe and react to changes in this value.
    var currentCardIndex by mutableStateOf(0)

    // State to toggle the visibility of the answer.
    var isAnswerVisible by mutableStateOf(false)

    // A check to handle the case where there are no flashcards.
    val hasFlashcards: Boolean
        get() = flashcards.isNotEmpty()

    // Get the current flashcard to display.
    val currentCard: Flashcard
        get() = flashcards[currentCardIndex]

    // Toggles the visibility of the answer.
    fun toggleAnswerVisibility() {
        isAnswerVisible = !isAnswerVisible
    }

    // Moves to the next flashcard.
    fun nextCard() {
        // Hide answer for the new card
        isAnswerVisible = false
        // Cycle through cards
        currentCardIndex = (currentCardIndex + 1) % flashcards.size
    }

    // **NEW FUNCTION**
    // Adds a new flashcard to the list and resets the deck.
    fun addFlashcard(question: String, answer: String) {
        val newFlashcard = Flashcard(question, answer)
        flashcards.add(newFlashcard)
        flashcards = flashcards.shuffled(Random).toMutableList() // Shuffle the deck after adding a new card
        currentCardIndex = 0 // Reset to the first card
    }

    // A private function to get sample flashcards. In a real app, this would
    // be replaced with logic to load from a database or file.
    private fun getSampleFlashcards(): List<Flashcard> {
        return listOf(
            Flashcard("What is the capital of France?", "Paris"),
            Flashcard("What is the main function of the heart?", "To pump blood throughout the body."),
            Flashcard("What does 'val' mean in Kotlin?", "It declares a read-only (immutable) variable."),
            Flashcard("What is the largest planet in our solar system?", "Jupiter")
        )
    }
}
