package com.ronda.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.ronda.app.alert.GuardianAlertService
import com.ronda.app.detection.DetectionService
import com.ronda.app.detection.FlaggedAppStore
import com.ronda.app.overlay.OverlayService
import com.ronda.app.pairing.Role
import com.ronda.app.pairing.RoleStore

/**
 * Which background services this phone runs, decided in one place for the
 * screen ([MainActivity.refreshStatus]) and for [StartupReceiver].
 *
 * The two roles run different services: a Rondor never runs detection or the
 * overlay, and a Rondee never opens an alert listener. Starting everything
 * everywhere would put a needless foreground service — and its permanent
 * notification — on both phones.
 */
object RondaServices {

    /**
     * A Rondee always detects, paired or not: detection and the overlay work
     * offline and need no guardian. A phone that was a Rondee and started
     * setup over keeps that local protection until it is set up as a Rondor —
     * otherwise a disconnect would uncover every app it had flagged.
     */
    fun runsDetection(context: Context): Boolean = when (RoleStore(context).role) {
        Role.PROTECTED -> true
        Role.GUARDIAN -> false
        null -> SettingsStore(context).keepsLocalProtection
    }

    /** Safe to call repeatedly — starting a running service is a no-op. */
    fun start(context: Context) {
        if (runsDetection(context)) {
            context.startForegroundService(Intent(context, DetectionService::class.java))
            // Resume covering what is still flagged, e.g. after a reboot.
            if (Permissions.canBlock(context) && FlaggedAppStore(context).flaggedPackages().isNotEmpty()) {
                OverlayService.start(context)
            }
        }
        val roleStore = RoleStore(context)
        if (roleStore.role == Role.GUARDIAN && roleStore.pairingIds.isNotEmpty()) {
            GuardianAlertService.start(context)
        }
    }

    /** This phone is becoming a Rondor: the Rondee half stands down. */
    fun stopDetection(context: Context) {
        context.stopService(Intent(context, DetectionService::class.java))
        OverlayService.stop(context)
    }
}

/**
 * Brings protection back without anyone opening RONDA: after the phone boots,
 * and after RONDA itself is updated (an update kills the running services).
 * Before this, a Rondee was unprotected from every restart until RONDA was
 * next opened — and an app installed in that gap was never seen at all.
 *
 * Both broadcasts are exempt from the background-start limits, so starting
 * the foreground services from here is allowed.
 */
class StartupReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED, Intent.ACTION_MY_PACKAGE_REPLACED -> {
                Log.d("StartupReceiver", "${intent.action}: restoring services")
                runCatching { RondaServices.start(context) }
                    .onFailure { Log.e("StartupReceiver", "Could not restore services", it) }
            }
        }
    }
}
