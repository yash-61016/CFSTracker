//package com.teessideUni.cfs_tracker.domain.use_case
//
//import android.content.Context.SENSOR_SERVICE
//import android.content.Intent
//import android.hardware.Sensor
//import android.hardware.SensorEvent
//import android.hardware.SensorEventListener
//import android.hardware.SensorManager
//import android.os.Bundle
//import android.os.CountDownTimer
//import android.util.Log
//import android.widget.TextView
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.content.ContextCompat.getSystemService
//import java.util.Arrays
//
//class Accelerometer : AppCompatActivity(), SensorEventListener {
//    var sensorManager: SensorManager? = null
//    var xList = ArrayList<Float>()
//    var yList = ArrayList<Float>()
//    var zList = ArrayList<Float>()
//    var ready = true
//    protected fun onCreate(savedInstanceState: Bundle?) {
//        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager?
//        sensorManager!!.registerListener(
//            this,
//            sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
//            SensorManager.SENSOR_DELAY_NORMAL
//        )
//        val timer: TextView = findViewById(R.id.timer)
//        object : CountDownTimer(30000, 800) {
//            override fun onTick(millisUntilFinished: Long) {
//                timer.text = java.lang.Long.toString(millisUntilFinished / 1000) + "s"
//                ready = true
//                // logic to set the EditText could go here
//            }
//
//            override fun onFinish() {
//                timer.text = "0s"
//                ready = false
//                Log.i("helloz", zList.toString())
//                val arr = DoubleArray(zList.size)
//                for (i in zList.indices) {
//                    arr[i] = zList[i].toDouble()
//                }
//                val arr2 = ComputeMovingAverage(arr, arr.size, 3)
//
//                //double arr3[] = ComputeMovingAverage(arr2, arr2.length, 3);
//                val peaks = printPeaksTroughs(arr2, arr2.size)
//                Log.i("hellopeaks", Integer.toString(peaks * 2))
//                var sum = 0.0
//                for (i in arr.indices) {
//                    sum += arr[i]
//                }
//                val avg = sum / arr.size
//                var sum2 = 0.0
//                for (i in arr.indices) {
//                    sum2 += Math.pow(arr[i] - avg, 2.0)
//                }
//                val `var` = sum2 / (arr.size - 1)
//                val sd = Math.sqrt(`var`)
//                Log.i("hellosd", Arrays.toString(arr))
//                Log.i("hellosd", java.lang.Double.toString(sd))
//                val intent = Intent(this@Accelerometer, ResultActivity::class.java)
//                intent.putExtra("name", "Respiratory Rate")
//                intent.putExtra("score", if (sd < 0.035) -1 else peaks * 2)
//                intent.putExtra("normal", "12 - 16")
//                startActivity(intent)
//                finish()
//            }
//        }.start()
//    }
//
//    override fun onSensorChanged(sensorEvent: SensorEvent) {
//        if (sensorEvent.sensor.type == Sensor.TYPE_ACCELEROMETER) {
//            if (ready) {
//                ready = false
//                //                xList.add(sensorEvent.values[0]);
////                yList.add(sensorEvent.values[1]);
//                zList.add(sensorEvent.values[2])
//
////                Log.i("hellox", xList.toString());
////                Log.i("helloy", yList.toString());
////                Log.i("helloz", zList.toString());
//            }
//        }
//    }
//
//    override fun onAccuracyChanged(sensor: Sensor, i: Int) {}
//    fun ComputeMovingAverage(arr: DoubleArray, N: Int, K: Int): DoubleArray {
//        val arr_new = DoubleArray(N)
//        var i: Int
//        var sum = 0f
//        i = 0
//        while (i < K) {
//            sum += arr[i].toFloat()
//            arr_new[i] = (sum / K).toDouble()
//            i++
//        }
//        i = K
//        while (i < N) {
//            sum -= arr[i - K].toFloat()
//            sum += arr[i].toFloat()
//            arr_new[i] = (sum / K).toDouble()
//            i++
//        }
//        return arr_new
//    }
//
//    fun isPeak(arr: DoubleArray, n: Int, num: Double, i: Int, j: Int): Boolean {
//        if (i >= 0 && arr[i] > num) return false
//        return if (j < n && arr[j] > num) false else true
//    }
//
//    fun printPeaksTroughs(arr: DoubleArray, n: Int): Int {
//        var count = 0
//        //System.out.print("Peaks : ");
//        for (i in 0 until n) {
//            if (isPeak(arr, n, arr[i], i - 1, i + 1)) {
//                //System.out.println(arr[i]);
//                count++
//            }
//        }
//        return count
//    }
//}