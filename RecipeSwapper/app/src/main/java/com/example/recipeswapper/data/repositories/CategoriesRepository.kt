package com.example.recipeswapper.data.repositories

import com.example.recipeswapper.data.database.CategoryDao
import com.example.recipeswapper.data.database.CategoryEntity
import com.example.recipeswapper.data.models.Category
import com.example.recipeswapper.data.models.toDomain
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class CategoriesRepository(
    private val dao: CategoryDao,
    private val firestore: FirebaseFirestore
) {

    fun getAllCategories() : Flow<List<Category>> = dao.getAllCategories().map {
        it.map { category ->
            category.toDomain()
        }
    }

    suspend fun getCategoriesDB() {
        val collection = firestore.collection("Categories").get().await()

        collection.documents.forEach { c ->
            val category = c.toObject(CategoryEntity::class.java)?.copy(id = c.id)

            if (category != null) {
                dao.upsertCategory(category)
            }
        }
    }

}