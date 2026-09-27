package com.ronda.app.ui.onboarding

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.ronda.app.R
import com.ronda.app.ui.components.BrandMark
import com.ronda.app.ui.theme.RondaTheme
import kotlinx.coroutines.delay

/**
 * The cold-start intro, drawn over the app while it composes underneath:
 * the mark pops in alone, then swings left as the name slides in beside it,
 * and the finished lockup dissolves into the first screen.
 *
 * The system splash in front of this is only the page colour (see
 * Theme.RONDA.Starting), so the hand-off from it is invisible. Touches are
 * swallowed until [onFinished], so nothing underneath takes a stray tap.
 */
@Composable
fun SplashIntro(onFinished: () -> Unit) {
    val colors = RondaTheme.colors
    val pop = remember { Animatable(0f) }
    val slide = remember { Animatable(0f) }
    val exit = remember { Animatable(0f) }

    var nameWidth by remember { mutableIntStateOf(0) }
    val gap = 16.dp
    val gapPx = with(LocalDensity.current) { gap.toPx() }
    val nameTravelPx = with(LocalDensity.current) { 48.dp.toPx() }

    LaunchedEffect(Unit) {
        // Low damping: it overshoots and settles — the "pop".
        pop.animateTo(1f, spring(dampingRatio = 0.42f, stiffness = 360f))
        delay(90)
        slide.animateTo(1f, tween(380, easing = FastOutSlowInEasing))
        delay(420)
        exit.animateTo(1f, tween(260, easing = FastOutLinearInEasing))
        onFinished()
    }

    Box(
        Modifier
            .fillMaxSize()
            .graphicsLayer { alpha = 1f - exit.value }
            .background(colors.bg)
            .clickable(interactionSource = null, indication = null) {},
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.graphicsLayer {
                // A slight push toward the viewer as it leaves.
                val grow = 1f + 0.06f * exit.value
                scaleX = grow
                scaleY = grow
            }
        ) {
            // The row is laid out as the finished lockup; before the slide
            // the mark is pushed right by half the name's width, so it starts
            // alone in the centre.
            val homeOffset = (nameWidth + gapPx) / 2f
            BrandMark(
                height = 76.dp,
                modifier = Modifier.graphicsLayer {
                    scaleX = pop.value
                    scaleY = pop.value
                    translationX = (1f - slide.value) * homeOffset
                }
            )
            Spacer(Modifier.width(gap))
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.displayMedium.copy(letterSpacing = 0.06.em),
                color = colors.textPrimary,
                modifier = Modifier
                    .onSizeChanged { nameWidth = it.width }
                    .graphicsLayer {
                        alpha = slide.value
                        translationX = (1f - slide.value) * nameTravelPx
                    }
            )
        }
    }
}
