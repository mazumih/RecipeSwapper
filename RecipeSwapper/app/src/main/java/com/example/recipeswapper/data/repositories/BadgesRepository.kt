package com.example.recipeswapper.data.repositories

import com.example.recipeswapper.data.database.BadgeDao
import com.example.recipeswapper.data.database.BadgeEntity
import com.example.recipeswapper.data.models.Badge
import com.example.recipeswapper.data.models.BadgeType
import com.example.recipeswapper.data.models.toDomain
import com.example.recipeswapper.utils.NotificationHelper
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class BadgesRepository(
    private val dao: BadgeDao,
    private val firestore: FirebaseFirestore,
    private val userRepository: UserRepository,
    private val recipesRepository: RecipesRepository,
    private val eventsRepository: EventsRepository
) {
    fun getAllBadges() : Flow<List<Badge>> = dao.getAllBadges().map {
        it.map { badge ->
            badge.toDomain()
        }
    }

    suspend fun getBadgesDB() {
        val collection = firestore.collection("Badges").get().await()

        collection.documents.forEach { b ->
            val badge = b.toObject(BadgeEntity::class.java)?.copy(id = b.id)

            if (badge != null) {
                dao.upsertBadge(badge)
            }
        }
    }

    suspend fun checkBadges(userId: String, notifier: NotificationHelper) {
        val user = userRepository.getUserById(userId) ?: return
        val badges = dao.getLockedBadges(user.unlockedBadges).map { badge ->
            badge.toDomain()
        }

        val recipesCreated = recipesRepository
        val favouritesAdded = user.favouriteRecipes.size
        val eventsAdded = eventsRepository.getUserEvents(userId).size
        val cakeRecipes = recipesRepository.getUserRecipes(userId).filter {
            recipe -> recipe.recipe.category == "Dolci"
        }.size
        val newBadges = mutableListOf<String>()

        badges.forEach { badge ->
            val isTriggered = when (badge.type) {
                BadgeType.Recipes -> recipesCreated >= badge.triggerValue
                BadgeType.Favourites -> favouritesAdded >= badge.triggerValue
                BadgeType.Events -> eventsAdded >= badge.triggerValue
                BadgeType.Dolci -> cakeRecipes >= badge.triggerValue
            }
            if (isTriggered) newBadges.add(badge.id)
        }

        if (newBadges.isNotEmpty()) {
            userRepository.addUserBadge(user, newBadges)
            notifier.notify("Nuovo Badge", "Hai sbloccato un nuovo badge!")
        }
    }
}