package com.example.recipeswapper.data.repositories

import android.content.ContentResolver
import android.net.Uri
import com.example.recipeswapper.data.database.UserDao
import com.example.recipeswapper.data.database.UserEntity
import com.example.recipeswapper.data.models.User
import com.example.recipeswapper.utils.saveImageToStorage
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val dao: UserDao,
    private val firestore: FirebaseFirestore,
    private val contentResolver: ContentResolver
) {
    fun getUser(id: String) : Flow<User?> {
        return dao.getUser(id).map { user ->
            user?.let {
                toUser(it)
            }
        }
    }

    suspend fun getUserDB(userId: String) : User? {
        val document = firestore.collection("Users").document(userId).get().await()
        val user = document.toObject(User::class.java)?.copy(id = document.id)

        if (user != null) {
            dao.upsertUser(
                UserEntity(
                    id = user.id,
                    username = user.username,
                    email = user.email,
                    profileImage = user.profileImage,
                    favouriteRecipes = user.favouriteRecipes,
                    unlockedBadges = user.unlockedBadges
                )
            )
        }

        return user
    }

    suspend fun upsertUser(user: User) {
        val document = firestore.collection("Users").document(user.id)

        val currentUser = dao.getUserById(user.id)
        var newImageUri = Uri.parse(user.profileImage)
        if (user.profileImage.isNotEmpty() && user.profileImage != currentUser?.profileImage) {
            newImageUri = saveImageToStorage(
                Uri.parse(user.profileImage),
                contentResolver,
                "RecipeSwapper_User${user.username}"
            )
        }

        val newUser = user.copy(profileImage = newImageUri.toString())
        document.set(newUser).await()

        dao.upsertUser(
            UserEntity(
                id = newUser.id,
                username = newUser.username,
                email = newUser.email,
                profileImage = newUser.profileImage,
                favouriteRecipes = newUser.favouriteRecipes,
                unlockedBadges = newUser.unlockedBadges
            )
        )
    }

    suspend fun getUserById(id: String): User? {
        val user = dao.getUserById(id)
        return user?.let {
            toUser(it)
        }
    }

    suspend fun delete(user: UserEntity) = dao.deleteUser(user)

    suspend fun addFavourite(userId: String, recipeId: String) {
        val user = dao.getUserById(userId) ?: return
        if (!user.favouriteRecipes.contains(recipeId)) {
            val updatedUser = user.copy(favouriteRecipes = user.favouriteRecipes + recipeId)
            upsertUser(toUser(updatedUser))
        }
    }

    suspend fun removeFavourite(userId: String, recipeId: String) {
        val user = dao.getUserById(userId) ?: return
        if (user.favouriteRecipes.contains(recipeId)) {
            val updatedUser = user.copy(favouriteRecipes = user.favouriteRecipes - recipeId)
            upsertUser(toUser(updatedUser))
        }
    }

    suspend fun addUserBadge(user: User, badges: List<String>) {
        val updatedUser = user.copy(unlockedBadges = user.unlockedBadges + badges)
        upsertUser(updatedUser)
    }

    private fun toUser(user: UserEntity) : User {
        return User(
            id = user.id,
            username = user.username,
            email = user.email,
            profileImage = user.profileImage,
            favouriteRecipes = user.favouriteRecipes,
            unlockedBadges = user.unlockedBadges
        )
    }
}