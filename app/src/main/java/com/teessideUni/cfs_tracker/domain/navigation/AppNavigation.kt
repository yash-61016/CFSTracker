package com.teessideUni.cfs_tracker.domain.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.teessideUni.cfs_tracker.presentation.screens.SplashScreen
import com.teessideUni.cfs_tracker.presentation.screens.forget_password_screen.ForgotPasswordScreen
import com.teessideUni.cfs_tracker.presentation.screens.home_screen.OldHomeScreen
import com.teessideUni.cfs_tracker.presentation.screens.login_screen.LoginScreen
import com.teessideUni.cfs_tracker.presentation.screens.register_screen.RegisterScene
import com.teessideUni.cfs_tracker.presentation.screens.report_screen.ReportComponent
import com.teessideUni.cfs_tracker.presentation.screens.settings_screen.SettingsComponent



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
            OldHomeScreen(navController = navController)
        }
        composable("report") {
            ReportComponent(navController = navController)
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
