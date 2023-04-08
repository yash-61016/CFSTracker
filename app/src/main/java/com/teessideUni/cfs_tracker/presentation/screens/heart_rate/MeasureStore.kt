package com.teessideUni.cfs_tracker.presentation.screens.heart_rate

import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

internal class MeasureStore {
    private val measurements = CopyOnWriteArrayList<Measurement<Int>>()
    private var minimum = 2147483647
    private var maximum = -2147483648

    /**
     * The latest N measurements are always averaged in order to smooth the values before it is
     * analyzed.
     *
     * This value may need to be experimented with - it is better on the class level than putting it
     * into local scope
     */
    private val rollingAverageSize = 4
    fun add(measurement: Int) {
        val measurementWithDate = Measurement(Date(), measurement)
        measurements.add(measurementWithDate)
        if (measurement < minimum) minimum = measurement
        if (measurement > maximum) maximum = measurement
    }

    val stdValues: CopyOnWriteArrayList<Measurement<Float>>
        get() {
            val stdValues = CopyOnWriteArrayList<Measurement<Float>>()
            for (i in measurements.indices) {
                var sum = 0
                for (rollingAverageCounter in 0 until rollingAverageSize) {
                    sum += measurements[Math.max(0, i - rollingAverageCounter)].measurement
                }
                val stdValue = Measurement(
                    measurements[i].timestamp,
                    (sum.toFloat() / rollingAverageSize - minimum) / (maximum - minimum)
                )
                stdValues.add(stdValue)
            }
            return stdValues
        }

    fun  // this parameter can be set at OutputAnalyzer
            getLastStdValues(count: Int): CopyOnWriteArrayList<Measurement<Int>> {
        return if (count < measurements.size) {
            CopyOnWriteArrayList(
                measurements.subList(
                    measurements.size - 1 - count,
                    measurements.size - 1
                )
            )
        } else {
            measurements
        }
    }

    val lastTimestamp: Date
        get() = measurements[measurements.size - 1].timestamp
}