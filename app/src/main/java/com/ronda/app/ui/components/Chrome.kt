package com.ronda.app.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.ronda.app.R
import com.ronda.app.ui.theme.LargePrint
import com.ronda.app.ui.theme.RondaRadius
import com.ronda.app.ui.theme.RondaTheme
import com.ronda.app.ui.theme.Tone
import kotlinx.coroutines.launch

/** Screens pad themselves: status bar, navigation bar, cutout and keyboard. */
@Composable
fun Modifier.screenInsets(): Modifier = windowInsetsPadding(WindowInsets.safeDrawing)

/** The RONDA mark: filled shield-check box beside the name in black weight. */
@Composable
fun Wordmark(modifier: Modifier = Modifier, boxSize: Dp = 32.dp) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        IconBox(
            icon = RondaIcons.shieldCheck,
            tone = Tone.SAFE,
            size = boxSize,
            filled = true,
            contentDescription = null
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineMedium.copy(letterSpacing = 0.06.em),
            color = RondaTheme.colors.textPrimary
        )
    }
}

/**
 * Screen chrome: 64dp, no elevation, sits on the page colour. [leading] and
 * [trailing] are slots so a screen can put a back button, the wordmark or a
 * profile box in the same place every time.
 */
@Composable
fun RondaTopBar(
    modifier: Modifier = Modifier,
    title: String? = null,
    leading: (@Composable RowScope.() -> Unit)? = null,
    trailing: (@Composable RowScope.() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .heightIn(min = 64.dp)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (leading != null) {
            leading()
            Spacer(Modifier.width(12.dp))
        }
        if (title != null) {
            // 20sp: the same bar heads protected screens, where nothing may
            // drop below the PRD's large-print floor.
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = RondaTheme.colors.textPrimary,
                modifier = Modifier.weight(1f)
            )
        } else {
            Spacer(Modifier.weight(1f))
        }
        if (trailing != null) trailing()
    }
}

/** A 44dp icon control in a tinted box — back, profile, close. */
@Composable
fun IconActionButton(
    @DrawableRes icon: Int,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tone: Tone = Tone.NEUTRAL
) {
    val colors = RondaTheme.colors
    val shape = RoundedCornerShape(RondaRadius.button)
    Box(
        modifier = modifier
            .size(44.dp)
            .clip(shape)
            .background(colors.tint(tone))
            .border(2.dp, colors.border(tone), shape)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        RondaIcon(
            id = icon,
            contentDescription = contentDescription,
            tint = if (tone == Tone.NEUTRAL) colors.textPrimary else colors.fill(tone),
            size = 22.dp
        )
    }
}

@Composable
fun BackTopBar(onBack: () -> Unit, title: String, modifier: Modifier = Modifier) {
    RondaTopBar(
        modifier = modifier,
        title = title,
        leading = {
            IconActionButton(
                icon = RondaIcons.arrowLeft,
                contentDescription = stringResource(R.string.detail_back),
                onClick = onBack
            )
        }
    )
}

/**
 * Confirmation for the actions that reduce safety — marking safe, unpairing.
 * Friction belongs on those and never on the actions that keep protection.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmSheet(
    title: String,
    body: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmTone: Tone = Tone.TRUST,
    @DrawableRes confirmIcon: Int? = null,
    cancelText: String = stringResource(R.string.action_cancel),
    large: Boolean = false
) {
    val colors = RondaTheme.colors
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    // Let the sheet slide away before the caller removes it.
    fun close(then: () -> Unit) {
        scope.launch { sheetState.hide() }.invokeOnCompletion { then() }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.card,
        contentColor = colors.textPrimary,
        shape = RoundedCornerShape(topStart = RondaRadius.sheet, topEnd = RondaRadius.sheet)
    ) {
        Column(Modifier.padding(start = 24.dp, end = 24.dp, bottom = 32.dp)) {
            Text(
                text = title,
                style = if (large) MaterialTheme.typography.displaySmall
                else MaterialTheme.typography.headlineMedium,
                color = colors.textPrimary
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = body,
                style = if (large) LargePrint else MaterialTheme.typography.bodyLarge,
                color = colors.textSecondary
            )
            Spacer(Modifier.height(24.dp))
            TactileButton(
                text = confirmText,
                onClick = { close(onConfirm) },
                tone = confirmTone,
                icon = confirmIcon,
                large = large,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            SecondaryButton(
                text = cancelText,
                onClick = { close(onDismiss) },
                large = large,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Setup progress as a row of segments — the Duolingo path, flattened. Each
 * segment fills green as its permission lands.
 */
