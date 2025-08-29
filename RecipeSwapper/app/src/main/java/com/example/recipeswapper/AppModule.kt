package com.example.recipeswapper

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.recipeswapper.data.database.RecipeSwapperDatabase
import com.example.recipeswapper.data.remote.OSMDataSource
import com.example.recipeswapper.data.repositories.AuthRepository
import com.example.recipeswapper.data.repositories.BadgesRepository
import com.example.recipeswapper.data.repositories.EventsRepository
import com.example.recipeswapper.data.repositories.RecipesRepository
import com.example.recipeswapper.data.repositories.SettingsRepository
import com.example.recipeswapper.data.repositories.UserRepository
import com.example.recipeswapper.ui.EventsViewModel
import com.example.recipeswapper.ui.RecipesViewModel
import com.example.recipeswapper.ui.screens.addevent.AddEventViewModel
import com.example.recipeswapper.ui.screens.addrecipe.AddRecipeViewModel
import com.example.recipeswapper.ui.screens.authentication.AuthViewModel
import com.example.recipeswapper.ui.BadgesViewModel
import com.example.recipeswapper.ui.screens.user.UserViewModel
import com.example.recipeswapper.ui.screens.settings.SettingsViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import org.koin.core.module.dsl.viewModel

val Context.dataStore by preferencesDataStore("theme")

val appModule = module {

    single {
        Room.databaseBuilder(
            get(),
            RecipeSwapperDatabase::class.java,
            "recipe-swapper"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    single { get<Context>().dataStore }

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

    single { Firebase.auth }
    single { Firebase.firestore }
    single { Firebase.storage }

    single { AuthRepository(get()) }
    single { SettingsRepository(get()) }
    single { RecipesRepository(get(), get<RecipeSwapperDatabase>().recipeDao(), get<Context>().contentResolver) }
    single { UserRepository(get<RecipeSwapperDatabase>().userDao(), get(), get<Context>().contentResolver) }
    single { BadgesRepository(get<RecipeSwapperDatabase>().badgeDao(), get(), get(), get()) }
    single { EventsRepository(get(), get<RecipeSwapperDatabase>().eventDao()) }

    viewModel { RecipesViewModel(get()) }
    viewModel { AuthViewModel(get(), get()) }
    viewModel { UserViewModel(get(), get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { AddRecipeViewModel(get(), get()) }
    viewModel { BadgesViewModel(get()) }
    viewModel { EventsViewModel(get()) }
    viewModel { AddEventViewModel() }
}