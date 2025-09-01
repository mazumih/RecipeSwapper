package com.example.recipeswapper.ui.screens.categorydetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeswapper.data.models.Category
import com.example.recipeswapper.data.models.Recipe
import com.example.recipeswapper.data.repositories.RecipesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class CategoryDetailsState(
    val recipes: List<Recipe> = emptyList(),
    val category: Category? = null
) {
    val filteredRecipes: List<Recipe>
        get() = recipes.filter { recipe -> category == null || recipe.category.equals(category.name, ignoreCase = true) }
}

interface CategoryDetailsActions {
    fun setCategoryFilter(category: Category)
}

class CategoryDetailsViewModel(
    private val recipesRepository: RecipesRepository
) : ViewModel() {

    private val _categoryFilter = MutableStateFlow<Category?>(null)

    val state = recipesRepository.getAllRecipes()
        .combine(_categoryFilter) { recipes, category ->
            CategoryDetailsState(
                recipes = recipes,
                category = category
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = CategoryDetailsState(emptyList())
        )

    val actions = object : CategoryDetailsActions {

        override fun setCategoryFilter(category: Category) {
            _categoryFilter.value = category
        }
    }
}