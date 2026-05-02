package com.buildsol.wordplaza.firebase.authentication

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.buildsol.wordplaza.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.GoogleAuthProvider
import com.buildsol.wordplaza.model.GoogleSignInResult
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth as GoogleFirebaseAuth
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FirebaseAuth(private val context: Context){

    private val credentialManager = CredentialManager.create(context)
    private fun createSignInRequest(): GetCredentialRequest{
        Log.d("signIn","Creating sign In Request")
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(context.getString(R.string.google_web_client_id))
            .setAutoSelectEnabled(false)
            .build()

        return GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
    }

    suspend fun signIn():GoogleSignInResult?{
        return try{
            Log.d("signIn","Signing In")
            val request = createSignInRequest()
            val result = credentialManager.getCredential(context,request)
            handleSignIn(result)
        } catch(e: GetCredentialException){
            Log.e("signIn","Error Signing In",e)
            null
        } catch(e: Exception){
            Log.e("signIn","Unexpected Error Signing In",e)
            null
        }

    }

    private suspend fun handleSignIn(result: GetCredentialResponse):GoogleSignInResult?{
        return when(val credential = result.credential){
            is CustomCredential -> {
                if(credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL){
                    Log.d("signIn","Checkpoint 1")
                    try{
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        val idToken = googleIdTokenCredential.idToken
                        val firebaseCredential = GoogleAuthProvider.getCredential(idToken,null)
                        return GoogleSignInResult(
                            idToken = idToken,
                            displayName = googleIdTokenCredential.displayName,
                            id = googleIdTokenCredential.id,
                            profilePictureUrl = googleIdTokenCredential.profilePictureUri?.toString(),
                            firebaseUid = GoogleFirebaseAuth.getInstance()
                                .signInWithCredential(firebaseCredential)
                                .await()
                                .user
                                ?.uid,
                            email = googleIdTokenCredential.id
                        )
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e("GoogleSignIn", "Invalid Google ID Token", e)
                        null
                    } catch (e: Exception) {
                        Log.e("GoogleSignIn", "Firebase Authentication Failed", e)
                        null
                    }
                } else {
                    Log.e("GoogleSignIn", "Unexpected credential type")
                    null
                }
            }
            else -> {
                Log.e("GoogleSignIn", "Unexpected credential type")
                null
            }
        }
    }
    suspend fun signOut() {
        try {
            GoogleFirebaseAuth.getInstance().signOut()

            val request = ClearCredentialStateRequest()
            credentialManager.clearCredentialState(request)

            Log.d("signOut", "User signed out successfully")

        } catch (e: Exception) {
            Log.e("signOut", "Error signing out", e)
        }
    }

    private suspend fun Task<AuthResult>.await(): AuthResult =
        suspendCancellableCoroutine { continuation ->
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
