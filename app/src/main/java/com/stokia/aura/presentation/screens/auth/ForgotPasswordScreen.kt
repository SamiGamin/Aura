package com.stokia.aura.presentation.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.stokia.aura.domain.model.AuthState
import com.stokia.aura.ui.theme.AuraBlack
import com.stokia.aura.ui.theme.AuraCardSurface
import com.stokia.aura.ui.theme.AuraCyan
import com.stokia.aura.ui.theme.AuraError
import com.stokia.aura.ui.theme.AuraGlassBorder
import com.stokia.aura.ui.theme.AuraGreen
import com.stokia.aura.ui.theme.AuraTextMuted
import com.stokia.aura.ui.theme.AuraTextPrimary
import com.stokia.aura.ui.theme.AuraTextSecondary
import com.stokia.aura.ui.theme.AuraViolet
import kotlinx.coroutines.delay

/**
 * Forgot Password Screen — Sends password reset email.
 */
@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsState()
    val email by viewModel.email.collectAsState()
    val resetSent by viewModel.passwordResetSent.collectAsState()
    val focusManager = LocalFocusManager.current
    var showContent by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        showContent = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AuraBlack)
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Back button
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.Start)
                .padding(top = 16.dp)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = AuraTextPrimary
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(spring()) + slideInVertically(spring()) { it / 4 }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Recuperar acceso",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        brush = Brush.linearGradient(listOf(AuraCyan, AuraViolet)),
                        fontWeight = FontWeight.Light
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Te enviaremos un enlace para restablecer tu contraseña",
                    style = MaterialTheme.typography.bodyMedium.copy(color = AuraTextSecondary),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(40.dp))

                if (resetSent) {
                    // Success state
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(AuraCardSurface.copy(alpha = 0.6f))
                            .border(
                                width = 1.dp,
                                brush = Brush.linearGradient(listOf(AuraGreen.copy(alpha = 0.3f), Color.Transparent)),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "✓",
                            fontSize = 40.sp,
                            color = AuraGreen
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Email enviado",
                            style = MaterialTheme.typography.titleLarge.copy(color = AuraGreen)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Revisa tu bandeja de entrada y sigue el enlace para restablecer tu contraseña.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = AuraTextSecondary),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    // Input state
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(AuraCardSurface.copy(alpha = 0.6f))
                            .border(
                                width = 1.dp,
                                brush = Brush.linearGradient(listOf(AuraGlassBorder, Color.Transparent)),
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
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                    viewModel.sendPasswordReset()
                                }
                            )
                        )

                        Spacer(modifier = Modifier.height(24.dp))

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
                            text = "ENVIAR ENLACE",
                            isLoading = authState is AuthState.Loading,
                            onClick = { viewModel.sendPasswordReset() }
                        )
                    }
                }
            }
        }
    }
}
