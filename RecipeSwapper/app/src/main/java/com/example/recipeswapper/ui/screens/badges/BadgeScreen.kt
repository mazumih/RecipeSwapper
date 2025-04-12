package com.example.recipeswapper.ui.screens.badges

import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.recipeswapper.BadgeState
import com.example.recipeswapper.data.database.Badge
import com.example.recipeswapper.ui.composable.AppBar

@Composable
fun BadgeScreen(
    state: BadgeState,
    navController: NavHostController
) {
    Scaffold(
        topBar = { AppBar(navController, title = "Badges") }
    ) { contentPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = contentPadding
        ) {
            items(state.badges) { item ->
                BadgeCard(item)
            }
        }
    }
}

@Composable
fun BadgeCard(badge: Badge, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .size(200.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            val grayscale = ColorMatrix().apply { setToSaturation(0f) }
            val painter = painterResource(id = badge.id)
            Image(
                painter = painter,
                contentDescription = badge.name,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                contentScale = ContentScale.Crop,
                colorFilter = if (badge.isUnlocked) null else ColorFilter.colorMatrix(grayscale)
            )
        }
    }
}