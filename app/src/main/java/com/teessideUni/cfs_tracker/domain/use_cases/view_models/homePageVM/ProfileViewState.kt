package com.teessideUni.cfs_tracker.domain.use_cases.view_models.homePageVM

data class ProfileViewState(
    val isLoading: Boolean = false,
    val isSuccess: String? = "",
    val isError: String?= ""
)
