package com.teessideUni.cfs_tracker.presentation.ui.reportpage.RespiratoryRateReportVM

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teessideUni.cfs_tracker.data.model.AverageRespiratoryRateData
import com.teessideUni.cfs_tracker.data.model.RespiratoryRateDataValues
import com.teessideUni.cfs_tracker.domain.model.Resource
import com.teessideUni.cfs_tracker.domain.repository.RespiratoryRateRepository
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
class RespiratoryRateReportViewModel @Inject constructor(
    private val respiratoryRateRepository: RespiratoryRateRepository
) : ViewModel()
{
    private val calendar: Calendar = Calendar.getInstance()
    private val currentYear = calendar.get(Calendar.YEAR)
    private val currentWeekNumber = calendar.get(Calendar.WEEK_OF_YEAR)

    private val respiratoryRateDataList = mutableStateListOf<RespiratoryRateDataValues>()
    private val respiratoryRateDataListForAMonth = mutableStateListOf<RespiratoryRateDataValues>()
    private val averageRespiratoryRateData = mutableListOf<AverageRespiratoryRateData>()

    init {
        getRespiratoryRateData(currentYear, currentWeekNumber).onEach { resource ->
            when (resource) {
                is Resource.Success -> {
                    respiratoryRateDataList.clear()
                    resource.data?.let { respiratoryRateDataList.addAll(it) }
                }
                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    fun getRespiratoryRateDataList(): SnapshotStateList<RespiratoryRateDataValues> {
        return respiratoryRateDataList
    }

    fun getRespiratoryRateData(year: Int, weekNumber: Int): Flow<Resource<ArrayList<RespiratoryRateDataValues>>> {
        return respiratoryRateRepository.getRespiratoryRateDataForWeek(year, weekNumber)
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

    private fun getRespiratoryRateDataForAMonth(year: Int, month: Int): Flow<Resource<ArrayList<RespiratoryRateDataValues>>> {
        return respiratoryRateRepository.getRespiratoryRateDataForMonth(year, month)
            .map { result ->
                when (result) {
                    is Resource.Success -> {
                        respiratoryRateDataListForAMonth.clear()
                        result.data?.let {
                            respiratoryRateDataListForAMonth.addAll(it)
                        }
                        var arrayList: ArrayList<RespiratoryRateDataValues> = ArrayList()
                        respiratoryRateDataListForAMonth.forEach {
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

    suspend fun getAverageRespiratoryRateDataForSixMonths(): List<AverageRespiratoryRateData> {
        // clear the list
        averageRespiratoryRateData.clear()

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
            val result = getRespiratoryRateDataForAMonth(yearData[i], monthData[i]).first()

            val monthDataResult = if (result is Resource.Success) {
                result.data ?: arrayListOf()
            } else {
                arrayListOf()
            }

            val monthDataAverage = monthDataResult.map { it.rateValue }.average()
            averageRespiratoryRateData.add(AverageRespiratoryRateData(monthDataAverage, Month.of(monthData[i]).name.substring(0, 3)))
        }

        return averageRespiratoryRateData
    }


    fun calculateAverageRespiratoryRateChangePercentage(averageRespiratoryRateData: List<AverageRespiratoryRateData>): String {

        if (averageRespiratoryRateData.isEmpty()) {
            return "0.00%" // No data available
        }
        val latestAverageRespiratoryRate = averageRespiratoryRateData.last().averageRespiratoryRate
        val previousAverageRespiratoryRate = averageRespiratoryRateData[averageRespiratoryRateData.size - 2].averageRespiratoryRate

        val changePercentage = if (previousAverageRespiratoryRate != 0.0) {
            ((latestAverageRespiratoryRate - previousAverageRespiratoryRate) / previousAverageRespiratoryRate * 100)
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

    fun isAverageRespiratoryRateIncreased(averageRespiratoryRateData: List<AverageRespiratoryRateData>): Boolean
    {
        if (averageRespiratoryRateData.isEmpty()) {
            return false // No data available
        }
        val latestAverageHeartRate = averageRespiratoryRateData.last().averageRespiratoryRate

        if (averageRespiratoryRateData.size > 1) {
            val previousAverageHeartRate = averageRespiratoryRateData[averageRespiratoryRateData.size - 2].averageRespiratoryRate

            if (latestAverageHeartRate > previousAverageHeartRate) {
                return true
            }
        }
        return false
    }
}