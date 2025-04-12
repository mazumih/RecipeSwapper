package com.example.recipeswapper.ui

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.recipeswapper.RecipeViewModel
import com.example.recipeswapper.ui.screens.addevent.AddEventScreen
import com.example.recipeswapper.ui.screens.addrecipe.AddRecipeScreen
import com.example.recipeswapper.ui.screens.addrecipe.AddRecipeViewModel
import com.example.recipeswapper.ui.screens.badges.BadgeScreen
import com.example.recipeswapper.BadgeViewModel
import com.example.recipeswapper.data.repositories.Theme
import com.example.recipeswapper.ui.screens.favourites.FavouritesScreen
import com.example.recipeswapper.ui.screens.home.HomeScreen
import com.example.recipeswapper.ui.screens.profilescreen.ProfileScreen
import com.example.recipeswapper.ui.screens.recipedetails.RecipeDetailsScreen
import com.example.recipeswapper.ui.screens.settings.SettingsScreen
import com.example.recipeswapper.ui.screens.settings.ThemeState
import kotlinx.coroutines.Job
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

sealed interface SwapperRoute {
    @Serializable data object Home : SwapperRoute
    @Serializable data object AddRecipe : SwapperRoute
    @Serializable data object Badge: SwapperRoute
    @Serializable data object Profile : SwapperRoute
    @Serializable data object AddEvent : SwapperRoute
    @Serializable data object Favs : SwapperRoute
    @Serializable data object Settings : SwapperRoute
    @Serializable data class RecipeDetails(val recipeId: Int): SwapperRoute
}

@Composable
fun SwapperNavGraph(
    navController: NavHostController,
    themeState: ThemeState,
    themeChange: (Theme) -> Unit
) {
    val recipesVm = koinViewModel<RecipeViewModel>()
    val recipesState by recipesVm.state.collectAsStateWithLifecycle()
    val badgeVm = koinViewModel<BadgeViewModel>()
    val badges by badgeVm.state.collectAsStateWithLifecycle()
    val favsVm = koinViewModel<AddFavouritesViewModel>()
    val favs by favsVm.state.collectAsStateWithLifecycle()

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        badgeVm.toastEvent.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    NavHost(
        navController = navController,
        startDestination = SwapperRoute.Home,
    ) {
        composable<SwapperRoute.Home> {
            HomeScreen(recipesState, navController)
        }
        composable<SwapperRoute.AddRecipe> {
            val addRecipeVm = koinViewModel<AddRecipeViewModel>()
            val state by addRecipeVm.state.collectAsStateWithLifecycle()
            AddRecipeScreen(
                state,
                addRecipeVm.actions,
                onSubmit = { recipesVm.addRecipe(state.toRecipe()) },
                navController
            )
        }
        composable<SwapperRoute.Badge> {
            BadgeScreen(badges, navController)
        }
        composable<SwapperRoute.Profile> {
            ProfileScreen(navController)
        }
        composable<SwapperRoute.AddEvent> {
            AddEventScreen(navController)
        }
        composable<SwapperRoute.Favs> {
            FavouritesScreen(navController, favs)
        }
        composable<SwapperRoute.Settings> {
            SettingsScreen(navController, themeState, themeChange)
        }
        composable<SwapperRoute.RecipeDetails> { backStackEntry ->
            val route = backStackEntry.toRoute<SwapperRoute.RecipeDetails>()
            recipesState.recipes.find { it.id == route.recipeId }?. let { recipe ->
                RecipeDetailsScreen(
                    onSubmit = { recipesVm.deleteRecipe(recipe) },
                    favSaved = { favsVm.addFavouriteRecipe(recipe) },
                    favDeleted = { favsVm.deleteFavouriteRecipe(recipe) },
                    favs,
                    recipe,
                    navController
                )
            }
        }
    }
}