package com.buildsol.wordplaza

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.buildsol.wordplaza.app.AppScaffold
import com.buildsol.wordplaza.model.AppUiState
import com.buildsol.wordplaza.navigation.AppNavHost
import com.buildsol.wordplaza.navigation.GTCAppRoute
import com.buildsol.wordplaza.navigation.HomeScreenRoute
import com.buildsol.wordplaza.navigation.OnboardingScreenRoute
import com.buildsol.wordplaza.ui.theme.AppTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    private val auth = FirebaseAuth.getInstance()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val firstScreen: GTCAppRoute = if (auth.currentUser != null) {
                HomeScreenRoute
            } else {
                OnboardingScreenRoute
            }
            val navController = rememberNavController()
            val appUiState = remember { mutableStateOf(AppUiState()) }

            AppTheme {
                AppScaffold(appUiState = appUiState.value) { padding ->
                    AppNavHost(
                        navController = navController,
                        startingScreen = firstScreen,
                        onSetAppUiState = { appUiState.value = it },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    )
                }
            }
        }
    }
}
