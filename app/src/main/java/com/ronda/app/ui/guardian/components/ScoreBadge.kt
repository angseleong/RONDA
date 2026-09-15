package com.ronda.app.ui.guardian.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
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
import com.ronda.app.ui.theme.ScoreBadgeNumeral
import com.ronda.app.ui.theme.scoreColor
import com.ronda.app.ui.theme.wash

/**
 * The list-row version. Fixed width with tabular figures so the badges form a
 * clean column and the eye can run down the scores without re-anchoring.
 *
 * The numeral alone would be read by TalkBack as a bare "96", so the badge
 * announces itself the way [ScoreBlock] does — "Risiko 96 dari 100, DARURAT" —
 * and [AlertRow] merges that into the row's single label.
 */
@Composable
fun ScoreBadge(score: Int, level: RiskLevel, modifier: Modifier = Modifier) {
    val color = scoreColor(level)
    val label = stringResource(R.string.a11y_score, score, level.name)
    Box(
        modifier = modifier
            .size(56.dp)
            .background(wash(color), MaterialTheme.shapes.medium)
            .clearAndSetSemantics { contentDescription = label },
        contentAlignment = Alignment.Center
    ) {
        Text(text = "$score", style = ScoreBadgeNumeral, color = color)
    }
}
