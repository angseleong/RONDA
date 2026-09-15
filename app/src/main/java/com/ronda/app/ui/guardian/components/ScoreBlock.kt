package com.ronda.app.ui.guardian.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import com.ronda.app.R
import com.ronda.app.core.RiskLevel
import com.ronda.app.ui.theme.ScoreNumeral
import com.ronda.app.ui.theme.linenDim
import com.ronda.app.ui.theme.scoreColor

/**
 * The first thing the eye lands on. A 72sp numeral in the band's colour, so the
 * guardian can triage from across a room before reading a word.
 *
 * TalkBack gets one label for the whole block ("Risiko 96 dari 100, Darurat")
 * rather than three fragments read in isolation.
 */
@Composable
fun ScoreBlock(score: Int, level: RiskLevel, modifier: Modifier = Modifier) {
    val label = stringResource(R.string.a11y_score, score, level.name)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clearAndSetSemantics { contentDescription = label },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // "/100" hangs off the numeral's own baseline, not a guessed offset.
        Row {
            Text(
                text = "$score",
                style = ScoreNumeral,
                color = scoreColor(level),
                modifier = Modifier.alignByBaseline()
            )
            Text(
                text = stringResource(R.string.detail_of_100),
                style = MaterialTheme.typography.titleLarge,
                color = linenDim,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .alignByBaseline()
            )
        }
        Text(
            text = level.name,
            style = MaterialTheme.typography.titleLarge,
            color = scoreColor(level)
        )
    }
}
