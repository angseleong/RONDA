package com.ronda.app.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.ronda.app.R
import com.ronda.app.core.RiskLevel

/** The band's word, in the app's language. */
@Composable
fun RiskLevel.label(): String = stringResource(
    when (this) {
        RiskLevel.AMAN -> R.string.level_aman
        RiskLevel.RENDAH -> R.string.level_rendah
        RiskLevel.PERINGATAN -> R.string.level_peringatan
        RiskLevel.DARURAT -> R.string.level_darurat
    }
)

@Composable
fun relativeTime(timestamp: Long): String {
    if (timestamp <= 0L) return ""
    val minutes = (System.currentTimeMillis() - timestamp) / 60_000L
    return when {
        minutes < 1 -> stringResource(R.string.time_just_now)
        minutes < 60 -> stringResource(R.string.time_minutes, minutes.toInt())
        minutes < 60 * 24 -> stringResource(R.string.time_hours, (minutes / 60).toInt())
        else -> stringResource(R.string.time_days, (minutes / (60 * 24)).toInt())
    }
}
