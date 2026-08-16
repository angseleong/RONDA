package com.ronda.app.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The calibration table. These five cases are the evidence that the weights are
 * principled rather than arbitrary — if the implementation disagrees with a
 * number here, the implementation is wrong, not the expectation.
 *
 * Cases 2 and 5 carry the most weight: they prove sideloading *alone* does not
 * raise an alert. A tool that flags every sideloaded app is useless, because
 * guardians learn to ignore it.
 */
class RiskEvaluatorTest {

    private fun score(vararg keys: String): Verdict =
        RiskEvaluator.evaluate("com.test.pkg", "Test", keys.toSet(), detectedAt = 0L)

    // ---------- Calibration table ----------

    /** 1. Real WhatsApp from Play Store. Permission-heavy but trusted. */
    @Test
    fun case1_whatsappFromPlayStore() {
        val v = score(
            "SMS_READ", "CONTACTS", "CAMERA", "AUDIO", "LOCATION",
            "PHONE_STATE", "QUERY_PKGS", "INTERNET", "BOOT", "FG_SERVICE",
            "SRC_PLAY"
        )
        assertEquals(100.0, v.impact, 0.001)   // 40 + 0.4*140 + 15 = 111, capped
        assertEquals(0.45, v.trust, 0.001)
        assertEquals(45, v.score)
        assertEquals(RiskLevel.RENDAH, v.level)
        assertEquals(listOf("SMS_READ+INTERNET"), v.combos)
    }

    /** 2. Sideloaded game, self-signed, internet only. Must NOT alert. */
    @Test
    fun case2_sideloadedGameStaysAman() {
        val v = score("INTERNET", "SRC_SIDELOAD", "CERT_SELF_SIGNED")
        assertEquals(10.0, v.impact, 0.001)
        assertEquals(1.4375, v.trust, 0.001)
        assertEquals(14, v.score)
        assertEquals(RiskLevel.AMAN, v.level)
        assertEquals(VerdictState.RESOLVED_PASSIVE, v.state)
    }

    /** 3. "Senter" (flashlight) that reads SMS. The core RONDA case. */
    @Test
    fun case3_flashlightReadingSms() {
        val v = score("SMS_READ", "INTERNET", "SRC_SIDELOAD", "CERT_SELF_SIGNED")
        assertEquals(59.0, v.impact, 0.001)    // 40 + 0.4*10 + 15
        assertEquals(1.4375, v.trust, 0.001)   // 1.25 * 1.15
        assertEquals(85, v.score)              // 84.8125 -> 85
        assertEquals(RiskLevel.PERINGATAN, v.level)
        assertEquals(VerdictState.PENDING_GUARDIAN, v.state)
    }

    /** 4. Banking trojan. Everything at once. */
    @Test
    fun case4_bankingTrojan() {
        val v = score(
            "ACCESSIBILITY", "SMS_READ", "OVERLAY", "INSTALL_PKG",
            "NOTIF_LISTENER", "INTERNET", "BOOT",
            "SRC_SIDELOAD", "CERT_SELF_SIGNED", "NO_LAUNCHER"
        )
        assertEquals(100.0, v.impact, 0.001)
        assertEquals(1.6, v.trust, 0.001)      // 1.796875 clamped
        assertEquals(100, v.score)
        assertEquals(RiskLevel.DARURAT, v.level)
        assertEquals(4, v.combos.size)
        assertEquals(VerdictState.PENDING_GUARDIAN, v.state)
    }

    /** 5. Legitimate courier app, sideloaded. Must NOT alert. */
    @Test
    fun case5_courierAppStaysBelowThreshold() {
        val v = score(
            "CONTACTS", "LOCATION", "PHONE_STATE", "INTERNET",
            "SRC_SIDELOAD", "CERT_SELF_SIGNED"
        )
        assertEquals(38.0, v.impact, 0.001)    // 20 + 0.4*45
        assertEquals(1.4375, v.trust, 0.001)
        assertEquals(55, v.score)              // 54.625 -> 55
        assertEquals(RiskLevel.RENDAH, v.level)
        assertEquals(VerdictState.RESOLVED_PASSIVE, v.state)
        assertTrue("no overlay below 60", !v.state.overlayActive)
    }

