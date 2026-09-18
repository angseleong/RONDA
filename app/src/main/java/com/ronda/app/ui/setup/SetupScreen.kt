package com.ronda.app.ui.setup

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ronda.app.R
import com.ronda.app.ui.components.BackTopBar
import com.ronda.app.ui.components.IconBox
import com.ronda.app.ui.components.RondaCard
import com.ronda.app.ui.components.RondaIcon
import com.ronda.app.ui.components.RondaIcons
import com.ronda.app.ui.components.SegmentedProgress
import com.ronda.app.ui.components.TactileButton
import com.ronda.app.ui.components.screenInsets
import com.ronda.app.ui.theme.LargePrint
import com.ronda.app.ui.theme.LargePrintTitle
import com.ronda.app.ui.theme.RondaTheme
import com.ronda.app.ui.theme.Tone

data class SetupStatus(
    val notifications: Boolean,
    val overlay: Boolean,
    val usageStats: Boolean,
    val batteryExemption: Boolean
) {
    val isFullyProtected: Boolean get() = notifications && overlay && usageStats && batteryExemption

    val grantedCount: Int
        get() = listOf(notifications, overlay, usageStats, batteryExemption).count { it }

    companion object {
        const val TOTAL = 4
    }
}

/**
 * What RONDA still needs before it can protect this phone, one permission per
 * screen (PRD: at most one decision on a protected screen).
 *
 * Two of these cannot be granted from a dialog — they only exist in system
 * Settings — so each step explains why it is needed and opens the right page.
 * Coming back from Settings re-checks everything, the segment fills green and
 * the next step slides in. System Back returns to the home screen, where the
 * status card still says what is missing.
 */
@Composable
fun SetupWizardScreen(
    status: SetupStatus,
    onRequestNotifications: () -> Unit,
    onOpenOverlaySettings: () -> Unit,
    onOpenUsageSettings: () -> Unit,
    onOpenBatterySettings: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = RondaTheme.colors
    BackHandler(onBack = onBack)

    val steps = listOf(
        Step(
            key = "notifications",
            icon = RondaIcons.bell,
            title = R.string.permission_notifications_title,
            why = R.string.permission_notifications_why,
            granted = status.notifications,
            enable = onRequestNotifications
        ),
        Step(
            key = "overlay",
            icon = RondaIcons.layers,
            title = R.string.permission_overlay_title,
            why = R.string.permission_overlay_why,
            granted = status.overlay,
            enable = onOpenOverlaySettings
        ),
        Step(
            key = "usage",
            icon = RondaIcons.eye,
            title = R.string.permission_usage_title,
            why = R.string.permission_usage_why,
            granted = status.usageStats,
            enable = onOpenUsageSettings
        ),
        Step(
            key = "battery",
            icon = RondaIcons.battery,
            title = R.string.permission_battery_title,
            why = R.string.permission_battery_why,
            granted = status.batteryExemption,
            enable = onOpenBatterySettings
        )
    )
    val done = steps.count { it.granted }
    val current = steps.firstOrNull { !it.granted }

    Column(
        modifier
            .fillMaxSize()
            .screenInsets()
    ) {
        BackTopBar(onBack = onBack, title = stringResource(R.string.setup_title))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            SegmentedProgress(total = SetupStatus.TOTAL, done = done)
            Spacer(Modifier.height(10.dp))
            Text(
                text = if (current == null) {
                    stringResource(R.string.setup_all_done_label)
                } else {
                    stringResource(R.string.setup_step_of, done + 1, SetupStatus.TOTAL)
                },
                style = LargePrint,
                color = colors.textSecondary
            )

            Spacer(Modifier.height(20.dp))

            // Slides sideways between steps: the same direction the segments
            // fill, so the motion says "next" without a word.
            AnimatedContent(
                targetState = current?.key ?: "done",
                transitionSpec = {
                    (slideInHorizontally { it / 3 } + fadeIn()) togetherWith
                        (slideOutHorizontally { -it / 3 } + fadeOut())
                },
                label = "setupStep"
            ) { key ->
                val step = steps.firstOrNull { it.key == key }
                if (step == null) AllDone(onBack) else StepCard(step)
            }

            Spacer(Modifier.height(20.dp))
            Row(verticalAlignment = Alignment.Top) {
                RondaIcon(
                    id = RondaIcons.circleInfo,
                    contentDescription = null,
                    tint = colors.textSecondary,
                    size = 20.dp,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.setup_wizard_hint),
                    style = LargePrint,
                    color = colors.textSecondary
                )
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

private data class Step(
    val key: String,
    @DrawableRes val icon: Int,
    @StringRes val title: Int,
    @StringRes val why: Int,
    val granted: Boolean,
    val enable: () -> Unit
)

@Composable
private fun StepCard(step: Step) {
    val colors = RondaTheme.colors
    Column {
        RondaCard(tone = Tone.WARN) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                // No 10sp status badge here: the amber card already says "not
                // yet", and nothing on a protected screen drops below 20sp.
                IconBox(icon = step.icon, tone = Tone.WARN, size = 72.dp)
                Spacer(Modifier.height(16.dp))
                Text(
                    text = stringResource(step.title),
                    style = LargePrintTitle,
                    color = colors.textPrimary,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = stringResource(step.why),
                    style = LargePrint,
                    color = colors.textPrimary,
                    textAlign = TextAlign.Center
                )
            }
        }
        Spacer(Modifier.height(20.dp))
        TactileButton(
            text = stringResource(R.string.permission_enable),
            onClick = step.enable,
            icon = RondaIcons.arrowRight,
            large = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun AllDone(onFinish: () -> Unit) {
    val colors = RondaTheme.colors
    Column {
        RondaCard(tone = Tone.SAFE) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                IconBox(icon = RondaIcons.shieldCheck, tone = Tone.SAFE, size = 72.dp, filled = true)
                Spacer(Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.setup_all_done_title),
                    style = LargePrintTitle,
                    color = colors.textPrimary,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = stringResource(R.string.setup_all_done_body),
                    style = LargePrint,
                    color = colors.textPrimary,
                    textAlign = TextAlign.Center
                )
            }
        }
        Spacer(Modifier.height(20.dp))
        TactileButton(
            text = stringResource(R.string.setup_finish),
            onClick = onFinish,
            icon = RondaIcons.check,
            large = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
