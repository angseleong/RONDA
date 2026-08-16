package com.ronda.app.alert

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.ronda.app.MainActivity
import com.ronda.app.R
import com.ronda.app.core.RiskEvaluator
import com.ronda.app.core.RiskLevel
import com.ronda.app.pairing.Role
import com.ronda.app.ui.guardian.components.explanationKeys
import com.ronda.app.ui.guardian.components.sentenceRes
import com.ronda.app.pairing.RoleStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * Keeps the guardian's Realtime Database connection open and turns every new
 * alert into a high-priority notification.
 *
 * This is the piece that stands in for FCM. Google shut off the legacy server
 * key in June 2024, so a device cannot push to another device without a backend
 * — and a Cloud Function needs a billing plan. A held-open RTDB listener
 * delivers the same thing for this POC: the guardian is notified within a
 * second of the write, whether or not RONDA is the app on screen.
 *
 * The trade-off, stated plainly: this only works while the service is alive. If
 * the OEM kills it, alerts are seen when RONDA is next opened, not before. FCM
 * would survive that, and is the right upgrade after the hackathon.
 */
class GuardianAlertService : Service() {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private lateinit var seenAlerts: SeenAlertStore

    override fun onCreate() {
        super.onCreate()
        seenAlerts = SeenAlertStore(this)

        val roleStore = RoleStore(this)
        val pairingId = roleStore.pairingId

        // Belt and braces: nothing should start this on a protected device.
        if (roleStore.role != Role.GUARDIAN || pairingId == null) {
            Log.w(TAG, "Not a paired guardian device, stopping")
            stopSelf()
            return
        }

        startForeground(NOTIFICATION_ID, createPersistentNotification())
        watchAlerts(pairingId)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_STICKY

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
        Log.d(TAG, "GuardianAlertService destroyed")
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun watchAlerts(pairingId: String) {
        scope.launch {
            AlertRepository().observeAlerts(pairingId)
                .catch { Log.e(TAG, "Alert stream failed", it) }
                .collect { alerts ->
                    // Oldest first, so the newest threat ends up on top of
                    // the notification shade.
                    val known = alerts.filter { it.alertId.isNotEmpty() }.sortedBy { it.timestamp }

                    known.filter { !seenAlerts.isNotified(it.alertId) }
                        .forEach { alert ->
                            notifyAlert(alert)
                            seenAlerts.markNotified(alert.alertId)
                        }

                    // The protected phone sets this status only after the OS
                    // confirms the package is gone, so it is safe to tell the
                    // guardian the job is actually done — the tap happened on
                    // the other phone and they have no other way to know.
                    known.filter {
                        it.status == Alert.STATUS_UNINSTALLED &&
                            !seenAlerts.isNotified(outcomeKey(it.alertId))
                    }.forEach { alert ->
                        notifyUninstalled(alert)
                        seenAlerts.markNotified(outcomeKey(alert.alertId))
                    }
                }
        }
    }

    private fun notifyAlert(alert: Alert) {
        Log.d(TAG, "New alert for guardian: ${alert.packageName}")

        // Only DARURAT earns the alarm treatment. A PERINGATAN that buzzes like
        // an emergency trains the guardian to swipe both away.
        val darurat = RiskLevel.of(alert.score) == RiskLevel.DARURAT

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(
            NotificationChannel(
                if (darurat) ALERT_CHANNEL_ID else WARN_CHANNEL_ID,
                getString(R.string.channel_guardian_alerts),
                if (darurat) NotificationManager.IMPORTANCE_HIGH
                else NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = getString(R.string.channel_guardian_alerts_desc)
                enableVibration(true)
                // Takes effect only if the guardian grants DND policy access;
                // harmless otherwise.
                setBypassDnd(darurat)
            }
        )

        val pendingIntent = openAlertIntent(alert)

        val body = getString(R.string.alert_notification_body, alert.appLabel)
        val notification =
            NotificationCompat.Builder(this, if (darurat) ALERT_CHANNEL_ID else WARN_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(
                getString(R.string.alert_notification_title) + " (${alert.score}/100)"
            )
            .setContentText(body)
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    (listOf(body) + topReasons(alert)).joinToString("\n\n")
                )
            )
            .setPriority(
                if (darurat) NotificationCompat.PRIORITY_HIGH
                else NotificationCompat.PRIORITY_DEFAULT
            )
            .setCategory(
                if (darurat) NotificationCompat.CATEGORY_ALARM
                else NotificationCompat.CATEGORY_MESSAGE
            )
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        manager.notify(ALERT_NOTIFICATION_ID_BASE + alert.alertId.hashCode(), notification)
    }

