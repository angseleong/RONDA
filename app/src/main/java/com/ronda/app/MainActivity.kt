package com.ronda.app

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.ronda.app.detection.DetectionService
import com.ronda.app.detection.FlaggedAppStore
import com.ronda.app.overlay.OverlayService
import com.ronda.app.ui.setup.SetupScreen
import com.ronda.app.ui.setup.SetupStatus
import com.ronda.app.ui.theme.RONDATheme

class MainActivity : ComponentActivity() {

    private var status by mutableStateOf(SetupStatus(false, false, false))

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { refreshStatus() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RONDATheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SetupScreen(
                        status = status,
                        onRequestNotifications = ::requestNotificationPermission,
                        onOpenOverlaySettings = {
                            startActivity(Permissions.overlaySettingsIntent(this))
                        },
                        onOpenUsageSettings = {
                            startActivity(Permissions.usageStatsSettingsIntent())
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    /**
     * Permissions are granted in system Settings, so the result arrives as a
     * resume rather than a callback. OEM power management also revokes them
     * silently, which is why this runs on every launch.
     */
    override fun onResume() {
        super.onResume()
        refreshStatus()
    }

    private fun refreshStatus() {
        status = SetupStatus(
            notifications = Permissions.hasNotifications(this),
            overlay = Permissions.hasOverlay(this),
            usageStats = Permissions.hasUsageStats(this)
        )

        startDetectionService()

        // Restore blocking after a reboot or a process kill: if an app is still
        // flagged and we are allowed to block, resume covering it.
        if (Permissions.canBlock(this) && FlaggedAppStore(this).flaggedPackages().isNotEmpty()) {
            OverlayService.start(this)
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun startDetectionService() {
        startForegroundService(Intent(this, DetectionService::class.java))
    }
}
