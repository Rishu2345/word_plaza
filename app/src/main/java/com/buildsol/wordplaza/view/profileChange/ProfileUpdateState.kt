package com.buildsol.wordplaza.view.profileChange

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.ui.res.painterResource
import com.buildsol.wordplaza.R

data class ProfileUpdateState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val displayName: String = "",
    val profilePictureUrl: String = "",
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val selectedAvatarId: Int? = null,
    val showAvtarSheet: Boolean = false,

) {
    val avatars = listOf(
        Avatar(
            id = 1,
            imageRes = R.drawable.onboarding_first,
            title = "https://example.com/avatar1.jpg"
        ),
        Avatar(
            id = 3,
            imageRes = R.drawable.onboarding_first,
            title = "https://example.com/avatar1.jpg"
        ),
        Avatar(
            id = 2,
            imageRes = R.drawable.onboarding_first,
            title = "https://example.com/avatar1.jpg"
        )
    )
}
