package com.teessideUni.cfs_tracker.domain.repository

import com.teessideUni.cfs_tracker.domain.model.RespiratoryAccelerometerData

interface RespiratoryRateRepository {
//    fun saveRR(): RespiratoryRateRecord
//
//    fun getMonthlyRR(monthNumber: Number): Flow<List<RespiratoryRateRecord>>

    fun startListening()
    fun stopListening()
    fun getData(): ArrayList<RespiratoryAccelerometerData>
}