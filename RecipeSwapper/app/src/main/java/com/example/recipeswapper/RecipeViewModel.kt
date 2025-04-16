package com.example.recipeswapper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recipeswapper.data.database.Recipe
import com.example.recipeswapper.data.repositories.RecipesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RecipesState(val recipes: List<Recipe>)
data class QueryState(val query: String)

class RecipeViewModel(
    private val repository: RecipesRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow(QueryState(""))
    val searchQuery = _searchQuery.asStateFlow()

    val state = combine(repository.recipes, _searchQuery) { recipes, query ->
        if (query.query.isBlank()) recipes else recipes.filter {
            it.name.contains(query.query, ignoreCase = true)
        }
    }.map { RecipesState(recipes = it) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = RecipesState(emptyList())
    )

    fun updateQuery(query: String) {
        _searchQuery.update { it.copy(query = query) }
    }

    fun addRecipe(recipe: Recipe) = viewModelScope.launch {
        repository.upsert(recipe)
    }

    fun updateFav(id: Int, isFav: Boolean = true) = viewModelScope.launch {
        repository.updateFav(id, isFav)
    }

    fun resetAllFav() = viewModelScope.launch {
        repository.resetAllFav()
    }

    fun deleteRecipe(recipe: Recipe) = viewModelScope.launch {
        repository.delete(recipe)
    }
}