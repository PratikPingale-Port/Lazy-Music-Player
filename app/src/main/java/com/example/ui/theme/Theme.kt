package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val ResonanceDarkColorScheme = darkColorScheme(
    primary = CyanNeon,
    onPrimary = ObsidianBlack,
    primaryContainer = SurfaceElevatedDark,
    onPrimaryContainer = CyanNeon,
    secondary = AmberGlow,
    onSecondary = ObsidianBlack,
    secondaryContainer = SurfaceVariantDark,
    onSecondaryContainer = AmberGlow,
    tertiary = CyanNeonDim,
    background = ObsidianBlack,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondary,
    outline = DividerDark,
    error = ErrorRed,
    onError = Color.White
)

private val ResonanceLightColorScheme = lightColorScheme(
    primary = Color(0xFF007A8A),
    onPrimary = Color.White,
    secondary = Color(0xFFC67D00),
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color.White,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF475569)
)

@Composable
fun ResonanceTheme(
    darkTheme: Boolean = true, // Default to audiophile dark theme
    dynamicColor: Boolean = false, // Keep high contrast brand colors by default
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> ResonanceDarkColorScheme
        else -> ResonanceLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Backward compatibility alias
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    ResonanceTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
