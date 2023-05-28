package com.teessideUni.cfs_tracker.presentation.ui.RespiratoryRate

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teessideUni.cfs_tracker.domain.use_case.RecordRespiratoryRateUseCase
import kotlinx.coroutines.launch

class RespiratoryRateViewModel(private val recordRespiratoryRateUseCase: RecordRespiratoryRateUseCase) : ViewModel() {
    private var RespiRatoryRate: Float = 0f
    private val timer = object : CountDownTimer(30000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            // Update the timer UI
        }

        override fun onFinish() {
            // Update the UI with the sum of sensor data
            Log.d("Final Sum", getSensorData().toString());
        }
    }

    fun startRecordingSensorData() {
        viewModelScope.launch {
            recordRespiratoryRateUseCase.startRecording()
        }
        timer.start()
    }

    fun getSensorData(): Float {
        return recordRespiratoryRateUseCase.getRespiratoryRate()
    }
}
