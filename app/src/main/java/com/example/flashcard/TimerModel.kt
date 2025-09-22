package com.example.flashcard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.filled.Refresh

// Enum to manage the state of the timer
enum class TimerState {
    SETTING,
    RUNNING,
    PAUSED,
    FINISHED
}

// ViewModel to handle timer logic
class TimerModel : ViewModel() {
    // A mutable state variable to hold the current timer state
    var timerState by mutableStateOf(TimerState.SETTING)

    // State for user input in minutes
    var minutesInput by mutableStateOf("")

    // State for user input in seconds
    var secondsInput by mutableStateOf("")

    // State for the remaining time in milliseconds
    var remainingTimeMillis by mutableStateOf(0L)

    // Job for the countdown coroutine
    private var countdownJob: Job? = null

    // Function to start the timer
    fun startTimer() {
        val totalMillis = try {
            (minutesInput.toLong() * 60 * 1000) + (secondsInput.toLong() * 1000)
        } catch (e: NumberFormatException) {
            0L // Handle invalid input gracefully
        }

        if (totalMillis > 0) {
            remainingTimeMillis = totalMillis
            timerState = TimerState.RUNNING
            startCountdown()
        }
    }

    // Function to pause the timer
    fun pauseTimer() {
        countdownJob?.cancel()
        timerState = TimerState.PAUSED
    }

    // Function to resume the timer
    fun resumeTimer() {
        timerState = TimerState.RUNNING
        startCountdown()
    }

    // Function to reset the timer
    fun resetTimer() {
        countdownJob?.cancel()
        timerState = TimerState.SETTING
        minutesInput = ""
        secondsInput = ""
        remainingTimeMillis = 0L
    }

    // Coroutine to handle the countdown
    private fun startCountdown() {
        countdownJob = viewModelScope.launch {
            while (remainingTimeMillis > 0 && timerState == TimerState.RUNNING) {
                delay(1000)
                remainingTimeMillis -= 1000
            }
            if (remainingTimeMillis <= 0) {
                timerState = TimerState.FINISHED
                // Logic for when the timer finishes
            }
        }
    }
}

// Composable function for the timer's UI.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(viewModel: TimerModel, onBack: () -> Unit) {
    Scaffold(
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (viewModel.timerState) {
                TimerState.SETTING -> SetTimerScreen(viewModel)
                TimerState.RUNNING, TimerState.PAUSED -> CountdownScreen(viewModel)
                TimerState.FINISHED -> FinishedScreen(viewModel)
            }
        }
    }
}

// Composable function for setting the timer.
@Composable
fun SetTimerScreen(viewModel: TimerModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Set Timer", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = viewModel.minutesInput,
                onValueChange = { viewModel.minutesInput = it },
                label = { Text("Minutes") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = viewModel.secondsInput,
                onValueChange = { viewModel.secondsInput = it },
                label = { Text("Seconds") },
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { viewModel.startTimer() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Start")
        }
    }
}

// Composable function for the countdown view.
@Composable
fun CountdownScreen(viewModel: TimerModel) {
    val minutes = viewModel.remainingTimeMillis / 1000 / 60
    val seconds = viewModel.remainingTimeMillis / 1000 % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(formattedTime, fontSize = 48.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val icon: ImageVector
            val description: String
            if (viewModel.timerState == TimerState.PAUSED) {
                icon = Icons.Default.PlayArrow
                description = "Resume"
            } else {
                icon = Icons.Default.Pause
                description = "Pause"
            }

            IconButton(onClick = {
                if (viewModel.timerState == TimerState.PAUSED) {
                    viewModel.resumeTimer()
                } else {
                    viewModel.pauseTimer()
                }
            }) {
                Icon(
                    imageVector = icon,
                    contentDescription = description,
                    modifier = Modifier.size(48.dp)
                )
            }

            IconButton(onClick = { viewModel.resetTimer() }) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset",
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }
}

// Composable function for when the timer has finished.
@Composable
fun FinishedScreen(viewModel: TimerModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Timer Finished!", fontSize = 32.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { viewModel.resetTimer() }) {
            Text("Set New Timer")
        }
    }
}
