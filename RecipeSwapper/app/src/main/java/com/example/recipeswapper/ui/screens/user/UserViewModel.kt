package com.example.recipeswapper.ui.screens.user

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeswapper.data.models.User
import com.example.recipeswapper.data.repositories.BadgesRepository
import com.example.recipeswapper.data.repositories.UserRepository
import com.example.recipeswapper.utils.NotificationHelper
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class UserState(
    val currentUser: User? = null,
    var isLoading: Boolean = false
)

interface UserActions {
    fun updateUser(user: User)
    fun updateUserDB(id: String)
    fun setImage(imageURI: Uri)
    fun toggleFavourite(id: String, notifier: NotificationHelper)
}

@OptIn(ExperimentalCoroutinesApi::class)
class UserViewModel(
    private val userRepository: UserRepository,
    private val badgesRepository: BadgesRepository
) : ViewModel() {

    val userId = MutableStateFlow(FirebaseAuth.getInstance().currentUser?.uid)

    val state = userId.filterNotNull().flatMapLatest { id -> userRepository.getUser(id) }
        .map { UserState(currentUser = it) }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = UserState()
        )

    val actions = object : UserActions {

        override fun updateUser(user: User) {
            viewModelScope.launch {
                userRepository.upsertUser(user)
            }
        }

        override fun updateUserDB(id: String) {
            viewModelScope.launch {
                userRepository.getUserDB(id)
            }
        }

        override fun setImage(imageURI: Uri) {
            viewModelScope.launch {
                val user = state.value.currentUser ?: return@launch
                val updatedUser = user.copy(profileImage = imageURI.toString())
                userRepository.upsertUser(updatedUser)
            }
        }

        override fun toggleFavourite(id: String, notifier: NotificationHelper) {
            val user = state.value.currentUser ?: return

            viewModelScope.launch {
                if (user.favouriteRecipes.contains(id)) {
                    userRepository.removeFavourite(user.id, id)
                } else {
                    userRepository.addFavourite(user.id, id)
                }
                badgesRepository.checkBadges(user.id, notifier)
            }
        }
    }

}