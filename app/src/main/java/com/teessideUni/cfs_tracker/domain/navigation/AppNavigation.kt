package com.teessideUni.cfs_tracker.domain.navigation

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.teessideUni.cfs_tracker.presentation.screens.SplashScreen
import com.teessideUni.cfs_tracker.presentation.screens.forget_password_screen.ForgotPasswordScreen
import com.teessideUni.cfs_tracker.presentation.screens.heart_rate_report.HeartRateReportComponent
import com.teessideUni.cfs_tracker.presentation.screens.home_screen.HomeScreen
import com.teessideUni.cfs_tracker.presentation.screens.login_screen.LoginScreen
import com.teessideUni.cfs_tracker.presentation.screens.register_screen.RegisterScene
import com.teessideUni.cfs_tracker.presentation.screens.settings_screen.SettingsComponent


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation(navController: NavHostController)
{
    NavHost(navController = navController, startDestination = "home_page") {
        composable("splash_screen") {
            SplashScreen(navController = navController)
        }
        composable("login_page") {
            BackHandler(true) {
                // Or do nothing
            }
            LoginScreen(navController = navController)
        }
        composable("register_page") {
            BackHandler(true) {
                // Or do nothing
            }
            RegisterScene(navController = navController)
        }
        composable("home_page") {
            BackHandler(true) {
                // Or do nothing
            }
            HomeScreen(navController = navController)
        }
        composable("report") {
            BackHandler(true) {
                // Or do nothing
            }
            HeartRateReportComponent(navController = navController)
        }
        composable("forget_password_page") {
            BackHandler(true) {
                // Or do nothing
            }
            ForgotPasswordScreen(navController = navController)
        }
        composable("settings_page") {
            BackHandler(true) {
                // Or do nothing
            }
            SettingsComponent(navController = navController)
        }
    }
}
