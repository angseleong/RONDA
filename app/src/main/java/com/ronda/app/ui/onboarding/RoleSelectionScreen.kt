package com.ronda.app.ui.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.ronda.app.R
import com.ronda.app.pairing.Role
import com.ronda.app.ui.theme.LargePrint
import com.ronda.app.ui.theme.LargePrintLabel
import com.ronda.app.ui.theme.nightRaised

/**
 * First screen on a fresh install: which phone is this?
 *
 * The wording avoids "guardian" and "protected" — an older user does not think
 * of themselves as a protected node. They think "this is my phone" and "my child
 * watches over it", so the options are phrased that way.
 *
 * Both cards carry equal weight on purpose. There is no recommended answer;
 * the honest one depends on whose hand the phone is in.
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
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Text(
            text = stringResource(R.string.role_title),
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.role_subtitle),
            style = LargePrint,
            color = MaterialTheme.colorScheme.onBackground
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
    // Two buttons on screen both read "Pilih"; TalkBack needs to know which.
    val chooseLabel = "${stringResource(R.string.role_choose)}: $title"

    Surface(
        color = nightRaised,
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = body,
                style = LargePrint,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = onChoose,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
                    .semantics { contentDescription = chooseLabel }
            ) {
                Text(stringResource(R.string.role_choose), style = LargePrintLabel)
            }
        }
    }
}
