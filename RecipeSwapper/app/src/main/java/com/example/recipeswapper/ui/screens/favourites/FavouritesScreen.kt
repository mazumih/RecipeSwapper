package com.example.recipeswapper.ui.screens.favourites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.recipeswapper.ui.RecipeSwapperRoute
import com.example.recipeswapper.ui.RecipesState
import com.example.recipeswapper.ui.composables.BottomBar
import com.example.recipeswapper.ui.composables.TopBar
import com.example.recipeswapper.ui.composables.NoItemsPlaceholder
import com.example.recipeswapper.ui.composables.GridItem
import com.example.recipeswapper.ui.screens.user.UserActions
import com.example.recipeswapper.ui.screens.user.UserState

@Composable
fun FavouritesScreen(
    recipesState: RecipesState,
    userState: UserState,
    onRecipeClick: (String) -> Unit,
    navController: NavController,
    actions: UserActions
) {
    val favouriteRecipes = recipesState.recipes.filter { recipe ->
        userState.currentUser?.favouriteRecipes?.contains(recipe.id) == true
    }

    Scaffold(
        topBar = { TopBar(navController, title = "Preferiti") },
        bottomBar = { BottomBar(navController, RecipeSwapperRoute.Favourites) },
    ) { contentPadding ->
        if (favouriteRecipes.isEmpty()) {
            NoItemsPlaceholder(Modifier.padding(contentPadding))
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(8.dp, 8.dp, 8.dp, 80.dp),
                modifier = Modifier.padding(contentPadding)
            ) {
                items(favouriteRecipes) { recipe ->
                    GridItem(
                        recipe,
                        onClick = { onRecipeClick(recipe.id) },
                        actions,
                        userState.currentUser?.favouriteRecipes ?: emptyList()
                    )
                }
            }
        }
    }
}