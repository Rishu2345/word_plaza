package com.buildsol.wordplaza.model

data class WordPost(
    val id: String = "",
    val wordId: String = "",
    val authorId: String = "",
    val authorUsername: String = "",
    val authorDisplayName: String = "",
    val authorProfilePictureUrl: String = "",
    val word: String = "",
    val normalizedWord: String = "",
    val meaning: String = "",
    val pronunciation: String? = null,
    val synonyms: List<String> = emptyList(),
    val antonyms: List<String> = emptyList(),
    val example: String = "",
    val caption: String = "",
    val visibility: String = VISIBILITY_PUBLIC,
    val likeCount: Int = 0,
    val dislikeCount: Int = 0,
    val saveCount: Int = 0,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
) {
    companion object {
        const val VISIBILITY_PUBLIC = "public"
    }
}
