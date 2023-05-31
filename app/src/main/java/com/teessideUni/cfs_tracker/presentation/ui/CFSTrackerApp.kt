package com.teessideUni.cfs_tracker.presentation.ui

//import com.teessideUni.cfs_tracker.presentation.ui.home.HomeScreen
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.teessideUni.cfs_tracker.data.repository.RespiratoryRateRepositoryImpl
import com.teessideUni.cfs_tracker.core.CFSTrackerNavigationActions
import com.teessideUni.cfs_tracker.core.CFSTrackerRoute
import com.teessideUni.cfs_tracker.core.CFSTrackerTopLevelDestination
import com.teessideUni.cfs_tracker.domain.repository.RespiratoryRateRepository
import com.teessideUni.cfs_tracker.domain.use_case.RecordRespiratoryRateUseCase
import com.teessideUni.cfs_tracker.presentation.screens.SplashScreen
import com.teessideUni.cfs_tracker.presentation.ui.forgetpassword.ForgotPasswordScreen
import com.teessideUni.cfs_tracker.presentation.ui.login.LoginScreen
import com.teessideUni.cfs_tracker.presentation.ui.register.RegisterScene
import com.teessideUni.cfs_tracker.presentation.ui.reportpage.ReportComponent
import com.teessideUni.cfs_tracker.presentation.ui.settings.SettingsComponent
import com.teessideUni.cfs_tracker.presentation.ui.RespiratoryRate.RespiratoryRateScreen
import com.teessideUni.cfs_tracker.presentation.ui.RespiratoryRate.RespiratoryRateViewModel
import com.teessideUni.cfs_tracker.presentation.ui.home.HomeScreen
import com.teessideUni.cfs_tracker.presentation.ui.questionnaire.QuestionnaireScreen
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CFSTrackerApp(
    context: Context, firebaseAuth: FirebaseAuth,
    firebaseFirestore: FirebaseFirestore
) {

    CFSTrackerNavigationWrapper(context, firebaseAuth, firebaseFirestore)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CFSTrackerNavigationWrapper(
    context: Context, firebaseAuth: FirebaseAuth,
    firebaseFirestore: FirebaseFirestore
) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val navController = rememberNavController()
    val navigationActions = remember(navController) {
        CFSTrackerNavigationActions(navController)
    }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val selectedDestination =
        navBackStackEntry?.destination?.route ?: CFSTrackerRoute.HOME
    CFSTrackerAppContent(
        context,
        navController = navController,
        selectedDestination = selectedDestination,
        navigateToTopLevelDestination = navigationActions::navigateTo,
        firebaseAuth = firebaseAuth,
        firebaseFirestore = firebaseFirestore
    ) {
        scope.launch {
            drawerState.open()
        }
    }
}

@Composable
fun CFSTrackerAppContent(
    context: Context,
    modifier: Modifier = Modifier,
    navController: NavHostController,
    selectedDestination: String,
    firebaseAuth: FirebaseAuth,
    firebaseFirestore: FirebaseFirestore,
    navigateToTopLevelDestination: (CFSTrackerTopLevelDestination) -> Unit,
    onDrawerClicked: () -> Unit = {},

    ) {
    Row(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.inverseOnSurface)
        ) {
            CFSTrackerNavHost(
                navController = navController,
                modifier = Modifier.weight(1f),
                context,
                firebaseAuth = firebaseAuth,
                firebaseFirestore = firebaseFirestore
            )
            AnimatedVisibility(visible = true) {
                CFSTrackerBottomNavigationBar(
                    selectedDestination = selectedDestination,
                    navigateToTopLevelDestination = navigateToTopLevelDestination
                )
            }
        }
    }
}

@Composable
private fun CFSTrackerNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    context: Context,
    firebaseAuth: FirebaseAuth,
    firebaseFirestore: FirebaseFirestore
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = CFSTrackerRoute.HOME,
    ) {
        composable(CFSTrackerRoute.HOME) {
            HomeScreen(navController)
        }
        composable(CFSTrackerRoute.REPORTS) {
//            EmptyComingSoon()
            ReportComponent(navController)
        }
        composable(CFSTrackerRoute.SETTINGS) {
            SettingsComponent(navController)
        }
        composable(CFSTrackerRoute.RESPIRATORY_RATE_RECORDER) {
            val dataSrc: RespiratoryRateRepository = RespiratoryRateRepositoryImpl(context, firebaseAuth, firebaseFirestore)
            val repo: RespiratoryRateRepository = dataSrc
            val useCase = RecordRespiratoryRateUseCase(repo)
            val viewModel = RespiratoryRateViewModel(useCase, dataSrc)
            RespiratoryRateScreen(viewModel)
        }
        composable(CFSTrackerRoute.LOGIN_PAGE) {
            LoginScreen(navController)
        }
        composable(CFSTrackerRoute.REGISTER_PAGE) {
            RegisterScene(navController)
        }
        composable(CFSTrackerRoute.SPLASH_SCREEN) {
            SplashScreen(navController)
        }
        composable(CFSTrackerRoute.FORGET_PASSWORD_PAGE) {
            ForgotPasswordScreen(navController)
        }
        composable(CFSTrackerRoute.QUESTIONNAIRE){
            QuestionnaireScreen( navController = navController)
        }
    }
}
