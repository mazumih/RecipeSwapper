package com.example.recipeswapper.ui.screens.addevent

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.recipeswapper.data.remote.OSMDataSource
import com.example.recipeswapper.ui.composables.TopBar
import com.example.recipeswapper.utils.Location
import com.example.recipeswapper.utils.PermissionStatus
import com.example.recipeswapper.utils.rememberMultiplePermissions
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.onFocusChanged
import com.example.recipeswapper.utils.isOnline
import com.example.recipeswapper.utils.openWirelessSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreen(
    state: AddEventState,
    actions: AddEventActions,
    onSubmit: () -> Unit,
    navController: NavController
) {
    val ctx = LocalContext.current
    val location = remember { Location(ctx) }
    var showDatePicker by remember { mutableStateOf(false) }

    val locationPermissions = rememberMultiplePermissions(
        listOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
    ) { statuses ->
        when {
            statuses.any { it.value == PermissionStatus.Granted } -> {}
            statuses.all { it.value == PermissionStatus.PermanentlyDenied } ->
                actions.setShowLocationPermissionPermanentlyDeniedSnackbar(true)
            else ->
                actions.setShowLocationPermissionDeniedAlert(true)
        }
    }

    val osmDataSource = koinInject<OSMDataSource>()
    val isLoading by location.isLoadingLocation.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    var isDateWritten by remember { mutableStateOf(false) }

    fun getCurrentLocation() = scope.launch {
        if (locationPermissions.statuses.none { it.value.isGranted }) {
            locationPermissions.launchPermissionRequest()
            return@launch
        }
        val coordinates = try {
            location.getCurrentLocation() ?: return@launch
        } catch (_: IllegalStateException) {
            actions.setShowLocationDisabledAlert(true)
            return@launch
        }
        if (!isOnline(ctx)) {
            actions.setShowNoInternetConnectivitySnackbar(true)
            return@launch
        }
        val place = osmDataSource.getPlace(coordinates)
        actions.setLocation(place.displayName)
    }

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = { TopBar(navController, "Add Event") },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.tertiary,
                onClick = {
                    if (!state.canSubmit) return@FloatingActionButton
                    onSubmit()
                    navController.navigateUp()
                }
            ) {
                Icon(Icons.Outlined.Check, "Add Event")
            }
        }
    ) { contentPadding ->

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(contentPadding)
                .padding(12.dp)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = state.title,
                onValueChange = actions::setTitle,
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.description,
                onValueChange = actions::setDescription,
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.size(16.dp))

            OutlinedTextField(
                value = state.location,
                onValueChange = actions::setLocation,
                label = { Text("Location") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    if (isLoading) {
                        CircularProgressIndicator(
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(24.dp),
                        )
                    } else {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    /*isLoading = true*/
                                    getCurrentLocation().join()
                                    /*isLoading = false*/
                                }
                            }
                        ) {
                            Icon(Icons.Outlined.MyLocation, "Current location")
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.dateString,
                onValueChange = { input ->
                    if (input.length < state.dateString.length) isDateWritten = true
                    if (!isDateWritten) return@OutlinedTextField

                    var date = ""
                    input.forEachIndexed { i, c ->
                        if (input.length >= state.dateString.length) {
                            when (i) {
                                2, 5 -> if (c == '/') date += c
                                0, 1, 3, 4, 6, 7, 8, 9 -> if (c.isDigit()) date += c
                            }
                        }
                    }

                    if (date.length <= 10) {
                        actions.setDateString(date)
                    }
                },
                label = { Text("Data") },
                placeholder = { Text("dd/mm/yyyy") },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focus ->
                        isDateWritten = focus.isFocused
                    },
                trailingIcon = {
                    IconButton(
                        onClick = { showDatePicker = true }
                    ) {
                        Icon(Icons.Outlined.CalendarMonth, contentDescription = "Calendario")
                    }
                },
                isError = state.date != null && state.date <= System.currentTimeMillis()
            )

            if (showDatePicker) {
                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = if (state.date != null && state.date >= System.currentTimeMillis()) state.date else System.currentTimeMillis(),
                    initialDisplayedMonthMillis = if (state.date != null && state.date >= System.currentTimeMillis()) state.date else System.currentTimeMillis(),
                    selectableDates = object : SelectableDates {
                        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                            return utcTimeMillis >= System.currentTimeMillis()
                        }
                    }
                )
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                datePickerState.selectedDateMillis?.let {
                                    actions.setDate(it)
                                }
                                showDatePicker = false
                                isDateWritten = false
                            }
                        ) { Text("OK") }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showDatePicker = false }
                        ) {
                            Text("Annulla")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }
        }

        if (state.showLocationDisabledAlert) {
            AlertDialog(
                title = { Text("Location disabled") },
                text = { Text("Location must be enabled to get your coordinates in the app.") },
                confirmButton = {
                    TextButton(onClick = {
                        location.openLocationSettings()
                        actions.setShowLocationDisabledAlert(false)
                    }) {
                        Text("Enable")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        actions.setShowLocationDisabledAlert(false)
                    }) {
                        Text("Dismiss")
                    }
                },
                onDismissRequest = { actions.setShowLocationDisabledAlert(false) }
            )
        }

        if (state.showLocationPermissionDeniedAlert) {
            AlertDialog(
                title = { Text("Location permission denied") },
                text = { Text("Location permission is required to get your coordinates in the app.") },
                confirmButton = {
                    TextButton(onClick = {
                        locationPermissions.launchPermissionRequest()
                        actions.setShowLocationPermissionDeniedAlert(false)
                    }) {
                        Text("Grant")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        actions.setShowLocationPermissionDeniedAlert(false)
                    }) {
                        Text("Dismiss")
                    }
                },
                onDismissRequest = { actions.setShowLocationPermissionDeniedAlert(false) }
            )
        }

        if (state.showLocationPermissionPermanentlyDeniedSnackbar) {
            LaunchedEffect(snackbarHostState) {
                val res = snackbarHostState.showSnackbar(
                    "Location permission is required.",
                    "Go to Settings",
                    duration = SnackbarDuration.Long
                )
                if (res == SnackbarResult.ActionPerformed) {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", ctx.packageName, null)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    if (intent.resolveActivity(ctx.packageManager) != null) {
                        ctx.startActivity(intent)
                    }
                }
                actions.setShowLocationPermissionPermanentlyDeniedSnackbar(false)
            }
        }

        if (state.showNoInternetConnectivitySnackbar) {
            LaunchedEffect(snackbarHostState) {
                val res = snackbarHostState.showSnackbar(
                    message = "No Internet connectivity",
                    actionLabel = "Go to Settings",
                    duration = SnackbarDuration.Long
                )
                if (res == SnackbarResult.ActionPerformed) {
                    openWirelessSettings(ctx)
                }
                actions.setShowNoInternetConnectivitySnackbar(false)
            }
        }

    }
}