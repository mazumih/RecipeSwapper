package com.example.recipeswapper.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeswapper.data.models.Category
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
    var search: String = "",
    val category: Category? = null
) {
    val filteredRecipes: List<Recipe>
        get() = recipes.filter { recipe ->
            val searchFilter = search.isBlank() || recipe.title.contains(search, ignoreCase = true)
            val categoryFilter = category == null || recipe.category.equals(category.name)
            searchFilter && categoryFilter
        }
}

interface RecipesActions {
    fun updateRecipesDB()
    fun updateSearch(query: String)
    fun deleteRecipe(recipe: Recipe)
    fun setCategoryFilter(category: Category)
}

class RecipesViewModel(
    private val recipesRepository: RecipesRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _categoryFilter = MutableStateFlow<Category?>(null)

    val state = recipesRepository.getAllRecipes()
        .combine(_searchQuery) { recipes, query ->
            RecipesState(
                recipes = recipes,
                search = query
            )
        }
        .combine(_categoryFilter) { recipesState, category -> recipesState.copy(category = category) }
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

        override fun setCategoryFilter(category: Category) {
            _categoryFilter.value = category
        }
    }
}