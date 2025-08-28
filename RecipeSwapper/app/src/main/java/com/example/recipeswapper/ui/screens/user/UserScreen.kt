package com.example.recipeswapper.ui.screens.user

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.recipeswapper.ui.RecipeSwapperRoute
import com.example.recipeswapper.ui.composables.AppBar
import com.example.recipeswapper.utils.rememberCameraLauncher
import com.example.recipeswapper.utils.rememberGalleryLauncher

@Composable
fun UserScreen(
    state: UserState,
    actions: UserActions,
    logout: () -> Unit,
    navController: NavController
) {

    val user = state.currentUser

    val cameraLauncher = rememberCameraLauncher(
        onPictureTaken = {
            imageUri ->
                actions.setImage(imageUri)
        }
    )

    val galleryLauncher = rememberGalleryLauncher(
        onImagePicked = { imageUri -> actions.setImage(imageUri) }
    )

    var showImageOptions by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { AppBar(navController, "Profile") },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.tertiary,
                onClick = { navController.navigate(RecipeSwapperRoute.AddEvent) }
            ) {
                Icon(Icons.Outlined.Groups, contentDescription = "Add Event")
            }
        }
    ) { contentPadding ->
        if(user != null) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(contentPadding)
                    .padding(12.dp)
                    .fillMaxSize()
            ) {

                if (user.profileImage != "") {
                    AsyncImage(
                        model = Uri.parse(user.profileImage),
                        contentDescription = "Foto profilo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.AccountCircle,
                        contentDescription = "Placeholder profilo",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                    )
                }

                IconButton(
                    onClick = { showImageOptions = true },
                    modifier = Modifier
                        .size(28.dp)
                        .background(Color.Blue, CircleShape)
                        .border(2.dp, Color.White, CircleShape)
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "Add image",
                        modifier = Modifier.size(ButtonDefaults.IconSize),
                        tint = Color.White
                    )
                }

                Spacer(Modifier.size(8.dp))

                Text(
                    "Username: ${user.username}"
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { navController.navigate(RecipeSwapperRoute.Badges) }
                ) {
                    Text("Badges")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = {
                    logout()
                }) {
                    Text("Logout")
                }

                if (showImageOptions) {
                    AlertDialog(
                        onDismissRequest = { showImageOptions = false },
                        title = { Text("Scegli immagine") },
                        text = { Text("Scatta una foto o scegli dalla galleria") },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    showImageOptions = false
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
}