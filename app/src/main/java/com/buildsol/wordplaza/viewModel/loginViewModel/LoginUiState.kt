package com.buildsol.wordplaza.viewModel.loginViewModel

data class LoginUiState(
    val isSignedIn : Boolean = false,
    val isLoading : Boolean = false,
    val errorMessage : String? = null
)
