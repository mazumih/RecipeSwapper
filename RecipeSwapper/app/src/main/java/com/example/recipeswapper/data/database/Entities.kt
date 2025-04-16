package com.example.recipeswapper.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Recipe(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo val name: String,
    @ColumnInfo val description: String,
    @ColumnInfo val imageUri: String?,
    @ColumnInfo val isFav: Boolean
)

@Entity
data class Badge(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val isUnlocked: Boolean,
    val description: String
)