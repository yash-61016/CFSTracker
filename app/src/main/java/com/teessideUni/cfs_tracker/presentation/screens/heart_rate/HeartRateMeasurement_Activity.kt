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
import android.view.Surface
import android.view.TextureView
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import com.teessideUni.cfs_tracker.R
import com.teessideUni.cfs_tracker.presentation.screens.heart_rate.Constants.FINGER_NOT_DETECTED
import com.teessideUni.cfs_tracker.presentation.screens.heart_rate.Constants.MESSAGE_UPDATE_REALTIME_TEXT
import com.teessideUni.cfs_tracker.presentation.screens.heart_rate.Constants.MESSAGE_UPDATE_PULSE_TEXT
import com.teessideUni.cfs_tracker.presentation.screens.heart_rate.Constants.REQUEST_CODE_CAMERA
import com.teessideUni.cfs_tracker.presentation.screens.heart_rate.Constants.UPDATED_MESSAGE
import java.util.*

class HeartRateMeasurement_Activity : AppCompatActivity() {
    private val viewModel by viewModels<MainHandlerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HeartRateMeasurementScreen(navController = NavController(this), viewModel = viewModel, onButtonClick = { this.onClickNewMeasurement()}, this, mainHandler )
        }
    }

    private var analyzer: OutputAnalyzer? = null

    @SuppressLint("HandlerLeak")
    private val mainHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            //Handle Pulse Rate Text
            if (msg.what == MESSAGE_UPDATE_PULSE_TEXT) {
                viewModel.updatePulseText(msg.obj.toString())
            }

            //Handle Timer Text
            if (msg.what == MESSAGE_UPDATE_REALTIME_TEXT) {
                val value = msg.arg1
                if( viewModel.pulseText.toString() == FINGER_NOT_DETECTED){
                    viewModel.updateRealTimeText(0)
                }
                viewModel.updateRealTimeText(value/1000)
            }

            //Handle MessageText
            if (msg.what == UPDATED_MESSAGE) {
                viewModel.updateMessageText(msg.obj.toString())
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
        viewModel.updateMessageText("")
        viewModel.updateRealTimeText(0)

        setContent {
            HeartRateMeasurementScreen(navController = NavController(this), viewModel = viewModel, onButtonClick = { this.onClickNewMeasurement()}, this, mainHandler )
        }
    }
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

    val retryIcon: Painter = painterResource(id = R.drawable.retry)

    FloatingActionButton(
        onClick = onClickNewMeasurement,
        backgroundColor = MaterialTheme.colors.primary
    ) {
        Icon(retryIcon, contentDescription = stringResource(id = R.string.new_measurement))
    }
}



@Composable
fun CameraPreview(context: Context, mainHandler: Handler) {
    var isCameraServiceRunning by remember { mutableStateOf(false) }

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
                    isCameraServiceRunning = true
                }

                override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
                    // handle size change if needed
                }

                override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                    // stop camera service and output analyzer if needed
                    analyzer?.stop()
                    cameraService?.stop()
                    isCameraServiceRunning = false
                    return true
                }

                override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
                    // handle updates if needed
                }
            }
        }
    }, modifier = Modifier.fillMaxSize())

    if (isCameraServiceRunning) {
        PulseLoading()
    }
}

@Composable
fun PulseLoading(
    durationMillis:Int = 1000,
    maxPulseSize:Float = 300f,
    minPulseSize:Float = 50f,
    pulseColor:Color = Color(234,240,246),
    centreColor:Color = Color.Red,
){

    val infiniteTransition = rememberInfiniteTransition()
    val size by infiniteTransition.animateFloat(
        initialValue = minPulseSize,
        targetValue = maxPulseSize,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    Box(contentAlignment = Alignment.Center,modifier = Modifier.fillMaxSize()) {
        Card(
            shape = CircleShape,
            modifier = Modifier.size(size.dp).align(Alignment.Center).alpha(alpha),
            backgroundColor = pulseColor,
            elevation = 0.dp
        ) {}
        Card(modifier = Modifier
            .size(minPulseSize.dp)
            .align(Alignment.Center),
            shape = CircleShape,
            backgroundColor = centreColor){}
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
    var messageText = viewModel.messageText.value
    if (messageText.isNullOrEmpty() || messageText == "") {
        messageText = "Please place your finger on camera to begin pulse rate measurement."
    }
    val realTime by viewModel.realTimeText

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {

        val PulseTextmodifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(top = 30.dp, bottom = 9.dp)
            .layoutId("textView")

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            Text(
                modifier = Modifier.padding(top = 10.dp ,start = 12.dp),
                text = "Time Left: $realTime seconds",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                textAlign = TextAlign.Center
            )

            Text(
                modifier = PulseTextmodifier,
                text = pulseText,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(start = 20.dp, end = 40.dp, top = 0.dp, bottom = 10.dp)
                    .layoutId("textView"),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.info_icon),
                    contentDescription = "Information Icon",
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .size(26.dp)
                )

                AnimatedVisibility(
                    visible = messageText.isNotEmpty(),
                    enter = fadeIn(
                        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
                    ) + expandVertically(),
                    exit = fadeOut(
                        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
                    ) + shrinkVertically()
                ) {
                    Text(
                        modifier = Modifier.padding(start = 10.dp),
                        text = messageText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .size(cameraPreviewSize.dp)
                    .padding(start = 0.dp, end = 0.dp, top = 10.dp, bottom = 0.dp)
            ) {

                CameraPreview(context = context, mainHandler = mainHandler)
            }
        }
    }
}



