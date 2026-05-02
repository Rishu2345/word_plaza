package com.buildsol.wordplaza.model

data class Word(
    val id: String = "",
    val word: String = "",
    val normalizedWord: String = "",
    val meaning: String = "",
    val synonyms: List<String> = emptyList(),
    val antonyms: List<String> = emptyList(),
    val pronunciation:String? = null,
    val egUse: String = "",
    val examples: List<String> = emptyList(),
    val like: Int = 0,
    val dislike: Int = 0,
    val saved: Boolean = false,
    val postCount: Int = 0,
    val createdByUserId: String = "",
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)
