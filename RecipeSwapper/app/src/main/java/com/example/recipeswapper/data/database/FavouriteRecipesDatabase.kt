package com.example.recipeswapper.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Recipe::class], version = 1)
abstract class FavouriteRecipesDatabase: RoomDatabase() {
    abstract fun favRecipesDAO(): FavouriteRecipesDAO
}