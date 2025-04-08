package com.example.recipeswapper.ui.screens.badges

import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
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
import com.example.recipeswapper.ui.composable.AppBar

@Composable
fun BadgeScreen(
    badges: List<Badge>,
    navController: NavHostController
) {
    Scaffold(
        topBar = { AppBar(navController, title = "Badges") }
    ) { contentPadding ->
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(badges) { item ->
                BadgeCard(item, Modifier.padding(contentPadding))
            }
        }
    }
}

@Composable
fun BadgeCard(badge: Badge, modifier: Modifier) {
    Card(
        modifier = modifier
            .size(200.dp)
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            val grayscale = ColorMatrix().apply { setToSaturation(0f) }
            val painter = painterResource(id = badge.imageResId)
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