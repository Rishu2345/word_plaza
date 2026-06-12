package com.buildsol.wordplaza.model

data class UserProfile(
    val id: String = "",
    val username: String = "",
    val displayName: String = "",
    val email: String = "",
    val profilePictureUrl: String = "",
    val avatarId:String = "",
    val bio: String = "",
    val followerCount: Int = 0,
    val followingCount: Int = 0,
    val postCount: Int = 0,
    val followingIds: List<String> = emptyList(),
    val followerIds: List<String> = emptyList(),
    val postedWordIds: List<String> = emptyList(),
    val createdAt: Long = 0L,
    val lastSeen: Long = 0L
)
