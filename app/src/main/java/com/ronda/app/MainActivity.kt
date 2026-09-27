package com.ronda.app

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ronda.app.ui.protectedrole.ProtectedTab
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ronda.app.alert.GuardianAlertService
import com.ronda.app.detection.DetectionService
import com.ronda.app.detection.FlaggedAppStore
import com.ronda.app.detection.PendingUninstall
import com.ronda.app.detection.PendingUninstallStore
import com.ronda.app.detection.ScanEvents
import com.ronda.app.overlay.OverlayService
import com.ronda.app.pairing.QrCodeUtils
import com.ronda.app.pairing.Role
import com.ronda.app.pairing.RoleStore
import com.ronda.app.ui.components.BackTopBar
import com.ronda.app.ui.components.RondaIcons
import com.ronda.app.ui.components.SkeletonCard
import com.ronda.app.ui.components.ToastHost
import com.ronda.app.ui.components.ToastState
import com.ronda.app.ui.components.screenInsets
import com.ronda.app.ui.guardian.AlertDetailScreen
import com.ronda.app.ui.guardian.FakeGuardianRepository
import com.ronda.app.ui.guardian.FirebaseGuardianRepository
import com.ronda.app.ui.guardian.GuardianEvent
import com.ronda.app.ui.guardian.GuardianHomeScreen
import com.ronda.app.ui.guardian.GuardianPairingScreen
import com.ronda.app.ui.guardian.GuardianTab
import com.ronda.app.ui.guardian.GuardianViewModel
import com.ronda.app.ui.guardian.PairingInfo
import com.ronda.app.ui.onboarding.IntroScreen
import com.ronda.app.ui.onboarding.LanguageScreen
import com.ronda.app.ui.onboarding.RoleSelectionScreen
import com.ronda.app.ui.onboarding.SplashIntro
import com.ronda.app.ui.protectedrole.InitialScanScreen
import com.ronda.app.ui.protectedrole.ProtectedHomeScreen
import com.ronda.app.ui.protectedrole.ProtectedPairingScreen
import com.ronda.app.ui.protectedrole.UninstallPromptScreen
import com.ronda.app.ui.setup.SetupStatus
import com.ronda.app.ui.setup.SetupWizardScreen
import com.ronda.app.ui.theme.RONDATheme
import com.ronda.app.ui.theme.RondaTheme
import com.ronda.app.ui.theme.Tone

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
    /** Set from a history row, where one app can have a ruling per install. */
    private var selectedAlertId by mutableStateOf<String?>(null)
    private var guardianTab by mutableStateOf(GuardianTab.ALERTS)
    private var protectedTab by mutableStateOf(ProtectedTab.ALERTS)

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

    private var introPlaying by mutableStateOf(false)

    /** Activity-wide, so a toast survives the screen change it announces. */
    private val toasts = ToastState()

    private fun toast(tone: Tone, icon: Int, @StringRes text: Int, vararg args: Any) {
        toasts.show(getString(text, *args), tone, icon)
    }

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

        syncPairingFromStore()
        languageChosen = settings.languageChosen
        introSeen = settings.introSeen
        themeMode = settings.themeMode
        initialScanDone = settings.initialScanDone
        selectedPackage = intent.getStringExtra(EXTRA_PACKAGE)
        selectedPairingId = intent.getStringExtra(EXTRA_PAIRING_ID)
        // Cold start from a scanned QR: RONDA was closed and the link opened it.
        scannedPairingCode = QrCodeUtils.codeFromLink(intent.data?.toString())

        // Only on a genuinely new launch, so a rotation does not re-prompt. On a
        // protected phone the wizard asks instead, one permission at a time.
        if (savedInstanceState == null && role == Role.GUARDIAN) requestNotificationPermission()

        // The intro plays on a fresh launch only: not on a rotation or a theme
        // or language switch, and not from an alert notification — a guardian
        // tapping one should land on the alert, not wait through a logo.
        introPlaying = savedInstanceState == null && intent.getStringExtra(EXTRA_PACKAGE) == null

        setContent {
            RONDATheme {
                Box(Modifier.fillMaxSize()) {
                    AppRoot()
                    if (introPlaying) SplashIntro(onFinished = { introPlaying = false })
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("guardian_tab", guardianTab.name)
        outState.putString("protected_tab", protectedTab.name)
    }

    /**
     * RONDA is already open and something points it somewhere: the guardian
     * taps an alert notification, or the protected phone scans the pairing QR.
     * Reached for both because the Activity is singleTask.
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        syncPairingFromStore()
        intent.getStringExtra(EXTRA_PACKAGE)?.let {
            selectedPackage = it
            selectedPairingId = intent.getStringExtra(EXTRA_PAIRING_ID)
            selectedAlertId = null
        }
        QrCodeUtils.codeFromLink(intent.data?.toString())?.let { scannedPairingCode = it }
    }

    override fun onResume() {
        super.onResume()
        syncPairingFromStore()
        refreshStatus()
    }

    /**
     * The background services can end a pairing while this screen is not
     * looking (CommandHandler on a Rondee, GuardianAlertService on a Rondor),
     * so the store is the truth and this state only mirrors it.
     */
    private fun syncPairingFromStore() {
        role = roleStore.role
        pairingId = roleStore.pairingId
        pairingIds = roleStore.pairingIds.toSet()
        protectedName = roleStore.protectedName
        guardianName = roleStore.guardianName
        // A disconnect seen in the background starts setup over, which
        // resets these too — see SettingsStore.startSetupOver.
        languageChosen = settings.languageChosen
        introSeen = settings.introSeen
        initialScanDone = settings.initialScanDone
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
                // CommandHandler may have got there first and already started
                // setup over; doing it again is harmless, and this is what
                // takes the open screen back to the start.
                com.ronda.app.alert.CommandRepository().observeCommands(currentId).collect { cmds ->
                    if (cmds.any { it.isDisconnectFrom(com.ronda.app.alert.Command.FROM_GUARDIAN) }) {
                        Log.d(TAG, "Pairing $currentId disconnected remotely by Rondor")
                        startOver()
                        toast(Tone.WARN, RondaIcons.logOut, R.string.toast_disconnected_by_guardian)
                    }
                }
            }
            ProtectedToasts()
        }
        if (role == Role.GUARDIAN && pairingIds.isNotEmpty()) {
            val viewModel = guardianViewModel(pairingIds)
            LaunchedEffect(viewModel) {
                viewModel.events.collect { event ->
                    val app = event.verdict.appLabel
                    when (event) {
                        is GuardianEvent.NeedsReview ->
                            toast(Tone.DANGER, RondaIcons.shieldAlert, R.string.toast_alert_review, app)
                        is GuardianEvent.Monitored ->
                            toast(Tone.TRUST, RondaIcons.eye, R.string.toast_alert_monitored, app)
                        is GuardianEvent.Removed ->
                            toast(Tone.SAFE, RondaIcons.check, R.string.toast_removed, app)
                    }
                }
            }
        }
        if (role == Role.GUARDIAN && pairingIds.isNotEmpty()) {
            LaunchedEffect(pairingIds) {
                val flows = pairingIds.map { id ->
                    com.ronda.app.alert.CommandRepository().observeCommands(id).map { cmds ->
                        id to cmds.any { it.isDisconnectFrom(com.ronda.app.alert.Command.FROM_PROTECTED) }
                    }
                }
                combine(flows) { it.toList() }.collect { list ->
                    for ((id, hasDisconnect) in list) {
                        if (hasDisconnect) {
                            Log.d(TAG, "Pairing $id disconnected remotely by Rondee")
                            onRondeeDisconnected(id)
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
                    // Every first-run screen can step back one, so a wrong tap
                    // never needs a data wipe. Each Back unsets the one stored
                    // value that moved the app forward.
                    Screen.INTRO -> IntroScreen(
                        onStart = ::finishIntro,
                        onBack = {
                            settings.languageChosen = false
                            languageChosen = false
                        }
                    )
                    Screen.ROLE -> RoleSelectionScreen(
                        onRoleChosen = ::chooseRole,
                        onBack = {
                            settings.introSeen = false
                            introSeen = false
                        }
                    )

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
                        onCancel = when {
                            addingDevice -> ({ addingDevice = false })
                            roleStore.canClearRole -> ::leaveRole
                            else -> null
                        },
                        adding = addingDevice
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
                                    selectedAlertId = it.alertId
                                    selectedPackage = it.packageName
                                },
                                themeMode = themeMode,
                                onThemeChange = ::changeTheme,
                                language = AppLanguage.current(),
                                onLanguageChange = { settings.setLanguage(it) },
                                onRename = ::renameDevice,
                                onDisconnect = ::disconnectDevice,
                                onAddDevice = ::addDevice,
                                onScan = { id ->
                                    viewModel.requestScan(id)
                                    toast(Tone.TRUST, RondaIcons.send, R.string.toast_scan_sent, roleStore.getProtectedName(id))
                                }
                            )
                        }
                    }

                    Screen.ALERT_DETAIL -> {
                        val viewModel = guardianViewModel(pairingIds)
                        val state by viewModel.state.collectAsState()
                        
                        val verdict = viewModel.find(selectedPairingId, selectedPackage, selectedAlertId)
                        if (verdict != null) {
                            AlertDetailScreen(
                                verdict = verdict,
                                protectedName = roleStore.getProtectedName(verdict.pairingId),
                                undoable = state.undoable == verdict.packageName,
                                onMarkSafe = {
                                    viewModel.markSafe(verdict.pairingId, verdict.packageName)
                                    toast(Tone.SAFE, RondaIcons.circleCheck, R.string.toast_marked_safe, verdict.appLabel)
                                },
                                onMarkUnsafe = {
                                    viewModel.markUnsafe(verdict.pairingId, verdict.packageName)
                                    toast(Tone.DANGER, RondaIcons.shieldAlert, R.string.toast_marked_unsafe, verdict.appLabel)
                                },
                                onUndo = {
                                    viewModel.undoMarkSafe(verdict.pairingId, verdict.packageName)
                                    toast(Tone.NEUTRAL, RondaIcons.undo, R.string.toast_undone)
                                },
                                onRequestUninstall = {
                                    viewModel.requestUninstall(verdict.pairingId, verdict.packageName)
                                    toast(
                                        Tone.TRUST, RondaIcons.send, R.string.toast_uninstall_sent,
                                        roleStore.getProtectedName(verdict.pairingId)
                                    )
                                },
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
                        scannedCode = scannedPairingCode,
                        onPaired = ::onPaired,
                        onBack = if (roleStore.canClearRole) ::leaveRole else null
                    )

                    Screen.INITIAL_SCAN -> InitialScanScreen(
                        onScan = {
                            val serviceIntent = Intent(this@MainActivity, DetectionService::class.java)
                            serviceIntent.action = ACTION_SCAN_EXISTING
                            // First scan of a pairing: report what is already
                            // flagged too, or a new Rondor never hears of it.
                            serviceIntent.putExtra(EXTRA_INITIAL_SCAN, true)
                            startForegroundService(serviceIntent)
                        },
                        onComplete = {
                            settings.initialScanDone = true
                            initialScanDone = true
                        },
                        // Paired with the wrong code: undo just the pairing,
                        // tell the Rondor, and land back on the code screen.
                        onBack = ::undoPairing
                    )

                    Screen.UNINSTALL_PROMPT -> pendingUninstall?.let { request ->
                        // A guardian request takes over the screen. The app
                        // underneath stays blocked whatever happens here, so
                        // deferring costs nothing.
                        UninstallPromptScreen(
                            appLabel = appLabelOf(request.packageName),
                            reason = remember(request.packageName) {
                                com.ronda.app.detection.flagReasons(this@MainActivity, request.packageName)
                                    .firstOrNull()
                            },
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

                        // Reload whenever InstallReceiver / DetectionService writes, not just on tab switch.
                        DisposableEffect(Unit) {
                            val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
                                flaggedPackages = flaggedStore.flaggedPackages()
                                historyItems = historyStore.history()
                            }
                            flaggedStore.prefs.registerOnSharedPreferenceChangeListener(listener)
                            historyStore.prefs.registerOnSharedPreferenceChangeListener(listener)
                            listener.onSharedPreferenceChanged(null, null) // catch writes made while off-screen
                            onDispose {
                                flaggedStore.prefs.unregisterOnSharedPreferenceChangeListener(listener)
                                historyStore.prefs.unregisterOnSharedPreferenceChangeListener(listener)
                            }
                        }

                        var detailPackage by rememberSaveable { mutableStateOf<String?>(null) }
                        val detailVerdict = detailPackage?.let { pkg ->
                            remember(pkg) { com.ronda.app.detection.flagVerdict(this@MainActivity, pkg) }
                        }
                        if (detailVerdict != null) {
                            AlertDetailScreen(
                                verdict = detailVerdict,
                                protectedName = stringResource(R.string.detail_self_name),
                                undoable = false,
                                onMarkUnsafe = {},
                                onMarkSafe = null,
                                onUndo = {},
                                onRequestUninstall = {
                                    detailPackage = null
                                    startUninstall(detailVerdict.packageName)
                                },
                                onBack = { detailPackage = null }
                            )
                        } else ProtectedHomeScreen(
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
                            onOpenDetail = { detailPackage = it },
                            onDisconnect = ::disconnectProtected
                        )
                    }
                }
            }

            ToastHost(
                state = toasts,
                modifier = Modifier.align(Alignment.TopCenter),
                large = role == Role.PROTECTED
            )
        }
    }

    /**
     * What changes on a Rondee's phone without a tap on it: a scan finishing,
     * an app being flagged, the Rondor clearing one, an app leaving. All of it
     * is written by services, so this watches the stores rather than callbacks.
     */
    @Composable
    private fun ProtectedToasts() {
        LaunchedEffect(Unit) {
            ScanEvents.results.collect { result ->
                if (result.found == 0) {
                    toast(Tone.SAFE, RondaIcons.shieldCheck, R.string.toast_scan_clean)
                } else {
                    toasts.show(
                        resources.getQuantityString(R.plurals.toast_scan_found, result.found, result.found),
                        Tone.DANGER,
                        RondaIcons.shieldAlert
                    )
                }
            }
        }

        DisposableEffect(Unit) {
            val flagged = FlaggedAppStore(this@MainActivity)
            val history = com.ronda.app.detection.ProtectedHistoryStore(this@MainActivity)
            var knownFlagged = flagged.flaggedPackages().toSet()
            var lastEvent = history.history().firstOrNull()?.timestamp ?: 0L
            // Read while the app is still installed: by the time "uninstalled"
            // is recorded, PackageManager no longer knows its name.
            val labels = knownFlagged.associateWith(::appLabelOf).toMutableMap()

            val onFlagged = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
                val now = flagged.flaggedPackages().toSet()
                (now - knownFlagged).forEach {
                    val app = appLabelOf(it).also { label -> labels[it] = label }
                    toast(Tone.DANGER, RondaIcons.shieldAlert, R.string.toast_flagged, app)
                }
                knownFlagged = now
            }
            val onHistory = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
                val newest = history.history().firstOrNull() ?: return@OnSharedPreferenceChangeListener
                if (newest.timestamp <= lastEvent) return@OnSharedPreferenceChangeListener
                lastEvent = newest.timestamp
                val app = labels[newest.packageName] ?: appLabelOf(newest.packageName)
                when (newest.action) {
                    "safe" -> toast(Tone.SAFE, RondaIcons.circleCheck, R.string.toast_cleared, app)
                    "uninstalled" -> toast(Tone.SAFE, RondaIcons.check, R.string.toast_removed, app)
                }
            }
            flagged.prefs.registerOnSharedPreferenceChangeListener(onFlagged)
            history.prefs.registerOnSharedPreferenceChangeListener(onHistory)
            onDispose {
                flagged.prefs.unregisterOnSharedPreferenceChangeListener(onFlagged)
                history.prefs.unregisterOnSharedPreferenceChangeListener(onHistory)
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
        if (role == Role.GUARDIAN) {
            // A former Rondee becoming a Rondor: its local protection ends here.
            settings.keepsLocalProtection = false
            RondaServices.stopDetection(this)
            requestNotificationPermission()
        }
        refreshStatus()
    }

    /** Setup's Back from pairing: the role was a wrong tap, pick again. */
    private fun leaveRole() {
        if (!roleStore.clearRole()) return
        role = null
        // Choosing Rondee starts detection straight away; undo that too —
        // unless this phone was a Rondee before and keeps local protection.
        if (!RondaServices.runsDetection(this)) RondaServices.stopDetection(this)
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
        toast(Tone.TRUST, RondaIcons.pen, R.string.toast_renamed, name)
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
            // Every pairing starts with a scan, re-pairs included: a new
            // guardian has never seen what is already on this phone. Reset
            // here rather than on unpair, because a guardian can end the
            // pairing while RONDA is closed and only CommandHandler sees it.
            settings.initialScanDone = false
            initialScanDone = false
        }
        roleStore.guardianName = pairedGuardianName
        pairingId = roleStore.pairingId
        guardianName = pairedGuardianName
        setupDismissed = false
        refreshStatus()
        if (role == Role.GUARDIAN) {
            restartGuardianWatch()
            toast(Tone.SAFE, RondaIcons.link, R.string.toast_paired_guardian, roleStore.getProtectedName(code))
        } else {
            toast(
                Tone.SAFE, RondaIcons.heartHandshake, R.string.toast_paired_protected,
                pairedGuardianName.ifBlank { getString(R.string.guardian_unknown_name) }
            )
        }
    }

    /** Guardian side, the Rondor's own choice: tell the Rondee, then forget it. */
    private fun disconnectDevice(id: String) {
        val name = roleStore.getProtectedName(id)
        sendDisconnect(id, com.ronda.app.alert.Command.FROM_GUARDIAN)
        removeDevice(id)
        toast(Tone.NEUTRAL, RondaIcons.logOut, R.string.toast_device_disconnected, name)
    }

    /**
     * Guardian side, the Rondee ended it. GuardianAlertService may have got
     * there first; only whoever still finds the pairing in the store notifies.
     */
    private fun onRondeeDisconnected(id: String) {
        val stillStored = id in roleStore.pairingIds
        val name = roleStore.getProtectedName(id)
        removeDevice(id)
        if (stillStored) GuardianAlertService.notifyRondeeDisconnected(this, name)
        toast(Tone.WARN, RondaIcons.logOut, R.string.toast_rondee_left, name)
    }

    private fun removeDevice(id: String) {
        roleStore.removePairing(id)
        pairingIds = roleStore.pairingIds.toSet()
        pairingId = roleStore.pairingId
        if (selectedPairingId == id) {
            selectedPackage = null
            selectedPairingId = null
        }
        if (pairingIds.isEmpty()) startOver() else restartGuardianWatch()
    }

    /** Protected side, the Rondee's own choice: tell the Rondor, then leave. */
    private fun disconnectProtected() {
        pairingId?.let { sendDisconnect(it, com.ronda.app.alert.Command.FROM_PROTECTED) }
        startOver()
        toast(Tone.NEUTRAL, RondaIcons.logOut, R.string.toast_disconnected_self)
    }

    /**
     * Back from the first scan: the code was the wrong one. Only the pairing
     * is undone, so this lands on the code screen, not the start of setup.
     */
    private fun undoPairing() {
        pairingId?.let { sendDisconnect(it, com.ronda.app.alert.Command.FROM_PROTECTED) }
        leavePairing()
    }

    /**
     * The last pairing ended, from either side: back to the language screen,
     * with nothing left of the old setup, so the phone can become either a
     * Rondor or a Rondee. The alert listener stops; a former Rondee's
     * detection and overlay keep running (refreshStatus below), so apps it
     * flagged stay covered until it is set up as a Rondor.
     */
    private fun startOver() {
        stopService(Intent(this, GuardianAlertService::class.java))
        settings.startSetupOver(roleStore)
        syncPairingFromStore()
        addingDevice = false
        selectedPackage = null
        selectedPairingId = null
        selectedAlertId = null
        guardianTab = GuardianTab.ALERTS
        protectedTab = ProtectedTab.ALERTS
        setupDismissed = false
        pendingUninstall = null
        uninstallDeferred = false
        refreshStatus()
    }

    private fun leavePairing() {
        roleStore.unpair()
        pairingId = null
        pairingIds = emptySet()
        // Detection and the overlay stay up: still a Rondee, still flagged
        // apps to cover. DetectionService drops the old pairing's command
        // listener on its next start, which refreshStatus triggers.
        protectedTab = ProtectedTab.ALERTS
        refreshStatus()
    }

    /**
     * Fire and forget: the local unpair must not wait on the network, and RTDB
     * keeps the write queued if the phone is offline.
     */
    private fun sendDisconnect(id: String, from: String) {
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                com.ronda.app.alert.CommandRepository().send(
                    id,
                    alertId = "",
                    action = com.ronda.app.alert.Command.ACTION_DISCONNECT,
                    packageName = "",
                    from = from
                )
            }.onFailure { Log.e(TAG, "Could not send disconnect for $id", it) }
        }
    }

    private fun addDevice() {
        addingDevice = true
    }

    /** The service re-reads the pairings on every start; no stop needed (stop-then-start raced and crashed). */
    private fun restartGuardianWatch() {
        if (roleStore.pairingIds.isNotEmpty()) GuardianAlertService.start(this)
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
     * silently, which is why this runs on every launch. Which services run is
     * decided by [RondaServices], shared with the boot receiver.
     */
    private fun refreshStatus() {
        status = SetupStatus(
            notifications = Permissions.hasNotifications(this),
            overlay = Permissions.hasOverlay(this),
            usageStats = Permissions.hasUsageStats(this),
            batteryExemption = Permissions.hasBatteryExemption(this)
        )

        RondaServices.start(this)
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
        /** On [ACTION_SCAN_EXISTING]: the pairing's first scan, see DetectionService. */
        const val EXTRA_INITIAL_SCAN = "initial_scan"
    }
}
