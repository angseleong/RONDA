package com.ronda.app.ui.guardian.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ronda.app.R
import com.ronda.app.ui.theme.Eyebrow
import com.ronda.app.ui.theme.bandSafe
import com.ronda.app.ui.theme.bandSafeTint
import com.ronda.app.ui.theme.bandUrgent
import com.ronda.app.ui.theme.bandUrgentTint
import com.ronda.app.ui.theme.bandWarn
import com.ronda.app.ui.theme.bandWarnTint

/**
 * The answer, before the list.
 *
 * A guardian opens RONDA to settle one question — *is anything wrong right now?*
 * The old screen made them infer it from a tab label and a list length. This
 * states it, in the largest type on the screen, and the list below becomes
 * supporting detail rather than the thing to be parsed.
 *
 * Three states, in strict priority. Disconnected outranks a clean list because
 * "nothing to review" and "cannot see anything to review" look identical from a
 * list and mean opposite things.
 */
@Composable
fun StatusHero(
    connected: Boolean,
    protectedName: String,
    reviewCount: Int,
    monitoredCount: Int,
    historyCount: Int,
    modifier: Modifier = Modifier
) {
    val tone = when {
        !connected -> HeroTone(bandWarn, bandWarnTint, "!")
        reviewCount > 0 -> HeroTone(bandUrgent, bandUrgentTint, "!")
        else -> HeroTone(bandSafe, bandSafeTint, "✓")
    }

    val title = when {
        !connected -> stringResource(R.string.watch_hero_offline_title)
        reviewCount > 0 -> stringResource(R.string.watch_hero_review_title, reviewCount)
        else -> stringResource(R.string.watch_hero_safe_title, protectedName)
    }

    val body = when {
        !connected -> stringResource(R.string.watch_hero_offline_body, protectedName)
        reviewCount > 0 -> stringResource(R.string.watch_hero_review_body)
        else -> stringResource(R.string.watch_hero_safe_body)
    }

    Surface(
        color = tone.tint,
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, tone.accent.copy(alpha = 0.22f), RoundedCornerShape(20.dp))
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // A glyph rather than an icon font: it carries no meaning the
                // headline does not already carry, so it is marked decorative
                // and never becomes the only way to know the state.
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(tone.accent, CircleShape)
                        .clearAndSetSemantics { },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tone.glyph,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.surface
                    )
                }
                Spacer(Modifier.width(14.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = tone.accent,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(12.dp))
            Text(
                text = body,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = tone.accent.copy(alpha = 0.18f))
            Spacer(Modifier.height(14.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Stat(reviewCount, stringResource(R.string.watch_stat_review), Modifier.weight(1f))
                Stat(
                    monitoredCount,
                    stringResource(R.string.watch_stat_monitored),
                    Modifier.weight(1f)
                )
                Stat(historyCount, stringResource(R.string.watch_stat_history), Modifier.weight(1f))
            }
        }
    }
}

/**
 * One count. Deliberately not tappable — three tiles that look like buttons but
 * only two of which lead anywhere is worse than three that lead nowhere.
 */
@Composable
private fun Stat(count: Int, label: String, modifier: Modifier = Modifier) {
    val description = "$count $label"
    Column(modifier.clearAndSetSemantics { contentDescription = description }) {
        Text(
            text = "$count",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = Eyebrow,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private data class HeroTone(val accent: Color, val tint: Color, val glyph: String)
