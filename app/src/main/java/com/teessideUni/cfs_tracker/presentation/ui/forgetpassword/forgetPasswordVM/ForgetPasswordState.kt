package com.teessideUni.cfs_tracker.presentation.ui.forgetpassword.forgetPasswordVM

data class ForgetPasswordState(
    val isLoading: Boolean = false,
    val isSuccess: String? = "",
    val isError: String?= ""
)