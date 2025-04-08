package com.example.recipeswapper.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.recipeswapper.ui.composable.AppBar

@Composable
fun HomeScreen(
    navController: NavHostController
) {
    Scaffold(
        topBar = { AppBar(navController, "RecipeSwapper") },
        floatingActionButton = {
            FloatingActionButton( onClick =  { /* TODO */ } ) {
                Icon(Icons.Filled.Add, "Aggiungi ricetta")
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { contentPadding ->
        Column(
            modifier = Modifier.padding(contentPadding)
        ) {  }
    }
}