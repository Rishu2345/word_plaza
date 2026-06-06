package com.buildsol.wordplaza.model

data class SavedPost(
    val userId: String = "",
    val postId: String = "",
    val savedAt: Long = 0L
)