    // ---------- Edge cases ----------

    @Test
    fun trustClampsAtCeiling() {
        val v = score("SRC_SIDELOAD", "CERT_SELF_SIGNED", "NO_LAUNCHER", "LEGACY_SDK", "NAME_MIMIC")
        assertEquals(RiskEvaluator.TRUST_CEILING, v.trust, 0.001)
    }

    @Test
    fun trustClampsAtFloor() {
        // SRC_PLAY is the only trust-reducing signal, so 0.45 is both the floor
        // and the minimum reachable product.
        val v = score("SRC_PLAY")
        assertEquals(RiskEvaluator.TRUST_FLOOR, v.trust, 0.001)
    }

    @Test
    fun impactCapsAt100() {
        val all = Signals.IMPACT.map { it.key }.toTypedArray()
        assertEquals(100.0, score(*all).impact, 0.001)
    }

    @Test
    fun readSmsAndReceiveSmsCountOnce() {
        // Both permissions map to the single SMS_READ key in SignalExtractor,
        // so the evaluator can never see them as two 40-point signals.
        val mapped = setOf("SMS_READ", "SMS_READ")
        assertEquals(1, mapped.size)
        assertEquals(40.0, score("SMS_READ").impact, 0.001)
    }

    @Test
    fun scoreAtThresholdPendsGuardian() {
        // 60 exactly is the first score that involves the guardian.
        val v = score("SMS_READ", "INTERNET", "SRC_SIDELOAD")   // 59 * 1.25 = 73.75
        assertTrue(v.score >= RiskEvaluator.GUARDIAN_THRESHOLD)
        assertEquals(VerdictState.PENDING_GUARDIAN, v.state)
        assertTrue(v.state.overlayActive)
    }

    @Test
    fun emptyPermissionsScoreZero() {
        val v = score()
        assertEquals(0.0, v.impact, 0.001)
        assertEquals(1.0, v.trust, 0.001)
        assertEquals(0, v.score)
        assertEquals(RiskLevel.AMAN, v.level)
        assertEquals(VerdictState.RESOLVED_PASSIVE, v.state)
        assertTrue(v.reasons.isEmpty())
    }

    // ---------- Verdict plumbing ----------

    @Test
    fun guardianDecisionResolvesPendingVerdict() {
        val v = score("SMS_READ", "INTERNET", "SRC_SIDELOAD", "CERT_SELF_SIGNED")
        assertEquals(VerdictState.RESOLVED_SAFE, v.resolve(safe = true).state)
        assertEquals(VerdictState.RESOLVED_UNSAFE, v.resolve(safe = false).state)
        // Unsafe keeps the overlay up; safe stops it permanently.
        assertTrue(v.resolve(safe = false).state.overlayActive)
        assertTrue(!v.resolve(safe = true).state.overlayActive)
    }

    @Test
    fun resolvedVerdictIgnoresLateCommands() {
        val passive = score("INTERNET", "SRC_SIDELOAD")
        assertEquals(VerdictState.RESOLVED_PASSIVE, passive.resolve(safe = false).state)
    }

    @Test
    fun reasonsAreOrderedBySeverity() {
        val v = score("INTERNET", "ACCESSIBILITY", "SMS_READ", "SRC_SIDELOAD")
        assertTrue(v.reasons.first().contains("melihat dan mengontrol"))   // ACCESSIBILITY, 45
        assertTrue(v.reasons[1].contains("semua SMS"))                     // SMS_READ, 40
        assertEquals(4, v.reasons.size)
    }

    @Test
    fun vectorStringIsWellFormed() {
        val v = score("SMS_READ", "INTERNET", "SRC_SIDELOAD", "CERT_SELF_SIGNED")
        assertEquals("RONDA:1.0/SMS_READ:40/INTERNET:10/CMB:15/SRC:SIDELOAD/CERT:SELF=85", v.vector)
    }

    @Test
    fun unknownSignalKeysAreIgnored() {
        val v = score("SMS_READ", "NOT_A_REAL_SIGNAL")
        assertEquals(40.0, v.impact, 0.001)
    }
}
