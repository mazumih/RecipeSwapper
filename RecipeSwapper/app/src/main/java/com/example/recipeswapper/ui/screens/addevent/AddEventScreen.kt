package com.example.recipeswapper.ui.screens.addevent

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.material3.AlertDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Group
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
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import com.example.recipeswapper.data.models.Recipe
import com.example.recipeswapper.ui.RecipesState
import com.example.recipeswapper.ui.screens.user.UserState
import com.example.recipeswapper.utils.NotificationHelper
import com.example.recipeswapper.utils.isOnline
import com.example.recipeswapper.utils.openWirelessSettings
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreen(
    state: AddEventState,
    actions: AddEventActions,
    recipesState: RecipesState,
    userState: UserState,
    navController: NavController
) {
    val host = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val favouriteRecipes = recipesState.recipes.filter { recipe ->
        userState.currentUser?.favouriteRecipes?.contains(recipe.id) == true &&
                recipe.author != host
    }
    val hostRecipes = recipesState.recipes.filter { recipe ->
        recipe.authorId == host
    }
    val recipes = favouriteRecipes + hostRecipes

    var expanded by remember { mutableStateOf(false) }
    var selectedRecipe by remember { mutableStateOf<Recipe?>(null) }

    val ctx = LocalContext.current
    val notifier = NotificationHelper(ctx)
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
        topBar = { TopBar(navController, "Nuovo evento") },
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 20.dp,
                    end = 20.dp,
                    top = 100.dp,
                    bottom = contentPadding.calculateBottomPadding()),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "New Event",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    OutlinedTextField(
                        value = state.title,
                        onValueChange = actions::setTitle,
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedLeadingIconColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = state.description,
                        onValueChange = actions::setDescription,
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedLeadingIconColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
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
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedLeadingIconColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
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
                                Icon(
                                    Icons.Outlined.CalendarMonth,
                                    contentDescription = "Calendario"
                                )
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        isError = state.date != null && state.date <= System.currentTimeMillis(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedLeadingIconColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = state.maxParticipantsString,
                        onValueChange = actions::setMaxParticipantsString,
                        label = { Text("Participants") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Outlined.Group, null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedLeadingIconColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = selectedRecipe?.title ?: "Select Recipe",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Ricetta") },
                            leadingIcon = { Icon(Icons.Outlined.RestaurantMenu, null) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedLeadingIconColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                        ) {
                            recipes.forEachIndexed { index, recipe ->
                                DropdownMenuItem(
                                    text = { Text(recipe.title) },
                                    onClick = {
                                        selectedRecipe = recipe
                                        expanded = false
                                        actions.setRecipe(recipe.id)
                                    }
                                )
                                if (index < recipes.lastIndex) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 4.dp),
                                        thickness = 1.dp,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = {
                            if (!state.canSubmit) return@Button
                            actions.addEvent(state.toEvent(), host, notifier)
                            navController.navigateUp()
                        },
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text("Crea Evento", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    }

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
                            val intent =
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
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
    }
}