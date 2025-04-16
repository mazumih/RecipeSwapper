package com.example.recipeswapper.ui.screens.home

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.example.recipeswapper.QueryState
import com.example.recipeswapper.RecipesState
import com.example.recipeswapper.data.database.Recipe
import com.example.recipeswapper.ui.SwapperRoute
import com.example.recipeswapper.ui.composable.BottomBar
import com.example.recipeswapper.ui.composable.GridItem
import com.example.recipeswapper.ui.composable.ImageWithPlaceholder
import com.example.recipeswapper.ui.composable.NoItemsPlaceholder
import com.example.recipeswapper.ui.composable.Size
import com.example.recipeswapper.utils.PermissionStatus
import com.example.recipeswapper.utils.rememberMultiplePermissions

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: RecipesState,
    queryState: QueryState,
    updateQuery: (String) -> Unit,
    navController: NavHostController
) {
    var isSearching by remember { mutableStateOf(false) }

    /* NOTIFICATION */
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

        /* SHOULD BE PLACED ON THE LOGIN BUTTON AFTER
           SUCCESSFULLY CHECKING NAME AND PASSWORD   */
        requestPermission()

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
            NoItemsPlaceholder(
                Modifier.padding(contentPadding),
                "RecipeSwapper"
            )
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
                    onValueChange = { updateQuery(it) },
                    placeholder = { Text("Search ...") },
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
