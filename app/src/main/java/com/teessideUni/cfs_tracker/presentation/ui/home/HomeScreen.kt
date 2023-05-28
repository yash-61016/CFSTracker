package com.teessideUni.cfs_tracker.presentation.ui.home

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.teessideUni.cfs_tracker.R
import com.teessideUni.cfs_tracker.data.local.HeartRateData
import com.teessideUni.cfs_tracker.data.local.HeartRatePoint
import com.teessideUni.cfs_tracker.domain.model.Resource
import com.teessideUni.cfs_tracker.domain.use_cases.view_models.homePageVM.ProfileViewModel
import com.teessideUni.cfs_tracker.presentation.screens.heart_rate.HeartRateMeasurementActivity
import com.teessideUni.cfs_tracker.presentation.screens.home_screen.components.heart_rate_graph.BarChart
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(navController: NavController) {
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    )
    val viewModel: ProfileViewModel = hiltViewModel()
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }
    var weekNumber = remember { mutableStateOf("") }
    var heartRateAllWeekDataList by remember { mutableStateOf<List<HeartRateData>>(emptyList()) }
    var heartRateDataPointList by remember { mutableStateOf<List<List<HeartRatePoint>>>(emptyList()) }
    val scope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { _ ->
    }
    LaunchedEffect(Unit) {
        isLoading = true
        viewModel.getCurrentWeekData().collect { result ->
            when (result) {
                is Resource.Success -> {
                    val data = result.data ?: emptyList()
                    // Clear the heartRateAllWeekDataList before updating it
                    heartRateAllWeekDataList = emptyList()
                    if (data.isNotEmpty()) {
                        // Update the heartRateDataPoint list
                        heartRateAllWeekDataList = data
                        heartRateDataPointList = viewModel.filterMaxMinHeartRatePerDay(data)
                        weekNumber.value = viewModel.getCurrentWeekNumber()
                    }
                    isLoading = false
                }

                is Resource.Error -> {
                    Toast.makeText(context, "Error loading Heart Rate Data", Toast.LENGTH_SHORT)
                        .show()
                }

                else -> {}
            }
        }
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetShape = MaterialTheme.shapes.large.copy(
            bottomEnd = CornerSize(0.dp),
            bottomStart = CornerSize(0.dp)
        ),
        sheetContent = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(top = 10.dp, bottom = 10.dp)
            ) {
                Card(
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                        }
                        val intent = Intent(context, HeartRateMeasurementActivity::class.java)
                        launcher.launch(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .padding(10.dp)
                ) {
                    Box(Modifier.fillMaxSize()) {
                        Text(
                            "Heart Rate",
                            Modifier.align(Alignment.Center),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
                Card(
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                        }
                        navController.navigate("RESPIRATORY_RATE_RECORDER");
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .padding(10.dp)
                ) {
                    Box(Modifier.fillMaxSize()) {
                        Text(
                            "Respiratory Rate",
                            Modifier.align(Alignment.Center),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }
        },
        sheetBackgroundColor = MaterialTheme.colorScheme.onPrimary,
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text("CFS Tracker")
                    }
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = {
                        Log.d("I'm in the home screen", "I clicked the Fab")
                        scope.launch {
                            if (sheetState.isVisible) {
                                sheetState.hide()
                            } else {
                                sheetState.show()
                            }
                        }
                    },
                    icon = { Icon(Icons.Filled.Add, "Localized description") },
                    text = { Text(text = "Read Values") },
                    elevation = FloatingActionButtonDefaults.elevation(0.dp)
                )
            }
        ) {
            it
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 110.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier.padding(8.dp),
                    shape = MaterialTheme.shapes.small,
                    elevation = CardDefaults.cardElevation()
                ) {
                    CardContent(
                        weekNumber = weekNumber.value,
                        DataPointList = heartRateDataPointList,
                        isLoading = isLoading,
                        "Heart Rate",
                        getPreviousWeekData = {
                            scope.launch {
                                isLoading = true
                                viewModel.getPreviousWeekData().collect { result ->
                                    when (result) {
                                        is Resource.Success -> {
                                            val data = result.data ?: emptyList()
                                            // Clear the heartRateAllWeekDataList before updating it
                                            heartRateAllWeekDataList = emptyList()
                                            weekNumber.value = viewModel.getPreviousWeekNumber()
                                            heartRateDataPointList = if (data.isNotEmpty()) {
                                                // Update the heartRateDataPoint list
                                                viewModel.filterMaxMinHeartRatePerDay(data)
                                            } else {
                                                emptyList()
                                            }
                                            isLoading = false
                                        }

                                        is Resource.Error -> {
                                            Toast.makeText(
                                                context,
                                                "Failed to connect to database",
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()
                                        }

                                        else -> {}
                                    }
                                }
                            }
                        },
                        getCurrentWeekData = {
                            scope.launch {
                                isLoading = true
                                viewModel.getCurrentWeekData().collect { result ->
                                    when (result) {
                                        is Resource.Success -> {
                                            val data = result.data ?: emptyList()
                                            Log.d(
                                                "reached in this week call",
                                                "reach here in this week call"
                                            )
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
                                            Toast.makeText(
                                                context,
                                                "Failed to connect to database",
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()
                                        }

                                        else -> {}
                                    }
                                }
                            }
                        }
                    )
                }

                Card(
                    modifier = Modifier.padding(8.dp),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation()
                ) {
//                    CardContent(
//                        title = "Respiratory Rate",
//                        text = "The graph for respiratory rate will come here"
//                    )
                }
            }
        }
    }
}

@Composable
fun OptionItem(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 16.dp)
    )
}

@Composable
fun CardContent(
    weekNumber: String,
    DataPointList: List<List<HeartRatePoint>>,
    isLoading: Boolean,
    title: String,
    getPreviousWeekData: () -> Unit,
    getCurrentWeekData: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .padding(start = 20.dp, top = 10.dp, end = 20.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                Text(
                    text = weekNumber,
                    style= MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(top=10.dp)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = getPreviousWeekData,
                    modifier = Modifier
                        .height(40.dp)
                        .weight(1f)
                        .padding(end = 4.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    )
                ) {
                    Text(text = "Last Week", color = MaterialTheme.colorScheme.onPrimary)
                }

                Button(
                    onClick = getCurrentWeekData,
                    modifier = Modifier
                        .height(40.dp)
                        .weight(1f),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    )
                ) {
                    Text(text = "This Week", color = MaterialTheme.colorScheme.onPrimary)
                }
            }

            if (DataPointList.isEmpty()) {
                // Display the "No data found" image
                Image(
                    painter = painterResource(R.drawable.no_data_found),
                    contentDescription = "No data found",
                    modifier = Modifier
                        .padding(start = 40.dp, end = 20.dp)
                )
            } else {
                BarChart(
                    data = DataPointList,
                    modifier = Modifier.fillMaxSize(),
                    isLoading = isLoading
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(rememberNavController())
}
