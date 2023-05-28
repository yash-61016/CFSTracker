package com.teessideUni.cfs_tracker.domain.use_cases.view_models.registerVM

data class RegisterState (
    val isLoading: Boolean = false,
    val isSuccess: String? = "",
    val isError: String?= ""
)