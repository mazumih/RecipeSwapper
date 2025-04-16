package com.example.recipeswapper.utils

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

interface GalleryLauncher {
    fun getImage()
}

@Composable
fun rememberGalleryLauncher(
    onPictureChosen: (imageUri: Uri) -> Unit = {}
): GalleryLauncher {

    var chosenImageUri by rememberSaveable { mutableStateOf(Uri.EMPTY) }
    val galleryActivityLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let {
                chosenImageUri = it
                onPictureChosen(chosenImageUri)
            }
        }

    val galleryLauncher by remember {
        derivedStateOf {
            object :  GalleryLauncher {
                override fun getImage() = galleryActivityLauncher.launch("image/*")
            }
        }
    }

    return galleryLauncher
}