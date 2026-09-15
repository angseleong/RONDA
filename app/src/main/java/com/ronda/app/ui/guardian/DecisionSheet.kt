package com.ronda.app.ui.guardian

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ronda.app.R
import com.ronda.app.ui.theme.nightRaised
import kotlinx.coroutines.launch

/**
 * Confirmation for "Tandai aman" only.
 *
 * The asymmetry is the point: marking dangerous keeps protection and needs no
 * confirmation, marking safe removes it and does. Friction belongs on the action
 * that reduces safety, never on the one that preserves it.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DecisionSheet(
    protectedName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    // Let the sheet slide away before the caller removes it, so a tap does not
    // cut the animation off mid-frame.
    fun close(then: () -> Unit) {
        scope.launch { sheetState.hide() }.invokeOnCompletion { then() }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = nightRaised
    ) {
        Column(Modifier.padding(start = 20.dp, end = 20.dp, bottom = 32.dp)) {
            Text(
                text = stringResource(R.string.confirm_safe_body, protectedName),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { close(onConfirm) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
            ) {
                Text(stringResource(R.string.confirm_safe_yes))
            }

            Spacer(Modifier.height(10.dp))

            OutlinedButton(
                onClick = { close(onDismiss) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
            ) {
                Text(stringResource(R.string.confirm_safe_cancel))
            }
        }
    }
}
