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
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val authorId: String = "",
    val author: String = "",
    val imageURI: Uri = Uri.EMPTY,
    val ingredients: List<Ingredient> = emptyList(),
    val category: String = "",
    val recipe: String = "",
    val portions: Int = 1,
    val prepTime: Int = 0,
    val difficulty: String = ""
) {
    val canSubmit get() =
        title.isNotBlank() && ingredients.isNotEmpty() && description.isNotBlank() && imageURI != Uri.EMPTY &&
                category.isNotBlank() && recipe.isNotBlank() && prepTime > 0 && difficulty.isNotBlank()

    fun toRecipe() = Recipe(
        id = id,
        title = title,
        description =  description,
        imagePath = imageURI.toString(),
        authorId = authorId,
        author = author,
        ingredients = ingredients,
        category = category,
        recipe = recipe,
        portions = portions,
        prepTime = prepTime,
        difficulty = difficulty
    )
}

interface AddRecipeActions {
    fun setTitle(title: String)
    fun setDescription(description: String)
    fun setCategory(category: String)
    fun setImage(imageURI: Uri)
    fun addIngredient(name: String, quantity: String)
    fun updateIngredient(index: Int, newName: String, newQuantity: String)
    fun removeIngredient(index: Int)
    fun setRecipe(recipe: String)
    fun setPortions(portions: Int)
    fun setPrepTime(prepTime: Int)
    fun setDifficulty(difficulty: String)
    fun addRecipe(recipe: Recipe, authorId: String, author: String, notifier: NotificationHelper)
    fun updateRecipe(recipe: Recipe)
    fun loadRecipe(recipe: Recipe)
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

        override fun setCategory(category: String) {
            _state.update { it.copy(category = category) }
        }

        override fun setImage(imageURI: Uri) {
            _state.update { it.copy(imageURI = imageURI) }
        }

        override fun addIngredient(name: String, quantity: String) {
            val currentIngredients = _state.value.ingredients + Ingredient(name, quantity)
            _state.update { it.copy(ingredients = currentIngredients) }
        }

        override fun updateIngredient(index: Int, newName: String, newQuantity: String) {
            val updatedIngredients = state.value.ingredients.toMutableList().apply {
                this[index] = this[index].copy(name = newName, quantity = newQuantity)
            }
            _state.update { it.copy(ingredients = updatedIngredients) }
        }

        override fun removeIngredient(index: Int) {
            val currentIngredients = _state.value.ingredients.toMutableList().apply { removeAt(index) }
            _state.update { it.copy(ingredients = currentIngredients) }
        }

        override fun setRecipe(recipe: String) {
            _state.update { it.copy(recipe = recipe) }
        }

        override fun setPortions(portions: Int) {
            _state.update { it.copy(portions = portions) }
        }

        override fun setPrepTime(prepTime: Int) {
            _state.update { it.copy(prepTime = prepTime) }
        }

        override fun setDifficulty(difficulty: String) {
            _state.update { it.copy(difficulty = difficulty) }
        }

        override fun addRecipe(recipe: Recipe, authorId: String, author: String, notifier: NotificationHelper) {
            viewModelScope.launch {
                recipesRepository.upsertRecipe(recipe, author, authorId)
                badgesRepository.checkBadges(authorId, notifier)
            }
        }

        override fun updateRecipe(recipe: Recipe) {
            viewModelScope.launch {
                recipesRepository.upsertRecipe(recipe, recipe.author, recipe.authorId)
            }
        }

        override fun loadRecipe(recipe: Recipe) {
            _state.update {
                it.copy(
                    id = recipe.id,
                    title = recipe.title,
                    description = recipe.description,
                    imageURI = Uri.parse(recipe.imagePath),
                    author = recipe.author,
                    authorId = recipe.authorId,
                    ingredients = recipe.ingredients,
                    category = recipe.category,
                    recipe = recipe.recipe,
                    portions = recipe.portions,
                    prepTime = recipe.prepTime,
                    difficulty = recipe.difficulty
                )
            }
        }
    }
}