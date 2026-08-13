package com.ronda.app

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

/**
 * Turns on Realtime Database disk persistence before anything touches the SDK.
 *
 * This matters for the protected device: an alert written the moment a malicious
 * app is installed must survive a dead connection. With persistence on, the
 * write is queued locally and flushed when the network returns, so a scammer
 * cannot suppress the alert by telling the victim to switch off mobile data.
 *
 * setPersistenceEnabled() throws if called after the first FirebaseDatabase
 * access, which is why it lives here and not in an Activity.
 */
class RondaApp : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}
