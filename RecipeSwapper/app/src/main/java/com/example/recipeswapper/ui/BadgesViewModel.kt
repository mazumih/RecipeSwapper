package com.example.recipeswapper.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeswapper.data.models.Badge
import com.example.recipeswapper.data.repositories.BadgesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class BadgesState(
    val badges: List<Badge> = emptyList(),
)

class BadgesViewModel (
    private val badgesRepository: BadgesRepository
) : ViewModel() {

    val state = badgesRepository.getAllBadges().map { BadgesState(badges = it) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = BadgesState()
    )

    fun updateBadgesDB() {
        viewModelScope.launch {
            badgesRepository.getBadgesDB()
        }
    }

    /*fun checkBadges(userId: String) {
        viewModelScope.launch {
            badgesRepository.checkBadges(userId)
        }
    }*/
}