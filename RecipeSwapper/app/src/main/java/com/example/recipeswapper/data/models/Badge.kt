package com.example.recipeswapper.data.models

import com.example.recipeswapper.R
import com.example.recipeswapper.data.database.BadgeEntity

enum class BadgeType {
    Recipes,
    Favourites,
    Events,
    Dolci
}

data class Badge(
    val id: String,
    val name: String,
    val description: String,
    val icon: String,
    val type: BadgeType,
    val triggerValue: Int
)

fun BadgeEntity.toDomain() : Badge {
    return Badge(
        id = id,
        name = name,
        description = description,
        icon = icon,
        type = try {
            BadgeType.valueOf(triggerType)
        } catch (e: Exception) {
            throw (IllegalArgumentException())
        },
        triggerValue = triggerValue
    )
}

fun Badge.toEntity() : BadgeEntity {
    return BadgeEntity(
        id = id,
        name = name,
        description = description,
        icon = icon,
        triggerType = type.name,
        triggerValue = triggerValue
    )
}

fun getBadgeIcon(icon: String): Int {
    val badgeIcons = mapOf(
        "ic_badge1" to R.drawable.bree_badge,
        "ic_badge2" to R.drawable.susan_badge,
        "ic_badge3" to R.drawable.lynette_badge,
        "ic_badge4" to R.drawable.gabrielle_badge
    )
    return badgeIcons[icon] ?: R.drawable.icona2
}