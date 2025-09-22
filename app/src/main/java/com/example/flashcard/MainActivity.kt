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

// 1. Enum to manage navigation between screens
enum class StudyBuddyScreen {
    Home,
    Flashcard,
    Timer
}

class MainActivity : ComponentActivity() {
    private val flashcardViewModel: FlashcardViewModel by viewModels()
    private val timerModel: TimerModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlashcardTheme {
                // A state variable to keep track of the current screen.
                var currentScreen by remember { mutableStateOf(StudyBuddyScreen.Home) }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // A `when` expression to handle screen navigation.
                    when (currentScreen) {
                        StudyBuddyScreen.Home -> {
                            HomeScreen(
                                onFlashcardClick = { currentScreen = StudyBuddyScreen.Flashcard },
                                onTimerClick = { currentScreen = StudyBuddyScreen.Timer },
                                contentPadding = innerPadding
                            )
                        }
                        StudyBuddyScreen.Flashcard -> {
                            FlashcardScreen(
                                viewModel = flashcardViewModel,
                                onBack = { currentScreen = StudyBuddyScreen.Home }
                            )
                        }
                        StudyBuddyScreen.Timer -> {
                            TimerScreen(
                                viewModel = timerModel,
                                onBack = { currentScreen = StudyBuddyScreen.Home }
                            )
                        }
                    }
                }
            }
        }
    }
}

// 3. Composable function for the home screen.
@Composable
fun HomeScreen(onFlashcardClick: () -> Unit, onTimerClick: () -> Unit, contentPadding: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Study Buddy",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        Button(
            onClick = onFlashcardClick,
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Flashcards")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onTimerClick,
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Timer")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    FlashcardTheme {
        HomeScreen(onFlashcardClick = {}, onTimerClick = {}, contentPadding = PaddingValues())
    }
}
