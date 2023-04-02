package com.teessideUni.cfs_tracker.domain.util

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.teessideUni.cfs_tracker.ui.theme.screens.LoginScreen
import com.teessideUni.cfs_tracker.ui.theme.screens.RegisterScene
import com.teessideUni.cfs_tracker.ui.theme.screens.SplashScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "splash_screen") {
        composable("splash_screen") {
            SplashScreen(navController = navController)
        }
        composable("login_screen") {
            LoginScreen(navController = navController)
        }
        composable("register_screen") {
            RegisterScene(navController = navController)
        }
    }
}
