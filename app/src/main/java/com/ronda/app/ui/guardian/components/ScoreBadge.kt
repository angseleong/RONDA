package com.ronda.app.ui.guardian.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import com.ronda.app.core.RiskLevel
import com.ronda.app.ui.theme.ScoreBadgeNumeral
import com.ronda.app.ui.theme.scoreColor
import com.ronda.app.ui.theme.scoreTint

/**
 * The list-row version. Fixed width with tabular figures so the badges form a
 * clean column and the eye can run down the scores without re-anchoring.
 *
 * The ring is what makes it survive the light theme: a pale tint alone floats on
 * a white card, and a solid fill would put four saturated blocks in a vertical
 * line and make every row look urgent.
 *
 * Semantics are cleared because [AlertRow] describes the whole row in one label.
 */
@Composable
fun ScoreBadge(score: Int, level: RiskLevel, modifier: Modifier = Modifier) {
    val color = scoreColor(level)
    val shape = RoundedCornerShape(14.dp)
    Box(
        modifier = modifier
            .size(58.dp)
            .background(scoreTint(level), shape)
            .border(1.5.dp, color.copy(alpha = 0.35f), shape)
            .clearAndSetSemantics { },
        contentAlignment = Alignment.Center
    ) {
        Text(text = "$score", style = ScoreBadgeNumeral, color = color)
    }
}
