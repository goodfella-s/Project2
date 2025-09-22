package com.example.flashcard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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


