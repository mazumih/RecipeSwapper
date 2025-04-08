package com.example.recipeswapper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeswapper.data.database.Recipe
import com.example.recipeswapper.data.repositories.RecipesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class RecipesState(val recipes: List<Recipe>)

class RecipeViewModel(
    private val repository: RecipesRepository
) : ViewModel() {
    val state = repository.recipes.map { RecipesState(recipes = it) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = RecipesState(emptyList())
    )

    fun addRecipe(recipe: Recipe) = viewModelScope.launch {
        repository.upsert(recipe)
    }

    fun deleteRecipe(recipe: Recipe) = viewModelScope.launch {
        repository.delete(recipe)
    }
}