package com.ronda.app

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ronda.app.alert.GuardianAlertService
import com.ronda.app.detection.DetectionService
import com.ronda.app.detection.FlaggedAppStore
import com.ronda.app.detection.PendingUninstall
import com.ronda.app.detection.PendingUninstallStore
import com.ronda.app.overlay.OverlayService
import com.ronda.app.pairing.QrCodeUtils
import com.ronda.app.pairing.Role
import com.ronda.app.pairing.RoleStore
import com.ronda.app.ui.components.BackTopBar
import com.ronda.app.ui.components.SkeletonCard
import com.ronda.app.ui.components.screenInsets
import com.ronda.app.ui.guardian.AlertDetailScreen
import com.ronda.app.ui.guardian.FakeGuardianRepository
import com.ronda.app.ui.guardian.FirebaseGuardianRepository
import com.ronda.app.ui.guardian.GuardianHomeScreen
import com.ronda.app.ui.guardian.GuardianPairingScreen
import com.ronda.app.ui.guardian.GuardianTab
import com.ronda.app.ui.guardian.GuardianViewModel
import com.ronda.app.ui.onboarding.IntroScreen
import com.ronda.app.ui.onboarding.LanguageScreen
import com.ronda.app.ui.onboarding.RoleSelectionScreen
import com.ronda.app.ui.protectedrole.ProtectedHomeScreen
import com.ronda.app.ui.protectedrole.ProtectedPairingScreen
import com.ronda.app.ui.protectedrole.UninstallPromptScreen
import com.ronda.app.ui.setup.SetupStatus
import com.ronda.app.ui.setup.SetupWizardScreen
import com.ronda.app.ui.theme.RONDATheme
import com.ronda.app.ui.theme.RondaTheme

/**
 * The whole app is one Activity. Which screen it shows is a function of a
 * handful of stored values — language, intro, role, pairing, permissions — so
 * there is no navigation graph to keep in sync with them.
 *
 * AppCompatActivity rather than ComponentActivity for two settings that live in
 * AppCompatDelegate: per-app language and the light/dark choice.
 */
class MainActivity : AppCompatActivity() {

    private val roleStore by lazy { RoleStore(this) }
    private val settings by lazy { SettingsStore(this) }

    private var status by mutableStateOf(SetupStatus(false, false, false, false))
    private var role by mutableStateOf<Role?>(null)
    private var pairingId by mutableStateOf<String?>(null)
    private var protectedName by mutableStateOf("")
    private var guardianName by mutableStateOf("")
    private var languageChosen by mutableStateOf(false)
    private var introSeen by mutableStateOf(false)
    private var themeMode by mutableStateOf(ThemeMode.SYSTEM)

    /** Guardian: which app's detail screen is open, addressed by package. */
    private var selectedPackage by mutableStateOf<String?>(null)
    private var guardianTab by mutableStateOf(GuardianTab.ALERTS)

    /**
     * Protected: a code scanned from the guardian's QR, waiting to be offered.
     *
     * Held here rather than applied straight away because the scan can land
     * before the pairing screen is reachable — a protected phone still on the
     * language or role screen, say. The pairing screen reads it whenever it
     * does open; on a guardian phone, or one already paired, that screen never
     * shows and the code is simply never used.
     */
    private var scannedPairingCode by mutableStateOf<String?>(null)

    /** Protected: the uninstall request to show, and whether it was deferred. */
    private var pendingUninstall by mutableStateOf<PendingUninstall?>(null)
    private var uninstallDeferred by mutableStateOf(false)

    /** Protected: the wizard shows itself while something is off, until Back. */
    private var setupDismissed by mutableStateOf(false)

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { refreshStatus() }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        role = roleStore.role
        pairingId = roleStore.pairingId
        protectedName = roleStore.protectedName
        guardianName = roleStore.guardianName
        languageChosen = settings.languageChosen
        introSeen = settings.introSeen
        themeMode = settings.themeMode
        selectedPackage = intent.getStringExtra(EXTRA_PACKAGE)
        // Cold start from a scanned QR: RONDA was closed and the link opened it.
        scannedPairingCode = QrCodeUtils.codeFromLink(intent.data?.toString())

        // Only on a genuinely new launch, so a rotation does not re-prompt. On a
        // protected phone the wizard asks instead, one permission at a time.
        if (savedInstanceState == null && role == Role.GUARDIAN) requestNotificationPermission()

