package com.example.recipeswapper.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipesDAO {
    @Query("SELECT * FROM recipe")
    fun getAll(): Flow<List<Recipe>>

    @Upsert
    suspend fun upsert(recipe: Recipe)

    @Delete
    suspend fun delete(trip: Recipe)
}