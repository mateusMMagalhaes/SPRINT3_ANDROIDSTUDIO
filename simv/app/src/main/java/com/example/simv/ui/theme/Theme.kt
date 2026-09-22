package com.example.simv.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = SimvCyan,
    onPrimary = SimvDarkBg,
    secondary = SimvCyan,
    onSecondary = SimvDarkBg,
    background = SimvDarkBg,
    onBackground = SimvTextPrimary,
    surface = SimvCardBg,
    onSurface = SimvTextPrimary,
    surfaceVariant = SimvInputFieldBg,
    onSurfaceVariant = SimvTextSecondary,
    outline = SimvBorder
)

@Composable
fun SimvTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = SimvDarkBg.toArgb()
            window.navigationBarColor = SimvDarkBg.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}