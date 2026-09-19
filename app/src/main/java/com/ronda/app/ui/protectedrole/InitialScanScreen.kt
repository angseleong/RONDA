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
import com.ronda.app.ui.components.IconBox
import com.ronda.app.ui.components.RondaIcons
import com.ronda.app.ui.components.TactileButton
import com.ronda.app.ui.components.Wordmark
import com.ronda.app.ui.components.screenInsets
import com.ronda.app.ui.theme.LargePrint
import com.ronda.app.ui.theme.LargePrintTitle
import com.ronda.app.ui.theme.RondaTheme
import com.ronda.app.ui.theme.Tone
import kotlinx.coroutines.delay

@Composable
fun InitialScanScreen(
    onScan: () -> Unit,
    onComplete: () -> Unit
) {
    val colors = RondaTheme.colors
    var scanning by remember { mutableStateOf(false) }

    LaunchedEffect(scanning) {
        if (scanning) {
            onScan()
            // Simulate scanning progress for UX
            delay(3000)
            onComplete()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .screenInsets()
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        Wordmark()

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
