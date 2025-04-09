package com.example.recipeswapper.ui.screens.badges

import androidx.lifecycle.ViewModel
import com.example.recipeswapper.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class Badge(
    val name: String,
    val imageResId: Int,
    val isUnlocked: Boolean
)

class BadgeViewModel : ViewModel() {
    private val _state = MutableStateFlow(
        listOf(
            Badge("Bree", R.drawable.bree_badge, isUnlocked = false),
            Badge("Susan", R.drawable.susan_badge, isUnlocked = false),
            Badge("Lynette", R.drawable.lynette_badge, isUnlocked = false),
            Badge("Gabrielle", R.drawable.gabrielle_badge, isUnlocked = false)
        )
    )
    val state = _state.asStateFlow()

    fun unlockBadge(name: String) {
        _state.update { list ->
            list.map {
                if (it.name == name) it.copy(isUnlocked = true) else it
            }
        }
    }
}