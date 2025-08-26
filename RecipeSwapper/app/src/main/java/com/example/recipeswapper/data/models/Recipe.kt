package com.example.recipeswapper.data.models

data class Recipe(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val imagePath: String = "",
    val author: String = "",
    val ingredients: List<Ingredient> = emptyList()
)