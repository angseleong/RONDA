package com.ronda.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

/**
 * Dark only, and deliberately not dynamic. Score colour carries meaning here —
 * letting the OS recolour it from the wallpaper would make DARURAT look
 * different on every phone.
 */
private val RondaColors = darkColorScheme(
    primary = lamp,
    onPrimary = night,
    secondary = linenDim,
    onSecondary = night,
    background = night,
    onBackground = linen,
    surface = night,
    onSurface = linen,
    surfaceVariant = nightRaised,
    onSurfaceVariant = linenDim,
    error = siren,
    onError = linen,
    outline = linenDim
)

@Composable
fun RONDATheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = RondaColors,
        typography = Typography,
        content = content
    )
}
