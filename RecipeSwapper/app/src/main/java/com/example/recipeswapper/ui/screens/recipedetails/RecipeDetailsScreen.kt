package com.example.recipeswapper.ui.screens.recipedetails

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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.recipeswapper.data.database.Recipe
import com.example.recipeswapper.ui.FavouriteRecipesState
import com.example.recipeswapper.ui.composable.BottomBar
import com.example.recipeswapper.ui.composable.Picture

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailsScreen(
    onSubmit: () -> Unit,
    favSaved: () -> Unit,
    favDeleted: () -> Unit,
    favs: FavouriteRecipesState,
    recipe: Recipe,
    navController: NavHostController
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(recipe.name) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Torna alla pagina precedente")
                    }
                },
                actions = {
                    if (favs.favRecipes.contains(recipe)) {
                        IconButton(onClick = favDeleted) {
                            Icon(Icons.Filled.Favorite, "Aggiungi nei preferiti")
                        }
                    } else {
                        IconButton(onClick = favSaved) {
                            Icon(Icons.Filled.FavoriteBorder, "Togli dai preferiti")
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
                    onSubmit()
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
            Picture("Food picture")
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