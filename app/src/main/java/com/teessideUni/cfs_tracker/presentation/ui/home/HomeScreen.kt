package com.teessideUni.cfs_tracker.presentation.ui.home

import android.content.Intent
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
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
import androidx.compose.runtime.derivedStateOf
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
import com.teessideUni.cfs_tracker.data.local.RespiratoryRateDataValues
import com.teessideUni.cfs_tracker.data.local.RespiratoryRatePoint
import com.teessideUni.cfs_tracker.domain.model.Resource
import com.teessideUni.cfs_tracker.domain.use_cases.view_models.homePageVM.ProfileViewModel
import com.teessideUni.cfs_tracker.presentation.screens.heart_rate.HeartRateMeasurementActivity
import com.teessideUni.cfs_tracker.presentation.ui.home.components.heart_rate_graph.HeartRateDataGraph
import com.teessideUni.cfs_tracker.presentation.ui.home.components.respiratory_rate_graph.RespiratoryRateDataGraph
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(navController: NavController) {
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    )
    val viewModel: ProfileViewModel = hiltViewModel()
    val context = LocalContext.current
    var isHRGraphLoading by remember { mutableStateOf(false) }
    var isRRGraphLoading by remember { mutableStateOf(false) }

    var hearRateWeekNumber = remember { mutableStateOf("") }
    var respiratoryRateWeekNumber = remember { mutableStateOf("") }

    var heartRateAllWeekDataList by remember { mutableStateOf<List<HeartRateData>>(emptyList()) }
    var heartRateDataPointList by remember { mutableStateOf<List<List<HeartRatePoint>>>(emptyList()) }

    var respiratoryRateAllWeekDataList by remember {
        mutableStateOf<List<RespiratoryRateDataValues>>(
            emptyList()
        )
    }
    var respiratoryRateDataPointList by remember {
        mutableStateOf<List<List<RespiratoryRatePoint>>>(
            emptyList()
        )
    }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { _ ->
    }
    LaunchedEffect(Unit) {
        isHRGraphLoading = true
        viewModel.getHeartRateCurrentWeekData().collect { result ->
            when (result) {
                is Resource.Success -> {
                    val data = result.data ?: emptyList()
                    // Clear the heartRateAllWeekDataList before updating it
                    heartRateAllWeekDataList = emptyList()
                    if (data.isNotEmpty()) {
                        // Update the heartRateDataPoint list
                        heartRateAllWeekDataList = data
                        heartRateDataPointList = viewModel.filterMaxMinHeartRatePerDay(data)
                        hearRateWeekNumber.value = viewModel.getCurrentWeekNumber()
                    }
                    isHRGraphLoading = false
                }

                is Resource.Error -> {
                    Toast.makeText(context, "Error loading Heart Rate Data", Toast.LENGTH_SHORT)
                        .show()
                }

                else -> {}
            }
        }

    }

    LaunchedEffect(Unit) {
        isRRGraphLoading = true
        viewModel.getRespiratoryRateCurrentWeekData().collect { result ->
            when (result) {
                is Resource.Success -> {
                    val data = result.data ?: emptyList()
                    // Clear the heartRateAllWeekDataList before updating it
                    respiratoryRateAllWeekDataList = emptyList()
                    if (data.isNotEmpty()) {
                        // Update the heartRateDataPoint list
                        respiratoryRateAllWeekDataList = data
                        respiratoryRateDataPointList =
                            viewModel.filterMaxMinRespiratoryRatePerDay(data)
                        respiratoryRateWeekNumber.value = viewModel.getCurrentWeekNumber()
                    }
                    isRRGraphLoading = false
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
                Card(
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                        }
                        navController.navigate("QUESTIONER");
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .padding(10.dp)
                ) {
                    Box(Modifier.fillMaxSize()) {
                        Text(
                            "Questioner",
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
                    },
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = {
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
                    elevation = FloatingActionButtonDefaults.elevation(0.dp),
                    expanded = false,
                    containerColor = MaterialTheme.colorScheme.primary
                )
            }
        ) {
            it
            LazyColumn(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 120.dp),

            ) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, end = 10.dp)
                            .height(350.dp),

                        ) {
                        Row(
                            modifier = Modifier
                                .padding(start = 20.dp, top = 10.dp, end = 20.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = " Heart Rate",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(bottom = 10.dp)
                            )
                            Text(
                                text = hearRateWeekNumber.value,
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(top = 10.dp)
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp, horizontal = 20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    scope.launch {
                                        isHRGraphLoading = true
                                        viewModel.getHeartRatePreviousWeekData()
                                            .collect { result ->
                                                when (result) {
                                                    is Resource.Success -> {
                                                        val data =
                                                            result.data ?: emptyList()
                                                        // Clear the heartRateAllWeekDataList before updating it
                                                        heartRateAllWeekDataList =
                                                            emptyList()
                                                        hearRateWeekNumber.value =
                                                            viewModel.getPreviousWeekNumber()
                                                        heartRateDataPointList =
                                                            if (data.isNotEmpty()) {
                                                                // Update the heartRateDataPoint list
                                                                viewModel.filterMaxMinHeartRatePerDay(
                                                                    data
                                                                )
                                                            } else {
                                                                emptyList()
                                                            }
                                                        isHRGraphLoading = false
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
                                Text(
                                    text = "Last Week",
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }

                            Button(
                                onClick = {
                                    scope.launch {
                                        isHRGraphLoading = true
                                        viewModel.getHeartRateCurrentWeekData()
                                            .collect { result ->
                                                when (result) {
                                                    is Resource.Success -> {
                                                        val data =
                                                            result.data ?: emptyList()
                                                        // Clear the heartRateAllWeekDataList before updating it
                                                        heartRateAllWeekDataList =
                                                            emptyList()
                                                        hearRateWeekNumber.value =
                                                            viewModel.getCurrentWeekNumber()
                                                        heartRateDataPointList =
                                                            if (data.isNotEmpty()) {
                                                                // Update the heartRateDataPoint list
                                                                viewModel.filterMaxMinHeartRatePerDay(
                                                                    data
                                                                )
                                                            } else {
                                                                emptyList()
                                                            }
                                                        isHRGraphLoading = false
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
                                modifier = Modifier
                                    .height(40.dp)
                                    .weight(1f),
                                shape = MaterialTheme.shapes.medium,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary,
                                    contentColor = MaterialTheme.colorScheme.onSecondary
                                )
                            ) {
                                Text(
                                    text = "This Week",
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }

                        if (heartRateDataPointList.isEmpty()) {
                            // Display the "No data found" image
                            Image(
                                painter = painterResource(R.drawable.no_data_found),
                                contentDescription = "No data found",
                                modifier = Modifier
                                    .padding(start = 40.dp, end = 20.dp)
                            )
                        } else {
                            HeartRateDataGraph(
                                data = heartRateDataPointList,
                                modifier = Modifier.fillMaxSize(),
                                isLoading = isHRGraphLoading
                            )
                        }
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp, start = 10.dp, end = 10.dp, bottom = 10.dp)
                            .height(350.dp),
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(start = 20.dp, top = 10.dp, end = 20.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Respiratory Rate",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(bottom = 10.dp)
                            )
                            Text(
                                text = respiratoryRateWeekNumber.value,
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(top = 10.dp)
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp, horizontal = 20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    scope.launch {
                                        isRRGraphLoading = true
                                        viewModel.getRespiratoryRatePreviousWeekData()
                                            .collect { result ->
                                                when (result) {
                                                    is Resource.Success -> {
                                                        val data =
                                                            result.data ?: emptyList()
                                                        // Clear the heartRateAllWeekDataList before updating it
                                                        respiratoryRateAllWeekDataList =
                                                            emptyList()
                                                        respiratoryRateWeekNumber.value =
                                                            viewModel.getPreviousWeekNumber()
                                                        respiratoryRateDataPointList =
                                                            if (data.isNotEmpty()) {
                                                                // Update the heartRateDataPoint list
                                                                viewModel.filterMaxMinRespiratoryRatePerDay(
                                                                    data
                                                                )
                                                            } else {
                                                                emptyList()
                                                            }
                                                        isRRGraphLoading = false
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
                                Text(
                                    text = "Last Week",
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }

                            Button(
                                onClick = {
                                    scope.launch {
                                        isRRGraphLoading = true
                                        viewModel.getRespiratoryRateCurrentWeekData()
                                            .collect { result ->
                                                when (result) {
                                                    is Resource.Success -> {
                                                        val data =
                                                            result.data ?: emptyList()
                                                        respiratoryRateAllWeekDataList =
                                                            emptyList()
                                                        respiratoryRateWeekNumber.value =
                                                            viewModel.getCurrentWeekNumber()
                                                        respiratoryRateDataPointList =
                                                            if (data.isNotEmpty()) {
                                                                // Update the heartRateDataPoint List
                                                                viewModel.filterMaxMinRespiratoryRatePerDay(
                                                                    data
                                                                )
                                                            } else {
                                                                emptyList()
                                                            }
                                                        isRRGraphLoading = false
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
                                modifier = Modifier
                                    .height(40.dp)
                                    .weight(1f),
                                shape = MaterialTheme.shapes.medium,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary,
                                    contentColor = MaterialTheme.colorScheme.onSecondary
                                )
                            ) {
                                Text(
                                    text = "This Week",
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }

                        if (respiratoryRateDataPointList.isEmpty()) {
                            // Display the "No data found" image
                            Image(
                                painter = painterResource(R.drawable.no_data_found),
                                contentDescription = "No data found",
                                modifier = Modifier
                                    .padding(start = 40.dp, end = 20.dp)
                            )
                        } else {
                            RespiratoryRateDataGraph(
                                data = respiratoryRateDataPointList,
                                modifier = Modifier.fillMaxSize(),
                                isLoading = isRRGraphLoading
                            )
                        }
                    }
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

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(rememberNavController())
}
