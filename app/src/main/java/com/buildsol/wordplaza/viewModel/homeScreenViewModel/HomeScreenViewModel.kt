package com.buildsol.wordplaza.viewModel.homeScreenViewModel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.buildsol.wordplaza.firebase.firestore.FireStore
import com.buildsol.wordplaza.model.PostReaction
import com.buildsol.wordplaza.model.PostUserState
import com.buildsol.wordplaza.model.Word
import com.buildsol.wordplaza.navigation.HomeScreenRoute
import com.buildsol.wordplaza.navigation.ProfileScreenRoute
import com.buildsol.wordplaza.viewModel.AppViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeScreenViewModel(
    private val fireStore: FireStore,
    savedStateHandle: SavedStateHandle,
): AppViewModel(savedStateHandle) {

    private val auth = FirebaseAuth.getInstance()
    private val _uiState = MutableStateFlow(HomeScreenUiState())
    val uiState = _uiState.asStateFlow()

    init {
        _appUiState.update {
            it.copy(
                hideTopBar = false,
                hideBottomNavigation = false,
                currentRoute = HomeScreenRoute,
                onProfileClick = { onNavigateToProfile() }
            )
        }
        refreshHome()
    }

    fun refreshHome() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMassage = null, refreshFeed = true) }
            try {
//                val wordOfTheDay = fireStore.getWordOfTheDay()
                val posts = fireStore.loadFeedPosts()
                Log.e("Error", "the post is cleared")
                val currentUserId = auth.currentUser?.uid.orEmpty()
                val states = fireStore.getCurrentUserPostStates(currentUserId, posts.map { it.id })
                Log.e("Error", "the states is cleared")
                _uiState.update {
                    it.copy(
                        posts = posts,
                        postStates = states,
                        isLoading = false,
                        refreshFeed = false
                    )
                }
            } catch (exception: Exception) {
                Log.e("Error",exception.toString())
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        refreshFeed = false,
                        errorMassage = exception.message ?: "Unable to load feed."
                    )
                }
            }
        }
    }

    fun onBookmarkClick(postId: String) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val saved = fireStore.toggleSavedPost(userId, postId)
                updatePostState(postId) { it.copy(saved = saved) }
            } catch (exception: Exception) {
                _uiState.update { it.copy(errorMassage = exception.message ?: "Unable to save post.") }
            }
        }
    }

    fun onLikeClick(postId: String) {
        setReaction(postId, PostReaction.TYPE_LIKE)
    }

    fun onDislikeClick(postId: String) {
        setReaction(postId, PostReaction.TYPE_DISLIKE)
    }

    fun postWord(word: Word) {
        val userId = auth.currentUser?.uid
        if (userId.isNullOrBlank()) {
            _uiState.update { it.copy(errorMassage = "Please sign in before posting a word.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isPosting = true, errorMassage = null) }
            try {
                fireStore.publishWordPost(userId, word)
                _appUiState.update { it.copy(showPostBottomSheet = false) }
                _uiState.update { it.copy(isPosting = false) }
                refreshHome()
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        isPosting = false,
                        errorMassage = exception.message ?: "Unable to post this word."
                    )
                }
            }
        }
    }

    fun toggleBottomSheet(value: Boolean) {
        _appUiState.update { it.copy(showPostBottomSheet = value) }
    }

    private fun setReaction(postId: String, type: String) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val current = _uiState.value.postStates[postId]
                val alreadySelected = (type == PostReaction.TYPE_LIKE && current?.liked == true) ||
                    (type == PostReaction.TYPE_DISLIKE && current?.disliked == true)
                val nextState = if (alreadySelected) {
                    fireStore.removePostReaction(userId, postId)
                } else {
                    fireStore.setPostReaction(userId, postId, type)
                }
                updatePostState(postId) { nextState.copy(saved = current?.saved == true) }
            } catch (exception: Exception) {
                _uiState.update { it.copy(errorMassage = exception.message ?: "Unable to update reaction.") }
            }
        }
    }

    private fun updatePostState(postId: String, transform: (PostUserState) -> PostUserState) {
        _uiState.update { state ->
            val current = state.postStates[postId] ?: PostUserState(postId = postId)
            state.copy(postStates = state.postStates + (postId to transform(current)))
        }
    }

    private fun onNavigateToProfile() {
        _appUiState.value.onNavigate(ProfileScreenRoute)
    }
}
