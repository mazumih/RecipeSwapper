package com.example.recipeswapper.data.models

data class User(
    val id: String = "",
    val username: String = "",
    val email: String = "",
    val profileImage: String = "",
    val favouriteRecipes: List<String> = emptyList(),
    val unlockedBadges: List<String> = emptyList()
)