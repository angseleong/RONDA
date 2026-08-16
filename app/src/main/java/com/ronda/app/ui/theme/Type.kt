@file:OptIn(ExperimentalTextApi::class)

package com.ronda.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
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

/**
 * Tabular figures so a 96 and a 100 occupy the same width — scores sit in a
 * column down the watch list and must not jitter as they change.
 */
val ScoreNumeral = TextStyle(
    fontFamily = Archivo,
    fontWeight = FontWeight.Bold,
    fontSize = 72.sp,
    lineHeight = 76.sp,
    fontFeatureSettings = "tnum"
)

val ScoreBadgeNumeral = TextStyle(
    fontFamily = Archivo,
    fontWeight = FontWeight.Bold,
    fontSize = 28.sp,
    lineHeight = 32.sp,
    fontFeatureSettings = "tnum"
)

/** Small caps by convention, not by feature: these strings are authored upper. */
val Eyebrow = TextStyle(
    fontFamily = Inter,
    fontWeight = FontWeight.Medium,
    fontSize = 12.sp,
    lineHeight = 16.sp,
    letterSpacing = 1.2.sp
)

val Typography = Typography(
    displayMedium = TextStyle(
        fontFamily = Archivo,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 34.sp
    ),
    titleLarge = TextStyle(
        fontFamily = Archivo,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 26.sp
    ),
    // 1.5 line height. These sentences are never truncated, so they need room.
    bodyLarge = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 21.sp
    )
)
