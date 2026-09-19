package com.ronda.app.ui.protectedrole

import android.content.pm.PackageManager
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ronda.app.AppLanguage
import com.ronda.app.R
import com.ronda.app.ThemeMode
import com.ronda.app.detection.ProtectedHistoryItem
import com.ronda.app.ui.components.CardDivider
import com.ronda.app.ui.components.ConfirmSheet
import com.ronda.app.ui.components.EmptyState
import com.ronda.app.ui.components.IconBox
import com.ronda.app.ui.components.RondaCard
import com.ronda.app.ui.components.RondaIcon
import com.ronda.app.ui.components.RondaIcons
import com.ronda.app.ui.components.RondaTopBar
import com.ronda.app.ui.components.SecondaryButton
import com.ronda.app.ui.components.SectionLabel
import com.ronda.app.ui.components.StatusBadge
import com.ronda.app.ui.components.TactileButton
import com.ronda.app.ui.components.Wordmark
import com.ronda.app.ui.setup.SetupStatus
import com.ronda.app.ui.theme.LargePrint
import com.ronda.app.ui.theme.LargePrintTitle
import com.ronda.app.ui.theme.PairingCode
import com.ronda.app.ui.theme.RondaDepth
import com.ronda.app.ui.theme.RondaRadius
import com.ronda.app.ui.theme.RondaTheme
import com.ronda.app.ui.theme.Tone
import java.text.DateFormat

enum class ProtectedTab { ALERTS, HISTORY, SETTINGS }

@Composable
fun ProtectedHomeScreen(
    status: SetupStatus,
    pairingCode: String,
    guardianName: String,
    tab: ProtectedTab,
    onTabChange: (ProtectedTab) -> Unit,
    flaggedPackages: Set<String>,
    historyItems: List<ProtectedHistoryItem>,
    themeMode: ThemeMode,
    onThemeChange: (ThemeMode) -> Unit,
    language: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    onContinueSetup: () -> Unit,
    onManualScan: () -> Unit,
    onUninstall: (String) -> Unit,
    onDisconnect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = tab,
            transitionSpec = {
                fadeIn(tween(200, delayMillis = 40)) togetherWith fadeOut(tween(80))
            },
            label = "protectedTab",
            modifier = Modifier
                .weight(1f)
                .statusBarsPadding()
        ) { current ->
            when (current) {
                ProtectedTab.ALERTS -> ProtectedAlertsTab(
                    status = status,
                    guardianName = guardianName,
                    flaggedPackages = flaggedPackages,
                    onContinueSetup = onContinueSetup,
                    onManualScan = onManualScan,
                    onUninstall = onUninstall
                )

                ProtectedTab.HISTORY -> ProtectedHistoryTab(
                    historyItems = historyItems
                )

                ProtectedTab.SETTINGS -> ProtectedSettingsTab(
                    status = status,
                    pairingCode = pairingCode,
                    guardianName = guardianName,
                    themeMode = themeMode,
                    onThemeChange = onThemeChange,
                    language = language,
                    onLanguageChange = onLanguageChange,
                    onContinueSetup = onContinueSetup,
                    onDisconnect = onDisconnect
                )
            }
        }

        ProtectedBottomNav(
            tab = tab,
            onTabChange = onTabChange,
            alertCount = flaggedPackages.size
        )
    }
}

