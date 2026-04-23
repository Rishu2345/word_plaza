package com.buildsol.wordplaza.app

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeContent
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.example.notable.app.AppUiState

@Composable
fun AppScaffold(
    appUiState: AppUiState,
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        contentWindowInsets = WindowInsets.safeContent,
        modifier = modifier,
        topBar = {
            if (!appUiState.hideAppBar) {
                HPTopBar(
                    appUiState = appUiState
                )
            }
        },
        bottomBar = {
            if (!appUiState.hideBottomNavigation) {
                AppNavigationBar(
                    appUiState = appUiState
                )
            }
        }

    ) { innerPadding ->

        content(innerPadding)
    }
}
