package com.ronda.app.pairing

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * The deep-link contract: what a scanned QR is allowed to put in the pairing
 * field. Anything this rejects has to be typed by hand instead, which is the
 * safe direction to fail in.
 */
class QrCodeUtilsTest {

    @Test
    fun `reads the code from the link the QR actually encodes`() {
        assertEquals("4F2K9A", QrCodeUtils.codeFromLink("ronda://pair/4F2K9A"))
    }

    @Test
    fun `accepts the query form too`() {
        assertEquals("4F2K9A", QrCodeUtils.codeFromLink("ronda://pair?code=4F2K9A"))
        assertEquals("4F2K9A", QrCodeUtils.codeFromLink("ronda://pair?code=4F2K9A&v=1"))
    }

    @Test
    fun `is forgiving about case and stray whitespace`() {
        assertEquals("4F2K9A", QrCodeUtils.codeFromLink("  ronda://pair/4f2k9a  "))
        assertEquals("4F2K9A", QrCodeUtils.codeFromLink("RONDA://PAIR/4f2k9a"))
    }

    @Test
    fun `rejects another app's links`() {
        assertNull(QrCodeUtils.codeFromLink("https://pair/4F2K9A"))
        assertNull(QrCodeUtils.codeFromLink("ronda://unpair/4F2K9A"))
    }

    @Test
    fun `rejects a link with no usable code`() {
        assertNull(QrCodeUtils.codeFromLink("ronda://pair"))
        assertNull(QrCodeUtils.codeFromLink("ronda://pair/"))
        assertNull(QrCodeUtils.codeFromLink("ronda://pair/TOOLONG9"))
        assertNull(QrCodeUtils.codeFromLink("ronda://pair/4F2K9"))
    }

    /**
     * The alphabet drops the characters that get misread when a code is read
     * aloud, so a link carrying one of them was not produced by RONDA.
     */
    @Test
    fun `rejects characters the pairing alphabet excludes`() {
        assertNull(QrCodeUtils.codeFromLink("ronda://pair/4F2K9O"))
        assertNull(QrCodeUtils.codeFromLink("ronda://pair/4F2K9I"))
    }

    @Test
    fun `rejects nothing at all`() {
        assertNull(QrCodeUtils.codeFromLink(null))
        assertNull(QrCodeUtils.codeFromLink(""))
        assertNull(QrCodeUtils.codeFromLink("not a uri at all"))
    }

    @Test
    fun `generated codes round-trip through their own link`() {
        repeat(50) {
            val code = QrCodeUtils.newPairingCode()
            assertEquals(code, QrCodeUtils.codeFromLink("ronda://pair/$code"))
        }
    }
}
