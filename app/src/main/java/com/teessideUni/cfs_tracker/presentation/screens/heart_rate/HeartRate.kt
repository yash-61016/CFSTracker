import android.annotation.SuppressLint
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.TextureView
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.teessideUni.cfs_tracker.R
import com.teessideUni.cfs_tracker.presentation.screens.heart_rate.*
import com.teessideUni.cfs_tracker.presentation.screens.heart_rate.CameraService
import com.teessideUni.cfs_tracker.presentation.screens.heart_rate.OutputAnalyzer


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HeartRateMeasurementScreen(navController: NavController) {
    Scaffold(
        topBar = { AppBar() },
        floatingActionButton = { NewMeasurementFab() },
        content = { HeartRateMeasurementContent() }
    )
}

@Composable
fun AppBar() {
    Box(modifier = Modifier.padding(top = 49.dp)) {
        TopAppBar(
            title = { Text(text = stringResource(id = R.string.app_name)) },
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = MaterialTheme.colors.onPrimary
        )
    }
}

@Composable
fun NewMeasurementFab() {
    FloatingActionButton(
        onClick = { /* Handle new measurement click */ },
        backgroundColor = MaterialTheme.colors.secondary
    ) {
        Icon(Icons.Default.Add, contentDescription = stringResource(id = R.string.new_measurement))
    }
}

@Composable
fun HeartRateMeasurementContent() {


    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {


        val textureView2Modifier = Modifier
            .layoutId("textureView2")
            .size(100.dp)
            .height(400.dp)
            .padding(top = 10.dp, start = 10.dp, end = 10.dp, bottom = 10.dp)
            .clip(RectangleShape)
            .border(1.dp, Color.Black, RectangleShape)
            .offset(y = 66.dp)

        val textViewModifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .width(50.dp)
            .padding(top = 110.dp)
            .layoutId("textView")
            .clip(RectangleShape)
            .border(1.dp, Color.Black, RectangleShape)

        val scrollViewModifier = Modifier
            .fillMaxSize()
            .layoutId("scrollView")

        Spacer(modifier = Modifier.height(16.dp))
        FloatingActionButton(
            onClick = { /* Handle new measurement click */ },
            backgroundColor = MaterialTheme.colors.secondary
        ) {
            Icon(Icons.Default.Add, contentDescription = stringResource(id = R.string.new_measurement))
        }

        AndroidView(
            modifier = textureView2Modifier,
            factory = { context ->
                TextureView(context).apply {
                    // Set up texture view as needed
                }
            },
            update = { view ->
                // Update texture view as needed
            }
        )

        Text(
            modifier = textViewModifier,
            text = "Some Text",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            textAlign = TextAlign.Center
        )

        LazyColumn(
            modifier = scrollViewModifier,
        ) {
            item {

                OutlinedTextField(
                    value = stringResource(id = R.string.output_hint),
                    onValueChange = { it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(900.dp) // Set the height to 64dp
                        .padding(top = 400.dp), // Add 16dp padding to the top
                    enabled = false,
                    placeholder = { Text(text = stringResource(id = R.string.output_hint)) },
                    singleLine = false,
                    textStyle = TextStyle(textAlign = TextAlign.Center)
                )
            }
        }
    }
}




