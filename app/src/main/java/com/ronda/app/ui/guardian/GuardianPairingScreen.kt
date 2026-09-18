package com.ronda.app.ui.guardian

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ronda.app.R
import com.ronda.app.pairing.PairingRepository
import com.ronda.app.pairing.QrCodeUtils
import com.ronda.app.ui.components.Chip
import com.ronda.app.ui.components.IconBox
import com.ronda.app.ui.components.RondaCard
import com.ronda.app.ui.components.RondaIcon
import com.ronda.app.ui.components.RondaIcons
import com.ronda.app.ui.components.RondaTextField
import com.ronda.app.ui.components.SecondaryButton
import com.ronda.app.ui.components.StatusBadge
import com.ronda.app.ui.components.TactileButton
import com.ronda.app.ui.components.TextAction
import com.ronda.app.ui.components.Wordmark
import com.ronda.app.ui.components.screenInsets
import com.ronda.app.ui.theme.PairingCode
import com.ronda.app.ui.theme.RondaRadius
import com.ronda.app.ui.theme.RondaTheme
import com.ronda.app.ui.theme.Tone

/**
 * Guardian side of pairing, in two steps: who is being guarded, then the code.
 *
 * The nickname comes first because every later sentence on this phone uses it
 * ("HP Ibu", "Ibu tetap membuka aplikasi ini"), and a guardian who has just
 * typed it recognises those sentences as their own.
 *
 * The code is shown as large text *and* as a QR. The text is the one that
 * actually gets used — pairing usually happens over a phone call, with the
 * guardian in another city, which is exactly the situation RONDA exists for.
 */
@Composable
fun GuardianPairingScreen(
    guardianDeviceId: String,
    initialGuardianName: String,
    initialNickname: String,
    /** The guardian's own name and what they call the protected person. */
    onIdentityChosen: (guardianName: String, nickname: String) -> Unit,
    onPaired: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var step by rememberSaveable { mutableIntStateOf(0) }
    var guardianName by rememberSaveable { mutableStateOf(initialGuardianName) }
    var nickname by rememberSaveable { mutableStateOf(initialNickname) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .screenInsets()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        Wordmark()
        Spacer(Modifier.height(36.dp))

        AnimatedContent(
            targetState = step,
            transitionSpec = {
                if (targetState > initialState) {
                    (slideInHorizontally { it / 3 } + fadeIn()) togetherWith
                        (slideOutHorizontally { -it / 3 } + fadeOut())
                } else {
                    (slideInHorizontally { -it / 3 } + fadeIn()) togetherWith
                        (slideOutHorizontally { it / 3 } + fadeOut())
                }
            },
            label = "pairingStep"
        ) { current ->
            if (current == 0) {
                IdentityStep(
                    initialGuardianName = guardianName,
                    initialNickname = nickname,
                    onContinue = { name, chosen ->
                        guardianName = name
                        nickname = chosen
                        onIdentityChosen(name, chosen)
                        step = 1
                    }
                )
            } else {
                CodeStep(
                    guardianDeviceId = guardianDeviceId,
                    guardianName = guardianName,
                    nickname = nickname,
                    onChangeName = { step = 0 },
                    onPaired = onPaired
                )
            }
        }
    }
}

