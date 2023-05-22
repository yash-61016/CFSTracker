package com.teessideUni.cfs_tracker.presentation.screens.home_screen

import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.teessideUni.cfs_tracker.R
import com.teessideUni.cfs_tracker.data.local.HeartRateData
import com.teessideUni.cfs_tracker.data.local.HeartRatePoint
import com.teessideUni.cfs_tracker.domain.model.Resource
import com.teessideUni.cfs_tracker.domain.use_cases.view_models.homePageVM.ProfileViewModel
import com.teessideUni.cfs_tracker.presentation.screens.heart_rate.HeartRateMeasurementActivity
import com.teessideUni.cfs_tracker.presentation.screens.home_screen.components.heart_rate_graph.BarChart
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(navController: NavController, viewModel: ProfileViewModel = hiltViewModel()) {

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { _ ->
    }
    var heartRateAllWeekDataList by remember { mutableStateOf<List<HeartRateData>>(emptyList()) }
    var heartRateDataPointList by remember { mutableStateOf<List<List<HeartRatePoint>>>(emptyList()) }
    var isHeartRateSelected by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(false) }
    var weekNumber = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.getCurrentWeekData().collect { result ->
            when (result) {
                is Resource.Success -> {
                    val data = result.data ?: emptyList()
                    isLoading = false
                    // Clear the heartRateAllWeekDataList before updating it
                    heartRateAllWeekDataList = emptyList()
                    if (data.isNotEmpty()) {
                        // Update the heartRateDataPoint list
                        heartRateAllWeekDataList = data
                        heartRateDataPointList = viewModel.filterMaxMinHeartRatePerDay(data)
                        weekNumber.value = viewModel.getCurrentWeekNumber()
                    }
                }
                is Resource.Error -> {
                   Toast.makeText(context, "Failed to connect to database", Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    // Handle loading state
                    isLoading = true
                }
            }
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    // Disable back press on home screen
    DisposableEffect(lifecycleOwner, onBackPressedDispatcher) {
        val callback = object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                // Do nothing to disable back press
            }
        }
        onBackPressedDispatcher?.addCallback(lifecycleOwner, callback)
        onDispose {
            callback.remove()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .systemBarsPadding(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Home Page: CFS Tracker",
                color = Color.Black,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            Column(
                modifier = Modifier.padding(top = 20.dp ,start = 0.dp, end = 15.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                          viewModel.logout()
                        } },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Logout",
                        tint = Color.Black,
                        modifier = Modifier
                            .size(34.dp)
                            .padding(top = 8.dp)
                    )
                }
                Text(
                    text = "Logout",
                    color = Color.Black,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            viewModel.getPreviousWeekData().collect { result ->
                                when (result) {
                                    is Resource.Success -> {
                                        val data = result.data ?: emptyList()
                                        isLoading = false
                                        // Clear the heartRateAllWeekDataList before updating it
                                        heartRateAllWeekDataList = emptyList()
                                        weekNumber.value = viewModel.getPreviousWeekNumber()
                                        heartRateDataPointList = if (data.isNotEmpty()) {
                                            // Update the heartRateDataPoint list
                                            viewModel.filterMaxMinHeartRatePerDay(data)
                                        } else {
                                            emptyList()
                                        }
                                    }
                                    is Resource.Error -> {
                                        Toast.makeText(context, "Failed to connect to database", Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                    is Resource.Loading -> {
                                        // Handle loading state
                                        isLoading = true
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .height(40.dp)
                        .padding(end = 4.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(text = "Last Week", color = MaterialTheme.colorScheme.onPrimary)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        coroutineScope.launch {
                            viewModel.getCurrentWeekData().collect { result ->
                                when (result) {
                                    is Resource.Success -> {
                                        val data = result.data ?: emptyList()
                                        Log.d("reached in this week call", "reach here in this week call")

                                        // Clear the heartRateAllWeekDataList before updating it
                                        heartRateAllWeekDataList = emptyList()
                                        weekNumber.value = viewModel.getCurrentWeekNumber()
                                        heartRateDataPointList = if (data.isNotEmpty()) {
                                            // Update the heartRateDataPoint List
                                            viewModel.filterMaxMinHeartRatePerDay(data)
                                        } else {
                                            emptyList()
                                        }
                                        isLoading = false
                                    }
                                    is Resource.Error -> {
                                        Toast.makeText(context, "Failed to connect to database", Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                    is Resource.Loading -> {
                                        // Handle loading state
                                        isLoading = true
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(text = "This Week", color = MaterialTheme.colorScheme.onPrimary)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = if (isHeartRateSelected) "Heart Rate" else "Respiratory Rate",
                color = Color.Black,
                fontSize = 14.sp,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 13.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Switch(
                checked = isHeartRateSelected,
                onCheckedChange = { isHeartRateSelected = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    uncheckedThumbColor = if (isHeartRateSelected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                    uncheckedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                ),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 13.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(horizontal = 16.dp)
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(16.dp),
                    clip = true
                )
        ) {
            Card(
                modifier = Modifier.fillMaxSize(),
                colors = CardDefaults.cardColors(Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(start = 20.dp, top = 10.dp, end = 13.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "HEART RATE",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                    Text(
                        text = weekNumber.value,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                }

                if (isHeartRateSelected) {

                    if(heartRateDataPointList.isEmpty()){
                        // Display the "No data found" image
                        Image(
                            painter = painterResource(R.drawable.no_data_found),
                            contentDescription = "No data found",
                            modifier = Modifier
                                    .padding(start = 40.dp, end = 20.dp)
                        )
                    }else{
                        BarChart(
                            data = heartRateDataPointList,
                            modifier = Modifier.fillMaxSize(),
                            isLoading = isLoading
                        )
                    }
                } else {
                    // TODO Respiratory data graph
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CardButton(
                onClick = {
                    //  Create an Intent to start the activity
                    val intent = Intent(context, HeartRateMeasurementActivity::class.java)
                    launcher.launch(intent)
                },
                text = "Heart Rate \n Measurement",
                iconResId =  R.drawable.heart ,
                backgroundColor = MaterialTheme.colorScheme.primary,
            )

            CardButton(
                onClick = {
                    navController.navigate("heart_rate_report_page") {
                    popUpTo(navController.graph.findStartDestination().id) {
                        inclusive = true
                    }
                    launchSingleTop = true
                 }
                },
                text = "Heart Rate \n Report",
                iconResId =  R.drawable.file ,
                backgroundColor = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardButton(
    onClick: () -> Unit,
    text: String,
    iconResId: Int,
    backgroundColor: Color
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .width(150.dp)
            .height(150.dp)
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        colors =  CardDefaults.cardColors(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(iconResId),
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                contentScale = ContentScale.Fit
            )
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp),
                maxLines = 2, // Set the maximum number of lines for text wrapping
                overflow = TextOverflow.Ellipsis // Add ellipsis for long texts
            )
        }
    }
}
