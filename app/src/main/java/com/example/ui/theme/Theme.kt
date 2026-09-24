package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// 1. GREEN THEME (Current Theme - Named "Green" per user request)
val GreenColorScheme = darkColorScheme(
    primary = BodhiGreenDark,
    onPrimary = Color(0xFF003915),
    primaryContainer = Color(0xFF144D29),
    onPrimaryContainer = Color(0xFFA5D6A7),
    secondary = BodhiGoldDark,
    onSecondary = Color(0xFF422100),
    secondaryContainer = Color(0xFF613300),
    onSecondaryContainer = Color(0xFFFFDDB8),
    tertiary = BodhiLotusDark,
    background = BodhiForestBgDark,
    onBackground = BodhiTextPrimaryDark,
    surface = BodhiSurfaceDark,
    onSurface = BodhiTextPrimaryDark,
    surfaceVariant = BodhiSurfaceVariantDark,
    onSurfaceVariant = BodhiTextSecondaryDark
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
    themeMode: AppThemeMode = AppThemeMode.GREEN,
    content: @Composable () -> Unit,
) {
    val colorScheme = when (themeMode) {
        AppThemeMode.GREEN -> GreenColorScheme
        AppThemeMode.LIGHT -> LightColorScheme
        AppThemeMode.AMOLED -> AmoledColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
