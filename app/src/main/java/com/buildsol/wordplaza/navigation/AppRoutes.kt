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
data class NoteEditorRoute(
    val noteId: String,
    val workspaceId: String
): GTCAppRoute {

    val id = noteId
    val wkId = workspaceId
    companion object {
        fun create(noteId: String, workspaceId: String) = NoteEditorRoute(noteId = noteId, workspaceId =  workspaceId)
    }

}