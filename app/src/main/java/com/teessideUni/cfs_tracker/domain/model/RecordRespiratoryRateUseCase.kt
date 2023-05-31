package com.teessideUni.cfs_tracker.domain.model

import androidx.room.Entity

@Entity
data class RespiratoryRateRecord(
    val timestamp: Long,
    val respiratoryRate: Float
)
