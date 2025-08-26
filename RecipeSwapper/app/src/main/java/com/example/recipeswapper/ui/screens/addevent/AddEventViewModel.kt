package com.example.recipeswapper.ui.screens.addevent

import androidx.lifecycle.ViewModel
import com.example.recipeswapper.data.models.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class AddEventState(
    val title: String = "",
    val description: String = "",
    val location: String = "",
    val date: Long? = null,
    val dateString: String = "",

    val showLocationDisabledAlert: Boolean = false,
    val showLocationPermissionDeniedAlert: Boolean = false,
    val showLocationPermissionPermanentlyDeniedSnackbar: Boolean = false,
    val showNoInternetConnectivitySnackbar: Boolean = false
) {
    val canSubmit get() = title.isNotBlank() && description.isNotBlank() && location.isNotBlank() && date != null && date >= System.currentTimeMillis()

    fun toEvent() = Event(
        title = title,
        description =  description,
        location = location,
        date = date
    )
}

interface AddEventActions {
    fun setTitle(title: String)
    fun setDescription(description: String)
    fun setLocation(location: String)
    fun setDate(date: Long)
    fun setDateString(date: String)

    fun setShowLocationDisabledAlert(show: Boolean)
    fun setShowLocationPermissionDeniedAlert(show: Boolean)
    fun setShowLocationPermissionPermanentlyDeniedSnackbar(show: Boolean)
    fun setShowNoInternetConnectivitySnackbar(show: Boolean)
}

class AddEventViewModel : ViewModel() {
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