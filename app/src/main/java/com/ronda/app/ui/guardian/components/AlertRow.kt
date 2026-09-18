package com.ronda.app.ui.guardian.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ronda.app.R
import com.ronda.app.core.RiskEvaluator
import com.ronda.app.core.RiskLevel
import com.ronda.app.core.Verdict
import com.ronda.app.core.VerdictState
import com.ronda.app.ui.components.IconBox
import com.ronda.app.ui.components.RondaCard
import com.ronda.app.ui.components.RondaIcon
import com.ronda.app.ui.components.RondaIcons
import com.ronda.app.ui.components.StatusBadge
import com.ronda.app.ui.components.label
import com.ronda.app.ui.components.relativeTime
import com.ronda.app.ui.theme.RondaTheme
import com.ronda.app.ui.theme.Tabular
import com.ronda.app.ui.theme.Tone
import com.ronda.app.ui.theme.tone

/**
 * One app in the list. The badge carries the triage; the first sentence carries
 * just enough of the reason to decide whether to open it.
 *
 * An open DARURAT row is a red state card, an open PERINGATAN row amber; a
 * quiet row and a decided row are plain cards with a quiet badge. The colour
 * is spent on what still needs a decision and nowhere else.
 */
@Composable
fun AlertRow(
    verdict: Verdict,
    protectedName: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = RondaTheme.colors
    val level = RiskLevel.of(verdict.score)
    val open = !verdict.state.decided
    val loud = open && level >= RiskLevel.PERINGATAN

    // Only apps that need a decision get a sentence in the list. Leading a
    // RENDAH row with "this steals bank OTPs" is the exact alert fatigue this
    // threshold exists to prevent — the quiet tab has to read quiet.
    //
    // One complete sentence, cut at its full stop rather than at a line count:
    // "…mengirimnya ke internet." is the consequence; the pattern sentence that
    // follows it belongs to the detail screen.
    val firstSentence = if (verdict.score < RiskEvaluator.GUARDIAN_THRESHOLD) "" else {
        explanationKeys(verdict)
            .firstNotNullOfOrNull { sentenceRes(it) }
            ?.let { stringResource(it, protectedName) }
            ?.let { full ->
                val stop = full.indexOf(". ")
                if (stop > 0) full.substring(0, stop + 1) else full
            }
            .orEmpty()
    }

    RondaCard(
        tone = if (loud) level.tone() else Tone.NEUTRAL,
        onClick = onClick,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.Top) {
            IconBox(
                icon = if (verdict.removed) RondaIcons.check else RondaIcons.forLevel(level),
                tone = if (verdict.removed) Tone.SAFE else level.tone(),
                size = 44.dp
            )
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                // Name first at full width, badge on its own line beneath: a
                // badge beside the name forces "Senter Super Terang" or "Info
                // BCA Mobile" to wrap at large font sizes and in history, where
                // the outcome badges are long.
                Text(
                    text = verdict.appLabel,
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.textPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(6.dp))
                OutcomeBadge(verdict, level)

                if (firstSentence.isNotBlank()) {
                    Spacer(Modifier.height(6.dp))
                    // Two lines: one cuts most of these sentences before the
                    // consequence, which is the part worth previewing.
                    Text(
                        text = firstSentence,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.score_of, verdict.score) +
                        " · " + relativeTime(verdict.detectedAt) +
                        " · " + statusLabel(verdict),
                    style = Tabular,
                    color = colors.textSecondary
                )

                // RONDA could not stop them, so it makes the override visible.
                // This is the mechanism, not a log line — it gets the accent.
                if (verdict.overrodeAt > 0L) {
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.Top) {
                        RondaIcon(
                            id = RondaIcons.eye,
                            contentDescription = null,
                            tint = colors.warn,
                            size = 16.dp,
                            modifier = Modifier.padding(top = 1.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.watch_override, protectedName) +
                                " · " + relativeTime(verdict.overrodeAt),
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.warn
                        )
                    }
                }
            }
        }
    }
}

/** The band while open; the outcome once decided. */
@Composable
private fun OutcomeBadge(verdict: Verdict, level: RiskLevel) {
    when {
        verdict.removed -> StatusBadge(
            text = stringResource(R.string.watch_status_removed),
            tone = Tone.SAFE,
            icon = RondaIcons.check
        )

        verdict.state == VerdictState.RESOLVED_SAFE -> StatusBadge(
            text = stringResource(R.string.watch_status_safe),
            tone = Tone.SAFE,
            filled = false
        )

        verdict.state == VerdictState.RESOLVED_UNSAFE -> StatusBadge(
            text = stringResource(R.string.watch_status_unsafe),
            tone = Tone.DANGER,
            filled = false
        )

        else -> StatusBadge(
            text = level.label(),
            tone = level.tone(),
            filled = level >= RiskLevel.PERINGATAN
        )
    }
}

@Composable
private fun statusLabel(verdict: Verdict): String = stringResource(
    when {
        verdict.removed -> R.string.watch_status_removed
        verdict.state == VerdictState.RESOLVED_SAFE -> R.string.watch_status_safe
        verdict.state == VerdictState.RESOLVED_UNSAFE -> R.string.watch_status_unsafe
        // Below the threshold nothing was ever asked of the guardian, so
        // "waiting for your decision" would be a lie.
        verdict.state == VerdictState.RESOLVED_PASSIVE -> R.string.watch_status_passive
        else -> R.string.watch_status_pending
    }
)
