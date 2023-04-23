package com.teessideUni.cfs_tracker.presentation.screens.heart_rate

import android.os.CountDownTimer
import android.os.Handler
import android.os.Message
import android.view.TextureView
import com.teessideUni.cfs_tracker.R
import com.teessideUni.cfs_tracker.presentation.screens.heart_rate.Constants.MESSAGE_CAMERA_NOT_AVAILABLE
import com.teessideUni.cfs_tracker.presentation.screens.heart_rate.Constants.MESSAGE_UPDATE_FINAL
import com.teessideUni.cfs_tracker.presentation.screens.heart_rate.Constants.MESSAGE_UPDATE_REALTIME
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.ceil

internal class OutputAnalyzer(
    private val activity: HeartRateMeasurement_Activity,
    graphTextureView: TextureView?,
    mainHandler: Handler
) {
    private val chartDrawer: ChartDrawer
    private var store: MeasureStore? = null
    private val measurementInterval = 45
    private val measurementLength = 15000
    private val clipLength = 3500
    private var detectedValleys = 0
    private var ticksPassed = 0
    private val valleys = CopyOnWriteArrayList<Long>()
    private var timer: CountDownTimer? = null
    private val mainHandler: Handler

    init {
        chartDrawer = ChartDrawer(graphTextureView!!)
        this.mainHandler = mainHandler
    }

    fun measurePulse(textureView: TextureView, cameraService: CameraService) {

        store = MeasureStore()
        detectedValleys = 0
        timer = object : CountDownTimer(measurementLength.toLong(), measurementInterval.toLong()) {
            override fun onTick(millisUntilFinished: Long) {

                if (clipLength > ++ticksPassed * measurementInterval) return
                val thread = Thread {
                    val currentBitmap = textureView.bitmap
                    val pixelCount = textureView.width * textureView.height
                    var measurement = 0
                    val pixels = IntArray(pixelCount)
                    currentBitmap!!.getPixels(
                        pixels,
                        0,
                        textureView.width,
                        0,
                        0,
                        textureView.width,
                        textureView.height
                    )

                    for (pixelIndex in 0 until pixelCount) {
                        measurement += pixels[pixelIndex] shr 16 and 0xff
                    }

                    store!!.add(measurement)
                    if (detectValley()) {
                        detectedValleys += 1
                        valleys.add(store!!.lastTimestamp.time)

                        val currentValue = String.format(
                            Locale.getDefault(),
                            activity.resources.getQuantityString(
                                R.plurals.measurement_output_template,
                                detectedValleys
                            ),
                            if (valleys.size == 1) 60f * detectedValleys / Math.max(
                                1f,
                                (measurementLength - millisUntilFinished - clipLength) / 1000f
                            ) else 60f * (detectedValleys - 1) / Math.max(
                                1f,
                                (valleys[valleys.size - 1] - valleys[0]) / 1000f
                            ),
                            detectedValleys,
                            1f * (measurementLength - millisUntilFinished - clipLength) / 1000f
                        )
                        sendMessage(MESSAGE_UPDATE_REALTIME, currentValue)
                    }

                    // draw the chart on a separate thread.
                    val chartDrawerThread = Thread {
                        chartDrawer.draw(
                            store!!.stdValues
                        )
                    }
                    chartDrawerThread.start()
                }
                thread.start()
            }

            override fun onFinish() {
                val stdValues = store!!.stdValues
                if (valleys.size == 0) {
                    mainHandler.sendMessage(
                        Message.obtain(
                            mainHandler,
                            MESSAGE_CAMERA_NOT_AVAILABLE,
                            "No valleys detected - there may be an issue when accessing the camera."
                        )
                    )
                    return
                }
                val currentValue = String.format(
                    Locale.getDefault(),
                    activity.resources.getQuantityString(
                        R.plurals.measurement_output_template,
                        detectedValleys - 1
                    ),
                    60f * (detectedValleys - 1) / Math.max(
                        1f,
                        (valleys[valleys.size - 1] - valleys[0]) / 1000f
                    ),
                    detectedValleys - 1,
                    1f * (valleys[valleys.size - 1] - valleys[0]) / 1000f
                )
                sendMessage(MESSAGE_UPDATE_REALTIME, currentValue)
                val returnValueSb = StringBuilder()
                returnValueSb.append(currentValue)
                returnValueSb.append(activity.getString(R.string.row_separator))
                returnValueSb.append(activity.getString(R.string.raw_values))
                returnValueSb.append(activity.getString(R.string.row_separator))
                for (stdValueIdx in stdValues.indices) {
                    val value = stdValues[stdValueIdx]
                    val timeStampString = SimpleDateFormat(
                        activity.getString(R.string.dateFormatGranular),
                        Locale.getDefault()
                    ).format(value.timestamp)
                    returnValueSb.append(timeStampString)
                    returnValueSb.append(activity.getString(R.string.separator))
                    returnValueSb.append(value.measurement)
                    returnValueSb.append(activity.getString(R.string.row_separator))
                }
                returnValueSb.append(activity.getString(R.string.output_detected_peaks_header))
                returnValueSb.append(activity.getString(R.string.row_separator))

                // add detected valleys location
                for (tick in valleys) {
                    returnValueSb.append(tick)
                    returnValueSb.append(activity.getString(R.string.row_separator))
                }
                sendMessage(MESSAGE_UPDATE_FINAL, returnValueSb.toString())
                cameraService.stop()
            }
        }
        (timer as CountDownTimer).start()
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