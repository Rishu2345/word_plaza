package com.buildsol.wordplaza.viewModel.homeScreenViewModel

import com.buildsol.wordplaza.model.PostUserState
import com.buildsol.wordplaza.model.Word
import com.buildsol.wordplaza.model.WordPost

data class HomeScreenUiState(
    val wordOfTheDay: Word? = null,
    val posts: List<WordPost> = emptyList(),
    val postStates: Map<String, PostUserState> = emptyMap(),
    val isLoading: Boolean = true,
    val isPosting: Boolean = false,
    val errorMassage: String? = null,
    val bottomSheet: Boolean = false,
    val refreshFeed: Boolean = false
)
