package com.teessideUni.cfs_tracker.domain.repository

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.teessideUni.cfs_tracker.domain.model.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val currentUser: FirebaseUser?
    suspend fun getCurrentUserName(): String?
    suspend fun getCurrentUserEmail(): String?
    suspend fun getCurrentUserContact(): String?
    fun loginUser(email:String, password:String): Flow<Resource<AuthResult>>
    fun registerUser(email:String, password:String, username: String, phone: String): Flow<Resource<AuthResult>>
    fun forgetPassword(email:String): Flow<Resource<Void>>
    suspend fun sendEmailVerification():  Flow<Resource<Void?>>
    fun reloadFirebaseUser():  Flow<Resource<Void?>>
    fun signOut()
    suspend fun revokeAccess():  Flow<Resource<Void?>>
    fun getAuthState(viewModelScope: CoroutineScope): StateFlow<Boolean>
}