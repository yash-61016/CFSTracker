package com.teessideUni.cfs_tracker

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.teessideUni.cfs_tracker.core.Constants
import com.teessideUni.cfs_tracker.presentation.ui.CFSTrackerApp
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
        val firebaseAuth = FirebaseAuth.getInstance()
        val firsStore = FirebaseFirestore.getInstance()
        setContent {
            navController = rememberNavController()
            CFSTrackerTheme {
                CFSTrackerApp(this, firebaseAuth, firsStore )
//                Surface {
//                    Navigation(navController = navController)
//                    AuthState(navController)
//                }
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
//
//    @Composable
//    private fun AuthState(navController: NavController) {
//        val isUserSignedOut = viewModel.getAuthState().collectAsState().value
//        var isLoggedIn = viewModel.getCurrentUser()
//
//        if (!isLoggedIn && isUserSignedOut) {
//            NavigateToSplashScreen(navController = navController)
//        } else {
//
//            if (viewModel.isEmailVerified) {
//                NavigateToProfileScreen(navController = navController)
//            } else {
//                Toast.makeText(
//                    this,
//                    "Please verify your email address to continue.",
//                    Toast.LENGTH_LONG
//                ).show()
//                NavigateToSignInScreen(navController = navController)
//            }
//        }
//    }
//
//    @Composable
//    private fun NavigateToSplashScreen(navController: NavController) =
//        navController.navigate("splash_screen") {
//            popUpTo(navController.graph.findStartDestination().id) {
//                inclusive = true
//            }
//            launchSingleTop = true
//        }
//
//    @Composable
//    private fun NavigateToSignInScreen(navController: NavController) =
//        navController.navigate("login_page") {
//            popUpTo(navController.graph.findStartDestination().id) {
//                inclusive = true
//            }
//            launchSingleTop = true
//        }
//
//    @Composable
//    private fun NavigateToProfileScreen(navController: NavController) =
//        navController.navigate("home_page") {
//            popUpTo(navController.graph.findStartDestination().id) {
//                inclusive = true
//            }
//            launchSingleTop = true
//        }
}