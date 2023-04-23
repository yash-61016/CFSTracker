package com.teessideUni.cfs_tracker.presentation.screens.heart_rate


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.TextureView

import android.view.View
import android.widget.EditText

import android.widget.TextView

import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar
import com.teessideUni.cfs_tracker.R
import com.teessideUni.cfs_tracker.presentation.screens.heart_rate.Constants.MENU_INDEX_EXPORT_DETAILS
import com.teessideUni.cfs_tracker.presentation.screens.heart_rate.Constants.MENU_INDEX_EXPORT_RESULT
import com.teessideUni.cfs_tracker.presentation.screens.heart_rate.Constants.MENU_INDEX_NEW_MEASUREMENT
import com.teessideUni.cfs_tracker.presentation.screens.heart_rate.Constants.MESSAGE_CAMERA_NOT_AVAILABLE
import com.teessideUni.cfs_tracker.presentation.screens.heart_rate.Constants.MESSAGE_UPDATE_FINAL
import com.teessideUni.cfs_tracker.presentation.screens.heart_rate.Constants.MESSAGE_UPDATE_REALTIME
import com.teessideUni.cfs_tracker.presentation.screens.heart_rate.Constants.REQUEST_CODE_CAMERA

import java.text.SimpleDateFormat
import java.util.*


class HeartRateMeasurement_Activity : Activity(), ActivityCompat.OnRequestPermissionsResultCallback {
    private var analyzer: OutputAnalyzer? = null
    private var justShared = false

    @SuppressLint("HandlerLeak")
    private val mainHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == MESSAGE_UPDATE_REALTIME) {
                (findViewById<View>(R.id.textView) as TextView).text = msg.obj.toString()
            }
            if (msg.what == MESSAGE_UPDATE_FINAL) {
                (findViewById<View>(R.id.editText) as EditText).setText(msg.obj.toString())

                val appMenu = (findViewById<View>(R.id.toolbar) as Toolbar).menu
                setViewState(View_State.SHOW_RESULTS)
            }
            if (msg.what == MESSAGE_CAMERA_NOT_AVAILABLE) {
                Log.println(Log.WARN, "camera", msg.obj.toString())
                (findViewById<View>(R.id.textView) as TextView).setText(
                    R.string.camera_not_found
                )
                analyzer!!.stop()
            }
        }
    }
    private val cameraService = CameraService(this, mainHandler)

    override fun onResume() {
        super.onResume()
        analyzer = OutputAnalyzer(this, findViewById(R.id.graphTextureView), mainHandler)
        val cameraTextureView = findViewById<TextureView>(R.id.textureView2)
        val previewSurfaceTexture = cameraTextureView.surfaceTexture

        // justShared is set if one clicks the share button.
        if (previewSurfaceTexture != null && !justShared) {
            val previewSurface = android.view.Surface(previewSurfaceTexture)
            if (!this.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                Snackbar.make(
                    findViewById(R.id.constraintLayout),
                    getString(R.string.noFlashWarning),
                    Snackbar.LENGTH_LONG
                ).show()
            }
            // hide the new measurement item while another one is in progress in order to wait
            // for the previous one to finish
            (findViewById<View>(R.id.toolbar) as Toolbar).menu.getItem(
                MENU_INDEX_NEW_MEASUREMENT
            ).isVisible = false
            cameraService.start(previewSurface)
            analyzer!!.measurePulse(cameraTextureView, cameraService)
        }
    }

    override fun onPause() {
        super.onPause()
        cameraService.stop()
        if (analyzer != null) analyzer!!.stop()
        analyzer = OutputAnalyzer(this, findViewById(R.id.graphTextureView), mainHandler)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ActivityCompat.requestPermissions(
            this, arrayOf<String>(Manifest.permission.CAMERA),
            REQUEST_CODE_CAMERA
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_CAMERA) {
            if (!(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Snackbar.make(
                    findViewById(R.id.constraintLayout),
                    getString(R.string.cameraPermissionRequired),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        Log.i("MENU", "menu is being prepared")
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return super.onPrepareOptionsMenu(menu)
    }

    fun setViewState(state: View_State?) {
        val appMenu = (findViewById<View>(R.id.toolbar) as Toolbar).menu
        when (state) {
            View_State.MEASUREMENT -> {
                appMenu.getItem(MENU_INDEX_NEW_MEASUREMENT).isVisible = false
                appMenu.getItem(MENU_INDEX_EXPORT_RESULT).isVisible = false
                appMenu.getItem(MENU_INDEX_EXPORT_DETAILS).isVisible = false
                findViewById<View>(R.id.floatingActionButton).visibility =
                    View.INVISIBLE
            }
            View_State.SHOW_RESULTS -> {
                findViewById<View>(R.id.floatingActionButton).visibility =
                    View.VISIBLE
                appMenu.getItem(MENU_INDEX_EXPORT_RESULT).isVisible = true
                appMenu.getItem(MENU_INDEX_EXPORT_DETAILS).isVisible = true
                appMenu.getItem(MENU_INDEX_NEW_MEASUREMENT).isVisible = true
            }
            else -> {}
        }
    }

    fun onClickNewMeasurement(item: MenuItem?) {
        onClickNewMeasurement()
    }

    fun onClickNewMeasurement(view: View?) {
        onClickNewMeasurement()
    }

    private fun onClickNewMeasurement() {
        analyzer = OutputAnalyzer(this, findViewById(R.id.graphTextureView), mainHandler)

        // clear prior results
        val empty = CharArray(0)
        (findViewById<View>(R.id.editText) as EditText).setText(empty, 0, 0)
        (findViewById<View>(R.id.textView) as TextView).setText(empty, 0, 0)

        setViewState(View_State.MEASUREMENT)
        val cameraTextureView = findViewById<TextureView>(R.id.textureView2)
        val previewSurfaceTexture = cameraTextureView.surfaceTexture
        if (previewSurfaceTexture != null) {
            val previewSurface = android.view.Surface(previewSurfaceTexture)
            cameraService.start(previewSurface)
            analyzer!!.measurePulse(cameraTextureView, cameraService)
        }
    }

    fun onClickExportResult(item: MenuItem?) {
        val intent = getTextIntent((findViewById<View>(R.id.textView) as TextView).text as String)
        justShared = true
        startActivity(Intent.createChooser(intent, getString(R.string.send_output_to)))
    }

    fun onClickExportDetails(item: MenuItem?) {
        val intent = getTextIntent((findViewById<View>(R.id.editText) as EditText).text.toString())
        justShared = true
        startActivity(Intent.createChooser(intent, getString(R.string.send_output_to)))
    }

    private fun getTextIntent(intentText: String): Intent {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(
            Intent.EXTRA_SUBJECT, String.format(
                getString(R.string.output_header_template),
                SimpleDateFormat(
                    getString(R.string.dateFormat),
                    Locale.getDefault()
                ).format(Date())
            )
        )
        intent.putExtra(Intent.EXTRA_TEXT, intentText)
        return intent
    }
}