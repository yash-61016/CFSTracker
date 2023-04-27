package com.teessideUni.cfs_tracker.presentation.screens.heart_rate

import android.os.CountDownTimer
import android.os.Handler
import android.os.Message
import android.view.TextureView
import com.teessideUni.cfs_tracker.R
import com.teessideUni.cfs_tracker.presentation.screens.heart_rate.Constants.MEASUREMENT_LENGTH
import com.teessideUni.cfs_tracker.presentation.screens.heart_rate.Constants.MESSAGE_CAMERA_NOT_AVAILABLE
import com.teessideUni.cfs_tracker.presentation.screens.heart_rate.Constants.MESSAGE_UPDATE_PULSE_TEXT
import com.teessideUni.cfs_tracker.presentation.screens.heart_rate.Constants.MESSAGE_UPDATE_REALTIME_TEXT
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.ceil
import kotlin.math.max

class OutputAnalyzer(private val activity: HeartRateMeasurement_Activity, private val mainHandler: Handler) {
    private var store: MeasureStore? = null
    private var detectedValleys = 0
    private var ticksPassed = 0
    private val valleys = CopyOnWriteArrayList<Long>()
    private var timer: CountDownTimer? = null

    private val measurementInterval = 45
    private val measurementLength = MEASUREMENT_LENGTH
    private val clipLength = 3500

    fun measurePulse(textureView: TextureView, cameraService: CameraService) {
        store = MeasureStore()
        detectedValleys = 0
        ticksPassed = 0
        valleys.clear()

        var fingerDetected = false // added variable to check if finger is detected
        var redPixelCount = 0 // added variable to count the number of red pixels

        timer = object : CountDownTimer(measurementLength.toLong(), measurementInterval.toLong()) {
            override fun onTick(millisUntilFinished: Long) {
                if (clipLength > ++ticksPassed * measurementInterval) {
                    return
                }

                mainHandler.obtainMessage(
                    MESSAGE_UPDATE_REALTIME_TEXT,
                    millisUntilFinished.toInt(),
                    0
                ).sendToTarget()

                Thread {

                    val currentBitmap = textureView.bitmap
                    val pixelCount = textureView.width * textureView.height
                    var measurement = 0
                    val pixels = IntArray(pixelCount)
                    currentBitmap?.getPixels(pixels, 0, textureView.width, 0, 0, textureView.width, textureView.height)

                    var redPixelThreshold = 0.99 * pixelCount // set threshold for red pixels

                    pixels.forEach {
                        val red = it shr 16 and 0xff
                        val green = it shr 8 and 0xff
                        val blue = it and 0xff

                        if (red >= green && red >= blue) {
                            redPixelCount++ // increment the count of red pixels
                        }

                        measurement += red // use only red channel for measurement
                    }

                    if (redPixelCount >= redPixelThreshold) {
                        fingerDetected = true // set finger detected to true if red pixel threshold is met
                    }

                    if (fingerDetected) { // proceed only if finger is detected
                        store?.add(measurement)

                        if (detectValley()) {
                            detectedValleys += 1
                            valleys.add(store!!.lastTimestamp.time)

                            val currentValue = String.format(
                                Locale.getDefault(),
                                activity.resources.getQuantityString(R.plurals.measurement_output_template, detectedValleys),
                                if (valleys.size == 1) 60f * detectedValleys / max(
                                    1f,
                                    (measurementLength - millisUntilFinished - clipLength) / 1000f
                                ) else 60f * (detectedValleys - 1) / max(
                                    1f,
                                    (valleys.last() - valleys.first()) / 1000f
                                ),
                                detectedValleys,
                                1f * (measurementLength - millisUntilFinished - clipLength) / 1000f
                            )

                            sendMessage(MESSAGE_UPDATE_PULSE_TEXT, currentValue)
                        }
                    } else {
                        // finger not detected, send message to UI
                        sendMessage(
                                MESSAGE_CAMERA_NOT_AVAILABLE,
                                "Finger not detected or camera preview does not have 99% of red or its variant color."
                            )

                        cameraService.stop()
                    }
                }.start()
            }

            override fun onFinish() {
                if (valleys.isEmpty()) {
                    sendMessage(
                            MESSAGE_CAMERA_NOT_AVAILABLE,
                            "No valleys detected - there may be an issue when accessing the camera."
                        )
                    return
                }
                val currentValue = String.format(
                    Locale.getDefault(),
                    activity.resources.getQuantityString(R.plurals.measurement_output_template, detectedValleys - 1),
                    60f * (detectedValleys - 1) / max(
                        1f,
                        (valleys.last() - valleys.first()) / 1000f
                    ),
                    detectedValleys - 1,
                    1f * (valleys.last() - valleys.first()) / 1000f
                )
                // Check if finger is still on the camera and preview is mostly red or its variant
                val currentBitmap = textureView.bitmap
                val pixelCount = textureView.width * textureView.height
                var redPixelCount = 0
                val pixels = IntArray(pixelCount)
                currentBitmap?.getPixels(pixels, 0, textureView.width, 0, 0, textureView.width, textureView.height)

                pixels.forEach {
                    val red = it shr 16 and 0xff
                    val green = it shr 8 and 0xff
                    val blue = it and 0xff

                    // Check if pixel is mostly red or its variant
                    if (red >= 0.99 * (red + green + blue)) {
                        redPixelCount++
                    }
                }

                val redPixelRatio = redPixelCount.toDouble() / pixelCount.toDouble()

                if (redPixelRatio < 0.99) {
                    sendMessage(MESSAGE_UPDATE_PULSE_TEXT, "Please keep your finger on the camera and ensure the preview is mostly red.")
                    cameraService.stop()
                }

                sendMessage(MESSAGE_UPDATE_PULSE_TEXT, currentValue)
                cameraService.stop()
            }
        }
        timer?.start()
    }

    private fun detectValley(): Boolean {
        val valleyDetectionWindowSize = 13
        val subList = store!!.getLastStdValues(valleyDetectionWindowSize)
        return if (subList.size < valleyDetectionWindowSize) {
            false
        } else {
            val referenceValue =
                subList[ceil((valleyDetectionWindowSize / 2f).toDouble()).toInt()].measurement
            for (measurement in subList) {
                if (measurement.measurement < referenceValue) return false
            }

            // filter out consecutive measurements due to too high measurement rate
            subList[ceil((valleyDetectionWindowSize / 2f).toDouble())
                .toInt()].measurement != subList[ceil((valleyDetectionWindowSize / 2f).toDouble())
                .toInt() - 1].measurement
        }
    }

    fun stop() {
        if (timer != null) {
            timer!!.cancel()
        }
    }

    fun sendMessage(what: Int, message: Any?) {
        val msg = Message()
        msg.what = what
        msg.obj = message
        mainHandler.sendMessage(msg)
    }
}

