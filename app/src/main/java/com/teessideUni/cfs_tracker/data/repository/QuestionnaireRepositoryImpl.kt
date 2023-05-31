package com.teessideUni.cfs_tracker.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.teessideUni.cfs_tracker.domain.model.QuestionWithOptions
import com.teessideUni.cfs_tracker.domain.model.Resource
import com.teessideUni.cfs_tracker.domain.repository.QuestionerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

class QuestionnaireRepositoryImpl  @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
)  : QuestionerRepository {
    val questionsWithOptions = listOf(
        QuestionWithOptions(
            question = "How fatigued are you?",
            options = listOf(
                "Mild fatigue",
                "Moderate fatigue",
                "Severe fatigue",
                "Very severe fatigue"
            )
        ),
        QuestionWithOptions(
            question = "What helps your fatigue the most? (Select all that apply)",
            options = listOf(
                "Resting",
                "Lying down",
                "Quiet situations",
                "Not exercising or avoiding exercise"
            )
        ),
        QuestionWithOptions(
            question = "What happens to you as you engage in normal physical or mental exertion, or after?",
            options = listOf(
                "No significant change",
                "Mild increase in symptoms",
                "Moderate increase in symptoms",
                "Severe increase in symptoms"
            )
        ),
        QuestionWithOptions(
            question = "How much activity does it take you to feel ill?",
            options = listOf(
                "Minimal activity",
                "Low level of activity",
                "Moderate level of activity",
                "High level of activity"
            )
        ),
        QuestionWithOptions(
            question = "Do you have any problems getting to sleep or staying asleep?",
            options = listOf(
                "No problems",
                "Mild difficulties",
                "Moderate difficulties",
                "Severe difficulties"
            )
        ),
        QuestionWithOptions(
            question = "Do you feel rested in the morning or after you have slept?",
            options = listOf(
                "Yes, consistently",
                "Occasionally",
                "Rarely",
                "Never"
            )
        )
    )

    override fun fetchQuestions(): List<QuestionWithOptions> {
        return questionsWithOptions
    }


    override fun sendAnswers(answers: List<Pair<String, String>>): Flow<Resource<Boolean>> {
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
                val dayOfWeekString = getDayOfWeekString(dayOfWeek)

                // Create a reference to the user's answer data for the current week and year
                val weekRef = firestore.collection("questionnaire")
                    .document(user.uid)
                    .collection("Year${year.toString().padStart(4, '0')}")
                    .document("Week${weekNumber.toString().padStart(2, '0')}")

                val finalRef = weekRef.collection(dayOfWeekString)
                // Create a new map to store the questions and answers with the day of the week
                val data = HashMap<String, Any>()

                // Iterate through the answers list and add each question and answer to the map
                for ((index, answer) in answers.withIndex()) {
                    val question = answer.first
                    val answerText = answer.second

                    // Create a map to store the question and answer for the current day of the week
                    val dayData = HashMap<String, String>()
                    dayData["question"] = question
                    dayData["answer"] = answerText

                    // Add the dayData map to the data map with the numerical count as the key
                    data["Question ${index + 1}"] = dayData
                }

                // Get the current timestamp
                val timestamp = Calendar.getInstance().time.toString()

                // Add the timestamp to the data map
                data["timestamp"] = timestamp

                // Add the data map to Firestore
                finalRef.document(timestamp).set(data, SetOptions.merge())

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
}
