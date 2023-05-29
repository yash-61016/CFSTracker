package com.teessideUni.cfs_tracker.domain.repository

import com.teessideUni.cfs_tracker.domain.model.Resource
import com.teessideUni.cfs_tracker.domain.model.RespiratoryAccelerometerData
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface RespiratoryRateRepository {
    fun storeRespiratoryRateData(respiratoryRate: Double, timeStamp: Date) : Flow<Resource<Boolean>>
    fun startListening()
    fun stopListening()
    fun getData(): ArrayList<RespiratoryAccelerometerData>
}