        setContent {
            RONDATheme {
                AppRoot()
            }
        }
    }

    /**
     * RONDA is already open and something points it somewhere: the guardian
     * taps an alert notification, or the protected phone scans the pairing QR.
     * Reached for both because the Activity is singleTask.
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        intent.getStringExtra(EXTRA_PACKAGE)?.let { selectedPackage = it }
        QrCodeUtils.codeFromLink(intent.data?.toString())?.let { scannedPairingCode = it }
    }

    override fun onResume() {
        super.onResume()
        refreshStatus()
    }

    private enum class Screen {
        LANGUAGE, INTRO, ROLE,
        GUARDIAN_PAIRING, GUARDIAN_HOME, ALERT_DETAIL,
        PROTECTED_PAIRING, UNINSTALL_PROMPT, PROTECTED_SETUP, PROTECTED_HOME
    }

    private fun currentScreen(): Screen = when {
        !languageChosen -> Screen.LANGUAGE
        !introSeen -> Screen.INTRO
        role == null -> Screen.ROLE
        role == Role.GUARDIAN -> when {
            pairingId == null -> Screen.GUARDIAN_PAIRING
            selectedPackage != null -> Screen.ALERT_DETAIL
            else -> Screen.GUARDIAN_HOME
        }

        else -> when {
            pairingId == null -> Screen.PROTECTED_PAIRING
            pendingUninstall != null && !uninstallDeferred -> Screen.UNINSTALL_PROMPT
            !status.isFullyProtected && !setupDismissed -> Screen.PROTECTED_SETUP
            else -> Screen.PROTECTED_HOME
        }
    }

    @Composable
    private fun AppRoot() {
        val colors = RondaTheme.colors

        if (role == Role.PROTECTED && pairingId != null) {
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
        }

        Box(
            Modifier
                .fillMaxSize()
                .background(colors.bg)
        ) {
            // Fade-through between screens: the outgoing one is gone before the
            // incoming one lands, so two full screens never overlap mid-motion.
            AnimatedContent(
                targetState = currentScreen(),
                transitionSpec = {
                    fadeIn(tween(220, delayMillis = 70)) togetherWith fadeOut(tween(90))
                },
                label = "screen"
            ) { screen ->
                when (screen) {
                    Screen.LANGUAGE -> LanguageScreen(onChosen = ::chooseLanguage)
                    Screen.INTRO -> IntroScreen(onStart = ::finishIntro)
                    Screen.ROLE -> RoleSelectionScreen(onRoleChosen = ::chooseRole)

                    Screen.GUARDIAN_PAIRING -> GuardianPairingScreen(
                        guardianDeviceId = roleStore.deviceId,
                        initialGuardianName = guardianName,
                        initialNickname = protectedName,
                        onIdentityChosen = { name, nickname ->
                            roleStore.guardianName = name
                            guardianName = name
                            rename(nickname)
                        },
                        onPaired = { code -> onPaired(code, guardianName) }
                    )

                    Screen.GUARDIAN_HOME -> pairingId?.let { pairing ->
                        val viewModel = guardianViewModel(pairing)
                        val state by viewModel.state.collectAsState()
                        GuardianHomeScreen(
                            state = state,
                            protectedName = protectedName,
                            pairingCode = pairing,
                            demo = pairing == DEMO_PAIRING,
                            tab = guardianTab,
                            onTabChange = { guardianTab = it },
                            onVerdictClick = { selectedPackage = it.packageName },
                            themeMode = themeMode,
                            onThemeChange = ::changeTheme,
                            language = AppLanguage.current(),
                            onLanguageChange = { settings.setLanguage(it) },
                            onRename = ::rename,
                            onDisconnect = ::disconnect
                        )
                    }

                    Screen.ALERT_DETAIL -> {
                        val pairing = pairingId
                        val packageName = selectedPackage
                        if (pairing != null && packageName != null) {
                            val viewModel = guardianViewModel(pairing)
                            val state by viewModel.state.collectAsState()
                            val selected = viewModel.find(packageName)
                            if (selected != null) {
                                AlertDetailScreen(
                                    verdict = selected,
                                    protectedName = protectedName,
                                    undoable = state.undoable == selected.packageName,
                                    onMarkUnsafe = { viewModel.markUnsafe(selected.packageName) },
                                    onMarkSafe = { viewModel.markSafe(selected.packageName) },
                                    onUndo = { viewModel.undoMarkSafe(selected.packageName) },
                                    onRequestUninstall = { viewModel.requestUninstall(selected.packageName) },
                                    onBack = { selectedPackage = null }
                                )
                            } else {
                                // Opened from a notification before the list arrived.
                                LoadingDetail(onBack = { selectedPackage = null })
                            }
                        }
                    }

                    Screen.PROTECTED_PAIRING -> ProtectedPairingScreen(
                        protectedDeviceId = roleStore.deviceId,
                        scannedCode = scannedPairingCode,
                        onPaired = ::onPaired
                    )

                    Screen.UNINSTALL_PROMPT -> pendingUninstall?.let { request ->
                        // A guardian request takes over the screen. The app
                        // underneath stays blocked whatever happens here, so
                        // deferring costs nothing.
                        UninstallPromptScreen(
                            appLabel = appLabelOf(request.packageName),
                            onConfirm = { startUninstall(request.packageName) },
                            onLater = { uninstallDeferred = true }
                        )
                    }

                    Screen.PROTECTED_SETUP -> SetupWizardScreen(
                        status = status,
                        onRequestNotifications = ::requestNotificationPermission,
                        onOpenOverlaySettings = { startActivity(Permissions.overlaySettingsIntent(this@MainActivity)) },
                        onOpenUsageSettings = { startActivity(Permissions.usageStatsSettingsIntent()) },
                        onOpenBatterySettings = { startActivity(Permissions.batteryOptimizationIntent(this@MainActivity)) },
                        onBack = { setupDismissed = true }
                    )

                    Screen.PROTECTED_HOME -> ProtectedHomeScreen(
                        status = status,
                        pairingCode = pairingId.orEmpty(),
                        guardianName = guardianName,
                        onContinueSetup = { setupDismissed = false }
                    )
                }
            }
        }
    }

    @Composable
    private fun LoadingDetail(onBack: () -> Unit) {
        Column(
            Modifier
                .fillMaxSize()
                .screenInsets()
        ) {
            BackTopBar(onBack = onBack, title = stringResource(R.string.detail_title))
            SkeletonCard(Modifier.padding(horizontal = 20.dp, vertical = 8.dp))
        }
    }

    /**
     * One view model per pairing, shared by the home and the detail screen. It
     * is keyed on the pairing so unpairing and re-pairing starts clean.
     */
    @Composable
    private fun guardianViewModel(pairing: String): GuardianViewModel = viewModel(
        key = pairing,
        factory = viewModelFactory {
            initializer {
                GuardianViewModel(
                    if (pairing == DEMO_PAIRING) {
                        FakeGuardianRepository(protectedName)
                    } else {
                        FirebaseGuardianRepository(pairing, protectedName)
                    }
                )
            }
        }
    )

    private fun chooseLanguage(language: AppLanguage) {
        languageChosen = true
        // May recreate the Activity on API 30–32; the flag above is already on disk.
        settings.setLanguage(language)
    }

    private fun finishIntro() {
        settings.introSeen = true
        introSeen = true
    }

    private fun chooseRole(chosen: Role) {
        roleStore.chooseRole(chosen)
        role = roleStore.role
        if (role == Role.GUARDIAN) requestNotificationPermission()
        refreshStatus()
    }

    private fun rename(name: String) {
        roleStore.protectedName = name
        protectedName = name
    }

    private fun changeTheme(mode: ThemeMode) {
        themeMode = mode
        // Applies through AppCompatDelegate, which recreates the Activity.
        settings.themeMode = mode
    }

    /**
     * Both sides land here. The guardian's name travels in the pairing record:
     * the guardian phone already knows it, the protected phone learns it from
     * the claim and keeps it so the home screen can say who is guarding.
     */
    private fun onPaired(code: String, pairedGuardianName: String) {
        roleStore.pairingId = code
        roleStore.guardianName = pairedGuardianName
        pairingId = code
        guardianName = pairedGuardianName
        setupDismissed = false
        refreshStatus()
    }

    /**
     * Guardian side. Local only: the database rules never let a client write
     * `revoked`, so the pairing node stays as it is and this phone simply stops
     * listening. A new code can be made straight away.
     */
    private fun disconnect() {
        stopService(Intent(this, GuardianAlertService::class.java))
        roleStore.unpair()
        pairingId = null
        selectedPackage = null
        guardianTab = GuardianTab.ALERTS
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
