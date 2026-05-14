package com.stokia.aura.presentation.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.stokia.aura.domain.model.AuthState
import com.stokia.aura.presentation.navigation.Routes
import com.stokia.aura.ui.theme.AuraBlack
import com.stokia.aura.ui.theme.AuraCyan
import com.stokia.aura.ui.theme.AuraCyanGlow
import com.stokia.aura.ui.theme.AuraTextMuted
import com.stokia.aura.ui.theme.AuraViolet
import kotlinx.coroutines.delay

/**
 * Splash Screen — Animated pulsing glow logo with auth state routing.
 */
@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsState()
    var showContent by remember { mutableStateOf(false) }

    // Animate entrance
    LaunchedEffect(Unit) {
        delay(300)
        showContent = true
    }

    // Navigate based on auth state after splash delay
    LaunchedEffect(authState) {
        if (authState is AuthState.Idle || authState is AuthState.Loading) return@LaunchedEffect
        delay(1800) // Minimum splash duration
        when (authState) {
            is AuthState.Authenticated -> {
                navController.navigate(Routes.ChatList.route) {
                    popUpTo(Routes.Splash.route) { inclusive = true }
                }
            }
            is AuthState.NeedsProfile -> {
                navController.navigate(Routes.CreateProfile.route) {
                    popUpTo(Routes.Splash.route) { inclusive = true }
                }
            }
            else -> {
                navController.navigate(Routes.Login.route) {
                    popUpTo(Routes.Splash.route) { inclusive = true }
                }
            }
        }
    }

    // Glow animation
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowPulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AuraBlack)
            .drawBehind {
                // Radial neon glow behind logo
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            AuraCyan.copy(alpha = glowAlpha * 0.15f),
                            Color.Transparent
                        ),
                        center = Offset(size.width / 2, size.height / 2),
                        radius = size.minDimension * 0.6f
                    ),
                    radius = size.minDimension * 0.6f,
                    center = Offset(size.width / 2, size.height / 2)
                )
            },
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(tween(800)),
            exit = fadeOut(tween(400))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Logo text with gradient
                Text(
                    text = "AURA",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 56.sp,
                        fontWeight = FontWeight.ExtraLight,
                        letterSpacing = 12.sp,
                        brush = Brush.linearGradient(
                            colors = listOf(AuraCyan, AuraViolet)
                        )
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "PRIVATE MESSENGER",
                    style = MaterialTheme.typography.bodySmall.copy(
                        letterSpacing = 6.sp,
                        color = AuraTextMuted
                    )
                )
            }
        }
    }
}
