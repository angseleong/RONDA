package com.ronda.app.ui.guardian

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ronda.app.R
import com.ronda.app.pairing.PairingRepository
import com.ronda.app.pairing.QrCodeUtils

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
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.pair_guardian_title),
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(10.dp))

        Text(
            text = stringResource(R.string.pair_guardian_step),
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(24.dp))

        val currentCode = code
        when {
            error != null -> Text(
                text = error.orEmpty(),
                fontSize = 17.sp,
                color = MaterialTheme.colorScheme.error
            )

            currentCode == null -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                Spacer(Modifier.height(12.dp))
                Text(stringResource(R.string.pair_creating), fontSize = 16.sp)
            }

            else -> PairingCode(currentCode)
        }
    }
}

@Composable
private fun PairingCode(code: String) {
    // 640px is regenerated only when the code changes, so this never runs per frame.
    val qr = remember(code) { QrCodeUtils.qrBitmap(code) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.pair_guardian_code_label),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = code,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                // Monospace with wide tracking so 6 characters can be read aloud
                // one by one without ambiguity.
                fontFamily = FontFamily.Monospace,
                letterSpacing = 8.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }

    Spacer(Modifier.height(24.dp))

    Text(
        text = stringResource(R.string.pair_guardian_or_scan),
        fontSize = 15.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Spacer(Modifier.height(12.dp))

    // White plate behind the QR: in dark theme the quiet zone would otherwise
    // blend into the background and scanners would fail to find the code.
    Column(
        modifier = Modifier
            .background(Color.White)
            .padding(12.dp)
    ) {
        Image(
            bitmap = qr,
            contentDescription = stringResource(R.string.pair_qr_desc),
            modifier = Modifier.size(220.dp)
        )
    }

    Spacer(Modifier.height(28.dp))

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Spacer(Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.pair_guardian_waiting),
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.pair_guardian_expiry),
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
