package com.buildsol.wordplaza.view.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.buildsol.wordplaza.model.PostUserState
import com.buildsol.wordplaza.model.Word
import com.buildsol.wordplaza.model.WordPost
import com.buildsol.wordplaza.viewModel.homeScreenViewModel.HomeScreenViewModel

@Composable
fun HomeScreen(viewModel: HomeScreenViewModel) {
    val appUiState by viewModel._appUiState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    HomeScreen(
        wordOfTheDay = uiState.wordOfTheDay,
        wordFeed = uiState.posts,
        postStates = uiState.postStates,
        onBookmarkClick = viewModel::onBookmarkClick,
        onLikeClick = viewModel::onLikeClick,
        onDislikeClick = viewModel::onDislikeClick,
        bottomSheet = appUiState.showPostBottomSheet,
        toggleBottomSheet = viewModel::toggleBottomSheet,
        onPostWord = viewModel::postWord,
        isPosting = uiState.isPosting,
        isLoading = uiState.isLoading,
        errorMassage = uiState.errorMassage,
        refreshFeed = viewModel::refreshHome,
        isRefreshing = uiState.refreshFeed
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    wordOfTheDay: Word?,
    onBookmarkClick: (String) -> Unit,
    onLikeClick: (String) -> Unit,
    onDislikeClick: (String) -> Unit,
    wordFeed: List<WordPost>,
    postStates: Map<String, PostUserState>,
    bottomSheet: Boolean = false,
    toggleBottomSheet: (Boolean) -> Unit = {},
    onPostWord: (Word) -> Unit = {},
    isPosting: Boolean = false,
    isLoading: Boolean = false,
    errorMassage: String? = null,
    refreshFeed:()-> Unit,
    isRefreshing:Boolean
) {
    val pullToRefreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        modifier = Modifier.fillMaxSize(),
        state = pullToRefreshState,
        isRefreshing = isRefreshing,
        onRefresh = {
            refreshFeed()
        }
    ) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            errorMassage?.let { message ->
                item {
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            items(wordFeed, key = { it.id }) { post ->
                val state = postStates[post.id]
                    ?: PostUserState(postId = post.id)

                WordBox(
                    word = post,
                    userState = state,
                    onBookmarkClick = { onBookmarkClick(post.id) },
                    onLikeClick = { onLikeClick(post.id) },
                    onDislikeClick = { onDislikeClick(post.id) }
                )
            }
        }



        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }

    if (bottomSheet) {
        AddWordBottomSheet(
            isPosting = isPosting,
            errorMessage = errorMassage,
            onDismiss = { toggleBottomSheet(false) },
            onPostWord = onPostWord
        )
    }
}
