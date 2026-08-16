package com.ronda.app.core

/**
 * A single observable property of an installed app.
 *
 * A signal is either an *impact* signal (it carries [weight] and contributes to
 * what the app can do) or a *trust* signal (it carries [multiplier] and reflects
 * where the app came from). Never both: impact signals have `multiplier = 1.0`,
 * trust signals have `weight = 0.0`.
 *
 * [attackId] is the MITRE ATT&CK for Mobile technique the capability maps to.
 * All IDs here were verified against attack.mitre.org on 16 Aug 2026.
 */
data class Signal(
    val key: String,
    val weight: Double,
    val multiplier: Double,
    val attackId: String?
)

/**
 * One ordering for both axes, so impact and trust signals can be ranked in a
 * single list. Impact signals rank by weight; trust signals by how far their
 * multiplier departs from neutral — which places sideloading and a hidden icon
 * among the mid-weight capabilities and sinks the reassuring Play Store note to
 * the bottom.
 */
val Signal.severity: Double
    get() = if (weight > 0.0) weight else (multiplier - 1.0) * 100

/** Signals whose presence is only meaningful in combination. */
data class Combo(val keys: Set<String>, val bonus: Double, val label: String)

/**
 * The single source of truth for scoring weights.
 *
 * Nothing else in the codebase may hard-code a weight, multiplier or bonus —
 * [RiskEvaluator] reads everything from here.
 */
object Signals {

    /** Axis 1 — what the app is capable of doing. */
    val IMPACT: List<Signal> = listOf(
        Signal("ACCESSIBILITY", 45.0, 1.0, "T1516"),      // Input Injection
        Signal("DEVICE_ADMIN", 40.0, 1.0, "T1626.001"),   // Device Administrator Permissions
        Signal("SMS_READ", 40.0, 1.0, "T1636.004"),       // SMS Messages
        Signal("INSTALL_PKG", 30.0, 1.0, null),           // dropper chain
        Signal("OVERLAY", 30.0, 1.0, "T1417.002"),        // GUI Input Capture
        Signal("NOTIF_LISTENER", 30.0, 1.0, "T1517"),     // Access Notifications
        Signal("AUDIO", 25.0, 1.0, "T1429"),              // Audio Capture
        Signal("CALL", 25.0, 1.0, "T1616"),               // Call Control
        Signal("CONTACTS", 20.0, 1.0, "T1636.003"),       // Contact List
        Signal("CAMERA", 20.0, 1.0, "T1512"),             // Video Capture
        Signal("LOCATION", 20.0, 1.0, "T1430"),           // Location Tracking
        Signal("PHONE_STATE", 15.0, 1.0, "T1426"),        // System Information Discovery
        Signal("QUERY_PKGS", 15.0, 1.0, "T1418"),         // Software Discovery
        Signal("BOOT", 10.0, 1.0, "T1398"),               // Boot or Logon Initialization Scripts
        Signal("INTERNET", 10.0, 1.0, null),              // exfiltration channel
        Signal("FG_SERVICE", 5.0, 1.0, "T1541")           // Foreground Persistence
    )

    /** Axis 2 — where the app came from and how it presents itself. */
    val TRUST: List<Signal> = listOf(
        Signal("SRC_PLAY", 0.0, 0.45, null),
        Signal("SRC_KNOWN_STORE", 0.0, 0.80, null),
        Signal("SRC_SIDELOAD", 0.0, 1.25, null),
        Signal("CERT_SELF_SIGNED", 0.0, 1.15, null),
        Signal("NO_LAUNCHER", 0.0, 1.25, null),
        Signal("LEGACY_SDK", 0.0, 1.15, null),
        Signal("NAME_MIMIC", 0.0, 1.20, null)
    )

    /**
     * Capability *pairs* express intent that the individual permissions do not.
     * This is what separates RONDA from a naive permission lister.
     */
    val COMBOS: List<Combo> = listOf(
        Combo(setOf("SMS_READ", "INTERNET"), 15.0, "SMS_READ+INTERNET"),
        Combo(setOf("ACCESSIBILITY", "OVERLAY"), 15.0, "ACCESSIBILITY+OVERLAY"),
        Combo(setOf("NOTIF_LISTENER", "INTERNET"), 15.0, "NOTIF_LISTENER+INTERNET"),
        Combo(setOf("INSTALL_PKG", "SRC_SIDELOAD"), 10.0, "INSTALL_PKG+SRC_SIDELOAD"),
        Combo(setOf("DEVICE_ADMIN", "NO_LAUNCHER"), 15.0, "DEVICE_ADMIN+NO_LAUNCHER")
    )

    private val byKey: Map<String, Signal> = (IMPACT + TRUST).associateBy { it.key }

    /** Unknown keys return null and are ignored by the evaluator. */
    operator fun get(key: String): Signal? = byKey[key]

    /** Short tag used in the vector string, e.g. `SRC_SIDELOAD` -> `SRC:SIDELOAD`. */
    val VECTOR_TAGS: Map<String, String> = mapOf(
        "SRC_PLAY" to "SRC:PLAY",
        "SRC_KNOWN_STORE" to "SRC:STORE",
        "SRC_SIDELOAD" to "SRC:SIDELOAD",
        "CERT_SELF_SIGNED" to "CERT:SELF",
        "NO_LAUNCHER" to "ICON:HIDDEN",
        "LEGACY_SDK" to "SDK:LEGACY",
        "NAME_MIMIC" to "NAME:MIMIC"
    )
}
