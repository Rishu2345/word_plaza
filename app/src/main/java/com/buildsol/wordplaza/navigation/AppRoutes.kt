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
data class ProfileUpdateRoute(
    val userName:String,
    val profilePic:String
) : GTCAppRoute {
    @Transient
    val name = userName

    @Transient
    val profile = profilePic

    companion object{
        fun create(userName:String,profilePic:String) = ProfileUpdateRoute(userName,profilePic)
    }
}

@Serializable
object LoginScreenRoute : GTCAppRoute

@Serializable
object OnboardingScreenRoute : GTCAppRoute