@Composable
private fun ProtectedAlertsTab(
    status: SetupStatus,
    guardianName: String,
    flaggedPackages: Set<String>,
    onContinueSetup: () -> Unit,
    onManualScan: () -> Unit,
    onUninstall: (String) -> Unit
) {
    val context = LocalContext.current
    val pm = context.packageManager
    val colors = RondaTheme.colors
    val hasThreats = flaggedPackages.isNotEmpty()
    val complete = status.isFullyProtected

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item(key = "top") {
            RondaTopBar(
                leading = { Wordmark() },
                trailing = {
                    StatusBadge(
                        text = stringResource(
                            if (hasThreats) R.string.channel_detection
                            else if (complete) R.string.status_connected
                            else R.string.home_protected_title_incomplete
                        ),
                        tone = if (hasThreats) Tone.DANGER else if (complete) Tone.SAFE else Tone.WARN,
                        icon = if (hasThreats) RondaIcons.triangleWarning else if (complete) RondaIcons.shieldCheck else RondaIcons.shieldAlert,
                        filled = false
                    )
                }
            )
        }

        item(key = "hero") {
            val heroTone = if (hasThreats) Tone.DANGER else if (complete) Tone.SAFE else Tone.WARN
            RondaCard(
                tone = heroTone,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconBox(
                        icon = if (hasThreats) RondaIcons.triangleWarning else if (complete) RondaIcons.shieldCheck else RondaIcons.shieldAlert,
                        tone = heroTone,
                        size = 72.dp,
                        filled = true
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = stringResource(
                            if (hasThreats) R.string.protected_threats_title
                            else if (complete) R.string.home_protected_title_active
                            else R.string.home_protected_title_incomplete
                        ),
                        style = LargePrintTitle,
                        color = colors.textPrimary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = if (hasThreats) {
                            stringResource(R.string.protected_threats_body)
                        } else if (complete) {
                            stringResource(R.string.home_protected_body_active)
                        } else {
                            stringResource(
                                R.string.home_protected_body_incomplete,
                                status.grantedCount,
                                SetupStatus.TOTAL
                            )
                        },
                        style = LargePrint,
                        color = colors.textSecondary,
                        textAlign = TextAlign.Center
                    )
                }

                if (!hasThreats && complete) {
                    Spacer(Modifier.height(20.dp))
                    CardDivider(tone = Tone.SAFE)
                    Spacer(Modifier.height(14.dp))
                    StatusLine(
                        icon = RondaIcons.eye,
                        text = stringResource(R.string.home_protected_watch_active)
                    )
                    Spacer(Modifier.height(10.dp))
                    StatusLine(
                        icon = RondaIcons.heartHandshake,
                        text = guardedBy(guardianName)
                    )
                }
            }
        }

        if (hasThreats) {
            item(key = "threats_label") {
                SectionLabel(
                    text = stringResource(R.string.protected_threats_title),
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 8.dp)
                )
            }

            items(flaggedPackages.toList(), key = { it }) { pkgName ->
                val appLabel = remember(pkgName) {
                    runCatching {
                        val appInfo = pm.getApplicationInfo(pkgName, 0)
                        pm.getApplicationLabel(appInfo).toString()
                    }.getOrDefault(pkgName)
                }

                RondaCard(
                    tone = Tone.DANGER,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        IconBox(icon = RondaIcons.shieldAlert, tone = Tone.DANGER, size = 48.dp)
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = appLabel,
                                style = MaterialTheme.typography.titleLarge,
                                color = colors.textPrimary
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = pkgName,
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.textSecondary
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.protected_action_uninstall_desc),
                                style = LargePrint.copy(fontSize = 15.sp),
                                color = colors.textPrimary
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    TactileButton(
                        text = stringResource(R.string.protected_action_uninstall),
                        onClick = { onUninstall(pkgName) },
                        tone = Tone.DANGER,
                        icon = RondaIcons.trash,
                        large = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        if (!complete) {
            item(key = "fix_setup") {
                RondaCard(
                    tone = Tone.WARN,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.home_protected_continue_setup),
                        style = LargePrintTitle.copy(fontSize = 18.sp),
                        color = colors.textPrimary
                    )
                    Spacer(Modifier.height(12.dp))
                    TactileButton(
                        text = stringResource(R.string.home_protected_continue_setup),
                        onClick = onContinueSetup,
                        icon = RondaIcons.arrowRight,
                        large = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        item(key = "manual_scan") {
            Spacer(Modifier.height(8.dp))
            SecondaryButton(
                text = stringResource(R.string.home_protected_manual_scan),
                onClick = onManualScan,
                icon = RondaIcons.activity,
                large = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            )
        }

        item(key = "privacy") {
            Spacer(Modifier.height(16.dp))
            RondaCard(modifier = Modifier.padding(horizontal = 20.dp)) {
                Row(verticalAlignment = Alignment.Top) {
                    IconBox(icon = RondaIcons.lock, tone = Tone.NEUTRAL, size = 44.dp)
                    Spacer(Modifier.width(14.dp))
                    Text(
                        text = stringResource(R.string.setup_privacy_note),
                        style = LargePrint,
                        color = colors.textPrimary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProtectedHistoryTab(
    historyItems: List<ProtectedHistoryItem>
) {
    val colors = RondaTheme.colors
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item(key = "top") {
            RondaTopBar(
                title = stringResource(R.string.protected_tab_history),
                leading = { IconBox(icon = RondaIcons.history, tone = Tone.TRUST, size = 44.dp) }
            )
        }

        if (historyItems.isEmpty()) {
            item(key = "empty") {
                EmptyState(
                    icon = RondaIcons.history,
                    title = stringResource(R.string.protected_empty_threats_title),
                    body = stringResource(R.string.protected_empty_threats_body),
                    tone = Tone.TRUST,
                    modifier = Modifier.padding(top = 40.dp)
                )
            }
        } else {
            items(historyItems, key = { "${it.packageName}:${it.timestamp}" }) { item ->
                val dateFormatted = remember(item.timestamp) {
                    DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(item.timestamp)
                }
                val isUninstalled = item.action == "uninstalled"

                RondaCard(
                    tone = Tone.NEUTRAL,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconBox(
                            icon = if (isUninstalled) RondaIcons.trash else RondaIcons.check,
                            tone = if (isUninstalled) Tone.SAFE else Tone.TRUST,
                            size = 44.dp
                        )
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = item.appLabel,
                                style = MaterialTheme.typography.titleMedium,
                                color = colors.textPrimary
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = if (isUninstalled) {
                                    stringResource(R.string.uninstalled_notification_title)
                                } else {
                                    stringResource(R.string.outcome_safe)
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.textSecondary
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = dateFormatted,
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.textSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProtectedSettingsTab(
    status: SetupStatus,
    pairingCode: String,
    guardianName: String,
    themeMode: ThemeMode,
    onThemeChange: (ThemeMode) -> Unit,
    language: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    onContinueSetup: () -> Unit,
    onDisconnect: () -> Unit
) {
    val colors = RondaTheme.colors
    var confirmDisconnectOpen by rememberSaveable { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            RondaTopBar(
                title = stringResource(R.string.protected_tab_settings),
                leading = { IconBox(icon = RondaIcons.gear, tone = Tone.TRUST, size = 44.dp) }
            )
        }

        // Section: Protection Permissions
        item {
            SectionLabel(
                text = stringResource(R.string.protected_permissions_title),
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 10.dp)
            )
            RondaCard(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = stringResource(R.string.protected_permissions_body),
                    style = LargePrint,
                    color = colors.textSecondary
                )
                Spacer(Modifier.height(16.dp))

                PermissionStatusRow("Tampilkan di Atas Aplikasi", status.hasOverlay)
                Spacer(Modifier.height(8.dp))
                PermissionStatusRow("Akses Penggunaan", status.hasUsageStats)
                Spacer(Modifier.height(8.dp))
                PermissionStatusRow("Optimasi Baterai Bebas", status.isBatteryOptimized)
                Spacer(Modifier.height(8.dp))
                PermissionStatusRow("Izin Notifikasi", status.hasNotification)

                if (!status.isFullyProtected) {
                    Spacer(Modifier.height(16.dp))
                    SecondaryButton(
                        text = stringResource(R.string.home_protected_continue_setup),
                        onClick = onContinueSetup,
                        icon = RondaIcons.shieldAlert,
                        large = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Section: Rondor Info
        item {
            SectionLabel(
                text = stringResource(R.string.protected_guardian_info_title),
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 10.dp)
            )
            RondaCard(modifier = Modifier.padding(horizontal = 20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconBox(icon = RondaIcons.heartHandshake, tone = Tone.SAFE, size = 48.dp)
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = guardedBy(guardianName),
                            style = LargePrintTitle.copy(fontSize = 18.sp),
                            color = colors.textPrimary
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.home_protected_profile_body),
                            style = LargePrint.copy(fontSize = 14.sp),
                            color = colors.textSecondary
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                CardDivider()
                Spacer(Modifier.height(14.dp))
                Text(
                    text = stringResource(R.string.home_protected_pairing_code).uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textSecondary
                )
                Spacer(Modifier.height(4.dp))
                SelectionContainer {
                    Text(text = pairingCode, style = PairingCode, color = colors.textPrimary)
                }
            }
        }

        // Section: Appearance
        item {
            SectionLabel(
                text = stringResource(R.string.settings_appearance),
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 10.dp)
            )
            RondaCard(
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                RadioRow(
                    title = stringResource(R.string.theme_system),
                    selected = themeMode == ThemeMode.SYSTEM,
                    onClick = { onThemeChange(ThemeMode.SYSTEM) },
                    icon = RondaIcons.smartphone
                )
                RadioRow(
                    title = stringResource(R.string.theme_light),
                    selected = themeMode == ThemeMode.LIGHT,
                    onClick = { onThemeChange(ThemeMode.LIGHT) },
                    icon = RondaIcons.sun
                )
                RadioRow(
                    title = stringResource(R.string.theme_dark),
                    selected = themeMode == ThemeMode.DARK,
                    onClick = { onThemeChange(ThemeMode.DARK) },
                    icon = RondaIcons.moon
                )
            }
        }

        // Section: Language
        item {
            SectionLabel(
                text = stringResource(R.string.settings_language),
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 10.dp)
            )
            RondaCard(
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                RadioRow(
                    title = stringResource(R.string.language_indonesian),
                    selected = language == AppLanguage.INDONESIAN,
                    onClick = { onLanguageChange(AppLanguage.INDONESIAN) },
                    icon = RondaIcons.globe
                )
                RadioRow(
                    title = stringResource(R.string.language_english),
                    selected = language == AppLanguage.ENGLISH,
                    onClick = { onLanguageChange(AppLanguage.ENGLISH) },
                    icon = RondaIcons.globe
                )
            }
        }

        // Section: Disconnect
        item {
            Spacer(Modifier.height(16.dp))
            SecondaryButton(
                text = stringResource(R.string.protected_disconnect_title),
                onClick = { confirmDisconnectOpen = true },
                icon = RondaIcons.logOut,
                large = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            )
        }
    }

    if (confirmDisconnectOpen) {
        ConfirmSheet(
            title = stringResource(R.string.protected_disconnect_title),
            body = stringResource(R.string.protected_disconnect_confirm, guardianName.ifBlank { "Rondor" }),
            confirmText = stringResource(R.string.disconnect_confirm),
            confirmTone = Tone.DANGER,
            confirmIcon = RondaIcons.logOut,
            onConfirm = {
                confirmDisconnectOpen = false
                onDisconnect()
            },
            onDismiss = { confirmDisconnectOpen = false },
            large = true
        )
    }
}

@Composable
private fun PermissionStatusRow(title: String, active: Boolean) {
    val colors = RondaTheme.colors
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        RondaIcon(
            id = if (active) RondaIcons.check else RondaIcons.close,
            contentDescription = null,
            tint = if (active) colors.safe else colors.danger,
            size = 20.dp
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = title,
            style = LargePrint.copy(fontSize = 16.sp),
            color = colors.textPrimary,
            modifier = Modifier.weight(1f)
        )
        StatusBadge(
            text = if (active) "Aktif" else "Mati",
            tone = if (active) Tone.SAFE else Tone.WARN,
            filled = false
        )
    }
}

@Composable
private fun RadioRow(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    @DrawableRes icon: Int? = null
) {
    val colors = RondaTheme.colors
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onClick, role = Role.RadioButton)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        if (icon != null) {
            RondaIcon(
                id = icon,
                contentDescription = null,
                tint = if (selected) colors.safe else colors.textSecondary,
                size = 22.dp
            )
            Spacer(Modifier.width(14.dp))
        }
        Text(
            text = title,
            style = LargePrint.copy(fontSize = 16.sp),
            color = colors.textPrimary,
            modifier = Modifier.weight(1f)
        )
        RadioButton(
            selected = selected,
            onClick = null,
            colors = RadioButtonDefaults.colors(
                selectedColor = colors.safe,
                unselectedColor = colors.textSecondary
            )
        )
    }
}

@Composable
private fun ProtectedBottomNav(
    tab: ProtectedTab,
    onTabChange: (ProtectedTab) -> Unit,
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
            ProtectedNavItem(
                icon = RondaIcons.bell,
                label = stringResource(R.string.protected_tab_alerts),
                selected = tab == ProtectedTab.ALERTS,
                badge = alertCount,
                onClick = { onTabChange(ProtectedTab.ALERTS) }
            )
            ProtectedNavItem(
                icon = RondaIcons.history,
                label = stringResource(R.string.protected_tab_history),
                selected = tab == ProtectedTab.HISTORY,
                onClick = { onTabChange(ProtectedTab.HISTORY) }
            )
            ProtectedNavItem(
                icon = RondaIcons.gear,
                label = stringResource(R.string.protected_tab_settings),
                selected = tab == ProtectedTab.SETTINGS,
                onClick = { onTabChange(ProtectedTab.SETTINGS) }
            )
        }
    }
}

@Composable
private fun RowScope.ProtectedNavItem(
    @DrawableRes icon: Int,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    badge: Int = 0
) {
    val colors = RondaTheme.colors
    val contentColor = if (selected) colors.safe else colors.textSecondary
    val bg = if (selected) colors.safeTint else colors.card

    Box(
        modifier = Modifier
            .weight(1f)
            .heightIn(min = 52.dp)
            .background(bg, shape = RoundedCornerShape(RondaRadius.pill))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .selectable(selected = selected, onClick = onClick, role = Role.Tab)
        ) {
            BadgedBox(
                badge = {
                    if (badge > 0) {
                        Badge(
                            containerColor = colors.danger,
                            contentColor = colors.paper
                        ) {
                            Text(text = badge.toString(), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            ) {
                RondaIcon(
                    id = icon,
                    contentDescription = label,
                    tint = contentColor,
                    size = 24.dp
                )
            }
            if (selected) {
                Spacer(Modifier.width(8.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = contentColor
                )
            }
        }
    }
}

@Composable
private fun guardedBy(guardianName: String): String =
    if (guardianName.isBlank()) stringResource(R.string.home_protected_guarded_by_unknown)
    else stringResource(R.string.home_protected_guarded_by, guardianName)

@Composable
private fun StatusLine(icon: Int, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconBox(icon = icon, tone = Tone.SAFE, size = 40.dp)
        Spacer(Modifier.width(12.dp))
        Text(
            text = text,
            style = LargePrint,
            color = RondaTheme.colors.textPrimary,
            modifier = Modifier.weight(1f)
        )
    }
}
