package com.example.recipeswapper.ui.screens.addevent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.recipeswapper.ui.composable.AppBar

@Composable
fun AddEventScreen(
    navController: NavHostController
) {
    Scaffold(
        topBar = { AppBar(navController, "Crea Evento") }
    ) { contentPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(contentPadding)
                .padding(12.dp)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = "",
                onValueChange = { /* TODO */ },
                label = { Text("Nome della serata") },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = "",
                onValueChange = { /* TODO */ },
                label = { Text("Data") },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = "",
                onValueChange = { /* TODO */ },
                label = { Text("Posto") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Outlined.MyLocation, "Current location")
                    }
                }
            )
            Spacer(Modifier.size(4.dp))
        }
    }
}