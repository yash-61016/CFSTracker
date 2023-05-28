package com.teessideUni.cfs_tracker.data.repository

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.teessideUni.cfs_tracker.domain.repository.AuthRepository
import com.teessideUni.cfs_tracker.domain.model.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
)  : AuthRepository {

    override val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    override suspend fun getCurrentUserName(): String? {
        val uid = firebaseAuth.currentUser?.uid ?: return null
        val userDoc = firestore.collection("users").document(uid).get().await()
        return userDoc.getString("username")
    }

    override suspend fun getCurrentUserEmail(): String? {
        val uid = firebaseAuth.currentUser?.uid ?: return null
        val userDoc = firestore.collection("users").document(uid).get().await()
        return userDoc.getString("email")
    }

    override suspend fun getCurrentUserContact(): String? {
        val uid = firebaseAuth.currentUser?.uid ?: return null
        val userDoc = firestore.collection("users").document(uid).get().await()
        return userDoc.getString("phone")
    }


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

    override suspend fun sendEmailVerification(): Flow<Resource<Void?>> {
        return flow {
            emit(Resource.Loading())
            try {
                val result = firebaseAuth.currentUser?.sendEmailVerification()?.await()
                emit(Resource.Success(result))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "An error occurred"))
            }
        }.catch { e ->
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    override fun reloadFirebaseUser(): Flow<Resource<Void?>> {
        return flow {
            emit(Resource.Loading())
            try {
                val result = firebaseAuth.currentUser?.reload()?.await()
                emit(Resource.Success(result))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "An error occurred"))
            }
        }.catch { e ->
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    override fun signOut() = firebaseAuth.signOut()

    override suspend fun revokeAccess(): Flow<Resource<Void?>> {
        return flow {
            emit(Resource.Loading())
            try {
                val result = firebaseAuth.currentUser?.delete()?.await()
                emit(Resource.Success(result))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "An error occurred"))
            }
        }.catch { e ->
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    override fun getAuthState(viewModelScope: CoroutineScope) = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser == null)
        }
        firebaseAuth.addAuthStateListener(authStateListener)
        awaitClose {
            firebaseAuth.removeAuthStateListener(authStateListener)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), firebaseAuth.currentUser == null)
}