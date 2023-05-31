package com.teessideUni.cfs_tracker.presentation.ui.login.loginVM

data class LoginState(
    val isLoading: Boolean = false,
    val isSuccess: String? = "",
    val isError: String?= ""
)