package com.ronda.app.alert

import android.content.Context

/**
 * Alert ids the guardian has already been notified about.
 *
 * The RTDB listener re-delivers the whole alert list on every reconnect, so
 * without this the guardian's phone would buzz again for the same threat every
 * time the network flickers — and a guardian who learns to ignore RONDA's
 * notifications is a guardian who misses the real one.
 */
class SeenAlertStore(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isNotified(alertId: String): Boolean = alertId in notifiedIds()

    fun markNotified(alertId: String) {
        prefs.edit().putStringSet(KEY_NOTIFIED, notifiedIds() + alertId).apply()
    }

    /** Defensive copy — SharedPreferences reuses the returned set instance. */
    private fun notifiedIds(): Set<String> =
        prefs.getStringSet(KEY_NOTIFIED, emptySet())?.toSet() ?: emptySet()

    private companion object {
        const val PREFS_NAME = "ronda_seen_alerts"
        const val KEY_NOTIFIED = "notified_alert_ids"
    }
}
