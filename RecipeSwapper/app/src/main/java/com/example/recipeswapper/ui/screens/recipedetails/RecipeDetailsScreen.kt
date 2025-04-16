package com.example.recipeswapper.ui.screens.recipedetails

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.recipeswapper.RecipesState
import com.example.recipeswapper.data.database.Recipe
import com.example.recipeswapper.ui.composable.BottomBar
import com.example.recipeswapper.ui.composable.ImageWithPlaceholder
import com.example.recipeswapper.ui.composable.Size

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailsScreen(
    deleteRecipe: () -> Unit,
    addFav: () -> Unit,
    removeFav: () -> Unit,
    state: RecipesState,
    recipe: Recipe,
    navController: NavHostController
) {
    /* SEND RECIPE, COULD BE USED ALSO FOR EVENTS */
    val ctx = LocalContext.current

    fun shareDetails() {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, recipe.name)
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share Recipe")
        if (shareIntent.resolveActivity(ctx.packageManager) != null) {
            ctx.startActivity(shareIntent)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(recipe.name) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Back to previous page")
                    }
                },
                actions = {
                    val favRecipes = state.recipes.filter { it.isFav }

                    if (favRecipes.contains(recipe)) {
                        IconButton(onClick = { removeFav() } ) {
                            Icon(Icons.Filled.Favorite, "Add to favourites")
                        }
                    } else {
                        IconButton(onClick = {addFav() }) {
                            Icon(Icons.Filled.FavoriteBorder, "Remove from favourites")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceDim
                )
            )
        },
        bottomBar = { BottomBar(navController, "Recipe Details") },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.tertiary,
                onClick = {
                    //deleteRecipe()
                    shareDetails()
                    navController.navigateUp()
                }
            ) {
                Icon(Icons.Filled.Delete, "Delete recipe")
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { contentPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(contentPadding).padding(12.dp).fillMaxSize()
        ) {
            ImageWithPlaceholder(Uri.parse(recipe.imageUri), Size.Lg)
            Text(
                recipe.name,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.size(8.dp))
            Text(
                recipe.description,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}