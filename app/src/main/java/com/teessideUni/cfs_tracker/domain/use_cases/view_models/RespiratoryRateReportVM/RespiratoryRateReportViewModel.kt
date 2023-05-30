package com.teessideUni.cfs_tracker.domain.use_cases.view_models.RespiratoryRateReportVM

import android.util.Log
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

    private val firstMonthData = calendar.get(Calendar.MONTH) + 1
    private val secondMonthData = if (firstMonthData == 1) 12 else firstMonthData - 1
    private val thirdMonthData = if (secondMonthData == 1) 12 else secondMonthData - 1
    private val forthMonthData = if (thirdMonthData == 1) 12 else thirdMonthData - 1
    private val fifthMonthData = if (forthMonthData == 1) 12 else forthMonthData - 1
    private val sixthMonthData = if (fifthMonthData == 1) 12 else fifthMonthData - 1

    suspend fun getAverageRespiratoryRateDataForSixMonths(): List<AverageRespiratoryRateData> {
        // clear the list
        averageRespiratoryRateData.clear()

        val fiveMonthsAgoYear = if (sixthMonthData >= firstMonthData) currentYear - 1 else currentYear
        val fourMonthsAgoYear = if (fifthMonthData >= firstMonthData) currentYear - 1 else currentYear
        val threeMonthsAgoYear = if (forthMonthData >= firstMonthData) currentYear - 1 else currentYear
        val monthBeforeLastYear = if (thirdMonthData >= firstMonthData) currentYear - 1 else currentYear
        val lastMonthYear = if (secondMonthData >= firstMonthData) currentYear - 1 else currentYear
        val currentMonthYear = currentYear

        val currentMonthDataResult = getRespiratoryRateDataForAMonth(currentMonthYear, firstMonthData).first()
        val lastMonthDataResult = getRespiratoryRateDataForAMonth(lastMonthYear, secondMonthData).first()
        val monthBeforeLastMonthDataResult = getRespiratoryRateDataForAMonth(monthBeforeLastYear, thirdMonthData).first()
        val threeMonthsBeforeDataResult = getRespiratoryRateDataForAMonth(threeMonthsAgoYear, forthMonthData).first()
        val fourMonthsBeforeDataResult = getRespiratoryRateDataForAMonth(fourMonthsAgoYear, fifthMonthData).first()
        val fiveMonthsBeforeDataResult = getRespiratoryRateDataForAMonth(fiveMonthsAgoYear, sixthMonthData).first()

        val fiveMonthsAgoData = if (fiveMonthsBeforeDataResult is Resource.Success) {
            fiveMonthsBeforeDataResult.data ?: emptyList()
        } else {
            emptyList()
        }
        val fourMonthsAgoData = if (fourMonthsBeforeDataResult is Resource.Success) {
            fourMonthsBeforeDataResult.data ?: emptyList()
        } else {
            emptyList()
        }
        val threeMonthsAgoData = if (threeMonthsBeforeDataResult is Resource.Success) {
            threeMonthsBeforeDataResult.data ?: emptyList()
        } else {
            emptyList()
        }
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

        val fiveMonthsAgoDataAverage = fiveMonthsAgoData.map { it.rateValue }.average()
        averageRespiratoryRateData.add(AverageRespiratoryRateData(fiveMonthsAgoDataAverage, Month.of(sixthMonthData).name.substring(0,3)))

        val fourMonthsAgoDataAverage = fourMonthsAgoData.map { it.rateValue }.average()
        averageRespiratoryRateData.add(AverageRespiratoryRateData(fourMonthsAgoDataAverage, Month.of(fifthMonthData).name.substring(0,3)))

        val threeMonthsAgoDataAverage = threeMonthsAgoData.map { it.rateValue }.average()
        averageRespiratoryRateData.add(AverageRespiratoryRateData(threeMonthsAgoDataAverage, Month.of(forthMonthData).name.substring(0,3)))

        val monthBeforeLastMonthAverage = monthBeforeLastMonthData.map { it.rateValue }.average()
        averageRespiratoryRateData.add(AverageRespiratoryRateData(monthBeforeLastMonthAverage, Month.of(thirdMonthData).name.substring(0,3)))

        val lastMonthAverage = lastMonthData.map { it.rateValue }.average()
        averageRespiratoryRateData.add(AverageRespiratoryRateData(lastMonthAverage, Month.of(secondMonthData).name.substring(0,3)))

        val currentMonthAverage = currentMonthData.map { it.rateValue }.average()
        averageRespiratoryRateData.add(AverageRespiratoryRateData(currentMonthAverage, Month.of(firstMonthData).name.substring(0,3)))

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