package com.ronda.app.ui.guardian

import com.ronda.app.core.RiskEvaluator
import com.ronda.app.ui.guardian.components.explanationKeys
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The combo-replacement rule. A combo card must absorb its member signals, or
 * the stack repeats itself: "can read SMS" directly under "reads your SMS and
 * sends it to the internet".
 */
class ExplanationKeysTest {

    private fun keys(vararg k: String) =
        explanationKeys(RiskEvaluator.evaluate("p", "L", k.toSet(), detectedAt = 0L))

    @Test
    fun comboLeadsAndAbsorbsItsMembers() {
        val result = keys("SMS_READ", "INTERNET", "SRC_SIDELOAD", "CERT_SELF_SIGNED")
        assertEquals("SMS_READ+INTERNET", result.first())
        assertFalse("SMS_READ is covered by the combo", result.contains("SMS_READ"))
        assertFalse("INTERNET is covered by the combo", result.contains("INTERNET"))
        // Signals outside the combo still get their own card.
        assertTrue(result.contains("SRC_SIDELOAD"))
        assertTrue(result.contains("CERT_SELF_SIGNED"))
    }

    @Test
    fun signalsWithoutACombOrderBySeverity() {
        // ACCESSIBILITY 45, CONTACTS 20, SRC_SIDELOAD 25-equivalent, BOOT 10.
        val result = keys("BOOT", "CONTACTS", "ACCESSIBILITY", "SRC_SIDELOAD")
        assertEquals(listOf("ACCESSIBILITY", "SRC_SIDELOAD", "CONTACTS", "BOOT"), result)
    }

    @Test
    fun everyComboIsAbsorbedInTheWorstCase() {
        val result = keys(
            "ACCESSIBILITY", "SMS_READ", "OVERLAY", "INSTALL_PKG", "NOTIF_LISTENER",
            "INTERNET", "BOOT", "SRC_SIDELOAD", "CERT_SELF_SIGNED", "NO_LAUNCHER"
        )
        assertEquals(4, result.count { it.contains("+") })
        // Only BOOT, CERT_SELF_SIGNED and NO_LAUNCHER survive uncovered.
        assertEquals(listOf("NO_LAUNCHER", "CERT_SELF_SIGNED", "BOOT"), result.filterNot { "+" in it })
    }

    @Test
    fun playStoreAppHasNoSideloadCard() {
        val result = keys("CONTACTS", "SRC_PLAY")
        // SRC_PLAY is reassuring, not evidence — it has no sentence at all.
        assertEquals(listOf("CONTACTS", "SRC_PLAY"), result)
    }

    @Test
    fun emptyVerdictExplainsNothing() {
        assertTrue(keys().isEmpty())
    }
}
