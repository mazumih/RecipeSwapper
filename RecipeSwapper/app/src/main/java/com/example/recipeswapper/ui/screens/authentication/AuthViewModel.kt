package com.example.recipeswapper.ui.screens.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeswapper.data.repositories.AuthRepository
import com.example.recipeswapper.data.models.User
import com.example.recipeswapper.data.repositories.UserRepository
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
    fun logout()
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
                _state.update { it.copy(isLoading = true, error = null) }
                val user = authRepository.register(email, password)
                if (user != null) {
                    val newUser = User(
                        id = user.uid,
                        email = user.email ?: "",
                        username = username,
                        profileImage = "",
                        favouriteRecipes = emptyList()
                    )
                    userRepository.upsertUser(newUser)
                    _state.update { it.copy(currentUser = user, isLoading = false) }
                } else {
                    _state.update { it.copy(isLoading = false, error = "Registrazione fallita") }
                }
            }
        }

        override fun login(email: String, password: String) {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true, error = null) }
                val user = authRepository.login(email, password)
                if (user != null) {
                    if (userRepository.getUserById(user.uid) == null) {
                        userRepository.getUserDB(user.uid)
                    }
                    _state.update { it.copy(currentUser = user, isLoading = false) }
                } else {
                    _state.update { it.copy(isLoading = false, error = "Accesso fallito") }
                }
            }
        }

        override fun logout() {
            authRepository.logout()
            _state.update { AuthState() }
        }

    }

}