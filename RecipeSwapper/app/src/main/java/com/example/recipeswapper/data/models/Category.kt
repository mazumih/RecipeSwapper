package com.example.recipeswapper.data.models

import com.example.recipeswapper.R
import com.example.recipeswapper.data.database.CategoryEntity

val CATEGORY_ORDER = listOf(
    "Antipasti",
    "Primi Piatti",
    "Secondi",
    "Contorni",
    "Dolci",
    "Lievitati"
)

data class Category(
    val id: String = "",
    val name: String = "",
    val icon: String = ""
)

fun CategoryEntity.toDomain() : Category {
    return Category(
        id = id,
        name = name,
        icon = icon
    )
}

fun Category.toEntity() : CategoryEntity {
    return CategoryEntity(
        id = id,
        name = name,
        icon = icon
    )
}

fun getCategoryIcon(icon: String): Int {
    val categoryIcons = mapOf(
        "ic_antipasti" to R.drawable.antipasti,
        "ic_primi_piatti" to R.drawable.primi_piatti,
        "ic_secondi" to R.drawable.secondi,
        "ic_contorni" to R.drawable.contorni,
        "ic_dolci" to R.drawable.dolci,
        "ic_lievitati" to R.drawable.lievitati
    )
    return categoryIcons[icon] ?: R.drawable.icona2
}