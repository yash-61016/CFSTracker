package com.teessideUni.cfs_tracker.data.repository

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.teessideUni.cfs_tracker.domain.model.Resource
import com.teessideUni.cfs_tracker.domain.model.RespiratoryAccelerometerData
import com.teessideUni.cfs_tracker.domain.repository.AndroidSensor
import com.teessideUni.cfs_tracker.domain.repository.RespiratoryRateRepository
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject

class RespiratoryRateRepositoryImpl @Inject constructor(
    private val context: Context,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : RespiratoryRateRepository {

    private val accelerometerSensor = AccelerometerSensor(context);
    private val sensorData = ArrayList<RespiratoryAccelerometerData>();
    override fun storeRespiratoryRateData(
        respiratoryRate: Double,
        timeStamp: Date
    ): Flow<Resource<Boolean>> {
        TODO("Not yet implemented")
    }

    override fun startListening() {
        Log.d("I'm in repo", "I'm about to start listening")

        accelerometerSensor.startListening()
        accelerometerSensor.setOnSensorValuesChangedListener { values ->
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

}

class AccelerometerSensor(
    context: Context
) : AndroidSensor(
    context = context,
    sensorFeature = PackageManager.FEATURE_SENSOR_ACCELEROMETER,
    sensorType = Sensor.TYPE_ACCELEROMETER
)