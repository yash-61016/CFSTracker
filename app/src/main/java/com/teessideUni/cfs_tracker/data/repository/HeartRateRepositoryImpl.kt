package com.teessideUni.cfs_tracker.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.teessideUni.cfs_tracker.data.local.HeartRateData
import com.teessideUni.cfs_tracker.domain.model.Resource
import com.teessideUni.cfs_tracker.domain.repository.AuthRepository
import com.teessideUni.cfs_tracker.domain.repository.HeartRateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

class HeartRateRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
)  : HeartRateRepository {

    override fun storeHeartRateData(
        heartRate: Double,
        timestamp: Date
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

                // Create a reference to the user's heart rate data for the current week and year
                val weekRef = firestore.collection("heartRate")
                    .document(user.uid)
                    .collection("Year${year.toString().padStart(4, '0')}")
                    .document("Week${weekNumber.toString().padStart(2, '0')}")

                // Create a new map with the heart rate and timestamp data
                val data = hashMapOf(
                    "heartRate" to heartRate,
                    "timestamp" to timestamp
                )

                // Add the heart rate data to the appropriate day of the week
                val dayOfWeekString = getDayOfWeekString(dayOfWeek)
                weekRef.collection(dayOfWeekString).add(data)

                Log.d("reached end successful", "saved")
                // Set result to true since data was stored successfully
                result = true
            } else {
                Log.d("Error", "error")
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

    override fun getHeartRateDataForWeek(
        year: Int,
        weekNumber: Int
    ): Flow<Resource<MutableList<HeartRateData>>> {
        return flow {
            emit(Resource.Loading())

            val user = firebaseAuth.currentUser
            if (user != null) {
                val (startDate, endDate) = getWeekStartAndEndDates(year, weekNumber)

                // Create a reference to the user's heart rate data for the selected week
                val weekRef = firestore.collection("heartRate")
                    .document(user.uid)
                    .collection("Year${year.toString().padStart(4, '0')}")
                    .document("Week${weekNumber.toString().padStart(2, '0')}")

                // Retrieve the heart rate data for the selected week
                val heartRateDataList = mutableListOf<HeartRateData>()
                for (dayOfWeek in 1..7) {
                    val dayOfWeekString = getDayOfWeekString(dayOfWeek)
                    val dayRef = weekRef.collection(dayOfWeekString)
                        .whereGreaterThanOrEqualTo("timestamp", startDate)
                        .whereLessThan("timestamp", endDate)

                    val daySnapshot = dayRef.get().await()
                    for (measurementSnapshot in daySnapshot.documents) {
                        val heartRate = measurementSnapshot.getDouble("heartRate")
                        val timestamp = measurementSnapshot.getDate("timestamp")

                        heartRate?.let { heartRateValue ->
                            timestamp?.let { timestampValue ->
                                heartRateDataList.add(HeartRateData(heartRateValue, timestampValue))
                            }
                        }
                    }
                }


                emit(Resource.Success(heartRateDataList))
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
}
