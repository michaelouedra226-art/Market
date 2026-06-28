package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ElectricBlue,
    secondary = SkyBlue,
    tertiary = PremiumViolet,
    background = DarkBg,
    surface = DarkCard,
    onPrimary = Color.White,
    onSecondary = DarkTextPrimary,
    onTertiary = Color.White,
    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary,
    error = PromoRed
)

private val LightColorScheme = lightColorScheme(
    primary = ElectricBlue,
    secondary = SkyBlue,
    tertiary = PremiumViolet,
    background = LightBg,
    surface = LightCard,
    onPrimary = Color.White,
    onSecondary = TextPrimary,
    onTertiary = Color.White,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    error = PromoRed
)

@Composable
fun PulseMarketTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
