package com.ronda.app.detection

import android.content.Context
import com.ronda.app.core.RiskEvaluator
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
fun flagReasons(context: Context, packageName: String): List<String> {
    val extractor = SignalExtractor(context.packageManager)
    val keys = extractor.extract(packageName)
    if (keys.isEmpty()) return emptyList()
    return RiskEvaluator.evaluate(packageName, extractor.labelOf(packageName), keys)
        .reasons
        .map { it.replace("**", "") }
}
