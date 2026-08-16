package com.ronda.app.ui.guardian

import com.ronda.app.core.Verdict
import kotlinx.coroutines.flow.Flow

/**
 * Everything the guardian UI needs, with all Firebase behind it.
 *
 * The UI never sees an RTDB key: a verdict is addressed by its package name and
 * the implementation maps that to whatever it stores. That is what lets
 * [FakeGuardianRepository] drive the entire interface with no network at all.
 */
interface GuardianRepository {

    /** What the guardian calls the protected person — "Ibu", "Ayah", "Nenek". */
    val protectedName: String

    fun observeVerdicts(): Flow<List<Verdict>>

    /**
     * True while the guardian device can actually reach the protected one. An
     * empty list that could mean either "nothing wrong" or "disconnected" is a
     * broken screen, so the UI always states which.
     */
    fun observeConnected(): Flow<Boolean>

    /** Records the guardian's judgement. Safe stops the overlay; unsafe keeps it. */
    suspend fun decide(packageName: String, safe: Boolean)

    /**
     * Asks the protected phone to open the system uninstall dialog. Android has
     * no way to remove an app silently without Device Owner, so the last tap
     * belongs to the person holding that phone.
     */
    suspend fun requestUninstall(packageName: String)
}
