package com.ronda.app.ui.guardian

import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.ronda.app.AppLanguage
import com.ronda.app.R
import com.ronda.app.ThemeMode
import com.ronda.app.ui.components.CardDivider
import com.ronda.app.ui.components.Chip
import com.ronda.app.ui.components.ConfirmSheet
import com.ronda.app.ui.components.IconBox
import com.ronda.app.ui.components.OptionRow
import com.ronda.app.ui.components.RondaCard
import com.ronda.app.ui.components.RondaIcons
import com.ronda.app.ui.components.RondaTextField
import com.ronda.app.ui.components.RondaTopBar
import com.ronda.app.ui.components.SecondaryButton
import com.ronda.app.ui.components.SectionLabel
import com.ronda.app.ui.components.StatusBadge
import com.ronda.app.ui.components.TactileButton
import com.ronda.app.ui.theme.RondaRadius
import com.ronda.app.ui.theme.RondaTheme
import com.ronda.app.ui.theme.Tabular
import com.ronda.app.ui.theme.Tone

/**
 * Setelan (USERFLOW §3): the guarded phone, appearance, language, privacy, and
 * — last, in red text, behind a confirmation — the way out of the pairing.
 */
@Composable
internal fun SettingsTab(
    state: GuardianUiState,
    protectedName: String,
    pairingCode: String,
    themeMode: ThemeMode,
    onThemeChange: (ThemeMode) -> Unit,
    language: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    onRename: (String) -> Unit,
    onDisconnect: () -> Unit
) {
    val colors = RondaTheme.colors
    var renaming by rememberSaveable { mutableStateOf(false) }
    var disconnecting by rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current
    val version = remember {
        runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        }.getOrNull() ?: "1.0"
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            RondaTopBar(
                title = stringResource(R.string.settings_title),
                leading = { IconBox(icon = RondaIcons.gear, tone = Tone.TRUST, size = 44.dp) }
            )
        }

        item {
            SectionLabel(
                text = stringResource(R.string.settings_device_section),
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 10.dp)
            )
            RondaCard(modifier = Modifier.padding(horizontal = 20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconBox(icon = RondaIcons.person, tone = Tone.TRUST, size = 52.dp)
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.home_guardian_device, protectedName),
                            style = MaterialTheme.typography.headlineMedium,
                            color = colors.textPrimary
                        )
                        Spacer(Modifier.height(6.dp))
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
                Spacer(Modifier.height(16.dp))
                CardDivider()
                Spacer(Modifier.height(14.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.settings_pairing_code).uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.textSecondary
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = pairingCode,
                            style = Tabular.copy(fontSize = MaterialTheme.typography.titleMedium.fontSize),
                            color = colors.textPrimary
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                SecondaryButton(
                    text = stringResource(R.string.settings_rename),
                    onClick = { renaming = true },
                    icon = RondaIcons.pen,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        item {
            SectionLabel(
                text = stringResource(R.string.settings_appearance),
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 10.dp)
            )
            RondaCard(
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                OptionRow(
                    icon = RondaIcons.smartphone,
                    title = stringResource(R.string.theme_system),
                    selected = themeMode == ThemeMode.SYSTEM,
                    onClick = { onThemeChange(ThemeMode.SYSTEM) }
                )
                OptionRow(
                    icon = RondaIcons.sun,
                    title = stringResource(R.string.theme_light),
                    selected = themeMode == ThemeMode.LIGHT,
                    onClick = { onThemeChange(ThemeMode.LIGHT) }
                )
                OptionRow(
                    icon = RondaIcons.moon,
                    title = stringResource(R.string.theme_dark),
                    selected = themeMode == ThemeMode.DARK,
                    onClick = { onThemeChange(ThemeMode.DARK) }
                )
            }
        }

        item {
            SectionLabel(
                text = stringResource(R.string.settings_language),
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 10.dp)
            )
            RondaCard(
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                OptionRow(
                    icon = RondaIcons.globe,
                    title = stringResource(R.string.lang_indonesian),
                    selected = language == AppLanguage.INDONESIAN,
                    onClick = { onLanguageChange(AppLanguage.INDONESIAN) }
                )
                OptionRow(
                    icon = RondaIcons.globe,
                    title = stringResource(R.string.lang_english),
                    selected = language == AppLanguage.ENGLISH,
                    onClick = { onLanguageChange(AppLanguage.ENGLISH) }
                )
            }
        }

        item {
            SectionLabel(
                text = stringResource(R.string.settings_privacy),
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 10.dp)
            )
            RondaCard(modifier = Modifier.padding(horizontal = 20.dp)) {
                Row(verticalAlignment = Alignment.Top) {
                    IconBox(icon = RondaIcons.lock, tone = Tone.SAFE, size = 40.dp)
                    Spacer(Modifier.width(14.dp))
                    Text(
                        text = stringResource(R.string.setup_privacy_note),
                        style = MaterialTheme.typography.bodyLarge,
                        color = colors.textPrimary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        item {
            Spacer(Modifier.height(28.dp))
            // Not red: red is spent on flagged apps and the uninstall only.
            // Unpairing is reversible with a new code, so it is an ordinary
            // outline action behind a confirmation.
            SecondaryButton(
                text = stringResource(R.string.settings_disconnect),
                onClick = { disconnecting = true },
                icon = RondaIcons.logOut,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.settings_version, version),
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }
    }

    if (renaming) {
        RenameSheet(
            current = protectedName,
            onSave = { onRename(it); renaming = false },
            onDismiss = { renaming = false }
        )
    }

    if (disconnecting) {
        ConfirmSheet(
            title = stringResource(R.string.disconnect_title, protectedName),
            body = stringResource(R.string.disconnect_body, protectedName),
            confirmText = stringResource(R.string.disconnect_confirm),
            confirmTone = Tone.TRUST,
            confirmIcon = RondaIcons.logOut,
            onConfirm = { disconnecting = false; onDisconnect() },
            onDismiss = { disconnecting = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun RenameSheet(current: String, onSave: (String) -> Unit, onDismiss: () -> Unit) {
    val colors = RondaTheme.colors
    val options = stringArrayResource(R.array.nickname_options).toList()
    var chosen by rememberSaveable { mutableStateOf(current) }
    var custom by rememberSaveable { mutableStateOf(if (current in options) "" else current) }
    val value = custom.trim().ifBlank { chosen }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = colors.card,
        contentColor = colors.textPrimary,
        shape = RoundedCornerShape(topStart = RondaRadius.sheet, topEnd = RondaRadius.sheet)
    ) {
        Column(Modifier.padding(start = 24.dp, end = 24.dp, bottom = 32.dp)) {
            Text(
                text = stringResource(R.string.rename_title),
                style = MaterialTheme.typography.headlineMedium,
                color = colors.textPrimary
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.rename_body),
                style = MaterialTheme.typography.bodyLarge,
                color = colors.textSecondary
            )
            Spacer(Modifier.height(20.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                options.forEach { option ->
                    Chip(
                        text = option,
                        selected = custom.isBlank() && chosen == option,
                        onClick = { chosen = option; custom = "" }
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            RondaTextField(
                value = custom,
                onValueChange = { custom = it.take(24) },
                placeholder = stringResource(R.string.pair_nickname_placeholder),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { if (value.isNotBlank()) onSave(value) })
            )
            Spacer(Modifier.height(24.dp))
            TactileButton(
                text = stringResource(R.string.rename_save),
                onClick = { onSave(value) },
                enabled = value.isNotBlank(),
                tone = Tone.TRUST,
                icon = RondaIcons.check,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
