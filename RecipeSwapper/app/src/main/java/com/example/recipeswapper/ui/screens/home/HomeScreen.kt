package com.example.recipeswapper.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.NoFood
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.example.recipeswapper.QueryState
import com.example.recipeswapper.RecipesState
import com.example.recipeswapper.data.database.Recipe
import com.example.recipeswapper.ui.SwapperRoute
import com.example.recipeswapper.ui.composable.BottomBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: RecipesState,
    queryState: QueryState,
    onSubmit: (String) -> Unit,
    navController: NavHostController
) {
    var isSearching by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("RecipeSwapper") },
                navigationIcon = {
                    IconButton(onClick = { isSearching = !isSearching }) {
                        Icon(Icons.Filled.Search, "Filtra")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(SwapperRoute.Profile) } ) {
                        Icon(Icons.Filled.AccountCircle, "Profile")
                    }
                }
            )
        },
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

    if(isSearching) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.Center)
                    .zIndex(1f),
                shape = RoundedCornerShape(12.dp),
                tonalElevation = 8.dp
            ) {
                TextField(
                    value = queryState.query,
                    onValueChange = { onSubmit(it) },
                    placeholder = { Text("Cerca ricetta...") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    trailingIcon = {
                        IconButton(onClick = {
                            isSearching = false
                        }) {
                            Icon(Icons.Filled.Close, contentDescription = "Close")
                        }
                    }
                )
            }
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
