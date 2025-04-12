package com.example.recipeswapper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.recipeswapper.data.repositories.Theme
import com.example.recipeswapper.ui.SwapperNavGraph
import com.example.recipeswapper.ui.screens.settings.SettingsViewModel
import com.example.recipeswapper.ui.theme.RecipeSwapperTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeViewModel = koinViewModel<SettingsViewModel>()
            val themeState by themeViewModel.state.collectAsStateWithLifecycle()

            RecipeSwapperTheme(
                darkTheme = when (themeState.theme) {
                    Theme.Light -> false
                    Theme.Dark -> true
                    Theme.System -> isSystemInDarkTheme()
                }
            ) {
                val navController = rememberNavController()
                SwapperNavGraph(
                    navController,
                    themeState,
                    themeViewModel::changeTheme)
            }
        }
    }
}
