package com.ronda.app.detection

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.ronda.app.Permissions
import com.ronda.app.R
import com.ronda.app.localized
import com.ronda.app.alert.Alert
import com.ronda.app.alert.AlertRepository
import com.ronda.app.core.RiskEvaluator
import com.ronda.app.core.Verdict
import com.ronda.app.core.VerdictState
import com.ronda.app.detect.SignalExtractor
import com.ronda.app.overlay.OverlayService
import com.ronda.app.pairing.RoleStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class InstallReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "InstallReceiver"
        private const val CHANNEL_ID = "ronda_alert_channel"
        private const val NOTIFICATION_ID_BASE = 1000
        private const val ALERT_WRITE_TIMEOUT_MS = 8_000L
    }

    override fun onReceive(context: Context, intent: Intent) {
        val packageName = intent.data?.schemeSpecificPart ?: return

        when (intent.action) {
            Intent.ACTION_PACKAGE_ADDED -> onPackageAdded(context, packageName)
            Intent.ACTION_PACKAGE_REMOVED -> {
                // An update fires REMOVED + ADDED; the app is still installed,
                // so it must stay flagged.
                if (!intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)) {
                    onPackageRemoved(context, packageName)
                }
            }
        }
    }

    private fun onPackageAdded(context: Context, packageName: String) {
        Log.d(TAG, "New package installed: $packageName")

        // The guardian has already looked at this one and cleared it. Flagging
        // it again would overrule a decision only they are allowed to make.
        if (SafeAppStore(context).isAllowed(packageName)) {
            Log.d(TAG, "Package is on the guardian's allowlist, skipping: $packageName")
            return
        }

        val extractor = SignalExtractor(context.packageManager)
        val verdict = RiskEvaluator.evaluate(
            packageName = packageName,
            appLabel = extractor.labelOf(packageName),
            activeKeys = extractor.extract(packageName)
        )
        Log.d(TAG, "Verdict for $packageName: ${verdict.vector} (${verdict.level})")

        // PENDING_GUARDIAN is exactly "score >= 60" — the band where the overlay
        // runs and the guardian is worth waking. AMAN and RENDAH stay on this
        // phone: an alert the guardian cannot act on only teaches them to ignore
        // the next one.
        if (verdict.state != VerdictState.PENDING_GUARDIAN) return

        showRiskNotification(context, verdict)

        // Remember the package so the soft-block knows what to cover, then
        // start blocking. Detection still alerts even if blocking cannot run.
        FlaggedAppStore(context).flag(packageName)
        if (Permissions.canBlock(context)) {
            OverlayService.start(context)
        } else {
            Log.w(TAG, "Cannot block $packageName: overlay or usage-stats permission missing")
        }

        publishAlert(context, verdict)
    }

    /**
     * Tell the guardian. Local protection above already happened, so a failure
     * here degrades RONDA to the offline behaviour of Block 2 rather than
     * leaving the victim unprotected.
     *
     * goAsync() buys the write a few extra seconds beyond the ~10s a receiver
     * normally gets. If it does not land in time, Realtime Database persistence
     * keeps the write queued on disk and flushes it when the app next runs.
     */
    private fun publishAlert(context: Context, verdict: Verdict) {
        val pairingId = RoleStore(context).pairingId
        if (pairingId == null) {
            Log.w(TAG, "Not paired yet — guardian cannot be notified about ${verdict.packageName}")
            return
        }

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            try {
                withTimeoutOrNull(ALERT_WRITE_TIMEOUT_MS) {
                    val alertId = AlertRepository().submit(pairingId, verdict)
                    Log.d(TAG, "Alert $alertId published for ${verdict.packageName}")
                } ?: Log.w(TAG, "Alert write did not confirm in time; queued for retry")
            } catch (e: Exception) {
                Log.e(TAG, "Could not publish alert for ${verdict.packageName}", e)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun onPackageRemoved(context: Context, packageName: String) {
        // A reinstall is a new question, so any earlier decision is forgotten
        // regardless of whether this package was the flagged one.
        SafeAppStore(context).forget(packageName)

        val pendingUninstalls = PendingUninstallStore(context)
        val alertId = pendingUninstalls.alertIdFor(packageName)
        pendingUninstalls.clear(packageName)

        val store = FlaggedAppStore(context)
        if (store.isFlagged(packageName)) {
            Log.d(TAG, "Flagged package uninstalled, clearing block: $packageName")
            // OverlayService stops itself once no flagged packages remain.
            store.unflag(packageName)
            ProtectedHistoryStore(context).record(packageName, packageName, "uninstalled")
        }

        // Only now is it true that the app is gone — report it to the guardian.
        if (alertId != null) reportUninstalled(context, alertId, packageName)
    }

    private fun reportUninstalled(context: Context, alertId: String, packageName: String) {
        val pairingId = RoleStore(context).pairingId ?: return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            try {
                withTimeoutOrNull(ALERT_WRITE_TIMEOUT_MS) {
                    AlertRepository().updateStatus(pairingId, alertId, Alert.STATUS_UNINSTALLED)
                    Log.d(TAG, "Reported $packageName as uninstalled to guardian")
                } ?: Log.w(TAG, "Uninstall report did not confirm in time; queued for retry")
            } catch (e: Exception) {
                Log.e(TAG, "Could not report uninstall of $packageName", e)
            } finally {
                pendingResult.finish()
            }
        }
    }

    /**
     * The protected phone's own notice. It says the same thing the overlay
     * says, in the same plain words, so the shade and the screen never disagree.
     */
    private fun showRiskNotification(context: Context, verdict: Verdict) {
        val localizedContext = context.localized()
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            CHANNEL_ID,
            localizedContext.getString(R.string.channel_detection),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = localizedContext.getString(R.string.channel_detection_desc)
        }
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_shield_alert)
            .setContentTitle(localizedContext.getString(R.string.detected_notification_title, verdict.appLabel))
            .setContentText(verdict.reasons.firstOrNull().orEmpty().replace("**", ""))
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(verdict.reasons.joinToString("\n\n").replace("**", "")))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        // Use hash of package name to prevent multiple notifications for the same app building up
        notificationManager.notify(NOTIFICATION_ID_BASE + verdict.packageName.hashCode(), notification)
    }
}
