package com.ronda.app.ui.guardian

import com.google.firebase.database.FirebaseDatabase
import com.ronda.app.alert.Alert
import com.ronda.app.alert.AlertRepository
import com.ronda.app.alert.Command
import com.ronda.app.alert.CommandRepository
import com.ronda.app.core.RiskEvaluator
import com.ronda.app.core.Verdict
import com.ronda.app.core.VerdictState
import com.ronda.app.valueEvents
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

/**
 * The live implementation. Alerts arrive carrying only raw signal keys, so the
 * verdict is rebuilt here by running the same pure evaluator the protected phone
 * ran. Two benefits over shipping the prose: the guardian renders sentences with
 * its own nickname for the protected person, and the score is recomputed rather
 * than trusted.
 */
class FirebaseGuardianRepository(
    private val pairingIds: Set<String>,
    override val protectedName: String
) : GuardianRepository {

    private val alerts = AlertRepository()
    private val commands = CommandRepository()

    /** "pairingId:packageName" -> RTDB alert key. */
    @Volatile
    private var alertIds: Map<String, String> = emptyMap()

    override fun observeVerdicts(): Flow<List<Verdict>> {
        if (pairingIds.isEmpty()) return flowOf(emptyList())
        val flows = pairingIds.map { alerts.observeAlerts(it) }
        return combine(flows) { lists -> lists.flatMap { it.toList() } }.map { list ->
            alertIds = list.associate { "${it.pairingId}:${it.packageName}" to it.alertId }
            list.map(::toVerdict)
        }
    }

    /**
     * Firebase's own connection flag. An empty watch list means something very
     * different when the guardian is offline, and the screen has to say which.
     */
    override fun observeConnected(): Flow<Boolean> =
        FirebaseDatabase.getInstance().getReference(".info/connected")
            .valueEvents()
            .map { it.getValue(Boolean::class.java) == true }

    override suspend fun decide(pairingId: String, packageName: String, safe: Boolean) {
        val alertId = alertIds["$pairingId:$packageName"] ?: return
        if (safe) {
            // Only "safe" reaches the protected phone: it is the one that lifts
            // the overlay. Unsafe changes nothing there — the overlay is already
            // up and stays up — so it is recorded for the guardian alone.
            commands.send(pairingId, alertId, Command.ACTION_MARK_SAFE, packageName)
            alerts.updateStatus(pairingId, alertId, Alert.STATUS_SAFE)
        } else {
            alerts.updateStatus(pairingId, alertId, Alert.STATUS_UNSAFE)
        }
    }

    override suspend fun requestUninstall(pairingId: String, packageName: String) {
        val alertId = alertIds["$pairingId:$packageName"] ?: return
        commands.send(pairingId, alertId, Command.ACTION_UNINSTALL, packageName)
    }

    override suspend fun requestScan(pairingId: String) {
        commands.send(pairingId, alertId = "", action = Command.ACTION_SCAN, packageName = "")
    }

    private fun toVerdict(alert: Alert): Verdict {
        val verdict = RiskEvaluator.evaluate(
            packageName = alert.packageName,
            appLabel = alert.appLabel.ifBlank { alert.packageName },
            activeKeys = alert.signals.toSet(),
            detectedAt = alert.timestamp
        )
        return verdict.copy(
            pairingId = alert.pairingId,
            state = stateOf(alert, verdict.state),
            overrodeAt = alert.overrodeAt,
            removed = alert.status == Alert.STATUS_UNINSTALLED
        )
    }

    private fun stateOf(alert: Alert, scored: VerdictState): VerdictState = when (alert.status) {
        Alert.STATUS_SAFE -> VerdictState.RESOLVED_SAFE
        Alert.STATUS_UNSAFE, Alert.STATUS_UNINSTALLED -> VerdictState.RESOLVED_UNSAFE
        else -> scored
    }
}
