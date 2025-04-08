package com.example.recipeswapper.ui.screens.addrecipe

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.recipeswapper.ui.composable.AppBar
import com.example.recipeswapper.ui.composable.FoodPicture

@Composable
fun AddRecipeScreen(
    state: AddRecipeState,
    actions: AddRecipeActions,
    onSubmit: () -> Unit,
    navController: NavHostController
) {
    Scaffold(
        topBar = { AppBar(navController, "Add recipe") },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.tertiary,
                onClick = {
                    if(!state.canSubmit) return@FloatingActionButton
                    onSubmit()
                    actions.clearForm()
                    navController.navigateUp()
                }
            ) {
                Icon(Icons.Outlined.Check, "Add Recipe")
            }
        }
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
                value = state.name,
                onValueChange = actions::setName,
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.description,
                onValueChange = actions::setDescription,
                label = { Text("Description") },
                modifier =  Modifier.fillMaxWidth()
            )
            Spacer(Modifier.size(24.dp))
            Button(
                onClick = { /* TODO */ },
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding
            ) {
                Icon(
                    Icons.Outlined.PhotoCamera,
                    contentDescription = "Camera icon",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSize))
                Text("Take a picture")
            }
            Spacer(Modifier.size(8.dp))
            FoodPicture()
        }
    }
}