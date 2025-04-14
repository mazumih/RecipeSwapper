package com.example.recipeswapper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeswapper.data.database.Badge
import com.example.recipeswapper.data.database.Recipe
import com.example.recipeswapper.data.repositories.BadgesRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class BadgeState(val badges: List<Badge>)

class BadgeViewModel(
    private val repository: BadgesRepository,
    private val recipeFlow: StateFlow<RecipesState>
) : ViewModel() {
    val state = repository.badges.map { BadgeState(badges = it) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = BadgeState(listOf())
    )

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            initialize()
        }

        viewModelScope.launch {
            recipeFlow
                .collect { recipe ->
                    checkBadge(recipe.recipes)
                }
        }
    }

    private suspend fun initialize() {
        if(repository.getBadgeCount() == 0) {
            repository.insertBadges(
                listOf(
                    Badge(R.drawable.bree_badge,"Bree", isUnlocked = false, ""),
                    Badge(R.drawable.susan_badge,"Susan", isUnlocked = false, ""),
                    Badge(R.drawable.lynette_badge,"Lynette", isUnlocked = false, ""),
                    Badge(R.drawable.gabrielle_badge, "Gabrielle", isUnlocked = false, "")
                )
            )
        }
    }

    /* migliorare e diversificare controlli */
    private suspend fun checkBadge(recipes: List<Recipe>) {
        when(recipes.size) {
            1 -> unlockBadge("Susan")
            2 -> unlockBadge("Gabrielle")
            3 -> unlockBadge("Lynette")
            4 -> unlockBadge("Bree")
            5 -> repository.lockAll()
        }
    }

    private suspend fun unlockBadge(name: String) {
        if(!repository.isUnlocked(name)) {
            repository.unlockBadge(name)
            _toastEvent.emit("Unlocked $name's badge")
        }
    }
}