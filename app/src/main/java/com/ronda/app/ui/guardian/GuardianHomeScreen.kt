package com.ronda.app.ui.guardian

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.ronda.app.AppLanguage
import com.ronda.app.R
import com.ronda.app.ThemeMode
import com.ronda.app.core.Verdict
import com.ronda.app.ui.components.EmptyState
import com.ronda.app.ui.components.IconBox
import com.ronda.app.ui.components.RondaCard
import com.ronda.app.ui.components.RondaIcon
import com.ronda.app.ui.components.RondaIcons
import com.ronda.app.ui.components.RondaTopBar
import com.ronda.app.ui.components.SectionLabel
import com.ronda.app.ui.components.SkeletonCard
import com.ronda.app.ui.components.StatusBadge
import com.ronda.app.ui.guardian.components.AlertRow
import com.ronda.app.ui.theme.RondaDepth
import com.ronda.app.ui.theme.RondaRadius
import com.ronda.app.ui.theme.RondaTheme
import com.ronda.app.ui.theme.Tone

enum class GuardianTab { ALERTS, HISTORY, SETTINGS }

/**
 * Home for the guardian: three destinations in a bottom bar (USERFLOW §3).
 * Peringatan is the working screen; Riwayat is the evidence of decisions made;
 * Setelan is where the nickname, theme, language and the pairing itself live.
 */
@Composable
fun GuardianHomeScreen(
    state: GuardianUiState,
    protectedName: String,
    pairingCode: String,
    /** Paired to the offline fixture: the alert list is synthetic and says so. */
    demo: Boolean,
    tab: GuardianTab,
    onTabChange: (GuardianTab) -> Unit,
    onVerdictClick: (Verdict) -> Unit,
    themeMode: ThemeMode,
    onThemeChange: (ThemeMode) -> Unit,
    language: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    onRename: (String) -> Unit,
    onDisconnect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxSize()) {
        // The status bar is paid for here, once, so a scrolled list never runs
        // under the clock; the bar at the bottom pays for the navigation bar.
        AnimatedContent(
            targetState = tab,
            transitionSpec = {
                fadeIn(tween(200, delayMillis = 40)) togetherWith fadeOut(tween(80))
            },
            label = "guardianTab",
            modifier = Modifier
                .weight(1f)
                .statusBarsPadding()
        ) { current ->
            when (current) {
                GuardianTab.ALERTS -> AlertsTab(
                    state = state,
                    protectedName = protectedName,
                    demo = demo,
                    onVerdictClick = onVerdictClick
                )

                GuardianTab.HISTORY -> HistoryTab(
                    state = state,
                    protectedName = protectedName,
                    onVerdictClick = onVerdictClick
                )

                GuardianTab.SETTINGS -> SettingsTab(
                    state = state,
                    protectedName = protectedName,
                    pairingCode = pairingCode,
                    themeMode = themeMode,
                    onThemeChange = onThemeChange,
                    language = language,
                    onLanguageChange = onLanguageChange,
                    onRename = onRename,
                    onDisconnect = onDisconnect
                )
            }
        }
        GuardianBottomNav(
            tab = tab,
            onTabChange = onTabChange,
            alertCount = state.needsReview.size
        )
    }
}

/**
 * The working screen. Top bar names the phone and its connection; a hero card
 * says whether anything is wrong; the two lists follow, loud then quiet.
 */
