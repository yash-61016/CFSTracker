package com.teessideUni.cfs_tracker.presentation.screens.home_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teessideUni.cfs_tracker.domain.model.Resource
import com.teessideUni.cfs_tracker.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel()
{
    fun logout() = viewModelScope.launch {
        repository.signOut();
    }
}