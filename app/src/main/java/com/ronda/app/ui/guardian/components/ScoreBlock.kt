package com.ronda.app.ui.guardian.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ronda.app.R
import com.ronda.app.core.RiskLevel
import com.ronda.app.ui.theme.ScoreNumeral
import com.ronda.app.ui.theme.scoreColor
import com.ronda.app.ui.theme.scoreTint

/**
 * The first thing the eye lands on: a 72sp numeral in the band's colour, so the
 * guardian can triage from across a room before reading a word.
 *
 * The bar underneath is the reason this is a block and not just a number. "92"
 * means nothing on its own to someone who has never seen another score; "92,
 * nearly the full width of the track" is legible on first encounter. It is the
 * cheapest way to teach the scale without a legend.
 *
 * TalkBack gets one label for the whole block ("Risiko 96 dari 100, DARURAT")
 * rather than four fragments read in isolation.
 */
@Composable
fun ScoreBlock(score: Int, level: RiskLevel, modifier: Modifier = Modifier) {
    val color = scoreColor(level)
    val label = stringResource(R.string.a11y_score, score, bandLabel(level))

    Surface(
        color = scoreTint(level),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
            .fillMaxWidth()
            .clearAndSetSemantics { contentDescription = label }
    ) {
        Column(
            modifier = Modifier.padding(vertical = 24.dp, horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(text = "$score", style = ScoreNumeral, color = color)
                Text(
                    text = stringResource(R.string.detail_of_100),
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
                )
            }

            Spacer(Modifier.height(14.dp))
            ScoreTrack(score = score, color = color)

            Spacer(Modifier.height(14.dp))
            Text(
                text = bandLabel(level),
                style = MaterialTheme.typography.titleLarge,
                color = color
            )
        }
    }
}

/** The 0–100 track. Rounded so the fill never reads as a hard progress bar. */
@Composable
private fun ScoreTrack(score: Int, color: androidx.compose.ui.graphics.Color) {
    val shape = RoundedCornerShape(999.dp)
    Box(
        Modifier
            .fillMaxWidth()
            .height(10.dp)
            .background(color.copy(alpha = 0.18f), shape)
    ) {
        Box(
            Modifier
                .fillMaxWidth(score.coerceIn(0, 100) / 100f)
                .height(10.dp)
                .background(color, shape)
        )
    }
}
