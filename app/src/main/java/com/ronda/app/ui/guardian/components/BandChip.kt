package com.ronda.app.ui.guardian.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ronda.app.R
import com.ronda.app.core.RiskLevel
import com.ronda.app.ui.theme.Eyebrow
import com.ronda.app.ui.theme.scoreColor
import com.ronda.app.ui.theme.scoreTint

/**
 * The band name as a filled pill.
 *
 * It exists so the band survives being photographed, printed, or looked at by
 * someone who cannot separate the four colours. The colour is the fast channel
 * and the word is the reliable one; neither is allowed to travel alone.
 */
@Composable
fun BandChip(level: RiskLevel, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(scoreTint(level), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(text = bandLabel(level), style = Eyebrow, color = scoreColor(level))
    }
}

/** The band's display name. Never use `RiskLevel.name` in UI. */
@Composable
fun bandLabel(level: RiskLevel): String = stringResource(
    when (level) {
        RiskLevel.AMAN -> R.string.band_aman
        RiskLevel.RENDAH -> R.string.band_rendah
        RiskLevel.PERINGATAN -> R.string.band_peringatan
        RiskLevel.DARURAT -> R.string.band_darurat
    }
)
