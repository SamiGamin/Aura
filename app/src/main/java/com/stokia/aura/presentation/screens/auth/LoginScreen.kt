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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalContext
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
import com.stokia.aura.ui.theme.AuraTextMuted
import com.stokia.aura.ui.theme.AuraTextPrimary
import com.stokia.aura.ui.theme.AuraTextSecondary
import com.stokia.aura.ui.theme.AuraViolet
import kotlinx.coroutines.delay

/**
 * Login Screen — Cyberpunk Glassmorphism aesthetic.
 * Features: email/password login, Google sign-in, navigation to register.
 */
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsState()
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val focusManager = LocalFocusManager.current
    var passwordVisible by remember { mutableStateOf(false) }
    var showContent by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        showContent = true
    }

    // Navigate on successful auth
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> {
                navController.navigate(Routes.ContactsList.route) {
                    popUpTo(Routes.Login.route) { inclusive = true }
                }
            }
            is AuthState.NeedsProfile -> {
                navController.navigate(Routes.CreateProfile.route) {
                    popUpTo(Routes.Login.route) { inclusive = true }
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
                // Subtle ambient glow top-right
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(AuraCyan.copy(alpha = 0.06f), Color.Transparent),
                        center = Offset(size.width * 0.8f, size.height * 0.15f),
                        radius = size.minDimension * 0.5f
                    ),
                    radius = size.minDimension * 0.5f,
                    center = Offset(size.width * 0.8f, size.height * 0.15f)
                )
                // Subtle ambient glow bottom-left
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(AuraViolet.copy(alpha = 0.04f), Color.Transparent),
                        center = Offset(size.width * 0.2f, size.height * 0.85f),
                        radius = size.minDimension * 0.4f
                    ),
                    radius = size.minDimension * 0.4f,
                    center = Offset(size.width * 0.2f, size.height * 0.85f)
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
                    .padding(top = 80.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // --- Header ---
                Text(
                    text = "AURA",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 40.sp,
                        fontWeight = FontWeight.ExtraLight,
                        letterSpacing = 10.sp,
                        brush = Brush.linearGradient(listOf(AuraCyan, AuraViolet))
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Iniciar sesión",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = AuraTextSecondary,
                        fontWeight = FontWeight.Light
                    )
                )

                Spacer(modifier = Modifier.height(48.dp))

                // --- Glass Card ---
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
                    // Email field
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

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password field
                    AuraTextField(
                        value = password,
                        onValueChange = { viewModel.onPasswordChange(it) },
                        label = "Contraseña",
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
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                viewModel.login()
                            }
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Forgot password
                    Text(
                        text = "¿Olvidaste tu contraseña?",
                        style = MaterialTheme.typography.bodySmall.copy(color = AuraCyan),
                        modifier = Modifier
                            .align(Alignment.End)
                            .clickable {
                                navController.navigate(Routes.ForgotPassword.route)
                            }
                            .padding(vertical = 4.dp)
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

                    // Login button
                    AuraPrimaryButton(
                        text = "INGRESAR",
                        isLoading = authState is AuthState.Loading,
                        onClick = { viewModel.login() }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- Divider ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(AuraGlassBorder)
                    )
                    Text(
                        text = "  o  ",
                        style = MaterialTheme.typography.bodySmall.copy(color = AuraTextMuted)
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(AuraGlassBorder)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- Google Sign In ---
                val context = LocalContext.current
                AuraOutlinedButton(
                    text = "Continuar con Google",
                    onClick = {
                        viewModel.loginWithGoogle(context)
                    }
                )

                Spacer(modifier = Modifier.weight(1f))

                // --- Register link ---
                Row(
                    modifier = Modifier.padding(bottom = 32.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "¿No tienes cuenta? ",
                        style = MaterialTheme.typography.bodyMedium.copy(color = AuraTextSecondary)
                    )
                    Text(
                        text = "Regístrate",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = AuraCyan,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.clickable {
                            navController.navigate(Routes.Register.route)
                        }
                    )
                }
            }
        }
    }
}

// --- Reusable Aura Components ---

@Composable
fun AuraTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = AuraTextMuted) },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = AuraTextPrimary,
            unfocusedTextColor = AuraTextPrimary,
            cursorColor = AuraCyan,
            focusedBorderColor = AuraCyan,
            unfocusedBorderColor = AuraGlassBorder,
            focusedLabelColor = AuraCyan,
            unfocusedLabelColor = AuraTextMuted,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent
        ),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun AuraPrimaryButton(
    text: String,
    isLoading: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = !isLoading,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = AuraCyan,
            contentColor = AuraBlack,
            disabledContainerColor = AuraCyan.copy(alpha = 0.4f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = AuraBlack,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
fun AuraOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = AuraTextPrimary
        ),
        border = ButtonDefaults.outlinedButtonBorder(true),
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                color = AuraTextPrimary,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Medium
            )
        )
    }
}
