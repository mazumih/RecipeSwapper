package com.example.recipeswapper.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.recipeswapper.RecipeViewModel
import com.example.recipeswapper.ui.screens.addrecipe.AddRecipeScreen
import com.example.recipeswapper.ui.screens.addrecipe.AddRecipeViewModel
import com.example.recipeswapper.ui.screens.home.HomeScreen
import com.example.recipeswapper.ui.screens.recipedetails.RecipeDetailsScreen
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

sealed interface SwapperRoute {
    @Serializable data object Home : SwapperRoute
    @Serializable data object AddRecipe : SwapperRoute
    @Serializable data class RecipeDetails(val recipeId: Int): SwapperRoute
}

@Composable
fun SwapperNavGraph(navController: NavHostController) {
    val recipesVm = koinViewModel<RecipeViewModel>()
    val recipesState by recipesVm.state.collectAsStateWithLifecycle()

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
        composable<SwapperRoute.RecipeDetails> { backStackEntry ->
            val route = backStackEntry.toRoute<SwapperRoute.RecipeDetails>()
            recipesState.recipes.find { it.id == route.recipeId }?. let { recipe ->
                RecipeDetailsScreen(
                    onSubmit = { recipesVm.deleteRecipe(recipe) },
                    recipe,
                    navController
                )
            } ?: run {
                navController.navigateUp()
            }
        }
    }
}