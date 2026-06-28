package com.buildsol.wordplaza.viewModel.profileViewModel

import com.buildsol.wordplaza.model.UserProfile
import com.buildsol.wordplaza.model.WordPost

data class ProfileUiState(
    val isLoading: Boolean = true,
    val isActionLoading: Boolean = false,
    val profile: UserProfile? = null,
    val posts: List<WordPost> = emptyList(),
    val errorMessage: String? = null
)

data class FollowListUiState(
    val isLoading: Boolean = true,
    val users: List<UserProfile> = emptyList(),
    val currentFollowingIds: Set<String> = emptySet(),
    val errorMessage: String? = null
)
