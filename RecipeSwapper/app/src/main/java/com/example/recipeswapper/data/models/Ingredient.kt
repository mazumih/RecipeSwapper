package com.example.recipeswapper.data.models

import com.example.recipeswapper.data.database.IngredientEntity

data class Ingredient(
    val name: String = "",
    val quantity: String = ""
)

fun IngredientEntity.toDomain() : Ingredient {
    return Ingredient(
        name = name,
        quantity = quantity
    )
}

fun Ingredient.toEntity(recipeId: String) : IngredientEntity {
    return IngredientEntity(
        recipeId = recipeId,
        name = name,
        quantity = quantity
    )
}