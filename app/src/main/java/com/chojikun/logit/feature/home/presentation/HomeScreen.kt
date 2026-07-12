package com.chojikun.logit.feature.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.chojikun.logit.core.navigation.Routes

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onLoggedOut: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
){
    val navController = rememberNavController()
    val isLoggingOut by viewModel.isLoggingOut.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigateToLogin.collect { onLoggedOut() }
    }

    Box(modifier = modifier) {
        Scaffold(
            modifier = Modifier.padding(
                top = 20.dp
            ),
            containerColor = Color(0xFFF2EDE4),
            bottomBar = { MyBottomBar(navController = navController) }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Routes.HOME,
                modifier = Modifier.padding(innerPadding),
            ) {
                composable(Routes.HOME) { HomeContent() }
                composable(Routes.TRENDS) { Text("Trends - Coming soon") }
                composable(Routes.MORE) {
                    MoreContent(onLogoutTapped = viewModel::onLogoutTapped)
                }
            }
        }

        if (isLoggingOut) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {},
                    ),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
    }
}

@Composable
private fun HomeContent() {
    Column(
        modifier = Modifier.padding(horizontal = 12.dp)
    ) {
        Text(
            "Hi, Welcome back!",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A),
        )
    }
}

@Composable
fun MyBottomBar(navController: NavHostController) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        fun navigate(route: String) {
            if (route == currentRoute) return
            navController.navigate(route) {
                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }

        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentRoute == Routes.HOME,
            onClick = { navigate(Routes.HOME) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = "Trends") },
            label = { Text("Trends") },
            selected = currentRoute == Routes.TRENDS,
            onClick = { navigate(Routes.TRENDS) }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Filled.MoreHoriz, contentDescription = "More") },
            label = { Text("More") },
            selected = currentRoute == Routes.MORE,
            onClick = { navigate(Routes.MORE) }
        )
    }
}
