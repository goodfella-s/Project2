package com.example.flashcard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api

// An enum to represent the different states of the timer.
sealed class TimerState {
    object SetTime : TimerState()
    object Countdown : TimerState()
}

class TimerModel : ViewModel() {

    // State to hold the current screen state for the timer.
    var currentTimerState by mutableStateOf<TimerState>(TimerState.SetTime)
        private set

    // State for the countdown time in milliseconds.
    var remainingTimeMillis by mutableStateOf(0L)
        private set

    // A job to manage the coroutine for the countdown.
    private var countdownJob: Job? = null

    // Sets the initial time for the timer.
    fun setTime(minutes: Int, seconds: Int) {
        remainingTimeMillis = (minutes * 60 + seconds) * 1000L
    }

    // Starts the countdown timer.
    fun startTimer() {
        if (countdownJob?.isActive == true) return

        currentTimerState = TimerState.Countdown

        countdownJob = viewModelScope.launch {
            while (remainingTimeMillis > 0) {
                delay(1000L)
                remainingTimeMillis -= 1000L
            }
            // Timer has finished, go back to the SetTime screen.
            currentTimerState = TimerState.SetTime
        }
    }

    // Resets the timer and cancels the countdown job.
    fun resetTimer() {
        countdownJob?.cancel()
        currentTimerState = TimerState.SetTime
        remainingTimeMillis = 0L
    }

    // Helper function to format the remaining time into a readable string (MM:SS).
    fun getFormattedTime(): String {
        val totalSeconds = remainingTimeMillis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    // Ensure the countdown job is cancelled when the ViewModel is destroyed.
    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }
}

// The main composable for the Timer feature that controls the state.
// It is now a standalone function that takes the ViewModel as a parameter.
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
            when (viewModel.currentTimerState) {
                is TimerState.SetTime -> SetTimerScreen(viewModel)
                is TimerState.Countdown -> CountdownScreen(viewModel)
            }
        }
    }
}

// Composable for the screen where the user sets the timer.
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

// Composable for the countdown screen.
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
