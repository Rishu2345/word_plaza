package com.buildsol.wordplaza.view.profileChange

import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class ProfileImageStorage(
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) {
    suspend fun uploadProfileImage(userId: String, imageBytes: ByteArray): String {
        require(userId.isNotBlank()) { "User id is required to upload a profile image." }
        require(imageBytes.isNotEmpty()) { "Profile image is empty." }

        val imageReference = storage.reference
            .child(PROFILE_IMAGES_FOLDER)
            .child(userId)
            .child(AVATAR_FILE_NAME)
        val metadata = StorageMetadata.Builder()
            .setContentType("image/jpeg")
            .build()

        imageReference.putBytes(imageBytes, metadata).await()
        return imageReference.downloadUrl.await().toString()
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

    private companion object {
        const val PROFILE_IMAGES_FOLDER = "profileImages"
        const val AVATAR_FILE_NAME = "avatar.jpg"
    }
}
