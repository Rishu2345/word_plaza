package com.buildsol.wordplaza.model

data class PostReaction(
    val userId: String = "",
    val postId: String = "",
    val type: String = "",
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
) {
    companion object {
        const val TYPE_LIKE = "like"
        const val TYPE_DISLIKE = "dislike"
    }
}
