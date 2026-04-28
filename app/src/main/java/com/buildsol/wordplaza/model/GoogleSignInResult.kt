package com.buildsol.wordplaza.model

data class GoogleSignInResult(
    val idToken: String?,
    val displayName: String?,
    val id: String?,
    val profilePictureUrl: String?
)
