package com.ronda.app.ui.guardian

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ronda.app.R
import com.ronda.app.core.RiskLevel
import com.ronda.app.core.Verdict
import com.ronda.app.core.VerdictState
import com.ronda.app.ui.components.BackTopBar
import com.ronda.app.ui.components.CardDivider
import com.ronda.app.ui.components.ConfirmSheet
import com.ronda.app.ui.components.IconBox
import com.ronda.app.ui.components.InfoRow
import com.ronda.app.ui.components.RondaCard
import com.ronda.app.ui.components.RondaIcon
import com.ronda.app.ui.components.RondaIcons
import com.ronda.app.ui.components.SecondaryButton
import com.ronda.app.ui.components.SectionLabel
import com.ronda.app.ui.components.StatusBadge
import com.ronda.app.ui.components.TactileButton
import com.ronda.app.ui.components.label
import com.ronda.app.ui.components.pressScale
import com.ronda.app.ui.components.relativeTime
import com.ronda.app.ui.components.screenInsets
import com.ronda.app.ui.guardian.components.ExplanationStack
import com.ronda.app.ui.theme.RondaRadius
import com.ronda.app.ui.theme.RondaTheme
import com.ronda.app.ui.theme.ScoreNumeral
import com.ronda.app.ui.theme.Tabular
import com.ronda.app.ui.theme.Tone
import com.ronda.app.ui.theme.tone

/**
 * The decision screen: identity and score, evidence, decision.
 *
 * Everything above the buttons is evidence; the buttons stay at the end of the
 * scroll so a guardian cannot decide before passing the sentences. Two actions
 * only (PRD FR-5): remove the app, or mark it safe.
 *
 * The protected phone reuses this read-only: [onMarkSafe] null drops the
 * guardian-only "mark safe" and the "they will be asked" hint, since only the
 * guardian decides.
 */
