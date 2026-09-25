package com.ronda.app.ui.onboarding

import androidx.annotation.DrawableRes
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ronda.app.R
import com.ronda.app.ui.components.CardDivider
import com.ronda.app.ui.components.BrandMark
import com.ronda.app.ui.components.IconBox
import com.ronda.app.ui.components.RondaCard
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
 * The value proposition, once. It names the scam before it names the product,
 * because the person reading this has probably received the exact WhatsApp
 * message it describes.
 *
 * The button lives in a footer below the scroll: the three points are worth
 * reading, but the way forward must never depend on finding the end.
 */
@Composable
fun IntroScreen(
    onStart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = RondaTheme.colors

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

            Spacer(Modifier.height(28.dp))
            BrandMark(height = 72.dp)
            Spacer(Modifier.height(20.dp))
            Text(
                text = stringResource(R.string.intro_title),
                style = LargePrintTitle,
                color = colors.textPrimary
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.intro_body),
                style = LargePrint,
                color = colors.textSecondary
            )

            Spacer(Modifier.height(28.dp))
            RondaCard {
                Step(
                    icon = RondaIcons.download,
                    title = stringResource(R.string.intro_point_1_title),
                    body = stringResource(R.string.intro_point_1_body)
                )
                Spacer(Modifier.height(16.dp))
                CardDivider()
                Spacer(Modifier.height(16.dp))
                Step(
                    icon = RondaIcons.bell,
                    title = stringResource(R.string.intro_point_2_title),
                    body = stringResource(R.string.intro_point_2_body)
                )
                Spacer(Modifier.height(16.dp))
                CardDivider()
                Spacer(Modifier.height(16.dp))
                Step(
                    icon = RondaIcons.shieldAlert,
                    title = stringResource(R.string.intro_point_3_title),
                    body = stringResource(R.string.intro_point_3_body)
                )
            }

            Spacer(Modifier.height(20.dp))
            Row(verticalAlignment = Alignment.Top) {
                RondaIcon(
                    id = RondaIcons.lock,
                    contentDescription = null,
                    tint = colors.safe,
                    size = 22.dp,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = stringResource(R.string.intro_privacy),
                    style = LargePrint,
                    color = colors.textSecondary
                )
            }
        }

        // A hairline marks the footer as its own bar, so content scrolling
        // beneath it reads as passing under a shelf, not as cut off.
        Box(
            Modifier
                .fillMaxWidth()
                .height(RondaDepth.border)
                .background(colors.border)
        )
        TactileButton(
            text = stringResource(R.string.intro_start),
            onClick = onStart,
            icon = RondaIcons.arrowRight,
            large = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 20.dp)
        )
    }
}

@Composable
private fun Step(@DrawableRes icon: Int, title: String, body: String) {
    val colors = RondaTheme.colors
    Row(verticalAlignment = Alignment.Top) {
        IconBox(icon = icon, tone = Tone.SAFE, size = 44.dp)
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = colors.textPrimary
            )
            Spacer(Modifier.height(4.dp))
            Text(text = body, style = LargePrint, color = colors.textSecondary)
        }
    }
}
