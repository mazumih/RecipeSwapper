package com.example.recipeswapper.ui.screens.recipedetails

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.recipeswapper.data.models.Recipe
import com.example.recipeswapper.ui.composables.BottomBar
import com.example.recipeswapper.ui.composables.ImageWithPlaceholder
import com.example.recipeswapper.ui.composables.Size
import com.example.recipeswapper.ui.screens.user.UserActions
import com.example.recipeswapper.ui.screens.user.UserState
import com.example.recipeswapper.utils.NotificationHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailsScreen(
    navController: NavController,
    recipe: Recipe,
    userActions: UserActions,
    userState: UserState
) {

    /*val ctx = LocalContext.current

    fun shareDetails() {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, recipe.title)
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share Recipe")
        if (shareIntent.resolveActivity(ctx.packageManager) != null) {
            ctx.startActivity(shareIntent)
        }
    }*/

    val ctx = LocalContext.current
    val notifier = NotificationHelper(ctx)
    val favouriteRecipes = userState.currentUser?.favouriteRecipes ?: emptyList()
    val isFavourite = remember(favouriteRecipes) { derivedStateOf { favouriteRecipes.contains(recipe.id) } }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(recipe.title) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Back to previous page")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { userActions.toggleFavourite(recipe.id, notifier) },
                    ) {
                        Icon(
                            if (isFavourite.value) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Add to Favourites",
                            tint = Color.Red,
                            modifier = Modifier.size(30.dp)
                        )
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
                    navController.navigateUp()
                }
            ) {
                Icon(Icons.Filled.Delete, "Delete recipe")
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { contentPadding ->
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(contentPadding).fillMaxSize()
        ) {
            item {
                val imageURI = Uri.parse(recipe.imagePath)
                ImageWithPlaceholder(imageURI, Size.Lg)

                Text(
                    recipe.title,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Author: " + recipe.author,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    recipe.description,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(16.dp))

                Text("Ingredienti", style = MaterialTheme.typography.titleMedium)
            }

            items(recipe.ingredients) { ing ->
                Text("${ing.name} ${ing.quantity}")
            }
        }
    }
}