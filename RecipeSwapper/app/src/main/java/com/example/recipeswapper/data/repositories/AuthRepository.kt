package com.example.recipeswapper.data.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class AuthRepository(private val auth: FirebaseAuth) {

    suspend fun register(email: String, password: String): Result<FirebaseUser> {
        return try {
            val user = auth.createUserWithEmailAndPassword(email, password).await().user
            if (user != null) Result.success(user) else Result.failure(IllegalStateException("La registrazione è fallita"))
        } catch (e: Exception) {
            Result.failure(getErrorMessage(e))
        }
    }

    suspend fun login(email: String, password: String): Result<FirebaseUser> {
        return try {
            val user = auth.signInWithEmailAndPassword(email, password).await().user
            if (user != null) Result.success(user) else Result.failure(IllegalStateException("L'accesso è fallito"))
        } catch (e: Exception) {
            Result.failure(getErrorMessage(e))
        }
    }

    suspend fun loginWithGoogle(idToken: String): Result<FirebaseUser> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val user = auth.signInWithCredential(credential).await().user
            if (user != null) Result.success(user) else Result.failure(IllegalStateException("Login Google fallito"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }

    fun getCurrentUser() : FirebaseUser? = auth.currentUser

    private fun getErrorMessage(e: Exception): Exception {
        return when (e) {
            is FirebaseAuthInvalidCredentialsException -> Exception("L'Email o la Password inseriti non sono corretti")
            is FirebaseAuthUserCollisionException -> Exception("L'Email inserita è già associata ad un account")
            is FirebaseAuthInvalidUserException -> Exception("Utente non trovato")
            else -> Exception("Errore imprevisto: ${e.message}")
        }
    }

}