@file:OptIn(ExperimentalTextApi::class)

package com.ronda.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.ronda.app.R

/**
 * Bundled variable fonts rather than Google's downloadable-font provider.
 *
 * The spec asked for `ui-text-google-fonts`, but that resolves fonts over the
 * network through Play Services on first launch, and silently falls back to
 * Roboto when either is missing. The whole point of the demo path is that it
 * runs offline, so the fonts ship in the APK — one variable file per family,
 * every weight, no provider and no certificate array to get wrong.
 */
private fun archivo(weight: FontWeight) = Font(
    R.font.archivo,
    weight,
    variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight))
)

private fun inter(weight: FontWeight) = Font(
    R.font.inter,
    weight,
    variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight))
)

/** Display face: app name, verdict words, and the score numeral. */
val Archivo = FontFamily(archivo(FontWeight.SemiBold), archivo(FontWeight.Bold))

/** Reading face. The explanation sentences are the decision material. */
val Inter = FontFamily(inter(FontWeight.Normal), inter(FontWeight.Medium))

private fun display(
    size: TextUnit,
    line: TextUnit,
    weight: FontWeight = FontWeight.SemiBold
) = TextStyle(fontFamily = Archivo, fontWeight = weight, fontSize = size, lineHeight = line)

private fun reading(
    size: TextUnit,
    line: TextUnit,
    weight: FontWeight = FontWeight.Normal,
    tracking: TextUnit = TextUnit.Unspecified
) = TextStyle(
    fontFamily = Inter,
    fontWeight = weight,
    fontSize = size,
    lineHeight = line,
    letterSpacing = tracking
)

/**
 * Tabular figures so a 96 and a 100 occupy the same width — scores sit in a
 * column down the watch list and must not jitter as they change.
 */
val ScoreNumeral = display(72.sp, 76.sp, FontWeight.Bold).copy(fontFeatureSettings = "tnum")

val ScoreBadgeNumeral = display(28.sp, 32.sp, FontWeight.Bold).copy(fontFeatureSettings = "tnum")

/**
 * The six-character pairing code, on both phones. Wide tracking so it can be
 * read aloud one character at a time; the same face on the guardian's screen
 * and in the protected phone's field, so what is typed looks like what is
 * printed.
 */
val PairingCode = display(44.sp, 52.sp, FontWeight.Bold).copy(
    letterSpacing = 8.sp,
    fontFeatureSettings = "tnum"
)

/** Small caps by convention, not by feature: these strings are authored upper. */
val Eyebrow = reading(12.sp, 16.sp, FontWeight.Medium, tracking = 1.2.sp)

/**
 * The large-print edition. Every sentence on a screen the protected person
 * reads — role choice, setup, the pairing field, the uninstall prompt — is set
 * in this. The PRD floor for those screens is 20sp, and 1.5 line height keeps
 * the long Indonesian sentences from stacking.
 */
val LargePrint = reading(20.sp, 30.sp)

/** Buttons and status words on the same screens: one weight up, tighter line. */
val LargePrintLabel = reading(20.sp, 24.sp, FontWeight.Medium)

/**
 * The whole Material scale, so no component falls back to Roboto: buttons take
 * labelLarge, tabs take titleSmall, text-field support takes bodySmall.
 * Display and headline roles are Archivo; title-medium down is Inter.
 */
val Typography = Typography(
    displayLarge = display(36.sp, 42.sp, FontWeight.Bold),
    displayMedium = display(28.sp, 34.sp),
    displaySmall = display(26.sp, 32.sp),
    headlineLarge = display(24.sp, 30.sp),
    headlineMedium = display(22.sp, 28.sp),
    headlineSmall = display(20.sp, 26.sp),
    titleLarge = display(20.sp, 26.sp),
    titleMedium = reading(16.sp, 24.sp, FontWeight.Medium),
    titleSmall = reading(14.sp, 20.sp, FontWeight.Medium),
    // 1.5 line height. These sentences are never truncated, so they need room.
    bodyLarge = reading(16.sp, 24.sp),
    bodyMedium = reading(14.sp, 21.sp),
    bodySmall = reading(12.sp, 16.sp),
    labelLarge = reading(16.sp, 20.sp, FontWeight.Medium),
    labelMedium = reading(13.sp, 16.sp, FontWeight.Medium),
    labelSmall = reading(11.sp, 16.sp, FontWeight.Medium, tracking = 0.4.sp)
)
