package com.example.recipeswapper.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recipeswapper.data.models.Event
import com.example.recipeswapper.data.models.Recipe
import com.example.recipeswapper.data.repositories.EventsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class EventsState(
    val events: List<Event> = emptyList(),
    var search: String = "",
) {
    val filteredEvents: List<Event>
        get() = events.filter { event ->
            val searchFilter = search.isBlank() || event.title.contains(search, ignoreCase = true)
            searchFilter
        }
}

interface EventsActions {
    fun updateEventsDB()
    fun updateSearch(query: String)
    fun deleteEvent(event: Event)
    fun addParticipant(event: Event, userId: String)
}

class EventsViewModel(
    private val eventsRepository: EventsRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _categoryFilter = MutableStateFlow<String?>(null)

    val state = eventsRepository.getAllEvents()
        .combine(_searchQuery) { events, query ->
            EventsState(
                events = events,
                search = query
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = EventsState(emptyList())
        )

    val actions = object : EventsActions {

        override fun updateEventsDB() {
            viewModelScope.launch {
                eventsRepository.getEventsDB()
            }
        }

        override fun updateSearch(query: String) {
            _searchQuery.value = query
        }

        override fun deleteEvent(event: Event) {
            viewModelScope.launch {
                eventsRepository.deleteEvent(event)
            }
        }

        override fun addParticipant(event: Event, userId: String) {
            viewModelScope.launch {
                eventsRepository.addParticipant(event, userId)
            }
        }
    }
}