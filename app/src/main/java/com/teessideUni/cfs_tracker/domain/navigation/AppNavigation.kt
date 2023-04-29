package com.teessideUni.cfs_tracker.domain.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.teessideUni.cfs_tracker.presentation.screens.HomeScreen
import com.teessideUni.cfs_tracker.presentation.screens.SplashScreen
import com.teessideUni.cfs_tracker.presentation.screens.forgetpassword.ForgotPasswordScreen
import com.teessideUni.cfs_tracker.presentation.screens.login_screen.LoginScreen
import com.teessideUni.cfs_tracker.presentation.screens.register_screen.RegisterScene

@Composable
fun Navigation() {
    val navController = rememberNavController()
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

        composable("forget_password_page") {
            ForgotPasswordScreen(navController = navController)
        }
    }
}
