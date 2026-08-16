package com.ronda.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/**
 * Light only, and deliberately not dynamic.
 *
 * Dynamic colour would repaint the score bands from the user's wallpaper, and a
 * DARURAT that comes out lilac on one phone is no longer a signal. Forcing light
 * is the same argument: the guardian and the protected user must be able to
 * describe what they are seeing to each other over a phone call.
 *
 * **Every role is assigned on purpose.** The previous scheme set nine of them and
 * left the rest to the Material baseline, so `primaryContainer` and
 * `errorContainer` — which the setup and uninstall screens both use — rendered in
 * default Material purple against a near-black background. Anything left unset
 * here reappears as that same stray purple, so add roles, never remove them.
 */
private val RondaColors = lightColorScheme(
    primary = teal,
    onPrimary = card,
    primaryContainer = tealSoft,
    onPrimaryContainer = tealInk,
    inversePrimary = tealOnDark,

    secondary = bandLow,
    onSecondary = card,
    secondaryContainer = bandLowTint,
    onSecondaryContainer = slateInk,

    // Tertiary is the caution role: "connected but incomplete", the override
    // banner, PERINGATAN. It is the only place the old orange survives.
    tertiary = bandWarn,
    onTertiary = card,
    tertiaryContainer = bandWarnTint,
    onTertiaryContainer = amberInk,

    background = paper,
    onBackground = ink,

    surface = card,
    onSurface = ink,
    surfaceVariant = cardSunken,
    onSurfaceVariant = inkSoft,
    surfaceTint = teal,

    surfaceContainerLowest = card,
    surfaceContainerLow = paper,
    surfaceContainer = card,
    surfaceContainerHigh = cardSunken,
    surfaceContainerHighest = cardSunken,
    surfaceBright = card,
    surfaceDim = cardSunken,

    inverseSurface = ink,
    inverseOnSurface = paper,

    error = bandUrgent,
    onError = card,
    errorContainer = bandUrgentTint,
    onErrorContainer = redInk,

    outline = hairlineStrong,
    outlineVariant = hairline,
    scrim = scrim
)

@Composable
fun RONDATheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = RondaColors,
        typography = Typography,
        content = content
    )
}
