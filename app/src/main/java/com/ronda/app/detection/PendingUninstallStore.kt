package com.ronda.app.detection

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Uninstall requests the guardian has sent that the phone has not carried out yet.
 *
 * RONDA cannot uninstall anything by itself — Android requires the user to
 * confirm in a system dialog. So a request lands here, the app shows a prompt
 * the next time it is opened, and the entry survives until the OS confirms the
 * package is actually gone. A request must not be lost because the phone was
 * locked when it arrived.
 *
 * Stored as packageName → alertId so the removal broadcast can report back
 * against the alert that started it.
 */
class PendingUninstallStore(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun request(packageName: String, alertId: String) {
        prefs.edit().putString(packageName, alertId).apply()
    }

    fun alertIdFor(packageName: String): String? = prefs.getString(packageName, null)

    fun clear(packageName: String) {
        prefs.edit().remove(packageName).apply()
    }

    /** The oldest outstanding request, or null. One prompt at a time. */
    fun firstPending(): PendingUninstall? =
        prefs.all.entries.firstOrNull()?.let { (packageName, alertId) ->
            PendingUninstall(packageName, alertId as? String ?: "")
        }

    /**
     * Emits the current request and every change to it.
     *
     * The request is written by [com.ronda.app.alert.CommandHandler] on the
     * service side while the UI may already be on screen. Reading this only on
     * resume would leave a guardian's request invisible for as long as the phone
     * sits open on another RONDA screen — the one situation where the user is
     * demonstrably holding the phone and could act immediately.
     */
    fun observe(): Flow<PendingUninstall?> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
            trySend(firstPending())
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        trySend(firstPending())
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    private companion object {
        const val PREFS_NAME = "ronda_pending_uninstall"
    }
}

data class PendingUninstall(val packageName: String, val alertId: String)
