package com.ronda.app.core

/**
 * Score band. Names are Indonesian because they surface directly in the UI.
 *
 * Note that RONDA never *blocks*. At PERINGATAN and DARURAT it draws an overlay
 * on every foreground launch, but an overlay is a window on top of another app —
 * pressing Home dismisses it. RONDA's mechanism is guardian-mediated
 * intervention, not enforcement.
 */
enum class RiskLevel {
    /** 0–29. No overlay, no notification. */
    AMAN,

    /** 30–59. Listed in-app for the guardian, passive. No overlay. */
    RENDAH,

    /** 60–89. Persistent overlay, standard guardian notification. */
    PERINGATAN,

    /** 90–100. Persistent overlay, high-priority notification, sound, bypass DND. */
    DARURAT;

    companion object {
        fun of(score: Int): RiskLevel = when {
            score >= 90 -> DARURAT
            score >= 60 -> PERINGATAN
            score >= 30 -> RENDAH
            else -> AMAN
        }
    }
}

/**
 * Lifecycle of a detection.
 *
 * ```
 * DETECTED
 *    ├── score <  60 ──► RESOLVED_PASSIVE   (logged, no overlay ever)
 *    └── score >= 60 ──► PENDING_GUARDIAN
 *
 * PENDING_GUARDIAN
 *    ├── guardian marks safe   ──► RESOLVED_SAFE    (overlay stops permanently)
 *    ├── guardian marks unsafe ──► RESOLVED_UNSAFE  (overlay persists + uninstall prompt)
 *    └── no response           ──► stays PENDING_GUARDIAN indefinitely
 * ```
 *
 * There is deliberately no timeout. For this POC an unanswered alert simply
 * keeps showing the overlay; escalation logic is out of scope.
 */
enum class VerdictState {
    DETECTED,
    PENDING_GUARDIAN,
    RESOLVED_PASSIVE,
    RESOLVED_SAFE,
    RESOLVED_UNSAFE;

    /** True while the overlay should reappear on every foreground launch. */
    val overlayActive: Boolean
        get() = this == PENDING_GUARDIAN || this == RESOLVED_UNSAFE

    /**
     * True once the guardian has actually ruled on it, which is what moves a row
     * into history. [RESOLVED_PASSIVE] is deliberately excluded: nothing was ever
     * asked of the guardian below the threshold, so there is no decision to file.
     */
    val decided: Boolean
        get() = this == RESOLVED_SAFE || this == RESOLVED_UNSAFE
}

data class Verdict(
    val packageName: String,
    val appLabel: String,
    val score: Int,
    val level: RiskLevel,
    val state: VerdictState,
    val impact: Double,
    val trust: Double,
    val signals: List<Signal>,
    val combos: List<String>,
    val reasons: List<String>,
    val vector: String,
    val detectedAt: Long,
    /**
     * When the protected person dismissed the overlay with "Lanjutkan Saja",
     * or 0. RONDA cannot stop a determined user — an overlay is dismissible by
     * pressing Home — so it makes the override socially visible instead. This
     * is the product's real mechanism, not a footnote.
     */
    val overrodeAt: Long = 0L
) {
    /**
     * Apply a guardian decision. Only meaningful from [VerdictState.PENDING_GUARDIAN];
     * any other state is returned unchanged so a late or duplicated command cannot
     * re-open a resolved verdict.
     */
    fun resolve(safe: Boolean): Verdict =
        if (state != VerdictState.PENDING_GUARDIAN) this
        else copy(state = if (safe) VerdictState.RESOLVED_SAFE else VerdictState.RESOLVED_UNSAFE)
}
