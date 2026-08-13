package com.ronda.app.pairing

import android.content.Context
import java.util.UUID

enum class Role {
    /** The child. Receives alerts, decides what happens to a flagged app. */
    GUARDIAN,

    /** The parent's phone. Runs detection and the soft-block. */
    PROTECTED
}

/**
 * Which side of the pair this phone is, and who it is paired with.
 *
 * Role is deliberately write-once (PRD: "cannot be changed without app
 * reinstall"). A scammer talking a victim through the app must not be able to
 * flip the phone into Guardian mode and cut the real guardian out of the loop.
 */
class RoleStore(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    val role: Role?
        get() = prefs.getString(KEY_ROLE, null)?.let(Role::valueOf)

    /** Stable identity for this install, generated once on first read. */
    val deviceId: String
        get() = prefs.getString(KEY_DEVICE_ID, null) ?: UUID.randomUUID().toString().also {
            prefs.edit().putString(KEY_DEVICE_ID, it).apply()
        }

    /** The pairing this device belongs to, or null while unpaired. */
    var pairingId: String?
        get() = prefs.getString(KEY_PAIRING_ID, null)
        set(value) = prefs.edit().putString(KEY_PAIRING_ID, value).apply()

    val isPaired: Boolean get() = pairingId != null

    /** No-op if a role is already set — see the write-once note above. */
    fun chooseRole(newRole: Role) {
        if (role != null) return
        prefs.edit().putString(KEY_ROLE, newRole.name).apply()
    }

    private companion object {
        const val PREFS_NAME = "ronda_role"
        const val KEY_ROLE = "role"
        const val KEY_DEVICE_ID = "device_id"
        const val KEY_PAIRING_ID = "pairing_id"
    }
}
