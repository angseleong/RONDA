package com.ronda.app.ui.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Light and dark, following the theme the user picked in Setelan. The choice is
 * applied through `AppCompatDelegate.setDefaultNightMode`, which is why this
 * theme only asks [isSystemInDarkTheme]: the delegate rewrites the Activity's
 * configuration, and every `-night` resource (the overlay, the window
 * background) follows the same switch.
 *
 * Not dynamic colour. The semantic colours carry meaning — green means guarded,
 * red means flagged — and a wallpaper-derived scheme would repaint them.
 */
object RondaTheme {
    val colors: RondaColors
        @Composable get() = LocalRondaColors.current
}

@Composable
fun RONDATheme(content: @Composable () -> Unit) {
    val dark = isSystemInDarkTheme()
    val colors = if (dark) DarkColors else LightColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = view.context.findActivity()?.window ?: return@SideEffect
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !dark
                isAppearanceLightNavigationBars = !dark
            }
        }
    }

    CompositionLocalProvider(LocalRondaColors provides colors) {
        MaterialTheme(
            colorScheme = colors.toMaterial(),
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}

/**
 * Every Material role a component might reach for, mapped. Left unset they fall
 * back to Material's baseline purple, which is how a text field's cursor ends
 * up violet on a white page.
 */
private fun RondaColors.toMaterial(): ColorScheme {
    val base = if (isDark) darkColorScheme() else lightColorScheme()
    return base.copy(
        primary = safe,
        onPrimary = onFill,
        primaryContainer = safeTint,
        onPrimaryContainer = textPrimary,
        inversePrimary = safe,
        secondary = trust,
        onSecondary = onFill,
        secondaryContainer = trustTint,
        onSecondaryContainer = trust,
        tertiary = warn,
        onTertiary = onFill,
        tertiaryContainer = warnTint,
        onTertiaryContainer = textPrimary,
        background = bg,
        onBackground = textPrimary,
        surface = bg,
        onSurface = textPrimary,
        surfaceVariant = surface,
        onSurfaceVariant = textSecondary,
        surfaceContainerLowest = bg,
        surfaceContainerLow = card,
        surfaceContainer = card,
        surfaceContainerHigh = card,
        surfaceContainerHighest = surface,
        surfaceTint = safe,
        inverseSurface = textPrimary,
        inverseOnSurface = bg,
        error = danger,
        onError = onFill,
        errorContainer = dangerTint,
        onErrorContainer = danger,
        outline = borderStrong,
        outlineVariant = border,
        scrim = textPrimary
    )
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
