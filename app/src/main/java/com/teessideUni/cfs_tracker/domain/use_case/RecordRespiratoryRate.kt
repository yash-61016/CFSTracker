package com.teessideUni.cfs_tracker.domain.use_case


import android.os.CountDownTimer
import android.util.Log
import com.teessideUni.cfs_tracker.domain.model.RespiratoryAccelerometerData
import com.teessideUni.cfs_tracker.domain.model.RespiratoryRateData
import com.teessideUni.cfs_tracker.domain.repository.RespiratoryRateRepository
import com.teessideUni.cfs_tracker.domain.util.LowPassFilter
import kotlin.math.pow
import kotlin.math.roundToInt


class RecordRespiratoryRateUseCase(private val respiratoryRateRepository: RespiratoryRateRepository) {
    private var sensorData = ArrayList<RespiratoryAccelerometerData>();
    private var rerspiratoryRate: Float = 0f;
    fun startRecording() {
        Log.d("tag", "I'm in usecase")
        val timer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                Log.d("I'm in the recordRepoRate", "Tick")
            }

            override fun onFinish() {
                Log.d("I'm in the recordRepoRate", "Finished with the timer")
                respiratoryRateRepository.stopListening()
                sensorData = respiratoryRateRepository.getData()
                Log.d("Finished listening to the sensor", sensorData.toString())
                val RRData = RespiratoryRateData(10f, System.currentTimeMillis(), sensorData);
                rerspiratoryRate = RRData.rateValue
                val filteredData = FloatArray(sensorData.size)
                val unFilteredData = DoubleArray(sensorData.size)
                val filter = LowPassFilter(alpha = 0.2f)

                for (i in sensorData.indices) {
                    val filteredValue = filter.filter(sensorData[i].z)
                    unFilteredData[i] = sensorData[i].z.toDouble()
                    filteredData[i] = filteredValue
                }
                val peakIndices = detectPeaks(filteredData, 70f)
                val interPeakIntervals = calculateInterPeakIntervals(peakIndices, 70f)
                val respiratoryRates = calculateRespiratoryRate(interPeakIntervals)
                Log.d("Unfiltered data", unFilteredData.toList().toString())
                Log.d("Filtered data", filteredData.toList().toString())
                Log.d("Peak Indices", peakIndices.toString())
                Log.d("Inter Peak Intervals", interPeakIntervals.toString())
                Log.d("Respiratory Rate", respiratoryRates.toString())
                Log.d("Respiratory Rate 2", (respiratoryRates/10).toString())
                val arr2: DoubleArray = computeMovingAverage(unFilteredData, unFilteredData.size, 3)
                val peaks = printPeaksTroughs(arr2, arr2.size)
                Log.d("Peak detection", peaks.toString())

            }
        }
        Log.d("I'm in the recordRepoRate", "I'm about to start listening")
        timer.start()
        respiratoryRateRepository.startListening()
    }

    fun getRespiratoryRate(): Float {
        return rerspiratoryRate
    }

    fun detectPeaks(filteredData: FloatArray, samplingRate: Float): List<Int> {
        // Differentiation
        val diffData = differentiate(filteredData)

        // Squaring
        val squaredData = square(diffData)

        // Moving Window Integration
        val mwIntegratedData = movingWindowIntegration(squaredData, samplingRate)

        // Find local maximums
        val peaks = mutableListOf<Int>()
        var isPeak = false
        var maxPeakIndex = 0
        var maxPeakValue = Float.MIN_VALUE

        for (i in 1 until mwIntegratedData.size - 1) {
            if (mwIntegratedData[i] > mwIntegratedData[i - 1] && mwIntegratedData[i] > mwIntegratedData[i + 1]) {
                if (!isPeak) {
                    isPeak = true
                    maxPeakIndex = i
                    maxPeakValue = mwIntegratedData[i]
                } else {
                    if (mwIntegratedData[i] > maxPeakValue) {
                        maxPeakIndex = i
                        maxPeakValue = mwIntegratedData[i]
                    }
                }
            } else if (isPeak && mwIntegratedData[i] < maxPeakValue * 0.5) { // Threshold for peak detection
                peaks.add(maxPeakIndex)
                isPeak = false
            }
        }

        return peaks
    }

    private fun differentiate(data: FloatArray): FloatArray {
        val diffData = FloatArray(data.size)

        for (i in 1 until data.size) {
            diffData[i] = data[i] - data[i - 1]
        }

        return diffData
    }

    private fun square(data: FloatArray): FloatArray {
        val squaredData = FloatArray(data.size)

        for (i in data.indices) {
            squaredData[i] = data[i] * data[i]
        }

        return squaredData
    }

    private fun movingWindowIntegration(data: FloatArray, samplingRate: Float): FloatArray {
        val windowSize = (0.1f * samplingRate).toInt() // 100 ms window (adjust as needed)
        val mwIntegratedData = FloatArray(data.size)

        for (i in data.indices) {
            val windowStart = maxOf(0, i - windowSize)
            val windowEnd = minOf(data.size - 1, i + windowSize)

            for (j in windowStart..windowEnd) {
                mwIntegratedData[i] += data[j]
            }
        }

        return mwIntegratedData
    }

    fun calculateInterPeakIntervals(peakIndices: List<Int>, samplingRate: Float): List<Float> {
        val interPeakIntervals = mutableListOf<Float>()

        // Sort the peak indices in ascending order
        val sortedPeakIndices = peakIndices.sorted()

        // Calculate the inter-peak intervals
        for (i in 1 until sortedPeakIndices.size) {
            val currentPeakIndex = sortedPeakIndices[i]
            val previousPeakIndex = sortedPeakIndices[i - 1]
            val timeInterval = (currentPeakIndex - previousPeakIndex) / samplingRate
            interPeakIntervals.add(timeInterval)
        }

        return interPeakIntervals
    }

    fun calculateRespiratoryRate(interPeakIntervals: List<Float>): Double {
        val respiratoryRates = mutableListOf<Float>()

        // Calculate the respiratory rate by taking the inverse of each inter-peak interval
        for (interval in interPeakIntervals) {
            val respiratoryRate = 60f / interval // Convert to breaths per minute
            val roundedRespiratoryRate =
                roundToDecimalPlaces(respiratoryRate, 2) // Round to 2 decimal places
            respiratoryRates.add(roundedRespiratoryRate)
        }

        return respiratoryRates.average()
    }

    private fun roundToDecimalPlaces(value: Float, decimalPlaces: Int): Float {
        val multiplier = 10f.pow(decimalPlaces)
        return (value * multiplier).roundToInt() / multiplier
    }

    private fun computeMovingAverage(arr: DoubleArray, N: Int, K: Int): DoubleArray {
        val arrNew = DoubleArray(N)
        var sum = 0.0
        for (i in 0 until K) {
            sum += arr[i]
            arrNew[i] = sum / (i + 1)
        }

        for (i in K until N) {
            sum += arr[i] - arr[i - K]
            arrNew[i] = sum / K
        }

        return arrNew
    }

    private fun printPeaksTroughs(arr: DoubleArray, N: Int): Int {
        var peaksTroughs = 0
        for (i in 1 until N - 1) {
            if (arr[i] > arr[i - 1] && arr[i] > arr[i + 1]) {
                peaksTroughs++
            } else if (arr[i] < arr[i - 1] && arr[i] < arr[i + 1]) {
                peaksTroughs++
            }
        }
        return peaksTroughs
    }
}