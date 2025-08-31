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
    val category: String = "",
    val recipe: String = "",
    val portions: Int = 1,
    val prepTime: Int = 0,
    val difficulty: String = ""
)

fun RecipeWithIngredients.toDomain() : Recipe {
    return Recipe(
        id = recipe.id,
        title = recipe.title,
        description = recipe.description,
        imagePath = recipe.imagePath,
        author = recipe.author,
        ingredients = ingredients.map { ing -> ing.toDomain() },
        category = recipe.category,
        recipe = recipe.recipe,
        portions = recipe.portions,
        prepTime = recipe.prepTime,
        difficulty = recipe.difficulty
    )
}

fun Recipe.toEntity() : RecipeEntity {
    return RecipeEntity(
        id = id,
        title = title,
        description = description,
        imagePath = imagePath,
        author = author,
        category = category,
        recipe = recipe,
        portions = portions,
        prepTime = prepTime,
        difficulty = difficulty
    )
}