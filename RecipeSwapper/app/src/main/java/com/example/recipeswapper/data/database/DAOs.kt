package com.example.recipeswapper.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
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
    suspend fun delete(recipe: Recipe)
}

@Dao
interface FavouriteRecipesDAO {
    @Query("SELECT * FROM recipe")
    fun getAll(): Flow<List<Recipe>>

    @Upsert
    suspend fun upsert(favRecipe: Recipe)

    @Delete
    suspend fun remove(favRecipe: Recipe)
}

@Dao
interface BadgeDao {
    @Query("SELECT * FROM badge")
    fun getAllBadges(): Flow<List<Badge>>

    @Insert
    suspend fun insertAll(badges: List<Badge>)

    @Query("UPDATE badge SET isUnlocked = 1 WHERE name = :badgeName AND isUnlocked = 0")
    suspend fun unlockBadge(badgeName: String)

    @Query("SELECT COUNT(*) FROM badge")
    suspend fun getBadgeCount(): Int

    /* debugging purpose */
    @Query("UPDATE badge SET isUnlocked = 0")
    suspend fun lockAll()
}