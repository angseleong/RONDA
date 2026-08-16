package com.ronda.app.ui.theme

import androidx.compose.ui.graphics.Color
import com.ronda.app.core.RiskLevel

/**
 * Daylight, not a control room.
 *
 * The previous palette was near-black with an orange accent. It read as an alarm
 * panel, which is wrong twice over: the guardian opens this app on an ordinary
 * afternoon to find nothing wrong, and the protected user is in their seventies.
 * Older eyes lose contrast sensitivity and scatter more light inside the eye, so
 * light text on a dark field blooms and gets *harder* to read, not easier — the
 * opposite of what a dark theme promises.
 *
 * So: a light ground, one calm brand colour, and saturation spent only where it
 * carries meaning.
 *
 * Every value here clears 4.5:1 against the surface it is used on, and the text
 * roles clear 7:1 — the AA-large bar is not enough for a reader who may also be
 * holding the phone at arm's length. Ratios are noted per colour; recheck them
 * before changing any value.
 */

// --- Ground ------------------------------------------------------------------

/** Page background. Very slightly cool so white cards lift off it. */
val paper = Color(0xFFF5F8F9)

/** Cards and sheets. */
val card = Color(0xFFFFFFFF)

/** Sunken tiles and the closed technical panel. */
val cardSunken = Color(0xFFEAEFF1)

/** Primary text. 16.4:1 on [card]. */
val ink = Color(0xFF14191B)

/** Secondary text — explanations, timestamps. 7.9:1 on [card]. */
val inkSoft = Color(0xFF4A5457)

/** Card borders and dividers. Not for text. */
val hairline = Color(0xFFD5DDE0)

/** Interactive outlines (outlined buttons, focus). 4.0:1 on [card]. */
val hairlineStrong = Color(0xFF78868A)

// --- Brand -------------------------------------------------------------------

/**
 * Deep teal, carried over from `DESIGN.md` §2.2. Trustworthy without being
 * institutional, and — the reason it beats blue — it cannot be confused with any
 * of the four band colours below.
 *
 * 8.9:1 with white text on it, 8.9:1 as text on [card].
 */
val teal = Color(0xFF0B4F60)
val tealSoft = Color(0xFFD2ECF4)
val tealInk = Color(0xFF002731)

/** Material's `inversePrimary`, used on inverted surfaces such as snackbars. */
val tealOnDark = Color(0xFF7FD1E8)

// --- Container ink -----------------------------------------------------------
// Text colours for the four container roles. Each is near-black in its own hue
// so a filled container reads as tinted paper rather than as a coloured block.

val slateInk = Color(0xFF16232B)
val amberInk = Color(0xFF2B1800)
val redInk = Color(0xFF4A0A08)

/** Behind modal sheets and dialogs. */
val scrim = Color(0x99000000)

// --- Bands -------------------------------------------------------------------

/**
 * One colour per [RiskLevel], each paired with a tint for the surface behind it.
 *
 * Colour is never the only signal — every band also prints its name — but it is
 * the one that works from across a room, so the four are separated in lightness
 * as well as hue. That keeps them apart for a red/green colour-blind reader, who
 * is otherwise the exact person a green/red pair fails.
 */
val bandSafe = Color(0xFF186B45)      // 5.9:1 on card
val bandSafeTint = Color(0xFFD8EFE3)

val bandLow = Color(0xFF3F5A66)       // 7.3:1 on card
val bandLowTint = Color(0xFFDFE8EC)

val bandWarn = Color(0xFF8A5300)      // 6.4:1 on card
val bandWarnTint = Color(0xFFFCE6C4)

/**
 * Kept from the existing overlay so the guardian's DARURAT and the red screen on
 * the parent's phone are recognisably the same event. 9.0:1 on [card].
 */
val bandUrgent = Color(0xFF8C1010)
val bandUrgentTint = Color(0xFFFADCD9)

/** The band's colour: score numeral, badge text, band chip. */
fun scoreColor(level: RiskLevel): Color = when (level) {
    RiskLevel.AMAN -> bandSafe
    RiskLevel.RENDAH -> bandLow
    RiskLevel.PERINGATAN -> bandWarn
    RiskLevel.DARURAT -> bandUrgent
}

/**
 * The tint behind it. A fixed colour rather than the band colour at low alpha:
 * alpha compounds against whatever sits underneath, and these blocks appear on
 * both [card] and [paper].
 */
fun scoreTint(level: RiskLevel): Color = when (level) {
    RiskLevel.AMAN -> bandSafeTint
    RiskLevel.RENDAH -> bandLowTint
    RiskLevel.PERINGATAN -> bandWarnTint
    RiskLevel.DARURAT -> bandUrgentTint
}