@Composable
fun SegmentedProgress(total: Int, done: Int, modifier: Modifier = Modifier) {
    val colors = RondaTheme.colors
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(total) { index ->
            val fill by animateColorAsState(
                targetValue = if (index < done) colors.safe else colors.border,
                animationSpec = tween(320),
                label = "segment$index"
            )
            Box(
                Modifier
                    .weight(1f)
                    .height(10.dp)
                    .background(fill, RoundedCornerShape(5.dp))
            )
        }
    }
}

/**
 * One choice among a few — a theme, a language, a nickname. Radio semantics,
 * a tinted icon box, and a drawn dot; never a bare Material radio on its own.
 */
@Composable
fun OptionRow(
    @DrawableRes icon: Int,
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    large: Boolean = false
) {
    val colors = RondaTheme.colors
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(RondaRadius.button))
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
            .heightIn(min = 56.dp)
            .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconBox(icon = icon, tone = if (selected) Tone.TRUST else Tone.NEUTRAL, size = 40.dp)
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = title,
                style = if (large) LargePrint else MaterialTheme.typography.titleMedium,
                color = colors.textPrimary
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        RadioDot(selected)
    }
}

@Composable
fun RadioDot(selected: Boolean, modifier: Modifier = Modifier) {
    val colors = RondaTheme.colors
    val edge by animateColorAsState(
        targetValue = if (selected) colors.trust else colors.borderStrong,
        animationSpec = tween(160),
        label = "radioEdge"
    )
    Box(
        modifier = modifier
            .size(24.dp)
            .border(2.5.dp, edge, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Box(
                Modifier
                    .size(12.dp)
                    .background(colors.trust, CircleShape)
            )
        }
    }
}

/**
 * A card that is one of several answers: it takes the trust tint when chosen
 * and shows a filled check. Used for role and language, where the answer is
 * committed by one button below rather than by the tap itself.
 */
@Composable
fun ChoiceCard(
    @DrawableRes icon: Int,
    title: String,
    body: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    large: Boolean = false
) {
    val colors = RondaTheme.colors
    RondaCard(
        tone = if (selected) Tone.TRUST else Tone.NEUTRAL,
        onClick = onClick,
        modifier = modifier
    ) {
        Row(verticalAlignment = Alignment.Top) {
            IconBox(icon = icon, tone = if (selected) Tone.TRUST else Tone.NEUTRAL, size = 48.dp)
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = if (large) MaterialTheme.typography.headlineMedium
                    else MaterialTheme.typography.titleLarge,
                    color = colors.textPrimary
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = body,
                    style = if (large) LargePrint else MaterialTheme.typography.bodyLarge,
                    color = colors.textSecondary
                )
            }
            Spacer(Modifier.width(12.dp))
            if (selected) {
                IconBox(icon = RondaIcons.check, tone = Tone.TRUST, size = 28.dp, filled = true)
            } else {
                Spacer(Modifier.size(28.dp))
            }
        }
    }
}

/** A quiet placeholder while the first alerts arrive: three bars, pulsing. */
@Composable
fun SkeletonCard(modifier: Modifier = Modifier) {
    val colors = RondaTheme.colors
    val pulse = rememberInfiniteTransition(label = "skeleton")
    val alpha by pulse.animateFloat(
        initialValue = 0.45f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "skeletonAlpha"
    )
    RondaCard(modifier = modifier) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                Modifier
                    .size(44.dp)
                    .alpha(alpha)
                    .background(colors.surface, RoundedCornerShape(RondaRadius.button))
            )
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Box(
                    Modifier
                        .fillMaxWidth(0.55f)
                        .height(18.dp)
                        .alpha(alpha)
                        .background(colors.surface, RoundedCornerShape(RondaRadius.badge))
                )
                Spacer(Modifier.height(10.dp))
                Box(
                    Modifier
                        .fillMaxWidth(0.9f)
                        .height(13.dp)
                        .alpha(alpha)
                        .background(colors.surface, RoundedCornerShape(RondaRadius.badge))
                )
                Spacer(Modifier.height(8.dp))
                Box(
                    Modifier
                        .fillMaxWidth(0.4f)
                        .height(13.dp)
                        .alpha(alpha)
                        .background(colors.surface, RoundedCornerShape(RondaRadius.badge))
                )
            }
        }
    }
}

/** An empty list that says what the emptiness means. */
@Composable
fun EmptyState(
    @DrawableRes icon: Int,
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    tone: Tone = Tone.SAFE
) {
    val colors = RondaTheme.colors
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconBox(icon = icon, tone = tone, size = 64.dp)
        Spacer(Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = colors.textPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = body,
            style = MaterialTheme.typography.bodyLarge,
            color = colors.textSecondary,
            textAlign = TextAlign.Center
        )
    }
}
