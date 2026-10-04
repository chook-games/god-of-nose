package com.godofnose.sneezeandthunder.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

// ─── Dark Color Scheme (Superhero Theme) ─────────────────────────────────────

private val GodOfNoseDarkColorScheme = darkColorScheme(
    primary = Gold,
    onPrimary = DarkText,
    primaryContainer = GoldDark,
    onPrimaryContainer = White,
    secondary = DeepBlueLight,
    onSecondary = White,
    secondaryContainer = DeepBlue,
    onSecondaryContainer = LightText,
    tertiary = StormPurple,
    onTertiary = White,
    tertiaryContainer = StormPurpleLight,
    onTertiaryContainer = DarkText,
    background = DarkSurface,
    onBackground = LightText,
    surface = DarkSurface,
    onSurface = LightText,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = OffWhite,
    outline = Gold.copy(alpha = 0.3f)
)

// ─── Theme Composable ────────────────────────────────────────────────────────

@Composable
fun GodOfNoseTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = GodOfNoseDarkColorScheme

    // Set status bar to transparent with light icons
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = GodOfNoseTypography,
        content = content
    )
}
