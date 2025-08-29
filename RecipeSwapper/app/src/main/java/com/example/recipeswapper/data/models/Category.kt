package com.example.recipeswapper.data.models

import com.example.recipeswapper.data.database.CategoryEntity

data class Category(
    val id: String = "",
    val name: String = ""
)

fun CategoryEntity.toDomain() : Category {
    return Category(
        id = id,
        name = name
    )
}

fun Category.toEntity() : CategoryEntity {
    return CategoryEntity(
        id = id,
        name = name
    )
}