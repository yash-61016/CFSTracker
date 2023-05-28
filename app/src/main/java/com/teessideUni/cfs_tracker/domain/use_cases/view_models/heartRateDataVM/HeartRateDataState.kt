package com.teessideUni.cfs_tracker.domain.use_cases.view_models.heartRateDataVM

data class HeartRateDataState(
    val isLoading: Boolean = false,
    val isSuccess: String? = "",
    val isError: String?= ""
)