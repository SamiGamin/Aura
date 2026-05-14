package com.stokia.aura.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Aura Dark Color Scheme — Cyberpunk Glassmorphism
 * Pure black backgrounds, cyan primary, magenta accents.
 */
private val AuraDarkColorScheme = darkColorScheme(
    primary = AuraCyan,
    onPrimary = AuraBlack,
    primaryContainer = AuraCyanDark,
    onPrimaryContainer = AuraTextPrimary,
    secondary = AuraViolet,
    onSecondary = AuraBlack,
    tertiary = AuraMagenta,
    onTertiary = AuraBlack,
    background = AuraBlack,
    onBackground = AuraTextPrimary,
    surface = AuraDeepBlack,
    onSurface = AuraTextPrimary,
    surfaceVariant = AuraCardSurface,
    onSurfaceVariant = AuraTextSecondary,
    error = AuraError,
    onError = AuraBlack,
    outline = AuraGlassBorder,
    outlineVariant = AuraGlassBorderAccent
)

@Composable
fun AuraTheme(content: @Composable () -> Unit) {
    val colorScheme = AuraDarkColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            // enableEdgeToEdge() in MainActivity handles transparent bars.
            // Here we only ensure dark icon style for our dark theme.
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AuraTypography,
        content = content
    )
}