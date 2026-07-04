package com.chojikun.logit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.chojikun.logit.core.navigation.AppNavGraph
import com.chojikun.logit.feature.auth.presentation.EntryScreen
import com.chojikun.logit.core.presentation.MainViewModel
import com.chojikun.logit.ui.theme.LogitTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition { viewModel.isFirstLaunch.value == null }
        enableEdgeToEdge()
        setContent {
            LogitTheme {
                AppNavGraph(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LogitTheme {
        EntryScreen(Modifier)
    }
}
