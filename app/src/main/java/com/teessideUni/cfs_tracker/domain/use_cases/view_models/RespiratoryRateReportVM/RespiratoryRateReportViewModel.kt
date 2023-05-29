package com.teessideUni.cfs_tracker.domain.use_cases.view_models.RespiratoryRateReportVM

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teessideUni.cfs_tracker.data.local.AverageRespiratoryRateData
import com.teessideUni.cfs_tracker.data.local.RespiratoryRateDataValues
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
    private val currentMonthNumber = calendar.get(Calendar.MONTH) + 1
    private val lastMonthNumber = if (currentMonthNumber == 1) 12 else currentMonthNumber - 1
    private val monthBeforeLastMonthNumber = if (lastMonthNumber == 1) 12 else currentMonthNumber - 2

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


    suspend fun getAverageRespiratoryRateDataForThreeMonths(): List<AverageRespiratoryRateData> {
        // clear the list
        averageRespiratoryRateData.clear()

        val currentMonthDataResult = getRespiratoryRateDataForAMonth(currentYear, currentMonthNumber).first()
        val lastMonthDataResult = getRespiratoryRateDataForAMonth(currentYear, lastMonthNumber).first()
        val monthBeforeLastMonthDataResult = getRespiratoryRateDataForAMonth(currentYear, monthBeforeLastMonthNumber).first()

        val monthBeforeLastMonthData = if (monthBeforeLastMonthDataResult is Resource.Success) {
            monthBeforeLastMonthDataResult.data ?: emptyList()
        } else {
            emptyList()
        }
        val lastMonthData = if (lastMonthDataResult is Resource.Success) {
            lastMonthDataResult.data ?: emptyList()
        } else {
            emptyList()
        }
        val currentMonthData = if (currentMonthDataResult is Resource.Success) {
            currentMonthDataResult.data ?: emptyList()
        } else {
            emptyList()
        }

        val monthBeforeLastMonthAverage = monthBeforeLastMonthData.map { it.rateValue }.average()
        averageRespiratoryRateData.add(AverageRespiratoryRateData(monthBeforeLastMonthAverage, Month.of(monthBeforeLastMonthNumber).name))

        val lastMonthAverage = lastMonthData.map { it.rateValue }.average()
        averageRespiratoryRateData.add(AverageRespiratoryRateData(lastMonthAverage, Month.of(lastMonthNumber).name))

        val currentMonthAverage = currentMonthData.map { it.rateValue }.average()
        averageRespiratoryRateData.add(AverageRespiratoryRateData(currentMonthAverage, Month.of(currentMonthNumber).name))

        return averageRespiratoryRateData
    }

    fun calculateAverageRespiratoryRateChangePercentage(averageRespiratoryRateData: List<AverageRespiratoryRateData>): String {

        if (averageRespiratoryRateData.isEmpty()) {
            return "0.00%" // No data available
        }
        val latestAverageHeartRate = averageRespiratoryRateData.last().averageRespiratoryRate
        val previousAverageHeartRate = averageRespiratoryRateData[averageRespiratoryRateData.size - 2].averageRespiratoryRate

        val changePercentage = ((latestAverageHeartRate - previousAverageHeartRate) / previousAverageHeartRate * 100)
        val decimalFormat = DecimalFormat("0.00")

        return "${decimalFormat.format(changePercentage)}%"
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