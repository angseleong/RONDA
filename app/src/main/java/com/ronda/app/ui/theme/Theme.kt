package com.ronda.app.ui.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Dark only, and deliberately not dynamic. Score colour carries meaning here —
 * letting the OS recolour it from the wallpaper would make DARURAT look
 * different on every phone.
 *
 * Every Material role a component might reach for is mapped, including the
 * container and surface-container roles. Left unset they fall back to
 * Material's baseline purple, which is how a "primaryContainer" card ends up
 * violet on a night-blue page.
 */
private val RondaColors = darkColorScheme(
    primary = lamp,
    onPrimary = night,
    primaryContainer = wash(lamp),
    onPrimaryContainer = linen,
    inversePrimary = lamp,
    secondary = linenDim,
    onSecondary = night,
    secondaryContainer = nightRaised,
    onSecondaryContainer = linen,
    tertiary = calm,
    onTertiary = night,
    tertiaryContainer = wash(calm),
    onTertiaryContainer = linen,
    background = night,
    onBackground = linen,
    surface = night,
    onSurface = linen,
    surfaceVariant = nightRaised,
    onSurfaceVariant = linenDim,
    surfaceContainerLowest = night,
    surfaceContainerLow = nightRaised,
    surfaceContainer = nightRaised,
    surfaceContainerHigh = nightRaised,
    surfaceContainerHighest = nightRaised,
    inverseSurface = linen,
    inverseOnSurface = night,
    error = siren,
    onError = linen,
    errorContainer = wash(siren),
    onErrorContainer = linen,
    outline = linenDim,
    outlineVariant = seam
)

@Composable
fun RONDATheme(content: @Composable () -> Unit) {
    // The app is dark whatever the phone's own setting is, so the status and
    // navigation bar icons have to be told to stay light. Edge-to-edge picks
    // them from the system theme, which on a light-mode phone means dark
    // icons over a night background.
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = view.context.findActivity()?.window ?: return@SideEffect
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = RondaColors,
        typography = Typography,
        content = content
    )
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
