package com.ronda.app.ui.setup

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ronda.app.R
import com.ronda.app.ui.theme.LargePrint
import com.ronda.app.ui.theme.LargePrintLabel
import com.ronda.app.ui.theme.calm
import com.ronda.app.ui.theme.lamp
import com.ronda.app.ui.theme.nightRaised
import com.ronda.app.ui.theme.seam
import com.ronda.app.ui.theme.wash

/**
 * What RONDA still needs before it can protect this phone.
 *
 * Two of these permissions cannot be granted from a dialog — they only exist in
 * system Settings, so each row explains why it is needed and opens the right
 * screen. Rechecked on every resume, because OEM power management revokes them.
 *
 * Colour discipline: a missing permission is amber, not red. Red is reserved
 * for a DARURAT score across the whole app, and four red rows on the home
 * screen would teach the eye to ignore it.
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
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Text(
            text = stringResource(R.string.setup_title),
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onBackground
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

        Spacer(Modifier.height(28.dp))
        HorizontalDivider(color = seam)
        Spacer(Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.setup_privacy_note),
            style = LargePrint,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun StatusBanner(isProtected: Boolean) {
    // The one state change on this screen happens off-screen, in Settings, and
    // lands on resume. A crossfade is what tells the eye the trip worked.
    val hue by animateColorAsState(
        targetValue = if (isProtected) calm else lamp,
        label = "setupStatus"
    )

    Surface(
        color = wash(hue),
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(
                text = stringResource(
                    if (isProtected) R.string.setup_status_active
                    else R.string.setup_status_incomplete
                ),
                style = MaterialTheme.typography.headlineMedium,
                color = hue
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(
                    if (isProtected) R.string.setup_status_active_detail
                    else R.string.setup_status_incomplete_detail
                ),
                style = LargePrint,
                color = MaterialTheme.colorScheme.onSurface
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
    val hue = if (granted) calm else lamp

    Surface(
        color = nightRaised,
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = why,
                style = LargePrint,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(10.dp)
                            .background(hue, CircleShape)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = stringResource(
                            if (granted) R.string.permission_granted
                            else R.string.permission_missing
                        ),
                        style = LargePrintLabel,
                        color = hue
                    )
                }
                if (!granted) {
                    Button(
                        onClick = onEnable,
                        modifier = Modifier.heightIn(min = 48.dp)
                    ) {
                        Text(stringResource(R.string.permission_enable), style = LargePrintLabel)
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
