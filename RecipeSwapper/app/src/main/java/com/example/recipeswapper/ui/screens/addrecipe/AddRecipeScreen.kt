package com.example.recipeswapper.ui.screens.addrecipe

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.recipeswapper.ui.composables.AppBar
import com.example.recipeswapper.ui.composables.ImageWithPlaceholder
import com.example.recipeswapper.ui.composables.Size
import com.example.recipeswapper.utils.NotificationHelper
import com.example.recipeswapper.utils.rememberCameraLauncher
import com.example.recipeswapper.utils.rememberGalleryLauncher
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun AddRecipeScreen(
    state: AddRecipeState,
    actions: AddRecipeActions,
    navController: NavController
) {
    val author = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val scope = rememberCoroutineScope()

    val ctx = LocalContext.current
    val notifier = NotificationHelper(ctx)

    var ingredientName by remember { mutableStateOf("") }
    var ingredientQuantity by remember { mutableStateOf("") }

    var isCameraImage by remember { mutableStateOf(false) }

    val cameraLauncher = rememberCameraLauncher(
        onPictureTaken = { imageUri -> actions.setImage(imageUri) }
    )

    val galleryLauncher = rememberGalleryLauncher(
        onImagePicked = { imageUri -> actions.setImage(imageUri) }
    )

    var showImageOptions by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = { AppBar(navController,"Add Recipe") },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.tertiary,
                onClick = {
                    if (!state.canSubmit) return@FloatingActionButton
                    actions.addRecipe(state.toRecipe(), author, notifier, isCameraImage)
                    scope.launch {
                        snackbarHostState.showSnackbar("Ricetta aggiunta con successo!")
                    }
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
                value = state.title,
                onValueChange = actions::setTitle,
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.description,
                onValueChange = actions::setDescription,
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.size(16.dp))

            Text("Ingredienti", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn {
                itemsIndexed(state.ingredients) {index, ingredient ->
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Text("${ingredient.name} - ${ingredient.quantity}")
                        TextButton(onClick = { actions.removeIngredient(index) }) {
                            Text("Rimuovi")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = ingredientName,
                    onValueChange = { ingredientName = it },
                    label = { Text("Nome") },
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedTextField(
                    value = ingredientQuantity,
                    onValueChange = { ingredientQuantity = it },
                    label = { Text("Quantità") },
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(onClick = {
                    if(ingredientName.isNotBlank() && ingredientQuantity.isNotBlank()) {
                        actions.addIngredient(ingredientName, ingredientQuantity)
                        ingredientName = ""
                        ingredientQuantity = ""
                    }
                }) {
                    Text("+")
                }
            }

            Spacer(Modifier.size(24.dp))

            Button(
                onClick = { showImageOptions = true },
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
            ) {
                Icon(
                    Icons.Outlined.PhotoCamera,
                    contentDescription = "Camera icon",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Scegli immagine")
            }
            Spacer(Modifier.size(8.dp))
            ImageWithPlaceholder(state.imageURI, Size.Lg)

            if (showImageOptions) {
                AlertDialog(
                    onDismissRequest = { showImageOptions = false },
                    title = { Text("Scegli immagine") },
                    text = { Text("Scatta una foto o scegli dalla galleria") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showImageOptions = false
                                isCameraImage = true
                                cameraLauncher.captureImage()
                            }
                        ) {
                            Text("Fotocamera")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showImageOptions = false
                                isCameraImage = false
                                galleryLauncher.pickImage()
                            }
                        ) {
                            Text("Galleria")
                        }
                    }
                )
            }
        }
    }
}