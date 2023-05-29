package com.teessideUni.cfs_tracker.presentation.screens.settings_screen

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.teessideUni.cfs_tracker.R
import com.teessideUni.cfs_tracker.domain.use_cases.view_models.settingsVM.SettingsViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsComponent(navController: NavController) {

    val viewModel: SettingsViewModel = hiltViewModel()

    val coroutineScope = rememberCoroutineScope()
    var isAboutExpanded by remember { mutableStateOf(false) }
    val aboutCardHeight by animateDpAsState(if (isAboutExpanded) 230.dp else 55.dp)

    var userName by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }
    var userPhone by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        userName = viewModel.getCurrentUserName()
        userEmail = viewModel.getCurrentUserEmailAddress()
        userPhone = viewModel.getCurrentUserContactNumber()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Settings")
                }
            )
        },
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(horizontal = 16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxSize(),
                    shape = MaterialTheme.shapes.small,
                ) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(
                        modifier = Modifier
                            .padding(
                                start = 20.dp,
                                top = 10.dp,
                                end = 20.dp
                            ) // Increased end padding to align the icon
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.height(20.dp))
                        Box(
                            modifier = Modifier
                                .size(50.dp) // Adjust the image size
                                .clip(CircleShape) // Apply circular shape to the Box
                                .border(
                                    1.dp,
                                    Color.Black,
                                    CircleShape
                                ) // Add a border with 1dp width and black color
                        ) {
                            Image(
                                painter = painterResource(R.drawable.baseline_person_24), // Replace with your heart icon resource
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(), // Fill the Box with the image
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary) // Apply the color filter to the image
                            )
                        }
                        Column(
                            modifier = Modifier.weight(2f)
                        ) {
                            Text(
                                text = userName,
                                modifier = Modifier
                                    .padding(top = 1.dp, start = 20.dp, end = 20.dp),
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))

                        if (userName == "Username") {
                            IconButton(
                                onClick = {
                                    navController.navigate("LOGIN_PAGE") {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            inclusive = true
                                        }
                                        launchSingleTop = true
                                    }
                                },
                                modifier = Modifier
                                    .size(50.dp) // Increased icon size
                                    .padding(
                                        top = 3.dp,
                                        end = 10.dp
                                    ), // Added end padding to align the icon to the right
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowRight,
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp) // Adjusted the icon size
                                )
                            }
                        } else {
                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        viewModel.logout()
                                        navController.navigate("SETTINGS") {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                inclusive = true
                                            }
                                            launchSingleTop = true
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .size(40.dp) // Increased icon size
                                    .padding(
                                        top = 3.dp,
                                        end = 10.dp
                                    ), // Added end padding to align the icon to the right
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ExitToApp,
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp) // Adjusted the icon size
                                )
                            }
                        }
                    }

                    if (userName == "Username") {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 15.dp, end = 15.dp, bottom = 5.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Box(modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 10.dp)) {
                                Image(
                                    painter = painterResource(R.drawable.baseline_cloud_off_24), // Replace with your heart icon resource
                                    contentDescription = null,
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .size(80.dp)
                                        .padding(top = 2.dp, bottom = 10.dp), // Fill the Box with the image
                                    colorFilter = ColorFilter.tint(Color.Gray) // Apply the color filter to the image
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "No User Details Found",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Gray,
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .padding(
                                            top = 85.dp,
                                            start = 20.dp,
                                            end = 20.dp,
                                            bottom = 20.dp
                                        )
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "Details",
                            style= MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(
                                top = 15.dp,
                                start = 20.dp,
                                end = 20.dp,
                                bottom = 10.dp
                            )
                        )
                        Row(modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 10.dp)) {
                            Text(
                                text = "Email: ",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color.Gray,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = userEmail,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )
                        }
                        Row(modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 10.dp)) {
                            Text(
                                text = "Phone: ",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color.Gray,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = userPhone,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )
                        }
                    }
                }

            }
            Spacer(modifier = Modifier.height(20.dp))

            Divider(modifier = Modifier.padding(start = 20.dp, end = 20.dp), color = Color.Gray)

            Spacer(modifier = Modifier.height(20.dp))

            IconButton(
                onClick = {
                    // handle Add Device button on click
                },
                enabled = userName != "Username", // Set enabled to false to disable the button
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
                    .padding(start = 20.dp, end = 20.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (userName != "Username") MaterialTheme.colorScheme.primary else Color.Gray),
                content = {
                    Row(
                        modifier = Modifier
                            .padding(start = 22.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Pair Device",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = "Add Device",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(aboutCardHeight)
                    .padding(start = 20.dp, end = 20.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxSize(),
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(
                        modifier = Modifier
                            .padding(
                                start = 20.dp,
                                top = 10.dp,
                                end = 20.dp
                            ) // Increased end padding to align the icon
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(2f)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(start = 2.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Pair Device",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                                Text(
                                    text = "About Application",
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        IconButton(
                            onClick = {isAboutExpanded = !isAboutExpanded},
                            modifier = Modifier
                                .size(30.dp) // Increased icon size
                                .padding(top = 3.dp, end = 10.dp), // Added end padding to align the icon to the right
                        ) {
                            Icon(
                                imageVector = if (isAboutExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(40.dp) // Adjusted the icon size
                            )
                        }
                    }

                    // Add additional content for details here, visible when expanded
                    if (isAboutExpanded) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Card(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(
                                    start = 15.dp,
                                    end = 15.dp,
                                    bottom = 15.dp
                                ),
                            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Details",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black,
                                modifier = Modifier.padding(
                                    top = 15.dp,
                                    start = 20.dp,
                                    end = 20.dp,
                                    bottom = 10.dp
                                )
                            )
                        }
                    }
                }
            }
        }

    }
}
