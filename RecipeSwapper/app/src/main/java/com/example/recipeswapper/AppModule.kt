package com.example.recipeswapper

import androidx.room.Room.databaseBuilder
import com.example.recipeswapper.data.database.BadgesDatabase
import com.example.recipeswapper.data.database.FavouriteRecipesDatabase
import com.example.recipeswapper.data.database.RecipeSwapperDatabase
import com.example.recipeswapper.data.repositories.BadgesRepository
import com.example.recipeswapper.data.repositories.FavouriteRecipesRepository
import com.example.recipeswapper.data.repositories.RecipesRepository
import com.example.recipeswapper.ui.AddFavouritesViewModel
import com.example.recipeswapper.ui.screens.addrecipe.AddRecipeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single { AddRecipeViewModel() }

    viewModel { RecipeViewModel(get()) }

    single {
        databaseBuilder(
            get(),
            RecipeSwapperDatabase::class.java,
            "recipe-list"
        ).build()
    }

    single {
        databaseBuilder(
            get(),
            BadgesDatabase::class.java,
            "badge-list"
        ).build()
    }

    single {
        databaseBuilder(
            get(),
            FavouriteRecipesDatabase::class.java,
            "favourites-list"
        ).build()
    }

    single { BadgesRepository(get<BadgesDatabase>().badgeDao()) }

    single { FavouriteRecipesRepository(get<FavouriteRecipesDatabase>().favRecipesDAO()) }

    single { RecipesRepository(get<RecipeSwapperDatabase>().recipesDAO()) }

    viewModel { BadgeViewModel(get(), get<RecipeViewModel>().state) }

    viewModel { AddFavouritesViewModel(get()) }
}