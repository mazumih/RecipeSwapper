package com.example.recipeswapper.ui.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.recipeswapper.ui.RecipeSwapperRoute

@Composable
fun BottomBar(navController: NavController, currentRoute: RecipeSwapperRoute?) {
    Box {
        BottomAppBar(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            tonalElevation = 4.dp,
            modifier = Modifier.clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
        ) {
            NavigationBarItem(
                selected = currentRoute == RecipeSwapperRoute.Home,
                onClick = { navController.navigate(RecipeSwapperRoute.Home) },
                icon = {
                    Icon(
                        Icons.Filled.Home,
                        "Home",
                        tint = if (currentRoute == RecipeSwapperRoute.Home) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.size(
                            if (currentRoute == RecipeSwapperRoute.Home) 30.dp else 24.dp
                        )
                    )
                },
                label = {
                    Text(
                        "Home",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (currentRoute == RecipeSwapperRoute.Home) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        fontWeight = if (currentRoute == RecipeSwapperRoute.Home) FontWeight.Black else FontWeight.Light
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                )
            )
            NavigationBarItem(
                selected = false,
                onClick = { navController.navigate(RecipeSwapperRoute.Categories) },
                icon = {
                    Icon(
                        Icons.Filled.Book,
                        "Esplora",
                        tint = if (currentRoute == RecipeSwapperRoute.Categories) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.size(
                            if (currentRoute == RecipeSwapperRoute.Categories) 30.dp else 24.dp
                        )
                    )
                },
                label = {
                    Text(
                        "Esplora",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (currentRoute == RecipeSwapperRoute.Categories) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        fontWeight = if (currentRoute == RecipeSwapperRoute.Categories) FontWeight.Black else FontWeight.Light
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                )
            )
            Spacer(Modifier.weight(1f))
            NavigationBarItem(
                selected = currentRoute == RecipeSwapperRoute.Favourites,
                onClick = { navController.navigate(RecipeSwapperRoute.Favourites) },
                icon = {
                    Icon(
                        Icons.Filled.Favorite,
                        "Preferiti",
                        tint = if (currentRoute == RecipeSwapperRoute.Favourites) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.size(
                            if (currentRoute == RecipeSwapperRoute.Favourites) 30.dp else 24.dp
                        )
                    )
                },
                label = {
                    Text(
                        "Preferiti",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (currentRoute == RecipeSwapperRoute.Favourites) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        fontWeight = if (currentRoute == RecipeSwapperRoute.Favourites) FontWeight.Black else FontWeight.Light
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                )
            )
            NavigationBarItem(
                selected = currentRoute == RecipeSwapperRoute.Badges,
                onClick = { navController.navigate(RecipeSwapperRoute.Badges) },
                icon = {
                    Icon(
                        Icons.Filled.EmojiEvents,
                        "Badge",
                        tint = if (currentRoute == RecipeSwapperRoute.Badges) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.size(
                            if (currentRoute == RecipeSwapperRoute.Badges) 30.dp else 24.dp
                        )
                    )
                },
                label = {
                    Text(
                        "Badge",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (currentRoute == RecipeSwapperRoute.Badges) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        fontWeight = if (currentRoute == RecipeSwapperRoute.Badges) FontWeight.Black else FontWeight.Light
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                )
            )
        }

        FloatingActionButton(
            onClick = { navController.navigate(RecipeSwapperRoute.AddRecipe) },
            modifier = Modifier.align(Alignment.TopCenter).offset(y = (-16).dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(
                Icons.Filled.Add,
                contentDescription = "Nuova Ricetta",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}