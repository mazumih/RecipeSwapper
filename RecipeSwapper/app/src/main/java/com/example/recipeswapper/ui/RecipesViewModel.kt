package com.example.recipeswapper.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeswapper.data.models.Recipe
import com.example.recipeswapper.data.repositories.BadgesRepository
import com.example.recipeswapper.data.repositories.RecipesRepository
import com.example.recipeswapper.utils.NotificationHelper
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
        get() = if (search.isBlank()) recipes else recipes.filter { it.title.contains(search, ignoreCase = true) }
}

class RecipesViewModel(
    private val recipesRepository: RecipesRepository,
    private val badgesRepository: BadgesRepository
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

    fun updateRecipesDB() {
        viewModelScope.launch {
            recipesRepository.getRecipesDB()
        }
    }

    fun addRecipe(recipe: Recipe, author: String, notifier: NotificationHelper) = viewModelScope.launch {
        recipesRepository.addRecipe(recipe, author)
        badgesRepository.checkBadges(author, notifier)
    }

    fun updateSearch(query: String) {
        _searchQuery.value = query
    }

}