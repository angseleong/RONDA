package com.ronda.app.detection

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.ronda.app.Permissions
import com.ronda.app.alert.Alert
import com.ronda.app.alert.AlertRepository
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

        val evaluator = RiskEvaluator(context)
        val result = evaluator.evaluate(packageName)

        if (result.riskLevel != RiskLevel.HIGH) return

        showRiskNotification(context, result)

        // Remember the package so the soft-block knows what to cover, then
        // start blocking. Detection still alerts even if blocking cannot run.
        FlaggedAppStore(context).flag(packageName)
        if (Permissions.canBlock(context)) {
            OverlayService.start(context)
        } else {
            Log.w(TAG, "Cannot block $packageName: overlay or usage-stats permission missing")
        }

        publishAlert(context, result)
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
    private fun publishAlert(context: Context, result: RiskResult) {
        val pairingId = RoleStore(context).pairingId
        if (pairingId == null) {
            Log.w(TAG, "Not paired yet — guardian cannot be notified about ${result.packageName}")
            return
        }

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            try {
                withTimeoutOrNull(ALERT_WRITE_TIMEOUT_MS) {
                    val alertId = AlertRepository().submit(pairingId, result)
                    Log.d(TAG, "Alert $alertId published for ${result.packageName}")
                } ?: Log.w(TAG, "Alert write did not confirm in time; queued for retry")
            } catch (e: Exception) {
                Log.e(TAG, "Could not publish alert for ${result.packageName}", e)
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

    private fun showRiskNotification(context: Context, result: RiskResult) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create channel for API 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Malware Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts for high-risk application installations"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert) // using standard icon for now
            .setContentTitle("HIGH RISK: Malware Detected")
            .setContentText("${result.appLabel} is suspected of stealing banking codes.")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("${result.appLabel} (${result.packageName}) is suspected of stealing banking codes.\n\n" +
                        "Install Source: ${result.installSource}\n" +
                        "Flagged Permissions: ${result.flaggedPermissions.joinToString(", ")}"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        // Use hash of package name to prevent multiple notifications for the same app building up
        notificationManager.notify(NOTIFICATION_ID_BASE + result.packageName.hashCode(), notification)
    }
}
