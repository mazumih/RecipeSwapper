package com.example.recipeswapper.ui.screens.home

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.recipeswapper.data.models.Event
import com.example.recipeswapper.data.models.formatDate
import com.example.recipeswapper.ui.EventsState
import com.example.recipeswapper.ui.RecipeSwapperRoute
import com.example.recipeswapper.ui.RecipesActions
import com.example.recipeswapper.ui.RecipesState
import com.example.recipeswapper.ui.composables.TopBar
import com.example.recipeswapper.ui.composables.BottomBar
import com.example.recipeswapper.ui.composables.EventCard
import com.example.recipeswapper.ui.composables.NoItemsPlaceholder
import com.example.recipeswapper.ui.composables.GridItem
import com.example.recipeswapper.ui.screens.user.UserActions
import com.example.recipeswapper.ui.screens.user.UserState
import com.example.recipeswapper.utils.PermissionStatus
import com.example.recipeswapper.utils.rememberMultiplePermissions

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable fun HomeScreen(
    recipesState: RecipesState,
    navController: NavController,
    onRecipeClick: (String) -> Unit,
    onSearch: (String) -> Unit,
    userActions: UserActions,
    userState: UserState,
    recipesActions: RecipesActions,
    eventsState: EventsState,
    onEventClick: (String) -> Unit
    ) {

    LaunchedEffect(Unit) {
        recipesActions.updateRecipesDB()
    }
    var isSearching by remember { mutableStateOf(false) }

    var selected by rememberSaveable { mutableStateOf(0) }
    val options = listOf("Ricette", "Eventi")

    val user = userState.currentUser
    val recipes = recipesState.filteredRecipes.filter { recipe ->
        recipe.author != (user?.id ?: "")
    }

    val events = eventsState.events.filter { event ->
        event.host != (user?.id ?: "")
    }

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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(Modifier.height(25.dp))
            TabRow(
                selectedTabIndex = selected,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp))
            ) {
                options.forEachIndexed { index, title ->
                    Tab(
                        selected = selected == index,
                        onClick = {
                            selected = index
                        },
                        text = {
                            Text(
                                title,
                                color = if (selected == index) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                                fontWeight = if (selected == index) FontWeight.Bold else FontWeight.Normal,
                                style = MaterialTheme.typography.titleMedium
                            )
                        },
                        modifier = Modifier
                            .background(
                                if (selected == index) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else Color.Transparent
                            )
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            when(selected) {
                0 -> {
                    if (recipes.isEmpty()) {
                        NoItemsPlaceholder(Modifier.padding(contentPadding))
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(8.dp, 8.dp, 8.dp, 80.dp),
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
                1 -> {
                    if (events.isEmpty()) {
                        NoItemsPlaceholder(Modifier.padding(contentPadding))
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(8.dp, 8.dp, 8.dp, 80.dp),
                        ) {
                            items(events) { event ->
                                EventCard(event, { onEventClick(event.id) })
                            }
                        }
                    }
                }
            }
        }
    }
}