package com.example.recipeswapper.ui.screens.addrecipe

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeswapper.data.models.Ingredient
import com.example.recipeswapper.data.models.Recipe
import com.example.recipeswapper.data.repositories.BadgesRepository
import com.example.recipeswapper.data.repositories.RecipesRepository
import com.example.recipeswapper.utils.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddRecipeState(
    val title: String = "",
    val description: String = "",
    val imageURI: Uri = Uri.EMPTY,
    val ingredients: List<Ingredient> = emptyList(),
    val categories: List<String> = emptyList()
) {
    val canSubmit get() = title.isNotBlank() && ingredients.isNotEmpty() && description.isNotBlank() && imageURI != Uri.EMPTY

    fun toRecipe() = Recipe(
        title = title,
        description =  description,
        imagePath = imageURI.toString(),
        ingredients = ingredients,
        categories = categories
    )
}

interface AddRecipeActions {
    fun setTitle(title: String)
    fun setDescription(description: String)
    fun setImage(imageURI: Uri)
    fun addIngredient(name: String, quantity: String)
    fun removeIngredient(index: Int)
    fun addRecipe(recipe: Recipe, author: String, notifier: NotificationHelper, isCameraImage: Boolean)
    fun updateRecipe(recipe: Recipe, isCameraImage: Boolean)
}

class AddRecipeViewModel(
    private val recipesRepository: RecipesRepository,
    private val badgesRepository: BadgesRepository
) : ViewModel() {

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

        override fun addRecipe(recipe: Recipe, author: String, notifier: NotificationHelper, isCameraImage: Boolean) {
            viewModelScope.launch {
                recipesRepository.upsertRecipe(recipe, author, isCameraImage)
                badgesRepository.checkBadges(author, notifier)
            }
        }

        override fun updateRecipe(recipe: Recipe, isCameraImage: Boolean) {
            viewModelScope.launch {
                recipesRepository.upsertRecipe(recipe, isCameraImage = isCameraImage)
            }
        }
    }
}