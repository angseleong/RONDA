package com.ronda.app.ui.protectedrole

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ronda.app.R
import com.ronda.app.pairing.ClaimResult
import com.ronda.app.pairing.PairingRepository
import com.ronda.app.pairing.QrCodeUtils
import kotlinx.coroutines.launch

/**
 * Protected side of pairing: type the code the guardian reads out.
 *
 * Every failure gets its own sentence. "Kode tidak ditemukan" and "kode sudah
 * dipakai" call for completely different actions, and a single generic error
 * would leave an older user retyping a code that can never work.
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = stringResource(R.string.pair_protected_title),
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(10.dp))

        Text(
            text = stringResource(R.string.pair_protected_step),
            fontSize = 17.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(28.dp))

        OutlinedTextField(
            value = input,
            onValueChange = {
                // Uppercase as they type so the field always matches what is
                // printed on the other phone.
                input = it.uppercase().take(10)
                errorRes = null
            },
            label = { Text(stringResource(R.string.pair_protected_field), fontSize = 16.sp) },
            singleLine = true,
            isError = errorRes != null,
            enabled = !connecting,
            textStyle = TextStyle(
                fontSize = 32.sp,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 6.sp,
                textAlign = TextAlign.Center
            ),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Characters
            ),
            modifier = Modifier.fillMaxWidth()
        )

        errorRes?.let {
            Spacer(Modifier.height(10.dp))
            Text(
                text = stringResource(it),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = ::submit,
            enabled = !connecting && input.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(
                    if (connecting) R.string.pair_connecting else R.string.pair_protected_submit
                ),
                fontSize = 18.sp
            )
        }

        if (connecting) {
            Spacer(Modifier.height(20.dp))
            CircularProgressIndicator()
        }
    }
}
