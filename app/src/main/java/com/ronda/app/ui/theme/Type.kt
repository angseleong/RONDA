@file:OptIn(ExperimentalTextApi::class)

package com.ronda.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.ronda.app.R

/**
 * One face for everything: Nunito, bundled as a single variable file so the
 * demo runs with no network and no font provider (DESIGN.md §3).
 *
 * The weights are heavy on purpose — 700 is the *body* weight in this system.
 * Rounded terminals at bold weights are what make the type read as friendly
 * rather than loud, and they hold up at 20sp on an older person's phone.
 */
private fun nunito(weight: FontWeight) = Font(
    R.font.nunito,
    weight,
    variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight))
)

val Nunito = FontFamily(
    nunito(FontWeight.Bold),
    nunito(FontWeight.ExtraBold),
    nunito(FontWeight.Black)
)

private fun style(
    size: TextUnit,
    line: TextUnit,
    weight: FontWeight,
    tracking: TextUnit = TextUnit.Unspecified
) = TextStyle(
    fontFamily = Nunito,
    fontWeight = weight,
    fontSize = size,
    lineHeight = line,
    letterSpacing = tracking,
    // Predictable line boxes: no font padding, glyphs centred in the line.
    // Buttons and badges are sized from these numbers.
    platformStyle = PlatformTextStyle(includeFontPadding = false),
    lineHeightStyle = LineHeightStyle(
        alignment = LineHeightStyle.Alignment.Center,
        trim = LineHeightStyle.Trim.None
    )
)

/**
 * DESIGN.md §3 mapped onto the Material roles, so a Material component that
 * reaches for `labelLarge` or `bodyMedium` lands in the system instead of Roboto.
 *
 * display → displayMedium · heading → headlineLarge · subheading → titleLarge
 * body → bodyLarge · body-sm → bodyMedium · caption → bodySmall
 * button → labelLarge · label → labelSmall (authored upper-case by the component)
 */
val Typography = Typography(
    displayLarge = style(32.sp, 38.sp, FontWeight.Black, (-0.02).em),
    displayMedium = style(28.sp, 34.sp, FontWeight.Black, (-0.02).em),
    displaySmall = style(24.sp, 30.sp, FontWeight.Black, (-0.01).em),

    headlineLarge = style(22.sp, 28.sp, FontWeight.Black, (-0.01).em),
    headlineMedium = style(20.sp, 26.sp, FontWeight.ExtraBold),
    headlineSmall = style(19.sp, 24.sp, FontWeight.ExtraBold),

    titleLarge = style(19.sp, 24.sp, FontWeight.ExtraBold),
    titleMedium = style(16.sp, 22.sp, FontWeight.ExtraBold),
    titleSmall = style(14.sp, 20.sp, FontWeight.ExtraBold),

    bodyLarge = style(15.sp, 22.sp, FontWeight.Bold),
    bodyMedium = style(13.sp, 18.sp, FontWeight.Bold),
    bodySmall = style(12.sp, 16.sp, FontWeight.Bold),

    labelLarge = style(16.sp, 20.sp, FontWeight.ExtraBold, 0.05.em),
    labelMedium = style(12.sp, 16.sp, FontWeight.ExtraBold, 0.02.em),
    labelSmall = style(10.sp, 14.sp, FontWeight.Black, 0.18.em)
)

/**
 * The large-print edition for screens the protected person reads. The PRD
 * floor for those screens is 20sp; 1.5 line height keeps long Indonesian
 * sentences from stacking into a wall.
 */
val LargePrint = style(20.sp, 30.sp, FontWeight.Bold)

/** Screen title on the same screens. */
val LargePrintTitle = style(30.sp, 36.sp, FontWeight.Black, (-0.02).em)

/** Buttons on the same screens: bigger than the guardian's 16sp, same voice. */
val LargePrintLabel = style(18.sp, 22.sp, FontWeight.ExtraBold, 0.04.em)

/**
 * The six-character pairing code, on both phones: wide tracking so it can be
 * read aloud one character at a time, tabular figures so it never jitters.
 */
val PairingCode = style(40.sp, 48.sp, FontWeight.Black, 8.sp)
    .copy(fontFeatureSettings = "tnum")

/** The risk score in the alert header. Tabular so 96 and 100 sit in one column. */
val ScoreNumeral = style(34.sp, 38.sp, FontWeight.Black, (-0.02).em)
    .copy(fontFeatureSettings = "tnum")

/** Figures in a row of data (times, vectors, maths): the body style, tabular. */
val Tabular = Typography.bodyMedium.copy(fontFeatureSettings = "tnum")
