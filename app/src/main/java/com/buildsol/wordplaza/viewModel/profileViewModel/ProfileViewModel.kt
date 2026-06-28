package com.buildsol.wordplaza.viewModel.profileViewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.buildsol.wordplaza.firebase.firestore.FireStore
import com.buildsol.wordplaza.navigation.FollowListRoute
import com.buildsol.wordplaza.navigation.ProfileScreenRoute
import com.buildsol.wordplaza.navigation.ProfileUpdateRoute
import com.buildsol.wordplaza.viewModel.AppViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val fireStore: FireStore,
    savedStateHandle: SavedStateHandle
) : AppViewModel(savedStateHandle) {

    private val auth = FirebaseAuth.getInstance()
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        _appUiState.update {
            it.copy(
                hideTopBar = false,
                hideBottomNavigation = false,
                currentRoute = ProfileScreenRoute
            )
        }
        loadProfile()
    }

    fun loadProfile() {
        val userId = auth.currentUser?.uid.orEmpty()
        if (userId.isBlank()) {
            _uiState.update { it.copy(isLoading = false, errorMessage = "Please sign in to view your profile.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val profile = fireStore.getUserData(userId)
                val posts = fireStore.loadProfilePosts(userId)
                _uiState.update {
                    it.copy(isLoading = false, profile = profile, posts = posts)
                }
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = exception.message ?: "Unable to load profile.")
                }
            }
        }
    }

    fun onEditProfileClick() {
        _appUiState.value.onNavigate(ProfileUpdateRoute)
    }

    fun onFollowersClick() {
        val userId = _uiState.value.profile?.id ?: auth.currentUser?.uid.orEmpty()
        if (userId.isNotBlank()) _appUiState.value.onNavigate(FollowListRoute(userId, FOLLOWERS_MODE))
    }

    fun onFollowingClick() {
        val userId = _uiState.value.profile?.id ?: auth.currentUser?.uid.orEmpty()
        if (userId.isNotBlank()) _appUiState.value.onNavigate(FollowListRoute(userId, FOLLOWING_MODE))
    }

    companion object {
        const val FOLLOWERS_MODE = "followers"
        const val FOLLOWING_MODE = "following"
    }
}
