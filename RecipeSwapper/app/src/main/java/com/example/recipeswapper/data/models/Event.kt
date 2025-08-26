package com.example.recipeswapper.data.models

import com.example.recipeswapper.data.database.EventEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Event(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val host: String = "",
    val location: String = "",
    val date: Long? = null
)

fun EventEntity.toDomain() : Event {
    return Event(
        id = id,
        title = title,
        description = description,
        host = host,
        location = location,
        date = date
    )
}

fun Event.toEntity() : EventEntity {
    return EventEntity(
        id = id,
        title = title,
        description = description,
        host = host,
        location = location,
        date = date ?: 0
    )
}

fun Event.formatDate() : String {
    return date?.let {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it))
    } ?: ""
}