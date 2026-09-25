package com.ronda.app.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.ronda.app.ui.theme.RondaDepth
import com.ronda.app.ui.theme.RondaRadius
import com.ronda.app.ui.theme.RondaTheme
import com.ronda.app.ui.theme.Tone

/**
 * The card (DESIGN.md §6.3): a 20dp surface with a 2.5dp border and a 4dp solid
 * drop in the same colour. A tone turns it into a state card — safe tint inside
 * a safe border — and the whole card animates between tones, which is how the
 * protected person sees a permission land without reading anything.
 *
 * With [onClick] the card presses like a button: the face travels onto its
 * drop. A row in a list is the same object as a button, only wider.
 */
@Composable
fun RondaCard(
    modifier: Modifier = Modifier,
    tone: Tone = Tone.NEUTRAL,
    onClick: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(20.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = RondaTheme.colors
    val shape = RoundedCornerShape(RondaRadius.card)
    val depth = RondaDepth.card

    val edge by animateColorAsState(
        targetValue = if (tone == Tone.NEUTRAL) colors.border else colors.border(tone),
        animationSpec = tween(260),
        label = "cardEdge"
    )
    val face by animateColorAsState(
        targetValue = if (tone == Tone.NEUTRAL) colors.card else colors.tint(tone),
        animationSpec = tween(260),
        label = "cardFace"
    )

    val interaction = remember { MutableInteractionSource() }
    val pressed = interaction.collectIsPressedVisibly()
    val lift by animateDpAsState(
        targetValue = if (pressed && onClick != null) 0.dp else depth,
        animationSpec = tween(durationMillis = 90, easing = FastOutSlowInEasing),
        label = "cardLift"
    )

    // A card is big, so it shrinks less than a button; a static card never presses.
    Box(modifier.pressScale(interaction, pressedScale = 0.98f)) {
        Box(
            Modifier
                .matchParentSize()
                .padding(top = depth)
                .background(edge, shape)
        )
        Column(
            modifier = Modifier
                .padding(bottom = depth)
                .offset { IntOffset(0, (depth - lift).roundToPx()) }
                .fillMaxWidth()
                .clip(shape)
                .background(face)
                .border(RondaDepth.border, edge, shape)
                .then(
                    if (onClick != null) {
                        Modifier.clickable(
                            interactionSource = interaction,
                            indication = null,
                            role = Role.Button,
                            onClick = onClick
                        )
                    } else {
                        Modifier
                    }
                )
                .padding(contentPadding),
            content = content
        )
    }
}

/**
 * A pictogram in a tinted box (DESIGN.md §6.4). Radius follows size: 24dp on a
 * hero box, 16dp on a card header, 12dp beside an info row. Filled boxes are
 * for the wordmark and the hero of a state card; everything else is a tint.
 */
@Composable
fun IconBox(
    @DrawableRes icon: Int,
    tone: Tone,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    filled: Boolean = false,
    contentDescription: String? = null
) {
    val colors = RondaTheme.colors
    val radius = when {
        size >= 48.dp -> RondaRadius.iconBoxLarge
        size >= 40.dp -> RondaRadius.button
        else -> RondaRadius.iconBoxSmall
    }
    val shape = RoundedCornerShape(radius)

    val background by animateColorAsState(
        targetValue = if (filled) colors.fill(tone) else colors.tint(tone),
        animationSpec = tween(260),
        label = "iconBoxBg"
    )
    val edge by animateColorAsState(
        targetValue = if (filled) colors.fill(tone) else colors.border(tone),
        animationSpec = tween(260),
        label = "iconBoxEdge"
    )
    val ink by animateColorAsState(
        targetValue = if (filled) colors.onFill(tone) else colors.fill(tone),
        animationSpec = tween(260),
        label = "iconBoxInk"
    )

    Box(
        modifier = modifier
            .size(size)
            .background(background, shape)
            .border(2.dp, edge, shape),
        contentAlignment = Alignment.Center
    ) {
        RondaIcon(
            id = icon,
            contentDescription = contentDescription,
            tint = ink,
            size = (size.value * 0.5f).coerceAtLeast(16f).dp
        )
    }
}

/**
 * The status badge (DESIGN.md §6.5): a filled pill of upper-case label type.
 * [filled] false gives the quiet version — tint and border — for states that
 * are information rather than alarm (a RENDAH band, "terhubung").
 */
@Composable
fun StatusBadge(
    text: String,
    tone: Tone,
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int? = null,
    filled: Boolean = true
) {
    val colors = RondaTheme.colors
    val shape = RoundedCornerShape(RondaRadius.badge)
    val background = if (filled) colors.badgeFill(tone) else colors.tint(tone)
    val ink = if (filled) colors.onFill(tone) else colors.fill(tone)

    Row(
        modifier = modifier
            .background(background, shape)
            .then(if (filled) Modifier else Modifier.border(1.5.dp, colors.border(tone), shape))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            RondaIcon(icon, contentDescription = null, tint = ink, size = 12.dp)
            Spacer(Modifier.width(6.dp))
        }
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = ink
        )
    }
}

/**
 * One fact inside a card (DESIGN.md §6.4): icon box, upper-case label, value.
 * The value takes a tone only when it is the finding — "Bukan Play Store" in
 * red — and stays ink otherwise.
 */
@Composable
fun InfoRow(
    @DrawableRes icon: Int,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    tone: Tone = Tone.NEUTRAL,
    valueTone: Tone? = null
) {
    val colors = RondaTheme.colors
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        IconBox(icon = icon, tone = tone, size = 32.dp)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = colors.textSecondary
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                color = valueTone?.let { colors.fill(it) } ?: colors.textPrimary
            )
        }
    }
}

/** A section heading in the list: upper-case label type, secondary ink. */
@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = RondaTheme.colors.textSecondary,
        modifier = modifier
    )
}

/** Hairline between rows inside a tinted card. */
@Composable
fun CardDivider(modifier: Modifier = Modifier, tone: Tone = Tone.NEUTRAL) {
    val colors = RondaTheme.colors
    Box(
        modifier
            .fillMaxWidth()
            .height(1.5.dp)
            .background(if (tone == Tone.NEUTRAL) colors.border else colors.border(tone))
    )
}
