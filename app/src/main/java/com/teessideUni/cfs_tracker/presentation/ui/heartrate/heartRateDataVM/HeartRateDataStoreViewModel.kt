package com.teessideUni.cfs_tracker.presentation.ui.heartrate.heartRateDataVM

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teessideUni.cfs_tracker.domain.model.Resource
import com.teessideUni.cfs_tracker.domain.repository.HeartRateRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.util.Date

class HeartRateDataStoreViewModel (
    private val repository: HeartRateRepository
) : ViewModel()
{
    private val _storeState = Channel<HeartRateDataState>()

    fun storeHeartRate(bpm:Double, timestamp: Date) = viewModelScope.launch {
        repository.storeHeartRateData(bpm, timestamp).collect{
                result ->
            when(result){
                is Resource.Success -> {
                    _storeState.send(HeartRateDataState(isSuccess = "Data get successfully stored."))
                }
                is Resource.Error -> {
                    _storeState.send(HeartRateDataState(isError = result.message.toString()))
                }
                is Resource.Loading -> {
                    _storeState.send(HeartRateDataState(isLoading = true))
                }
            }
        }
    }
}