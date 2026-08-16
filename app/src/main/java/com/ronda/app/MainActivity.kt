package com.ronda.app

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ronda.app.alert.GuardianAlertService
import com.ronda.app.detection.DetectionService
import com.ronda.app.detection.FlaggedAppStore
import com.ronda.app.detection.PendingUninstall
import com.ronda.app.detection.PendingUninstallStore
import com.ronda.app.overlay.OverlayService
import com.ronda.app.pairing.Role
import com.ronda.app.pairing.RoleStore
import com.ronda.app.ui.guardian.AlertDetailScreen
import com.ronda.app.ui.guardian.FakeGuardianRepository
import com.ronda.app.ui.guardian.FirebaseGuardianRepository
import com.ronda.app.ui.guardian.GuardianPairingScreen
import com.ronda.app.ui.guardian.GuardianViewModel
import com.ronda.app.ui.guardian.WatchListScreen
import com.ronda.app.ui.onboarding.RoleSelectionScreen
import com.ronda.app.ui.protectedrole.ProtectedPairingScreen
import com.ronda.app.ui.protectedrole.UninstallPromptScreen
import com.ronda.app.ui.setup.SetupScreen
import com.ronda.app.ui.setup.SetupStatus
import com.ronda.app.ui.theme.RONDATheme
import kotlinx.coroutines.launch

/**
 * The whole app is one Activity. Which screen it shows is a function of two
 * stored values — the role and whether this device is paired — so there is no
 * navigation graph to keep in sync with them.
 */
class MainActivity : ComponentActivity() {

    private val roleStore by lazy { RoleStore(this) }

    private var status by mutableStateOf(SetupStatus(false, false, false, false))
    private var role by mutableStateOf<Role?>(null)
    private var pairingId by mutableStateOf<String?>(null)

    /** Guardian: which app's detail screen is open, addressed by package. */
    private var selectedPackage by mutableStateOf<String?>(null)

    /** Protected: the uninstall request to show, and whether it was deferred. */
    private var pendingUninstall by mutableStateOf<PendingUninstall?>(null)
    private var uninstallDeferred by mutableStateOf(false)

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { refreshStatus() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        role = roleStore.role
        pairingId = roleStore.pairingId
        selectedPackage = intent.getStringExtra(EXTRA_PACKAGE)

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
        intent.getStringExtra(EXTRA_PACKAGE)?.let { selectedPackage = it }
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

        val viewModel: GuardianViewModel = viewModel(
            key = currentPairing,
            factory = viewModelFactory {
                initializer {
                    GuardianViewModel(
                        if (currentPairing == DEMO_PAIRING) {
                            FakeGuardianRepository(roleStore.protectedName)
                        } else {
                            FirebaseGuardianRepository(currentPairing, roleStore.protectedName)
                        }
                    )
                }
            }
        )
        val state by viewModel.state.collectAsState()

        val selected = viewModel.find(selectedPackage)
        if (selected != null) {
            AlertDetailScreen(
                verdict = selected,
                protectedName = state.protectedName,
                undoable = state.undoable == selected.packageName,
                onMarkUnsafe = { viewModel.markUnsafe(selected.packageName) },
                onMarkSafe = { viewModel.markSafe(selected.packageName) },
                onUndo = { viewModel.undoMarkSafe(selected.packageName) },
                onRequestUninstall = { viewModel.requestUninstall(selected.packageName) },
                onBack = { selectedPackage = null },
                modifier = modifier
            )
        } else {
            WatchListScreen(
                state = state,
                onVerdictClick = { selectedPackage = it.packageName },
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

        // Live, not polled on resume: the request is written by the service
        // while this screen may already be visible.
        LaunchedEffect(Unit) {
            PendingUninstallStore(this@MainActivity).observe().collect { request ->
                // A new request must not inherit the previous one's dismissal.
                if (request?.packageName != pendingUninstall?.packageName) {
                    uninstallDeferred = false
                }
                pendingUninstall = request
            }
        }

        // A guardian request takes over the screen. The app underneath stays
        // blocked whatever happens here, so deferring costs nothing.
        val request = pendingUninstall
        if (request != null && !uninstallDeferred) {
            UninstallPromptScreen(
                appLabel = appLabelOf(request.packageName),
                onConfirm = { startUninstall(request.packageName) },
                onLater = { uninstallDeferred = true },
                modifier = modifier
            )
            return
        }

        SetupScreen(
            status = status,
            onRequestNotifications = ::requestNotificationPermission,
            onOpenOverlaySettings = { startActivity(Permissions.overlaySettingsIntent(this)) },
            onOpenUsageSettings = { startActivity(Permissions.usageStatsSettingsIntent()) },
            onOpenBatterySettings = { startActivity(Permissions.batteryOptimizationIntent(this)) },
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
     * Protected side. Android has no API to remove another app silently, by
     * design — this opens the system dialog and the user has the final say.
     * The pending request is cleared by the removal broadcast, not here, so a
     * cancelled dialog leaves the prompt in place.
     */
    private fun startUninstall(packageName: String) {
        val intent = Intent(Intent.ACTION_DELETE, Uri.parse("package:$packageName"))
        runCatching { startActivity(intent) }
            .onFailure { Log.e(TAG, "Could not open uninstall dialog for $packageName", it) }
    }

    private fun appLabelOf(packageName: String): String = runCatching {
        packageManager.getApplicationLabel(
            packageManager.getApplicationInfo(packageName, 0)
        ).toString()
    }.getOrDefault(packageName)

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
            usageStats = Permissions.hasUsageStats(this),
            batteryExemption = Permissions.hasBatteryExemption(this)
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
        private const val TAG = "MainActivity"

        /** Set by [GuardianAlertService] so a tapped notification opens its alert. */
        const val EXTRA_PACKAGE = "extra_package"

        /**
         * Pair with this code to serve the guardian UI from
         * [FakeGuardianRepository] instead of Firebase — the offline path for
         * recording the demo without two emulators and RTDB latency.
         *
         * Deliberately a pairing id rather than a build flag. A `const val
         * DEMO_MODE` has to be flipped, rebuilt and reinstalled, and a build
         * left in the wrong state shows five fake apps on a real guardian phone
         * with no sign anything is wrong. Keying it to a pairing nobody types by
         * accident makes that failure impossible.
         */
        const val DEMO_PAIRING = "DEMO01"
    }
}
