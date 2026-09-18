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

    /**
     * What the guardian calls the protected person, used throughout the guardian
     * UI ("HP Ibu", never "HP kamu"). Set during pairing; defaults until then.
     */
    var protectedName: String
        get() = prefs.getString(KEY_PROTECTED_NAME, null) ?: DEFAULT_PROTECTED_NAME
        set(value) = prefs.edit().putString(KEY_PROTECTED_NAME, value).apply()

    /**
     * The guardian's own name. Typed on the guardian phone during pairing,
     * carried in the pairing record, and shown on the protected phone as
     * "Dijaga oleh Rina" — the PRD's "monitored, and by whom". Empty until
     * pairing lands; the UI falls back to "keluarga Anda".
     */
    var guardianName: String
        get() = prefs.getString(KEY_GUARDIAN_NAME, null).orEmpty()
        set(value) = prefs.edit().putString(KEY_GUARDIAN_NAME, value).apply()

    /** No-op if a role is already set — see the write-once note above. */
    fun chooseRole(newRole: Role) {
        if (role != null) return
        prefs.edit().putString(KEY_ROLE, newRole.name).apply()
    }

    /**
     * Guardian side: forget the pairing so a new code can be made. Local only —
     * the database rules never let a client write `revoked` (ARCHITECTURE.md §7),
     * and the role stays put: unpairing is not a way to flip sides.
     */
    fun unpair() {
        prefs.edit().remove(KEY_PAIRING_ID).apply()
    }

    private companion object {
        const val PREFS_NAME = "ronda_role"
        const val KEY_ROLE = "role"
        const val KEY_DEVICE_ID = "device_id"
        const val KEY_PAIRING_ID = "pairing_id"
        const val KEY_PROTECTED_NAME = "protected_name"
        const val KEY_GUARDIAN_NAME = "guardian_name"
        const val DEFAULT_PROTECTED_NAME = "Ibu"
    }
}
