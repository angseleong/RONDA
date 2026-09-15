package com.ronda.app.ui.theme

import androidx.compose.ui.graphics.Color
import com.ronda.app.core.RiskLevel

/**
 * A night watchman, not an alarm panel.
 *
 * Dark only, no dynamic color: the score colour *is* the information, and a
 * wallpaper-derived palette would repaint it differently on every phone.
 * Red ([siren]) appears on at most one element per screen — spend it on a
 * DARURAT score and nothing else, or it stops meaning anything.
 */
val night = Color(0xFF12161F)
val nightRaised = Color(0xFF1B2130)
val lamp = Color(0xFFE8A33D)
val linen = Color(0xFFEDE7DC)
val linenDim = Color(0xFF8C93A3)
val siren = Color(0xFFD2453B)
val calm = Color(0xFF5B9A78)

/** Hairline between regions — under the tabs, above a footnote. Never a card edge. */
val seam = linenDim.copy(alpha = 0.24f)

/**
 * A colour's wash: the 14% tint that carries its meaning onto a surface
 * without spending the colour itself. The score badge, the override notice
 * and the setup banner all sit on a wash, which is what keeps the solid
 * colour rare enough to mean something.
 */
fun wash(color: Color): Color = color.copy(alpha = 0.14f)

/** The band's colour, used for the score numeral and its list badge. */
fun scoreColor(level: RiskLevel): Color = when (level) {
    RiskLevel.AMAN -> calm
    RiskLevel.RENDAH -> linenDim
    RiskLevel.PERINGATAN -> lamp
    RiskLevel.DARURAT -> siren
}
