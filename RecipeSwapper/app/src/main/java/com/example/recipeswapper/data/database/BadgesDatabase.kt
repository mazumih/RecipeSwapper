package com.example.recipeswapper.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Badge::class], version = 2)
abstract class BadgesDatabase : RoomDatabase() {
    abstract fun badgeDao(): BadgeDao
}