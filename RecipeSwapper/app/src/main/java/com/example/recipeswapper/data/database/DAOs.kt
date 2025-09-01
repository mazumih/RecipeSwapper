package com.example.recipeswapper.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Transaction
    @Query("SELECT * FROM RecipeEntity")
    fun getAllRecipes(): Flow<List<RecipeWithIngredients>>

    @Upsert
    suspend fun upsertRecipe(recipe: RecipeEntity)

    @Delete
    suspend fun deleteRecipe(recipe: RecipeEntity)

    @Upsert
    suspend fun upsertIngredients(ingredients: List<IngredientEntity>)

    @Query("DELETE FROM IngredientEntity WHERE recipeId = :recipeId")
    suspend fun deleteIngredients(recipeId: String)

    @Transaction
    @Query("SELECT * FROM RecipeEntity WHERE author = :userId")
    suspend fun getUserRecipes(userId: String): List<RecipeWithIngredients>

    @Query("SELECT * FROM RecipeEntity WHERE id = :recipeId")
    suspend fun getRecipeById(recipeId: String): RecipeEntity?
}

@Dao
interface UserDao {
    @Query("SELECT * FROM UserEntity WHERE id = :userId")
    fun getUser(userId: String): Flow<UserEntity?>

    @Upsert
    suspend fun upsertUser(user: UserEntity)

    @Delete
    suspend fun deleteUser(user: UserEntity)

    @Query("SELECT * FROM UserEntity WHERE id = :userId")
    suspend fun getUserById(userId: String): UserEntity?
}

@Dao
interface BadgeDao {
    @Query("SELECT * FROM BadgeEntity")
    fun getAllBadges(): Flow<List<BadgeEntity>>

    @Upsert
    suspend fun upsertBadge(badge: BadgeEntity)

    @Query("SELECT * FROM BadgeEntity WHERE id = :badgeId")
    suspend fun getBadgeById(badgeId: String): BadgeEntity?

    @Query("SELECT * FROM BadgeEntity WHERE id NOT IN (:unlockedBadges)")
    suspend fun getLockedBadges(unlockedBadges: List<String>): List<BadgeEntity>
}

@Dao
interface EventDao {
    @Query("SELECT * FROM EventEntity")
    fun getAllEvents(): Flow<List<EventEntity>>

    @Upsert
    suspend fun upsertEvent(event: EventEntity)

    @Query("SELECT * FROM EventEntity WHERE id = :eventId")
    suspend fun getEvent(eventId: String): EventEntity?

    @Delete
    suspend fun deleteEvent(event: EventEntity)

    @Query("SELECT * FROM EventEntity WHERE host = :userId")
    suspend fun getUserEvents(userId: String): List<EventEntity>
}

@Dao
interface CategoryDao {
    @Query("SELECT * FROM CategoryEntity")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Upsert
    suspend fun upsertCategory(category: CategoryEntity)

    @Query("SELECT * FROM CategoryEntity WHERE id = :categoryId")
    suspend fun getCategoryById(categoryId: String): CategoryEntity?
}