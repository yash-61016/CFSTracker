package com.teessideUni.cfs_tracker.domain.model

import android.util.Log

abstract class MeasurableSensor(
    protected val sensorType: Int
) {

    protected var onSensorValuesChanged: ((List<Float>) -> Unit)? = null

    abstract val doesSensorExist: Boolean

    abstract fun startListening()
    abstract fun stopListening()

    fun setOnSensorValuesChangedListener(listener: (List<Float>) -> Unit) {
        Log.d("I'm in the measurable sensor file",listener.toString())
        onSensorValuesChanged = listener
    }
}