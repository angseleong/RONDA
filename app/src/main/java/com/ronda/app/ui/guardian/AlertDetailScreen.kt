package com.ronda.app.ui.guardian

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ronda.app.R
import com.ronda.app.alert.Alert
import com.ronda.app.alert.Command

/**
 * The decision screen. The guardian sees exactly what RONDA saw and picks one
 * of two outcomes.
 *
 * The evidence is spelled out rather than reduced to a risk score, because the
 * guardian may well be the only person in the family who can judge whether an
 * app their parent just installed is legitimate.
 *
 * The buttons write to `commands/{pairingId}`; the protected phone acts on them.
 *
 * @param pendingAction the action already sent and not yet confirmed by the
 *   protected phone, or null. Uninstall in particular takes as long as it takes
 *   the person holding that phone to tap through the system dialog, and the
 *   guardian is owed an honest "waiting" rather than a button that looks unpressed.
 */
@Composable
fun AlertDetailScreen(
    alert: Alert,
    pendingAction: String?,
    onUninstall: () -> Unit,
    onMarkSafe: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        TextButton(onClick = onBack) {
            Text(stringResource(R.string.alert_back), fontSize = 16.sp)
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.alert_detail_title),
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.error
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = alert.appLabel.ifBlank { alert.packageName },
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(24.dp))

        EvidenceRow(
            label = stringResource(R.string.alert_detail_source),
            value = alert.installSource
        )
        EvidenceRow(
            label = stringResource(R.string.alert_detail_permissions),
            value = alert.flaggedPermissions
                .joinToString(", ") { it.substringAfterLast('.') }
                .ifBlank { "-" }
        )
        EvidenceRow(
            label = stringResource(R.string.alert_detail_package),
            value = alert.packageName
        )

        Spacer(Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Column(Modifier.padding(20.dp)) {
                Text(
                    text = stringResource(R.string.alert_detail_why),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.alert_detail_why_body),
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.alert_detail_blocked),
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(28.dp))

        when {
            // The protected phone has reported a real outcome — nothing left to decide.
            alert.status != Alert.STATUS_PENDING -> OutcomeBanner(alert.status)

            pendingAction != null -> WaitingBanner(pendingAction)

            else -> {
                Button(
                    onClick = onUninstall,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Text(stringResource(R.string.alert_action_uninstall), fontSize = 18.sp)
                }

                Spacer(Modifier.height(12.dp))

                OutlinedButton(onClick = onMarkSafe, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.alert_action_safe), fontSize = 18.sp)
                }
            }
        }
    }
}

@Composable
private fun OutcomeBanner(status: String) {
    val isSafe = status == Alert.STATUS_SAFE
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSafe) MaterialTheme.colorScheme.surfaceVariant
            else MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Text(
            text = stringResource(
                if (isSafe) R.string.alert_outcome_safe else R.string.alert_outcome_uninstalled
            ),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(20.dp)
        )
    }
}

@Composable
private fun WaitingBanner(pendingAction: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(
                text = stringResource(
                    if (pendingAction == Command.ACTION_UNINSTALL) {
                        R.string.alert_waiting_uninstall
                    } else {
                        R.string.alert_waiting_safe
                    }
                ),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.alert_waiting_detail),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
private fun EvidenceRow(label: String, value: String) {
    Column(Modifier.padding(bottom = 14.dp)) {
        Text(
            text = label,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(2.dp))
        Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}
