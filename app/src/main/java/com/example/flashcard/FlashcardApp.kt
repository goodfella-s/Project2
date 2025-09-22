// FlashcardApp.kt
// A simple command-line application demonstrating the core flashcard feature.
// This code is compatible with Kotlin version 1.9.0.

import kotlin.random.Random

// 1. Data Class for a Flashcard
// A simple data class to hold the question and answer for each flashcard.
data class Flashcard(val question: String, val answer: String)

// 2. Main logic for the Flashcard App
class FlashcardApp {

    // A list to store our flashcards. Since there's no online connectivity, we use an in-memory list.
    private val flashcards = mutableListOf<Flashcard>()

    // Adds a new flashcard to our list.
    fun addFlashcard(question: String, answer: String) {
        val newFlashcard = Flashcard(question, answer)
        flashcards.add(newFlashcard)
        println("Flashcard added: \"$question\"")
    }

    // Starts a review session, shuffling the cards and presenting them one by one.
    fun startReviewSession() {
        if (flashcards.isEmpty()) {
            println("No flashcards to review. Please add some first.")
            return
        }

        // Shuffle the flashcards for a randomized revision.
        val shuffledCards = flashcards.shuffled(Random)
        println("Starting review session with ${shuffledCards.size} cards.")
        println("--------------------------------------------------")

        // Iterate through the shuffled cards and quiz the user.
        for ((index, card) in shuffledCards.withIndex()) {
            println("Card ${index + 1}/${shuffledCards.size}:")
            println("Question: ${card.question}")
            print("Press Enter to see the answer...")
            readLine() // Wait for user input to reveal the answer.
            println("Answer: ${card.answer}\n")
        }

        println("--------------------------------------------------")
        println("Review session complete! You've gone through all the cards.")
    }

    // Prints all the flashcards currently in the list.
    fun printAllFlashcards() {
        if (flashcards.isEmpty()) {
            println("No flashcards to display.")
            return
        }
        println("Current Flashcards:")
        flashcards.forEachIndexed { index, card ->
            println("${index + 1}. Q: ${card.question} | A: ${card.answer}")
        }
    }
}

// 3. Main function to run the application
fun main() {
    val app = FlashcardApp()

    // Add some sample flashcards. In a real app, this data would come from a file or user input.
    app.addFlashcard("What is the capital of France?", "Paris")
    app.addFlashcard("What is the main function of the heart?", "To pump blood throughout the body.")
    app.addFlashcard("What does 'val' mean in Kotlin?", "It declares a read-only (immutable) variable.")
    app.addFlashcard("What is the largest planet in our solar system?", "Jupiter")

    println("\nWelcome to Study Buddy! You can now review your flashcards.")
    println("--------------------------------------------------")

    // Start a review session to demonstrate the core feature.
    app.startReviewSession()

    println("\nHere are all the flashcards you have in this session:")
    app.printAllFlashcards()
}
