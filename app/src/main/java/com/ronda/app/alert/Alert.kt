package com.ronda.app.alert

import com.google.firebase.database.Exclude

/**
 * One HIGH RISK detection, as seen by the guardian.
 *
 * Only the raw [signals] travel, never the prose. Scoring is pure and
 * deterministic, so the guardian re-runs [com.ronda.app.core.RiskEvaluator] on
 * these keys and gets impact, trust, vector and sentences back locally. That
 * keeps the wire small, lets the guardian render the sentences with its own
 * nickname for the protected person, and means a reweighting does not leave
 * stale prose in the database.
 *
 * [score] is carried anyway so the notification can pick its channel without
 * evaluating; it must agree with the recomputed score.
 *
 * Band is derived, never stored — `RiskLevel.of(score)`.
 */
data class Alert(
    val packageName: String = "",
    val appLabel: String = "",
    val score: Int = 0,
    val signals: List<String> = emptyList(),
    val combos: List<String> = emptyList(),
    val status: String = STATUS_PENDING,
    /** Set when the protected person tapped "Lanjutkan Saja" on the overlay. */
    val overrodeAt: Long = 0L,
    val timestamp: Long = 0L
) {
    /** The RTDB key. Carried on the object for convenience, never written back. */
    @get:Exclude
    var alertId: String = ""

    @get:Exclude
    var pairingId: String = ""

    companion object {
        const val STATUS_PENDING = "pending"
        const val STATUS_UNINSTALLED = "uninstalled"
        const val STATUS_SAFE = "safe"

        /** Guardian judged it dangerous; the app is still on the phone. */
        const val STATUS_UNSAFE = "unsafe"
    }
}
