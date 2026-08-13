package com.ronda.app.alert

import com.google.firebase.database.Exclude

/**
 * One HIGH RISK detection, as seen by the guardian.
 *
 * The fields mirror what [com.ronda.app.detection.RiskEvaluator] found, because
 * the guardian is the one who has to make a judgement call — "sideloaded and
 * asks to read your SMS" is the whole argument for uninstalling, so it travels
 * with the alert instead of being summarised away.
 */
data class Alert(
    val packageName: String = "",
    val appLabel: String = "",
    val installSource: String = "",
    val flaggedPermissions: List<String> = emptyList(),
    val status: String = STATUS_PENDING,
    val timestamp: Long = 0L
) {
    /** The RTDB key. Carried on the object for convenience, never written back. */
    @get:Exclude
    var alertId: String = ""

    companion object {
        const val STATUS_PENDING = "pending"
        const val STATUS_UNINSTALLED = "uninstalled"
        const val STATUS_SAFE = "safe"
    }
}
