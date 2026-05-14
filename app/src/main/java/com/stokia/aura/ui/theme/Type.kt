package com.stokia.aura.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Using default sans-serif as base — replace with custom font when added to res/font
val AuraFontFamily = FontFamily.Default

val AuraTypography = Typography(
    // Hero / Splash titles
    displayLarge = TextStyle(
        fontFamily = AuraFontFamily,
        fontWeight = FontWeight.Light,
        fontSize = 48.sp,
        letterSpacing = (-1).sp,
        color = AuraTextPrimary
    ),
    // Screen titles
    headlineLarge = TextStyle(
        fontFamily = AuraFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        letterSpacing = (-0.5).sp,
        color = AuraTextPrimary
    ),
    headlineMedium = TextStyle(
        fontFamily = AuraFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        letterSpacing = 0.sp,
        color = AuraTextPrimary
    ),
    // Section titles
    titleLarge = TextStyle(
        fontFamily = AuraFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        letterSpacing = 0.sp,
        color = AuraTextPrimary
    ),
    titleMedium = TextStyle(
        fontFamily = AuraFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        letterSpacing = 0.15.sp,
        color = AuraTextPrimary
    ),
    // Body text
    bodyLarge = TextStyle(
        fontFamily = AuraFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        letterSpacing = 0.5.sp,
        color = AuraTextPrimary
    ),
    bodyMedium = TextStyle(
        fontFamily = AuraFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        letterSpacing = 0.25.sp,
        color = AuraTextSecondary
    ),
    bodySmall = TextStyle(
        fontFamily = AuraFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        letterSpacing = 0.4.sp,
        color = AuraTextMuted
    ),
    // Buttons
    labelLarge = TextStyle(
        fontFamily = AuraFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        letterSpacing = 1.sp,
        color = AuraBlack
    ),
    labelMedium = TextStyle(
        fontFamily = AuraFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        letterSpacing = 0.5.sp,
        color = AuraTextSecondary
    )
)