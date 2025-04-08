package com.example.recipeswapper.ui.screens.recipedetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.recipeswapper.data.database.Recipe
import com.example.recipeswapper.ui.composable.AppBar
import com.example.recipeswapper.ui.composable.FoodPicture

@Composable
fun RecipeDetailsScreen(
    onSubmit: () -> Unit,
    recipe: Recipe,
    navController: NavHostController
) {
    Scaffold(
        topBar = { AppBar(navController, "Recipe Details") },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.tertiary,
                onClick = {
                    onSubmit()
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
            FoodPicture()
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