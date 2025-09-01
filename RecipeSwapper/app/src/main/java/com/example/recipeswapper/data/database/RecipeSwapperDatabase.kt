package com.example.recipeswapper.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        RecipeEntity::class,
        IngredientEntity::class,
        UserEntity::class,
        BadgeEntity::class,
        EventEntity::class,
        CategoryEntity::class
    ],
    version = 10
)
@TypeConverters(Converters::class)
abstract class RecipeSwapperDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao
    abstract fun userDao(): UserDao
    abstract fun badgeDao(): BadgeDao
    abstract fun eventDao(): EventDao
    abstract fun categoryDao(): CategoryDao
}