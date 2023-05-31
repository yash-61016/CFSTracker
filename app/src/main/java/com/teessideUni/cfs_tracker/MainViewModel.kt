package com.teessideUni.cfs_tracker

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teessideUni.cfs_tracker.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repo: AuthRepository
): ViewModel() {
    init {
        getAuthState()
    }

    fun getCurrentUser(): Boolean {
        val user = repo.currentUser
        Log.d("currentUser", user?.email.toString())
        return user != null
    }

    fun getAuthState() = repo.getAuthState(viewModelScope)

    val isEmailVerified get() = repo.currentUser?.isEmailVerified ?: false
}