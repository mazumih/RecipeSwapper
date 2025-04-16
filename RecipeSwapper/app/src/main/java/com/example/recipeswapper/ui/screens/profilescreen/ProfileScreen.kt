package com.example.recipeswapper.ui.screens.profilescreen

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.recipeswapper.ui.SwapperRoute
import com.example.recipeswapper.ui.composable.AppBar
import com.example.recipeswapper.utils.rememberCameraLauncher
import com.example.recipeswapper.utils.rememberGalleryLauncher
import com.example.recipeswapper.utils.saveImageToStorage

@Composable
fun ProfileScreen(
    navController: NavHostController,
    state: ProfileState,
    actions: ProfileActions
) {
    var showDialog by remember { mutableStateOf(false) }

    val ctx = LocalContext.current

    val cameraLauncher = rememberCameraLauncher(
        onPictureTaken = { imageUri ->
            saveImageToStorage(imageUri, ctx.contentResolver)
            actions.setImage(imageUri)
        })

    val galleryLauncher = rememberGalleryLauncher(
        onPictureChosen = { actions.setImage(it) }
    )

    Scaffold(
        topBar = { AppBar(navController, title = "Profile") },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(SwapperRoute.AddEvent) }) {
                Icon(Icons.Outlined.Groups, "Crea festa")
            }
        },
    ) { contentPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(contentPadding).padding(12.dp).fillMaxSize()
        ) {

            if (state.imageUri != Uri.EMPTY) {
                AsyncImage(
                    model = state.imageUri,
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
                onClick = { showDialog = true },
                modifier = Modifier
                    .size(28.dp)
                    .background(Color.Blue, CircleShape)
                    .border(2.dp, Color.White, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Aggiungi foto",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(Modifier.size(4.dp))
            Text(state.name + "nickname")
            Text(state.bio + "bio")
        }
    }

    if (showDialog) {
        PhotoOptionDialog(
            onDismiss = { showDialog = false },
            onPickGallery = galleryLauncher::getImage,
            onTakePhoto = cameraLauncher::captureImage
        )
    }
}

@Composable
fun PhotoOptionDialog(
    onDismiss: () -> Unit,
    onPickGallery: () -> Unit,
    onTakePhoto: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Aggiungi immagine") },
        text = { Text("Scegli come vuoi aggiungere la tua foto profilo") },
        confirmButton = {
            TextButton(onClick = {
                onDismiss()
                onPickGallery()
            }) {
                Text("Galleria")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onDismiss()
                onTakePhoto()
            }) {
                Text("Fotocamera")
            }
        }
    )
}