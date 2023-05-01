package com.teessideUni.cfs_tracker.domain.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.teessideUni.cfs_tracker.presentation.screens.home_screen.HomeScreen
import com.teessideUni.cfs_tracker.presentation.screens.SplashScreen
import com.teessideUni.cfs_tracker.presentation.screens.forgetpassword.ForgotPasswordScreen
import com.teessideUni.cfs_tracker.presentation.screens.heartRateReport.HeartRateDataScreen
import com.teessideUni.cfs_tracker.presentation.screens.login_screen.LoginScreen
import com.teessideUni.cfs_tracker.presentation.screens.register_screen.RegisterScene


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation(navController: NavHostController)
{
    NavHost(navController = navController, startDestination = "splash_screen") {
        composable("splash_screen") {
            SplashScreen(navController = navController)
        }
        composable("login_page") {
            LoginScreen(navController = navController)
        }
        composable("register_page") {
            RegisterScene(navController = navController)
        }
        composable("home_page") {
            HomeScreen(navController = navController)
        }
        composable("heart_rate_report_page") {
            HeartRateDataScreen(navController = navController)
        }
        composable("forget_password_page") {
            ForgotPasswordScreen(navController = navController)
        }
    }
}
