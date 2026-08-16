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

/**
 * Sized up one step across the board from the first draft.
 *
 * The guardian is usually the younger of the two, but they read this screen in
 * the ninety seconds between a notification and a decision, often while walking.
 * The protected side is read by someone in their seventies. Neither case is
 * served by 14sp, and the screens have room — there was never more than one card
 * competing for the fold.
 *
 * `bodyMedium` at 15sp is the floor. Anything smaller in this app is a mistake;
 * `Eyebrow` at 12sp is the single exception and it only ever carries a label that
 * the sentence beneath it repeats in full.
 */
val Typography = Typography(
    displayMedium = TextStyle(
        fontFamily = Archivo,
        fontWeight = FontWeight.SemiBold,
        fontSize = 30.sp,
        lineHeight = 36.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = Archivo,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 30.sp
    ),
    titleLarge = TextStyle(
        fontFamily = Archivo,
        fontWeight = FontWeight.SemiBold,
        fontSize = 21.sp,
        lineHeight = 27.sp
    ),
    titleMedium = TextStyle(
        fontFamily = Archivo,
        fontWeight = FontWeight.SemiBold,
        fontSize = 17.sp,
        lineHeight = 23.sp
    ),
    // 1.45 line height. These sentences are never truncated, so they need room.
    bodyLarge = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 17.sp,
        lineHeight = 25.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp
    ),
    // Buttons. Material's default is 14sp, which is below this app's floor.
    labelLarge = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 22.sp
    )
)

/**
 * The protected phone's scale, applied on top of [Typography].
 *
 * `DESIGN.md` §3 sets a 20sp floor for every screen an older user reads without
 * a guardian present. That is too large for the guardian's dense watch list, so
 * it lives here as a separate set rather than as a global bump.
 */
val ProtectedTitle = TextStyle(
    fontFamily = Archivo,
    fontWeight = FontWeight.Bold,
    fontSize = 30.sp,
    lineHeight = 37.sp
)

val ProtectedSubtitle = TextStyle(
    fontFamily = Archivo,
    fontWeight = FontWeight.SemiBold,
    fontSize = 22.sp,
    lineHeight = 29.sp
)

val ProtectedBody = TextStyle(
    fontFamily = Inter,
    fontWeight = FontWeight.Normal,
    fontSize = 20.sp,
    lineHeight = 30.sp
)
