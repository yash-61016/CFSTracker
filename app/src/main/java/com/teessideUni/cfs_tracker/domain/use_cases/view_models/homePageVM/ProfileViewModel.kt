package com.teessideUni.cfs_tracker.domain.use_cases.view_models.homePageVM

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teessideUni.cfs_tracker.data.local.HeartRateData
import com.teessideUni.cfs_tracker.data.local.HeartRatePoint
import com.teessideUni.cfs_tracker.data.local.RespiratoryRateDataValues
import com.teessideUni.cfs_tracker.data.local.RespiratoryRatePoint
import com.teessideUni.cfs_tracker.domain.model.Resource
import com.teessideUni.cfs_tracker.domain.repository.AuthRepository
import com.teessideUni.cfs_tracker.domain.repository.HeartRateRepository
import com.teessideUni.cfs_tracker.domain.repository.RespiratoryRateRepository
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
    private val heartRateRepository: HeartRateRepository,
    private val respiratoryRateRepository: RespiratoryRateRepository
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

    private val respiratoryRateDataList = mutableStateListOf<RespiratoryRateDataValues>()
    private val respiratoryRateDataListPreviousWeek = mutableStateListOf<RespiratoryRateDataValues>()

    fun getCurrentWeekNumber() : String {
        return "Week No.: $currentWeekNumber"
    }
    fun getPreviousWeekNumber() : String {
        return "Week No.: $previousWeekNumber"
    }

    private fun Calendar.getWeeksInYear(year: Int): Int {
        val calendar = this.clone() as Calendar
        calendar.clear()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, Calendar.DECEMBER)
        calendar.set(Calendar.DAY_OF_MONTH, 31)
        return calendar.get(Calendar.WEEK_OF_YEAR)
    }

    //Heart Rate data fetch
    fun getHeartRateCurrentWeekData(): Flow<Resource<ArrayList<HeartRateData>>> {
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

    fun getHeartRatePreviousWeekData(): Flow<Resource<ArrayList<HeartRateData>>> {
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

    fun filterMaxMinHeartRatePerDay(heartRateDataList: List<HeartRateData>): List<List<HeartRatePoint>> {
        val sortedDataList = heartRateDataList.sortedBy { it.timestamp }
        val dailyHeartRateList = mutableListOf<List<HeartRatePoint>>()
        val dateFormat = SimpleDateFormat("EEE", Locale.US)

        val calendar = Calendar.getInstance()

        // Create a set of all weekdays
        val allWeekDays = setOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", )

        if (sortedDataList.isNotEmpty()) {
            val startOfWeek = sortedDataList.first().timestamp
            val endOfWeek = sortedDataList.last().timestamp

            calendar.time = startOfWeek

            while (calendar.time <= endOfWeek) {
                val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
                val dataForCurrentDay = sortedDataList.filter {
                    val calendarItem = Calendar.getInstance()
                    calendarItem.time = it.timestamp
                    calendarItem.get(Calendar.DAY_OF_MONTH) == currentDay
                }
                val dayOfWeek = dateFormat.format(calendar.time)

                val heartRatePoint = if (dataForCurrentDay.isNotEmpty()) {
                    val maxHeartRate = dataForCurrentDay.maxByOrNull { it.heartRate }!!.heartRate
                    val minHeartRate = dataForCurrentDay.minByOrNull { it.heartRate }!!.heartRate
                    HeartRatePoint(dayOfWeek, maxHeartRate, minHeartRate)
                } else {
                    // HeartRatePoint with 0 values for max and min heart rate
                    HeartRatePoint(dayOfWeek, 0.0, 0.0)
                }
                dailyHeartRateList.add(listOf(heartRatePoint))

                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }

            // Check for missing days and add HeartRatePoints with 0 values
            val existingDaysOfWeek = dailyHeartRateList.map { it.first().dayOfWeek }.toSet()
            val missingDaysOfWeek = allWeekDays - existingDaysOfWeek

            missingDaysOfWeek.forEach { dayOfWeek ->
                dailyHeartRateList.add(listOf(HeartRatePoint(dayOfWeek, 0.0, 0.0)))
            }
            // Sort the dailyHeartRateList based on the desired order
            dailyHeartRateList.sortBy { allWeekDays.indexOf(it.first().dayOfWeek) }
        } else {
            // If the input list is empty, add HeartRatePoints with 0 values for all weekdays
            allWeekDays.forEach { dayOfWeek ->
                dailyHeartRateList.add(listOf(HeartRatePoint(dayOfWeek, 0.0, 0.0)))
            }
        }

        return dailyHeartRateList
    }


    //Respiratory Rate data fetch
    fun getRespiratoryRateCurrentWeekData(): Flow<Resource<ArrayList<RespiratoryRateDataValues>>> {
        return respiratoryRateRepository.getRespiratoryRateDataForWeek(currentYear, currentWeekNumber)
            .map { result ->
                when (result) {
                    is Resource.Success -> {
                        respiratoryRateDataList.clear()
                        result.data?.let { respiratoryRateDataList.addAll(it) }
                        var arrayList: ArrayList<RespiratoryRateDataValues> = ArrayList()
                        respiratoryRateDataList.forEach { arrayList.add(it) }
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

    fun getRespiratoryRatePreviousWeekData(): Flow<Resource<ArrayList<RespiratoryRateDataValues>>> {
        return respiratoryRateRepository.getRespiratoryRateDataForWeek(currentYear, previousWeekNumber)
            .map { result ->
                when (result) {
                    is Resource.Success -> {
                        respiratoryRateDataListPreviousWeek.clear()
                        result.data?.let { respiratoryRateDataListPreviousWeek.addAll(it) }
                        var arrayList: ArrayList<RespiratoryRateDataValues> = ArrayList()
                        respiratoryRateDataListPreviousWeek.forEach { arrayList.add(it) }
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

    fun filterMaxMinRespiratoryRatePerDay(respiratoryRateDataList: List<RespiratoryRateDataValues>): List<List<RespiratoryRatePoint>> {
        val sortedDataList = respiratoryRateDataList.sortedBy { it.timestamp }
        val dailyHeartRateList = mutableListOf<List<RespiratoryRatePoint>>()
        val dateFormat = SimpleDateFormat("EEE", Locale.US)

        val calendar = Calendar.getInstance()

        // Create a set of all weekdays
        val allWeekDays = setOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", )

        if (sortedDataList.isNotEmpty()) {
            val startOfWeek = sortedDataList.first().timestamp
            val endOfWeek = sortedDataList.last().timestamp

            calendar.time = startOfWeek

            while (calendar.time <= endOfWeek) {
                val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
                val dataForCurrentDay = sortedDataList.filter {
                    val calendarItem = Calendar.getInstance()
                    calendarItem.time = it.timestamp
                    calendarItem.get(Calendar.DAY_OF_MONTH) == currentDay
                }
                val dayOfWeek = dateFormat.format(calendar.time)

                val respiratoryRatePoint = if (dataForCurrentDay.isNotEmpty()) {
                    val maxRespiratoryRate = dataForCurrentDay.maxByOrNull { it.rateValue }!!.rateValue
                    val minRespiratoryRate = dataForCurrentDay.minByOrNull { it.rateValue }!!.rateValue
                    RespiratoryRatePoint(dayOfWeek, maxRespiratoryRate, minRespiratoryRate)
                } else {
                    // HeartRatePoint with 0 values for max and min heart rate
                    RespiratoryRatePoint(dayOfWeek, 0.0f, 0.0f)
                }
                dailyHeartRateList.add(listOf(respiratoryRatePoint))

                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }

            // Check for missing days and add HeartRatePoints with 0 values
            val existingDaysOfWeek = dailyHeartRateList.map { it.first().dayOfWeek }.toSet()
            val missingDaysOfWeek = allWeekDays - existingDaysOfWeek

            missingDaysOfWeek.forEach { dayOfWeek ->
                dailyHeartRateList.add(listOf(RespiratoryRatePoint(dayOfWeek, 0.0f, 0.0f)))
            }
            // Sort the dailyHeartRateList based on the desired order
            dailyHeartRateList.sortBy { allWeekDays.indexOf(it.first().dayOfWeek) }
        } else {
            // If the input list is empty, add HeartRatePoints with 0 values for all weekdays
            allWeekDays.forEach { dayOfWeek ->
                dailyHeartRateList.add(listOf(RespiratoryRatePoint(dayOfWeek, 0.0f, 0.0f)))
            }
        }

        return dailyHeartRateList
    }
}

