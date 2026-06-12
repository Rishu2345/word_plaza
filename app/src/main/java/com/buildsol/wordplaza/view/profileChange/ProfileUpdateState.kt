package com.buildsol.wordplaza.view.profileChange

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.ui.res.painterResource
import com.buildsol.wordplaza.R
import com.buildsol.wordplaza.images.Image

data class ProfileUpdateState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val displayName: String = "",
    val profilePictureUrl: String = "",
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val selectedAvatarId: Image? = null,
    val showAvtarSheet: Boolean = false,

) {
    val avatars = listOf(
        Avatar(
            id = 1,
            imageRes = Image.FIRST,
            title = "https://example.com/avatar1.jpg"
        ),
        Avatar(
            id = 3,
            imageRes = Image.SECOND,
            title = "https://example.com/avatar1.jpg"
        ),
        Avatar(
            id = 2,
            imageRes = Image.THREE,
            title = "https://example.com/avatar1.jpg"
        )
    )
}
