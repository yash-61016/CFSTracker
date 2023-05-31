package com.teessideUni.cfs_tracker.presentation.ui.register.registerVM

data class RegisterState (
    val isLoading: Boolean = false,
    val isSuccess: String? = "",
    val isError: String?= ""
)