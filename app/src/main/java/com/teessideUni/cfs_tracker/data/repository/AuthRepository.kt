package com.teessideUni.cfs_tracker.data.repository

import com.google.firebase.auth.AuthResult
import com.teessideUni.cfs_tracker.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun loginUser(email:String, password:String): Flow<Resource<AuthResult>>
    fun registerUser(email:String, password:String, username: String, phone: String): Flow<Resource<AuthResult>>
    fun forgetPassword(email:String): Flow<Resource<Void>>
}