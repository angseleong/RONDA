package com.ronda.app.ui.guardian.components

import androidx.compose.foundation.background
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

/**
 * The list-row version. Fixed width with tabular figures so the badges form a
 * clean column and the eye can run down the scores without re-anchoring.
 *
 * Semantics are cleared because [AlertRow] describes the whole row in one label.
 */
@Composable
fun ScoreBadge(score: Int, level: RiskLevel, modifier: Modifier = Modifier) {
    val color = scoreColor(level)
    Box(
        modifier = modifier
            .size(56.dp)
            .background(color.copy(alpha = 0.14f), RoundedCornerShape(12.dp))
            .clearAndSetSemantics { },
        contentAlignment = Alignment.Center
    ) {
        Text(text = "$score", style = ScoreBadgeNumeral, color = color)
    }
}
