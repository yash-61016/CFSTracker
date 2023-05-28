package com.teessideUni.cfs_tracker.core

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Constants {

    const val MEASUREMENT_LENGTH = 35000f

    const val REQUEST_CODE_CAMERA = 0
    const val MESSAGE_UPDATE_PULSE_TEXT = 1
    const val MESSAGE_UPDATE_REALTIME_TEXT = 2
    const val UPDATED_MESSAGE = 3

    const val FINGER_NOT_DETECTED = "Finger not detected. Please place your finger on the camera to begin pulse measurement."
    const val CAMERA_ERROR = "No pixels detected - there may be an issue when accessing the camera."
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
    val MEASUREMENT_COMPLETE = "Pulse rate measurement is successfully calculated at ${LocalDateTime.now().format(
        formatter
    )}"
    const val CAMERA_PERMISSION_REQUIRED = "Camera permissions required. "
    const val FAILED_CAMERA_ACCESS = "No access to camera...."

}