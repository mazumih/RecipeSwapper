package com.example.recipeswapper.ui.screens.favourites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.recipeswapper.RecipesState
import com.example.recipeswapper.ui.SwapperRoute
import com.example.recipeswapper.ui.composable.AppBar
import com.example.recipeswapper.ui.screens.home.GridItem

@Composable
fun FavouritesScreen(
    navController: NavHostController,
    onSubmit: () -> Unit,
    state: RecipesState
) {
    Scaffold(
        topBar = { AppBar(navController, "Favourites") },
        floatingActionButton = {
            FloatingActionButton(onClick = onSubmit) {
                Icon(Icons.Filled.Delete, "Delete all")
            }
        }
    ) { contentPadding ->
        val favRecipes = state.recipes.filter { it.isFav }

        if(favRecipes.isNotEmpty()) {
            /* POSSIBILE DIVERSIFICARE UN PO' RISPETTO ALLA HOME */
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(8.dp, 8.dp, 8.dp, 80.dp),
                modifier = Modifier.padding(contentPadding)
            ) {
                items(favRecipes) { item ->
                    GridItem(
                        onClick = { navController.navigate(SwapperRoute.RecipeDetails(item.id)) },
                        item
                    )
                }
            }
        }
    }
}