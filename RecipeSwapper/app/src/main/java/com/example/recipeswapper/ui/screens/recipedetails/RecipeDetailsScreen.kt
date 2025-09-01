package com.example.recipeswapper.ui.screens.recipedetails

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material.icons.outlined.SentimentNeutral
import androidx.compose.material.icons.outlined.SentimentSatisfied
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.recipeswapper.data.models.Difficulty
import com.example.recipeswapper.data.models.Recipe
import com.example.recipeswapper.data.models.User
import com.example.recipeswapper.ui.RecipeSwapperRoute
import com.example.recipeswapper.ui.RecipesActions
import com.example.recipeswapper.ui.screens.user.UserActions
import com.example.recipeswapper.utils.NotificationHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailsScreen(
    navController: NavController,
    recipe: Recipe,
    userActions: UserActions,
    currentUser: User?,
    recipesActions: RecipesActions
) {

    val ctx = LocalContext.current

    fun shareDetails() {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            if (currentUser != null) {
                putExtra(
                    Intent.EXTRA_TEXT,
                    "${currentUser.username} vuole condividere con te la ricetta di ${recipe.author}. " +
                          "Si tratta di \"${recipe.title}\". " +
                          "Per visualizzarla scarica l'app *Recipe Swapper* sul tuo smartphone."
                )
            }
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share Recipe")
        if (shareIntent.resolveActivity(ctx.packageManager) != null) {
            ctx.startActivity(shareIntent)
        }
    }

    val notifier = NotificationHelper(ctx)
    val favouriteRecipes = currentUser?.favouriteRecipes ?: emptyList()
    val isFavourite = remember(favouriteRecipes) { derivedStateOf { favouriteRecipes.contains(recipe.id) } }
    val canEdit = currentUser?.username == recipe.author

    val hours = recipe.prepTime / 60
    val minutes = recipe.prepTime % 60

    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            if (canEdit) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(end = 12.dp)
                ) {
                    FloatingActionButton(
                        containerColor = MaterialTheme.colorScheme.primary,
                        onClick = {
                            navController.navigate(RecipeSwapperRoute.EditRecipe(recipe.id))
                        }
                    ) {
                        Icon(Icons.Filled.Edit, "Update recipe", modifier = Modifier.size(30.dp))
                    }
                    FloatingActionButton(
                        containerColor = MaterialTheme.colorScheme.error,
                        onClick = {
                            recipesActions.deleteRecipe(recipe)
                            navController.navigateUp()
                        }
                    ) {
                        Icon(Icons.Filled.Delete, "Delete recipe", modifier = Modifier.size(30.dp))
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                val imageURI = Uri.parse(recipe.imagePath)
                AsyncImage(
                    ImageRequest.Builder(LocalContext.current)
                        .data(imageURI)
                        .crossfade(true)
                        .build(),
                    recipe.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().height(300.dp).clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                )
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.Outlined.ArrowBackIosNew, "Back", tint = Color.White)
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { userActions.toggleFavourite(recipe.id, notifier) },
                            modifier = Modifier
                                .background(Color.Black.copy(0.25f), CircleShape)
                                .size(45.dp)
                        ) {
                            Icon(
                                if (isFavourite.value) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Preferito",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                        Spacer(Modifier.width(5.dp))
                        IconButton(
                            onClick = { shareDetails() },
                            modifier = Modifier
                                .background(Color.Black.copy(0.25f), CircleShape)
                                .size(45.dp)
                        ) {
                            Icon(Icons.Outlined.Share, "Condividi", tint = Color.White, modifier = Modifier.size(30.dp))
                        }
                        Spacer(Modifier.width(5.dp))
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    windowInsets = WindowInsets(0.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black.copy(0.6f))
                            )
                        )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            recipe.title,
                            style = MaterialTheme.typography.headlineLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.Person,
                                "Autore",
                                tint = Color.White.copy(0.8f),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                recipe.author,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(0.8f)
                            )
                        }
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Outlined.RestaurantMenu,
                        "Categoria",
                        modifier = Modifier.size(30.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        recipe.category,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(40.dp)
                        .background(MaterialTheme.colorScheme.onSurface.copy(0.3f))
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Outlined.Timer,
                        "Tempo",
                        modifier = Modifier.size(30.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        (if (hours > 0) "${hours}h" else "") + " " + (if(minutes > 0) "${minutes}min" else ""),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(40.dp)
                        .background(MaterialTheme.colorScheme.onSurface.copy(0.3f))
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = when (recipe.difficulty) {
                            Difficulty.EASY.level -> Icons.Outlined.SentimentSatisfied
                            Difficulty.MEDIUM.level -> Icons.Outlined.SentimentNeutral
                            Difficulty.HARD.level -> Icons.Filled.SentimentVeryDissatisfied
                            else -> Icons.AutoMirrored.Outlined.Help
                        },
                        "Difficoltà",
                        modifier = Modifier.size(30.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        recipe.difficulty,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    "Descrizione",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    recipe.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(Modifier.height(20.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(0.3f))
                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(0.5f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Ingredienti",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${recipe.portions} person${if (recipe.portions == 1) "a" else "e"}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                recipe.ingredients.forEach { ing ->
                    var checked by remember { mutableStateOf(false) }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { checked = !checked }
                            .padding(vertical = 8.dp)
                    ) {
                        Icon(
                            if (checked) Icons.Filled.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                            "seleziona",
                            tint = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(0.6f),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "${ing.quantity} ${ing.name}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (checked) MaterialTheme.colorScheme.onSurface.copy(0.5f) else MaterialTheme.colorScheme.onSurface,
                            textDecoration = if (checked) TextDecoration.LineThrough else TextDecoration.None
                        )
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
            Column(
                modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
            ) {
                Text(
                    "Procedimento",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize()
                ) {
                    Text(
                        text = if (expanded) recipe.recipe else recipe.recipe.take(150) + if (recipe.recipe.length > 150) "..." else "",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                if (recipe.recipe.length > 150) {
                    Text(
                        if (expanded) "Chiudi" else "Leggi tutto",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { expanded = !expanded }.padding(top = 8.dp)
                    )
                }
            }
        }
        Spacer(Modifier.height(30.dp))
    }
}