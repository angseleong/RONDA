package com.ronda.app.ui.guardian

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ronda.app.R
import com.ronda.app.alert.Alert
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * What the guardian sees between incidents: confirmation that RONDA is watching,
 * and the history of what it has caught.
 */
@Composable
fun GuardianHomeScreen(
    alerts: List<Alert>,
    onAlertClick: (Alert) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = stringResource(R.string.guardian_home_title),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Text(
                text = stringResource(R.string.guardian_home_connected),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(20.dp)
            )
        }

        Spacer(Modifier.height(28.dp))

        if (alerts.isEmpty()) {
            Text(
                text = stringResource(R.string.guardian_home_empty_title),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.guardian_home_empty_body),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            return@Column
        }

        Text(
            text = stringResource(R.string.guardian_home_alerts),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(12.dp))

        alerts.forEach { alert ->
            AlertRow(alert = alert, onClick = { onAlertClick(alert) })
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun AlertRow(alert: Alert, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(
                text = alert.appLabel.ifBlank { alert.packageName },
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = formatTimestamp(alert.timestamp),
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    if (timestamp <= 0L) return "-"
    val format = SimpleDateFormat("d MMM yyyy, HH:mm", Locale.forLanguageTag("id-ID"))
    return format.format(Date(timestamp))
}
