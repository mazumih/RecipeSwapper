package com.example.recipeswapper.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Recipe::class], version = 3)
abstract class RecipeSwapperDatabase: RoomDatabase() {
    abstract fun recipesDAO(): RecipesDAO
}