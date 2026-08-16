package com.ronda.app.core

import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Pure, deterministic, offline scoring. No Android imports, no `Context`, no
 * network — everything the evaluator needs arrives as a set of signal keys from
 * [com.ronda.app.detect.SignalExtractor].
 *
 * The shape is adapted from CVSS: an Impact term modified by a provenance term.
 * (Not "CVE-based" — a CVE is an identifier for one disclosed vulnerability, not
 * a scoring system.)
 *
 * ```
 * sorted     = impact weights, descending
 * impact     = min(100, sorted[0] + 0.4 * sum(sorted[1..]) + combo bonuses)
 * trust      = clamp(product of multipliers, TRUST_FLOOR, TRUST_CEILING)
 * score      = min(100, round(impact * trust))
 * ```
 *
 * The 0.4 diminishing-return factor is the load-bearing part: naive summation
 * would push any permission-heavy legitimate app straight to 100. The worst
 * capability dominates; the rest only reinforce it.
 */
object RiskEvaluator {

    /** Weight applied to every impact signal except the largest. */
    const val DIMINISHING = 0.4

    /**
     * Lower bound on the trust product.
     *
     * Set to 0.45 rather than the 0.5 named in the spec prose, because the
     * calibration table requires a Play-Store app to land on exactly 0.45
     * (case 1: WhatsApp, impact 100, score 45). A 0.5 floor would make that
     * case score 50 and no combination of multipliers could ever produce 0.45.
     * The calibration table wins; see RiskEvaluatorTest.
     *
     * Only SRC_PLAY reduces trust, so 0.45 is also the true minimum reachable
     * product — the floor is a guard, not an active constraint.
     */
    const val TRUST_FLOOR = 0.45

    /** Upper bound on the trust product. Reached by sideload x self-signed x hidden icon. */
    const val TRUST_CEILING = 1.6

    /** Score at or above which the guardian is brought in. */
    const val GUARDIAN_THRESHOLD = 60

    fun evaluate(
        packageName: String,
        appLabel: String,
        activeKeys: Set<String>,
        detectedAt: Long = System.currentTimeMillis()
    ): Verdict {
        // Unknown keys are dropped rather than throwing: an extractor that learns
        // a new signal must not crash a detection on an older build.
        val active = activeKeys.mapNotNull { Signals[it] }

        val impactSignals = active
            .filter { it.weight > 0.0 }
            .sortedWith(compareByDescending<Signal> { it.weight }.thenBy { it.key })

        val base = if (impactSignals.isEmpty()) 0.0 else {
            impactSignals.first().weight + DIMINISHING * impactSignals.drop(1).sumOf { it.weight }
        }

        val combos = Signals.COMBOS.filter { activeKeys.containsAll(it.keys) }
        val impact = min(100.0, base + combos.sumOf { it.bonus })

        val trustSignals = active.filter { it.weight == 0.0 }
        val trust = trustSignals
            .fold(1.0) { acc, s -> acc * s.multiplier }
            .coerceIn(TRUST_FLOOR, TRUST_CEILING)

        val score = min(100, (impact * trust).roundToInt())
        val level = RiskLevel.of(score)

        return Verdict(
            packageName = packageName,
            appLabel = appLabel,
            score = score,
            level = level,
            state = if (score >= GUARDIAN_THRESHOLD) VerdictState.PENDING_GUARDIAN
                    else VerdictState.RESOLVED_PASSIVE,
            impact = impact,
            trust = trust,
            signals = impactSignals + trustSignals.sortedByDescending { it.multiplier },
            combos = combos.map { it.label },
            reasons = ReasonBuilder.build(active),
            vector = vector(impactSignals, combos, trustSignals, score),
            detectedAt = detectedAt
        )
    }

    /**
     * Compact CVSS-inspired string for logs, debugging and the proposal, e.g.
     * `RONDA:1.0/ACCESSIBILITY:45/SMS_READ:40/CMB:15/SRC:SIDELOAD/CERT:SELF=100`
     */
    private fun vector(
        impactSignals: List<Signal>,
        combos: List<Combo>,
        trustSignals: List<Signal>,
        score: Int
    ): String = buildString {
        append("RONDA:1.0")
        impactSignals.forEach { append("/").append(it.key).append(":").append(it.weight.toInt()) }
        if (combos.isNotEmpty()) append("/CMB:").append(combos.sumOf { it.bonus }.toInt())
        trustSignals
            .sortedByDescending { it.multiplier }
            .forEach { append("/").append(Signals.VECTOR_TAGS[it.key] ?: it.key) }
        append("=").append(score)
    }
}
