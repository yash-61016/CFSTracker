package com.teessideUni.cfs_tracker.presentation.ui.RespiratoryRate

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.teessideUni.cfs_tracker.data.remote.RespiratoryRateDataSourceImpl
import com.teessideUni.cfs_tracker.data.repository.RespiratoryRateRepositoryImpl
import com.teessideUni.cfs_tracker.domain.use_case.RecordRespiratoryRateUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RespiratoryRateScreen(respiratoryRateViewModel: RespiratoryRateViewModel) {
    var timerRunning by remember { mutableStateOf(false) }
    var remainingTime by remember { mutableStateOf(30) }

    val timerCoroutineScope = rememberCoroutineScope()

//    val sensorData by respiratoryRateViewModel.getSensorData().observeAsState(emptyList())

    var progress by remember { mutableStateOf(1f) }
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
        visibilityThreshold = 0.005f,
        finishedListener = {
            if(progress == 0f){
                progress = 1f
            }
        }
    )
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        "Respiratory Rate",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        },
    ) {
        it
        Column(
            modifier = Modifier.fillMaxSize().padding(top=150.dp, start=18.dp, end=20.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            // UI element to display the timer
            Text(
                text = "Place phone on your chest and breath",
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.requiredHeight(50.dp))
            CircularProgressIndicator(
                progress = animatedProgress,
                Modifier.fillMaxWidth().padding(horizontal = 50.dp),
                strokeWidth = 20.dp
            )
            Spacer(Modifier.requiredHeight(280.dp))
//            Text(
//                text = "Sensor data: $sensorData",
//                fontWeight = FontWeight.Bold,
//                fontSize = 20.sp,
//                textAlign = TextAlign.Center,
//                modifier = Modifier.padding(bottom = 16.dp)
//            )

            Card(
                onClick = {
                    respiratoryRateViewModel.startRecordingSensorData()
                    if (!timerRunning) {
                        timerRunning = true
                        startTimer(timerCoroutineScope) { remaining ->
                             if( remaining == 1){
                                 progress = 0f
                                 timerRunning = false
                            }else{
                                 progress = remaining.toFloat() / 30f
                            }

                        }
                    }
                },
                enabled = !timerRunning,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(10.dp)
            ) {
                Box(Modifier.fillMaxSize()) {
                    Text(
                        "Start",
                        Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }

        }

    }

}

private fun startTimer(coroutineScope: CoroutineScope, onTick: (Int) -> Unit) {
    coroutineScope.launch {
        var remainingTime = 30
        while (remainingTime > 0) {
            onTick(remainingTime)
            delay(1000)
            remainingTime--
        }
    }
}
@Preview(showBackground = true)
@Composable
fun RespiratoryRateScreenPreview() {
    val dataSrc = RespiratoryRateDataSourceImpl()
    val repo = RespiratoryRateRepositoryImpl(LocalContext.current, dataSrc)
    val useCase = RecordRespiratoryRateUseCase(repo)
    val viewModel = RespiratoryRateViewModel(useCase)

    RespiratoryRateScreen(viewModel)
}
