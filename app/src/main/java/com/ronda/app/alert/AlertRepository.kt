package com.ronda.app.alert

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.ronda.app.awaitSet
import com.ronda.app.core.Verdict
import com.ronda.app.valueEvents
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Alerts, stored under the pairing that owns them: `alerts/{pairingId}/{alertId}`.
 *
 * Nesting by pairing rather than keeping one flat `alerts` list means the
 * guardian subscribes to a single node and receives only its own alerts — no
 * query, no `.indexOn` rule, and no way to read another family's alerts.
 */
class AlertRepository {

    private val alerts = FirebaseDatabase.getInstance().reference.child("alerts")

    /**
     * Protected side: publish a detection.
     *
     * The timestamp is set by the server, not the phone — a device with a wrong
     * clock would otherwise sort itself to the bottom of the guardian's list and
     * be missed.
     *
     * @return the generated alert id
     */
    suspend fun submit(pairingId: String, verdict: Verdict): String {
        val ref = alerts.child(pairingId).push()
        ref.awaitSet(
            mapOf(
                "packageName" to verdict.packageName,
                "appLabel" to verdict.appLabel,
                "score" to verdict.score,
                "signals" to verdict.signals.map { it.key },
                "combos" to verdict.combos,
                "status" to Alert.STATUS_PENDING,
                "timestamp" to ServerValue.TIMESTAMP
            )
        )
        return ref.key.orEmpty()
    }

    /**
     * Protected side: report what actually happened to a flagged app.
     *
     * Written when the outcome is real — an app is only `uninstalled` once the
     * OS says it is gone, never on the strength of a command being delivered.
     */
    suspend fun updateStatus(pairingId: String, alertId: String, status: String) {
        if (alertId.isEmpty()) return
        alerts.child(pairingId).child(alertId).child("status").awaitSet(status)
    }

    /** Guardian side: every alert for this pairing, newest first. */
    fun observeAlerts(pairingId: String): Flow<List<Alert>> =
        alerts.child(pairingId).valueEvents().map { snapshot ->
            snapshot.children.mapNotNull { child ->
                child.getValue(Alert::class.java)?.apply { alertId = child.key.orEmpty() }
            }.sortedByDescending { it.timestamp }
        }
}
