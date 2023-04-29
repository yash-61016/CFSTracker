package com.teessideUni.cfs_tracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.teessideUni.cfs_tracker.presentation.ui.CFSTrackerApp
import com.teessideUni.cfs_tracker.ui.theme.CFSTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CFSTrackerTheme {
                CFSTrackerApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(showBackground = true)
@Composable
fun CFSTrackerAppPreview() {
    CFSTrackerTheme {
        CFSTrackerApp()
    }
}