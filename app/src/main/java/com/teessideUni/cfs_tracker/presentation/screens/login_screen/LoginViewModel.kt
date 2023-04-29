package com.teessideUni.cfs_tracker.presentation.screens.login_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teessideUni.cfs_tracker.domain.model.Resource
import com.teessideUni.cfs_tracker.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel()
{
    private val _loginState = Channel<LoginState>()
    val loginState = _loginState.receiveAsFlow()

    fun loginUser(email:String, password:String) = viewModelScope.launch {
        repository.loginUser(email, password).collect{
            result ->
            when(result){
                is Resource.Success -> {
                    _loginState.send(LoginState(isSuccess = "Log In Success"))
                }
                is Resource.Error -> {
                    _loginState.send(LoginState(isError = result.message.toString()))
                }
                is Resource.Loading -> {
                    _loginState.send(LoginState(isLoading = true))
                }
            }
        }
    }
}