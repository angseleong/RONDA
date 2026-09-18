package com.ronda.app.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ronda.app.AppLanguage
import com.ronda.app.R
import com.ronda.app.ui.components.ChoiceCard
import com.ronda.app.ui.components.IconBox
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
 * The very first screen. Each option is written in its own language, whatever
 * the phone speaks, because the person reading it may not read the other one.
 *
 * The button lives in a footer below the scroll, so the way forward is on
 * screen whatever the font size — the person reading this may be old.
 */
@Composable
fun LanguageScreen(
    onChosen: (AppLanguage) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = RondaTheme.colors
    var selected by rememberSaveable { mutableStateOf(AppLanguage.current()) }

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
            IconBox(icon = RondaIcons.languages, tone = Tone.TRUST, size = 56.dp)
            Spacer(Modifier.height(20.dp))
            Text(
                text = stringResource(R.string.lang_title),
                style = LargePrintTitle,
                color = colors.textPrimary
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.lang_body),
                style = LargePrint,
                color = colors.textSecondary
            )

            Spacer(Modifier.height(28.dp))
            ChoiceCard(
                icon = RondaIcons.globe,
                title = stringResource(R.string.lang_indonesian),
                body = stringResource(R.string.lang_indonesian_body),
                selected = selected == AppLanguage.INDONESIAN,
                onClick = { selected = AppLanguage.INDONESIAN },
                large = true
            )
            Spacer(Modifier.height(12.dp))
            ChoiceCard(
                icon = RondaIcons.globe,
                title = stringResource(R.string.lang_english),
                body = stringResource(R.string.lang_english_body),
                selected = selected == AppLanguage.ENGLISH,
                onClick = { selected = AppLanguage.ENGLISH },
                large = true
            )
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
            text = stringResource(R.string.lang_continue),
            onClick = { onChosen(selected) },
            icon = RondaIcons.arrowRight,
            large = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 20.dp)
        )
    }
}
