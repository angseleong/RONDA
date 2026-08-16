package com.ronda.app.ui.guardian

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ronda.app.R
import com.ronda.app.core.RiskLevel
import com.ronda.app.core.Verdict
import com.ronda.app.core.VerdictState
import com.ronda.app.ui.guardian.components.ExplanationStack
import com.ronda.app.ui.guardian.components.ScoreBlock
import com.ronda.app.ui.guardian.components.relativeTime
import com.ronda.app.ui.theme.Eyebrow
import com.ronda.app.ui.theme.bandSafe
import com.ronda.app.ui.theme.bandWarn
import com.ronda.app.ui.theme.bandWarnTint

/**
 * The decision screen: score, identity, explanations, decision.
 *
 * Everything above the buttons is evidence; the buttons stay at the end of the
 * scroll so a guardian cannot decide before passing the sentences.
 */
@Composable
fun AlertDetailScreen(
    verdict: Verdict,
    protectedName: String,
    undoable: Boolean,
    onMarkUnsafe: () -> Unit,
    onMarkSafe: () -> Unit,
    onUndo: () -> Unit,
    onRequestUninstall: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var confirming by remember { mutableStateOf(false) }
    var technicalOpen by rememberSaveable { mutableStateOf(false) }
    val level = RiskLevel.of(verdict.score)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        TextButton(onClick = onBack, modifier = Modifier.padding(vertical = 4.dp)) {
            Text("‹  " + stringResource(R.string.detail_back))
        }

        Spacer(Modifier.height(4.dp))
        ScoreBlock(score = verdict.score, level = level)
        Spacer(Modifier.height(24.dp))

        // --- Identity ---
        Text(
            text = verdict.appLabel,
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = verdict.packageName,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = stringResource(
                if (verdict.signals.any { it.key == "SRC_PLAY" }) R.string.detail_source_play
                else R.string.detail_source_sideload
            ) + " · " + stringResource(
                R.string.detail_installed_at,
                relativeTime(verdict.detectedAt)
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (verdict.overrodeAt > 0L) {
            Spacer(Modifier.height(14.dp))
            Surface(color = bandWarnTint, shape = RoundedCornerShape(14.dp)) {
                Text(
                    text = stringResource(R.string.watch_override, protectedName) +
                        " · " + relativeTime(verdict.overrodeAt),
                    style = MaterialTheme.typography.bodyLarge,
                    color = bandWarn,
                    modifier = Modifier.padding(14.dp)
                )
            }
        }

        Spacer(Modifier.height(24.dp))
        ExplanationStack(verdict = verdict, protectedName = protectedName)

        Spacer(Modifier.height(8.dp))
        TechnicalDetails(verdict, technicalOpen) { technicalOpen = !technicalOpen }

        Spacer(Modifier.height(24.dp))
        Decision(
            verdict = verdict,
            protectedName = protectedName,
            undoable = undoable,
            onMarkUnsafe = onMarkUnsafe,
            onAskMarkSafe = { confirming = true },
            onUndo = onUndo,
            onRequestUninstall = onRequestUninstall
        )
        Spacer(Modifier.height(32.dp))
    }

    if (confirming) {
        DecisionSheet(
            protectedName = protectedName,
            onConfirm = { confirming = false; onMarkSafe() },
            onDismiss = { confirming = false }
        )
    }
}

@Composable
private fun Decision(
    verdict: Verdict,
    protectedName: String,
    undoable: Boolean,
    onMarkUnsafe: () -> Unit,
    onAskMarkSafe: () -> Unit,
    onUndo: () -> Unit,
    onRequestUninstall: () -> Unit
) {
    when (verdict.state) {
        VerdictState.RESOLVED_SAFE -> {
            Text(
                text = stringResource(R.string.resolved_safe),
                style = MaterialTheme.typography.bodyLarge,
                color = bandSafe
            )
            if (undoable) {
                Spacer(Modifier.height(14.dp))
                OutlinedButton(
                    onClick = onUndo,
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text(stringResource(R.string.action_undo))
                }
            }
        }

        VerdictState.RESOLVED_UNSAFE -> {
            Text(
                text = stringResource(R.string.resolved_unsafe, protectedName),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(14.dp))
            OutlinedButton(
                onClick = onRequestUninstall,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text(stringResource(R.string.action_request_uninstall, protectedName))
            }
        }

        else -> {
            // Primary keeps protection, so it needs no confirmation. It is also
            // the only red button in the app outside the overlay — the colour is
            // spent here because this is the consequential tap.
            Button(
                onClick = onMarkUnsafe,
                modifier = Modifier.fillMaxWidth().height(58.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Text(stringResource(R.string.action_mark_unsafe))
            }
            Spacer(Modifier.height(12.dp))
            OutlinedButton(
                onClick = onAskMarkSafe,
                modifier = Modifier.fillMaxWidth().height(58.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(stringResource(R.string.action_mark_safe))
            }
        }
    }
}

/**
 * Closed by default and never part of the main flow. It exists so the guardian
 * can show a technical friend, not so RONDA can look clever.
 */
@Composable
private fun TechnicalDetails(verdict: Verdict, open: Boolean, onToggle: () -> Unit) {
    Column(Modifier.fillMaxWidth()) {
        Text(
            text = (if (open) "− " else "+ ") + stringResource(R.string.detail_technical),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggle)
                .padding(vertical = 14.dp)
        )
        AnimatedVisibility(visible = open) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Label(R.string.detail_technical_vector)
                    Mono(verdict.vector)

                    Spacer(Modifier.height(12.dp))
                    Mono(
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
                        Mono("${it.key}  ${it.attackId}")
                    }
                }
            }
        }
    }
}

@Composable
private fun Label(res: Int) {
    Text(
        text = stringResource(res),
        style = Eyebrow,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(Modifier.height(4.dp))
}

@Composable
private fun Mono(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface
    )
}
