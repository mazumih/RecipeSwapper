package com.example.recipeswapper.ui.screens.addrecipe

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.SentimentNeutral
import androidx.compose.material.icons.outlined.SentimentSatisfied
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.recipeswapper.data.models.User
import com.example.recipeswapper.ui.composables.TopBar
import com.example.recipeswapper.ui.screens.category.CategoriesState
import com.example.recipeswapper.utils.NotificationHelper
import com.example.recipeswapper.utils.rememberCameraLauncher
import com.example.recipeswapper.utils.rememberGalleryLauncher
import kotlinx.coroutines.launch

enum class Difficulty(val level: String) {
    EASY("Facile"),
    MEDIUM("Media"),
    HARD("Difficile")
}


@Composable
fun AddRecipeScreen(
    addRecipeState: AddRecipeState,
    actions: AddRecipeActions,
    navController: NavController,
    categoriesState: CategoriesState,
    currentUser: User?
) {
    val ctx = LocalContext.current
    val notifier = NotificationHelper(ctx)

    var ingredientName by remember { mutableStateOf("") }
    var ingredientQuantity by remember { mutableStateOf("") }

    var showImageOptions by remember { mutableStateOf(false) }

    val cameraLauncher = rememberCameraLauncher(
        onPictureTaken = { imageUri -> actions.setImage(imageUri) }
    )

    val galleryLauncher = rememberGalleryLauncher(
        onImagePicked = { imageUri -> actions.setImage(imageUri) }
    )

    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = { TopBar(navController,"Nuova Ricetta") },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.primary,
                onClick = {
                    if (!addRecipeState.canSubmit) {
                        scope.launch {
                            snackbar.showSnackbar(
                                message = "Completa tutti i campi",
                                duration = SnackbarDuration.Short
                            )
                        }
                        return@FloatingActionButton
                    }
                    actions.addRecipe(addRecipeState.toRecipe(), currentUser?.username ?: "", notifier)
                    navController.navigateUp()
                }
            ) {
                Icon(Icons.Outlined.Check, "Salva Ricetta")
            }
        }
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 20.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(4.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), RoundedCornerShape(28.dp))
                    .clickable { showImageOptions = true },
                contentAlignment = Alignment.Center
            ) {
                if (addRecipeState.imageURI.path.isNullOrBlank()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Outlined.PhotoCamera,
                            "Carica immagine",
                            modifier = Modifier.size(56.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            "Carica immagine",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                } else {
                    AsyncImage(
                        addRecipeState.imageURI,
                        "Foto Ricetta",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(28.dp))
                            .background(Color.Black.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.PhotoCamera,
                            "Cambia immagine",
                            modifier = Modifier.size(56.dp),
                            tint = Color.White.copy(0.8f)
                        )
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
            OutlinedTextField(
                value = addRecipeState.title,
                onValueChange = actions::setTitle,
                label = {
                    Text(
                        "Nome ricetta",
                        style = MaterialTheme.typography.bodyLarge,
                        fontStyle = FontStyle.Italic
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = addRecipeState.description,
                onValueChange = actions::setDescription,
                label = {
                    Text(
                        "Descrizione",
                        style = MaterialTheme.typography.bodyLarge,
                        fontStyle = FontStyle.Italic
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "Categoria",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(categoriesState.categories) { category ->
                    val isSelected = category.name == addRecipeState.category
                    ElevatedFilterChip(
                        selected = isSelected,
                        onClick = { actions.setCategory(category.name) },
                        label = {
                            Text(
                                category.name,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Light,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        },
                        leadingIcon = { if (isSelected) Icon(Icons.Filled.Check, null, tint = MaterialTheme.colorScheme.onPrimary) },
                        shape = RoundedCornerShape(20.dp),
                        colors = FilterChipDefaults.elevatedFilterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(
                "Difficoltà",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Row(
                modifier = Modifier
                    .height(40.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.5.dp, MaterialTheme.colorScheme.primary.copy(0.5f), RoundedCornerShape(16.dp))
            ) {
                Difficulty.entries.forEachIndexed { index, difficulty ->
                    val isSelected = difficulty.level == addRecipeState.difficulty
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface.copy(0.5f)
                            )
                            .clickable { actions.setDifficulty(difficulty.level) },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = when (difficulty) {
                                    Difficulty.EASY -> Icons.Outlined.SentimentSatisfied
                                    Difficulty.MEDIUM -> Icons.Outlined.SentimentNeutral
                                    Difficulty.HARD -> Icons.Filled.SentimentVeryDissatisfied
                                },
                                difficulty.level,
                                tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(if (isSelected) 25.dp else 20.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                difficulty.level,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                    if (index < Difficulty.entries.size - 1) {
                        Box(
                            modifier = Modifier
                                .width(1.5.dp)
                                .fillMaxHeight()
                                .background(MaterialTheme.colorScheme.primary.copy(0.5f))
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(
                "Tempo di preparazione",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val hours = addRecipeState.prepTime / 60
                val minutes = addRecipeState.prepTime % 60
                Text(
                    text = (if (hours > 0) "${hours}h" else "") + " " + (if (minutes > 0) "${minutes}min" else ""),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = {
                            if (addRecipeState.prepTime > 1) actions.setPrepTime(addRecipeState.prepTime - 1)
                            },
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(30.dp)
                            .background(MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Filled.Remove, "meno", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                    Slider(
                        value = addRecipeState.prepTime.toFloat(),
                        onValueChange = { actions.setPrepTime(it.toInt()) },
                        valueRange = 0f..300f,
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(
                            inactiveTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                        )
                    )
                    IconButton(
                        onClick = {
                            if (addRecipeState.prepTime < 300) actions.setPrepTime(addRecipeState.prepTime + 1)
                        },
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(30.dp)
                            .background(MaterialTheme.colorScheme.primary)
                    ) { Icon(Icons.Filled.Add, "più", tint = MaterialTheme.colorScheme.onPrimary) }
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(
                "Ingredienti",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    IconButton(
                        onClick = { if (addRecipeState.portions > 1) actions.setPortions(addRecipeState.portions - 1) }
                    ) {
                        Icon(Icons.Filled.RemoveCircleOutline, "meno", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(30.dp))
                    }
                    Text(
                        "${addRecipeState.portions} person${if (addRecipeState.portions == 1) "a" else "e"}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(
                        onClick = { actions.setPortions(addRecipeState.portions + 1) }
                    ) {
                        Icon(Icons.Filled.AddCircleOutline, "più", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(30.dp))
                    }
                }
                addRecipeState.ingredients.forEachIndexed { index, ingredient ->
                    var editing by remember { mutableStateOf(false) }
                    var name by remember { mutableStateOf(ingredient.name) }
                    var quantity by remember { mutableStateOf(ingredient.quantity) }

                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            if (editing) {
                                OutlinedTextField(
                                    value = name,
                                    onValueChange = { name = it },
                                    label = { Text("Ingrediente", style = MaterialTheme.typography.bodyLarge, fontStyle = FontStyle.Italic) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                                    )
                                )
                                Spacer(Modifier.width(8.dp))
                                OutlinedTextField(
                                    value = quantity,
                                    onValueChange = { quantity = it },
                                    label = { Text("Quantità", style = MaterialTheme.typography.bodyLarge, fontStyle = FontStyle.Italic) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                                    )
                                )
                                TextButton(
                                    onClick = {
                                        actions.updateIngredient(index, name, quantity)
                                        editing = false
                                    }
                                ) { Text("OK", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold) }
                            } else {
                                Text(
                                    "${ingredient.name} - ${ingredient.quantity}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Row {
                                    IconButton(onClick = { editing = true }) {
                                        Icon(
                                            Icons.Filled.Edit,
                                            "Modifica",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                    IconButton(onClick = { actions.removeIngredient(index) }) {
                                        Icon(
                                            Icons.Filled.Delete,
                                            "Rimuovi",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = ingredientName,
                        onValueChange = { ingredientName = it },
                        label = { Text("Ingrediente", style = MaterialTheme.typography.bodyLarge, fontStyle = FontStyle.Italic) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = ingredientQuantity,
                        onValueChange = { ingredientQuantity = it },
                        label = { Text("Quantità", style = MaterialTheme.typography.bodyLarge, fontStyle = FontStyle.Italic) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    IconButton(
                        onClick = {
                            if(ingredientName.isNotBlank() && ingredientQuantity.isNotBlank()) {
                                actions.addIngredient(ingredientName, ingredientQuantity)
                                ingredientName = ""
                                ingredientQuantity = ""
                            }
                        },
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .size(40.dp)
                            .background(MaterialTheme.colorScheme.primary)
                    ) { Icon(Icons.Filled.Add, "Aggiungi", tint = MaterialTheme.colorScheme.onPrimary) }
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(
                "Procedimento",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            OutlinedTextField(
                value = addRecipeState.recipe,
                onValueChange = { actions.setRecipe(it) },
                placeholder = { Text("Scrivi i passaggi...", style = MaterialTheme.typography.bodyLarge, fontStyle = FontStyle.Italic) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(Modifier.height(60.dp))
        }

        if (showImageOptions) {
            AlertDialog(
                onDismissRequest = { showImageOptions = false },
                title = {
                    Text(
                        "Aggiungi immagine",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                text = {
                    Text(
                        "Scatta una foto o sceglila dalla galleria",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showImageOptions = false
                            cameraLauncher.captureImage()
                        },
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(0.3f))
                    ) {
                        Text(
                            "Fotocamera",
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showImageOptions = false
                            galleryLauncher.pickImage()
                        },
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(0.3f))
                    ) {
                        Text(
                            "Galleria",
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            )
        }
    }
}