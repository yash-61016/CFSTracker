package com.teessideUni.cfs_tracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.teessideUni.cfs_tracker.presentation.ui.CFSTrackerApp
import com.teessideUni.cfs_tracker.ui.theme.CFSTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CFSTrackerTheme {
                CFSTrackerApp(this)
            }
        }
    }
}