package com.teessideUni.cfs_tracker

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.teessideUni.cfs_tracker.domain.navigation.Navigation
import com.teessideUni.cfs_tracker.presentation.screens.heart_rate.Constants
import com.teessideUni.cfs_tracker.ui.theme.CFSTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
class MainActivity : ComponentActivity(),  ActivityCompat.OnRequestPermissionsResultCallback {
    private lateinit var navController: NavHostController
    private val viewModel by viewModels<MainViewModel>()

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            navController = rememberNavController()
            CFSTrackerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = colorResource(R.color.dark)
                ) {

                    Navigation(navController = navController)
                    AuthState(navController = navController)
                }
            }
        }
        requestPermissions(
            this, arrayOf<String>(Manifest.permission.CAMERA),
            Constants.REQUEST_CODE_CAMERA
        )
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { view, insets ->
            val bottom = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            view.updatePadding(bottom = bottom)
            insets
        }
    }

    @Composable
    private fun AuthState(navController: NavController) {
        val isUserSignedOut = viewModel.getAuthState().collectAsState().value
        var isLoggedIn = viewModel.getCurrentUser()

        if (!isLoggedIn && isUserSignedOut) {
            Log.d("reached", "enetered")
            NavigateToSplashScreen(navController = navController)
        } else {

            if (viewModel.isEmailVerified) {
                NavigateToProfileScreen(navController = navController)
            } else {
                Toast.makeText(
                    this,
                    "Please verify your email address to continue.",
                    Toast.LENGTH_LONG
                ).show()
                NavigateToSignInScreen(navController = navController)
            }
        }
    }

    @Composable
    private fun NavigateToSplashScreen(navController: NavController) =  navController.navigate("splash_screen") {
        popUpTo(navController.graph.findStartDestination().id) {
            inclusive = true
        }
        launchSingleTop = true
    }

    @Composable
    private fun NavigateToSignInScreen(navController: NavController) =  navController.navigate("login_page") {
        popUpTo(navController.graph.findStartDestination().id) {
            inclusive = true
        }
        launchSingleTop = true
    }

    @Composable
    private fun NavigateToProfileScreen(navController: NavController) = navController.navigate("home_page") {
        popUpTo(navController.graph.findStartDestination().id) {
            inclusive = true
        }
        launchSingleTop = true
    }
}