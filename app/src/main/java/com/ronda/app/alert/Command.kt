package com.ronda.app.alert

import com.google.firebase.database.Exclude

/**
 * A decision the guardian made, travelling back to the protected phone.
 *
 * [packageName] is carried on the command rather than looked up from the alert:
 * the protected device must know what to act on even if the alert record is
 * unreachable, and acting on the wrong package would be unrecoverable.
 */
data class Command(
    val alertId: String = "",
    val action: String = "",
    val packageName: String = "",
    val createdAt: Long = 0L,
    /** Null until the protected device has picked this up. */
    val executedAt: Long? = null,
    /**
     * Which side wrote it. Only disconnect travels both ways, and both phones
     * listen on the same channel, so each must ignore the one it sent itself.
     */
    val from: String = ""
) {
    /** The RTDB key. Carried for convenience, never written back. */
    @get:Exclude
    var commandId: String = ""

    @get:Exclude
    val isPending: Boolean get() = executedAt == null

    /** Commands from before [from] existed were all written by the guardian. */
    fun isDisconnectFrom(sender: String): Boolean =
        action == ACTION_DISCONNECT && from.ifEmpty { FROM_GUARDIAN } == sender

    companion object {
        const val FROM_GUARDIAN = "guardian"
        const val FROM_PROTECTED = "protected"

        const val ACTION_UNINSTALL = "uninstall"
        const val ACTION_MARK_SAFE = "mark_safe"
        const val ACTION_SCAN = "scan"
        const val ACTION_DISCONNECT = "disconnect"
    }
}
