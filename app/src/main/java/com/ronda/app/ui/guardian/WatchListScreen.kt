package com.ronda.app.ui.guardian

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ronda.app.R
import com.ronda.app.core.Verdict
import com.ronda.app.ui.guardian.components.AlertRow
import com.ronda.app.ui.guardian.components.StatusHero
import com.ronda.app.ui.theme.Eyebrow
import com.ronda.app.ui.theme.bandSafe
import com.ronda.app.ui.theme.bandWarn

/**
 * Home. A status card that answers the question, then two tabs split on the
 * alert threshold: apps that need a decision, and apps RONDA has seen and
 * cleared.
 *
 * The "Terpantau" tab is not filler — it is the evidence that RONDA does not
 * flag every sideloaded app. A guardian who can see the quiet ones trusts the
 * loud ones.
 *
 * Below both lists sits "Riwayat": everything the guardian has already ruled on.
 * It hangs under either tab on purpose — "what did I decide about that app?" is
 * a question that arrives without regard for which list is on screen.
 */
@Composable
fun WatchListScreen(
    state: GuardianUiState,
    onVerdictClick: (Verdict) -> Unit,
    modifier: Modifier = Modifier
) {
    var tab by rememberSaveable { mutableIntStateOf(0) }
    val list = if (tab == 0) state.needsReview else state.monitored

    Column(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Header(connected = state.connected)

        LazyColumn(
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                StatusHero(
                    connected = state.connected,
                    protectedName = state.protectedName,
                    reviewCount = state.needsReview.size,
                    monitoredCount = state.monitored.size,
                    historyCount = state.history.size
                )
            }

            item {
                SegmentedTabs(
                    selected = tab,
                    reviewCount = state.needsReview.size,
                    monitoredCount = state.monitored.size,
                    onSelect = { tab = it },
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            if (list.isEmpty()) {
                item {
                    EmptyState(
                        text = stringResource(
                            if (tab == 0) R.string.watch_empty_review
                            else R.string.watch_empty_monitored,
                            state.protectedName
                        )
                    )
                }
            } else {
                items(list, key = { "open:${it.packageName}" }) { verdict ->
                    AlertRow(
                        verdict = verdict,
                        protectedName = state.protectedName,
                        onClick = { onVerdictClick(verdict) }
                    )
                }
            }

            if (state.history.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.watch_history_header),
                        style = Eyebrow,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 16.dp, bottom = 2.dp)
                    )
                }
                items(state.history, key = { "history:${it.packageName}" }) { verdict ->
                    AlertRow(
                        verdict = verdict,
                        protectedName = state.protectedName,
                        onClick = { onVerdictClick(verdict) }
                    )
                }
            }
        }
    }
}

/**
 * Wordmark and connection state.
 *
 * The connection is stated on every visit rather than only on failure: an empty
 * list means something very different when RONDA cannot see the other phone.
 */
@Composable
private fun Header(connected: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.watch_title),
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )

        val tone = if (connected) bandSafe else bandWarn
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(999.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.size(8.dp).background(tone, CircleShape))
                Spacer(Modifier.width(7.dp))
                Text(
                    text = stringResource(
                        if (connected) R.string.watch_chip_connected
                        else R.string.watch_chip_disconnected
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = tone
                )
            }
        }
    }
}

/**
 * Two segments with their counts.
 *
 * Material's `TabRow` puts a hairline indicator under a label; at a glance the
 * selected and unselected tabs look the same. A filled pill is unambiguous from
 * arm's length, which is the distance this app is actually read from.
 */
@Composable
private fun SegmentedTabs(
    selected: Int,
    reviewCount: Int,
    monitoredCount: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(14.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(4.dp)) {
            Segment(
                label = stringResource(R.string.watch_tab_review),
                count = reviewCount,
                selected = selected == 0,
                onClick = { onSelect(0) },
                modifier = Modifier.weight(1f)
            )
            Segment(
                label = stringResource(R.string.watch_tab_monitored),
                count = monitoredCount,
                selected = selected == 1,
                onClick = { onSelect(1) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun Segment(
    label: String,
    count: Int,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (selected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(10.dp),
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = "$label  $count",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            color = if (selected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        )
    }
}

@Composable
private fun EmptyState(text: String) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(24.dp)) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
        }
    }
}
