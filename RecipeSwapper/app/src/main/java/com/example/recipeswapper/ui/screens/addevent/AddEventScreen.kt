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
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.recipeswapper.ui.composable.AppBar
import com.example.recipeswapper.utils.LocationService
import com.example.recipeswapper.utils.PermissionStatus
import com.example.recipeswapper.utils.rememberMultiplePermissions
import kotlinx.coroutines.launch

@Composable
fun AddEventScreen(
    navController: NavHostController
) {
    var showLocationDisabledAlert by remember { mutableStateOf(false) }
    var showPermissionDeniedAlert by remember { mutableStateOf(false) }
    var showPermissionPermanentlyDeniedSnackbar by remember { mutableStateOf(false) }

    val ctx = LocalContext.current
    val locationService = remember { LocationService(ctx) }

    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    fun getCurrentLocation() = scope.launch {
        try {
            locationService.getCurrentLocation()
        } catch (_: IllegalStateException) {
            showLocationDisabledAlert = true
        }
    }

    val locationPermissions = rememberMultiplePermissions(
        listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    ) { statuses ->
        when {
            statuses.any { it.value == PermissionStatus.Granted } ->
                getCurrentLocation()
            statuses.all { it.value == PermissionStatus.PermanentlyDenied } ->
                showPermissionPermanentlyDeniedSnackbar = true
            else ->
                showPermissionDeniedAlert = true
        }
    }

    fun getLocationOrRequestPermission() {
        if (locationPermissions.statuses.any { it.value.isGranted }) {
            getCurrentLocation()
        } else {
            locationPermissions.launchPermissionRequest()
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = { AppBar(navController, "Crea Evento") },
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
                value = "",
                onValueChange = { /* TODO */ },
                label = { Text("Nome della serata") },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = "",
                onValueChange = { /* TODO */ },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = "",
                onValueChange = { /* TODO */ },
                label = { Text("Data") },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = "",
                onValueChange = { /* TODO */ },
                label = { Text("Posto") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    if (isLoading) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    } else {
                        IconButton(onClick = ::getLocationOrRequestPermission) {
                            Icon(Icons.Outlined.MyLocation, "Current location")
                        }
                    }
                }
            )
            Spacer(Modifier.size(4.dp))
        }
        if (showLocationDisabledAlert) {
            AlertDialog(
                title = { Text("Location disabled") },
                text = { Text("Location must be enabled to get your coordinates in the app.") },
                confirmButton = {
                    TextButton(onClick = {
                        locationService.openLocationSettings()
                        showLocationDisabledAlert = false
                    }) {
                        Text("Enable")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showLocationDisabledAlert = false
                    }) {
                        Text("Dismiss")
                    }
                },
                onDismissRequest = {
                    showLocationDisabledAlert = false
                }
            )
        }
        if (showPermissionDeniedAlert) {
            AlertDialog(
                title = { Text("Location permission denied") },
                text = { Text("Location permission is required to get your coordinates in the app.") },
                confirmButton = {
                    TextButton(onClick = {
                        locationPermissions.launchPermissionRequest()
                        showPermissionDeniedAlert = false
                    }) {
                        Text("Grant")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showPermissionDeniedAlert = false
                    }) {
                        Text("Dismiss")
                    }
                },
                onDismissRequest = { showPermissionDeniedAlert = false }
            )
        }
        if (showPermissionPermanentlyDeniedSnackbar) {
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
                showPermissionPermanentlyDeniedSnackbar = false
            }
        }
    }
}