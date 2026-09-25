package com.ronda.app.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.ronda.app.ui.theme.LargePrintLabel
import com.ronda.app.ui.theme.RondaDepth
import com.ronda.app.ui.theme.RondaRadius
import com.ronda.app.ui.theme.RondaTheme
import com.ronda.app.ui.theme.Tone
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

/**
 * Pressed, but held for at least [PRESS_HOLD_MS] after release. Inside a
 * scrolling screen Compose delays the press and a quick tap emits Press and
 * Release in the same frame, so collectIsPressedAsState() never shows it and
 * the button looks dead. Holding the release makes every tap visible.
 */
@Composable
fun InteractionSource.collectIsPressedVisibly(): Boolean {
    var pressed by remember { mutableStateOf(false) }
    LaunchedEffect(this) {
        interactions.collectLatest {
            when (it) {
                is PressInteraction.Press -> pressed = true
                is PressInteraction.Release, is PressInteraction.Cancel -> {
                    delay(PRESS_HOLD_MS)
                    pressed = false
                }
            }
        }
    }
    return pressed
}

/**
 * The press cue every tappable shares: a slight shrink while pressed. Goes
 * first in the chain so the whole control (shadow, border, face) scales as one.
 */
@Composable
fun Modifier.pressScale(interaction: InteractionSource, pressedScale: Float = 0.96f): Modifier {
    val scale by animateFloatAsState(
        targetValue = if (interaction.collectIsPressedVisibly()) pressedScale else 1f,
        animationSpec = tween(durationMillis = 100, easing = FastOutSlowInEasing),
        label = "pressScale"
    )
    return graphicsLayer { scaleX = scale; scaleY = scale }
}

private const val PRESS_HOLD_MS = 120L

/**
 * The signature of the system (DESIGN.md §6.1): a face resting 5dp above a
 * solid shadow. Pressing pushes the face down onto the shadow, so the button
 * physically travels under the thumb — the one piece of motion every primary
 * action shares.
 *
 * Tone picks the family: SAFE for progress and confirmation, DANGER only for
 * the irreversible (uninstall), TRUST for guardian confirmations.
 */
@Composable
fun TactileButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tone: Tone = Tone.SAFE,
    enabled: Boolean = true,
    loading: Boolean = false,
    @DrawableRes icon: Int? = null,
    large: Boolean = false
) {
    val colors = RondaTheme.colors
    val shape = RoundedCornerShape(RondaRadius.button)
    val travel = RondaDepth.button
    val active = enabled && !loading

    val interaction = remember { MutableInteractionSource() }
    val pressed = interaction.collectIsPressedVisibly()
    val lift by animateDpAsState(
        targetValue = if (pressed && active) 0.dp else travel,
        animationSpec = tween(durationMillis = 90, easing = FastOutSlowInEasing),
        label = "tactileLift"
    )

    // The press is felt as well as seen: one tick on release, the other half
    // of the tactile device the brief borrows.
    val haptic = LocalHapticFeedback.current

    // A disabled button loses its colour, not its shape: the layout does not
    // jump when a field becomes valid.
    val fill = if (enabled) colors.fill(tone) else colors.borderStrong
    val shadow = if (enabled) colors.shadow(tone) else colors.border
    val ink = colors.onFill(tone)

    Box(modifier.pressScale(interaction)) {
        Box(
            Modifier
                .matchParentSize()
                .padding(top = travel)
                .background(shadow, shape)
        )
        Row(
            modifier = Modifier
                .padding(bottom = travel)
                .offset { IntOffset(0, (travel - lift).roundToPx()) }
                .fillMaxWidth()
                .heightIn(min = if (large) 60.dp else 56.dp)
                .clip(shape)
                .background(fill)
                .clickable(
                    interactionSource = interaction,
                    indication = null,
                    enabled = active,
                    role = Role.Button,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                        onClick()
                    }
                )
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = ink,
                    strokeWidth = 2.5.dp
                )
                Spacer(Modifier.width(10.dp))
            } else if (icon != null) {
                RondaIcon(icon, contentDescription = null, tint = ink, size = 22.dp)
                Spacer(Modifier.width(10.dp))
            }
            Text(
                text = text.uppercase(),
                style = if (large) LargePrintLabel else MaterialTheme.typography.labelLarge,
                color = ink,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * The outline button (DESIGN.md §6.2): card surface, thick border, the same
 * solid drop and the same travel. Its text carries the tone, never its fill —
 * "Tandai aman" is blue text, "Putuskan hubungan" is red text, and neither
 * competes with the one filled button on the screen.
 */
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tone: Tone = Tone.TRUST,
    enabled: Boolean = true,
    @DrawableRes icon: Int? = null,
    large: Boolean = false
) {
    val colors = RondaTheme.colors
    val shape = RoundedCornerShape(RondaRadius.button)
    val travel = RondaDepth.button

    val interaction = remember { MutableInteractionSource() }
    val pressed = interaction.collectIsPressedVisibly()
    val lift by animateDpAsState(
        targetValue = if (pressed && enabled) 0.dp else travel,
        animationSpec = tween(durationMillis = 90, easing = FastOutSlowInEasing),
        label = "secondaryLift"
    )

    val edge = if (tone == Tone.DANGER) colors.dangerBorder else colors.border
    val ink = if (enabled) colors.fill(tone) else colors.textMuted

    Box(modifier.pressScale(interaction)) {
        Box(
            Modifier
                .matchParentSize()
                .padding(top = travel)
                .background(edge, shape)
        )
        Row(
            modifier = Modifier
                .padding(bottom = travel)
                .offset { IntOffset(0, (travel - lift).roundToPx()) }
                .fillMaxWidth()
                .heightIn(min = if (large) 56.dp else 52.dp)
                .clip(shape)
                .background(colors.card)
                .border(RondaDepth.border, edge, shape)
                .clickable(
                    interactionSource = interaction,
                    indication = null,
                    enabled = enabled,
                    role = Role.Button,
                    onClick = onClick
                )
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                RondaIcon(icon, contentDescription = null, tint = ink, size = 20.dp)
                Spacer(Modifier.width(10.dp))
            }
            Text(
                text = text,
                style = if (large) LargePrintLabel else MaterialTheme.typography.titleMedium,
                color = ink,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * The quiet third option — "Nanti saja", "Lewati" — set as trust-coloured text
 * with a full-height touch target and no chrome, so it never reads as a peer
 * of the button above it.
 */
@Composable
fun TextAction(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tone: Tone = Tone.TRUST,
    large: Boolean = false
) {
    val colors = RondaTheme.colors
    val interaction = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .pressScale(interaction)
            .heightIn(min = 48.dp)
            .clip(RoundedCornerShape(RondaRadius.button))
            .clickable(interaction, LocalIndication.current, role = Role.Button, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = if (large) LargePrintLabel else MaterialTheme.typography.titleMedium,
            color = colors.fill(tone),
            textAlign = TextAlign.Center
        )
    }
}
