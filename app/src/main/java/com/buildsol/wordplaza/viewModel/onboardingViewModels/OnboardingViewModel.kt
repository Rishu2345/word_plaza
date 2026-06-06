package com.buildsol.wordplaza.viewModel.onboardingViewModels

import androidx.lifecycle.SavedStateHandle
import com.buildsol.wordplaza.navigation.LoginScreenRoute
import com.buildsol.wordplaza.navigation.NavCommand
import com.buildsol.wordplaza.viewModel.AppViewModel
import kotlinx.coroutines.flow.update

class OnboardingViewModel(
    savedStateHandle: SavedStateHandle
): AppViewModel(savedStateHandle) {

    init {
        _appUiState.update {
            it.copy (
                hideBottomNavigation = true,
                hideTopBar = true
            )
        }
    }
    fun moveToProfileUpdateScreen(){
            _navCommandFlow.tryEmit(NavCommand.Navigate(
                destination = LoginScreenRoute,
                clearBackStack = true
            )
        )
    }
}