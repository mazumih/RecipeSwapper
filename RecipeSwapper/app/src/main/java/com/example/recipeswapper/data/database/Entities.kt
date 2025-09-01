package com.example.recipeswapper.data.database

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class RecipeEntity(
    @PrimaryKey val id: String = "",
    @ColumnInfo val title: String = "",
    @ColumnInfo val description: String = "",
    @ColumnInfo val imagePath: String = "",
    @ColumnInfo val author: String = "",
    @ColumnInfo val category: String = "",
    @ColumnInfo val recipe: String = "",
    @ColumnInfo val portions: Int = 1,
    @ColumnInfo val prepTime: Int = 0,
    @ColumnInfo val difficulty: String = ""
)

@Entity(
    primaryKeys = ["recipeId", "name"],
    foreignKeys = [
        ForeignKey(
            entity = RecipeEntity::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class IngredientEntity(
    @ColumnInfo val recipeId: String = "",
    @ColumnInfo val name: String = "",
    @ColumnInfo val quantity: String = ""
)

data class RecipeWithIngredients(
    @Embedded val recipe: RecipeEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "recipeId"
    )
    val ingredients: List<IngredientEntity>
)

@Entity
data class UserEntity(
    @PrimaryKey val id: String = "",
    @ColumnInfo val username: String = "",
    @ColumnInfo val email: String = "",
    @ColumnInfo val profileImage: String = "",
    @ColumnInfo val favouriteRecipes: List<String> = emptyList(),
    @ColumnInfo val unlockedBadges: List<String> = emptyList()
)

@Entity
data class BadgeEntity(
    @PrimaryKey val id: String = "",
    @ColumnInfo val name: String = "",
    @ColumnInfo val description: String = "",
    @ColumnInfo val icon: String = "",
    @ColumnInfo val triggerType: String = "",
    @ColumnInfo val triggerValue: Int = 0
)

@Entity
data class EventEntity(
    @PrimaryKey val id: String = "",
    @ColumnInfo val title: String = "",
    @ColumnInfo val description: String = "",
    @ColumnInfo val host: String = "",
    @ColumnInfo val location: String = "",
    @ColumnInfo val date: Long = 0,
    @ColumnInfo val maxParticipants: Long = 0,
    @ColumnInfo val participants: List<String> = emptyList(),
    @ColumnInfo val recipeId: String = ""
)

@Entity
data class CategoryEntity(
    @PrimaryKey val id: String = "",
    @ColumnInfo val name: String = "",
    @ColumnInfo val icon: String = ""
)