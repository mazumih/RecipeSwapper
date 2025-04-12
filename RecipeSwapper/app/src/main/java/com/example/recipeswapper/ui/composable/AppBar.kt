package com.example.recipeswapper.ui.composable

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.recipeswapper.ui.SwapperRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(navController: NavHostController, title: String) {
    CenterAlignedTopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (navController.previousBackStackEntry != null) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Torna alla pagina precedente")
                }
            } else {
                IconButton(onClick = { navController.navigate(SwapperRoute.Settings)  }) {
                    Icon(Icons.Filled.Search, "Cerca")
                }
            }
        },
        actions = {
            if (title == "RecipeSwapper") {
                IconButton(onClick = { navController.navigate(SwapperRoute.Profile) } ) {
                    Icon(Icons.Filled.AccountCircle, "Immagine profilo")
                }
            }
            if (title == "Profile") {
                IconButton(onClick = { navController.navigate(SwapperRoute.Badge)}) {
                    Icon(Icons.Filled.Settings, "Impostazioni")
                }
                IconButton(onClick = { navController.navigate(SwapperRoute.Favs) } ) {
                    Icon(Icons.Filled.Star, "Preferiti")
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceDim
        )
    )
}