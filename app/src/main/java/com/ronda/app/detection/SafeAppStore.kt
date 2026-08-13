package com.ronda.app.detection

import android.content.Context

/**
 * Packages the guardian has explicitly cleared.
 *
 * Only the guardian can add to this list — never the person holding the
 * protected phone. That asymmetry is the point: a scammer on the line can talk
 * a victim into tapping almost anything, but cannot reach the guardian's phone.
 */
class SafeAppStore(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun allow(packageName: String) {
        prefs.edit().putStringSet(KEY_SAFE, safePackages() + packageName).apply()
    }

    fun isAllowed(packageName: String): Boolean = packageName in safePackages()

    /** Forget the decision when the app is uninstalled — a reinstall is a new question. */
    fun forget(packageName: String) {
        prefs.edit().putStringSet(KEY_SAFE, safePackages() - packageName).apply()
    }

    /** Defensive copy — SharedPreferences reuses the returned set instance. */
    private fun safePackages(): Set<String> =
        prefs.getStringSet(KEY_SAFE, emptySet())?.toSet() ?: emptySet()

    private companion object {
        const val PREFS_NAME = "ronda_safe_apps"
        const val KEY_SAFE = "safe_packages"
    }
}
