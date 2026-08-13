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
import com.ronda.app.pairing.Role
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
                    alerts
                        .filter { it.alertId.isNotEmpty() && !seenAlerts.isNotified(it.alertId) }
                        // Oldest first, so the newest threat ends up on top of
                        // the notification shade.
                        .sortedBy { it.timestamp }
                        .forEach { alert ->
                            notifyAlert(alert)
                            seenAlerts.markNotified(alert.alertId)
                        }
                }
        }
    }

    private fun notifyAlert(alert: Alert) {
        Log.d(TAG, "New alert for guardian: ${alert.packageName}")

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(
            NotificationChannel(
                ALERT_CHANNEL_ID,
                getString(R.string.channel_guardian_alerts),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = getString(R.string.channel_guardian_alerts_desc)
                enableVibration(true)
            }
        )

        val openDetail = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(MainActivity.EXTRA_ALERT_ID, alert.alertId)
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            alert.alertId.hashCode(),
            openDetail,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val body = getString(R.string.alert_notification_body, alert.appLabel)
        val notification = NotificationCompat.Builder(this, ALERT_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(getString(R.string.alert_notification_title))
            .setContentText(body)
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    getString(
                        R.string.alert_notification_detail,
                        alert.appLabel,
                        alert.installSource,
                        alert.flaggedPermissions.joinToString(", ")
                    )
                )
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        manager.notify(ALERT_NOTIFICATION_ID_BASE + alert.alertId.hashCode(), notification)
    }

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
        private const val CHANNEL_ID = "ronda_guardian_watch_channel"
        private const val ALERT_CHANNEL_ID = "ronda_guardian_alert_channel"

        /** Safe to call repeatedly — starting a running service is a no-op. */
        fun start(context: Context) {
            context.startForegroundService(Intent(context, GuardianAlertService::class.java))
        }
    }
}