/**
 * Two names on one screen: the guardian's own, which the protected phone will
 * show as "Dijaga oleh Rina", and what the guardian calls the protected person,
 * which every sentence on this phone uses.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun IdentityStep(
    initialGuardianName: String,
    initialNickname: String,
    onContinue: (guardianName: String, nickname: String) -> Unit
) {
    val colors = RondaTheme.colors
    val options = stringArrayResource(R.array.nickname_options).toList()
    var guardianName by rememberSaveable { mutableStateOf(initialGuardianName) }
    var chosen by rememberSaveable { mutableStateOf(initialNickname) }
    var custom by rememberSaveable {
        mutableStateOf(if (initialNickname in options) "" else initialNickname)
    }
    val nickname = custom.trim().ifBlank { chosen }
    val ready = guardianName.isNotBlank() && nickname.isNotBlank()

    Column {
        IconBox(icon = RondaIcons.heartHandshake, tone = Tone.TRUST, size = 56.dp)
        Spacer(Modifier.height(20.dp))
        Text(
            text = stringResource(R.string.pair_guardian_name_title),
            style = MaterialTheme.typography.displayMedium,
            color = colors.textPrimary
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.pair_guardian_name_body),
            style = MaterialTheme.typography.bodyLarge,
            color = colors.textSecondary
        )
        Spacer(Modifier.height(16.dp))
        RondaTextField(
            value = guardianName,
            onValueChange = { guardianName = it.take(24) },
            placeholder = stringResource(R.string.pair_guardian_name_placeholder),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            )
        )

        Spacer(Modifier.height(28.dp))
        Text(
            text = stringResource(R.string.pair_nickname_title),
            style = MaterialTheme.typography.headlineMedium,
            color = colors.textPrimary
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.pair_nickname_body),
            style = MaterialTheme.typography.bodyLarge,
            color = colors.textSecondary
        )

        Spacer(Modifier.height(16.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            options.forEach { option ->
                Chip(
                    text = option,
                    selected = custom.isBlank() && chosen == option,
                    onClick = {
                        chosen = option
                        custom = ""
                    }
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.pair_nickname_field).uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = colors.textSecondary
        )
        Spacer(Modifier.height(8.dp))
        RondaTextField(
            value = custom,
            onValueChange = { custom = it.take(24) },
            placeholder = stringResource(R.string.pair_nickname_placeholder),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                if (ready) onContinue(guardianName.trim(), nickname)
            })
        )

        Spacer(Modifier.height(32.dp))
        TactileButton(
            text = stringResource(R.string.pair_nickname_continue),
            onClick = { onContinue(guardianName.trim(), nickname) },
            enabled = ready,
            icon = RondaIcons.arrowRight,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun CodeStep(
    guardianDeviceId: String,
    guardianName: String,
    nickname: String,
    onChangeName: () -> Unit,
    onPaired: (String) -> Unit
) {
    val colors = RondaTheme.colors
    val repository = remember { PairingRepository() }
    var code by remember { mutableStateOf<String?>(null) }
    var failed by remember { mutableStateOf(false) }
    var attempt by remember { mutableIntStateOf(0) }

    LaunchedEffect(guardianDeviceId, attempt) {
        failed = false
        val newCode = QrCodeUtils.newPairingCode()
        runCatching { repository.createPairing(newCode, guardianDeviceId, guardianName) }
            .onSuccess { code = newCode }
            .onFailure { failed = true }
    }

    LaunchedEffect(code) {
        val current = code ?: return@LaunchedEffect
        repository.observePairing(current).collect { pairing ->
            if (pairing?.isActive == true) onPaired(current)
        }
    }

    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            StatusBadge(
                text = stringResource(R.string.pair_guardian_for, nickname),
                tone = Tone.TRUST,
                icon = RondaIcons.smartphone,
                filled = false
            )
            Spacer(Modifier.weight(1f))
            TextAction(text = stringResource(R.string.pair_guardian_change_name), onClick = onChangeName)
        }

        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.pair_guardian_title),
            style = MaterialTheme.typography.displayMedium,
            color = colors.textPrimary
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.pair_guardian_step, nickname),
            style = MaterialTheme.typography.bodyLarge,
            color = colors.textSecondary
        )

        Spacer(Modifier.height(24.dp))
        val currentCode = code
        when {
            failed -> FailedCard(onRetry = { attempt++ })
            currentCode == null -> Waiting(stringResource(R.string.pair_creating))
            else -> CodeCards(currentCode)
        }
    }
}

@Composable
private fun CodeCards(code: String) {
    val colors = RondaTheme.colors
    // 640px is regenerated only when the code changes, so this never runs per frame.
    val qr = remember(code) { QrCodeUtils.qrBitmap(code) }

    RondaCard(tone = Tone.TRUST) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.pair_guardian_code_label).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = colors.trust
            )
            Spacer(Modifier.height(10.dp))
            // Long-press to copy: the code is as often pasted into a chat as
            // read down the phone.
            SelectionContainer {
                Text(
                    text = code,
                    style = PairingCode,
                    color = colors.textPrimary,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.pair_guardian_expiry),
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary
            )
        }
    }

    Spacer(Modifier.height(16.dp))
    RondaCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // White plate behind the QR: in dark theme the quiet zone would
            // otherwise blend into the card and scanners would fail to find it.
            Image(
                bitmap = qr,
                contentDescription = stringResource(R.string.pair_qr_desc),
                modifier = Modifier
                    .size(112.dp)
                    .background(Color.White, RoundedCornerShape(RondaRadius.iconBoxSmall))
                    .padding(6.dp)
            )
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RondaIcon(RondaIcons.qrCode, contentDescription = null, tint = colors.textSecondary, size = 18.dp)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.pair_guardian_or_scan_label).uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textSecondary
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.pair_guardian_or_scan),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary
                )
            }
        }
    }

    Spacer(Modifier.height(24.dp))
    Waiting(stringResource(R.string.pair_guardian_waiting))
}

@Composable
private fun FailedCard(onRetry: () -> Unit) {
    val colors = RondaTheme.colors
    RondaCard(tone = Tone.WARN) {
        Row(verticalAlignment = Alignment.Top) {
            IconBox(icon = RondaIcons.wifiOff, tone = Tone.WARN, size = 44.dp)
            Spacer(Modifier.width(14.dp))
            Text(
                text = stringResource(R.string.pair_error_offline),
                style = MaterialTheme.typography.bodyLarge,
                color = colors.textPrimary,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(16.dp))
        SecondaryButton(
            text = stringResource(R.string.pair_retry),
            onClick = onRetry,
            icon = RondaIcons.undo,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/** A small spinner beside its sentence, rather than a large one above it. */
@Composable
private fun Waiting(text: String) {
    val colors = RondaTheme.colors
    Row(verticalAlignment = Alignment.CenterVertically) {
        CircularProgressIndicator(
            modifier = Modifier.size(22.dp),
            color = colors.trust,
            strokeWidth = 2.5.dp
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = colors.textPrimary
        )
    }
}
