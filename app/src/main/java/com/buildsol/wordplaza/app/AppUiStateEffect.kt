package com.buildsol.wordplaza.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.buildsol.wordplaza.model.AppUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

@Composable
fun AppUiStateEffect(
    appUiStateFlow: Flow<AppUiState>,
    onSetAppUiState: (AppUiState) -> Unit,
) {
    LaunchedEffect(appUiStateFlow) {
        withContext(Dispatchers.Main.immediate) {
            appUiStateFlow.collect {
                onSetAppUiState(it)
            }
        }
    }
}
