package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = BuyOneDarkPrimary,
    onPrimary = BuyOneDarkOnPrimary,
    primaryContainer = BuyOneGreenPrimary,
    onPrimaryContainer = BuyOneMintContainer,
    secondary = BuyOneDarkSecondary,
    onSecondary = Color(0xFF332000),
    secondaryContainer = Color(0xFF523604),
    onSecondaryContainer = BuyOneGoldContainer,
    tertiary = Color(0xFF38BDF8),
    background = BuyOneDarkBg,
    onBackground = Color(0xFFECFDF5),
    surface = BuyOneDarkSurface,
    onSurface = Color(0xFFECFDF5),
    surfaceVariant = BuyOneDarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFA7F3D0),
    error = Color(0xFFF87171),
    outline = Color(0xFF2D5441)
)

private val LightColorScheme = lightColorScheme(
    primary = BuyOneGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = BuyOneMintContainer,
    onPrimaryContainer = BuyOneOnMintContainer,
    secondary = BuyOneGoldAccent,
    onSecondary = Color.White,
    secondaryContainer = BuyOneGoldContainer,
    onSecondaryContainer = BuyOneOnGoldContainer,
    tertiary = BuyOneGreenLight,
    onTertiary = Color.White,
    background = BuyOneCreamBg,
    onBackground = BuyOneTextPrimary,
    surface = BuyOneSurfaceWhite,
    onSurface = BuyOneTextPrimary,
    surfaceVariant = BuyOneSurfaceVariant,
    onSurfaceVariant = BuyOneTextSecondary,
    error = BuyOneCrimson,
    errorContainer = BuyOneCrimsonContainer,
    onErrorContainer = Color(0xFF7F1D1D),
    outline = BuyOneOutline
)

@Composable
fun BuyOneBDTheme(
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

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    BuyOneBDTheme(darkTheme = darkTheme, content = content)
}
