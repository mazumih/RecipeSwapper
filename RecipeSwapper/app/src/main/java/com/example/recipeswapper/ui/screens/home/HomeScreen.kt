package com.example.recipeswapper.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.NoFood
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.recipeswapper.RecipesState
import com.example.recipeswapper.data.database.Recipe
import com.example.recipeswapper.ui.SwapperRoute
import com.example.recipeswapper.ui.composable.AppBar

@Composable
fun HomeScreen(
    state: RecipesState,
    navController: NavHostController
) {
    Scaffold(
        topBar = { AppBar(navController, "RecipeSwapper") },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(SwapperRoute.AddRecipe) },
                containerColor = MaterialTheme.colorScheme.primary ,
            ) {
                Icon(Icons.Default.Add, "Add")
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        bottomBar = { BottomBar(navController, "RecipeSwapper") },
    ) { contentPadding ->
        if(state.recipes.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(8.dp, 8.dp, 8.dp, 80.dp),
                modifier = Modifier.padding(contentPadding)
            ) {
                items(state.recipes) { item ->
                    GridItem(
                        onClick = { navController.navigate(SwapperRoute.RecipeDetails(item.id)) },
                        item
                    )
                }
            }
        } else {
            NoItemsPlaceholder(Modifier.padding(contentPadding))
        }
    }
}

@Composable
fun GridItem(onClick: () -> Unit, item: Recipe) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .size(150.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                Icons.Outlined.Image,
                "Recipe picture",
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer),
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(20.dp)
            )
            Spacer(Modifier.size(8.dp))
            Text(
                item.name,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun NoItemsPlaceholder(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Icon(
            Icons.Outlined.NoFood, "No Food icon",
            modifier = Modifier.padding(bottom = 16.dp).size(48.dp),
            tint = MaterialTheme.colorScheme.secondary
        )
        Text(
            "No recipes",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            "Tap the + button to add a new recipe.",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun BottomBar(navController: NavHostController, title: String) {
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        IconButton(onClick = {
            if(title != "RecipeSwapper") {
                navController.navigate(SwapperRoute.Home)
            }
        }) {
            Icon(Icons.Filled.Home, contentDescription = "Home")
        }
        Spacer(Modifier.weight(0.5f))
        Spacer(Modifier.width(56.dp))
        Spacer(Modifier.weight(0.5f))
        IconButton(onClick = { navController.navigate(SwapperRoute.Profile) }) {
            Icon(Icons.Filled.Person, contentDescription = "Profile")
        }
    }
}