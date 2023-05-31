package com.teessideUni.cfs_tracker.data.remote

import android.util.Log

// Data source for storing the recorded sensor data
interface RespiratoryRateDataSource {
    fun addSensorData(sensorData: Float)
    fun getSensorData(): List<Float>
}

class RespiratoryRateDataSourceImpl : RespiratoryRateDataSource {
    private val sensorData = mutableListOf<Float>()

    override fun addSensorData(sensorData: Float) {
        Log.d("I'm in DataSource file", sensorData.toString())
        this.sensorData.add(sensorData)
    }

    override fun getSensorData(): List<Float> {
        return sensorData.toList()
    }
}
