package com.ronda.app.detection

import android.content.Context
import com.ronda.app.core.RiskEvaluator
import com.ronda.app.core.Verdict
import com.ronda.app.detect.SignalExtractor

/**
 * Why *this* app was flagged, most severe first, in the same second-person
 * sentences the detection notification uses — so the overlay and the uninstall
 * prompt name the real trigger (accessibility, notification access, …) instead
 * of assuming every threat reads SMS.
 *
 * Re-evaluated from the installed package rather than stored at flag time: the
 * evaluator is pure and cheap, and an app that is still installed still declares
 * what it declared. Empty if the package is gone.
 */
fun flagReasons(context: Context, packageName: String): List<String> =
    flagVerdict(context, packageName)?.reasons?.map { it.replace("**", "") }.orEmpty()

/** The full verdict, re-evaluated the same way; null if the package is gone. */
fun flagVerdict(context: Context, packageName: String): Verdict? {
    val extractor = SignalExtractor(context.packageManager)
    val keys = extractor.extract(packageName)
    if (keys.isEmpty()) return null
    // Real install time, not "now": the evaluation is fresh, the install is not.
    val installedAt = runCatching {
        context.packageManager.getPackageInfo(packageName, 0).lastUpdateTime
    }.getOrDefault(System.currentTimeMillis())
    return RiskEvaluator.evaluate(packageName, extractor.labelOf(packageName), keys, installedAt)
}
