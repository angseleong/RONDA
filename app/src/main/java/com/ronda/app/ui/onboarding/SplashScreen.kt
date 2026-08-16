package com.ronda.app.ui.onboarding

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ronda.app.R
import com.ronda.app.ui.components.RondaWordmark
import kotlinx.coroutines.delay

/**
 * The first second of the app.
 *
 * Deliberately thin. It is not a loading screen — nothing is being loaded, the
 * role and pairing are already in SharedPreferences by the time this composes —
 * it is a held beat so the app opens on its own name rather than snapping
 * straight into a permission checklist.
 *
 * That matters most on the protected phone: an older user who has just been
 * talked into installing something needs a moment to see whose app this is
 * before it starts asking for things.
 *
 * Total 1100ms. Long enough to register, short enough that nobody waits for it.
 * A progress bar would be a lie about work that is not happening, so the only
 * moving part is a determinate sweep that finishes with the delay.
 */
@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val fade = remember { Animatable(0f) }
    val sweep = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        fade.animateTo(1f, tween(durationMillis = 420, easing = LinearEasing))
    }
    LaunchedEffect(Unit) {
        // Starts after the mark is legible, ends with the screen. Tied to the
        // same clock as the dismissal so it can never sit full and wait.
        delay(180)
        sweep.animateTo(1f, tween(durationMillis = 820, easing = LinearEasing))
    }
    LaunchedEffect(Unit) {
        delay(1100)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.alpha(fade.value)
        ) {
            RondaWordmark(height = 52.dp)

            Box(
                modifier = Modifier
                    .padding(top = 28.dp)
                    .width(132.dp)
                    .height(3.dp)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.16f),
                        RoundedCornerShape(999.dp)
                    )
            ) {
                Box(
                    Modifier
                        .fillMaxWidth(sweep.value)
                        .height(3.dp)
                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(999.dp))
                )
            }
        }

        Text(
            text = stringResource(R.string.splash_tagline),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp)
                .alpha(fade.value)
        )
    }
}
