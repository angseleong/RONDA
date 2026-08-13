package com.ronda.app

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Coroutine adapters for the Realtime Database SDK.
 *
 * Firebase ships these in kotlinx-coroutines-play-services, but pulling in a
 * whole artifact for three functions is not worth it here (AGENTS.md §1).
 */

private suspend fun <T> Task<T>.await(): T = suspendCoroutine { cont ->
    addOnSuccessListener { cont.resume(it) }
    addOnFailureListener { cont.resumeWithException(it) }
}

suspend fun DatabaseReference.awaitSet(value: Any?) {
    setValue(value).await()
}

suspend fun DatabaseReference.awaitUpdate(values: Map<String, Any?>) {
    updateChildren(values).await()
}

/** One-shot read. Goes to the server when online, falls back to cache when not. */
suspend fun Query.awaitGet(): DataSnapshot = get().await()

/**
 * Emits on every change to this node, starting with the current value.
 *
 * This is the channel that replaces FCM: the guardian keeps one of these open
 * and hears about a new alert the moment the protected device writes it.
 */
fun Query.valueEvents(): Flow<DataSnapshot> = callbackFlow {
    val listener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            trySend(snapshot)
        }

        override fun onCancelled(error: DatabaseError) {
            close(error.toException())
        }
    }
    addValueEventListener(listener)
    awaitClose { removeEventListener(listener) }
}
