package com.example.recipeswapper.ui.screens.addrecipe

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.recipeswapper.data.models.Ingredient
import com.example.recipeswapper.data.models.Recipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AddRecipeState(
    val title: String = "",
    val description: String = "",
    val imageURI: Uri = Uri.EMPTY,
    val ingredients: List<Ingredient> = emptyList()
) {
    val canSubmit get() = title.isNotBlank() && ingredients.isNotEmpty() && description.isNotBlank() && imageURI != Uri.EMPTY

    fun toRecipe() = Recipe(
        title = title,
        description =  description,
        imagePath = imageURI.toString(),
        ingredients = ingredients
    )
}

interface AddRecipeActions {
    fun setTitle(title: String)
    fun setDescription(description: String)
    fun setImage(imageURI: Uri)
    fun addIngredient(name: String, quantity: String)
    fun removeIngredient(index: Int)
}

class AddRecipeViewModel : ViewModel() {
    private val _state = MutableStateFlow(AddRecipeState())
    val state = _state.asStateFlow()

    val actions = object : AddRecipeActions {

        override fun setTitle(title: String) {
            _state.update { it.copy(title = title) }
        }

        override fun setDescription(description: String) {
            _state.update { it.copy(description = description) }
        }

        override fun setImage(imageURI: Uri) {
            _state.update { it.copy(imageURI = imageURI) }
        }

        override fun addIngredient(name: String, quantity: String) {
            val currentIngredients = _state.value.ingredients + Ingredient(name, quantity)
            _state.update { it.copy(ingredients = currentIngredients) }
        }

        override fun removeIngredient(index: Int) {
            val currentIngredients = _state.value.ingredients.toMutableList().apply { removeAt(index) }
            _state.update { it.copy(ingredients = currentIngredients) }
        }
    }
}