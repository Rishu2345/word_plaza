package com.buildsol.wordplaza.viewModel.loginViewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.buildsol.wordplaza.firebase.authentication.FirebaseAuth
import com.buildsol.wordplaza.firebase.firestore.FireStore
import com.buildsol.wordplaza.navigation.GTCAppRoute
import com.buildsol.wordplaza.navigation.NavCommand
import com.buildsol.wordplaza.viewModel.AppViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    savedStateHandle: SavedStateHandle,
    private val firebaseAuth: FirebaseAuth,
    private val fireStore: FireStore
): AppViewModel(savedStateHandle) {
    init {
        _appUiState.update {
            it.copy (
                hideBottomNavigation = true,
                hideTopBar = true
            )
        }
    }
    val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun onSignInClicked(){
            if (!uiState.value.isLoading && !uiState.value.isSignedIn) {
                viewModelScope.launch {
                    _uiState.update { it.copy(isLoading = true,errorMessage = null) }

                    try {
                        val signInResult = firebaseAuth.signIn()
                        if (signInResult?.firebaseUid.isNullOrBlank()) {
                            _uiState.update { it.copy(isLoading = false,errorMessage = "Google sign-in did not complete. Please try again.") }
                        } else {
                            fireStore.initializeUserData(signInResult)
                            _uiState.update { it.copy(isLoading = false,errorMessage = null, isSignedIn = true) }
                        }
                    } catch (exception: Exception) {
                        _uiState.update { it.copy(isLoading = false,errorMessage = exception.message ?: "Unable to sign in right now.") }
                    } finally {
                        _uiState.update { it.copy(isLoading = true) }
                    }

            }
        }
    }


    fun moveToProfileUpdateScreen(){
        _navCommandFlow.tryEmit(
            NavCommand.Navigate(
                route = GTCAppRoute.ProfileUpdate.route,
            )
        )
    }
}