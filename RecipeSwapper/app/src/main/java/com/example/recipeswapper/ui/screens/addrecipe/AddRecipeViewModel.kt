package com.example.recipeswapper.ui.screens.addrecipe

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.recipeswapper.data.database.Recipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AddRecipeState(
    val name: String = "",
    val description: String = "",
    val imageUri: Uri = Uri.EMPTY,
    val isFav: Boolean = false
) {
    val canSubmit get() = name.isNotBlank() && description.isNotBlank()

    fun toRecipe() = Recipe(
        name = name,
        description =  description,
        imageUri = imageUri.toString(),
        isFav = isFav
    )
}

interface AddRecipeActions {
    fun setName(name: String)
    fun setDescription(description: String)
    fun setUriImage(imageUri: Uri)
    fun setFav(isFav: Boolean)
    fun clearForm()
}

class AddRecipeViewModel : ViewModel() {
    private val _state = MutableStateFlow(AddRecipeState())
    val state = _state.asStateFlow()

    val actions = object : AddRecipeActions {
        override fun setName(name: String) {
            _state.update { it.copy(name = name) }
        }

        override fun setDescription(description: String) {
            _state.update { it.copy(description = description) }
        }

        override fun setUriImage(imageUri: Uri) {
            _state.update { it.copy(imageUri = imageUri) }
        }

        override fun setFav(isFav: Boolean) {
            _state.update { it.copy(isFav = isFav) }
        }

        override fun clearForm() {
            _state.update {
                it.copy(
                    name = "",
                    description = "",
                    imageUri = Uri.EMPTY
                )
            }
        }
    }
}