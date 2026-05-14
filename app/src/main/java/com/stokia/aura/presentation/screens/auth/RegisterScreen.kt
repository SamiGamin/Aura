package com.stokia.aura.presentation.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.stokia.aura.domain.model.AuthState
import com.stokia.aura.presentation.navigation.Routes
import com.stokia.aura.ui.theme.AuraBlack
import com.stokia.aura.ui.theme.AuraCardSurface
import com.stokia.aura.ui.theme.AuraCyan
import com.stokia.aura.ui.theme.AuraError
import com.stokia.aura.ui.theme.AuraGlassBorder
import com.stokia.aura.ui.theme.AuraMagenta
import com.stokia.aura.ui.theme.AuraTextMuted
import com.stokia.aura.ui.theme.AuraTextSecondary
import com.stokia.aura.ui.theme.AuraViolet
import kotlinx.coroutines.delay

/**
 * Register Screen — Matching Cyberpunk Glassmorphism aesthetic.
 */
@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsState()
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val confirmPassword by viewModel.confirmPassword.collectAsState()
    val focusManager = LocalFocusManager.current
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var showContent by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        showContent = true
    }

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> {
                navController.navigate(Routes.ChatList.route) {
                    popUpTo(Routes.Register.route) { inclusive = true }
                }
            }
            is AuthState.NeedsProfile -> {
                navController.navigate(Routes.CreateProfile.route) {
                    popUpTo(Routes.Register.route) { inclusive = true }
                }
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AuraBlack)
            .drawBehind {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(AuraMagenta.copy(alpha = 0.05f), Color.Transparent),
                        center = Offset(size.width * 0.9f, size.height * 0.1f),
                        radius = size.minDimension * 0.4f
                    ),
                    radius = size.minDimension * 0.4f,
                    center = Offset(size.width * 0.9f, size.height * 0.1f)
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(AuraCyan.copy(alpha = 0.04f), Color.Transparent),
                        center = Offset(size.width * 0.1f, size.height * 0.9f),
                        radius = size.minDimension * 0.5f
                    ),
                    radius = size.minDimension * 0.5f,
                    center = Offset(size.width * 0.1f, size.height * 0.9f)
                )
            }
    ) {
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(spring()) + slideInVertically(spring()) { it / 4 }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 28.dp)
                    .padding(top = 60.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "AURA",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraLight,
                        letterSpacing = 10.sp,
                        brush = Brush.linearGradient(listOf(AuraCyan, AuraViolet))
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Crear cuenta",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = AuraTextSecondary,
                        fontWeight = FontWeight.Light
                    )
                )

                Spacer(modifier = Modifier.height(36.dp))

                // Glass Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(AuraCardSurface.copy(alpha = 0.6f))
                        .border(
                            width = 1.dp,
                            brush = Brush.linearGradient(
                                listOf(AuraGlassBorder, Color.Transparent)
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AuraTextField(
                        value = email,
                        onValueChange = { viewModel.onEmailChange(it) },
                        label = "Correo electrónico",
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = AuraTextMuted)
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    AuraTextField(
                        value = password,
                        onValueChange = { viewModel.onPasswordChange(it) },
                        label = "Contraseña (mín. 8 caracteres)",
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = AuraTextMuted)
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle password",
                                    tint = AuraTextMuted
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    AuraTextField(
                        value = confirmPassword,
                        onValueChange = { viewModel.onConfirmPasswordChange(it) },
                        label = "Confirmar contraseña",
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = AuraTextMuted)
                        },
                        trailingIcon = {
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(
                                    if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle password",
                                    tint = AuraTextMuted
                                )
                            }
                        },
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                viewModel.register()
                            }
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Error message
                    if (authState is AuthState.Error) {
                        Text(
                            text = (authState as AuthState.Error).message,
                            style = MaterialTheme.typography.bodySmall.copy(color = AuraError),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    AuraPrimaryButton(
                        text = "CREAR CUENTA",
                        isLoading = authState is AuthState.Loading,
                        onClick = { viewModel.register() }
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Login link
                Row(
                    modifier = Modifier.padding(bottom = 32.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "¿Ya tienes cuenta? ",
                        style = MaterialTheme.typography.bodyMedium.copy(color = AuraTextSecondary)
                    )
                    Text(
                        text = "Inicia sesión",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = AuraCyan,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.clickable {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}
