package com.ronda.app.ui.guardian

import com.ronda.app.MainActivity
import com.ronda.app.core.RiskEvaluator
import com.ronda.app.core.Verdict
import com.ronda.app.core.VerdictState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf

/**
 * The demo fixture. Five apps, no network, no Firebase — the recording must not
 * depend on FCM latency or on an emulator staying paired.
 *
 * Scores are not hardcoded: each entry lists real signal keys and runs them
 * through the real [RiskEvaluator]. If the weights are ever retuned, this
 * fixture moves with them instead of quietly lying in the demo video.
 *
 * WhatsApp and Sudoku are the point of the fixture, not filler. They prove
 * RONDA does not flag every sideloaded app — a system that alerts on everything
 * is worse than no system, because guardians learn to swipe it away.
 */
class FakeGuardianRepository(
    override val protectedName: String = "Ibu"
) : GuardianRepository {

    private val decisions = MutableStateFlow(emptyMap<String, VerdictState>())

    /** Packages the "protected phone" has confirmed removed, a few seconds after the request. */
    private val removed = MutableStateFlow(emptySet<String>())

    private val now = System.currentTimeMillis()

    private data class Fixture(
        val packageName: String,
        val label: String,
        val keys: Set<String>,
        val ageMillis: Long,
        val overrodeAt: Long = 0L
    )

    private val fixtures = listOf(
        Fixture(
            "com.senter.terang", "Senter Super Terang",
            setOf("SMS_READ", "INTERNET", "SRC_SIDELOAD", "CERT_SELF_SIGNED"),
            ageMillis = 12 * 60_000L,
            // The product's real mechanism: RONDA could not stop her, so it made
            // the override visible instead.
            overrodeAt = now - 2 * 60_000L
        ),
        Fixture(
            "com.bca.info.mobile", "Info BCA Mobile",
            setOf(
                "ACCESSIBILITY", "SMS_READ", "OVERLAY", "INSTALL_PKG", "NOTIF_LISTENER",
                "INTERNET", "NO_LAUNCHER", "NAME_MIMIC", "SRC_SIDELOAD"
            ),
            ageMillis = 40 * 60_000L
        ),
        Fixture(
            "com.resi.kilat", "Cek Resi Kilat",
            setOf(
                "CONTACTS", "LOCATION", "PHONE_STATE", "INTERNET",
                "INSTALL_PKG", "SRC_SIDELOAD"
            ),
            ageMillis = 5 * 60 * 60_000L
        ),
        Fixture(
            "com.whatsapp", "WhatsApp",
            setOf(
                "SMS_READ", "CONTACTS", "CAMERA", "AUDIO", "LOCATION", "PHONE_STATE",
                "QUERY_PKGS", "INTERNET", "BOOT", "FG_SERVICE", "SRC_PLAY"
            ),
            ageMillis = 3 * 24 * 60 * 60_000L
        ),
        Fixture(
            "com.sudoku.offline", "Sudoku Offline",
            setOf("INTERNET", "SRC_SIDELOAD", "CERT_SELF_SIGNED"),
            ageMillis = 6 * 24 * 60 * 60_000L
        )
    )

    override fun observeVerdicts(): Flow<List<Verdict>> =
        combine(decisions, removed) { decided, gone ->
            fixtures.map { f ->
                val verdict = RiskEvaluator.evaluate(
                    packageName = f.packageName,
                    appLabel = f.label,
                    activeKeys = f.keys,
                    detectedAt = now - f.ageMillis
                ).copy(pairingId = MainActivity.DEMO_PAIRING, overrodeAt = f.overrodeAt, removed = f.packageName in gone)
                decided[f.packageName]?.let { verdict.copy(state = it) } ?: verdict
            }
        }

    override fun observeConnected(): Flow<Boolean> = flowOf(true)

    override suspend fun decide(pairingId: String, packageName: String, safe: Boolean) {
        decisions.value = decisions.value + (packageName to
            if (safe) VerdictState.RESOLVED_SAFE else VerdictState.RESOLVED_UNSAFE)
    }

    /**
     * Offline there is no other phone, so the fixture plays it: the "person
     * holding the phone" taps Hapus about four seconds later, which is how long
     * the real dialog takes when someone is expecting it.
     */
    override suspend fun requestUninstall(pairingId: String, packageName: String) {
        delay(4_000L)
        removed.value = removed.value + packageName
    }

    override suspend fun requestScan(pairingId: String) = Unit
}
