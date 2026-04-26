package com.buildsol.wordplaza.navigation


import kotlinx.serialization.Serializable

@Serializable
sealed interface GTCAppRoute

@Serializable
object FirstScreen : GTCAppRoute

@Serializable
object NoteListRoute : GTCAppRoute



@Serializable
data class SecondScreen(
    val name: String
): GTCAppRoute {
    companion object {
        fun create(name: String) = SecondScreen(name)
    }

}

@Serializable
object HomeScreenRoute : GTCAppRoute

@Serializable
object SearchScreenRoute : GTCAppRoute

@Serializable
object ProfileScreenRoute : GTCAppRoute

@Serializable
object CreatePostRoute :  GTCAppRoute