package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// 1. BROWN THEME (Sacred Sandalwood, Aged Teak & Monastery Earth)
val BrownColorScheme = darkColorScheme(
    primary = BodhiBrownPrimary, // Warm radiant sandalwood / golden copper amber
    onPrimary = Color(0xFF2C1605),
    primaryContainer = Color(0xFF4A2D1A), // Deep rich mahogany
    onPrimaryContainer = Color(0xFFFFDCC1),
    secondary = BodhiBrownSecondary, // Golden temple glow
    onSecondary = Color(0xFF3E2800),
    secondaryContainer = Color(0xFF5A3B00),
    onSecondaryContainer = Color(0xFFFFDF9E),
    tertiary = BodhiBrownTertiary, // Sacred terracotta lotus
    onTertiary = Color(0xFF441813),
    background = BodhiBrownBg, // Deep warm monastery teak & roasted sandalwood night
    onBackground = BodhiTextPrimaryBrown,
    surface = BodhiSurfaceBrown, // Aged teak wood surface
    onSurface = BodhiTextPrimaryBrown,
    surfaceVariant = BodhiSurfaceVariantBrown, // Warm cedar / sandalwood bark
    onSurfaceVariant = BodhiTextSecondaryBrown,
    outline = Color(0xFF6E5343)
)

// 2. LIGHT THEME
val LightColorScheme = lightColorScheme(
    primary = BodhiGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC8E6C9),
    onPrimaryContainer = Color(0xFF00210B),
    secondary = BodhiGoldSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFF3CD),
    onSecondaryContainer = Color(0xFF432200),
    tertiary = BodhiLotusPink,
    background = BodhiIvoryBg,
    onBackground = BodhiTextPrimaryLight,
    surface = BodhiSurfaceLight,
    onSurface = BodhiTextPrimaryLight,
    surfaceVariant = BodhiSurfaceVariantLight,
    onSurfaceVariant = BodhiTextSecondaryLight
)

// 3. AMOLED THEME (Pure pitch black #000000 with glowing copper/rose-gold highlights)
val AmoledColorScheme = darkColorScheme(
    primary = Color(0xFFD48B69), // Luminous copper
    onPrimary = Color(0xFF261009),
    primaryContainer = Color(0xFF381B12),
    onPrimaryContainer = Color(0xFFFFCCBC),
    secondary = Color(0xFFFFD54F), // Golden glow
    onSecondary = Color(0xFF3E2723),
    secondaryContainer = Color(0xFF332600),
    onSecondaryContainer = Color(0xFFFFE082),
    tertiary = Color(0xFF81C784),
    background = Color(0xFF000000), // Pure AMOLED black
    onBackground = Color(0xFFF5F5F5),
    surface = Color(0xFF0A0A0A),
    onSurface = Color(0xFFF5F5F5),
    surfaceVariant = Color(0xFF161616),
    onSurfaceVariant = Color(0xFFB0B0B0),
    outline = Color(0xFF333333)
)

@Composable
fun MyApplicationTheme(
    themeMode: AppThemeMode = AppThemeMode.BROWN,
    content: @Composable () -> Unit,
) {
    val colorScheme = when (themeMode) {
        AppThemeMode.BROWN -> BrownColorScheme
        AppThemeMode.LIGHT -> LightColorScheme
        AppThemeMode.AMOLED -> AmoledColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
