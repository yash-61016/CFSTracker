package com.teessideUni.cfs_tracker.presentation.screens.heart_rate

data class DataStoreState(
    val isLoading: Boolean = false,
    val isSuccess: String? = "",
    val isError: String?= ""
)