package com.ronda.app.alert

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.ronda.app.awaitSet
import com.ronda.app.valueEvents
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * The return path: guardian decisions written to `commands/{pairingId}/{commandId}`.
 *
 * Same shape as [AlertRepository], same held-open listener, opposite direction.
 * One mechanism carries both halves of the conversation.
 */
class CommandRepository {

    private val commands = FirebaseDatabase.getInstance().reference.child("commands")

    /**
     * Record a decision for the other phone to act on. Almost always the
     * guardian; the protected side only ever sends a disconnect.
     */
    suspend fun send(
        pairingId: String,
        alertId: String,
        action: String,
        packageName: String,
        from: String = Command.FROM_GUARDIAN
    ): String {
        val ref = commands.child(pairingId).push()
        ref.awaitSet(
            mapOf(
                "alertId" to alertId,
                "action" to action,
                "packageName" to packageName,
                "createdAt" to ServerValue.TIMESTAMP,
                "executedAt" to null,
                "from" to from
            )
        )
        return ref.key.orEmpty()
    }

    /** Protected side: every command for this pairing, oldest first. */
    fun observeCommands(pairingId: String): Flow<List<Command>> =
        commands.child(pairingId).valueEvents().map { snapshot ->
            snapshot.children.mapNotNull { child ->
                child.getValue(Command::class.java)?.apply { commandId = child.key.orEmpty() }
            }.sortedBy { it.createdAt }
        }

    /**
     * Protected side: acknowledge receipt.
     *
     * This means "delivered and surfaced to the user", not "the app is gone".
     * Whether an uninstall actually happened is reported separately, when the OS
     * broadcasts the removal — the guardian should never be told an app was
     * removed on the strength of a command that was merely received.
     */
    suspend fun markExecuted(pairingId: String, commandId: String) {
        commands.child(pairingId).child(commandId).child("executedAt")
            .awaitSet(ServerValue.TIMESTAMP)
    }
}
