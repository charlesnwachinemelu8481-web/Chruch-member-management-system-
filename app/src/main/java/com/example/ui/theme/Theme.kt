package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val FaithConnectLightColorScheme = lightColorScheme(
    primary = BurgundyWine,
    onPrimary = Color.White,
    primaryContainer = BurgundyContainer,
    onPrimaryContainer = OnBurgundyContainer,
    secondary = NavyPrimary,
    onSecondary = Color.White,
    secondaryContainer = NavyContainer,
    onSecondaryContainer = OnNavyContainer,
    tertiary = GoldAccent,
    onTertiary = Color.White,
    tertiaryContainer = GoldContainer,
    onTertiaryContainer = GoldDark,
    background = WarmBackground,
    onBackground = TextCharcoal,
    surface = WarmSurface,
    onSurface = TextCharcoal,
    surfaceVariant = WarmSurfaceVariant,
    onSurfaceVariant = TextMuted,
    outline = BorderOutline,
    outlineVariant = BorderSubtle,
)

private val FaithConnectDarkColorScheme = darkColorScheme(
    primary = Color(0xFFF399AE),
    onPrimary = BurgundyDark,
    primaryContainer = BurgundyDeep,
    onPrimaryContainer = Color(0xFFFFD9E2),
    secondary = Color(0xFFA1C9FD),
    onSecondary = Color(0xFF072648),
    secondaryContainer = NavyPrimary,
    onSecondaryContainer = Color(0xFFD6E4F7),
    tertiary = Color(0xFFF6CE65),
    onTertiary = Color(0xFF453000),
    tertiaryContainer = GoldDark,
    onTertiaryContainer = GoldLight,
    background = Color(0xFF1B1516),
    onBackground = Color(0xFFECE0E2),
    surface = Color(0xFF231C1E),
    onSurface = Color(0xFFECE0E2),
    surfaceVariant = Color(0xFF332A2C),
    onSurfaceVariant = Color(0xFFCCC0C3),
    outline = Color(0xFF5A4D50),
    outlineVariant = Color(0xFF3B3234)
)

@Composable
fun FaithConnectTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) FaithConnectDarkColorScheme else FaithConnectLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
