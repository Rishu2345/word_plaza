package com.buildsol.wordplaza.navigation


import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed interface GTCAppRoute


@Serializable
object HomeScreenRoute : GTCAppRoute

@Serializable
object SearchScreenRoute : GTCAppRoute

@Serializable
object ProfileScreenRoute : GTCAppRoute

@Serializable
object CreatePostRoute :  GTCAppRoute

@Serializable
object ProfileUpdateRoute : GTCAppRoute


@Serializable
object LoginScreenRoute : GTCAppRoute

@Serializable
object OnboardingScreenRoute : GTCAppRoute