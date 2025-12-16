package com.sales.app.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Gold,
    secondary = NavyBlueLight,
    tertiary = GoldDark,
    background = DarkGrey,
    surface = NavyBlue,
    onPrimary = Color.Black,
    onSecondary = White,
    onBackground = LightGrey,
    onSurface = LightGrey,
    error = ErrorRed,
    
    // Fix for "Purple" default container
    primaryContainer = NavyBlueLight, 
    onPrimaryContainer = White,
    
    surfaceVariant = NavyBlueLight, // Card backgrounds in settings
    onSurfaceVariant = LightGrey
)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = White,
    primaryContainer = NavyBlueLight,
    onPrimaryContainer = White,
    
    secondary = Secondary,
    onSecondary = NavyBlue,
    secondaryContainer = Gold, // or a lighter gold if available
    onSecondaryContainer = NavyBlue,
    
    tertiary = GoldDark,
    onTertiary = White,
    tertiaryContainer = LightGrey,
    onTertiaryContainer = NavyBlue,
    
    background = LightGrey,
    onBackground = NavyBlue,
    
    surface = White,
    onSurface = NavyBlue,
    
    surfaceVariant = LightGrey,
    onSurfaceVariant = NavyBlueLight,
    
    error = ErrorRed,
    onError = White
)

@Composable
fun SalesAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Dynamic color is Android 12+ specific, disabling for KMP simplicity for now
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
