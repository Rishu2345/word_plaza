package com.buildsol.wordplaza.navigation

import androidx.compose.runtime.Composable
import com.buildsol.wordplaza.app.AppUiStateEffect
import com.buildsol.wordplaza.model.AppUiState
import com.buildsol.wordplaza.viewModel.AppViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
inline fun <reified T : AppViewModel> appViewModel(
    noinline onSetAppUiState: (AppUiState) -> Unit,
    navController: NotableComposeNavController,
): T {

    val viewModel: T = koinViewModel()

    AppUiStateEffect(
        appUiStateFlow = viewModel.appUiState,
        onSetAppUiState = onSetAppUiState,
    )

    NavCommandEffect(
        navHostController = navController,
        navCommandFlow = viewModel.navCommandFlow
    )

    return viewModel
}

