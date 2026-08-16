package com.ronda.app.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ronda.app.R
import com.ronda.app.pairing.Role
import com.ronda.app.ui.theme.ProtectedBody
import com.ronda.app.ui.theme.ProtectedSubtitle
import com.ronda.app.ui.theme.ProtectedTitle

/**
 * First screen on a fresh install: which phone is this?
 *
 * The wording avoids "guardian" and "protected" — an older user does not think
 * of themselves as a protected node. They think "this is my phone" and "my child
 * watches over it", so the options are phrased that way.
 *
 * The two cards are visually unequal on purpose. This screen is most often
 * driven by the child, sitting next to the parent, setting up the parent's phone
 * first — so that option is the filled one.
 */
@Composable
fun RoleSelectionScreen(
    onRoleChosen: (Role) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 32.dp)
    ) {
        Text(
            text = stringResource(R.string.watch_title),
            style = ProtectedTitle,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.role_title),
            style = ProtectedSubtitle,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(10.dp))

        Text(
            text = stringResource(R.string.role_subtitle),
            style = ProtectedBody,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(28.dp))

        RoleCard(
            title = stringResource(R.string.role_protected_title),
            body = stringResource(R.string.role_protected_body),
            primary = true,
            onChoose = { onRoleChosen(Role.PROTECTED) }
        )

        Spacer(Modifier.height(16.dp))

        RoleCard(
            title = stringResource(R.string.role_guardian_title),
            body = stringResource(R.string.role_guardian_body),
            primary = false,
            onChoose = { onRoleChosen(Role.GUARDIAN) }
        )
    }
}

@Composable
private fun RoleCard(
    title: String,
    body: String,
    primary: Boolean,
    onChoose: () -> Unit
) {
    Surface(
        color = if (primary) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (primary) MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                else MaterialTheme.colorScheme.outlineVariant,
                RoundedCornerShape(20.dp)
            )
    ) {
        Column(Modifier.padding(22.dp)) {
            Text(
                text = title,
                style = ProtectedSubtitle,
                color = if (primary) MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = body,
                style = ProtectedBody,
                color = if (primary) MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(20.dp))

            val label = stringResource(R.string.role_choose)
            if (primary) {
                Button(
                    onClick = onChoose,
                    modifier = Modifier.fillMaxWidth().height(58.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(label, fontSize = 19.sp)
                }
            } else {
                OutlinedButton(
                    onClick = onChoose,
                    modifier = Modifier.fillMaxWidth().height(58.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(label, fontSize = 19.sp)
                }
            }
        }
    }
}
