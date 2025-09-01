package com.example.recipeswapper.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeswapper.data.models.Recipe
import com.example.recipeswapper.data.repositories.RecipesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class RecipesState(
    val recipes: List<Recipe> = emptyList(),
    var search: String = ""
) {
    val filteredRecipes: List<Recipe>
        get() = recipes.filter { recipe -> search.isBlank() || recipe.title.contains(search, ignoreCase = true) }
}

interface RecipesActions {
    fun updateRecipesDB()
    fun updateSearch(query: String)
    fun deleteRecipe(recipe: Recipe)
}

class RecipesViewModel(
    private val recipesRepository: RecipesRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val state = recipesRepository.getAllRecipes()
        .combine(_searchQuery) { recipes, query ->
            RecipesState(
                recipes = recipes,
                search = query
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = RecipesState(emptyList())
        )

    val actions = object : RecipesActions {

        override fun updateRecipesDB() {
            viewModelScope.launch {
                recipesRepository.getRecipesDB()
            }
        }

        override fun updateSearch(query: String) {
            _searchQuery.value = query
        }

        override fun deleteRecipe(recipe: Recipe) {
            viewModelScope.launch {
                recipesRepository.deleteRecipe(recipe)
            }
        }
    }
}