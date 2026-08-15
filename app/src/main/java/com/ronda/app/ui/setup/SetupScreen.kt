package com.ronda.app.ui.setup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ronda.app.R

/**
 * What RONDA still needs before it can protect this phone.
 *
 * Two of these permissions cannot be granted from a dialog — they only exist in
 * system Settings, so each row explains why it is needed and opens the right
 * screen. Rechecked on every resume, because OEM power management revokes them.
 */
@Composable
fun SetupScreen(
    status: SetupStatus,
    onRequestNotifications: () -> Unit,
    onOpenOverlaySettings: () -> Unit,
    onOpenUsageSettings: () -> Unit,
    onOpenBatterySettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = stringResource(R.string.setup_title),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(16.dp))

        StatusBanner(isProtected = status.isFullyProtected)

        Spacer(Modifier.height(24.dp))

        PermissionRow(
            title = stringResource(R.string.permission_notifications_title),
            why = stringResource(R.string.permission_notifications_why),
            granted = status.notifications,
            onEnable = onRequestNotifications
        )

        Spacer(Modifier.height(12.dp))

        PermissionRow(
            title = stringResource(R.string.permission_overlay_title),
            why = stringResource(R.string.permission_overlay_why),
            granted = status.overlay,
            onEnable = onOpenOverlaySettings
        )

        Spacer(Modifier.height(12.dp))

        PermissionRow(
            title = stringResource(R.string.permission_usage_title),
            why = stringResource(R.string.permission_usage_why),
            granted = status.usageStats,
            onEnable = onOpenUsageSettings
        )

        Spacer(Modifier.height(12.dp))

        PermissionRow(
            title = stringResource(R.string.permission_battery_title),
            why = stringResource(R.string.permission_battery_why),
            granted = status.batteryExemption,
            onEnable = onOpenBatterySettings
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.setup_privacy_note),
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun StatusBanner(isProtected: Boolean) {
    val containerColor =
        if (isProtected) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.errorContainer
    val contentColor =
        if (isProtected) MaterialTheme.colorScheme.onPrimaryContainer
        else MaterialTheme.colorScheme.onErrorContainer

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(
                text = stringResource(
                    if (isProtected) R.string.setup_status_active
                    else R.string.setup_status_incomplete
                ),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = stringResource(
                    if (isProtected) R.string.setup_status_active_detail
                    else R.string.setup_status_incomplete_detail
                ),
                fontSize = 17.sp,
                color = contentColor
            )
        }
    }
}

@Composable
private fun PermissionRow(
    title: String,
    why: String,
    granted: Boolean,
    onEnable: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp)) {
            Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text(
                text = why,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(
                        if (granted) R.string.permission_granted
                        else R.string.permission_missing
                    ),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (granted) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error
                )
                if (!granted) {
                    Button(onClick = onEnable) {
                        Text(stringResource(R.string.permission_enable), fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

data class SetupStatus(
    val notifications: Boolean,
    val overlay: Boolean,
    val usageStats: Boolean,
    val batteryExemption: Boolean
) {
    val isFullyProtected: Boolean get() = notifications && overlay && usageStats && batteryExemption
}
