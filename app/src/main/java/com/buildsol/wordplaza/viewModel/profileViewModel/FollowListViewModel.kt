package com.buildsol.wordplaza.viewModel.profileViewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.buildsol.wordplaza.firebase.firestore.FireStore
import com.buildsol.wordplaza.navigation.FollowListRoute
import com.buildsol.wordplaza.viewModel.AppViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FollowListViewModel(
    private val fireStore: FireStore,
    savedStateHandle: SavedStateHandle
) : AppViewModel(savedStateHandle) {

    private val auth = FirebaseAuth.getInstance()
    private val route = savedStateHandle.toRoute<FollowListRoute>()
    private val _uiState = MutableStateFlow(FollowListUiState())
    val uiState = _uiState.asStateFlow()

    init {
        _appUiState.update {
            it.copy(
                hideTopBar = false,
                hideBottomNavigation = false,
                currentRoute = route
            )
        }
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val users = if (route.mode == ProfileViewModel.FOLLOWING_MODE) {
                    fireStore.getFollowingUsers(route.userId)
                } else {
                    fireStore.showFollower(route.userId)
                }
                val currentUserId = auth.currentUser?.uid.orEmpty()
                val followingIds = fireStore.getFollowingUsers(currentUserId).map { it.id }.toSet()
                _uiState.update {
                    it.copy(isLoading = false, users = users, currentFollowingIds = followingIds)
                }
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = exception.message ?: "Unable to load users.")
                }
            }
        }
    }

    fun toggleFollow(targetUserId: String) {
        val currentUserId = auth.currentUser?.uid.orEmpty()
        if (currentUserId.isBlank() || currentUserId == targetUserId) return

        viewModelScope.launch {
            try {
                val isFollowing = targetUserId in _uiState.value.currentFollowingIds
                if (isFollowing) {
                    fireStore.unfollowUser(currentUserId, targetUserId)
                    _uiState.update { it.copy(currentFollowingIds = it.currentFollowingIds - targetUserId) }
                } else {
                    fireStore.followUser(currentUserId, targetUserId)
                    _uiState.update { it.copy(currentFollowingIds = it.currentFollowingIds + targetUserId) }
                }
            } catch (exception: Exception) {
                _uiState.update { it.copy(errorMessage = exception.message ?: "Unable to update follow state.") }
            }
        }
    }

    fun title(): String = if (route.mode == ProfileViewModel.FOLLOWING_MODE) "Following" else "Followers"
}
