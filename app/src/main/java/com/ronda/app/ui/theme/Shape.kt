package com.ronda.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * DESIGN.md §5. Nothing is square: the minimum radius anywhere is 8dp.
 *
 * Material roles: small = badge, medium = button and field, large = card,
 * extraLarge = bottom sheet (top corners only, applied by the sheet).
 */
val Shapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

object RondaRadius {
    val badge = 8.dp
    val iconBoxSmall = 12.dp
    val button = 16.dp
    val iconBoxLarge = 24.dp
    val card = 20.dp
    val sheet = 28.dp
}

/** DESIGN.md §6: the solid drop under a card and under a button. */
object RondaDepth {
    val card = 4.dp
    val button = 5.dp
    val border = 2.5.dp
}
