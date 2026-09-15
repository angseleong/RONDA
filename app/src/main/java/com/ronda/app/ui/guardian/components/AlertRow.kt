package com.ronda.app.ui.guardian.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ronda.app.R
import com.ronda.app.core.RiskEvaluator
import com.ronda.app.core.RiskLevel
import com.ronda.app.core.Verdict
import com.ronda.app.core.VerdictState
import com.ronda.app.ui.theme.Eyebrow
import com.ronda.app.ui.theme.lamp
import com.ronda.app.ui.theme.linenDim
import com.ronda.app.ui.theme.nightRaised

/**
 * One app in the watch list. The score badge carries the triage; the first
 * sentence carries just enough of the reason to decide whether to open it.
 *
 * Resolved rows drop to 60% opacity — still readable as history, visibly not
 * asking for anything.
 *
 * The whole row is one tappable surface, so the ripple is clipped to the
 * corners and TalkBack reads it as a single button: score, name, band,
 * sentence, time.
 */
@Composable
fun AlertRow(
    verdict: Verdict,
    protectedName: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val level = RiskLevel.of(verdict.score)
    val resolved = verdict.state != VerdictState.PENDING_GUARDIAN &&
        verdict.state != VerdictState.DETECTED

    // Only apps that need a decision get a sentence in the list. Leading a
    // RENDAH row with "this steals bank OTPs" is the exact alert fatigue this
    // threshold exists to prevent — the quiet tab has to read quiet.
    val firstSentence = if (verdict.score < RiskEvaluator.GUARDIAN_THRESHOLD) "" else {
        explanationKeys(verdict)
            .firstNotNullOfOrNull { sentenceRes(it) }
            ?.let { stringResource(it, protectedName) }
            .orEmpty()
    }

    Surface(
        onClick = onClick,
        color = nightRaised,
        shape = MaterialTheme.shapes.large,
        modifier = modifier
            .fillMaxWidth()
            .alpha(if (resolved) 0.6f else 1f)
    ) {
        Row(Modifier.padding(16.dp)) {
            ScoreBadge(score = verdict.score, level = level)
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = verdict.appLabel,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(text = level.name, style = Eyebrow, color = linenDim)

                if (firstSentence.isNotBlank()) {
                    Spacer(Modifier.height(6.dp))
                    // Two lines: one cuts most of these sentences before the
                    // consequence, which is the part worth previewing.
                    Text(
                        text = firstSentence,
                        style = MaterialTheme.typography.bodyMedium,
                        color = linenDim,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(Modifier.height(8.dp))
                Text(
                    text = "${relativeTime(verdict.detectedAt)} · ${statusLabel(verdict)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = linenDim
                )

                // RONDA could not stop them, so it makes the override visible.
                // This is the mechanism, not a log line — it gets the accent.
                if (verdict.overrodeAt > 0L) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.watch_override, protectedName) +
                            " · " + relativeTime(verdict.overrodeAt),
                        style = MaterialTheme.typography.bodyMedium,
                        color = lamp
                    )
                }
            }
        }
    }
}

@Composable
private fun statusLabel(verdict: Verdict): String = stringResource(
    when (verdict.state) {
        VerdictState.RESOLVED_SAFE -> R.string.watch_status_safe
        VerdictState.RESOLVED_UNSAFE -> R.string.watch_status_unsafe
        // Below the threshold nothing was ever asked of the guardian, so
        // "waiting for your decision" would be a lie.
        VerdictState.RESOLVED_PASSIVE -> R.string.watch_status_passive
        else -> R.string.watch_status_pending
    }
)

@Composable
fun relativeTime(timestamp: Long): String {
    if (timestamp <= 0L) return ""
    val minutes = (System.currentTimeMillis() - timestamp) / 60_000L
    return when {
        minutes < 1 -> stringResource(R.string.time_just_now)
        minutes < 60 -> stringResource(R.string.time_minutes, minutes.toInt())
        minutes < 60 * 24 -> stringResource(R.string.time_hours, (minutes / 60).toInt())
        else -> stringResource(R.string.time_days, (minutes / (60 * 24)).toInt())
    }
}
