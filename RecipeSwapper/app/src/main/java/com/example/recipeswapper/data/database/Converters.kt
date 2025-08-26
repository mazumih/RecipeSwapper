package com.example.recipeswapper.data.database

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromList(list: List<String>?): String {
        return list?.joinToString(",") ?: ""
    }

    @TypeConverter
    fun toList(string: String?): List<String> {
        return string?.split(",")?.filter { it.isNotEmpty() } ?: emptyList()
    }
}