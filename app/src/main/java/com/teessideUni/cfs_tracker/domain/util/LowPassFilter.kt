package com.teessideUni.cfs_tracker.domain.util


class LowPassFilter(private val alpha: Float) {
    private var lastOutput = 0f

    init {
        require(alpha in 0f..1f) { "Alpha must be between 0 and 1" }
    }

    fun filter(input: Float): Float {
        val output = alpha * input + (1 - alpha) * lastOutput
        lastOutput = output
        return output
    }
}