@Composable
fun AlertDetailScreen(
    verdict: Verdict,
    protectedName: String,
    undoable: Boolean,
    onMarkUnsafe: () -> Unit,
    onMarkSafe: (() -> Unit)?,
    onUndo: () -> Unit,
    onRequestUninstall: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // System Back returns to the list. Without this it would leave the app,
    // since the list and the detail are the same Activity.
    BackHandler(onBack = onBack)

    var confirming by remember { mutableStateOf(false) }
    var technicalOpen by rememberSaveable { mutableStateOf(false) }
    val level = RiskLevel.of(verdict.score)

    Column(
        modifier
            .fillMaxSize()
            .screenInsets()
    ) {
        BackTopBar(onBack = onBack, title = stringResource(R.string.detail_title))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            HeaderCard(verdict = verdict, level = level)

            if (verdict.overrodeAt > 0L) {
                Spacer(Modifier.height(12.dp))
                RondaCard(tone = Tone.WARN, contentPadding = PaddingValues(16.dp)) {
                    Row(verticalAlignment = Alignment.Top) {
                        IconBox(icon = RondaIcons.eye, tone = Tone.WARN, size = 40.dp)
                        Spacer(Modifier.width(14.dp))
                        Text(
                            text = stringResource(R.string.watch_override, protectedName) +
                                " · " + relativeTime(verdict.overrodeAt),
                            style = MaterialTheme.typography.bodyLarge,
                            color = RondaTheme.colors.textPrimary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            SectionLabel(stringResource(R.string.detail_why))
            Spacer(Modifier.height(10.dp))
            ExplanationStack(verdict = verdict, protectedName = protectedName)

            Spacer(Modifier.height(8.dp))
            TechnicalDetails(verdict, technicalOpen) { technicalOpen = !technicalOpen }

            Spacer(Modifier.height(24.dp))
            Decision(
                verdict = verdict,
                protectedName = protectedName,
                undoable = undoable,
                onAskMarkSafe = onMarkSafe?.let { { confirming = true } },
                onUndo = onUndo,
                onUninstall = {
                    onMarkUnsafe()
                    onRequestUninstall()
                },
                onRequestAgain = onRequestUninstall
            )
            Spacer(Modifier.height(32.dp))
        }
    }

    if (confirming) {
        ConfirmSheet(
            title = stringResource(R.string.confirm_safe_title),
            body = stringResource(R.string.confirm_safe_body, protectedName),
            confirmText = stringResource(R.string.confirm_safe_yes),
            confirmTone = Tone.TRUST,
            confirmIcon = RondaIcons.check,
            onConfirm = { confirming = false; onMarkSafe?.invoke() },
            onDismiss = { confirming = false }
        )
    }
}

/** Identity and score in one card, in the band's colour while the app is still a question. */
@Composable
private fun HeaderCard(verdict: Verdict, level: RiskLevel) {
    val colors = RondaTheme.colors
    val fromPlay = verdict.signals.any { it.key == "SRC_PLAY" }
    val tone = when {
        verdict.removed -> Tone.SAFE
        verdict.state == VerdictState.RESOLVED_SAFE -> Tone.NEUTRAL
        else -> level.tone()
    }

    RondaCard(tone = tone) {
        Row(verticalAlignment = Alignment.Top) {
            IconBox(
                icon = if (verdict.removed) RondaIcons.check else RondaIcons.forLevel(level),
                tone = if (verdict.removed) Tone.SAFE else level.tone(),
                size = 56.dp
            )
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
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

                    else -> StatusBadge(
                        text = level.label(),
                        tone = level.tone(),
                        filled = level >= RiskLevel.PERINGATAN
                    )
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    text = verdict.appLabel,
                    style = MaterialTheme.typography.headlineLarge,
                    color = colors.textPrimary
                )
                Text(
                    text = verdict.packageName,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        // TalkBack gets one label for the whole row ("Risiko 96 dari 100,
        // Darurat") rather than three fragments read in isolation.
        val scoreLabel = stringResource(R.string.a11y_score, verdict.score, level.label())
        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.clearAndSetSemantics { contentDescription = scoreLabel }
        ) {
            Text(
                text = "${verdict.score}",
                style = ScoreNumeral,
                color = colors.fill(level.tone()),
                modifier = Modifier.alignByBaseline()
            )
            Text(
                text = stringResource(R.string.detail_of_100),
                style = MaterialTheme.typography.titleLarge,
                color = colors.textSecondary,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .alignByBaseline()
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.detail_score_label).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = colors.textSecondary,
                modifier = Modifier.alignByBaseline()
            )
        }

        Spacer(Modifier.height(16.dp))
        CardDivider(tone = tone)
        Spacer(Modifier.height(16.dp))
        InfoRow(
            icon = if (fromPlay) RondaIcons.circleCheck else RondaIcons.send,
            label = stringResource(R.string.detail_label_source),
            value = stringResource(
                if (fromPlay) R.string.detail_source_play else R.string.detail_source_sideload
            ),
            tone = if (fromPlay) Tone.SAFE else Tone.DANGER,
            valueTone = if (fromPlay) null else Tone.DANGER
        )
        Spacer(Modifier.height(12.dp))
        InfoRow(
            icon = RondaIcons.clock,
            label = stringResource(R.string.detail_label_installed),
            value = relativeTime(verdict.detectedAt),
            tone = Tone.NEUTRAL
        )
    }
}

@Composable
private fun Decision(
    verdict: Verdict,
    protectedName: String,
    undoable: Boolean,
    onAskMarkSafe: (() -> Unit)?,
    onUndo: () -> Unit,
    onUninstall: () -> Unit,
    onRequestAgain: () -> Unit
) {
    val colors = RondaTheme.colors
    val wide = Modifier.fillMaxWidth()

    when {
        verdict.removed -> OutcomeCard(
            tone = Tone.SAFE,
            icon = RondaIcons.check,
            text = stringResource(R.string.resolved_removed, protectedName)
        )

        verdict.state == VerdictState.RESOLVED_SAFE -> {
            OutcomeCard(
                tone = Tone.SAFE,
                icon = RondaIcons.circleCheck,
                text = stringResource(R.string.resolved_safe)
            )
            if (undoable) {
                Spacer(Modifier.height(12.dp))
                SecondaryButton(
                    text = stringResource(R.string.action_undo),
                    onClick = onUndo,
                    icon = RondaIcons.undo,
                    modifier = wide
                )
            }
        }

        verdict.state == VerdictState.RESOLVED_UNSAFE -> {
            OutcomeCard(
                tone = Tone.WARN,
                icon = RondaIcons.clock,
                text = stringResource(R.string.resolved_unsafe_waiting, protectedName)
            )
            Spacer(Modifier.height(12.dp))
            SecondaryButton(
                text = stringResource(R.string.action_request_again),
                onClick = onRequestAgain,
                icon = RondaIcons.send,
                modifier = wide
            )
        }

        else -> {
            // The one red button in the app: irreversible, so it is red; the
            // protected person still confirms, so it says so underneath.
            TactileButton(
                text = stringResource(R.string.action_uninstall),
                onClick = onUninstall,
                tone = Tone.DANGER,
                icon = RondaIcons.trash,
                modifier = wide
            )
            if (onAskMarkSafe != null) {
                Spacer(Modifier.height(10.dp))
                Text(
                    text = stringResource(R.string.action_uninstall_hint, protectedName),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = wide
                )
                Spacer(Modifier.height(16.dp))
                SecondaryButton(
                    text = stringResource(R.string.action_mark_safe),
                    onClick = onAskMarkSafe,
                    icon = RondaIcons.check,
                    modifier = wide
                )
            }
        }
    }
}

@Composable
private fun OutcomeCard(tone: Tone, icon: Int, text: String) {
    RondaCard(tone = tone, contentPadding = PaddingValues(16.dp)) {
        Row(verticalAlignment = Alignment.Top) {
            IconBox(icon = icon, tone = tone, size = 40.dp, filled = tone == Tone.SAFE)
            Spacer(Modifier.width(14.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = RondaTheme.colors.textPrimary,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Closed by default and never part of the main flow. It exists so the guardian
 * can show a technical friend, not so RONDA can look clever.
 */
@Composable
private fun TechnicalDetails(verdict: Verdict, open: Boolean, onToggle: () -> Unit) {
    val colors = RondaTheme.colors
    val rotation by animateFloatAsState(targetValue = if (open) 180f else 0f, label = "chevron")
    val interaction = remember { MutableInteractionSource() }

    Column(Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .pressScale(interaction, pressedScale = 0.98f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(RondaRadius.iconBoxSmall))
                .clickable(interaction, LocalIndication.current, role = Role.Button, onClick = onToggle)
                .padding(vertical = 12.dp, horizontal = 4.dp)
        ) {
            Text(
                text = stringResource(R.string.detail_technical),
                style = MaterialTheme.typography.titleSmall,
                color = colors.textSecondary,
                modifier = Modifier.weight(1f)
            )
            RondaIcon(
                id = RondaIcons.chevronDown,
                contentDescription = null,
                tint = colors.textSecondary,
                size = 18.dp,
                modifier = Modifier.graphicsLayer { rotationZ = rotation }
            )
        }
        AnimatedVisibility(visible = open) {
            RondaCard(contentPadding = PaddingValues(16.dp)) {
                Label(R.string.detail_technical_vector)
                Datum(verdict.vector)

                Spacer(Modifier.height(12.dp))
                Datum(
                    stringResource(
                        R.string.detail_technical_math,
                        "%.1f".format(verdict.impact),
                        "%.4f".format(verdict.trust),
                        verdict.score
                    )
                )

                Spacer(Modifier.height(12.dp))
                Label(R.string.detail_technical_attack)
                verdict.signals.filter { it.attackId != null }.forEach {
                    Datum("${it.key}  ${it.attackId}")
                }
            }
        }
    }
}

@Composable
private fun Label(res: Int) {
    Text(
        text = stringResource(res).uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = RondaTheme.colors.textSecondary
    )
    Spacer(Modifier.height(4.dp))
}

/** One line of figures. Tabular numerals so the columns hold still. */
@Composable
private fun Datum(text: String) {
    Text(text = text, style = Tabular, color = RondaTheme.colors.textPrimary)
}
