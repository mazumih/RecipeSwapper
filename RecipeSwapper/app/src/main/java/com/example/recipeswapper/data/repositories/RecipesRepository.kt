package com.example.recipeswapper.data.repositories

import com.example.recipeswapper.data.database.Recipe
import com.example.recipeswapper.data.database.RecipesDAO
import kotlinx.coroutines.flow.Flow

class RecipesRepository(
    private val dao: RecipesDAO
) {
    val recipes: Flow<List<Recipe>> = dao.getAll()

    suspend fun upsert(recipe: Recipe) = dao.upsert(recipe)

    suspend fun delete(recipe: Recipe) = dao.delete(recipe)
}