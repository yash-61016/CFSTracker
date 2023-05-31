package com.teessideUni.cfs_tracker.domain.use_case.heart_rate_service

import java.util.*

internal class Measurement<T>(val timestamp: Date, val measurement: T)