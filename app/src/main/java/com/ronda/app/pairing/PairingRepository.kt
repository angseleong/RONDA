package com.ronda.app.pairing

import com.google.firebase.database.Exclude
import com.google.firebase.database.FirebaseDatabase
import com.ronda.app.awaitGet
import com.ronda.app.awaitSet
import com.ronda.app.awaitUpdate
import com.ronda.app.valueEvents
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * One pairing between a guardian phone and a protected phone.
 *
 * The pairing code doubles as the RTDB key, so no lookup or index is needed —
 * the protected device types the code and goes straight to the node.
 */
data class Pairing(
    val guardianDeviceId: String = "",
    val protectedDeviceId: String? = null,
    val status: String = STATUS_PENDING,
    val createdAt: Long = 0L,
    val expiresAt: Long = 0L
) {
    /**
     * Derived, never stored. Without @get:Exclude the SDK treats the getter as
     * a field and writes a junk `active` boolean next to `status` — two sources
     * of truth for the same fact, and a warning on every read.
     */
    @get:Exclude
    val isActive: Boolean get() = status == STATUS_ACTIVE

    companion object {
        const val STATUS_PENDING = "pending"
        const val STATUS_ACTIVE = "active"
        const val STATUS_REVOKED = "revoked"

        /** A pending code is useless after this long. Keeps a leaked code short-lived. */
        const val TTL_MS = 10 * 60 * 1000L
    }
}

sealed interface ClaimResult {
    data object Success : ClaimResult

    /** No such code — almost always a typo. */
    data object NotFound : ClaimResult

    /** Someone already paired with this code. Codes are single-use. */
    data object AlreadyUsed : ClaimResult

    /** Older than 10 minutes. The guardian must generate a new one. */
    data object Expired : ClaimResult

    data class Failed(val message: String) : ClaimResult
}

class PairingRepository {

    private val pairings = FirebaseDatabase.getInstance().reference.child("pairings")

    /** Guardian side: publish a fresh pending code for the protected device to claim. */
    suspend fun createPairing(code: String, guardianDeviceId: String) {
        val now = System.currentTimeMillis()
        pairings.child(code).awaitSet(
            Pairing(
                guardianDeviceId = guardianDeviceId,
                protectedDeviceId = null,
                status = Pairing.STATUS_PENDING,
                createdAt = now,
                expiresAt = now + Pairing.TTL_MS
            )
        )
    }

    /** Guardian side: watch the code until the protected device claims it. */
    fun observePairing(code: String): Flow<Pairing?> =
        pairings.child(code).valueEvents().map { it.getValue(Pairing::class.java) }

    /**
     * Protected side: take ownership of a pending code.
     *
     * The checks below produce readable errors, but they are not what makes the
     * code single-use — the database rules do that (see docs/ARCHITECTURE.md
     * §7). A second device racing for the same code is rejected by the server
     * even if it passes these checks locally.
     */
    suspend fun claimPairing(code: String, protectedDeviceId: String): ClaimResult {
        return try {
            val snapshot = pairings.child(code).awaitGet()
            val pairing = snapshot.getValue(Pairing::class.java) ?: return ClaimResult.NotFound

            when {
                pairing.status != Pairing.STATUS_PENDING -> ClaimResult.AlreadyUsed
                System.currentTimeMillis() > pairing.expiresAt -> ClaimResult.Expired
                else -> {
                    pairings.child(code).awaitUpdate(
                        mapOf(
                            "protectedDeviceId" to protectedDeviceId,
                            "status" to Pairing.STATUS_ACTIVE
                        )
                    )
                    ClaimResult.Success
                }
            }
        } catch (e: Exception) {
            ClaimResult.Failed(e.message ?: "Tidak bisa terhubung ke server")
        }
    }
}
