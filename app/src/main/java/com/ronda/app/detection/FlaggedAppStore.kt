package com.ronda.app.detection

import android.content.Context

/**
 * The set of packages the detection engine has flagged as HIGH RISK.
 *
 * This is the handoff point between detection and blocking: [InstallReceiver]
 * writes here, and OverlayService reads here to decide what to cover.
 *
 * Persisted so a flagged app stays blocked across reboots and app restarts.
 */
class FlaggedAppStore(context: Context) {

    val prefs = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun flag(packageName: String) {
        prefs.edit().putStringSet(KEY_FLAGGED, flaggedPackages() + packageName).apply()
    }

    fun unflag(packageName: String) {
        prefs.edit()
            .putStringSet(KEY_FLAGGED, flaggedPackages() - packageName)
            .remove(KEY_REPORTED_PREFIX + packageName)
            .apply()
    }

    /**
     * Which pairing has already been sent an alert for this package. The
     * initial scan of a pairing re-files flagged apps for a new guardian, and
     * without this it also re-filed the one the install receiver had just
     * sent to the same guardian, so it showed up twice.
     */
    fun markReported(packageName: String, pairingId: String) {
        prefs.edit().putString(KEY_REPORTED_PREFIX + packageName, pairingId).apply()
    }

    fun reportedTo(packageName: String): String? =
        prefs.getString(KEY_REPORTED_PREFIX + packageName, null)

    fun isFlagged(packageName: String): Boolean = packageName in flaggedPackages()

    /**
     * Defensive copy — SharedPreferences returns a set that must never be mutated,
     * and reuses the same instance across reads.
     */
    fun flaggedPackages(): Set<String> =
        prefs.getStringSet(KEY_FLAGGED, emptySet())?.toSet() ?: emptySet()

    private companion object {
        const val PREFS_NAME = "ronda_flagged_apps"
        const val KEY_FLAGGED = "flagged_packages"
        const val KEY_REPORTED_PREFIX = "reported_to_"
    }
}
