package com.example.recipeswapper.ui.screens.authentication

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.recipeswapper.R
import com.example.recipeswapper.data.models.Theme
import com.example.recipeswapper.ui.theme.RecipeSwapperTheme
import com.example.recipeswapper.utils.Google

@Composable
fun AuthScreen(
    state: AuthState,
    actions: AuthActions,
    onAuthSuccess: () -> Unit
) {
    
    var selected by remember { mutableStateOf(0) }
    val options = listOf("Accedi", "Registrati")

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var isLoadingLogin by remember { mutableStateOf(false) }
    var isLoadingGoogle by remember { mutableStateOf(false) }

    val ctx = LocalContext.current
    val googleSignIn = remember { Google(ctx) }

    LaunchedEffect(state.currentUser) {
        if (state.currentUser != null) {
            onAuthSuccess()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
                .animateContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Recipe Swapper",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Black
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Condividi e scopri nuove ricette",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                fontStyle = FontStyle.Italic
            )
            Spacer(Modifier.height(25.dp))
            TabRow(
                selectedTabIndex = selected,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp))
            ) {
                options.forEachIndexed { index, title ->
                    Tab(
                        selected = selected == index,
                        onClick = {
                            selected = index
                            actions.resetError()
                        },
                        text = {
                            Text(
                                title,
                                color = if (selected == index) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                                fontWeight = if (selected == index) FontWeight.Bold else FontWeight.Normal,
                                style = MaterialTheme.typography.titleMedium
                            )
                        },
                        modifier = Modifier
                            .background(
                                if (selected == index) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else Color.Transparent
                            )
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            AnimatedVisibility(visible = !state.error.isNullOrBlank()) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.errorContainer,
                                RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Outlined.Warning,
                            contentDescription = "Errore",
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = state.error ?: "",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Light
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                }
            }
            AnimatedContent(
                targetState = selected,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInHorizontally{ fullWidth -> fullWidth } + fadeIn(tween(300)) togetherWith
                                slideOutHorizontally { fullWidth -> -fullWidth } + fadeOut(tween(300))
                    } else {
                        slideInHorizontally { fullWidth -> -fullWidth } + fadeIn(tween(300)) togetherWith
                                slideOutHorizontally { fullWidth -> fullWidth } + fadeOut(tween(300))
                    }
                }
            ) { index ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (index == 0) {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email", style = MaterialTheme.typography.bodyLarge, fontStyle = FontStyle.Italic) },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(Icons.Outlined.Email, contentDescription = "email")
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedLeadingIconColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password", style = MaterialTheme.typography.bodyLarge, fontStyle = FontStyle.Italic) },
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                        contentDescription = if (passwordVisible) "Nascondi password" else "Mostra password"
                                    )
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedTrailingIconColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    } else {
                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            label = { Text("Username", style = MaterialTheme.typography.bodyLarge, fontStyle = FontStyle.Italic) },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(Icons.Outlined.Person, contentDescription = "username")
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedLeadingIconColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email", style = MaterialTheme.typography.bodyLarge, fontStyle = FontStyle.Italic) },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(Icons.Outlined.Email, contentDescription = "email")
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedLeadingIconColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password", style = MaterialTheme.typography.bodyLarge, fontStyle = FontStyle.Italic) },
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                        contentDescription = if (passwordVisible) "Nascondi password" else "Mostra password"
                                    )
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedTrailingIconColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text("Conferma Password", style = MaterialTheme.typography.bodyLarge, fontStyle = FontStyle.Italic) },
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = PasswordVisualTransformation(),
                            isError = confirmPassword.isNotEmpty() && !confirmPassword.equals(password),
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                errorContainerColor = Color.White
                            )
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
            Button(
                onClick = {
                    if (state.isLoading) return@Button
                    if (actions.isFormValid(email, password, username, confirmPassword, selected == 0)) {
                        isLoadingLogin = true
                        isLoadingGoogle = false
                        if (selected == 0) actions.login(email, password) else actions.register(email, password, username)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (state.isLoading && isLoadingLogin) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        text = if (selected == 0) "Accedi" else "Registrati",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                "- oppure -",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedButton(
                onClick = {
                    if (state.isLoading) return@OutlinedButton
                    isLoadingGoogle = true
                    isLoadingLogin = false
                    actions.loginWithGoogle(googleSignIn)
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color.Gray)
            ) {
                if (state.isLoading && isLoadingGoogle) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.Gray,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        painterResource(R.drawable.google),
                        contentDescription = "Google",
                        modifier = Modifier.size(24.dp),
                        tint = Color.Unspecified
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = "Accedi con Google",
                        color = Color.Black,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}