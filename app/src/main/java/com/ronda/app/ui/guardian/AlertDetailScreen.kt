package com.ronda.app.ui.guardian

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.LocalContentColor
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.ronda.app.R
import com.ronda.app.core.RiskLevel
import com.ronda.app.core.Verdict
import com.ronda.app.core.VerdictState
import com.ronda.app.ui.guardian.components.ExplanationStack
import com.ronda.app.ui.guardian.components.ScoreBlock
import com.ronda.app.ui.guardian.components.relativeTime
import com.ronda.app.ui.theme.Eyebrow
import com.ronda.app.ui.theme.calm
import com.ronda.app.ui.theme.lamp
import com.ronda.app.ui.theme.linenDim
import com.ronda.app.ui.theme.nightRaised
import com.ronda.app.ui.theme.wash

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
    // System Back returns to the list. Without this it would leave the app,
    // since the list and the detail are the same Activity.
    BackHandler(onBack = onBack)

    var confirming by remember { mutableStateOf(false) }
    var technicalOpen by rememberSaveable { mutableStateOf(false) }
    // Feedback only. The request itself is fire-and-forget on the repository,
    // and a second one later does no harm.
    var uninstallRequested by rememberSaveable(verdict.packageName) { mutableStateOf(false) }
    val level = RiskLevel.of(verdict.score)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(8.dp))
        BackButton(onBack)

        Spacer(Modifier.height(8.dp))
        ScoreBlock(score = verdict.score, level = level)
        Spacer(Modifier.height(28.dp))

        // --- Identity ---
        Text(
            text = verdict.appLabel,
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = verdict.packageName,
            style = MaterialTheme.typography.bodyMedium,
            color = linenDim
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(
                if (verdict.signals.any { it.key == "SRC_PLAY" }) R.string.detail_source_play
                else R.string.detail_source_sideload
            ) + " · " + stringResource(
                R.string.detail_installed_at,
                relativeTime(verdict.detectedAt)
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = linenDim
        )

        if (verdict.overrodeAt > 0L) {
            Spacer(Modifier.height(12.dp))
            Surface(color = wash(lamp), shape = MaterialTheme.shapes.medium) {
                Text(
                    text = stringResource(R.string.watch_override, protectedName) +
                        " · " + relativeTime(verdict.overrodeAt),
                    style = MaterialTheme.typography.bodyLarge,
                    color = lamp,
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
            uninstallRequested = uninstallRequested,
            onMarkUnsafe = onMarkUnsafe,
            onAskMarkSafe = { confirming = true },
            onUndo = onUndo,
            onRequestUninstall = {
                uninstallRequested = true
                onRequestUninstall()
            }
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
    uninstallRequested: Boolean,
    onMarkUnsafe: () -> Unit,
    onAskMarkSafe: () -> Unit,
    onUndo: () -> Unit,
    onRequestUninstall: () -> Unit
) {
    val wide = Modifier
        .fillMaxWidth()
        .heightIn(min = 48.dp)

    when (verdict.state) {
        VerdictState.RESOLVED_SAFE -> {
            Text(
                text = stringResource(R.string.resolved_safe),
                style = MaterialTheme.typography.bodyLarge,
                color = calm
            )
            if (undoable) {
                Spacer(Modifier.height(12.dp))
                OutlinedButton(onClick = onUndo, modifier = wide) {
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
            Spacer(Modifier.height(12.dp))
            if (uninstallRequested) {
                Text(
                    text = stringResource(R.string.alert_waiting_uninstall),
                    style = MaterialTheme.typography.titleMedium,
                    color = calm
                )
            } else {
                OutlinedButton(onClick = onRequestUninstall, modifier = wide) {
                    Text(stringResource(R.string.action_request_uninstall, protectedName))
                }
            }
        }

        else -> {
            // Primary keeps protection, so it needs no confirmation.
            Button(onClick = onMarkUnsafe, modifier = wide) {
                Text(stringResource(R.string.action_mark_unsafe))
            }
            Spacer(Modifier.height(10.dp))
            OutlinedButton(onClick = onAskMarkSafe, modifier = wide) {
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
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .clickable(onClick = onToggle, role = Role.Button)
                .padding(vertical = 12.dp)
        ) {
            Text(
                text = stringResource(R.string.detail_technical),
                style = MaterialTheme.typography.bodyMedium,
                color = linenDim,
                modifier = Modifier.weight(1f)
            )
            Chevron(open = open, color = linenDim, modifier = Modifier.size(16.dp))
        }
        AnimatedVisibility(visible = open) {
            Surface(color = nightRaised, shape = MaterialTheme.shapes.medium) {
                Column(Modifier.padding(16.dp)) {
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
}

@Composable
private fun Label(res: Int) {
    Text(text = stringResource(res), style = Eyebrow, color = linenDim)
    Spacer(Modifier.height(4.dp))
}

/** One line of figures. Tabular numerals so the columns hold still. */
@Composable
private fun Datum(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium.copy(fontFeatureSettings = "tnum"),
        color = MaterialTheme.colorScheme.onSurface
    )
}

/**
 * A text button with a drawn arrow, pulled 8dp left so the arrow sits on the
 * same 20dp gutter as everything below it instead of floating in from it.
 */
@Composable
private fun BackButton(onBack: () -> Unit) {
    TextButton(
        onClick = onBack,
        contentPadding = PaddingValues(start = 8.dp, top = 8.dp, end = 16.dp, bottom = 8.dp),
        modifier = Modifier
            .offset(x = (-8).dp)
            .heightIn(min = 48.dp)
    ) {
        BackArrow(Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Text(stringResource(R.string.detail_back))
    }
}

/** Drawn, not a glyph: one 2dp round stroke, the same weight as the chevron. */
@Composable
private fun BackArrow(modifier: Modifier = Modifier) {
    val color = LocalContentColor.current
    Canvas(modifier) {
        val stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        val inset = stroke.width / 2f
        val midY = size.height / 2f
        val head = size.width * 0.42f
        drawLine(
            color = color,
            start = Offset(size.width - inset, midY),
            end = Offset(inset, midY),
            strokeWidth = stroke.width,
            cap = StrokeCap.Round
        )
        drawPath(
            path = Path().apply {
                moveTo(inset + head, midY - head)
                lineTo(inset, midY)
                lineTo(inset + head, midY + head)
            },
            color = color,
            style = stroke
        )
    }
}

/** Points down when closed, up when open; the turn is the only motion here. */
@Composable
private fun Chevron(open: Boolean, color: Color, modifier: Modifier = Modifier) {
    val rotation by animateFloatAsState(
        targetValue = if (open) 180f else 0f,
        label = "chevron"
    )
    Canvas(modifier.graphicsLayer { rotationZ = rotation }) {
        val stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        val inset = stroke.width
        drawPath(
            path = Path().apply {
                moveTo(inset, size.height * 0.35f)
                lineTo(size.width / 2f, size.height * 0.65f)
                lineTo(size.width - inset, size.height * 0.35f)
            },
            color = color,
            style = stroke
        )
    }
}
