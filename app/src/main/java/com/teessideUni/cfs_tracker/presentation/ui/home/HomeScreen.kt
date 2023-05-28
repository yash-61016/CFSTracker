package com.teessideUni.cfs_tracker.presentation.ui.home

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(navController: NavController) {
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    )

    val scope = rememberCoroutineScope()
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
                    .padding(top = 70.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier.padding(8.dp),
                    shape = MaterialTheme.shapes.small,
                    elevation = CardDefaults.cardElevation()
                ) {
                    CardContent(
                        title = "Heart Rate",
                        text = "The graph for heart rate will come here"
                    )
                }

                Card(
                    modifier = Modifier.padding(8.dp),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation()
                ) {
                    CardContent(
                        title = "Respiratory Rate",
                        text = "The graph for respiratory rate will come here"
                    )
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
    title: String,
    text: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            title,
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(rememberNavController())
}
