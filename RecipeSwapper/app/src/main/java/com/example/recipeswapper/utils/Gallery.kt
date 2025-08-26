package com.example.recipeswapper.utils

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

interface GalleryLauncher {
    val selectedImageUri: Uri?
    fun pickImage()
}

@Composable
fun rememberGalleryLauncher(
    onImagePicked: (imageUri: Uri) -> Unit = { }
) : GalleryLauncher {

    var selectedImageUri by remember { mutableStateOf(Uri.EMPTY) }

    val galleryActivityLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { pictureChosen ->
        if (pictureChosen == null) return@rememberLauncherForActivityResult
        selectedImageUri = pictureChosen
        onImagePicked(pictureChosen)
    }

    val galleryLauncher = remember(galleryActivityLauncher) {
        object : GalleryLauncher {
            override val selectedImageUri get() = selectedImageUri
            override fun pickImage() {
                galleryActivityLauncher.launch("image/*")
            }
        }
    }

    return galleryLauncher
}