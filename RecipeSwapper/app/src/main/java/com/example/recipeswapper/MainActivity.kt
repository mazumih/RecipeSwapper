package com.example.recipeswapper

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.recipeswapper.ui.RecipeSwapperNavGraph
import com.example.recipeswapper.ui.screens.settings.SettingsViewModel
import com.example.recipeswapper.ui.theme.RecipeSwapperTheme
import org.koin.androidx.compose.koinViewModel

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsViewModel = koinViewModel<SettingsViewModel>()
            val state by settingsViewModel.state.collectAsStateWithLifecycle()

            RecipeSwapperTheme(state.theme) {
                val navController = rememberNavController()
                RecipeSwapperNavGraph(navController)
            }
        }
    }
}