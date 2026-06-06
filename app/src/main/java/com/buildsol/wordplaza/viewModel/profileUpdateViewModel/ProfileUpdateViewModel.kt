package com.buildsol.wordplaza.viewModel.profileUpdateViewModel

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buildsol.wordplaza.firebase.firestore.FireStore
import com.buildsol.wordplaza.view.profileChange.ProfileImageProcessor
import com.buildsol.wordplaza.view.profileChange.ProfileImageStorage
import com.buildsol.wordplaza.view.profileChange.ProfileUpdateState
import com.buildsol.wordplaza.viewModel.AppViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class ProfileUpdateViewModel(
    private val auth: FirebaseAuth,
    private val fireStore: FireStore,
    private val imageProcessor: ProfileImageProcessor,
    private val imageStorage: ProfileImageStorage,
    savedStateHandle: SavedStateHandle
) : AppViewModel(savedStateHandle = savedStateHandle) {
    private val _uiState = MutableStateFlow(ProfileUpdateState())
    val uiState: StateFlow<ProfileUpdateState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            val firebaseUser = auth.currentUser
            if (firebaseUser == null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Please sign in before updating your profile."
                    )
                }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val userProfile = fireStore.getUserData(firebaseUser.uid)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        displayName = userProfile?.displayName.orEmpty()
                            .ifBlank { firebaseUser.displayName.orEmpty() },
                        profilePictureUrl = userProfile?.profilePictureUrl.orEmpty()
                            .ifBlank { firebaseUser.photoUrl?.toString().orEmpty() },
                        selectedGalleryUri = null,
                        selectedCameraBitmap = null
                    )
                }
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        displayName = firebaseUser.displayName.orEmpty(),
                        profilePictureUrl = firebaseUser.photoUrl?.toString().orEmpty(),
                        errorMessage = exception.message ?: "Unable to load your profile."
                    )
                }
            }
        }
    }

    fun onDisplayNameChange(displayName: String) {
        _uiState.update {
            it.copy(
                displayName = displayName,
                errorMessage = null,
                successMessage = null
            )
        }
    }

    fun onGalleryImageSelected(uri: Uri) {
        _uiState.update {
            it.copy(
                selectedGalleryUri = uri,
                selectedCameraBitmap = null,
                errorMessage = null,
                successMessage = null
            )
        }
    }

    fun onCameraImageCaptured(bitmap: Bitmap) {
        _uiState.update {
            it.copy(
                selectedGalleryUri = null,
                selectedCameraBitmap = bitmap,
                errorMessage = null,
                successMessage = null
            )
        }
    }

    fun saveProfile(context: Context) {
        val currentState = _uiState.value
        val displayName = currentState.displayName.trim()
        val firebaseUser = auth.currentUser

        if (firebaseUser == null) {
            _uiState.update { it.copy(errorMessage = "Please sign in before saving your profile.") }
            return
        }

        if (displayName.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Name cannot be empty.") }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(isSaving = true, errorMessage = null, successMessage = null)
            }

            try {
                val imageUrl = uploadSelectedImageIfNeeded(context, firebaseUser.uid, currentState)
                    ?: currentState.profilePictureUrl

                fireStore.updateUserProfile(
                    userId = firebaseUser.uid,
                    displayName = displayName,
                    profilePictureUrl = imageUrl
                )
                updateFirebaseAuthProfile(displayName, imageUrl)

                _uiState.update {
                    it.copy(
                        isSaving = false,
                        displayName = displayName,
                        profilePictureUrl = imageUrl,
                        selectedGalleryUri = null,
                        selectedCameraBitmap = null,
                        successMessage = "Profile updated."
                    )
                }
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = exception.message ?: "Unable to save your profile."
                    )
                }
            }
        }
    }

    private suspend fun uploadSelectedImageIfNeeded(
        context: Context,
        userId: String,
        state: ProfileUpdateState
    ): String? {
        val imageBytes = when {
            state.selectedCameraBitmap != null -> imageProcessor.bytesFromBitmap(state.selectedCameraBitmap)
            state.selectedGalleryUri != null -> imageProcessor.bytesFromUri(context, state.selectedGalleryUri)
            else -> null
        } ?: return null

        return imageStorage.uploadProfileImage(userId, imageBytes)
    }

    private suspend fun updateFirebaseAuthProfile(displayName: String, profilePictureUrl: String) {
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(displayName)
            .setPhotoUri(profilePictureUrl.takeIf { it.isNotBlank() }?.let(Uri::parse))
            .build()

        auth.currentUser?.updateProfile(profileUpdates)?.await()
    }

    private suspend fun <T> Task<T>.await(): T = suspendCancellableCoroutine { continuation ->
        addOnSuccessListener { result ->
            continuation.resume(result)
        }
        addOnFailureListener { exception ->
            continuation.resumeWithException(exception)
        }
        addOnCanceledListener {
            continuation.cancel()
        }
    }
}