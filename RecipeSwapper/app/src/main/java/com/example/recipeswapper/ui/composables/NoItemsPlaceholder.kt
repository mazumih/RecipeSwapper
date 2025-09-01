package com.example.recipeswapper.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.NoFood
import androidx.compose.material.icons.filled.NoMeals
import androidx.compose.material.icons.outlined.DoNotDisturbAlt
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
        when(title) {
            "Profilo" -> {
                Icon(
                    Icons.Default.Edit, "No Content",
                    modifier = Modifier.padding(bottom = 16.dp).size(48.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                Text(
                    "Niente da vedere qui",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    "Crea un nuovo contenuto!",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            "Home" -> {
                Icon(
                    Icons.Default.NoFood, "No Content",
                    modifier = Modifier.padding(bottom = 16.dp).size(48.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                Text(
                    "Nessun contenuto",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    "Nessuno ha ancora pubblicato niente!",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            "Preferiti" -> {
                Icon(
                    Icons.Default.NoMeals, "No Content",
                    modifier = Modifier.padding(bottom = 16.dp).size(48.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                Text(
                    "Nessun preferito",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    "Esplora le nostre ricette e scegli le tue preferite!",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}