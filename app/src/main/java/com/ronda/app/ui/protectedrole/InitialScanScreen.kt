package com.ronda.app.ui.protectedrole

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ronda.app.R
import com.ronda.app.detection.ScanEvents
import com.ronda.app.ui.components.IconBox
import com.ronda.app.ui.components.RondaIcons
import com.ronda.app.ui.components.TactileButton
import com.ronda.app.ui.components.WordmarkBar
import com.ronda.app.ui.components.screenInsets
import com.ronda.app.ui.theme.LargePrint
import com.ronda.app.ui.theme.LargePrintTitle
import com.ronda.app.ui.theme.RondaTheme
import com.ronda.app.ui.theme.Tone
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull

@Composable
fun InitialScanScreen(
    onScan: () -> Unit,
    onComplete: () -> Unit,
    /** Undoes the pairing. Offered until the scan starts, never during it. */
    onBack: (() -> Unit)? = null
) {
    val colors = RondaTheme.colors
    var scanning by remember { mutableStateOf(false) }

    LaunchedEffect(scanning) {
        if (!scanning) return@LaunchedEffect
        val startedAt = System.currentTimeMillis()
        coroutineScope {
            // Listening before the scan is asked for: the result is a one-off
            // event, and a fast scan could otherwise finish unheard.
            val result = async(start = CoroutineStart.UNDISPATCHED) {
                withTimeoutOrNull(SCAN_TIMEOUT_MS) { ScanEvents.results.first() }
            }
            onScan()
            result.await()
        }
        // A phone with few apps finishes instantly; a screen that flashes past
        // reads as "nothing happened" rather than "all checked".
        val elapsed = System.currentTimeMillis() - startedAt
        if (elapsed < MIN_VISIBLE_MS) delay(MIN_VISIBLE_MS - elapsed)
        onComplete()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .screenInsets()
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        WordmarkBar(onBack.takeUnless { scanning })

        Spacer(Modifier.height(36.dp))
        IconBox(
            icon = RondaIcons.activity,
            tone = Tone.TRUST,
            size = 56.dp
        )
        Spacer(Modifier.height(20.dp))
        Text(
            text = stringResource(R.string.initial_scan_title),
            style = LargePrintTitle,
            color = colors.textPrimary
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.initial_scan_body),
            style = LargePrint,
            color = colors.textSecondary
        )

        Spacer(Modifier.weight(1f))

        TactileButton(
            text = stringResource(
                if (scanning) R.string.initial_scan_scanning else R.string.initial_scan_action
            ),
            onClick = { scanning = true },
            enabled = !scanning,
            loading = scanning,
            icon = RondaIcons.shieldCheck,
            large = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

private const val MIN_VISIBLE_MS = 1_800L

/** Past this the screen moves on anyway; the scan keeps running in the service. */
private const val SCAN_TIMEOUT_MS = 30_000L
