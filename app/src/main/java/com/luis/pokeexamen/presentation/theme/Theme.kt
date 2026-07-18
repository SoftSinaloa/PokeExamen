package com.luis.pokeexamen.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = PokeRed,
    onPrimary = Color.White,
    secondary = PokeGray,
    background = PokeGrayLight,
    surface = Color.White
)

private val DarkColors = darkColorScheme(
    primary = PokeRedLight,
    onPrimary = Color.Black,
    secondary = PokeGray,
    background = PokeGrayDark,
    surface = Color(0xFF1E1E1E)
)

@Composable
fun PokeExamenTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(colorScheme = colors, content = content)
}
