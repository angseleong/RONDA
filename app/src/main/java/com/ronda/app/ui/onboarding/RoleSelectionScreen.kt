package com.ronda.app.ui.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ronda.app.R
import com.ronda.app.pairing.Role

/**
 * First screen on a fresh install: which phone is this?
 *
 * The wording avoids "guardian" and "protected" — an older user does not think
 * of themselves as a protected node. They think "this is my phone" and "my child
 * watches over it", so the options are phrased that way.
 */
@Composable
fun RoleSelectionScreen(
    onRoleChosen: (Role) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = stringResource(R.string.role_title),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(10.dp))

        Text(
            text = stringResource(R.string.role_subtitle),
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(28.dp))

        RoleCard(
            title = stringResource(R.string.role_protected_title),
            body = stringResource(R.string.role_protected_body),
            onChoose = { onRoleChosen(Role.PROTECTED) }
        )

        Spacer(Modifier.height(16.dp))

        RoleCard(
            title = stringResource(R.string.role_guardian_title),
            body = stringResource(R.string.role_guardian_body),
            onChoose = { onRoleChosen(Role.GUARDIAN) }
        )
    }
}

@Composable
private fun RoleCard(title: String, body: String, onChoose: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp)) {
            Text(text = title, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(
                text = body,
                fontSize = 17.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))
            Button(onClick = onChoose, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.role_choose), fontSize = 18.sp)
            }
        }
    }
}
