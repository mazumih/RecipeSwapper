package com.example.recipeswapper.ui.screens.badges

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.recipeswapper.data.models.getBadgeIcon
import com.example.recipeswapper.ui.composables.TopBar
import com.example.recipeswapper.ui.BadgesState
import com.example.recipeswapper.ui.RecipeSwapperRoute
import com.example.recipeswapper.ui.composables.BottomBar
import com.example.recipeswapper.ui.screens.user.UserState

@Composable
fun BadgesScreen(
    badgesState: BadgesState,
    userState: UserState,
    navController: NavController
) {
    Scaffold(
        topBar = { TopBar(navController, title = "Badges") },
        bottomBar = { BottomBar(navController, RecipeSwapperRoute.Badges) },
    ) { contentPadding ->

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = contentPadding
        ) {
            items(badgesState.badges) { badge ->
                val unlocked = userState.currentUser?.unlockedBadges?.contains(badge.id) ?: false

                Card(
                    modifier = Modifier
                        .size(200.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val grayscale = ColorMatrix().apply { setToSaturation(0f) }
                        val painter = painterResource(id = getBadgeIcon(badge.icon))
                        Image(
                            painter = painter,
                            contentDescription = badge.name,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            contentScale = ContentScale.Crop,
                            colorFilter = if (unlocked) null else ColorFilter.colorMatrix(grayscale)
                        )
                    }
                }
            }
        }
    }
}