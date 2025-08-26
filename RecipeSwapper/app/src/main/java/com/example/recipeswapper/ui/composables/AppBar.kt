package com.example.recipeswapper.ui.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.example.recipeswapper.ui.RecipeSwapperRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(navController: NavController, title: String, onSearchClick: () -> Unit = {}) {
    TopAppBar(
        title = {
            Text(
                title,
                fontWeight = FontWeight.Medium
            )
        },
        navigationIcon = {
            if (navController.previousBackStackEntry != null) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Go Back")
                }
            }
        },
        actions = {
            if (title == "RecipeSwapper") {
                IconButton(onClick = { onSearchClick() }) {
                    Icon(Icons.Outlined.Search, contentDescription = "Search")
                }
            }
            /*if (title != "Profile") {
                IconButton(onClick = { navController.navigate(RecipeSwapperRoute.Profile) }) {
                    Icon(Icons.Outlined.AccountCircle, "Profile")
                }
            }*/
            if (title == "Profile") {
                /*IconButton(onClick = { navController.navigate(RecipeSwapperRoute.Settings)}) {
                    Icon(Icons.Filled.Settings, "Impostazioni")
                }*/
                IconButton(onClick = { navController.navigate(RecipeSwapperRoute.Favourites) } ) {
                    Icon(Icons.Filled.Star, "Preferiti")
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    )
}