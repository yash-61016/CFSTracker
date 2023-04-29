package com.teessideUni.cfs_tracker.presentation.screens

//import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
//import androidx.navigation.NavGraph.Companion.findStartDestination
//import com.teessideUni.cfs_tracker.presentation.screens.heart_rate.HeartRateMeasurement_Activity

@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        // Handle the result of the activity here
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Home Page: CFS Tracker",
                color = Color.Black,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    //  Create an Intent to start the activity
//                    val intent = Intent(context, HeartRateMeasurement_Activity::class.java)
//                    launcher.launch(intent)
//                    navController.navigate("heart_rate") {
//                        popUpTo(navController.graph.findStartDestination().id) {
//                            inclusive = true
//                        }
//                        launchSingleTop = true
//                    }
                }
            ) {
                Text("Button")
            }
        }
    }
}