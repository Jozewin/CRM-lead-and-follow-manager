package com.techpuram.leadandfollowmanagement.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF90CAF9),      // Light Blue
    secondary = Color(0xFFFFE082),     // Light Yellow
    tertiary = Color(0xFFA5D6A7),      // Light Green
    error = Color(0xFFEF9A9A),         // Light Red
    
    // Surface colors
    surface = Color(0xFF1C1B1F),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),
    
    // Background
    background = Color(0xFF1C1B1F),
    onBackground = Color.White,
    
    // Primary colors
    onPrimary = Color(0xFF1C1B1F),
    primaryContainer = Color(0xFF1976D2),
    onPrimaryContainer = Color.White,
    
    // Secondary colors
    onSecondary = Color(0xFF1C1B1F),
    secondaryContainer = Color(0xFFFFA000),
    onSecondaryContainer = Color.White,
    
    // Tertiary colors
    onTertiary = Color(0xFF1C1B1F),
    tertiaryContainer = Color(0xFF2E7D32),
    onTertiaryContainer = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1976D2),      // Blue from logo
    secondary = Color(0xFFFFC107),     // Yellow from logo
    tertiary = Color(0xFF4CAF50),      // Green from logo
    error = Color(0xFFE53935),         // Red from logo
    
    // Surface colors
    surface = Color.White,
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = Color(0xFF49454F),
    
    // Background
    background = Color.White,
    onBackground = Color(0xFF1C1B1F),
    
    // Primary colors
    onPrimary = Color.White,
    primaryContainer = Color(0xFF1976D2),
    onPrimaryContainer = Color(0xFF1976D2),
    
    // Secondary colors
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFFFFECB3),
    onSecondaryContainer = Color(0xFFFFA000),
    
    // Tertiary colors
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFC8E6C9),
    onTertiaryContainer = Color(0xFF2E7D32)
)

@Composable
fun LeadAndFollowManagementTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> DarkColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}