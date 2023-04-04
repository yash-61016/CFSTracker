package com.teessideUni.cfs_tracker.screens

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.teessideUni.cfs_tracker.R
import com.teessideUni.cfs_tracker.ui.theme.Poppins
import kotlinx.coroutines.delay
import java.lang.Math.PI
import java.lang.Math.sin




//Version 2
@Composable
fun SplashScreen(navController: NavController) {
    val scale = remember { Animatable(0f) }
    val lineLength = remember { Animatable(0f) }

    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 0.3f,
            animationSpec = tween(
                durationMillis = 400,
                easing = {
                    OvershootInterpolator(2f).getInterpolation(it)
                }
            )
        )
        delay(3000L)
        navController.navigate("login_page")
    }

    LaunchedEffect(key1 = lineLength) {
        lineLength.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 600),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .width(IntrinsicSize.Min)
        ) {
            Image(
                painter = painterResource(id = R.drawable.health_app_icon),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(280.dp)
                    .scale(scale.value)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "CFS Tracker",
            color = Color.White,
            fontFamily = Poppins,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier
                .height(50.dp)
                .width(160.dp * lineLength.value)
        ) {
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                drawLine(
                    color = Color.White,
                    strokeWidth = 4.dp.toPx(),
                    start = Offset(0f, size.height / 2 - 5.dp.toPx()),
                    end = Offset(size.width, size.height / 2 - 5.dp.toPx())
                )
            }
        }
    }
}



//Version 1
//@Composable
//fun SplashScreen(navController: NavController) {
//    val scale = remember {
//        Animatable(0f)
//    }
//    LaunchedEffect(key1 = true) {
//        scale.animateTo(
//            targetValue = 0.3f,
//            animationSpec = tween(
//                durationMillis = 400,
//                easing = {
//                    OvershootInterpolator(2f).getInterpolation(it)
//                }
//            )
//        )
//        delay(3000L)
//        navController.navigate("login_page")
//    }
//    Box(
//        contentAlignment = Alignment.Center,
//        modifier = Modifier.fillMaxSize()
//    ) {
//        Image(
//            painter = painterResource(id = R.drawable.health_app_icon),
//            contentDescription = "Logo",
//            modifier = Modifier
//                .size(280.dp)
//                .scale(scale.value)
//        )
//
//        Text(
//            text = "CFS Tracker",
//            color = Color.White,
//            fontFamily = Poppins,
//            fontWeight = FontWeight.Bold,
//            modifier = Modifier.padding(top = 120.dp)
//        )
//    }
//}