package com.example.recipeswapper.utils

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.example.recipeswapper.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

class Google(private val ctx: Context) {

    val credentialManager = CredentialManager.create(ctx)

    suspend fun signIn(): Result<String> {
        return try {

            val googleIdOption = GetGoogleIdOption.Builder()
                .setServerClientId(ctx.getString(R.string.default_web_client_id))
                .setFilterByAuthorizedAccounts(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val credential = credentialManager.getCredential(ctx, request).credential
            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                Result.success(googleIdTokenCredential.idToken)
            } else {
                Result.failure(Exception("Credenziali non valide"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}