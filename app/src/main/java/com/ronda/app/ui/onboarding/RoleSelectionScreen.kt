package com.ronda.app.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ronda.app.R
import com.ronda.app.pairing.Role
import com.ronda.app.ui.components.ChoiceCard
import com.ronda.app.ui.components.RondaIcon
import com.ronda.app.ui.components.RondaIcons
import com.ronda.app.ui.components.TactileButton
import com.ronda.app.ui.components.Wordmark
import com.ronda.app.ui.components.screenInsets
import com.ronda.app.ui.theme.LargePrint
import com.ronda.app.ui.theme.LargePrintTitle
import com.ronda.app.ui.theme.RondaDepth
import com.ronda.app.ui.theme.RondaTheme
import com.ronda.app.ui.theme.Tone

/**
 * Which phone is this? The wording avoids "guardian" and "protected" — an older
 * user does not think of themselves as a protected node. They think "this is
 * my phone" and "my child watches over it", so the options are phrased that way.
 *
 * Two answers, one button: the cards select, the button commits. Both cards
 * carry equal weight on purpose; the honest answer depends on whose hand the
 * phone is in. The button lives in a footer below the scroll, always on screen.
 */
@Composable
fun RoleSelectionScreen(
    onRoleChosen: (Role) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = RondaTheme.colors
    var selected by rememberSaveable { mutableStateOf<Role?>(null) }

    Column(
        modifier
            .fillMaxSize()
            .screenInsets()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(start = 24.dp, end = 24.dp, top = 20.dp, bottom = 24.dp)
        ) {
            Wordmark()

            Spacer(Modifier.height(32.dp))
            Text(
                text = stringResource(R.string.role_title),
                style = LargePrintTitle,
                color = colors.textPrimary
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.role_subtitle),
                style = LargePrint,
                color = colors.textSecondary
            )

            Spacer(Modifier.height(28.dp))
            ChoiceCard(
                icon = RondaIcons.smartphone,
                title = stringResource(R.string.role_protected_title),
                body = stringResource(R.string.role_protected_body),
                selected = selected == Role.PROTECTED,
                onClick = { selected = Role.PROTECTED },
                large = true
            )
            Spacer(Modifier.height(12.dp))
            ChoiceCard(
                icon = RondaIcons.users,
                title = stringResource(R.string.role_guardian_title),
                body = stringResource(R.string.role_guardian_body),
                selected = selected == Role.GUARDIAN,
                onClick = { selected = Role.GUARDIAN },
                large = true
            )

            Spacer(Modifier.height(20.dp))
            Row(verticalAlignment = Alignment.Top) {
                RondaIcon(
                    id = RondaIcons.circleInfo,
                    contentDescription = null,
                    tint = colors.textSecondary,
                    size = 20.dp,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.role_note),
                    style = LargePrint,
                    color = colors.textSecondary
                )
            }
        }

        val choice = selected
        // A hairline marks the footer as its own bar, so content scrolling
        // beneath it reads as passing under a shelf, not as cut off.
        Box(
            Modifier
                .fillMaxWidth()
                .height(RondaDepth.border)
                .background(colors.border)
        )
        TactileButton(
            text = stringResource(R.string.role_continue),
            onClick = { if (choice != null) onRoleChosen(choice) },
            enabled = choice != null,
            icon = RondaIcons.arrowRight,
            large = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 20.dp)
        )
    }
}
