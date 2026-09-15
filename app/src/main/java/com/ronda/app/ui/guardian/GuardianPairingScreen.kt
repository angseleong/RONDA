package com.ronda.app.ui.guardian

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ronda.app.R
import com.ronda.app.pairing.PairingRepository
import com.ronda.app.pairing.QrCodeUtils
import com.ronda.app.ui.theme.PairingCode
import com.ronda.app.ui.theme.lamp
import com.ronda.app.ui.theme.linenDim
import com.ronda.app.ui.theme.nightRaised

/**
 * Guardian side of pairing: publish a code and wait for the other phone.
 *
 * The code is shown as large text *and* as a QR. The text is the one that
 * actually gets used — pairing usually happens over a phone call, with the
 * guardian in another city, which is exactly the situation RONDA exists for.
 */
@Composable
fun GuardianPairingScreen(
    guardianDeviceId: String,
    onPaired: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val repository = remember { PairingRepository() }
    var code by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(guardianDeviceId) {
        val newCode = QrCodeUtils.newPairingCode()
        runCatching { repository.createPairing(newCode, guardianDeviceId) }
            .onSuccess { code = newCode }
            .onFailure { error = it.message }
    }

    LaunchedEffect(code) {
        val current = code ?: return@LaunchedEffect
        repository.observePairing(current).collect { pairing ->
            if (pairing?.isActive == true) onPaired(current)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.pair_guardian_title),
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.pair_guardian_step),
            style = MaterialTheme.typography.bodyLarge,
            color = linenDim,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(28.dp))

        val currentCode = code
        when {
            error != null -> Text(
                text = error.orEmpty(),
                style = MaterialTheme.typography.bodyLarge,
                color = lamp,
                modifier = Modifier.fillMaxWidth()
            )

            currentCode == null -> Waiting(stringResource(R.string.pair_creating))

            else -> PairingCode(currentCode)
        }
    }
}

@Composable
private fun PairingCode(code: String) {
    // 640px is regenerated only when the code changes, so this never runs per frame.
    val qr = remember(code) { QrCodeUtils.qrBitmap(code) }

    Surface(
        color = nightRaised,
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.pair_guardian_code_label),
                style = MaterialTheme.typography.bodyMedium,
                color = linenDim
            )
            Spacer(Modifier.height(8.dp))
            // Long-press to copy: the code is as often pasted into a chat as
            // read down the phone.
            SelectionContainer {
                Text(
                    text = code,
                    style = PairingCode,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }

    Spacer(Modifier.height(24.dp))

    Text(
        text = stringResource(R.string.pair_guardian_or_scan),
        style = MaterialTheme.typography.bodyMedium,
        color = linenDim,
        textAlign = TextAlign.Center
    )

    Spacer(Modifier.height(12.dp))

    // White plate behind the QR: in dark theme the quiet zone would otherwise
    // blend into the background and scanners would fail to find the code.
    Surface(color = Color.White, shape = MaterialTheme.shapes.medium) {
        Image(
            bitmap = qr,
            contentDescription = stringResource(R.string.pair_qr_desc),
            modifier = Modifier
                .padding(12.dp)
                .size(220.dp)
        )
    }

    Spacer(Modifier.height(28.dp))

    Waiting(stringResource(R.string.pair_guardian_waiting))

    Spacer(Modifier.height(6.dp))

    Text(
        text = stringResource(R.string.pair_guardian_expiry),
        style = MaterialTheme.typography.bodyMedium,
        color = linenDim
    )
}

/** A small spinner beside its sentence, rather than a large one above it. */
@Composable
private fun Waiting(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        CircularProgressIndicator(
            modifier = Modifier.size(22.dp),
            color = lamp,
            strokeWidth = 2.5.dp
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
