package com.teessideUni.cfs_tracker.presentation.ui.RespiratoryRate

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teessideUni.cfs_tracker.domain.model.Resource
import com.teessideUni.cfs_tracker.domain.repository.RespiratoryRateRepository
import com.teessideUni.cfs_tracker.domain.use_case.RecordRespiratoryRateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class RespiratoryRateViewModel @Inject constructor(
    private val recordRespiratoryRateUseCase: RecordRespiratoryRateUseCase,
    private val respiratoryRateRepository: RespiratoryRateRepository
) : ViewModel() {
    private val timer = object : CountDownTimer(30000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            // Update the timer UI
        }
        override fun onFinish() {
            // Update the UI with the sum of sensor data
            Log.d("Final Sum", getSensorData().toString());
        }
    }

    private val _storeState = Channel<RespiratoryRateDataStoreState>()

    private fun storeHeartRate(respiratoryRate:Float, timestamp: Date) = viewModelScope.launch {
        respiratoryRateRepository.storeRespiratoryRateData(respiratoryRate, timestamp).collect{
                result ->
            when(result){
                is Resource.Success -> {
                    _storeState.send(RespiratoryRateDataStoreState(isSuccess = "Data get successfully stored."))
                }
                is Resource.Error -> {
                    _storeState.send(RespiratoryRateDataStoreState(isError = result.message.toString()))
                }
                is Resource.Loading -> {
                    _storeState.send(RespiratoryRateDataStoreState(isLoading = true))
                }
            }
        }
    }

    fun startRecordingSensorData() {
        viewModelScope.launch {
            recordRespiratoryRateUseCase.startRecording { respiratoryRateData ->
                // Handle the received respiratoryRateData
                storeHeartRate(respiratoryRateData.rateValue, respiratoryRateData.timestamp)
            }
        }
        timer.start()
    }

    fun getSensorData(): Float {
        return recordRespiratoryRateUseCase.getRespiratoryRate()
    }
}
