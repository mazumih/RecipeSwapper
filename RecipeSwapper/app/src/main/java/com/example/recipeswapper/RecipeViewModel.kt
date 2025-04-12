package com.example.recipeswapper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeswapper.data.database.Recipe
import com.example.recipeswapper.data.repositories.RecipesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class RecipesState(val recipes: List<Recipe>)

class RecipeViewModel(
    private val repository: RecipesRepository
) : ViewModel() {
    /**
    val state = repository.recipes.map { RecipesState(recipes = it) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = RecipesState(emptyList())
    )*/

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val state = combine(repository.recipes, _searchQuery) { recipes, query ->
        if (query.isBlank()) recipes else recipes.filter {
            it.name.contains(query, ignoreCase = true)
        }
    }.map { RecipesState(recipes = it) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = RecipesState(emptyList())
    )

    fun updateQuery(query: String) {
        _searchQuery.value = query
    }

    fun addRecipe(recipe: Recipe) = viewModelScope.launch {
        repository.upsert(recipe)
    }

    fun deleteRecipe(recipe: Recipe) = viewModelScope.launch {
        repository.delete(recipe)
    }
}