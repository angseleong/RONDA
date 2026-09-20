package com.ronda.app

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.BackHandler
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ronda.app.ui.protectedrole.ProtectedTab
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.NotificationCompat
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
import com.ronda.app.ui.guardian.PairingInfo
import com.ronda.app.ui.onboarding.IntroScreen
import com.ronda.app.ui.onboarding.LanguageScreen
import com.ronda.app.ui.onboarding.RoleSelectionScreen
import com.ronda.app.ui.protectedrole.InitialScanScreen
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
    private var pairingIds by mutableStateOf<Set<String>>(emptySet())
    private var addingDevice by mutableStateOf(false)
    private var protectedName by mutableStateOf("")
    private var guardianName by mutableStateOf("")
    private var languageChosen by mutableStateOf(false)
    private var introSeen by mutableStateOf(false)
    private var themeMode by mutableStateOf(ThemeMode.SYSTEM)
    private var initialScanDone by mutableStateOf(false)

    /** Guardian: which app's detail screen is open, addressed by package. */
    private var selectedPackage by mutableStateOf<String?>(null)
    private var selectedPairingId by mutableStateOf<String?>(null)
    private var guardianTab by mutableStateOf(GuardianTab.ALERTS)
    private var protectedTab by mutableStateOf(ProtectedTab.ALERTS)

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

        savedInstanceState?.getString("guardian_tab")?.let {
            runCatching { guardianTab = GuardianTab.valueOf(it) }
        }
        savedInstanceState?.getString("protected_tab")?.let {
            runCatching { protectedTab = ProtectedTab.valueOf(it) }
        }

        if (intent.getBooleanExtra(EXTRA_DISCONNECTED, false)) {
            roleStore.unpair()
            pairingId = null
            pairingIds = emptySet()
        } else {
            pairingId = roleStore.pairingId
            pairingIds = roleStore.pairingIds.toSet()
        }
        role = roleStore.role
        protectedName = roleStore.protectedName
        guardianName = roleStore.guardianName
        languageChosen = settings.languageChosen
        introSeen = settings.introSeen
        themeMode = settings.themeMode
        initialScanDone = settings.initialScanDone
        selectedPackage = intent.getStringExtra(EXTRA_PACKAGE)
        selectedPairingId = intent.getStringExtra(EXTRA_PAIRING_ID)

        // Only on a genuinely new launch, so a rotation does not re-prompt. On a
        // protected phone the wizard asks instead, one permission at a time.
        if (savedInstanceState == null && role == Role.GUARDIAN) requestNotificationPermission()

        setContent {
            RONDATheme {
                AppRoot()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("guardian_tab", guardianTab.name)
        outState.putString("protected_tab", protectedTab.name)
    }

    /** The guardian taps an alert notification while RONDA is already open. */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent.getBooleanExtra(EXTRA_DISCONNECTED, false)) {
            roleStore.unpair()
            pairingId = null
            pairingIds = emptySet()
        } else {
            pairingId = roleStore.pairingId
            pairingIds = roleStore.pairingIds.toSet()
        }
        role = roleStore.role
        protectedName = roleStore.protectedName
        guardianName = roleStore.guardianName
        selectedPackage = intent.getStringExtra(EXTRA_PACKAGE)
        selectedPairingId = intent.getStringExtra(EXTRA_PAIRING_ID)
    }

    override fun onResume() {
        super.onResume()
        if (intent.getBooleanExtra(EXTRA_DISCONNECTED, false)) {
            roleStore.unpair()
            pairingId = null
            pairingIds = emptySet()
        } else {
            pairingId = roleStore.pairingId
            pairingIds = roleStore.pairingIds.toSet()
        }
        role = roleStore.role
        protectedName = roleStore.protectedName
        guardianName = roleStore.guardianName
        refreshStatus()
    }

    private enum class Screen {
        LANGUAGE, INTRO, ROLE,
        GUARDIAN_PAIRING, GUARDIAN_HOME, ALERT_DETAIL,
        PROTECTED_PAIRING, INITIAL_SCAN, UNINSTALL_PROMPT, PROTECTED_SETUP, PROTECTED_HOME
    }

    private fun currentScreen(): Screen = when {
        !languageChosen -> Screen.LANGUAGE
        !introSeen -> Screen.INTRO
        role == null -> Screen.ROLE
        role == Role.GUARDIAN -> when {
            addingDevice || pairingIds.isEmpty() -> Screen.GUARDIAN_PAIRING
            selectedPackage != null -> Screen.ALERT_DETAIL
            else -> Screen.GUARDIAN_HOME
        }

        else -> when {
            pairingId == null -> Screen.PROTECTED_PAIRING
            !initialScanDone -> Screen.INITIAL_SCAN
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

        if (role == Role.PROTECTED && pairingId != null) {
            val currentId = pairingId!!
            LaunchedEffect(currentId) {
                com.ronda.app.alert.CommandRepository().observeCommands(currentId).collect { cmds ->
                    if (cmds.any { it.action == com.ronda.app.alert.Command.ACTION_DISCONNECT }) {
                        Log.d(TAG, "Pairing $currentId disconnected remotely by Rondor")
                        disconnectProtected(notify = true)
                    }
                }
            }
        }
        if (role == Role.GUARDIAN && pairingIds.isNotEmpty()) {
            LaunchedEffect(pairingIds) {
                val flows = pairingIds.map { id ->
                    com.ronda.app.alert.CommandRepository().observeCommands(id)
                        .map { cmds -> id to cmds.any { it.action == com.ronda.app.alert.Command.ACTION_DISCONNECT } }
                }
                combine(flows) { it.toList() }.collect { list ->
                    for ((id, hasDisconnect) in list) {
                        if (hasDisconnect) {
                            Log.d(TAG, "Pairing $id disconnected remotely by Rondee")
                            disconnectDevice(id, notify = true)
                        }
                    }
                }
            }
        }

        if (addingDevice) {
            BackHandler { addingDevice = false }
        }
        if (selectedPackage != null) {
            BackHandler {
                selectedPackage = null
                selectedPairingId = null
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
                        initialNickname = if (addingDevice) "" else protectedName,
                        onIdentityChosen = { name, nickname ->
                            roleStore.guardianName = name
                            guardianName = name
                            protectedName = nickname
                        },
                        onPaired = { code -> onPaired(code, guardianName) },
                        onCancel = if (addingDevice) ({ addingDevice = false }) else null
                    )

                    Screen.GUARDIAN_HOME -> {
                        if (pairingIds.isNotEmpty()) {
                            val viewModel = guardianViewModel(pairingIds)
                            val state by viewModel.state.collectAsState()
                            val devices = pairingIds.map { id ->
                                PairingInfo(id, roleStore.getProtectedName(id))
                            }
                            GuardianHomeScreen(
                                state = state,
                                devices = devices,
                                demo = pairingIds.contains(DEMO_PAIRING),
                                tab = guardianTab,
                                onTabChange = { guardianTab = it },
                                onVerdictClick = {
                                    selectedPairingId = it.pairingId
                                    selectedPackage = it.packageName
                                },
                                themeMode = themeMode,
                                onThemeChange = ::changeTheme,
                                language = AppLanguage.current(),
                                onLanguageChange = { settings.setLanguage(it) },
                                onRename = ::renameDevice,
                                onDisconnect = ::disconnectDevice,
                                onAddDevice = ::addDevice,
                                onScan = { id -> viewModel.requestScan(id) }
                            )
                        }
                    }

                    Screen.ALERT_DETAIL -> {
                        val viewModel = guardianViewModel(pairingIds)
                        val state by viewModel.state.collectAsState()
                        
                        val verdict = viewModel.find(selectedPairingId, selectedPackage)
                        if (verdict != null) {
                            AlertDetailScreen(
                                verdict = verdict,
                                protectedName = roleStore.getProtectedName(verdict.pairingId),
                                undoable = state.undoable == verdict.packageName,
                                onMarkSafe = { viewModel.markSafe(verdict.pairingId, verdict.packageName) },
                                onMarkUnsafe = { viewModel.markUnsafe(verdict.pairingId, verdict.packageName) },
                                onUndo = { viewModel.undoMarkSafe(verdict.pairingId, verdict.packageName) },
                                onRequestUninstall = { viewModel.requestUninstall(verdict.pairingId, verdict.packageName) },
                                onBack = {
                                    selectedPairingId = null
                                    selectedPackage = null
                                }
                            )
                        } else {
                            selectedPairingId = null
                            selectedPackage = null
                        }
                    }

                    Screen.PROTECTED_PAIRING -> ProtectedPairingScreen(
                        protectedDeviceId = roleStore.deviceId,
                        onPaired = ::onPaired
                    )

                    Screen.INITIAL_SCAN -> InitialScanScreen(
                        onScan = {
                            val serviceIntent = Intent(this@MainActivity, DetectionService::class.java)
                            serviceIntent.action = ACTION_SCAN_EXISTING
                            startForegroundService(serviceIntent)
                        },
                        onComplete = {
                            settings.initialScanDone = true
                            initialScanDone = true
                        }
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

                    Screen.PROTECTED_HOME -> {
                        val flaggedStore = remember { FlaggedAppStore(this@MainActivity) }
                        var flaggedPackages by remember { mutableStateOf(flaggedStore.flaggedPackages()) }
                        val historyStore = remember { com.ronda.app.detection.ProtectedHistoryStore(this@MainActivity) }
                        var historyItems by remember { mutableStateOf(historyStore.history()) }

                        LaunchedEffect(protectedTab) {
                            flaggedPackages = flaggedStore.flaggedPackages()
                            historyItems = historyStore.history()
                        }

                        ProtectedHomeScreen(
                            status = status,
                            pairingCode = pairingId.orEmpty(),
                            guardianName = guardianName,
                            tab = protectedTab,
                            onTabChange = { protectedTab = it },
                            flaggedPackages = flaggedPackages,
                            historyItems = historyItems,
                            themeMode = themeMode,
                            onThemeChange = ::changeTheme,
                            language = AppLanguage.current(),
                            onLanguageChange = { settings.setLanguage(it) },
                            onContinueSetup = { setupDismissed = false },
                            onManualScan = {
                                val serviceIntent = Intent(this@MainActivity, DetectionService::class.java)
                                serviceIntent.action = ACTION_SCAN_EXISTING
                                startForegroundService(serviceIntent)
                            },
                            onUninstall = ::startUninstall,
                            onDisconnect = ::disconnectProtected
                        )
                    }
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
    private fun guardianViewModel(pairingIds: Set<String>): GuardianViewModel = viewModel(
        key = pairingIds.joinToString(),
        factory = viewModelFactory {
            initializer {
                GuardianViewModel(
                    if (pairingIds.contains(DEMO_PAIRING)) {
                        FakeGuardianRepository(protectedName)
                    } else {
                        FirebaseGuardianRepository(pairingIds, protectedName)
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

    private fun renameDevice(id: String, name: String) {
        roleStore.setProtectedName(id, name)
        if (id == roleStore.pairingId) {
            roleStore.protectedName = name
            protectedName = name
        }
        // New set instance so the settings list re-reads nicknames.
        pairingIds = roleStore.pairingIds.toSet()
        protectedName = protectedName
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
        if (role == Role.GUARDIAN) {
            roleStore.addPairing(code, protectedName)
            pairingIds = roleStore.pairingIds.toSet()
            addingDevice = false
        } else {
            roleStore.pairingId = code
        }
        roleStore.guardianName = pairedGuardianName
        pairingId = roleStore.pairingId
        guardianName = pairedGuardianName
        setupDismissed = false
        refreshStatus()
        if (role == Role.GUARDIAN) restartGuardianWatch()
    }

    /**
     * Guardian side: disconnects a protected device by removing the pairing
     * and sending ACTION_DISCONNECT to the commands channel.
     */
    private fun disconnectDevice(id: String, notify: Boolean = false) {
        val rondeeName = roleStore.getProtectedName(id)
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            runCatching {
                com.ronda.app.alert.CommandRepository().send(
                    id,
                    alertId = "",
                    action = com.ronda.app.alert.Command.ACTION_DISCONNECT,
                    packageName = ""
                )
            }
        }

        roleStore.removePairing(id)
        pairingIds = roleStore.pairingIds.toSet()
        pairingId = roleStore.pairingId
        if (selectedPairingId == id) {
            selectedPackage = null
            selectedPairingId = null
        }
        if (pairingIds.isEmpty()) {
            stopService(Intent(this, GuardianAlertService::class.java))
            guardianTab = GuardianTab.ALERTS
            status = SetupStatus(false, false, false, false)
        } else {
            restartGuardianWatch()
        }
        if (notify) {
            notifyRondeeDisconnected(rondeeName)
        }
    }

    private fun disconnectProtected(notify: Boolean = false) {
        val currentPairingId = pairingId
        if (currentPairingId != null) {
            val scope = CoroutineScope(Dispatchers.IO)
            scope.launch {
                runCatching {
                    com.ronda.app.alert.CommandRepository().send(
                        currentPairingId,
                        alertId = "",
                        action = com.ronda.app.alert.Command.ACTION_DISCONNECT,
                        packageName = ""
                    )
                }
            }
        }

        roleStore.unpair()
        pairingId = null
        pairingIds = emptySet()
        stopService(Intent(this, DetectionService::class.java))
        OverlayService.stop(this)
        protectedTab = ProtectedTab.ALERTS
        refreshStatus()
        if (notify) {
            notifyProtectedDisconnected()
        }
    }

    private fun addDevice() {
        addingDevice = true
    }

    private fun restartGuardianWatch() {
        stopService(Intent(this, GuardianAlertService::class.java))
        if (roleStore.pairingIds.isNotEmpty()) GuardianAlertService.start(this)
    }

    /**
     * Shown on the Rondor's phone when the Rondee side has revoked the pairing.
     * Uses the localized context so the user sees the notification in the language
     * they chose inside the app, not the system default.
     */
    private fun notifyRondeeDisconnected(rondeeName: String) {
        val loc = localized()
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "ronda_rondee_disconnected_channel"
        manager.createNotificationChannel(
            NotificationChannel(
                channelId,
                loc.getString(R.string.rondee_disconnected_notification_title),
                NotificationManager.IMPORTANCE_HIGH
            )
        )
        val name = rondeeName.ifBlank { loc.getString(R.string.guardian_unknown_name) }
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_log_out)
            .setContentTitle(loc.getString(R.string.rondee_disconnected_notification_title))
            .setContentText(loc.getString(R.string.rondee_disconnected_notification_body, name))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        manager.notify(NOTIF_ID_RONDEE_DISCONNECTED, notification)
    }

    private fun notifyProtectedDisconnected() {
        val loc = localized()
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "ronda_disconnect_channel"
        manager.createNotificationChannel(
            NotificationChannel(
                channelId,
                loc.getString(R.string.disconnect_notification_title),
                NotificationManager.IMPORTANCE_HIGH
            )
        )
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_log_out)
            .setContentTitle(loc.getString(R.string.disconnect_notification_title))
            .setContentText(loc.getString(R.string.disconnect_notification_body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        manager.notify(9999, notification)
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

            Role.GUARDIAN -> if (roleStore.pairingIds.isNotEmpty()) GuardianAlertService.start(this)

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
        const val EXTRA_PACKAGE = "package"
        /** Carries the pairing ID that issued the alert when launched from a notification. */
        const val EXTRA_PAIRING_ID = "pairing_id"

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
        /** Key for the action to start an existing apps scan manually. */
        const val ACTION_SCAN_EXISTING = "com.ronda.app.action.SCAN_EXISTING"
        /** Notification ID for the "Rondee disconnected" alert shown on the Rondor's phone. */
        private const val NOTIF_ID_RONDEE_DISCONNECTED = 8001
        /** Key indicating that the pairing was disconnected remotely. */
        const val EXTRA_DISCONNECTED = "disconnected"
    }
}
