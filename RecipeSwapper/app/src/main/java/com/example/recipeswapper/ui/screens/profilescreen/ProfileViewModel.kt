package com.example.recipeswapper.ui.screens.profilescreen

import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ProfileState(
    val name: String = "",
    val bio: String = "",
    val imageUri: Uri = Uri.EMPTY
)

interface ProfileActions {
    fun setName(name: String)
    fun setBio(bio: String)
    fun setImage(imageUri: Uri)
}

class ProfileViewModel(
    // repository
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    val actions = object : ProfileActions {
        override fun setName(name: String) {
            _state.update { it.copy(name = name) }
        }

        override fun setBio(bio: String) {
            _state.update { it.copy(bio = bio) }
        }

        override fun setImage(imageUri: Uri) {
            _state.update { it.copy(imageUri = imageUri) }
        }

    }
}