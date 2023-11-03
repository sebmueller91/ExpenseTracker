package com.example.expensetracker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

val LightColorPalette = lightColors(
    primary = Orange700,
    primaryVariant = Orange900,
    secondary = Orange200,
    secondaryVariant = Orange400,
    background = LightGray,
    surface = White,
    error = Red800,
    onPrimary = White,
    onSecondary = AlmostBlack,
    onBackground = AlmostBlack,
    onSurface = AlmostBlack,
    onError = White
)

val DarkColorPalette = darkColors(
    primary = Orange700,
    primaryVariant = Orange900,
    secondary = Orange200,
    secondaryVariant = Orange400,
    background = DarkGray,
    surface = DarkSurface,
    error = ErrorDark,
    onPrimary = AlmostBlack,
    onSecondary = White,
    onBackground = LightText,
    onSurface = LightText,
    onError = White
)

@Composable
fun ExpenseTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}