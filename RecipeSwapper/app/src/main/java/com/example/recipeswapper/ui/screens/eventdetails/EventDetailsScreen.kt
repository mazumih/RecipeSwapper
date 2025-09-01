package com.example.recipeswapper.ui.screens.eventdetails

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.recipeswapper.data.models.Event
import com.example.recipeswapper.data.models.formatDate
import com.example.recipeswapper.ui.EventsActions
import com.example.recipeswapper.ui.RecipesState
import com.example.recipeswapper.ui.composables.BottomBar
import com.example.recipeswapper.ui.composables.TopBar
import com.example.recipeswapper.ui.screens.user.UserState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsScreen (
    navController: NavController,
    event: Event,
    eventsActions: EventsActions,
    state: UserState,
    recipesState: RecipesState,
    onRecipeClick: (String) -> Unit,
) {
    val user = state.currentUser
    val recipe = recipesState.recipes.find { recipe ->
        recipe.id == event.recipeId
    }

    Scaffold(
        topBar = { TopBar(navController, "Dettagli Evento") },
            floatingActionButton = {
                if (user != null) {
                    if(user.id == event.host) {
                        FloatingActionButton(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            onClick = {
                                eventsActions.deleteEvent(event)
                                navController.navigateUp()
                            }
                        ) {
                            Icon(Icons.Filled.Delete, "Delete event")
                        }
                    }
                }
            },
            floatingActionButtonPosition = FabPosition.End
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Info",
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = event.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "📍 ${event.location}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "🗓️ ${event.formatDate()}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable{
                        onRecipeClick(event.recipeId)
                    },
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (recipe != null) {
                        Text(
                            text = "Recipe",
                            style = MaterialTheme.typography.headlineLarge
                        )
                        Text(
                            text = recipe.title,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = recipe.description,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        recipe.ingredients.forEach { ing ->
                            Text(
                                text = "${ing.name} x${ing.quantity}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Participants",
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${event.participants.size} / ${event.maxParticipants}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (event.participants.size >= event.maxParticipants) Color.Red else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            if (user != null) {
                if (user.id != event.host) {
                    Button(
                        onClick = {
                            eventsActions.addParticipant(event, user.id)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        val isJoined = user.let { event.participants.contains(it.id) }

                        Text(
                            text = if (isJoined) "Lascia evento" else "Partecipa all'evento"
                        )
                    }
                }
            }
        }
    }
}