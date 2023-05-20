package com.teessideUni.cfs_tracker.domain.repository

import com.teessideUni.cfs_tracker.data.local.HeartRateData
import com.teessideUni.cfs_tracker.domain.model.Resource
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface HeartRateRepository {
    fun storeHeartRateData(heartRate: Double, timestamp: Date) : Flow<Resource<Boolean>>
    fun getHeartRateDataForWeek(year: Int, weekNumber: Int): Flow<Resource<MutableList<HeartRateData>>>
    fun getHeartRateDataForMonth(year: Int, month: Int): Flow<Resource<MutableList<HeartRateData>>>
}