package com.teessideUni.cfs_tracker.presentation.screens.heart_rate

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class MainHandlerViewModel : ViewModel() {
    // Define State variables
    private val _pulseText = mutableStateOf("")
    private val _messageText = mutableStateOf("")
    private val _realTimeText =  mutableStateOf(0)

    // Expose immutable LiveData for composable functions to observe
    val pulseText: State<String> = _pulseText
    val messageText: State<String> = _messageText
    val realTimeText: State<Int> = _realTimeText

    // Call this function to update the realtime text
    fun updatePulseText(text: String) {
        _pulseText.value = text
    }

    // Call this function to update the realtime text
    fun updateMessageText(text: String) {
        _messageText.value = text
    }

    // Call this function to update the final text
    fun updateRealTimeText(value: Int) {
        _realTimeText.value = value
    }
}