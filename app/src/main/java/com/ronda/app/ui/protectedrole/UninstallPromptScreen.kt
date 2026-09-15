package com.ronda.app.ui.protectedrole

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ronda.app.R
import com.ronda.app.ui.theme.LargePrint
import com.ronda.app.ui.theme.LargePrintLabel
import com.ronda.app.ui.theme.siren
import com.ronda.app.ui.theme.wash

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
 * Type is large and the contrast is high for the same reason the overlay is.
 * Red is spent once, on the frame around the app's name: that is the danger.
 * The button that removes it is the ordinary amber of every protective action.
 */
@Composable
fun UninstallPromptScreen(
    appLabel: String,
    onConfirm: () -> Unit,
    onLater: () -> Unit,
    modifier: Modifier = Modifier
) {
    // System Back means the same as "Nanti saja": the prompt comes back later
    // and the app underneath stays blocked either way.
    BackHandler(onBack = onLater)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.uninstall_prompt_title),
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(24.dp))

        Surface(
            color = wash(siren),
            shape = MaterialTheme.shapes.large,
            border = BorderStroke(1.dp, siren.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = appLabel,
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.uninstall_prompt_body),
                    style = LargePrint,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(Modifier.height(28.dp))

        Text(
            text = stringResource(R.string.uninstall_prompt_next),
            style = LargePrint,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(28.dp))

        Button(
            onClick = onConfirm,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
        ) {
            Text(stringResource(R.string.uninstall_prompt_confirm), style = LargePrintLabel)
        }

        Spacer(Modifier.height(8.dp))

        // The app underneath stays blocked either way, so deferring is safe.
        TextButton(
            onClick = onLater,
            modifier = Modifier.heightIn(min = 48.dp)
        ) {
            Text(stringResource(R.string.uninstall_prompt_later), style = LargePrintLabel)
        }
    }
}
