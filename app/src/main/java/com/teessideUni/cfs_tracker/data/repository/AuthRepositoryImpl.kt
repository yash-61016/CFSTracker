package com.teessideUni.cfs_tracker.data.repository

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.teessideUni.cfs_tracker.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
)  : AuthRepository {

    override fun loginUser(email: String, password: String): Flow<Resource<AuthResult>> {
        return flow {
            emit(Resource.Loading())
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            if (result.user != null && result.user!!.isEmailVerified) {
                emit(Resource.Success(result))
            } else {
                firebaseAuth.signOut()
                val message = "Please verify your email address before logging in."
                emit(Resource.Error(message))
            }
        }.catch {
            val message = it.message.toString()
            emit(Resource.Error(message))
        }
    }

    override fun registerUser(
        email: String,
        password: String,
        username: String,
        phone:String
    ): Flow<Resource<AuthResult>> {
        return flow {
            emit(Resource.Loading())
            // Create the user with email and password
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()

            // Send email verification
            result.user?.sendEmailVerification()?.await()

            // Save user data to Firestore
            val user = hashMapOf(
                "username" to username,
                "phone" to phone,
                "email" to email
            )
            val userRef = firestore.collection("users").document(result.user?.uid.toString())
            userRef.set(user).await()

            emit(Resource.Success(result))
        }.catch {
            emit(Resource.Error(it.message.toString()))
        }
    }

    override fun forgetPassword(email: String): Flow<Resource<Void>> {
        return flow {
            emit(Resource.Loading())
            try {
                val result = firebaseAuth.sendPasswordResetEmail(email).await()
                emit(Resource.Success(result))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "An error occurred"))
            }
        }.catch { e ->
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }
}