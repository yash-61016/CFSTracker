package com.teessideUni.cfs_tracker.domain.use_cases.heart_rate_service

import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

internal class MeasureStore {
    private val measurements = CopyOnWriteArrayList<Measurement<Int>>()
    private var minimum = 2147483647
    private var maximum = -2147483648

    fun add(measurement: Int) {
        val measurementWithDate = Measurement(Date(), measurement)
        measurements.add(measurementWithDate)
        if (measurement < minimum) minimum = measurement
        if (measurement > maximum) maximum = measurement
    }

    fun getLastStdValues(count: Int): List<Measurement<Int>> {
        val startIndex = if (count < measurements.size) measurements.size - count else 0
        val endIndex = measurements.size
        val result = ArrayList<Measurement<Int>>(count)
        for (i in startIndex until endIndex) {
            result.add(measurements[i])
        }
        return result
    }

    val lastTimestamp: Date
        get() = measurements[measurements.size - 1].timestamp
}