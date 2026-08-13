package com.ronda.app.detection

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.ronda.app.alert.CommandHandler
import com.ronda.app.pairing.RoleStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class DetectionService : Service() {

    companion object {
        private const val TAG = "DetectionService"
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "ronda_monitoring_channel"
    }

    private var installReceiver: InstallReceiver? = null
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "DetectionService created")

        startForeground(NOTIFICATION_ID, createPersistentNotification())
        registerInstallReceiver()
        listenForGuardianCommands()
    }

    /**
     * The guardian's half of the conversation arrives here.
     *
     * It rides on the service that is already running rather than a second
     * foreground service: the protected phone would otherwise carry two
     * permanent notifications for what the user experiences as one feature.
     */
    private fun listenForGuardianCommands() {
        val pairingId = RoleStore(this).pairingId
        if (pairingId == null) {
            Log.d(TAG, "Not paired — no guardian to take commands from")
            return
        }

        scope.launch {
            runCatching { CommandHandler(this@DetectionService).run(pairingId) }
                .onFailure { Log.e(TAG, "Command stream failed", it) }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "DetectionService started")
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "DetectionService destroyed")
        scope.cancel()
        unregisterInstallReceiver()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null // We don't provide binding
    }

    private fun registerInstallReceiver() {
        if (installReceiver == null) {
            val filter = IntentFilter().apply {
                addAction(Intent.ACTION_PACKAGE_ADDED)
                // Uninstalling a flagged app must clear its block (FR-8).
                addAction(Intent.ACTION_PACKAGE_REMOVED)
                addDataScheme("package")
            }
            installReceiver = InstallReceiver()
            // In API 33+ it is good practice to specify receiver flags, but RECEIVER_NOT_EXPORTED is only for implicit intent targeting within the app.
            // ACTION_PACKAGE_ADDED is a system broadcast, so we can register normally.
            registerReceiver(installReceiver, filter)
            Log.d(TAG, "InstallReceiver registered")
        }
    }

    private fun unregisterInstallReceiver() {
        installReceiver?.let {
            unregisterReceiver(it)
            installReceiver = null
            Log.d(TAG, "InstallReceiver unregistered")
        }
    }

    private fun createPersistentNotification(): Notification {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "RONDA Monitoring",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Keeps RONDA active in the background to detect threats"
            }
            notificationManager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_secure) // using standard icon for now
            .setContentTitle("RONDA is active")
            .setContentText("Monitoring device for malicious installations.")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }
}
