package com.example.recipeswapper.ui.screens.home

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.recipeswapper.ui.RecipeSwapperRoute
import com.example.recipeswapper.ui.RecipesActions
import com.example.recipeswapper.ui.RecipesState
import com.example.recipeswapper.ui.composables.TopBar
import com.example.recipeswapper.ui.composables.BottomBar
import com.example.recipeswapper.ui.composables.NoItemsPlaceholder
import com.example.recipeswapper.ui.composables.GridItem
import com.example.recipeswapper.ui.screens.user.UserActions
import com.example.recipeswapper.ui.screens.user.UserState
import com.example.recipeswapper.utils.PermissionStatus
import com.example.recipeswapper.utils.rememberMultiplePermissions


import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.recipeswapper.ui.theme.RecipeSwapperTheme
import com.example.recipeswapper.data.models.Recipe
import com.example.recipeswapper.data.models.User
import com.example.recipeswapper.utils.NotificationHelper

/*@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val navController = rememberNavController()

    val dummyRecipes = listOf(
        Recipe(id = "1", title = "Pasta al pomodoro", description = "Classico italiano"),
        Recipe(id = "2", title = "Tiramisù", description = "Dolce al caffè")
    )

    val recipesState = RecipesState(
        recipes = dummyRecipes,
        category = "",
        search = ""
    )

    val userState = UserState(currentUser = null)

    // Mock vuoti, giusto per compilare
    val recipesActions = object : RecipesActions{
        override fun updateRecipesDB() {}
        override fun updateSearch(query: String) {}

        override fun deleteRecipe(recipe: Recipe) {}

        override fun setCategoryFilter(category: String) {}

    }
    val userActions = object : UserActions{
        override fun updateUser(user: User) {}

        override fun updateUserDB(id: String) {}

        override fun setImage(imageURI: Uri) {}

        override fun toggleFavourite(id: String, notifier: NotificationHelper) {}

    }

    RecipeSwapperTheme(theme = com.example.recipeswapper.data.models.Theme.Light) {
        HomeScreen(
            recipesState = recipesState,
            navController = navController,
            onRecipeClick = {},
            onSearch = {},
            userActions = userActions,
            userState = userState,
            recipesActions = recipesActions
        )
    }
}*/



@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable fun HomeScreen(
    recipesState: RecipesState,
    navController: NavController,
    onRecipeClick: (String) -> Unit,
    onSearch: (String) -> Unit,
    userActions: UserActions,
    userState: UserState,
    recipesActions: RecipesActions
    ) {

    LaunchedEffect(Unit) {
        recipesActions.updateRecipesDB()
    }

    var isSearching by remember { mutableStateOf(false) }

    val notificationPermissions = rememberMultiplePermissions(
        listOf(Manifest.permission.POST_NOTIFICATIONS)
    ) { statuses ->
        when {
            statuses.any { it.value == PermissionStatus.Granted } -> {}
            else -> { /* ALERT DIALOG */}
        }
    }

    fun requestPermission() {
        if (!notificationPermissions.statuses.any { it.value.isGranted }) {
            notificationPermissions.launchPermissionRequest()
        }
    }

    Scaffold(
        topBar = {
            if (isSearching) {
                SearchBar(
                    query = recipesState.search,
                    onQueryChange = onSearch,
                    onSearch = { query ->
                        onSearch(query)
                        isSearching = false
                    },
                    active = true,
                    onActiveChange = { },
                    placeholder = { Text("Cerca ricetta...") },
                    leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = "Search") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = {
                            isSearching = false
                            onSearch("")
                        }) {
                            Icon(Icons.Filled.Close, contentDescription = "Close")
                        }
                    }
                ) {

                }
            } else {
                TopBar(navController, title = "RecipeSwapper", onSearchClick = { isSearching = true })
            }
        },
        bottomBar = { BottomBar(navController, RecipeSwapperRoute.Home) },
    ) { contentPadding ->

        requestPermission()

        if (recipesState.filteredRecipes.isEmpty()) {
            NoItemsPlaceholder(Modifier.padding(contentPadding))
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(8.dp, 8.dp, 8.dp, 80.dp),
                modifier = Modifier.padding(contentPadding)
            ) {
                items(recipesState.filteredRecipes) { recipe ->
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