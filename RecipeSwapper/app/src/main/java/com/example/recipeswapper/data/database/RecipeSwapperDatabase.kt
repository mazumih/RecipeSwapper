package com.example.recipeswapper.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Recipe::class], version = 4)
abstract class RecipeSwapperDatabase: RoomDatabase() {
    abstract fun recipesDAO(): RecipesDAO
}