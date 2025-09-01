package com.example.recipeswapper.data.repositories

import android.content.ContentResolver
import android.net.Uri
import com.example.recipeswapper.data.database.RecipeDao
import com.example.recipeswapper.data.database.RecipeWithIngredients
import com.example.recipeswapper.data.models.Recipe
import com.example.recipeswapper.data.models.toDomain
import com.example.recipeswapper.data.models.toEntity
import com.example.recipeswapper.utils.saveImageToStorage
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class RecipesRepository(
    private val firestore: FirebaseFirestore,
    private val dao: RecipeDao,
    private val contentResolver: ContentResolver
) {

    fun getAllRecipes(): Flow<List<Recipe>> {
        return dao.getAllRecipes().map { recipes ->
            recipes.map { ri -> ri.toDomain() }
        }
    }

    suspend fun getRecipesDB() {
        val collection = firestore.collection("Recipes").get().await()
        collection.documents.forEach { r ->
            val recipe = r.toObject(Recipe::class.java)?.copy(id = r.id)

            if (recipe != null) {
                dao.upsertRecipe(recipe.toEntity())

                dao.upsertIngredients(recipe.ingredients.map { ing -> ing.toEntity(recipe.id) })
            }
        }
    }

    suspend fun upsertRecipe(recipe: Recipe, author: String) {
        val document = if (recipe.id.isBlank()) firestore.collection("Recipes").document() else firestore.collection("Recipes").document(recipe.id)
        var newRecipe = recipe.copy(id = document.id, author = author)

        val currentRecipe = dao.getRecipeById(recipe.id)
        var newImageUri = Uri.parse(recipe.imagePath)
        if (newRecipe.imagePath.isNotEmpty() && recipe.imagePath != currentRecipe?.imagePath) {
            newImageUri = saveImageToStorage(
                Uri.parse(newRecipe.imagePath),
                contentResolver,
                "RecipeSwapper_Recipe${recipe.title}"
            )
        }

        newRecipe = newRecipe.copy(imagePath = newImageUri.toString())
        document.set(newRecipe).await()
        dao.upsertRecipe(newRecipe.toEntity())
        dao.deleteIngredients(recipe.id)
        dao.upsertIngredients(newRecipe.ingredients.map { ing -> ing.toEntity(newRecipe.id) })
    }

    suspend fun getUserRecipes(userId: String) : List<RecipeWithIngredients> {
        return dao.getUserRecipes(userId)
    }

    suspend fun deleteRecipe(recipe: Recipe) {
        firestore.collection("Recipes").document(recipe.id).delete().await()
        dao.deleteIngredients(recipe.id)
        dao.deleteRecipe(recipe.toEntity())
    }

}