package com.example.recipeswapper.ui.screens.authentication

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeswapper.data.repositories.AuthRepository
import com.example.recipeswapper.data.models.User
import com.example.recipeswapper.data.repositories.UserRepository
import com.example.recipeswapper.utils.Google
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthState(
    val currentUser: FirebaseUser? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

interface AuthActions {
    fun register(email: String, password: String, username: String)
    fun login(email: String, password: String)
    fun loginWithGoogle(googleSignIn: Google)
    fun logout()
    fun isFormValid(email: String, password: String, username: String, confirmPassword: String, isLogin: Boolean) : Boolean
    fun resetError()
}

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState(currentUser = authRepository.getCurrentUser()))
    val state = _state.asStateFlow()

    val actions = object : AuthActions {

        override fun register(email: String, password: String, username: String) {
            viewModelScope.launch {
                updateState(isLoading = true)
                val result = authRepository.register(email, password)

                result.onSuccess { user ->
                    val newUser = User(
                        id = user.uid,
                        email = user.email ?: "",
                        username = username
                    )
                    userRepository.upsertUser(newUser)
                    updateState(user = user, isLoading = false)
                }.onFailure { e ->
                    updateState(isLoading = false, error = e.message ?: "Errore sconosciuto")
                }
            }
        }

        override fun login(email: String, password: String) {
            viewModelScope.launch {
                updateState(isLoading = true)
                val result = authRepository.login(email, password)

                result.onSuccess { user ->
                    if (userRepository.getUserById(user.uid) == null) {
                        userRepository.getUserDB(user.uid)
                    }
                    updateState(user = user, isLoading = false)
                }.onFailure { e ->
                    updateState(isLoading = false, error = e.message ?: "Errore sconosciuto")
                }
            }
        }

        override fun loginWithGoogle(googleSignIn: Google) {

            viewModelScope.launch {
                updateState(isLoading = true)
                val tokenResult = googleSignIn.signIn()

                tokenResult.onSuccess { token ->
                    val result = authRepository.loginWithGoogle(token)

                    result.onSuccess { user ->
                        if (userRepository.getUserById(user.uid) == null) {
                            val userDB = userRepository.getUserDB(user.uid)
                            if (userDB == null) {
                                val newUser = User(
                                    id = user.uid,
                                    email = user.email ?: "",
                                    username = user.displayName ?: user.email?.substringBefore("@") ?: ""
                                )
                                userRepository.upsertUser(newUser)
                            }
                        }
                        updateState(user = user, isLoading = false)
                    }.onFailure { e ->
                        updateState(isLoading = false, error = e.message ?: "Errore sconosciuto")
                    }
                }.onFailure { e ->
                    updateState(isLoading = false, error = "")
                }
            }
        }

        override fun logout() {
            authRepository.logout()
            _state.update { AuthState() }
        }

        override fun isFormValid(
            email: String,
            password: String,
            username: String,
            confirmPassword: String,
            isLogin: Boolean
        ): Boolean {
            if (email.isBlank() || password.isBlank() || (!isLogin && username.isBlank()) || (!isLogin && confirmPassword.isBlank())) {
                updateState(isLoading = false, error = "Compilare tutti i campi per procedere")
                return false
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                updateState(isLoading = false, error = "L'email inserita non è valida")
                return false
            }
            if (!isLogin && password.length < 6) {
                updateState(isLoading = false, error = "La password deve essere di almeno 6 caratteri")
                return false
            }
            if (!isLogin && !password.equals(confirmPassword)) {
                updateState(isLoading = false, error = "Le password non corrispondono")
                return false
            }
            return true
        }

        override fun resetError() {
            updateState(error = null)
        }

    }

    private fun updateState(user: FirebaseUser? = null, isLoading: Boolean = false, error: String? = null) {
        _state.update { it.copy(currentUser = user, isLoading = isLoading, error = error) }
    }

}