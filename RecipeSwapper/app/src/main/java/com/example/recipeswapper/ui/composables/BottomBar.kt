package com.example.recipeswapper.ui.composables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.recipeswapper.ui.RecipeSwapperRoute

@Composable
fun BottomBar(navController: NavController, title: String) {
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        IconButton(onClick = {
            if(title != "RecipeSwapper") {
                navController.navigate(RecipeSwapperRoute.Home)
            } else {
                navController.navigate(RecipeSwapperRoute.Settings)
            }
        }) {
            if(title != "RecipeSwapper") {
                Icon(Icons.Filled.Home, contentDescription = "Home")
            } else {
                Icon(Icons.Filled.Settings, contentDescription = "Impostazioni")
            }
        }
        Spacer(Modifier.weight(0.5f))
        Spacer(Modifier.width(56.dp))
        Spacer(Modifier.weight(0.5f))
        if (title == "Recipe Details") {
            IconButton(onClick = { }) {
                Icon(Icons.Filled.AddComment, contentDescription = "Aggiungi commento")
            }
        }
        if (title == "RecipeSwapper") {
            IconButton(onClick = { navController.navigate(RecipeSwapperRoute.Profile) }) {
                Icon(Icons.Filled.Person, contentDescription = "Profile")
            }
        }
    }
}