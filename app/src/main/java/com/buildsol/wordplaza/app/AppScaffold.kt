package com.buildsol.wordplaza.app

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.buildsol.wordplaza.model.AppUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    appUiState: AppUiState,
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        contentWindowInsets = WindowInsets.safeContent,
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            if (!appUiState.hideTopBar) {
                HPTopBar(
                    appUiState = appUiState,
                    scrollBehavior = scrollBehavior
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
