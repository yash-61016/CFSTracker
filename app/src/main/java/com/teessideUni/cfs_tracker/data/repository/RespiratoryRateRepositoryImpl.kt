package com.teessideUni.cfs_tracker.data.repository

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.util.Log
import com.teessideUni.cfs_tracker.data.remote.RespiratoryRateDataSource
import com.teessideUni.cfs_tracker.domain.model.RespiratoryAccelerometerData
import com.teessideUni.cfs_tracker.domain.repository.AndroidSensor
import com.teessideUni.cfs_tracker.domain.repository.RespiratoryRateRepository

class RespiratoryRateRepositoryImpl(private val context: Context,private val respiratoryRateDataSource: RespiratoryRateDataSource) : RespiratoryRateRepository {
    private val app = Application();
    private val accelerometerSensor = AccelerometerSensor(context);
    private val sensorData = ArrayList<RespiratoryAccelerometerData>();
    override fun startListening() {
        Log.d("I'm in repo", "I'm about to start listening")

        accelerometerSensor.startListening()
        accelerometerSensor.setOnSensorValuesChangedListener{values ->
            val zAxisValue = values[2]
//            Log.d("I'm in repo", zvalues.toString())
            var value = RespiratoryAccelerometerData(zAxisValue, System.currentTimeMillis())
            sensorData.add(value)
        }
    }

    override fun stopListening() {
        Log.d("I'm in repo", "I'm about to stop listening")
        accelerometerSensor.stopListening()

    }

    override fun getData(): ArrayList<RespiratoryAccelerometerData> {
        return sensorData
    }
    init {
        Log.d("I'm in repo", "I'm about to init")
    }
}
class AccelerometerSensor(
    context: Context
): AndroidSensor(
    context = context,
    sensorFeature = PackageManager.FEATURE_SENSOR_ACCELEROMETER,
    sensorType = Sensor.TYPE_ACCELEROMETER
)