package com.teessideUni.cfs_tracker.domain.use_cases.view_models.loginVM

data class LoginState(
    val isLoading: Boolean = false,
    val isSuccess: String? = "",
    val isError: String?= ""
)