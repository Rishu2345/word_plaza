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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.buildsol.wordplaza.model.GoogleSignInResult

class FirebaseAuth(private val context: Context){

    private val credentialManager = CredentialManager.create(context)
    private fun createSignInRequest(): GetCredentialRequest{
        Log.d("signIn","Creating sign In Request")
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(context.getString(R.string.app_name)) // we have to add the api key here
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
            val user = handleSignIn(result)
            user

        } catch(e: GetCredentialException){
            Log.e("signIn","Error Signing In",e)
            null
        }

    }

    private fun handleSignIn(result: GetCredentialResponse):GoogleSignInResult?{
        return when(val credential = result.credential){
            is CustomCredential -> {
                if(credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL){
                    Log.d("signIn","Checkpoint 1")
                    try{
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        val idToken = googleIdTokenCredential.idToken
                        val firebaseCredential = GoogleAuthProvider.getCredential(idToken,null)
                        FirebaseAuth.getInstance().signInWithCredential(firebaseCredential)
                            .addOnCompleteListener { task ->
                                if(task.isSuccessful){
                                    Log.d("GoogleSignIn","Firebase Authentication Successful")
                                }else{
                                    Log.e("GoogleSignIn","Firebase Authentication Failed",task.exception)
                                }
                            }
                        return GoogleSignInResult(
                            googleIdTokenCredential.id,
                            googleIdTokenCredential.displayName,
                            googleIdTokenCredential.idToken,
                            googleIdTokenCredential.profilePictureUri.toString()
                        )
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e("GoogleSignIn", "Invalid Google ID Token", e)
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
            FirebaseAuth.getInstance().signOut()

            val request = ClearCredentialStateRequest()
            credentialManager.clearCredentialState(request)

            Log.d("signOut", "User signed out successfully")

        } catch (e: Exception) {
            Log.e("signOut", "Error signing out", e)
        }
    }

}

