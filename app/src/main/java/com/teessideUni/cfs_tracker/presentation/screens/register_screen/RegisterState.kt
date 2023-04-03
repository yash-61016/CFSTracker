package com.teessideUni.cfs_tracker.presentation.screens.register_screen

data class RegisterState (
    val isLoading: Boolean = false,
    val isSuccess: String? = "",
    val isError: String?= ""
)