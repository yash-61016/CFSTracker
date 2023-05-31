package com.teessideUni.cfs_tracker.domain.repository


import com.teessideUni.cfs_tracker.data.model.RespiratoryRateDataValues
import com.teessideUni.cfs_tracker.domain.model.Resource
import com.teessideUni.cfs_tracker.data.model.RespiratoryAccelerometerData
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface RespiratoryRateRepository {
    fun storeRespiratoryRateData(respiratoryRate: Float, timeStamp: Date) : Flow<Resource<Boolean>>
    fun startListening()
    fun stopListening()
    fun getData(): ArrayList<RespiratoryAccelerometerData>
    fun getRespiratoryRateDataForWeek(year: Int, weekNumber: Int): Flow<Resource<MutableList<RespiratoryRateDataValues>>>
    fun getRespiratoryRateDataForMonth(year: Int, month: Int): Flow<Resource<MutableList<RespiratoryRateDataValues>>?>
}