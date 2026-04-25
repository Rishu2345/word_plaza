package com.buildsol.wordplaza.view.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalFlexBoxApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFlexBoxApi::class)
@Composable
fun HomeScreen(
    wordOfTheDay: Word,
    onWordClick: (Word) -> Unit,
    onBookmarkClick: (Word) -> Unit,
    onLikeClick: (Word) -> Unit,
    onDislikeClick: (Word) -> Unit,
    wordFeed: List<Word>,
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        // Title
        item {
            Text(
                text = "Word of the Day",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        // Word of the day card (big one)
        item {

        }

        // Feed
        items(wordFeed) { word ->

        }
    }
}

data class Word(
    val id :String,
    val word:String,
    val meaning:String,
    val synonyms:List<String>,
    val antonyms:List<String>,
    val pronunciation:String? = null,
    val egUse:String
)