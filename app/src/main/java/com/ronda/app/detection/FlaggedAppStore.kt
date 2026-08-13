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

    private val prefs = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun flag(packageName: String) {
        prefs.edit().putStringSet(KEY_FLAGGED, flaggedPackages() + packageName).apply()
    }

    fun unflag(packageName: String) {
        prefs.edit().putStringSet(KEY_FLAGGED, flaggedPackages() - packageName).apply()
    }

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
    }
}
