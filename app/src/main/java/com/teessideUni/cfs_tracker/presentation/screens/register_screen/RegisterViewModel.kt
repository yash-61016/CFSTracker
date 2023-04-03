package com.teessideUni.cfs_tracker.presentation.screens.register_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teessideUni.cfs_tracker.data.repository.AuthRepository
import com.teessideUni.cfs_tracker.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel()
{
    private val _registerState = Channel<RegisterState>()
    val registerState = _registerState.receiveAsFlow()

    fun registerUser(email:String, password:String, username: String, phone: String) = viewModelScope.launch {
        repository.registerUser(email, password, username, phone ).collect{
                result ->
            when(result){
                is Resource.Success -> {
                    _registerState.send(RegisterState(isSuccess = "Sign Up Success"))
                }
                is Resource.Error -> {
                    _registerState.send(RegisterState(isError = result.message.toString()))
                }
                is Resource.Loading -> {
                    _registerState.send(RegisterState(isLoading = true))
                }
            }
        }
    }
}