package com.buildsol.wordplaza.model

data class PostUserState(
    val postId: String = "",
    val liked: Boolean = false,
    val disliked: Boolean = false,
    val saved: Boolean = false
)
