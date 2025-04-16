package com.example.recipeswapper.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NoFood
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NoItemsPlaceholder(modifier: Modifier = Modifier, title: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Icon(
            Icons.Outlined.NoFood, "No food icon",
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
            if(title == "RecipeSwapper") {
                "Tap the + button to add a new recipe."
            } else {
                "Go on homepage and look for something you like."
            },
            style = MaterialTheme.typography.bodyLarge
        )
    }
}