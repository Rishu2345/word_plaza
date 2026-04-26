package com.buildsol.wordplaza.viewModel.homeScreenViewModel

import androidx.lifecycle.SavedStateHandle
import com.buildsol.wordplaza.model.Word
import com.buildsol.wordplaza.viewModel.AppViewModel

class HomeScreenViewModel(
    savedStateHandle: SavedStateHandle,
): AppViewModel(savedStateHandle) {

    fun wordOfTheDay(): Word{
        return Word(
            id = "1",
            word = "Serendipity",
            meaning = "The occurrence of events by chance in a happy or beneficial way.",
            synonyms = listOf("fluke", "chance", "fortune"),
            antonyms = listOf("misfortune", "bad luck"),
            egUse = "Finding that book was pure serendipity.",
            saved = true,
            like = 120,
            dislike = 10,
            pronunciation = "/ˌserənˈdipədē/"
        )
    }

    fun wordFeed():List<Word> = emptyList()

    fun onWordClick(word: Word) {

    }

    fun onBookmarkClick(wordId:String) {

    }
    fun onLikeClick(wordId:String) {

    }
    fun onDislikeClick(wordId:String) {

    }



}