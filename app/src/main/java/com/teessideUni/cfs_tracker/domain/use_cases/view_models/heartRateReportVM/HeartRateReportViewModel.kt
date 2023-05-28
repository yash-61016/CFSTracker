package com.teessideUni.cfs_tracker.domain.use_cases.view_models.heartRateReportVM

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teessideUni.cfs_tracker.data.local.AverageHeartRateData
import com.teessideUni.cfs_tracker.data.local.HeartRateData
import com.teessideUni.cfs_tracker.domain.model.Resource
import com.teessideUni.cfs_tracker.domain.repository.AuthRepository
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
    private val repository: AuthRepository,
    private val heartRateRepository: HeartRateRepository
) : ViewModel()
{
    private val calendar: Calendar = Calendar.getInstance()
    private val currentYear = calendar.get(Calendar.YEAR)
    private val currentWeekNumber = calendar.get(Calendar.WEEK_OF_YEAR)
    private val currentMonthNumber = calendar.get(Calendar.MONTH) + 1
    private val lastMonthNumber = if (currentMonthNumber == 1) 12 else currentMonthNumber - 1
    private val monthBeforeLastMonthNumber = if (lastMonthNumber == 1) 12 else currentMonthNumber - 2

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


    suspend fun getAverageHeartRateDataForThreeMonths(): List<AverageHeartRateData> {
        // clear the list
        averageHeartRateData.clear()

        val currentMonthDataResult = getHeartRateDataForAMonth(currentYear, currentMonthNumber).first()
        val lastMonthDataResult = getHeartRateDataForAMonth(currentYear, lastMonthNumber).first()
        val monthBeforeLastMonthDataResult = getHeartRateDataForAMonth(currentYear, monthBeforeLastMonthNumber).first()

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

        val monthBeforeLastMonthAverage = monthBeforeLastMonthData.map { it.heartRate }.average()
        averageHeartRateData.add(AverageHeartRateData(monthBeforeLastMonthAverage, Month.of(monthBeforeLastMonthNumber).name))

        val lastMonthAverage = lastMonthData.map { it.heartRate }.average()
        averageHeartRateData.add(AverageHeartRateData(lastMonthAverage, Month.of(lastMonthNumber).name))

        val currentMonthAverage = currentMonthData.map { it.heartRate }.average()
        averageHeartRateData.add(AverageHeartRateData(currentMonthAverage, Month.of(currentMonthNumber).name))

        return averageHeartRateData
    }

    fun calculateAverageHeartRateChangePercentage(averageHeartRateData: List<AverageHeartRateData>): String {

        if (averageHeartRateData.isEmpty()) {
            return "0.00%" // No data available
        }
        val latestAverageHeartRate = averageHeartRateData.last().averageHeartRate
        val previousAverageHeartRate = averageHeartRateData[averageHeartRateData.size - 2].averageHeartRate

        val changePercentage = ((latestAverageHeartRate - previousAverageHeartRate) / previousAverageHeartRate * 100)
        val decimalFormat = DecimalFormat("0.00")

        return "${decimalFormat.format(changePercentage)}%"
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