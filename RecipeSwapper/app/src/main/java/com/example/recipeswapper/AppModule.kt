package com.example.recipeswapper

import androidx.room.Room.databaseBuilder
import com.example.recipeswapper.data.database.RecipeSwapperDatabase
import com.example.recipeswapper.data.repositories.RecipesRepository
import com.example.recipeswapper.ui.screens.addrecipe.AddRecipeViewModel
import com.example.recipeswapper.ui.screens.badges.BadgeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single { AddRecipeViewModel() }

    single { BadgeViewModel() }

    viewModel { RecipeViewModel(get()) }

    single {
        databaseBuilder(
            get(),
            RecipeSwapperDatabase::class.java,
            "recipe-list"
        ).build()
    }

    single { RecipesRepository(get<RecipeSwapperDatabase>().recipesDAO()) }
}