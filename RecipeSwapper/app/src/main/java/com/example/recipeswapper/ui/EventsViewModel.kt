package com.example.recipeswapper.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeswapper.data.models.Event
import com.example.recipeswapper.data.repositories.EventsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class EventsState(
    val events: List<Event> = emptyList()
)

class EventsViewModel(
    private val eventsRepository: EventsRepository
) : ViewModel() {

    val state = eventsRepository.getAllEvents().map { EventsState(events = it) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = EventsState()
    )

    fun updateEventsDB() {
        viewModelScope.launch {
            eventsRepository.getEventsDB()
        }
    }

    fun addEvent(event: Event, host: String) {
        viewModelScope.launch {
            eventsRepository.addEvent(event, host)
        }
    }

}