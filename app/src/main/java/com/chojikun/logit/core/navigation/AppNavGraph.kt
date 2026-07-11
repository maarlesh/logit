package com.chojikun.logit.core.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.chojikun.logit.feature.auth.presentation.EntryScreen
import com.chojikun.logit.core.presentation.MainViewModel
import com.chojikun.logit.feature.auth.presentation.LoginScreen
import com.chojikun.logit.feature.auth.presentation.RegisterScreen

@Composable
fun AppNavGraph(modifier: Modifier = Modifier) {
    val viewModel: MainViewModel = hiltViewModel()
    val isFirstLaunch by viewModel.isFirstLaunch.collectAsStateWithLifecycle()
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val navController = rememberNavController()

    if (isFirstLaunch == null || isLoggedIn == null) return

    val startDestination = when {
        isFirstLaunch == true -> Routes.ENTRY
        isLoggedIn == true -> Routes.HOME
        else -> Routes.LOGIN
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable(Routes.ENTRY) {
            EntryScreen(
                onGettingStarted = {
                    viewModel.onEntryScreenDone()
                    navController.navigate(Routes.REGISTER) {
                        popUpTo(Routes.ENTRY) { inclusive = true }
                    }
                },
                onSigningInTapped = {
                    navController.navigate(Routes.LOGIN)
                }
            )
        }
        composable(Routes.HOME) {
            Text(text = "Home Screen")
        }
        composable(Routes.LOGIN) {
            LoginScreen(
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }
        composable(Routes.REGISTER) {
            RegisterScreen(
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onSigningInTapped = {
                    navController.navigate(Routes.LOGIN)
                }
            )
        }
    }
}
