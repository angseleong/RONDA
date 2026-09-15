package com.ronda.app.ui.protectedrole

import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ronda.app.R
import com.ronda.app.pairing.ClaimResult
import com.ronda.app.pairing.PairingRepository
import com.ronda.app.pairing.QrCodeUtils
import com.ronda.app.ui.theme.LargePrint
import com.ronda.app.ui.theme.LargePrintLabel
import com.ronda.app.ui.theme.PairingCode
import com.ronda.app.ui.theme.lamp
import kotlinx.coroutines.launch

/**
 * Protected side of pairing: type the code the guardian reads out.
 *
 * Every failure gets its own sentence. "Kode tidak ditemukan" and "kode sudah
 * dipakai" call for completely different actions, and a single generic error
 * would leave an older user retyping a code that can never work.
 *
 * A wrong code is amber, not red: it asks for a second look, it is not a
 * danger. Red stays reserved for DARURAT.
 */
@Composable
fun ProtectedPairingScreen(
    protectedDeviceId: String,
    onPaired: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val repository = remember { PairingRepository() }
    val scope = rememberCoroutineScope()

    var input by remember { mutableStateOf("") }
    var errorRes by remember { mutableStateOf<Int?>(null) }
    var connecting by remember { mutableStateOf(false) }

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
                is ClaimResult.Success -> onPaired(code)
                is ClaimResult.NotFound -> errorRes = R.string.pair_error_notfound
                is ClaimResult.AlreadyUsed -> errorRes = R.string.pair_error_used
                is ClaimResult.Expired -> errorRes = R.string.pair_error_expired
                is ClaimResult.Failed -> errorRes = R.string.pair_error_notfound
            }
            connecting = false
        }
    }

    // Edge-to-edge means the keyboard no longer resizes the window on its own;
    // the screen has to make room itself or the button ends up under it. The
    // navigation bar is already paid for by the Scaffold padding in the
    // modifier, so it is taken back out of the keyboard's height.
    val keyboard = WindowInsets.ime.exclude(WindowInsets.navigationBars)

    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(keyboard)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Text(
            text = stringResource(R.string.pair_protected_title),
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.pair_protected_step),
            style = LargePrint,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(28.dp))

        // Read once so the slot lambda captures a plain value, not the delegate.
        val currentError = errorRes
        OutlinedTextField(
            value = input,
            onValueChange = {
                // Uppercase as they type so the field always matches what is
                // printed on the other phone.
                input = it.uppercase().take(10)
                errorRes = null
            },
            label = { Text(stringResource(R.string.pair_protected_field)) },
            singleLine = true,
            isError = errorRes != null,
            enabled = !connecting,
            textStyle = PairingCode.copy(
                fontSize = 32.sp,
                lineHeight = 40.sp,
                letterSpacing = 6.sp,
                textAlign = TextAlign.Center
            ),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Characters,
                keyboardType = KeyboardType.Ascii,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { submit() }),
            supportingText = if (currentError == null) null else {
                { Text(text = stringResource(currentError), style = LargePrint) }
            },
            shape = MaterialTheme.shapes.medium,
            colors = OutlinedTextFieldDefaults.colors(
                errorBorderColor = lamp,
                errorLabelColor = lamp,
                errorCursorColor = lamp,
                errorSupportingTextColor = lamp
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        // Stays lit while connecting so the progress reads as progress, not as
        // a greyed-out control; the click guard is what stops a second submit.
        Button(
            onClick = { if (!connecting) submit() },
            enabled = connecting || input.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
        ) {
            if (connecting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = LocalContentColor.current,
                    strokeWidth = 2.5.dp
                )
                Spacer(Modifier.width(12.dp))
            }
            Text(
                text = stringResource(
                    if (connecting) R.string.pair_connecting else R.string.pair_protected_submit
                ),
                style = LargePrintLabel
            )
        }
    }
}
