package com.ronda.app.ui.protectedrole

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ronda.app.R
import com.ronda.app.ui.components.IconBox
import com.ronda.app.ui.components.RondaCard
import com.ronda.app.ui.components.RondaIcons
import com.ronda.app.ui.components.RondaTopBar
import com.ronda.app.ui.components.TactileButton
import com.ronda.app.ui.components.TextAction
import com.ronda.app.ui.components.Wordmark
import com.ronda.app.ui.components.screenInsets
import com.ronda.app.ui.theme.LargePrint
import com.ronda.app.ui.theme.LargePrintTitle
import com.ronda.app.ui.theme.RondaTheme
import com.ronda.app.ui.theme.Tone

/**
 * What the protected phone shows once the guardian asks for an app to be removed.
 *
 * Android will not let RONDA uninstall anything on its own — the system dialog
 * that follows is the only way, and it is deliberately terse and generic. This
 * screen is the explanation that dialog does not give: who asked, which app, and
 * why. Without it, an older user meets a bare "Do you want to uninstall this
 * app?" with no idea where it came from, which is exactly the confusion scammers
 * rely on.
 *
 * Red is spent once, on the card that names the app and on the button that
 * removes it — the one irreversible action DESIGN.md allows a red button for.
 */
@Composable
fun UninstallPromptScreen(
    appLabel: String,
    onConfirm: () -> Unit,
    onLater: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = RondaTheme.colors

    // System Back means the same as "Nanti saja": the prompt comes back later
    // and the app underneath stays blocked either way.
    BackHandler(onBack = onLater)

    Column(
        modifier
            .fillMaxSize()
            .screenInsets()
    ) {
        RondaTopBar(leading = { Wordmark() })

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.uninstall_prompt_title),
                style = LargePrintTitle,
                color = colors.textPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))
            RondaCard(tone = Tone.DANGER) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconBox(icon = RondaIcons.triangleWarning, tone = Tone.DANGER, size = 72.dp)
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = appLabel,
                        style = MaterialTheme.typography.displayMedium,
                        color = colors.textPrimary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = stringResource(R.string.uninstall_prompt_body),
                        style = LargePrint,
                        color = colors.textPrimary,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.uninstall_prompt_next),
                style = LargePrint,
                color = colors.textSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(28.dp))
            TactileButton(
                text = stringResource(R.string.uninstall_prompt_confirm),
                onClick = onConfirm,
                tone = Tone.DANGER,
                icon = RondaIcons.trash,
                large = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))
            // The app underneath stays blocked either way, so deferring is safe.
            TextAction(
                text = stringResource(R.string.uninstall_prompt_later),
                onClick = onLater,
                large = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}
