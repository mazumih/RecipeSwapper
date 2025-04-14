package com.example.recipeswapper.data.repositories

import com.example.recipeswapper.R
import com.example.recipeswapper.data.database.Badge
import com.example.recipeswapper.data.database.BadgeDao
import kotlinx.coroutines.flow.Flow

class BadgesRepository(
    private val badgesDao: BadgeDao
) {
    val badges: Flow<List<Badge>> = badgesDao.getAllBadges()

    suspend fun unlockBadge(badgeName: String) = badgesDao.unlockBadge(badgeName)

    suspend fun insertBadges(badges: List<Badge>) = badgesDao.insertAll(badges)

    suspend fun getBadgeCount() = badgesDao.getBadgeCount()

    suspend fun isUnlocked(badgeName: String) : Boolean = badgesDao.isUnlocked(badgeName)

    suspend fun lockAll() = badgesDao.lockAll()
}
