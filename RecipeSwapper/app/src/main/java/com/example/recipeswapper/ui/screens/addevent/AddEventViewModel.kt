package com.example.recipeswapper.ui.screens.addevent

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AddEventState(
    val name: String = "",
    val date: String = "",
    val description: String = "",
    val location: String = "",

    val showLocationDisabledAlert: Boolean = false,
    val showLocationPermissionDeniedAlert: Boolean = false,
    val showLocationPermissionPermanentlyDeniedSnackbar: Boolean = false,
    val showNoInternetConnectivitySnackbar: Boolean = false
) {
    val canSubmit get() =
        name.isNotBlank()
                && date.isNotBlank()
                && description.isNotBlank()
                && location.isNotBlank()
}

interface AddEventActions {
    fun setName(name: String)
    fun setDate(date: String)
    fun setDescription(description: String)
    fun setLocation(location: String)

    fun setShowLocationDisabledAlert(show: Boolean)
    fun setShowLocationPermissionDeniedAlert(show: Boolean)
    fun setShowLocationPermissionPermanentlyDeniedSnackbar(show: Boolean)
    fun setShowNoInternetConnectivitySnackbar(show: Boolean)

    fun clearForm()
}

class AddEventViewModel : ViewModel() {
    private val _state = MutableStateFlow(AddEventState())
    val state = _state.asStateFlow()

    val actions = object : AddEventActions {
        override fun setName(name: String) {
            _state.update { it.copy(name = name) }
        }

        override fun setDate(date: String) {
            _state.update { it.copy(date = date) }
        }

        override fun setDescription(description: String) {
            _state.update { it.copy(description = description) }
        }

        override fun setLocation(location: String) {
            _state.update { it.copy(location = location) }
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

        override fun clearForm() {
            _state.update {
                it.copy(
                    name = "",
                    date = "",
                    description = "",
                    location = ""
                )
            }
        }
    }
}