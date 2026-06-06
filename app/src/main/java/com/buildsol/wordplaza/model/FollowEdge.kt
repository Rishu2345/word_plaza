package com.buildsol.wordplaza.model

data class FollowEdge(
    val ownerUserId: String = "",
    val relatedUserId: String = "",
    val createdAt: Long = 0L
)
