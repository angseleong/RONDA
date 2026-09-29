package com.ronda.app.alert

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.ronda.app.MainActivity
import com.ronda.app.R
import com.ronda.app.RondaNotifications
import com.ronda.app.localized
import com.ronda.app.detection.DetectionService
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
        // Our own disconnect, echoed back on the shared channel. Acting on it
        // would unpair a phone that may already be paired to someone new.
        if (command.from == Command.FROM_PROTECTED) return

        Log.d(TAG, "Guardian command: ${command.action} for ${command.packageName}")

        when (command.action) {
            Command.ACTION_MARK_SAFE -> markSafe(pairingId, command)
            Command.ACTION_REVOKE_SAFE -> revokeSafe(pairingId, command)
            Command.ACTION_UNINSTALL -> requestUninstall(command)
            Command.ACTION_SCAN -> requestScan()
            Command.ACTION_DISCONNECT -> handleDisconnect(pairingId)
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
        com.ronda.app.detection.ProtectedHistoryStore(context).record(command.packageName, command.packageName, "safe")
        Log.d(TAG, "Marked safe, block cleared: ${command.packageName}")
        notifyMarkedSafe(command.packageName)
    }

    /**
     * The guardian took "safe" back inside the undo window. Everything
     * [markSafe] did is reversed: the app leaves the allowlist, is flagged
     * again, and the overlay comes back. Without this the guardian's screen
     * would say "dangerous" over an app that nothing on this phone covers.
     */
    private suspend fun revokeSafe(pairingId: String, command: Command) {
        val packageName = command.packageName
        SafeAppStore(context).forget(packageName)
        com.ronda.app.detection.ProtectedHistoryStore(context).retract(packageName, "safe")
        // Removed in the meantime: nothing left to cover.
        if (isInstalled(packageName)) {
            FlaggedAppStore(context).flag(packageName)
            if (com.ronda.app.Permissions.canBlock(context)) {
                com.ronda.app.overlay.OverlayService.start(context)
            }
        }
        alertRepository.updateStatus(pairingId, command.alertId, Alert.STATUS_UNSAFE)
        context.getSystemService(NotificationManager::class.java)
            .cancel(MARKED_SAFE_NOTIFICATION_ID_BASE + packageName.hashCode())
        Log.d(TAG, "Safe revoked, block restored: $packageName")
    }

    private fun isInstalled(packageName: String): Boolean = runCatching {
        context.packageManager.getApplicationInfo(packageName, 0)
    }.isSuccess

    /**
     * The block lifting is otherwise silent, and an app that was covered
     * yesterday opening normally today would look like RONDA had stopped.
     */
    private fun notifyMarkedSafe(packageName: String) {
        val loc = context.localized()
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = RondaNotifications.ensureChannel(
            context,
            RondaNotifications.CHANNEL_GUARDIAN_DECISION,
            loc.getString(R.string.channel_guardian_decision),
            NotificationManager.IMPORTANCE_DEFAULT,
            RondaNotifications.Sound.RESOLVED,
            loc.getString(R.string.channel_guardian_decision_desc)
        )
        val notification = NotificationCompat.Builder(context, channel)
            .setSmallIcon(RondaNotifications.SMALL_ICON)
            .setColor(RondaNotifications.COLOR_SAFE)
            .setContentTitle(loc.getString(R.string.marked_safe_notification_title))
            .setContentText(loc.getString(R.string.marked_safe_notification_body, appLabelOf(packageName)))
            .setAutoCancel(true)
            .build()
        manager.notify(MARKED_SAFE_NOTIFICATION_ID_BASE + packageName.hashCode(), notification)
    }

    /**
     * Works with the app closed. MainActivity re-reads the pairing on every
     * resume, and its own listener updates the screen if it is open, so there
     * is no need to pull the app to the front over whatever the user is doing.
     */
    private fun handleDisconnect(pairingId: String) {
        val roleStore = com.ronda.app.pairing.RoleStore(context)
        // A stale command for a pairing this phone has already left.
        if (roleStore.pairingId != pairingId) return

        Log.d(TAG, "Guardian disconnected the pairing")
        // A Rondee has one pairing, so this was the last: set up from scratch.
        // Detection and the overlay keep running — this service included —
        // so flagged apps stay covered; see RondaServices.runsDetection.
        com.ronda.app.SettingsStore(context).startSetupOver(roleStore)
        notifyDisconnected()
    }

    private fun notifyDisconnected() {
        val loc = context.localized()
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = RondaNotifications.ensureChannel(
            context,
            RondaNotifications.CHANNEL_DISCONNECTED,
            loc.getString(R.string.disconnect_notification_title),
            NotificationManager.IMPORTANCE_HIGH,
            RondaNotifications.Sound.WARN
        )
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(RondaNotifications.SMALL_ICON)
            .setColor(RondaNotifications.COLOR_WARN)
            .setContentTitle(loc.getString(R.string.disconnect_notification_title))
            .setContentText(loc.getString(R.string.disconnect_notification_body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        manager.notify(9999, notification)
    }

    private fun requestUninstall(command: Command) {
        PendingUninstallStore(context).request(command.packageName, command.alertId)
        notifyUninstallRequested(command.packageName)
    }

    private fun requestScan() {
        val intent = Intent(context, DetectionService::class.java).apply {
            action = MainActivity.ACTION_SCAN_EXISTING
        }
        context.startForegroundService(intent)
        Log.d(TAG, "Requested a scan of installed apps")
    }

    /**
     * The prompt lives inside RONDA, but the phone may be in a pocket when the
     * guardian decides. This notification is the way back into the app.
     */
    private fun notifyUninstallRequested(packageName: String) {
        val loc = context.localized()
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = RondaNotifications.ensureChannel(
            context,
            RondaNotifications.CHANNEL_GUARDIAN_REQUEST,
            loc.getString(R.string.channel_guardian_request),
            NotificationManager.IMPORTANCE_HIGH,
            RondaNotifications.Sound.INFO,
            loc.getString(R.string.channel_guardian_request_desc)
        ) { enableVibration(true) }

        val open = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            packageName.hashCode(),
            open,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channel)
            .setSmallIcon(RondaNotifications.SMALL_ICON)
            .setColor(RondaNotifications.COLOR_TRUST)
            .setContentTitle(loc.getString(R.string.uninstall_request_notification_title))
            .setContentText(
                loc.getString(
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
        const val NOTIFICATION_ID_BASE = 3000
        const val MARKED_SAFE_NOTIFICATION_ID_BASE = 3500
    }
}
