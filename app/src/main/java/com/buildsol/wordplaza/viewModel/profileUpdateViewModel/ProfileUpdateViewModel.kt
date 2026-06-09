package com.buildsol.wordplaza.viewModel.profileUpdateViewModel

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.ContactsContract
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buildsol.wordplaza.firebase.firestore.FireStore
import com.buildsol.wordplaza.navigation.HomeScreenRoute
import com.buildsol.wordplaza.navigation.LoginScreenRoute
import com.buildsol.wordplaza.navigation.NavCommand
import com.buildsol.wordplaza.view.profileChange.ProfileImageProcessor
import com.buildsol.wordplaza.view.profileChange.ProfileImageStorage
import com.buildsol.wordplaza.view.profileChange.ProfileUpdateState
import com.buildsol.wordplaza.viewModel.AppViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.timestampcamera.intalyx.db.DataStoreHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.swaggy.hotelpanel.db.DataStoreKey
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class ProfileUpdateViewModel(
    private val auth: FirebaseAuth,
    private val fireStore: FireStore,
    private val dataStoreHelper: DataStoreHelper,
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
                navigateToLoginPage()
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val displayName = dataStoreHelper.getString(DataStoreKey.USER_NAME).first()
            val profilePictureUrl = dataStoreHelper.getString(DataStoreKey.USER_IMAGE).first()
            _uiState.update {
                it.copy(
                    isLoading = false,
                    displayName = displayName,
                    profilePictureUrl = profilePictureUrl,

                )
            }
            _uiState.update { it.copy(isLoading = false) }
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


    fun saveProfile(context: Context) {
        val currentState = _uiState.value
        val displayName = currentState.displayName.trim()
        val firebaseUser = auth.currentUser

        if (firebaseUser == null) {
            _uiState.update { it.copy(errorMessage = "Please sign in before saving your profile.") }
            navigateToLoginPage()
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
            val imageUrl = "" // todo

            try {

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

    private suspend fun updateFirebaseAuthProfile(displayName: String, profilePictureUrl: String) {
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(displayName)
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

    //move to login page
    fun navigateToLoginPage(){
        _navCommandFlow.tryEmit(
            NavCommand.Navigate(
                destination = LoginScreenRoute
            )
        )
    }

    //move to home page
    fun navigateToHomePage(){
        _navCommandFlow.tryEmit(
            NavCommand.Navigate(
                destination = HomeScreenRoute
            )
        )
    }


}