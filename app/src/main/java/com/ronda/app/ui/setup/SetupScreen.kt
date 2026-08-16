package com.ronda.app.ui.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ronda.app.R
import com.ronda.app.ui.components.RondaWordmark
import com.ronda.app.ui.theme.ProtectedBody
import com.ronda.app.ui.theme.ProtectedSubtitle
import com.ronda.app.ui.theme.bandSafe
import com.ronda.app.ui.theme.bandSafeTint
import com.ronda.app.ui.theme.bandWarn
import com.ronda.app.ui.theme.bandWarnTint

/**
 * What RONDA still needs before it can protect this phone — and, once it has
 * everything, the protected phone's home screen.
 *
 * That dual role is why the status card is the largest thing here. On most days
 * nothing needs doing, and the screen has exactly one job: tell a seventy-year-old
 * that they are safe, in a sentence, without them having to interpret a checklist.
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
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        RondaWordmark(height = 30.dp)

        Spacer(Modifier.height(20.dp))

        StatusBanner(isProtected = status.isFullyProtected)

        Spacer(Modifier.height(24.dp))

        PermissionRow(
            title = stringResource(R.string.permission_notifications_title),
            why = stringResource(R.string.permission_notifications_why),
            granted = status.notifications,
            onEnable = onRequestNotifications
        )

        Spacer(Modifier.height(14.dp))

        PermissionRow(
            title = stringResource(R.string.permission_overlay_title),
            why = stringResource(R.string.permission_overlay_why),
            granted = status.overlay,
            onEnable = onOpenOverlaySettings
        )

        Spacer(Modifier.height(14.dp))

        PermissionRow(
            title = stringResource(R.string.permission_usage_title),
            why = stringResource(R.string.permission_usage_why),
            granted = status.usageStats,
            onEnable = onOpenUsageSettings
        )

        Spacer(Modifier.height(24.dp))

        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.setup_privacy_note),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(18.dp)
            )
        }
    }
}

@Composable
private fun StatusBanner(isProtected: Boolean) {
    val accent = if (isProtected) bandSafe else bandWarn
    val tint = if (isProtected) bandSafeTint else bandWarnTint

    Surface(
        color = tint,
        shape = RoundedCornerShape(22.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, accent.copy(alpha = 0.22f), RoundedCornerShape(22.dp))
    ) {
        Column(Modifier.padding(22.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(52.dp).background(accent, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isProtected) "✓" else "!",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.surface
                    )
                }
                Spacer(Modifier.width(16.dp))
                Text(
                    text = stringResource(
                        if (isProtected) R.string.setup_status_active
                        else R.string.setup_status_incomplete
                    ),
                    style = ProtectedSubtitle,
                    color = accent,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(14.dp))
            Text(
                text = stringResource(
                    if (isProtected) R.string.setup_status_active_detail
                    else R.string.setup_status_incomplete_detail
                ),
                style = ProtectedBody,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * One permission.
 *
 * A granted row keeps its explanation rather than collapsing to a tick. This
 * screen is re-read weeks later when a system update has silently revoked
 * something, and "why did I turn this on?" needs to still be answerable then.
 */
@Composable
private fun PermissionRow(
    title: String,
    why: String,
    granted: Boolean,
    onEnable: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(18.dp))
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusDot(granted)
                Spacer(Modifier.width(12.dp))
                Text(
                    text = title,
                    style = ProtectedSubtitle,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(10.dp))
            Text(
                text = why,
                style = ProtectedBody,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(16.dp))
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
                    style = ProtectedBody,
                    fontWeight = FontWeight.Bold,
                    color = if (granted) bandSafe else bandWarn
                )
                if (!granted) {
                    // 56dp: a hand that shakes needs a target it cannot miss.
                    Button(
                        onClick = onEnable,
                        modifier = Modifier.height(56.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.permission_enable),
                            fontSize = 18.sp,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

/** Shape as well as colour, so the state survives a colour-blind reader. */
@Composable
private fun StatusDot(granted: Boolean) {
    val accent = if (granted) bandSafe else bandWarn
    Box(
        modifier = Modifier
            .size(32.dp)
            .background(if (granted) bandSafeTint else bandWarnTint, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (granted) "✓" else "!",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = accent
        )
    }
}

data class SetupStatus(
    val notifications: Boolean,
    val overlay: Boolean,
    val usageStats: Boolean
) {
    val isFullyProtected: Boolean get() = notifications && overlay && usageStats
}
