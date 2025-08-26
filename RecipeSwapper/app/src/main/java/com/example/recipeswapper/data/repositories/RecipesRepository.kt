package com.example.recipeswapper.data.repositories

import android.content.ContentResolver
import android.net.Uri
import com.example.recipeswapper.data.database.IngredientEntity
import com.example.recipeswapper.data.database.RecipeDao
import com.example.recipeswapper.data.database.RecipeEntity
import com.example.recipeswapper.data.database.RecipeWithIngredients
import com.example.recipeswapper.data.models.Ingredient
import com.example.recipeswapper.data.models.Recipe
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
            recipes.map { ri ->
                Recipe(
                    id = ri.recipe.id,
                    title = ri.recipe.title,
                    description = ri.recipe.description,
                    imagePath = ri.recipe.imagePath,
                    author = ri.recipe.author,
                    ingredients = ri.ingredients.map {
                        Ingredient(it.name, it.quantity)
                    }
                )
            }
        }
    }

    suspend fun getRecipesDB() {
        val collection = firestore.collection("Recipes").get().await()
        collection.documents.forEach { r ->
            val recipe = r.toObject(Recipe::class.java)?.copy(id = r.id)

            if (recipe != null) {
                dao.upsertRecipe(
                    RecipeEntity(
                        id = recipe.id,
                        title = recipe.title,
                        description = recipe.description,
                        imagePath = recipe.imagePath,
                        author = recipe.author
                    )
                )

                dao.upsertIngredient(
                    recipe.ingredients.map {
                        IngredientEntity(
                            recipeId = recipe.id,
                            name = it.name,
                            quantity = it.quantity
                        )
                    }
                )
            }
        }
    }

    suspend fun addRecipe(recipe: Recipe, author: String) {
        val document = firestore.collection("Recipes").document()
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

        dao.upsertRecipe(
            RecipeEntity(
                id = newRecipe.id,
                title = newRecipe.title,
                description = newRecipe.description,
                imagePath = newRecipe.imagePath,
                author = newRecipe.author
            )
        )
        dao.upsertIngredient(
            newRecipe.ingredients.map {
                IngredientEntity(
                    recipeId = newRecipe.id,
                    name = it.name,
                    quantity = it.quantity
                )
            }
        )
    }

    suspend fun getUserRecipes(userId: String) : List<RecipeWithIngredients> {
        return dao.getUserRecipes(userId)
    }

    /*suspend fun deleteRecipe(recipe: Recipe) { }*/

}