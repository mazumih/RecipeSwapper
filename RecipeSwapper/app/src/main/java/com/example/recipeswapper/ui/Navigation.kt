package com.example.recipeswapper.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.recipeswapper.ui.screens.HomeScreen
import kotlinx.serialization.Serializable

sealed interface SwapperRoute {
    @Serializable data object Home : SwapperRoute
}

@Composable
fun SwapperNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = SwapperRoute.Home,
    ) {
        composable<SwapperRoute.Home> {
            HomeScreen()
        }
    }
}