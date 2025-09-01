package com.example.recipeswapper.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.recipeswapper.ui.screens.addevent.AddEventScreen
import com.example.recipeswapper.ui.screens.addevent.AddEventViewModel
import com.example.recipeswapper.ui.screens.favourites.FavouritesScreen
import com.example.recipeswapper.ui.screens.addrecipe.AddRecipeScreen
import com.example.recipeswapper.ui.screens.addrecipe.AddRecipeViewModel
import com.example.recipeswapper.ui.screens.home.HomeScreen
import com.example.recipeswapper.ui.screens.authentication.AuthScreen
import com.example.recipeswapper.ui.screens.authentication.AuthViewModel
import com.example.recipeswapper.ui.screens.badges.BadgesScreen
import com.example.recipeswapper.ui.screens.eventdetails.EventDetailsScreen
import com.example.recipeswapper.ui.screens.category.CategoriesScreen
import com.example.recipeswapper.ui.screens.category.CategoriesViewModel
import com.example.recipeswapper.ui.screens.user.UserScreen
import com.example.recipeswapper.ui.screens.user.UserViewModel
import com.example.recipeswapper.ui.screens.recipedetails.RecipeDetailsScreen
import com.example.recipeswapper.ui.screens.settings.SettingsScreen
import com.example.recipeswapper.ui.screens.settings.SettingsViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

sealed interface RecipeSwapperRoute {
    @Serializable data object Home : RecipeSwapperRoute
    @Serializable data object Authentication : RecipeSwapperRoute
    @Serializable data object Profile : RecipeSwapperRoute
    @Serializable data object Settings : RecipeSwapperRoute
    @Serializable data object AddRecipe : RecipeSwapperRoute
    @Serializable data class RecipeDetails(val recipeId: String) : RecipeSwapperRoute
    @Serializable data class EventDetails(val eventId: String) : RecipeSwapperRoute
    @Serializable data object Favourites : RecipeSwapperRoute
    @Serializable data object Badges : RecipeSwapperRoute
    @Serializable data object AddEvent: RecipeSwapperRoute
    @Serializable data object Categories: RecipeSwapperRoute
    @Serializable data class EditRecipe(val recipeId: String) : RecipeSwapperRoute
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun RecipeSwapperNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val recipesViewModel = koinViewModel<RecipesViewModel>()
    val recipesState by recipesViewModel.state.collectAsStateWithLifecycle()
    recipesViewModel.actions.updateRecipesDB()

    val userViewModel = koinViewModel<UserViewModel>()
    val userState by userViewModel.state.collectAsStateWithLifecycle()

    val badgesViewModel = koinViewModel<BadgesViewModel>()
    badgesViewModel.updateBadgesDB()

    val eventsViewModel = koinViewModel<EventsViewModel>()
    val eventsState by eventsViewModel.state.collectAsStateWithLifecycle()
    eventsViewModel.actions.updateEventsDB()

    val categoriesViewModel = koinViewModel<CategoriesViewModel>()
    categoriesViewModel.actions.updateCategoriesDB()

    val currentUser = FirebaseAuth.getInstance().currentUser

