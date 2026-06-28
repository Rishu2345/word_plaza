package com.buildsol.wordplaza.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface GTCAppRoute

@Serializable
object HomeScreenRoute : GTCAppRoute

@Serializable
object SearchScreenRoute : GTCAppRoute

@Serializable
object ProfileScreenRoute : GTCAppRoute

@Serializable
data class FollowListRoute(
    val userId: String,
    val mode: String
) : GTCAppRoute

@Serializable
object CreatePostRoute : GTCAppRoute

@Serializable
object ProfileUpdateRoute : GTCAppRoute

@Serializable
object LoginScreenRoute : GTCAppRoute

@Serializable
object OnboardingScreenRoute : GTCAppRoute
