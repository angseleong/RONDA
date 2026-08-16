package com.ronda.app.ui.protectedrole

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ronda.app.ui.theme.ProtectedBody
import com.ronda.app.ui.theme.ProtectedTitle
import com.ronda.app.R

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
 * It is the one screen in the app allowed to be loud. Red is spent here and on
 * the overlay and nowhere else.
 */
@Composable
fun UninstallPromptScreen(
    appLabel: String,
    onConfirm: () -> Unit,
    onLater: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(MaterialTheme.colorScheme.errorContainer, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "!",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.uninstall_prompt_title),
            style = ProtectedTitle,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.error
        )

        Spacer(Modifier.height(24.dp))

        Surface(
            color = MaterialTheme.colorScheme.errorContainer,
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // lineHeight is the whole fix. Without it Compose falls back to
                // the font's own metrics, which at 32sp Bold are tighter than
                // the ascenders need — so "Undangan Pernikahan" wrapped onto two
                // lines that overlapped. App labels are arbitrary length and
                // frequently wrap, so this can never be left to the default.
                Text(
                    text = appLabel,
                    fontSize = 30.sp,
                    lineHeight = 38.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.uninstall_prompt_body),
                    style = ProtectedBody,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // Says what the next screen will look like. The system dialog that
        // follows is unbranded and abrupt; being told it is coming is the
        // difference between confirming and backing out in confusion.
        //
        // On its own tinted ground because it is an instruction about what
        // happens next, not more description of the app — running it as loose
        // centred text let it blur into the red card above it.
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.uninstall_prompt_next),
                style = ProtectedBody,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp)
            )
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = onConfirm,
            modifier = Modifier.fillMaxWidth().height(64.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            )
        ) {
            Text(text = stringResource(R.string.uninstall_prompt_confirm), fontSize = 20.sp)
        }

        Spacer(Modifier.height(10.dp))

        // The app underneath stays blocked either way, so deferring is safe.
        TextButton(onClick = onLater, modifier = Modifier.height(52.dp)) {
            Text(stringResource(R.string.uninstall_prompt_later), fontSize = 18.sp)
        }
    }
}
