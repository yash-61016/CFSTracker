package com.teessideUni.cfs_tracker.domain.use_cases.view_models.homePageVM

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teessideUni.cfs_tracker.data.local.HeartRateData
import com.teessideUni.cfs_tracker.data.local.HeartRatePoint
import com.teessideUni.cfs_tracker.domain.model.Resource
import com.teessideUni.cfs_tracker.domain.repository.AuthRepository
import com.teessideUni.cfs_tracker.domain.repository.HeartRateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val heartRateRepository: HeartRateRepository
) : ViewModel()
{
    // current week
    private val calendar: Calendar = Calendar.getInstance()
    private val currentYear = calendar.get(Calendar.YEAR)
    private val currentWeekNumber = calendar.get(Calendar.WEEK_OF_YEAR)

    // previous week
    private val previousWeekNumber = if (currentWeekNumber > 1) {
        currentWeekNumber - 1
    } else {
        // If current week is the first week of the year, set previous week as the last week of the previous year
        val previousYear = currentYear - 1
        val previousYearWeeks = calendar.getWeeksInYear(previousYear)
        previousYearWeeks
    }

    private val heartRateDataList = mutableStateListOf<HeartRateData>()
    private val heartRateDataListPreviousWeek = mutableStateListOf<HeartRateData>()

    fun logout() = viewModelScope.launch {
        repository.signOut();
    }

    fun getCurrentWeekData(): Flow<Resource<ArrayList<HeartRateData>>> {
        return heartRateRepository.getHeartRateDataForWeek(currentYear, currentWeekNumber)
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

    fun getPreviousWeekData(): Flow<Resource<ArrayList<HeartRateData>>> {
        return heartRateRepository.getHeartRateDataForWeek(currentYear, previousWeekNumber)
            .map { result ->
                when (result) {
                    is Resource.Success -> {
                        heartRateDataListPreviousWeek.clear()
                        result.data?.let { heartRateDataListPreviousWeek.addAll(it) }
                        var arrayList: ArrayList<HeartRateData> = ArrayList()
                        heartRateDataListPreviousWeek.forEach { arrayList.add(it) }
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

    private fun Calendar.getWeeksInYear(year: Int): Int {
        val calendar = this.clone() as Calendar
        calendar.clear()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, Calendar.DECEMBER)
        calendar.set(Calendar.DAY_OF_MONTH, 31)
        return calendar.get(Calendar.WEEK_OF_YEAR)
    }

    fun filterMaxMinHeartRatePerDay(heartRateDataList: List<HeartRateData>): List<List<HeartRatePoint>> {
        val sortedDataList = heartRateDataList.sortedBy { it.timestamp }
        val dailyHeartRateList = mutableListOf<List<HeartRatePoint>>()
        val dateFormat = SimpleDateFormat("EEE", Locale.US)

        if (sortedDataList.isNotEmpty()) {
            val startOfWeek = sortedDataList.first().timestamp
            val endOfWeek = sortedDataList.last().timestamp

            val calendar = Calendar.getInstance()
            calendar.time = startOfWeek

            while (calendar.time <= endOfWeek) {
                val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
                val dataForCurrentDay = sortedDataList.filter {
                    val calendarItem = Calendar.getInstance()
                    calendarItem.time = it.timestamp
                    calendarItem.get(Calendar.DAY_OF_MONTH) == currentDay
                }
                if (dataForCurrentDay.isNotEmpty()) {
                    val dayOfWeek = dateFormat.format(dataForCurrentDay.first().timestamp)
                    val maxHeartRate = dataForCurrentDay.maxByOrNull { it.heartRate }!!.heartRate
                    val minHeartRate = dataForCurrentDay.minByOrNull { it.heartRate }!!.heartRate
                    dailyHeartRateList.add(listOf(HeartRatePoint(dayOfWeek, maxHeartRate, minHeartRate)))
                }
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        return dailyHeartRateList
    }

}

