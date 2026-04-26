package com.buildsol.wordplaza.model

data class Word(
    val id :String,
    val word:String,
    val meaning:String,
    val synonyms:List<String>,
    val antonyms:List<String>,
    val pronunciation:String? = null,
    val egUse:String,
    val like:Int,
    val dislike:Int,
    val saved:Boolean
)