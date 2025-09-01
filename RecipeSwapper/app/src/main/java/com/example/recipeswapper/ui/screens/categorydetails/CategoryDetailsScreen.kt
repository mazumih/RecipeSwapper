package com.example.recipeswapper.ui.screens.categorydetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.recipeswapper.ui.composables.TopBar
import com.example.recipeswapper.ui.composables.NoItemsPlaceholder
import com.example.recipeswapper.ui.composables.GridItem
import com.example.recipeswapper.ui.screens.user.UserActions
import com.example.recipeswapper.ui.screens.user.UserState

@Composable fun CategoryDetailsScreen(
    categoryDetailsState: CategoryDetailsState,
    navController: NavController,
    onRecipeClick: (String) -> Unit,
    userActions: UserActions,
    userState: UserState
) {
    val user = userState.currentUser
    val recipes = categoryDetailsState.filteredRecipes.filter { recipe ->
        recipe.authorId != (user?.id ?: "")
    }

    Scaffold(
        topBar = { TopBar(navController, title = categoryDetailsState.category?.name ?: "") }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            if (recipes.isEmpty()) {
                NoItemsPlaceholder(Modifier.padding(contentPadding))
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(8.dp, 8.dp, 8.dp, 80.dp)
                ) {
                    items(recipes) { recipe ->
                        GridItem(
                            recipe,
                            onClick = { onRecipeClick(recipe.id) },
                            userActions,
                            userState.currentUser?.favouriteRecipes ?: emptyList()
                        )
                    }
                }
            }
        }
    }
}