package com.example.recipeswapper.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeswapper.data.database.Recipe
import com.example.recipeswapper.data.repositories.FavouriteRecipesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class FavouriteRecipesState(val favRecipes: List<Recipe>)

class AddFavouritesViewModel(
    private val repository: FavouriteRecipesRepository
) : ViewModel() {
    val state = repository.favRecipes.map { FavouriteRecipesState(favRecipes = it) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = FavouriteRecipesState(emptyList())
    )

    fun addFavouriteRecipe(favRecipe: Recipe) = viewModelScope.launch {
        repository.upsert(favRecipe)
    }

    fun removeFavouriteRecipe(favRecipe: Recipe) = viewModelScope.launch {
        repository.remove(favRecipe)
    }
}