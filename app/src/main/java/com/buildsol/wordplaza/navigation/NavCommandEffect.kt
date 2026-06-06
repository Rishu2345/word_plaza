package com.buildsol.wordplaza.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.Flow

@Composable
fun NavCommandEffect(
    navHostController: NotableComposeNavController,
    navCommandFlow: Flow<NavCommand>,
) {
    LaunchedEffect(navHostController) {
        navCommandFlow.collect { navCommand ->
            navHostController.onCollectNavCommand(navCommand)
        }
    }
}
