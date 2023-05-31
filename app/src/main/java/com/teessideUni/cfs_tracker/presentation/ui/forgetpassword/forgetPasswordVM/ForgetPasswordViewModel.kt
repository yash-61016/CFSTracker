package com.teessideUni.cfs_tracker.presentation.ui.forgetpassword.forgetPasswordVM

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teessideUni.cfs_tracker.domain.repository.AuthRepository
import com.teessideUni.cfs_tracker.domain.model.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgetPasswordViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel()
{
    private val _forgetPwdState = Channel<ForgetPasswordState>()
    val forgetPwdState = _forgetPwdState.receiveAsFlow()

    fun forgetPassword(email:String) = viewModelScope.launch {
            repository.forgetPassword(email).collect{
                result ->
            when(result){
                is Resource.Success -> {
                    _forgetPwdState.send(ForgetPasswordState(isSuccess = "Password reset link is sent to $email"))
                }
                is Resource.Error -> {
                    _forgetPwdState.send(ForgetPasswordState(isError = result.message.toString()))
                }
                is Resource.Loading -> {
                    _forgetPwdState.send(ForgetPasswordState(isLoading = true))
                }
            }
        }
    }
}