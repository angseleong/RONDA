package com.ronda.app.ui.protectedrole

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ronda.app.R
import com.ronda.app.pairing.ClaimResult
import com.ronda.app.pairing.PairingRepository
import com.ronda.app.pairing.QrCodeUtils
import com.ronda.app.ui.components.IconBox
import com.ronda.app.ui.components.RondaIcon
import com.ronda.app.ui.components.RondaIcons
import com.ronda.app.ui.components.TactileButton
import com.ronda.app.ui.components.Wordmark
import com.ronda.app.ui.components.screenInsets
import com.ronda.app.ui.theme.LargePrint
import com.ronda.app.ui.theme.LargePrintTitle
import com.ronda.app.ui.theme.PairingCode
import com.ronda.app.ui.theme.RondaDepth
import com.ronda.app.ui.theme.RondaRadius
import com.ronda.app.ui.theme.RondaTheme
import com.ronda.app.ui.theme.Tone
import kotlinx.coroutines.launch

/**
 * Protected side of pairing: type the code the guardian reads out.
 *
 * Every failure gets its own sentence. "Kode tidak ditemukan" and "kode sudah
 * dipakai" call for completely different actions, and a single generic error
 * would leave an older user retyping a code that can never work.
 *
 * A wrong code is amber, not red: it asks for a second look, it is not a danger.
 */
@Composable
fun ProtectedPairingScreen(
    protectedDeviceId: String,
    /** The claimed code and the guardian's name, read from the pairing record. */
    onPaired: (code: String, guardianName: String) -> Unit,
    modifier: Modifier = Modifier,
    /** Code read from a scanned `ronda://pair/…` QR, already validated. */
    scannedCode: String? = null
) {
    val colors = RondaTheme.colors
    val repository = remember { PairingRepository() }
    val scope = rememberCoroutineScope()

    var input by remember { mutableStateOf("") }
    var errorRes by remember { mutableStateOf<Int?>(null) }
    var connecting by remember { mutableStateOf(false) }

    // A scan fills the field and stops there. Pairing still needs the button,
    // so the person holding the phone is the one who agrees to be watched —
    // a link arriving on its own must never be able to pair a device (PRD FR-2).
    // Keyed on the code so a second scan of a *different* QR replaces a stale
    // one, while retyping over the same code is left alone.
    LaunchedEffect(scannedCode) {
        if (!scannedCode.isNullOrBlank()) {
            input = scannedCode
            errorRes = null
        }
    }

    fun submit() {
        val code = QrCodeUtils.normalizeCode(input)
        if (!QrCodeUtils.isValidCode(code)) {
            errorRes = R.string.pair_error_invalid
            return
        }

        errorRes = null
        connecting = true
        scope.launch {
            when (val result = repository.claimPairing(code, protectedDeviceId)) {
                is ClaimResult.Success -> onPaired(code, result.guardianName)
                is ClaimResult.NotFound -> errorRes = R.string.pair_error_notfound
                is ClaimResult.AlreadyUsed -> errorRes = R.string.pair_error_used
                is ClaimResult.Expired -> errorRes = R.string.pair_error_expired
                is ClaimResult.Failed -> errorRes = R.string.pair_error_offline
            }
            connecting = false
        }
    }

    // Edge-to-edge means the keyboard no longer resizes the window on its own;
    // the screen makes room itself or the button ends up under it.
    val keyboard = WindowInsets.ime.exclude(WindowInsets.navigationBars)

    Column(
        modifier = modifier
            .fillMaxSize()
            .screenInsets()
            .windowInsetsPadding(keyboard)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        Wordmark()

        Spacer(Modifier.height(36.dp))
        IconBox(icon = RondaIcons.link, tone = Tone.TRUST, size = 56.dp)
        Spacer(Modifier.height(20.dp))
        Text(
            text = stringResource(R.string.pair_protected_title),
            style = LargePrintTitle,
            color = colors.textPrimary
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.pair_protected_step),
            style = LargePrint,
            color = colors.textSecondary
        )

        Spacer(Modifier.height(28.dp))
        // Large print even for a field label: nothing on a protected screen
        // drops below 20sp (PRD usability floor).
        Text(
            text = stringResource(R.string.pair_protected_field),
            style = LargePrint,
            color = colors.textPrimary
        )
        Spacer(Modifier.height(8.dp))
        CodeField(
            value = input,
            onValueChange = {
                // Uppercase as they type so the field always matches what is
                // printed on the other phone.
                input = it.uppercase().take(10)
                errorRes = null
            },
            error = errorRes != null,
            enabled = !connecting,
            onDone = { submit() }
        )

        // Says what just happened, in the plainest terms available: the code is
        // in, nothing has been connected yet, press the button. Without it the
        // field simply fills itself and an older user has no idea why.
        if (errorRes == null && scannedCode != null && input == scannedCode) {
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.Top) {
                RondaIcon(
                    id = RondaIcons.circleCheck,
                    contentDescription = null,
                    tint = colors.trust,
                    size = 22.dp,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.pair_protected_scanned),
                    style = LargePrint,
                    color = colors.trust
                )
            }
        }

        val currentError = errorRes
        if (currentError != null) {
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.Top) {
                RondaIcon(
                    id = RondaIcons.circleInfo,
                    contentDescription = null,
                    tint = colors.warn,
                    size = 22.dp,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = stringResource(currentError),
                    style = LargePrint,
                    color = colors.warn
                )
            }
        }

        Spacer(Modifier.height(28.dp))
        // Stays lit while connecting so the progress reads as progress, not as
        // a greyed-out control; the click guard is what stops a second submit.
        TactileButton(
            text = stringResource(
                if (connecting) R.string.pair_connecting else R.string.pair_protected_submit
            ),
            onClick = { if (!connecting) submit() },
            enabled = connecting || input.isNotBlank(),
            loading = connecting,
            icon = RondaIcons.link,
            large = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * The code field, in the system's own vocabulary: a bordered box that turns
 * trust-blue on focus and amber on error, the code set in the same face and
 * tracking the guardian's screen prints it in.
 */
@Composable
private fun CodeField(
    value: String,
    onValueChange: (String) -> Unit,
    error: Boolean,
    enabled: Boolean,
    onDone: () -> Unit
) {
    val colors = RondaTheme.colors
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val shape = RoundedCornerShape(RondaRadius.button)
    val edge = when {
        error -> colors.warn
        focused -> colors.trust
        else -> colors.borderStrong
    }
    val label = stringResource(R.string.pair_protected_field)

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        singleLine = true,
        interactionSource = interaction,
        textStyle = PairingCode.copy(color = colors.textPrimary, textAlign = TextAlign.Center),
        cursorBrush = SolidColor(colors.trust),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Characters,
            keyboardType = KeyboardType.Ascii,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = label },
        decorationBox = { inner ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 84.dp)
                    .background(colors.card, shape)
                    .border(RondaDepth.border, edge, shape)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = "······",
                        style = PairingCode,
                        color = colors.textMuted,
                        textAlign = TextAlign.Center
                    )
                }
                inner()
            }
        }
    )
}
