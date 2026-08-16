package com.ronda.app.ui.guardian

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ronda.app.R
import com.ronda.app.core.Verdict
import com.ronda.app.ui.guardian.components.AlertRow
import com.ronda.app.ui.theme.calm
import com.ronda.app.ui.theme.lamp
import com.ronda.app.ui.theme.linenDim

/**
 * Home. Two tabs split on the alert threshold: apps that need a decision, and
 * apps RONDA has seen and cleared.
 *
 * The "Terpantau" tab is not filler — it is the evidence that RONDA does not
 * flag every sideloaded app. A guardian who can see the quiet ones trusts the
 * loud ones.
 */
@Composable
fun WatchListScreen(
    state: GuardianUiState,
    onVerdictClick: (Verdict) -> Unit,
    modifier: Modifier = Modifier
) {
    var tab by rememberSaveable { mutableIntStateOf(0) }
    val list = if (tab == 0) state.needsReview else state.monitored

    Column(modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.watch_title),
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(start = 20.dp, top = 20.dp, end = 20.dp)
        )

        // An empty list means something very different when disconnected, so
        // the connection is stated on every visit rather than only on failure.
        Text(
            text = if (state.connected) {
                stringResource(R.string.watch_connected, state.protectedName)
            } else {
                stringResource(R.string.watch_disconnected, state.protectedName)
            },
            style = MaterialTheme.typography.bodyMedium,
            color = if (state.connected) calm else lamp,
            modifier = Modifier.padding(start = 20.dp, top = 4.dp, end = 20.dp, bottom = 12.dp)
        )

        TabRow(selectedTabIndex = tab, containerColor = MaterialTheme.colorScheme.background) {
            Tab(
                selected = tab == 0,
                onClick = { tab = 0 },
                text = { Text(stringResource(R.string.watch_tab_review)) }
            )
            Tab(
                selected = tab == 1,
                onClick = { tab = 1 },
                text = { Text(stringResource(R.string.watch_tab_monitored)) }
            )
        }

        if (list.isEmpty()) {
            Text(
                text = stringResource(
                    if (tab == 0) R.string.watch_empty_review else R.string.watch_empty_monitored,
                    state.protectedName
                ),
                style = MaterialTheme.typography.bodyLarge,
                color = linenDim,
                modifier = Modifier.padding(20.dp)
            )
            return@Column
        }

        LazyColumn(
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(list, key = { it.packageName }) { verdict ->
                AlertRow(
                    verdict = verdict,
                    protectedName = state.protectedName,
                    onClick = { onVerdictClick(verdict) }
                )
            }
            item { Spacer(Modifier.height(8.dp)) }
        }
    }
}
