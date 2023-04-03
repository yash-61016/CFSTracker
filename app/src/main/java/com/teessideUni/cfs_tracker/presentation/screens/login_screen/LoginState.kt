package com.teessideUni.cfs_tracker.presentation.screens.login_screen

data class LoginState(
    val isLoading: Boolean = false,
    val isSuccess: String? = "",
    val isError: String?= ""
)