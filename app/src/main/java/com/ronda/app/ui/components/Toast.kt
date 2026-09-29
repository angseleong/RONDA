package com.ronda.app.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.ronda.app.ui.theme.LargePrint
import com.ronda.app.ui.theme.RondaTheme
import com.ronda.app.ui.theme.Tone
import kotlinx.coroutines.delay

/** One short line about something that just changed. */
data class RondaToast(
    val text: String,
    val tone: Tone,
    @DrawableRes val icon: Int,
    /** Distinct per show, so the same sentence twice still re-times itself. */
    val id: Long = System.nanoTime()
)

/**
 * Held by the Activity rather than a screen: most of what a toast reports —
 * a pairing landing, a scan finishing, the other phone disconnecting — also
 * changes which screen is showing, and the toast has to outlive that switch.
 *
 * One at a time, newest wins. A burst (a scan flagging three apps) would
 * otherwise queue into a slideshow nobody reads to the end.
 */
@Stable
class ToastState {
    var current by mutableStateOf<RondaToast?>(null)
        private set

    fun show(text: String, tone: Tone, @DrawableRes icon: Int) {
        current = RondaToast(text, tone, icon)
    }

    fun dismiss(id: Long) {
        if (current?.id == id) current = null
    }
}

/**
 * Drops in from the top, below the status bar: the bottom of a screen is
 * where its buttons and tab bar live, and a toast there would sit on them.
 * Tap to dismiss; otherwise it leaves on its own.
 *
 * [large] is the Rondee's ≥20sp floor (PRD usability), which applies to
 * everything on that phone, toasts included.
 */
@Composable
fun ToastHost(state: ToastState, modifier: Modifier = Modifier, large: Boolean = false) {
    val toast = state.current
    val haptics = LocalHapticFeedback.current

    // Keeps the last toast composed while it animates out.
    var shown by remember { mutableStateOf(toast) }
    if (toast != null) shown = toast

    LaunchedEffect(toast?.id) {
        if (toast == null) return@LaunchedEffect
        haptics.performHapticFeedback(
            if (toast.tone == Tone.DANGER) HapticFeedbackType.Reject else HapticFeedbackType.Confirm
        )
        delay(if (large) LARGE_VISIBLE_MS else VISIBLE_MS)
        state.dismiss(toast.id)
    }

    AnimatedVisibility(
        visible = toast != null,
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        enter = slideInVertically(spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMediumLow)) { -it } +
            scaleIn(spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMedium), initialScale = 0.92f) +
            fadeIn(tween(120)),
        exit = slideOutVertically(tween(180)) { -it } + fadeOut(tween(160))
    ) {
        val item = shown ?: return@AnimatedVisibility
        RondaCard(
            tone = item.tone,
            onClick = { state.dismiss(item.id) },
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconBox(icon = item.icon, tone = item.tone, size = 36.dp, filled = true)
                Spacer(Modifier.width(12.dp))
                Text(
                    text = item.text,
                    style = if (large) LargePrint else MaterialTheme.typography.titleMedium,
                    color = RondaTheme.colors.textPrimary
                )
            }
        }
    }
}

private const val VISIBLE_MS = 3_200L

/** Longer on the Rondee's phone: larger print, and a slower reader. */
private const val LARGE_VISIBLE_MS = 4_500L
