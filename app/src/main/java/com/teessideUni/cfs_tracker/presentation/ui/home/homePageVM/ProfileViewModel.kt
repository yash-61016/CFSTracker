package com.teessideUni.cfs_tracker.presentation.ui.home.homePageVM

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.teessideUni.cfs_tracker.data.model.HeartRateData
import com.teessideUni.cfs_tracker.data.model.HeartRatePoint
import com.teessideUni.cfs_tracker.data.model.RespiratoryRateDataValues
import com.teessideUni.cfs_tracker.data.model.RespiratoryRatePoint
import com.teessideUni.cfs_tracker.domain.model.Resource
import com.teessideUni.cfs_tracker.domain.repository.HeartRateRepository
import com.teessideUni.cfs_tracker.domain.repository.RespiratoryRateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
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
        val allWeekDays = setOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

        if (sortedDataList.isNotEmpty()) {
            val startOfWeek = getStartOfWeek(sortedDataList.first().timestamp)
            val endOfWeek = getEndOfWeek(sortedDataList.last().timestamp)

            calendar.time = startOfWeek

            while (calendar.time <= endOfWeek) {
                val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
                val dataForCurrentDay = sortedDataList.filter { data ->
                    val dataCalendar = Calendar.getInstance()
                    dataCalendar.time = data.timestamp
                    dataCalendar.get(Calendar.DAY_OF_MONTH) == currentDay
                }
                val dayOfWeek = dateFormat.format(calendar.time)

                val heartRatePoint = if (dataForCurrentDay.isNotEmpty()) {
                    val maxHeartRate = dataForCurrentDay.maxByOrNull { it.heartRate }!!.heartRate
                    val minHeartRate = dataForCurrentDay.minByOrNull { it.heartRate }!!.heartRate

                    HeartRatePoint(dayOfWeek, maxHeartRate, minHeartRate)
                } else {
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
                        respiratoryRateDataList.forEach {
                            arrayList.add(it)
                        }
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
                        respiratoryRateDataListPreviousWeek.forEach {
                            arrayList.add(it)
                        }
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
        val dailyRespiratoryRateList = mutableListOf<List<RespiratoryRatePoint>>()
        val dateFormat = SimpleDateFormat("EEE", Locale.US)

        val calendar = Calendar.getInstance()
        val allWeekDays = setOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

        if (sortedDataList.isNotEmpty()) {
            val startOfWeek = getStartOfWeek(sortedDataList.first().timestamp)
            val endOfWeek = getEndOfWeek(sortedDataList.last().timestamp)

            calendar.time = startOfWeek

            while (calendar.time <= endOfWeek) {
                val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
                val dataForCurrentDay = sortedDataList.filter { data ->
                    val dataCalendar = Calendar.getInstance()
                    dataCalendar.time = data.timestamp
                    dataCalendar.get(Calendar.DAY_OF_MONTH) == currentDay
                }
                val dayOfWeek = dateFormat.format(calendar.time)

                val respiratoryRatePoint = if (dataForCurrentDay.isNotEmpty()) {
                    val maxHeartRate = dataForCurrentDay.maxByOrNull { it.rateValue }!!.rateValue
                    val minHeartRate = dataForCurrentDay.minByOrNull { it.rateValue }!!.rateValue

                    RespiratoryRatePoint(dayOfWeek, maxHeartRate.toDouble(), minHeartRate.toDouble())
                } else {
                    RespiratoryRatePoint(dayOfWeek, 0.0, 0.0)
                }
                dailyRespiratoryRateList.add(listOf(respiratoryRatePoint))

                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }

            // Check for missing days and add HeartRatePoints with 0 values
            val existingDaysOfWeek = dailyRespiratoryRateList.map { it.first().dayOfWeek }.toSet()
            val missingDaysOfWeek = allWeekDays - existingDaysOfWeek

            missingDaysOfWeek.forEach { dayOfWeek ->
                dailyRespiratoryRateList.add(listOf(RespiratoryRatePoint(dayOfWeek, 0.0, 0.0)))
            }

            // Sort the dailyHeartRateList based on the desired order
            dailyRespiratoryRateList.sortBy { allWeekDays.indexOf(it.first().dayOfWeek) }
        } else {
            // If the input list is empty, add HeartRatePoints with 0 values for all weekdays
            allWeekDays.forEach { dayOfWeek ->
                dailyRespiratoryRateList.add(listOf(RespiratoryRatePoint(dayOfWeek, 0.0, 0.0)))
            }
        }
        return dailyRespiratoryRateList
    }

    private fun getStartOfWeek(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        resetTime(calendar)
        return calendar.time
    }

    private fun getEndOfWeek(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek + 6)
        setEndOfDay(calendar)
        return calendar.time
    }

    private fun resetTime(calendar: Calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
    }

    private fun setEndOfDay(calendar: Calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
    }
}

