package com.example.flashcard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcard.ui.theme.FlashcardTheme
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    private val flashcardViewModel: FlashcardViewModel by viewModels()
    private val timerViewModel: TimerModel by viewModels()

    // 1. New enum to represent different screens.
    sealed class StudyBuddyScreen {
        object Home : StudyBuddyScreen()
        object Flashcard : StudyBuddyScreen()
        object Timer : StudyBuddyScreen()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlashcardTheme {
                // 2. State to track the current screen.
                var currentScreen by remember { mutableStateOf<StudyBuddyScreen>(StudyBuddyScreen.Home) }

                // 3. Conditional composable based on the current screen.
                when (currentScreen) {
                    is StudyBuddyScreen.Home -> HomeScreen(onScreenSelected = { currentScreen = it })
                    is StudyBuddyScreen.Flashcard -> FlashcardScreen(flashcardViewModel, onBack = { currentScreen = StudyBuddyScreen.Home })
                    is StudyBuddyScreen.Timer -> TimerScreen(timerViewModel, onBack = { currentScreen = StudyBuddyScreen.Home })
                }
            }
        }
    }
}

// Composable for the home screen with navigation options.
@Composable
fun HomeScreen(onScreenSelected: (MainActivity.StudyBuddyScreen) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Welcome to Study Buddy", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { onScreenSelected(MainActivity.StudyBuddyScreen.Flashcard) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Flashcard")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onScreenSelected(MainActivity.StudyBuddyScreen.Timer) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Timer")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardScreen(viewModel: FlashcardViewModel, onBack: () -> Unit) {

    // State to manage the visibility of the "add new card" form.
    var isAddingCard by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Flashcards") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(viewModel: TimerModel, onBack: () -> Unit) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Timer") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // This composable acts as a controller, switching between screens based on the ViewModel's state.
            when (viewModel.currentTimerState) {
                is TimerState.SetTime -> SetTimerScreen(viewModel)
                is TimerState.Countdown -> CountdownScreen(viewModel)
            }
        }
    }
}

@Composable
fun SetTimerScreen(viewModel: TimerModel) {
    var minutes by remember { mutableStateOf("25") }
    var seconds by remember { mutableStateOf("00") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Set Study Time", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = minutes,
                onValueChange = { minutes = it },
                label = { Text("Minutes") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.width(16.dp))
            OutlinedTextField(
                value = seconds,
                onValueChange = { seconds = it },
                label = { Text("Seconds") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                val min = minutes.toIntOrNull() ?: 0
                val sec = seconds.toIntOrNull() ?: 0
                viewModel.setTime(min, sec)
                viewModel.startTimer()
            },
            enabled = (minutes.toIntOrNull() ?: 0) + (seconds.toIntOrNull() ?: 0) > 0,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Start Timer")
        }
    }
}

@Composable
fun CountdownScreen(viewModel: TimerModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = viewModel.getFormattedTime(),
            fontSize = 72.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(16.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { viewModel.resetTimer() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reset Timer")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FlashcardScreenPreview() {
    FlashcardTheme {
        FlashcardScreen(viewModel = FlashcardViewModel(), onBack = {})
    }
}
