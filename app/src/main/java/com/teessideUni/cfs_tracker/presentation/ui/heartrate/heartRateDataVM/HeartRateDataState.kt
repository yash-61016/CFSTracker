package com.teessideUni.cfs_tracker.presentation.ui.heartrate.heartRateDataVM

data class HeartRateDataState(
    val isLoading: Boolean = false,
    val isSuccess: String? = "",
    val isError: String?= ""
)