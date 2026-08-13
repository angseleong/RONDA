package com.ronda.app.detection

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.ronda.app.R

class InstallReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "InstallReceiver"
        private const val CHANNEL_ID = "ronda_alert_channel"
        private const val NOTIFICATION_ID_BASE = 1000
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_PACKAGE_ADDED) {
            val packageName = intent.data?.schemeSpecificPart ?: return
            Log.d(TAG, "New package installed: $packageName")

            val evaluator = RiskEvaluator(context)
            val result = evaluator.evaluate(packageName)

            if (result.riskLevel == RiskLevel.HIGH) {
                showRiskNotification(context, result)
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
