package com.teessideUni.cfs_tracker.presentation.ui.reportpage.heartRateReportVM

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teessideUni.cfs_tracker.data.model.AverageHeartRateData
import com.teessideUni.cfs_tracker.data.model.HeartRateData
import com.teessideUni.cfs_tracker.domain.model.Resource
import com.teessideUni.cfs_tracker.domain.repository.HeartRateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.text.DecimalFormat
import java.time.Month
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HeartRateReportViewModel @Inject constructor(
    private val heartRateRepository: HeartRateRepository
) : ViewModel()
{
    private val calendar: Calendar = Calendar.getInstance()
    private val currentYear = calendar.get(Calendar.YEAR)
    private val currentWeekNumber = calendar.get(Calendar.WEEK_OF_YEAR)

    private val heartRateDataList = mutableStateListOf<HeartRateData>()
    private val heartRateDataListForAMonth = mutableStateListOf<HeartRateData>()
    private val averageHeartRateData = mutableListOf<AverageHeartRateData>()

    init {
        getHeartRateData(currentYear, currentWeekNumber).onEach { resource ->
            when (resource) {
                is Resource.Success -> {
                    heartRateDataList.clear()
                    resource.data?.let { heartRateDataList.addAll(it) }
                }
                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    fun getHeartRateDataList(): SnapshotStateList<HeartRateData> {
        return heartRateDataList
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

    private fun getHeartRateDataForAMonth(year: Int, month: Int): Flow<Resource<ArrayList<HeartRateData>>> {
        return heartRateRepository.getHeartRateDataForMonth(year, month)
            .map { result ->
                when (result) {
                    is Resource.Success -> {
                        heartRateDataListForAMonth.clear()
                        result.data?.let {
                            heartRateDataListForAMonth.addAll(it)
                        }
                        var arrayList: ArrayList<HeartRateData> = ArrayList()
                        heartRateDataListForAMonth.forEach {
                            arrayList.add(it)
                        }
                        Resource.Success(arrayList)
                    }
                    is Resource.Error -> Resource.Error(result.message.toString())
                    is Resource.Loading -> Resource.Loading()
                    null -> TODO()
                }
            }
            .catch { e ->
                emit(Resource.Error(e.message.toString()))
            }
    }

    private val firstMonthData = calendar.get(Calendar.MONTH)+2

    suspend fun getAverageHeartRateDataForSixMonths(): List<AverageHeartRateData> {
        // clear the list
        averageHeartRateData.clear()

        val monthData = mutableListOf<Int>()
        val yearData = mutableListOf<Int>()

        val currentMonth = firstMonthData
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        for (i in 0 until 6) {
            val monthOffset = (i + 1)
            val currentMonthData = if (currentMonth - monthOffset <= 0) 12 + currentMonth - monthOffset else currentMonth - monthOffset
            val currentYearData = if (currentMonth - monthOffset <= 0) currentYear - 1 else currentYear

            monthData.add(currentMonthData)
            yearData.add(currentYearData)
        }

        for (i in 5 downTo 0) {
            val result = getHeartRateDataForAMonth(yearData[i], monthData[i]).first()

            val monthDataResult = if (result is Resource.Success) {
                result.data ?: arrayListOf()
            } else {
                arrayListOf()
            }

            val monthDataAverage = monthDataResult.map { it.heartRate }.average()
            averageHeartRateData.add(AverageHeartRateData(monthDataAverage, Month.of(monthData[i]).name.substring(0, 3)))
        }

        return averageHeartRateData
    }

    fun calculateAverageHeartRateChangePercentage(averageHeartRateData: List<AverageHeartRateData>): String {

        if (averageHeartRateData.isEmpty()) {
            return "0.00%" // No data available
        }
        val latestAverageHeartRate = averageHeartRateData.last().averageHeartRate
        val previousAverageHeartRate = averageHeartRateData[averageHeartRateData.size - 2].averageHeartRate

        val changePercentage = if (previousAverageHeartRate != 0.0) {
            ((latestAverageHeartRate - previousAverageHeartRate) / previousAverageHeartRate * 100)
        } else {
            0.0
        }

        val decimalFormat = DecimalFormat("0.00")
        return if ("${changePercentage}%" == "NaN%") {
            "0.00%"
        } else {
            "${decimalFormat.format(changePercentage)}%"
        }
    }

    fun isAverageHeartRateIncreased(averageHeartRateData: List<AverageHeartRateData>): Boolean
    {
        if (averageHeartRateData.isEmpty()) {
            return false // No data available
        }
        val latestAverageHeartRate = averageHeartRateData.last().averageHeartRate

        if (averageHeartRateData.size > 1) {
            val previousAverageHeartRate = averageHeartRateData[averageHeartRateData.size - 2].averageHeartRate

            if (latestAverageHeartRate > previousAverageHeartRate) {
                return true
            }
        }
        return false
    }
}