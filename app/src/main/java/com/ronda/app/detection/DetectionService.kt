package com.ronda.app.detection

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.ronda.app.MainActivity
import com.ronda.app.R
import com.ronda.app.alert.CommandHandler
import com.ronda.app.localized
import com.ronda.app.pairing.RoleStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.ronda.app.alert.AlertRepository
import com.ronda.app.core.RiskEvaluator
import com.ronda.app.core.Verdict
import com.ronda.app.core.VerdictState
import com.ronda.app.detect.SignalExtractor
import com.ronda.app.overlay.OverlayService
import com.ronda.app.Permissions

class DetectionService : Service() {

    companion object {
        private const val TAG = "DetectionService"
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "ronda_monitoring_channel"
    }

    private var installReceiver: InstallReceiver? = null
    private var commandJob: Job? = null
    private var commandPairingId: String? = null
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "DetectionService created")

        startForeground(NOTIFICATION_ID, createPersistentNotification())
        registerInstallReceiver()
    }

    /**
     * The guardian's half of the conversation arrives here.
     *
     * It rides on the service that is already running rather than a second
     * foreground service: the protected phone would otherwise carry two
     * permanent notifications for what the user experiences as one feature.
     *
     * Deliberately *not* in onCreate. Detection starts as soon as the role is
     * chosen, which is before pairing — reading the pairing id once at creation
     * meant the phone was still unpaired at the only moment it ever looked, and
     * the guardian's commands were never collected on a freshly set up device.
     * MainActivity re-starts this service on every resume, so onStartCommand is
     * the hook that eventually sees a pairing id.
     */
    private fun listenForGuardianCommands() {
        val pairingId = RoleStore(this).pairingId
        if (commandJob?.isActive == true && commandPairingId == pairingId) return

        // Unpaired or re-paired since: the old listener would keep acting on
        // the previous guardian's commands and never hear the new one's.
        commandJob?.cancel()
        commandJob = null
        commandPairingId = pairingId

        if (pairingId == null) {
            Log.d(TAG, "Not paired yet — will look again on next start")
            return
        }

        Log.d(TAG, "Listening for guardian commands on $pairingId")
        commandJob = scope.launch {
            runCatching { CommandHandler(this@DetectionService).run(pairingId) }
                .onFailure { Log.e(TAG, "Command stream failed", it) }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "DetectionService started")
        listenForGuardianCommands()
        
        if (intent?.action == MainActivity.ACTION_SCAN_EXISTING) {
            scanExistingApps()
        }
        
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

    /**
     * The visible price of a foreground service, and also the consent indicator
     * the PRD asks for: the protected person can always see RONDA is watching.
     */
    private fun createPersistentNotification(): Notification {
        val loc = localized()
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            CHANNEL_ID,
            loc.getString(R.string.channel_monitoring),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = loc.getString(R.string.channel_monitoring_desc)
        }
        notificationManager.createNotificationChannel(channel)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_shield_check)
            .setContentTitle(loc.getString(R.string.monitoring_notification_title))
            .setContentText(loc.getString(R.string.monitoring_notification_body))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }

    /**
     * Scans all installed non-system apps for risks. Used for initial scan and manual scans.
     */
    fun scanExistingApps() {
        val pairingId = RoleStore(this).pairingId
        if (pairingId == null) {
            Log.w(TAG, "Not paired yet — skipping scanExistingApps")
            return
        }

        scope.launch {
            Log.d(TAG, "Starting manual/initial scan of installed apps")
            val pm = packageManager
            val packages = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS)
            val extractor = SignalExtractor(pm)
            var foundRisk = false

            for (pkg in packages) {
                val packageName = pkg.packageName
                if (packageName == this@DetectionService.packageName) continue

                val appInfo = pkg.applicationInfo
                if (appInfo != null && appInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0) continue

                if (SafeAppStore(this@DetectionService).isAllowed(packageName)) continue
                if (FlaggedAppStore(this@DetectionService).isFlagged(packageName)) continue

                val activeKeys = extractor.extract(packageName)
                if (activeKeys.isEmpty()) continue

                val appLabel = extractor.labelOf(packageName)
                val verdict = RiskEvaluator.evaluate(
                    packageName = packageName,
                    appLabel = appLabel,
                    activeKeys = activeKeys
                )

                if (verdict.state == VerdictState.PENDING_GUARDIAN) {
                    foundRisk = true
                    Log.d(TAG, "Found risk during manual scan: $packageName (${verdict.vector})")
                    
                    showRiskNotification(this@DetectionService, verdict)
                    FlaggedAppStore(this@DetectionService).flag(packageName)
                    publishAlert(pairingId, verdict)
                }
            }

            if (foundRisk && Permissions.canBlock(this@DetectionService)) {
                OverlayService.start(this@DetectionService)
            }
            Log.d(TAG, "Finished manual/initial scan")
        }
    }

    private fun publishAlert(pairingId: String, verdict: Verdict) {
        scope.launch(Dispatchers.IO) {
            try {
                withTimeoutOrNull(8_000L) {
                    val alertId = AlertRepository().submit(pairingId, verdict)
                    Log.d(TAG, "Alert $alertId published for ${verdict.packageName} (manual scan)")
                } ?: Log.w(TAG, "Alert write did not confirm in time; queued for retry (manual scan)")
            } catch (e: Exception) {
                Log.e(TAG, "Could not publish alert for ${verdict.packageName}", e)
            }
        }
    }

    private fun showRiskNotification(context: Context, verdict: Verdict) {
        val localizedContext = context.localized()
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            "ronda_alert_channel",
            localizedContext.getString(R.string.channel_detection),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = localizedContext.getString(R.string.channel_detection_desc)
        }
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(context, "ronda_alert_channel")
            .setSmallIcon(R.drawable.ic_shield_alert)
            .setContentTitle(localizedContext.getString(R.string.detected_notification_title, verdict.appLabel))
            .setContentText(verdict.reasons.firstOrNull().orEmpty().replace("**", ""))
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(verdict.reasons.joinToString("\n\n").replace("**", "")))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1000 + verdict.packageName.hashCode(), notification)
    }
}
