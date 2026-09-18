package com.ronda.app.overlay

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.core.app.NotificationCompat
import com.ronda.app.Permissions
import com.ronda.app.R
import com.ronda.app.detection.FlaggedAppStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Soft-block (FR-8): covers a flagged app with a RONDA warning whenever it is
 * brought to the foreground.
 *
 * Runs entirely on-device with no network. If FCM delivery fails or the phone is
 * offline, the block still works — that is the point of building it before Block 3.
 *
 * Known limitation, stated openly: this is a soft-block. The flagged app is not
 * frozen, and pressing Home dismisses the overlay. It is designed to hold for the
 * minutes the guardian needs to intervene, not indefinitely.
 */
class OverlayService : Service() {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private lateinit var flaggedAppStore: FlaggedAppStore
    private lateinit var foregroundAppMonitor: ForegroundAppMonitor
    private lateinit var windowManager: WindowManager

    /** Non-null while the warning is on screen. */
    private var overlayView: View? = null
    private var overlayPackage: String? = null

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "OverlayService created")

        flaggedAppStore = FlaggedAppStore(this)
        foregroundAppMonitor = ForegroundAppMonitor(this)
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        startForeground(NOTIFICATION_ID, createPersistentNotification())
        startWatching()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_STICKY

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "OverlayService destroyed")
        scope.cancel()
        hideOverlay()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startWatching() {
        scope.launch {
            while (isActive) {
                checkForegroundApp()
                delay(POLL_INTERVAL_MS)
            }
        }
    }

    private fun checkForegroundApp() {
        val flagged = flaggedAppStore.flaggedPackages()

        // Nothing left to block — e.g. the flagged app was uninstalled.
        if (flagged.isEmpty()) {
            Log.d(TAG, "No flagged apps remain, stopping")
            hideOverlay()
            stopSelf()
            return
        }

        // The permission can be revoked while the service runs (common on OEM skins).
        if (!Permissions.canBlock(this)) {
            Log.w(TAG, "Overlay or usage-stats permission revoked, stopping")
            hideOverlay()
            stopSelf()
            return
        }

        val foregroundPackage = foregroundAppMonitor.currentForegroundPackage()
        if (foregroundPackage != null && foregroundPackage in flagged) {
            showOverlay(foregroundPackage)
        } else {
            hideOverlay()
        }
    }

    private fun showOverlay(packageName: String) {
        // Already covering this exact package — nothing to do.
        if (overlayView != null && overlayPackage == packageName) return
        hideOverlay()

        val view = LayoutInflater.from(this).inflate(R.layout.overlay_warning, null)
        view.findViewById<TextView>(R.id.overlay_app_label).text = appLabelOf(packageName)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            // No FLAG_NOT_TOUCHABLE: the overlay must absorb taps so the app
            // underneath cannot be used. No FLAG_NOT_FOCUSABLE either, so Back
            // does not fall through to the flagged app.
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.OPAQUE
        ).apply {
            // One red field edge to edge: on API 30+ the flags above alone still
            // leave a dark band under the status bar, because the window is fitted
            // to the system insets. Fit none and extend into the cutout.
            fitInsetsTypes = 0
            layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
        }

        try {
            windowManager.addView(view, params)
            overlayView = view
            overlayPackage = packageName
            Log.d(TAG, "Overlay shown over $packageName")
        } catch (e: WindowManager.BadTokenException) {
            // Happens when SYSTEM_ALERT_WINDOW is missing or blocked by the OEM.
            Log.e(TAG, "Could not draw overlay over $packageName", e)
        }
    }

    private fun hideOverlay() {
        val view = overlayView ?: return
        try {
            windowManager.removeView(view)
        } catch (e: IllegalArgumentException) {
            Log.w(TAG, "Overlay was already removed", e)
        }
        overlayView = null
        overlayPackage = null
        Log.d(TAG, "Overlay hidden")
    }

    private fun appLabelOf(packageName: String): String = try {
        val appInfo = packageManager.getApplicationInfo(packageName, 0)
        packageManager.getApplicationLabel(appInfo).toString()
    } catch (e: Exception) {
        packageName
    }

    private fun createPersistentNotification(): Notification {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.channel_blocking),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = getString(R.string.channel_blocking_desc)
        }
        notificationManager.createNotificationChannel(channel)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_shield_alert)
            .setContentTitle(getString(R.string.blocking_notification_title))
            .setContentText(getString(R.string.blocking_notification_body))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }

    companion object {
        private const val TAG = "OverlayService"
        private const val NOTIFICATION_ID = 2
        private const val CHANNEL_ID = "ronda_blocking_channel"
        private const val POLL_INTERVAL_MS = 700L

        /** Safe to call repeatedly — starting an already-running service is a no-op. */
        fun start(context: Context) {
            context.startForegroundService(Intent(context, OverlayService::class.java))
        }
    }
}
