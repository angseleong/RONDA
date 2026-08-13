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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.ronda.app.alert.Alert
import com.ronda.app.alert.AlertRepository
import com.ronda.app.alert.GuardianAlertService
import com.ronda.app.detection.DetectionService
import com.ronda.app.detection.FlaggedAppStore
import com.ronda.app.overlay.OverlayService
import com.ronda.app.pairing.Role
import com.ronda.app.pairing.RoleStore
import com.ronda.app.ui.guardian.AlertDetailScreen
import com.ronda.app.ui.guardian.GuardianHomeScreen
import com.ronda.app.ui.guardian.GuardianPairingScreen
import com.ronda.app.ui.onboarding.RoleSelectionScreen
import com.ronda.app.ui.protectedrole.ProtectedPairingScreen
import com.ronda.app.ui.setup.SetupScreen
import com.ronda.app.ui.setup.SetupStatus
import com.ronda.app.ui.theme.RONDATheme

/**
 * The whole app is one Activity. Which screen it shows is a function of two
 * stored values — the role and whether this device is paired — so there is no
 * navigation graph to keep in sync with them.
 */
class MainActivity : ComponentActivity() {

    private val roleStore by lazy { RoleStore(this) }

    private var status by mutableStateOf(SetupStatus(false, false, false))
    private var role by mutableStateOf<Role?>(null)
    private var pairingId by mutableStateOf<String?>(null)
    private var selectedAlertId by mutableStateOf<String?>(null)

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { refreshStatus() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        role = roleStore.role
        pairingId = roleStore.pairingId
        selectedAlertId = intent.getStringExtra(EXTRA_ALERT_ID)

        // Only on a genuinely new launch, so a rotation does not re-prompt.
        if (savedInstanceState == null) requestNotificationPermission()

        setContent {
            RONDATheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val screenModifier = Modifier.padding(innerPadding)
                    when (role) {
                        null -> RoleSelectionScreen(
                            onRoleChosen = ::chooseRole,
                            modifier = screenModifier
                        )

                        Role.GUARDIAN -> GuardianFlow(screenModifier)
                        Role.PROTECTED -> ProtectedFlow(screenModifier)
                    }
                }
            }
        }
    }

    /** The guardian taps an alert notification while RONDA is already open. */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        intent.getStringExtra(EXTRA_ALERT_ID)?.let { selectedAlertId = it }
    }

    override fun onResume() {
        super.onResume()
        refreshStatus()
    }

    @Composable
    private fun GuardianFlow(modifier: Modifier) {
        val currentPairing = pairingId
        if (currentPairing == null) {
            GuardianPairingScreen(
                guardianDeviceId = roleStore.deviceId,
                onPaired = ::onPaired,
                modifier = modifier
            )
            return
        }

        var alerts by remember { mutableStateOf(emptyList<Alert>()) }
        LaunchedEffect(currentPairing) {
            AlertRepository().observeAlerts(currentPairing).collect { alerts = it }
        }

        val selected = alerts.firstOrNull { it.alertId == selectedAlertId }
        if (selected != null) {
            AlertDetailScreen(
                alert = selected,
                // Wired to commands/{pairingId} in Block 4.
                onUninstall = {},
                onMarkSafe = {},
                onBack = { selectedAlertId = null },
                modifier = modifier
            )
        } else {
            GuardianHomeScreen(
                alerts = alerts,
                onAlertClick = { selectedAlertId = it.alertId },
                modifier = modifier
            )
        }
    }

    @Composable
    private fun ProtectedFlow(modifier: Modifier) {
        if (pairingId == null) {
            ProtectedPairingScreen(
                protectedDeviceId = roleStore.deviceId,
                onPaired = ::onPaired,
                modifier = modifier
            )
            return
        }

        SetupScreen(
            status = status,
            onRequestNotifications = ::requestNotificationPermission,
            onOpenOverlaySettings = { startActivity(Permissions.overlaySettingsIntent(this)) },
            onOpenUsageSettings = { startActivity(Permissions.usageStatsSettingsIntent()) },
            modifier = modifier
        )
    }

    private fun chooseRole(chosen: Role) {
        roleStore.chooseRole(chosen)
        role = roleStore.role
        refreshStatus()
    }

    private fun onPaired(code: String) {
        roleStore.pairingId = code
        pairingId = code
        refreshStatus()
    }

    /**
     * Permissions are granted in system Settings, so the result arrives as a
     * resume rather than a callback. OEM power management also revokes them
     * silently, which is why this runs on every launch.
     *
     * The two roles start different services: a guardian phone never runs
     * detection or the overlay, and a protected phone never opens an alert
     * listener. Starting everything everywhere would put a needless foreground
     * service — and its permanent notification — on both devices.
     */
    private fun refreshStatus() {
        status = SetupStatus(
            notifications = Permissions.hasNotifications(this),
            overlay = Permissions.hasOverlay(this),
            usageStats = Permissions.hasUsageStats(this)
        )

        when (role) {
            Role.PROTECTED -> {
                startForegroundService(Intent(this, DetectionService::class.java))

                // Restore blocking after a reboot or a process kill: if an app is
                // still flagged and we are allowed to block, resume covering it.
                if (Permissions.canBlock(this) &&
                    FlaggedAppStore(this).flaggedPackages().isNotEmpty()
                ) {
                    OverlayService.start(this)
                }
            }

            Role.GUARDIAN -> if (pairingId != null) GuardianAlertService.start(this)

            null -> Unit
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            !Permissions.hasNotifications(this)
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    companion object {
        /** Set by [GuardianAlertService] so a tapped notification opens its alert. */
        const val EXTRA_ALERT_ID = "extra_alert_id"
    }
}
