package com.teessideUni.cfs_tracker.data.repository

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.teessideUni.cfs_tracker.data.local.RespiratoryRateDataValues
import com.teessideUni.cfs_tracker.domain.model.Resource
import com.teessideUni.cfs_tracker.domain.model.RespiratoryAccelerometerData
import com.teessideUni.cfs_tracker.domain.repository.AndroidSensor
import com.teessideUni.cfs_tracker.domain.repository.RespiratoryRateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

class RespiratoryRateRepositoryImpl @Inject constructor(
    private val context: Context,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : RespiratoryRateRepository {

    private val accelerometerSensor = AccelerometerSensor(context)
    private val sensorData = ArrayList<RespiratoryAccelerometerData>()

    override fun storeRespiratoryRateData(
        respiratoryRate: Float,
        timeStamp: Date
    ): Flow<Resource<Boolean>> {
        return flow {
            var result = false
            val user = firebaseAuth.currentUser
            if (user != null) {

                // Get the current week number and year
                val calendar = Calendar.getInstance()
                val weekNumber = calendar.get(Calendar.WEEK_OF_YEAR)
                val year = calendar.get(Calendar.YEAR)

                // Get the current day of the week
                val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

                // Create a reference to the user's respiratory rate data for the current week and year
                val weekRef = firestore.collection("respiratoryRate")
                    .document(user.uid)
                    .collection("Year${year.toString().padStart(4, '0')}")
                    .document("Week${weekNumber.toString().padStart(2, '0')}")

                // Create a new map with the respiratory rate and timestamp data
                val data = hashMapOf(
                    "respiratoryRate" to respiratoryRate,
                    "timestamp" to timeStamp
                )

                // Add the heart rate data to the appropriate day of the week
                val dayOfWeekString = getDayOfWeekString(dayOfWeek)
                weekRef.collection(dayOfWeekString).add(data)

                // Set result to true since data was stored successfully
                result = true
            } else {
                emit(Resource.Error("Failed to add data."))
            }
            emit(Resource.Success(result))
        }.catch {
            emit(Resource.Error(it.message.toString()))
        }
    }

    private fun getDayOfWeekString(dayOfWeek: Int): String {
        return when (dayOfWeek) {
            Calendar.MONDAY -> "monday"
            Calendar.TUESDAY -> "tuesday"
            Calendar.WEDNESDAY -> "wednesday"
            Calendar.THURSDAY -> "thursday"
            Calendar.FRIDAY -> "friday"
            Calendar.SATURDAY -> "saturday"
            Calendar.SUNDAY -> "sunday"
            else -> throw IllegalArgumentException("Invalid day of week: $dayOfWeek")
        }
    }

    override fun startListening() {
        accelerometerSensor.startListening()
        accelerometerSensor.setOnSensorValuesChangedListener { values ->
            val zAxisValue = values[2]
            var value = RespiratoryAccelerometerData(zAxisValue, System.currentTimeMillis())
            sensorData.add(value)
        }
    }

    override fun stopListening() {
        accelerometerSensor.stopListening()
    }

    override fun getData(): ArrayList<RespiratoryAccelerometerData> {
        return sensorData
    }


    override fun getRespiratoryRateDataForWeek(
        year: Int,
        weekNumber: Int
    ): Flow<Resource<MutableList<RespiratoryRateDataValues>>> {
        return flow {
            val user = firebaseAuth.currentUser
            if (user != null) {
                val (startDate, endDate) = getWeekStartAndEndDates(year, weekNumber)

                // Create a reference to the user's heart rate data for the selected week
                val weekRef = firestore.collection("respiratoryRate")
                    .document(user.uid)
                    .collection("Year${year.toString().padStart(4, '0')}")
                    .document("Week${weekNumber.toString().padStart(2, '0')}")

                // Retrieve the heart rate data for the selected week
                val respiratoryRateDataList = mutableListOf<RespiratoryRateDataValues>()
                for (dayOfWeek in 1..7) {
                    val dayOfWeekString = getDayOfWeekString(dayOfWeek)
                    val dayRef = weekRef.collection(dayOfWeekString)
                        .whereGreaterThanOrEqualTo("timestamp", startDate)
                        .whereLessThan("timestamp", endDate)

                    val daySnapshot = dayRef.get().await()
                    for (measurementSnapshot in daySnapshot.documents) {
                        val respiratoryRate = measurementSnapshot.getDouble("respiratoryRate")
                        val timestamp = measurementSnapshot.getDate("timestamp")

                        respiratoryRate?.let { respiratoryRateValue ->
                            timestamp?.let { timestampValue ->
                                respiratoryRateDataList.add(RespiratoryRateDataValues(
                                    respiratoryRate.toFloat(), timestampValue))
                            }
                        }
                    }
                }
                emit(Resource.Success(respiratoryRateDataList))
            } else {
                emit(Resource.Error("Failed to retrieve data."))
            }
        }.catch {
            emit(Resource.Error(it.message.toString()))
        }
    }

    private fun getWeekStartAndEndDates(year: Int, weekNumber: Int): Pair<Date, Date> {
        val calendar = Calendar.getInstance()
        calendar.clear()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.WEEK_OF_YEAR, weekNumber)
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        val startDate = calendar.time
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
        calendar.add(Calendar.DATE, 1)
        val endDate = calendar.time
        return Pair(startDate, endDate)
    }

    override fun getRespiratoryRateDataForMonth(
        year: Int,
        month: Int
    ): Flow<Resource<MutableList<RespiratoryRateDataValues>>?> {
        return flow {
            val user = firebaseAuth.currentUser
            if (user != null) {
                val (startWeek, endWeek) = getMonthStartAndEndWeeks(year, month)

                // Retrieve the heart rate data for the selected weeks of the month
                val respiratoryRateDataList = mutableListOf<RespiratoryRateDataValues>()
                for (weekNumber in startWeek..endWeek) {
                    when (val weekDataResource = getRespiratoryRateDataForWeek(year, weekNumber).first()) {
                        is Resource.Success -> {
                            val dataList = weekDataResource.data
                            dataList?.let { respiratoryRateDataList.addAll(it) }
                        }

                        is Resource.Error -> {
                            emit(weekDataResource.message?.let { Resource.Error(it) })
                            return@flow
                        }

                        is Resource.Loading -> {
                            emit(Resource.Loading())
                        }
                    }
                }
                emit(Resource.Success(respiratoryRateDataList))
            } else {
                emit(Resource.Error("Failed to retrieve data."))
            }
        }.catch {
            emit(Resource.Error(it.message.toString()))
        }
    }

    private fun getMonthStartAndEndWeeks(year: Int, month: Int): Pair<Int, Int> {
        val calendar = Calendar.getInstance()
        calendar.clear()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month - 1) // Adjust month to 0-based index
        calendar.set(Calendar.DAY_OF_MONTH, 1) // Set to the first day of the month

        val startWeek = calendar.get(Calendar.WEEK_OF_YEAR)

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) // Set to the last day of the month
        val endWeek = calendar.get(Calendar.WEEK_OF_YEAR)

        return Pair(startWeek, endWeek)
    }
}

class AccelerometerSensor(
    context: Context
) : AndroidSensor(
    context = context,
    sensorFeature = PackageManager.FEATURE_SENSOR_ACCELEROMETER,
    sensorType = Sensor.TYPE_ACCELEROMETER
)