package com.teessideUni.cfs_tracker

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
   // private val viewModel by viewModels<MainViewModel>()

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
}