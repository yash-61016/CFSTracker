package com.teessideUni.cfs_tracker.presentation.screens.heart_rate


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.Surface
import android.view.TextureView
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import com.teessideUni.cfs_tracker.R
import com.teessideUni.cfs_tracker.presentation.screens.heart_rate.Constants.MESSAGE_CAMERA_NOT_AVAILABLE
import com.teessideUni.cfs_tracker.presentation.screens.heart_rate.Constants.MESSAGE_UPDATE_REALTIME_TEXT
import com.teessideUni.cfs_tracker.presentation.screens.heart_rate.Constants.MESSAGE_UPDATE_PULSE_TEXT
import com.teessideUni.cfs_tracker.presentation.screens.heart_rate.Constants.REQUEST_CODE_CAMERA
import java.util.*

class HeartRateMeasurement_Activity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {
    private val viewModel by viewModels<MainHandlerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HeartRateMeasurementScreen(navController = NavController(this), viewModel = viewModel, onButtonClick = { this.onClickNewMeasurement()}, this, mainHandler )
        }
        ActivityCompat.requestPermissions(
            this, arrayOf<String>(Manifest.permission.CAMERA),
            REQUEST_CODE_CAMERA
        )
    }

    private var analyzer: OutputAnalyzer? = null
    private var justShared = false

    @SuppressLint("HandlerLeak")
    private val mainHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == MESSAGE_UPDATE_PULSE_TEXT) {
                viewModel.updatePulseText(msg.obj.toString())
            }
            if (msg.what == MESSAGE_UPDATE_REALTIME_TEXT) {
                val value = msg.arg1
                viewModel.updateRealTimeText(value/1000)
                Log.d("MyApp", value.toString())
            }
            if (msg.what == MESSAGE_CAMERA_NOT_AVAILABLE) {
                Log.println(Log.WARN, "camera", msg.obj.toString())
                viewModel.updatePulseText(msg.obj.toString())
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_CAMERA) {
            if (!(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this, "Camera permissions required. ", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onClickNewMeasurement() {
        analyzer = OutputAnalyzer(this, mainHandler)

        // clear prior results
        viewModel.updatePulseText("")
        viewModel.updateRealTimeText(0)

        setContent {
            HeartRateMeasurementScreen(navController = NavController(this), viewModel = viewModel, onButtonClick = { this.onClickNewMeasurement()}, this, mainHandler )
        }

        Log.d("MyApp", "onClickNewMeasurement() called")
    }
}

@Composable
fun CameraPreview(context: Context, mainHandler: Handler) {
    AndroidView(factory = { context ->
        TextureView(context).apply {
            // set up the texture view
            // e.g., setSurfaceTextureListener, setLayoutParams, etc.

            // start the camera service and output analyzer when the surface is ready
            surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                private var cameraService: CameraService? = null
                private var analyzer: OutputAnalyzer? = null
                private var previewSurface: Surface? = null

                override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
                    cameraService = CameraService(context as HeartRateMeasurement_Activity, mainHandler)
                    analyzer = OutputAnalyzer(context, mainHandler)
                    previewSurface = Surface(surfaceTexture)

                    if (!context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                        Toast.makeText(context, "No flash light available. ", Toast.LENGTH_SHORT).show()
                    }

                    cameraService?.start(previewSurface!!)
                    analyzer?.measurePulse(this@apply, cameraService!!)
                }

                override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
                    // handle size change if needed
                }

                override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                    // stop camera service and output analyzer if needed
                    analyzer?.stop()
                    cameraService?.stop()
                    return true
                }

                override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
                    // handle updates if needed
                }
            }
        }
    }, modifier = Modifier.fillMaxSize())
}


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HeartRateMeasurementScreen(navController: NavController, viewModel: MainHandlerViewModel, onButtonClick: () -> Unit, context: Context, handler: Handler) {
    Scaffold(
        topBar = { AppBar() },
        floatingActionButton = { NewMeasurementFab(onClickNewMeasurement = { onButtonClick() }) },
        content = { HeartRateMeasurementContent(viewModel = viewModel, context = context, mainHandler = handler) }
    )
}


@Composable
fun AppBar() {
    Box(modifier = Modifier.padding(top = 0.dp)) {
        TopAppBar(
            title = { Text(text = stringResource(id = R.string.app_name)) },
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = MaterialTheme.colors.onPrimary
        )
    }
}

@Composable
fun NewMeasurementFab(onClickNewMeasurement: () -> Unit) {
    FloatingActionButton(
        onClick = onClickNewMeasurement,
        backgroundColor = MaterialTheme.colors.secondary
    ) {
        Icon(Icons.Default.Add, contentDescription = stringResource(id = R.string.new_measurement))
    }
}

@Composable
fun HeartRateMeasurementContent(
    viewModel: MainHandlerViewModel,
    context: Context,
    mainHandler: Handler,
    cameraPreviewSize: Int = 600,
) {

    // Read the value of the State variables
    val pulseText = viewModel.pulseText.value
    val realTime by viewModel.realTimeText
    val realTimeInMillis = (viewModel.realTimeText.value * 1000).toLong()

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {

        val textViewModifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(top = 0.dp, bottom = 9.dp)
            .layoutId("textView")

        val textView2Modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(start = 50.dp, end = 50.dp, top = 0.dp, bottom = 10.dp)
            .layoutId("textView")
            .clip(RectangleShape)
            .border(1.dp, Color.Black, RectangleShape)

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            Text(
                modifier = textViewModifier,
                text = pulseText,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )

            Row(
                modifier = textView2Modifier,
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CircularCountdownLoader(
                    modifier = Modifier.size(50.dp),
                    timeInMillis = realTimeInMillis,
                    strokeWidth = 3.dp,
                    color = MaterialTheme.colors.secondary
                )

                Text(
                    modifier = Modifier.padding(start = 12.dp),
                    text = "Time Left: $realTime seconds",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .size(cameraPreviewSize.dp)
                    .padding(start = 0.dp, end = 0.dp, top = 40.dp, bottom = 0.dp)
                    .clip(RectangleShape)
                    .border(1.dp, Color.Black, RectangleShape)
            ) {
                CameraPreview(context = context, mainHandler = mainHandler)
            }
        }
    }
}



@Composable
fun CircularCountdownLoader(
    modifier: Modifier = Modifier,
    timeInMillis: Long,
    strokeWidth: Dp,
    color: Color
) {
    val animationSpec = remember { Animatable(0f) }
    val animatableValue = animationSpec.value.toInt()
    val arcAngle = (animatableValue / timeInMillis.toFloat()) * 360f

    LaunchedEffect(Unit) {
        animationSpec.animateTo(
            targetValue = timeInMillis.toFloat(),
            animationSpec = tween(durationMillis = timeInMillis.toInt())
        )
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            progress = 1f,
            modifier = Modifier.fillMaxSize(),
            strokeWidth = strokeWidth,
            color = color
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    drawArc(
                        color = color,
                        startAngle = 270f,
                        sweepAngle = -arcAngle,
                        useCenter = false,
                        style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
                    )
                }
        )
    }
}