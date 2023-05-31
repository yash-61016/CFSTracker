package com.teessideUni.cfs_tracker.core

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.teessideUni.cfs_tracker.R

object CFSTrackerRoute {
    const val HOME = "HOME"
    const val REPORTS = "REPORTS"
    const val SETTINGS = "SETTINGS"
    const val RESPIRATORY_RATE_RECORDER = "RESPIRATORY_RATE_RECORDER"
    const val LOGIN_PAGE = "LOGIN_PAGE"
    const val REGISTER_PAGE = "REGISTER_PAGE"
    const val FORGET_PASSWORD_PAGE = "FORGET_PASSWORD_PAGE"
    const val SPLASH_SCREEN = "SPLASHSCREEN"
    const val QUESTIONER = "QUESTIONER"
}

data class CFSTrackerTopLevelDestination(
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val iconTextId: Int
)

class CFSTrackerNavigationActions(private val navController: NavHostController) {

    fun navigateTo(destination: CFSTrackerTopLevelDestination) {
        navController.navigate(destination.route) {

            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}

val TOP_LEVEL_DESTINATIONS = listOf(
    CFSTrackerTopLevelDestination(
        route = CFSTrackerRoute.HOME,
        selectedIcon = Icons.Default.Home,
        unselectedIcon = Icons.Default.Home,
        iconTextId = R.string.tab_home
    ),
    CFSTrackerTopLevelDestination(
        route = CFSTrackerRoute.REPORTS,
        selectedIcon = Icons.Default.Article,
        unselectedIcon = Icons.Default.Article,
        iconTextId = R.string.tab_report
    ),
    CFSTrackerTopLevelDestination(
        route = CFSTrackerRoute.SETTINGS,
        selectedIcon = Icons.Outlined.Settings,
        unselectedIcon = Icons.Outlined.Settings,
        iconTextId = R.string.tab_setting
    ),
)
