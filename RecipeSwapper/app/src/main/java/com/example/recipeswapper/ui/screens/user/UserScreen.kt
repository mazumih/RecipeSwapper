package com.example.recipeswapper.ui.screens.user

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.recipeswapper.data.models.Event
import com.example.recipeswapper.data.models.Recipe
import com.example.recipeswapper.data.models.User
import com.example.recipeswapper.ui.EventsActions
import com.example.recipeswapper.ui.EventsState
import com.example.recipeswapper.ui.RecipeSwapperRoute
import com.example.recipeswapper.ui.RecipesState
import com.example.recipeswapper.ui.composables.EventCard
import com.example.recipeswapper.ui.composables.TopBar
import com.example.recipeswapper.ui.composables.EventRow
import com.example.recipeswapper.ui.composables.GridItem
import com.example.recipeswapper.ui.composables.NoItemsPlaceholder
import com.example.recipeswapper.ui.composables.RecipeCard
import com.example.recipeswapper.ui.theme.Typography
import com.example.recipeswapper.ui.theme.primary
import com.example.recipeswapper.utils.NotificationHelper
import com.example.recipeswapper.utils.rememberCameraLauncher
import com.example.recipeswapper.utils.rememberGalleryLauncher

@Composable
fun UserScreen(
    state: UserState,
    recipesState: RecipesState,
    eventsState: EventsState,
    onEventClick: (String) -> Unit,
    onRecipeClick: (String) -> Unit,
    actions: UserActions,
    logout: () -> Unit,
    navController: NavController
) {
    val user = state.currentUser!!
    val userEvents = eventsState.events
        .filter { it.participants.contains(user.id) || it.host == user.id }

    val userRecipes = recipesState.recipes
        .filter { it.authorId == user.id }

    var selected by rememberSaveable { mutableStateOf(0) }
    val options = listOf("Ricette", "Eventi")

    val cameraLauncher = rememberCameraLauncher(
        onPictureTaken = {
            imageUri ->
                actions.setImage(imageUri)
        }
    )
    val galleryLauncher = rememberGalleryLauncher(
        onImagePicked = { imageUri -> actions.setImage(imageUri) }
    )
    var showImageOptions by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopBar(navController, "Profilo", logout) },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.primary,
                onClick = { navController.navigate(RecipeSwapperRoute.AddEvent) }
            ) {
                Icon(Icons.Outlined.Groups, contentDescription = "Add Event")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { contentPadding ->
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(contentPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            item {
                Box(contentAlignment = Alignment.BottomEnd) {
                    if (user.profileImage != "") {
                        AsyncImage(
                            model = Uri.parse(user.profileImage),
                            contentDescription = "Foto profilo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(150.dp)
                                .clip(CircleShape)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.AccountCircle,
                            contentDescription = "Placeholder profilo",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(primary, CircleShape)
                            .border(2.dp, Color.White, CircleShape)
                            .clickable { showImageOptions = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = "Add image",
                            modifier = Modifier.size(ButtonDefaults.IconSize),
                            tint = Color.White
                        )
                    }
                }
                Spacer(Modifier.size(8.dp))
                Text(
                    style = Typography.headlineMedium,
                    text = user.username
                )
                if (showImageOptions) {
                    AlertDialog(
                        onDismissRequest = { showImageOptions = false },
                        title = { Text("Scegli immagine") },
                        text = { Text("Scatta una foto o scegli dalla galleria") },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    showImageOptions = false
                                    cameraLauncher.captureImage()
                                }
                            ) {
                                Text("Fotocamera")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    showImageOptions = false
                                    galleryLauncher.pickImage()
                                }
                            ) {
                                Text("Galleria")
                            }
                        }
                    )
                }
                Spacer(modifier = Modifier.height(25.dp))
                TabRow(
                    selectedTabIndex = selected,
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    options.forEachIndexed { index, title ->
                        Tab(
                            selected = selected == index,
                            onClick = { selected = index },
                            text = {
                                Text(
                                    text = title,
                                    color = if (selected == index) MaterialTheme.colorScheme.onPrimary
                                    else MaterialTheme.colorScheme.primary,
                                    fontWeight = if (selected == index) FontWeight.Bold else FontWeight.Normal,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            },
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (selected == index) {
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                    } else {
                                        Color.Transparent
                                    }
                                )
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
            when (selected) {
                0 -> {
                    if (userRecipes.isEmpty()) {
                        item {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                NoItemsPlaceholder(Modifier.padding(contentPadding), "Profilo")
                            }
                        }
                    } else {
                        items(userRecipes) { recipe ->
                            RecipeCard(recipe, { onRecipeClick(recipe.id) })
                        }
                    }
                }

                1 -> {
                    if (userEvents.isEmpty()) {
                        item {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                NoItemsPlaceholder(Modifier.padding(contentPadding), "Profilo")
                            }
                        }
                    } else {
                        items(userEvents) { event ->
                            EventRow(event, { onEventClick(event.id) })
                        }
                    }
                }
            }
        }
    }
}