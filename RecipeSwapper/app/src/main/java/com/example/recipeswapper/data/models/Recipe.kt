package com.example.recipeswapper.data.models

import com.example.recipeswapper.data.database.RecipeEntity
import com.example.recipeswapper.data.database.RecipeWithIngredients

data class Recipe(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val imagePath: String = "",
    val author: String = "",
    val ingredients: List<Ingredient> = emptyList(),
    val categories: List<String> = emptyList()
)

fun RecipeWithIngredients.toDomain() : Recipe {
    return Recipe(
        id = recipe.id,
        title = recipe.title,
        description = recipe.description,
        imagePath = recipe.imagePath,
        author = recipe.author,
        ingredients = ingredients.map { ing -> ing.toDomain() },
        categories = recipe.categories
    )
}

fun Recipe.toEntity() : RecipeEntity {
    return RecipeEntity(
        id = id,
        title = title,
        description = description,
        imagePath = imagePath,
        author = author,
        categories = categories
    )
}