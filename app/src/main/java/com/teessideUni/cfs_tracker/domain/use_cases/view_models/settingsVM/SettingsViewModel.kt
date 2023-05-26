package com.teessideUni.cfs_tracker.domain.use_cases.view_models.settingsVM

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teessideUni.cfs_tracker.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: AuthRepository,
) : ViewModel() {

    //Log out settings
    fun logout() = viewModelScope.launch {
        repository.signOut();
    }

    suspend fun getCurrentUserName(): String {
        val currentUser = repository.getCurrentUserName()
        return if (currentUser != null) {
            currentUser ?: ""
        } else {
            // Handle the case where currentUser is null
            "Username"
        }
    }

    suspend fun getCurrentUserEmailAddress(): String {
        val currentUserEmail = repository.getCurrentUserEmail()
        return if (currentUserEmail != null) {
            currentUserEmail ?: ""
        } else {
            // Handle the case where currentUser is null
            ""
        }
    }

    suspend fun getCurrentUserContactNumber(): String {
        val currentUserContact = repository.getCurrentUserContact()
        return if (currentUserContact != null) {
            currentUserContact ?: ""
        } else {
            // Handle the case where currentUser is null
            ""
        }
    }
}