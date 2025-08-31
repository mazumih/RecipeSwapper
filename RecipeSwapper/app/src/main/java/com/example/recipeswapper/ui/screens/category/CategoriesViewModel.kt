package com.example.recipeswapper.ui.screens.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeswapper.data.models.Category
import com.example.recipeswapper.data.repositories.CategoriesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CategoriesState(
    val categories: List<Category> = emptyList(),
)

interface CategoriesActions {
    fun updateCategoriesDB()
}

class CategoriesViewModel (
    private val categoriesRepository: CategoriesRepository
) : ViewModel() {

    val state = categoriesRepository.getAllCategories().map { CategoriesState(categories = it) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = CategoriesState()
    )

    val actions = object : CategoriesActions {
        override fun updateCategoriesDB() {
            viewModelScope.launch {
                categoriesRepository.getCategoriesDB()
            }
        }
    }
}