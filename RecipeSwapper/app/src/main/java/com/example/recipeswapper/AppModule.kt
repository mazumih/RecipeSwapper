package com.example.recipeswapper

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room.databaseBuilder
import com.example.recipeswapper.data.database.BadgesDatabase
import com.example.recipeswapper.data.database.RecipeSwapperDatabase
import com.example.recipeswapper.data.remote.OSMDataSource
import com.example.recipeswapper.data.repositories.BadgesRepository
import com.example.recipeswapper.data.repositories.RecipesRepository
import com.example.recipeswapper.data.repositories.SettingsRepository
import com.example.recipeswapper.ui.screens.addevent.AddEventViewModel
import com.example.recipeswapper.ui.screens.addrecipe.AddRecipeViewModel
import com.example.recipeswapper.ui.screens.profilescreen.ProfileViewModel
import com.example.recipeswapper.ui.screens.settings.SettingsViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val Context.dataStore by preferencesDataStore("theme")

val appModule = module {

    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }
    }
    single { OSMDataSource(get()) }

    single { get<Context>().dataStore }

    single { SettingsRepository(get()) }

    viewModel { SettingsViewModel(get()) }

    single { AddRecipeViewModel() }

    single { AddEventViewModel() }

    single { ProfileViewModel() }

    viewModel { RecipeViewModel(get()) }

    single {
        databaseBuilder(
            get(),
            RecipeSwapperDatabase::class.java,
            "recipe-list"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    single {
        databaseBuilder(
            get(),
            BadgesDatabase::class.java,
            "badge-list"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    single { BadgesRepository(get<BadgesDatabase>().badgeDao()) }

    single { RecipesRepository(get<RecipeSwapperDatabase>().recipesDAO()) }

    viewModel { BadgeViewModel(get(), get<RecipeViewModel>().state) }
}