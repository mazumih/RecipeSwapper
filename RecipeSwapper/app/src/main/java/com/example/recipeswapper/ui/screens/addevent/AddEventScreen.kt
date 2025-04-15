package com.example.recipeswapper.ui.screens.addevent

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.recipeswapper.data.remote.OSMDataSource
import com.example.recipeswapper.ui.composable.AppBar
import com.example.recipeswapper.utils.LocationService
import com.example.recipeswapper.utils.PermissionStatus
import com.example.recipeswapper.utils.isOnline
import com.example.recipeswapper.utils.openWirelessSettings
import com.example.recipeswapper.utils.rememberMultiplePermissions
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun AddEventScreen(
    navController: NavHostController,
    state: AddEventState,
    onSubmit: () -> Unit,
    actions: AddEventActions
) {
    val ctx = LocalContext.current
    val locationService = remember { LocationService(ctx) }

    var isLoading by remember { mutableStateOf(false) }

    val locationPermissions = rememberMultiplePermissions(
        listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
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
    val scope = rememberCoroutineScope()
    fun getCurrentLocationName() = scope.launch {
        if (locationPermissions.statuses.none { it.value.isGranted }) {
            locationPermissions.launchPermissionRequest()
            return@launch
        }
        val coordinates = try {
            locationService.getCurrentLocation() ?: return@launch
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
        topBar = { AppBar(navController, "Crea Evento") },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.tertiary,
                onClick = {
                    if(!state.canSubmit) return@FloatingActionButton
                    //onSubmit
                    actions.clearForm()
                    navController.navigateUp()
                }
            ) {
                Icon(Icons.Filled.Check, "Add Event")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                value = state.name,
                onValueChange = actions::setName,
                label = { Text("Nome della serata") },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.date,
                onValueChange = actions::setDate,
                label = { Text("Date") },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.description,
                onValueChange = actions::setDescription,
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.location,
                onValueChange = actions::setLocation,
                label = { Text("Posto") },
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
                                    isLoading = true
                                    getCurrentLocationName().join()
                                    isLoading = false
                                }
                            }
                        ) {
                            Icon(Icons.Outlined.MyLocation, "Current location")
                        }
                    }
                }
            )
            Spacer(Modifier.size(4.dp))
        }
        if (state.showLocationDisabledAlert) {
            AlertDialog(
                title = { Text("Location disabled") },
                text = { Text("Location must be enabled to get your coordinates in the app.") },
                confirmButton = {
                    TextButton(onClick = {
                        locationService.openLocationSettings()
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
                onDismissRequest = {
                    actions.setShowLocationDisabledAlert(false)
                }
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
                actions.setShowLocationPermissionDeniedAlert(false)
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