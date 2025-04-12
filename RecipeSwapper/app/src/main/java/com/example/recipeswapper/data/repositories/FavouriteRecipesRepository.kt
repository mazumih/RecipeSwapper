package com.example.recipeswapper.data.repositories

import com.example.recipeswapper.data.database.FavouriteRecipesDAO
import com.example.recipeswapper.data.database.Recipe
import kotlinx.coroutines.flow.Flow

class FavouriteRecipesRepository(
    private val dao: FavouriteRecipesDAO
) {
    val favRecipes: Flow<List<Recipe>> = dao.getAll()

    suspend fun upsert(favRecipe: Recipe) = dao.upsert(favRecipe)

    suspend fun delete(favRecipe: Recipe) = dao.delete(favRecipe)
}