    NavHost(
        navController = navController,
        startDestination = if (currentUser == null) RecipeSwapperRoute.Authentication else RecipeSwapperRoute.Home,
    ) {

        composable<RecipeSwapperRoute.Home> {
            HomeScreen(
                recipesState,
                navController,
                onRecipeClick = { recipeId ->
                    navController.navigate(RecipeSwapperRoute.RecipeDetails(recipeId))
                },
                onSearch = { query -> recipesViewModel.actions.updateSearch(query)
                           eventsViewModel.actions.updateSearch(query)},
                userViewModel.actions,
                userState,
                eventsState,
                onEventClick = { eventId ->
                    navController.navigate(RecipeSwapperRoute.EventDetails(eventId))
                }
            )
        }

        composable<RecipeSwapperRoute.Authentication> {
            val authViewModel = koinViewModel<AuthViewModel>()
            val state by authViewModel.state.collectAsStateWithLifecycle()
            AuthScreen(state, authViewModel.actions, onAuthSuccess = {
                userViewModel.userId.value = state.currentUser?.uid
                navController.navigate(RecipeSwapperRoute.Home) {
                    popUpTo(0) { inclusive = true }
                }
            })
        }

        composable<RecipeSwapperRoute.Profile> {
            val authViewModel = koinViewModel<AuthViewModel>()
            val state by userViewModel.state.collectAsStateWithLifecycle()
            UserScreen(state, recipesState, eventsState, eventsViewModel.actions,
                onEventClick = { eventId ->
                    navController.navigate(RecipeSwapperRoute.EventDetails(eventId))
                },
                onRecipeClick = { recipeId ->
                    navController.navigate(RecipeSwapperRoute.RecipeDetails(recipeId))
                },  userViewModel.actions, logout = {
                    authViewModel.actions.logout()
                    navController.navigate(RecipeSwapperRoute.Authentication)
            }, navController)
        }

        composable<RecipeSwapperRoute.Settings> {
            val settingsViewModel = koinViewModel<SettingsViewModel>()
            val state by settingsViewModel.state.collectAsStateWithLifecycle()
            SettingsScreen(state, settingsViewModel::changeTheme)
        }

        composable<RecipeSwapperRoute.AddRecipe> {
            val addRecipeViewModel = koinViewModel<AddRecipeViewModel>()
            val addRecipeState by addRecipeViewModel.state.collectAsStateWithLifecycle()
            val categoriesState by categoriesViewModel.state.collectAsStateWithLifecycle()
            AddRecipeScreen(
                addRecipeState,
                addRecipeViewModel.actions,
                navController,
                categoriesState,
                userState.currentUser
            )
        }

        composable<RecipeSwapperRoute.RecipeDetails> { backStackEntry ->
            val route = backStackEntry.toRoute<RecipeSwapperRoute.RecipeDetails>()
            val recipe = recipesState.recipes.find { it.id == route.recipeId }
            if (recipe != null) RecipeDetailsScreen(
                navController,
                recipe,
                userViewModel.actions,
                userState.currentUser,
                recipesViewModel.actions
            )
        }

        composable<RecipeSwapperRoute.EventDetails> { backStackEntry ->
            val route = backStackEntry.toRoute<RecipeSwapperRoute.EventDetails>()
            val event = eventsState.events.find { it.id == route.eventId }
            if (event != null) EventDetailsScreen(navController, event, eventsViewModel.actions, userState, recipesState)
        }

        composable<RecipeSwapperRoute.Favourites> {
            FavouritesScreen(
                recipesState,
                userState,
                onRecipeClick = { recipeId ->
                    navController.navigate(RecipeSwapperRoute.RecipeDetails(recipeId))
                },
                navController,
                userViewModel.actions
            )
        }

        composable<RecipeSwapperRoute.Badges> {
            val state by badgesViewModel.state.collectAsStateWithLifecycle()
            BadgesScreen(state, userState, navController)
        }

        composable<RecipeSwapperRoute.AddEvent> {
            val addEventViewModel = koinViewModel<AddEventViewModel>()
            val state by addEventViewModel.state.collectAsStateWithLifecycle()
            val host = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            AddEventScreen(
                state,
                addEventViewModel.actions,
                recipesState,
                userState,
                navController
            )
        }

        composable<RecipeSwapperRoute.Categories> {
            val state by categoriesViewModel.state.collectAsStateWithLifecycle()
            CategoriesScreen(state, categoriesViewModel.actions, navController,
                onFilter = { category ->
                    recipesViewModel.actions.setCategoryFilter(category)
                    //navController.navigate(RecipeSwapperRoute.Home)
                }
            )
        }

        composable<RecipeSwapperRoute.EditRecipe> { backStackEntry ->
            val route = backStackEntry.toRoute<RecipeSwapperRoute.EditRecipe>()
            val recipe = recipesState.recipes.find { it.id == route.recipeId }
            val addRecipeViewModel = koinViewModel<AddRecipeViewModel>()
            val addRecipeState by addRecipeViewModel.state.collectAsStateWithLifecycle()
            val categoriesState by categoriesViewModel.state.collectAsStateWithLifecycle()
            if (recipe != null) AddRecipeScreen(
                addRecipeState,
                addRecipeViewModel.actions,
                navController,
                categoriesState,
                recipe = recipe
            )
        }
    }
}