package com.buildsol.wordplaza.view.profileChange

import android.graphics.Bitmap
import android.net.Uri

data class ProfileUpdateState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val displayName: String = "",
    val profilePictureUrl: String = "",
    val selectedGalleryUri: Uri? = null,
    val selectedCameraBitmap: Bitmap? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null
) {
    val hasProfileImage: Boolean
        get() = selectedCameraBitmap != null || selectedGalleryUri != null || profilePictureUrl.isNotBlank()
}