    /**
     * The two worst sentences, in the guardian's own words for the protected
     * person. Rebuilt locally from the alert's signal keys — the sentences are
     * never sent over the wire, because only this device knows the nickname.
     */
    private fun topReasons(alert: Alert): List<String> {
        val name = RoleStore(this).protectedName
        val verdict = RiskEvaluator.evaluate(
            packageName = alert.packageName,
            appLabel = alert.appLabel,
            activeKeys = alert.signals.toSet(),
            detectedAt = alert.timestamp
        )
        return explanationKeys(verdict)
            .mapNotNull { sentenceRes(it) }
            .take(2)
            .map { getString(it, name) }
    }

    /**
     * Good news, so it does not use the alarm channel: this must be findable in
     * the shade without being another thing that buzzes like an emergency.
     */
    private fun notifyUninstalled(alert: Alert) {
        Log.d(TAG, "Protected phone removed ${alert.packageName}")

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(
            NotificationChannel(
                OUTCOME_CHANNEL_ID,
                getString(R.string.channel_guardian_outcome),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = getString(R.string.channel_guardian_outcome_desc) }
        )

        val notification = NotificationCompat.Builder(this, OUTCOME_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_delete)
            .setContentTitle(getString(R.string.uninstalled_notification_title))
            .setContentText(getString(R.string.uninstalled_notification_body, alert.appLabel))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(openAlertIntent(alert))
            .setAutoCancel(true)
            .build()

        // The threat is resolved; leaving its red alert in the shade would have
        // the guardian acting on something that no longer exists.
        manager.cancel(ALERT_NOTIFICATION_ID_BASE + alert.alertId.hashCode())
        manager.notify(OUTCOME_NOTIFICATION_ID_BASE + alert.alertId.hashCode(), notification)
    }

    private fun openAlertIntent(alert: Alert): PendingIntent {
        val openDetail = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(MainActivity.EXTRA_PACKAGE, alert.packageName)
        }
        return PendingIntent.getActivity(
            this,
            alert.alertId.hashCode(),
            openDetail,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    /** Separate key so the outcome notification is independent of the alert's. */
    private fun outcomeKey(alertId: String): String = "$alertId:uninstalled"

    private fun createPersistentNotification(): Notification {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_ID,
                getString(R.string.channel_guardian_watch),
                NotificationManager.IMPORTANCE_LOW
            ).apply { description = getString(R.string.channel_guardian_watch_desc) }
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_view)
            .setContentTitle(getString(R.string.guardian_watch_title))
            .setContentText(getString(R.string.guardian_watch_body))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }

    companion object {
        private const val TAG = "GuardianAlertService"
        private const val NOTIFICATION_ID = 3
        private const val ALERT_NOTIFICATION_ID_BASE = 2000
        private const val OUTCOME_NOTIFICATION_ID_BASE = 2500
        private const val CHANNEL_ID = "ronda_guardian_watch_channel"
        private const val ALERT_CHANNEL_ID = "ronda_guardian_alert_channel"
        private const val WARN_CHANNEL_ID = "ronda_guardian_warn_channel"
        private const val OUTCOME_CHANNEL_ID = "ronda_guardian_outcome_channel"

        /** Safe to call repeatedly — starting a running service is a no-op. */
        fun start(context: Context) {
            context.startForegroundService(Intent(context, GuardianAlertService::class.java))
        }
    }
}
