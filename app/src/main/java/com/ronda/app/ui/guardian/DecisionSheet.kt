package com.ronda.app.ui.guardian

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ronda.app.R
import com.ronda.app.ui.theme.lamp
import com.ronda.app.ui.theme.night
import com.ronda.app.ui.theme.nightRaised

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
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(),
        containerColor = nightRaised
    ) {
        Column(Modifier.padding(start = 24.dp, end = 24.dp, bottom = 32.dp)) {
            Text(
                text = stringResource(R.string.confirm_safe_body, protectedName),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = lamp, contentColor = night)
            ) {
                Text(stringResource(R.string.confirm_safe_yes))
            }

            Spacer(Modifier.height(10.dp))

            OutlinedButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.confirm_safe_cancel))
            }
        }
    }
}
