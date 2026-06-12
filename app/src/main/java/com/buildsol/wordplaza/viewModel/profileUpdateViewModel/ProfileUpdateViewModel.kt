package com.buildsol.wordplaza.viewModel.profileUpdateViewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.buildsol.wordplaza.firebase.firestore.FireStore
import com.buildsol.wordplaza.images.Image
import com.buildsol.wordplaza.navigation.HomeScreenRoute
import com.buildsol.wordplaza.navigation.LoginScreenRoute
import com.buildsol.wordplaza.navigation.NavCommand
import com.buildsol.wordplaza.view.profileChange.ProfileUpdateState
import com.buildsol.wordplaza.viewModel.AppViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.timestampcamera.intalyx.db.DataStoreHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.swaggy.hotelpanel.db.DataStoreKey
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class ProfileUpdateViewModel(
    private val fireStore: FireStore,
    savedStateHandle: SavedStateHandle
) : AppViewModel(savedStateHandle = savedStateHandle) {

    val auth = Firebase.auth
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


            _uiState.update {
                it.copy(
                    isLoading = false,
                    displayName = auth.currentUser?.displayName ?: "",
                    profilePictureUrl = auth.currentUser?.photoUrl?.toString() ?: "",

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


    fun saveProfile() {
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
                    avatarId = currentState.selectedAvatarId?.name ?: ""
                )
                updateFirebaseAuthProfile(displayName)

                _uiState.update {
                    it.copy(
                        isSaving = false,
                        displayName = displayName,
                        profilePictureUrl = imageUrl,
                        successMessage = "Profile updated."
                    )
                }
                navigateToHomePage()
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

    private suspend fun updateFirebaseAuthProfile(displayName: String) {
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

    fun onAvtarClicked(image: Image){
        _uiState.update {
            it.copy(
                selectedAvatarId = image
            )
        }
    }

    fun toggleAvatarSheet(visibility:Boolean){
        _uiState.update {
            it.copy(
                showAvtarSheet = visibility
            )
        }
    }


}