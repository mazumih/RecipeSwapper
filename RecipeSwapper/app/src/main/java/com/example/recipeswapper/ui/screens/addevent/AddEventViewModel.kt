package com.example.recipeswapper.ui.screens.addevent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeswapper.data.models.Event
import com.example.recipeswapper.data.repositories.BadgesRepository
import com.example.recipeswapper.data.repositories.EventsRepository
import com.example.recipeswapper.utils.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class AddEventState(
    val title: String = "",
    val description: String = "",
    val location: String = "",
    val date: Long? = null,
    val dateString: String = "",
    val maxParticipants: Long = 0,
    val maxParticipantsString: String = "",
    val recipeId: String = "",

    val showLocationDisabledAlert: Boolean = false,
    val showLocationPermissionDeniedAlert: Boolean = false,
    val showLocationPermissionPermanentlyDeniedSnackbar: Boolean = false,
    val showNoInternetConnectivitySnackbar: Boolean = false
) {
    val canSubmit get() = title.isNotBlank() && description.isNotBlank() && location.isNotBlank() && date != null && date >= System.currentTimeMillis()
            && maxParticipantsString.isNotBlank() && recipeId.isNotBlank()

    fun toEvent() = Event(
        title = title,
        description =  description,
        location = location,
        date = date,
        maxParticipants = maxParticipants,
        recipeId = recipeId
    )
}

interface AddEventActions {
    fun setTitle(title: String)
    fun setDescription(description: String)
    fun setLocation(location: String)
    fun setDate(date: Long)
    fun setDateString(date: String)
    fun setMaxParticipantsString(maxParticipantsString: String)
    fun setRecipe(recipeId: String)
    fun addEvent(event: Event, host: String, notifier: NotificationHelper)

    fun setShowLocationDisabledAlert(show: Boolean)
    fun setShowLocationPermissionDeniedAlert(show: Boolean)
    fun setShowLocationPermissionPermanentlyDeniedSnackbar(show: Boolean)
    fun setShowNoInternetConnectivitySnackbar(show: Boolean)
}

class AddEventViewModel(
    private val eventsRepository: EventsRepository,
    private val badgesRepository: BadgesRepository
) : ViewModel() {
    private val _state = MutableStateFlow(AddEventState())
    val state = _state.asStateFlow()

    val actions = object : AddEventActions {
        override fun setTitle(title: String) {
            _state.update { it.copy(title = title) }
        }

        override fun setDescription(description: String) {
            _state.update { it.copy(description = description) }
        }

        override fun setLocation(location: String) {
            _state.update { it.copy(location = location) }
        }

        override fun setDate(date: Long) {
            _state.update { it.copy(date = date, dateString = formattedDate(date)) }
        }

        override fun setRecipe(recipeId: String) {
            _state.update { it.copy(recipeId = recipeId)}
        }

        override fun setDateString(date: String) {
            _state.update { it.copy(dateString = date) }
            if (date.length == 10) {
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                sdf.isLenient = false

                val dateLong = try {
                    sdf.parse(date)?.time
                } catch (e: Exception) {
                    null
                }
                if (dateLong != null) _state.update { it.copy(date = dateLong) }
            }
        }

        override fun setMaxParticipantsString(maxParticipantsString: String) {
            _state.update { it.copy(maxParticipants = maxParticipantsString.toLongOrNull() ?: 0) }
            _state.update { it.copy(maxParticipantsString = maxParticipantsString) }
        }

        override fun addEvent(event: Event, host: String, notifier: NotificationHelper) {
            viewModelScope.launch {
                eventsRepository.addEvent(event, host)
                badgesRepository.checkBadges(host, notifier)
            }
        }

        override fun setShowLocationDisabledAlert(show: Boolean) {
            _state.update { it.copy(showLocationDisabledAlert = show) }
        }

        override fun setShowLocationPermissionDeniedAlert(show: Boolean) {
            _state.update { it.copy(showLocationPermissionDeniedAlert = show) }
        }

        override fun setShowLocationPermissionPermanentlyDeniedSnackbar(show: Boolean) {
            _state.update { it.copy(showLocationPermissionPermanentlyDeniedSnackbar = show) }
        }

        override fun setShowNoInternetConnectivitySnackbar(show: Boolean) {
            _state.update { it.copy(showNoInternetConnectivitySnackbar = show) }
        }

    }

    fun formattedDate(date: Long): String {
        return date.let {
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it))
        } ?: ""
    }
}