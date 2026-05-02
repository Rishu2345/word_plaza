package com.buildsol.wordplaza.model

data class GoogleSignInResult(
    val idToken: String?,
    val displayName: String?,
    val id: String?,
    val profilePictureUrl: String?,
    val firebaseUid: String? = null,
    val email: String? = null
)
