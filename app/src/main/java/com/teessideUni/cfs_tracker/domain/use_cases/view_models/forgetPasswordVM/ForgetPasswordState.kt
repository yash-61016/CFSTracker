package com.teessideUni.cfs_tracker.domain.use_cases.view_models.forgetPasswordVM

data class ForgetPasswordState(
    val isLoading: Boolean = false,
    val isSuccess: String? = "",
    val isError: String?= ""
)