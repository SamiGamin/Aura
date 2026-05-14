package com.stokia.aura.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Aura Cyberpunk Glassmorphism Color Palette
 *
 * Design philosophy: Pure blacks for OLED + neon accent highlights.
 * Surfaces use ultra-low opacity whites for glassmorphism depth.
 */

// --- Backgrounds (OLED optimized) ---
val AuraBlack = Color(0xFF000000)
val AuraDeepBlack = Color(0xFF0A0A0C)
val AuraDarkSurface = Color(0xFF0D0D10)
val AuraCardSurface = Color(0xFF111116)

// --- Neon Accents ---
val AuraCyan = Color(0xFF00F0FF)
val AuraCyanDark = Color(0xFF00B8C4)
val AuraCyanGlow = Color(0x3300F0FF) // 20% opacity for glows
val AuraMagenta = Color(0xFFFF003C)
val AuraMagentaGlow = Color(0x33FF003C)
val AuraViolet = Color(0xFF8B5CF6)
val AuraGreen = Color(0xFF00FF41)

// --- Text ---
val AuraTextPrimary = Color(0xFFE8E8EA)
val AuraTextSecondary = Color(0xFF8A8A94)
val AuraTextMuted = Color(0xFF4A4A54)

// --- Glass surfaces ---
val AuraGlassWhite = Color(0x0DFFFFFF) // 5% white
val AuraGlassBorder = Color(0x1AFFFFFF) // 10% white
val AuraGlassBorderAccent = Color(0x3300F0FF) // 20% cyan border

// --- Status ---
val AuraOnline = Color(0xFF00FF41)
val AuraOffline = Color(0xFF4A4A54)
val AuraError = Color(0xFFFF4444)
val AuraWarning = Color(0xFFFFAA00)

// --- Gradients helper (use with Brush.linearGradient) ---
val AuraCyanGradient = listOf(AuraCyan, AuraViolet)
val AuraDarkGradient = listOf(AuraDeepBlack, AuraDarkSurface)