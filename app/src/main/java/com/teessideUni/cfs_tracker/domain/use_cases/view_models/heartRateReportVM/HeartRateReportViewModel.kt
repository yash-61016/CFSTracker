package com.teessideUni.cfs_tracker.domain.use_cases.view_models.heartRateReportVM

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teessideUni.cfs_tracker.data.local.HeartRateData
import com.teessideUni.cfs_tracker.domain.model.Resource
import com.teessideUni.cfs_tracker.domain.repository.AuthRepository
import com.teessideUni.cfs_tracker.domain.repository.HeartRateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HeartRateReportViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val heartRateRepository: HeartRateRepository
) : ViewModel()
{
    private val calendar: Calendar = Calendar.getInstance()
    private val currentYear = calendar.get(Calendar.YEAR)
    private val currentWeekNumber = calendar.get(Calendar.WEEK_OF_YEAR)
    private val heartRateDataList = mutableStateListOf<HeartRateData>()

    init {
        getHeartRateData(currentYear, currentWeekNumber).onEach { resource ->
            when (resource) {
                is Resource.Success -> {
                    heartRateDataList.clear()
                    resource.data?.let { heartRateDataList.addAll(it) }
                }
                is Resource.Error -> {
                    // handle error
                }
                is Resource.Loading -> {
                    // handle loading state
                }
            }
        }.launchIn(viewModelScope)
    }

    fun getHeartRateDataList(): SnapshotStateList<HeartRateData> {
        return heartRateDataList
    }

    suspend fun getCurrentUserName(): String {
        val currentUser = repository.getCurrentUserName()
        return if (currentUser != null) {
            currentUser ?: ""
        } else {
            // Handle the case where currentUser is null
            ""
        }
    }

    fun getHeartRateData(year: Int, weekNumber: Int): Flow<Resource<ArrayList<HeartRateData>>> {
        return heartRateRepository.getHeartRateDataForWeek(year, weekNumber)
            .map { result ->
                when (result) {
                    is Resource.Success -> {
                        heartRateDataList.clear()
                        result.data?.let { heartRateDataList.addAll(it) }
                        var arrayList: ArrayList<HeartRateData> = ArrayList()
                        heartRateDataList.forEach { arrayList.add(it) }
                        Resource.Success(arrayList)
                    }
                    is Resource.Error -> Resource.Error(result.message.toString())
                    is Resource.Loading -> Resource.Loading()
                }
            }
            .catch { e ->
                emit(Resource.Error(e.message.toString()))
            }
    }
}