@Composable
private fun AlertsTab(
    state: GuardianUiState,
    protectedName: String,
    demo: Boolean,
    onVerdictClick: (Verdict) -> Unit
) {
    val colors = RondaTheme.colors
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item(key = "top") {
            RondaTopBar(
                title = stringResource(R.string.home_guardian_device, protectedName),
                leading = { IconBox(icon = RondaIcons.person, tone = Tone.TRUST, size = 44.dp) },
                trailing = {
                    // The fixture's apps carry real bank names, so a demo says
                    // so where the connection pill would go: offline there is
                    // no connection to report, and two pills crowd the title.
                    if (demo) {
                        StatusBadge(
                            text = stringResource(R.string.demo_mode),
                            tone = Tone.NEUTRAL,
                            filled = false
                        )
                    } else {
                        StatusBadge(
                            text = stringResource(
                                if (state.connected) R.string.status_connected
                                else R.string.status_disconnected
                            ),
                            tone = if (state.connected) Tone.SAFE else Tone.WARN,
                            icon = if (state.connected) RondaIcons.link else RondaIcons.wifiOff,
                            filled = false
                        )
                    }
                }
            )
        }

        item(key = "hero") {
            HeroCard(
                state = state,
                protectedName = protectedName,
                modifier = Modifier
                    .animateItem()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            )
        }

        if (!state.connected) {
            item(key = "offline") {
                RondaCard(
                    tone = Tone.WARN,
                    modifier = Modifier
                        .animateItem()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        IconBox(icon = RondaIcons.wifiOff, tone = Tone.WARN, size = 40.dp)
                        Spacer(Modifier.width(14.dp))
                        Text(
                            text = stringResource(R.string.watch_disconnected, protectedName),
                            style = MaterialTheme.typography.bodyLarge,
                            color = colors.textPrimary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        if (!state.loaded) {
            items(3, key = { "skeleton$it" }) {
                SkeletonCard(
                    Modifier
                        .animateItem()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                )
            }
            return@LazyColumn
        }

        item(key = "review-label") {
            SectionLabel(
                text = stringResource(R.string.section_review),
                modifier = Modifier
                    .animateItem()
                    .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 10.dp)
            )
        }
        if (state.needsReview.isEmpty()) {
            item(key = "review-empty") {
                Text(
                    text = stringResource(R.string.section_review_empty),
                    style = MaterialTheme.typography.bodyLarge,
                    color = colors.textSecondary,
                    modifier = Modifier
                        .animateItem()
                        .padding(horizontal = 20.dp, vertical = 4.dp)
                )
            }
        } else {
            items(state.needsReview, key = { "open:${it.packageName}" }) { verdict ->
                AlertRow(
                    verdict = verdict,
                    protectedName = protectedName,
                    onClick = { onVerdictClick(verdict) },
                    modifier = Modifier
                        .animateItem()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                )
            }
        }

        item(key = "monitored-label") {
            SectionLabel(
                text = stringResource(R.string.section_monitored),
                modifier = Modifier
                    .animateItem()
                    .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 10.dp)
            )
        }
        if (state.monitored.isEmpty()) {
            item(key = "monitored-empty") {
                Text(
                    text = stringResource(R.string.watch_empty_monitored, protectedName),
                    style = MaterialTheme.typography.bodyLarge,
                    color = colors.textSecondary,
                    modifier = Modifier
                        .animateItem()
                        .padding(horizontal = 20.dp, vertical = 4.dp)
                )
            }
        } else {
            items(state.monitored, key = { "quiet:${it.packageName}" }) { verdict ->
                AlertRow(
                    verdict = verdict,
                    protectedName = protectedName,
                    onClick = { onVerdictClick(verdict) },
                    modifier = Modifier
                        .animateItem()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun HeroCard(state: GuardianUiState, protectedName: String, modifier: Modifier = Modifier) {
    val colors = RondaTheme.colors
    val open = state.needsReview.size
    val tone = when {
        !state.loaded -> Tone.NEUTRAL
        open == 0 -> Tone.SAFE
        else -> Tone.DANGER
    }
    RondaCard(tone = tone, modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconBox(
                icon = when (tone) {
                    Tone.SAFE -> RondaIcons.shieldCheck
                    Tone.DANGER -> RondaIcons.triangleWarning
                    else -> RondaIcons.shield
                },
                tone = tone,
                size = 60.dp,
                filled = state.loaded
            )
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = when {
                        !state.loaded -> stringResource(R.string.hero_loading_title)
                        open == 0 -> stringResource(R.string.hero_all_clear_title)
                        else -> pluralStringResource(R.plurals.hero_review_title, open, open)
                    },
                    style = MaterialTheme.typography.headlineLarge,
                    color = colors.textPrimary
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = when {
                        !state.loaded -> stringResource(R.string.hero_loading_body, protectedName)
                        open == 0 -> stringResource(R.string.hero_all_clear_body, protectedName)
                        else -> stringResource(R.string.hero_review_body)
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = colors.textSecondary
                )
            }
        }
    }
}

/** Everything the guardian has already ruled on, newest first, with its outcome. */
@Composable
private fun HistoryTab(
    state: GuardianUiState,
    protectedName: String,
    onVerdictClick: (Verdict) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item(key = "top") {
            RondaTopBar(
                title = stringResource(R.string.history_title),
                leading = { IconBox(icon = RondaIcons.history, tone = Tone.TRUST, size = 44.dp) }
            )
        }
        if (state.history.isEmpty()) {
            item(key = "empty") {
                EmptyState(
                    icon = RondaIcons.history,
                    title = stringResource(R.string.history_empty_title),
                    body = stringResource(R.string.history_empty_body),
                    tone = Tone.TRUST,
                    modifier = Modifier.animateItem()
                )
            }
        } else {
            items(state.history, key = { "history:${it.packageName}" }) { verdict ->
                AlertRow(
                    verdict = verdict,
                    protectedName = protectedName,
                    onClick = { onVerdictClick(verdict) },
                    modifier = Modifier
                        .animateItem()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                )
            }
        }
    }
}

/**
 * The bottom bar (DESIGN.md §6.6): the active destination sits in a trust-tint
 * pill, the others in muted ink. A count on the bell says how many apps are
 * waiting without making the guardian open the tab to find out.
 */
@Composable
private fun GuardianBottomNav(
    tab: GuardianTab,
    onTabChange: (GuardianTab) -> Unit,
    alertCount: Int
) {
    val colors = RondaTheme.colors
    Column(
        Modifier
            .fillMaxWidth()
            .background(colors.card)
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(RondaDepth.border)
                .background(colors.border)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .heightIn(min = 64.dp)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                icon = RondaIcons.bell,
                label = stringResource(R.string.tab_alerts),
                selected = tab == GuardianTab.ALERTS,
                badge = alertCount,
                onClick = { onTabChange(GuardianTab.ALERTS) }
            )
            NavItem(
                icon = RondaIcons.history,
                label = stringResource(R.string.tab_history),
                selected = tab == GuardianTab.HISTORY,
                onClick = { onTabChange(GuardianTab.HISTORY) }
            )
            NavItem(
                icon = RondaIcons.gear,
                label = stringResource(R.string.tab_settings),
                selected = tab == GuardianTab.SETTINGS,
                onClick = { onTabChange(GuardianTab.SETTINGS) }
            )
        }
    }
}

@Composable
private fun RowScope.NavItem(
    @DrawableRes icon: Int,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    badge: Int = 0
) {
    val colors = RondaTheme.colors
    val shape = RoundedCornerShape(RondaRadius.iconBoxSmall)
    val fill by animateColorAsState(
        targetValue = if (selected) colors.trustTint else colors.card,
        animationSpec = tween(160),
        label = "navFill"
    )
    val edge by animateColorAsState(
        targetValue = if (selected) colors.trustBorder else colors.card,
        animationSpec = tween(160),
        label = "navEdge"
    )
    val ink = if (selected) colors.trust else colors.textMuted

    Column(
        modifier = Modifier
            .weight(1f)
            .clip(shape)
            .background(fill)
            .border(2.dp, edge, shape)
            .selectable(selected = selected, role = Role.Tab, onClick = onClick)
            .heightIn(min = 52.dp)
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box {
            RondaIcon(id = icon, contentDescription = null, tint = ink, size = 22.dp)
            if (badge > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(start = 14.dp)
                        .size(18.dp)
                        .background(colors.danger, CircleShape)
                        .border(2.dp, colors.card, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (badge > 9) "9+" else "$badge",
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = androidx.compose.ui.unit.TextUnit.Unspecified),
                        color = colors.onFill
                    )
                }
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = ink
        )
    }
}
