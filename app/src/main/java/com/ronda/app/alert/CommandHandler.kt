package com.ronda.app.alert

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.ronda.app.MainActivity
import com.ronda.app.R
import com.ronda.app.detection.FlaggedAppStore
import com.ronda.app.detection.PendingUninstallStore
import com.ronda.app.detection.SafeAppStore

/**
 * Protected side of Block 4: carries out what the guardian decided.
 *
 * The two actions are deliberately asymmetric in how final they are.
 * **Mark safe** takes effect immediately — the guardian has looked at the
 * evidence and cleared the app, so the block comes down at once. **Uninstall**
 * cannot: Android only removes an app after the user confirms in a system
 * dialog, so the request is stored and surfaced as a prompt. RONDA never
 * pretends to have uninstalled something it has only asked about.
 */
class CommandHandler(private val context: Context) {

    private val commandRepository = CommandRepository()
    private val alertRepository = AlertRepository()

    /** Collects forever. Cancel the surrounding scope to stop. */
    suspend fun run(pairingId: String) {
        commandRepository.observeCommands(pairingId).collect { commands ->
            commands.filter { it.isPending }.forEach { handle(pairingId, it) }
        }
    }

    private suspend fun handle(pairingId: String, command: Command) {
        Log.d(TAG, "Guardian command: ${command.action} for ${command.packageName}")

        when (command.action) {
            Command.ACTION_MARK_SAFE -> markSafe(pairingId, command)
            Command.ACTION_UNINSTALL -> requestUninstall(command)
            else -> Log.w(TAG, "Unknown command action: ${command.action}")
        }

        // Acknowledge only after the local effect has landed, so a crash in
        // between leaves the command pending and it is retried on next connect.
        commandRepository.markExecuted(pairingId, command.commandId)
    }

    private suspend fun markSafe(pairingId: String, command: Command) {
        SafeAppStore(context).allow(command.packageName)
        // OverlayService stops itself once no flagged packages remain.
        FlaggedAppStore(context).unflag(command.packageName)
        alertRepository.updateStatus(pairingId, command.alertId, Alert.STATUS_SAFE)
        Log.d(TAG, "Marked safe, block cleared: ${command.packageName}")
    }

    private fun requestUninstall(command: Command) {
        PendingUninstallStore(context).request(command.packageName, command.alertId)
        notifyUninstallRequested(command.packageName)
    }

    /**
     * The prompt lives inside RONDA, but the phone may be in a pocket when the
     * guardian decides. This notification is the way back into the app.
     */
    private fun notifyUninstallRequested(packageName: String) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.channel_guardian_request),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.channel_guardian_request_desc)
                enableVibration(true)
            }
        )

        val open = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            packageName.hashCode(),
            open,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_delete)
            .setContentTitle(context.getString(R.string.uninstall_request_notification_title))
            .setContentText(
                context.getString(
                    R.string.uninstall_request_notification_body,
                    appLabelOf(packageName)
                )
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        manager.notify(NOTIFICATION_ID_BASE + packageName.hashCode(), notification)
    }

    private fun appLabelOf(packageName: String): String = try {
        val pm = context.packageManager
        pm.getApplicationLabel(pm.getApplicationInfo(packageName, 0)).toString()
    } catch (e: Exception) {
        packageName
    }

    private companion object {
        const val TAG = "CommandHandler"
        const val CHANNEL_ID = "ronda_guardian_request_channel"
        const val NOTIFICATION_ID_BASE = 3000
    }
}
