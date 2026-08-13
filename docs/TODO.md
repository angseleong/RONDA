# RONDA — Active Task List

> **Rule:** AI hanya boleh mengerjakan tugas yang ada di bagian ACTIVE.
> Tugas di bawah garis (---) adalah backlog. Jangan dikerjakan sampai dipindahkan ke atas.

---

## DONE — Block 0: Project Setup (10–11 Aug)

- [x] Android Studio + SDK installed, emulator running
- [x] Create Kotlin + Compose project: `com.ronda.app`, `minSdk 30`
- [x] Verify empty app builds and runs on emulator
- [x] `git init`, push to GitHub, first commit
- [x] Create empty package folders: `detection/`, `overlay/`, `pairing/`, `alert/`, `ui/`
- [x] Context documents finalized: `AGENTS.md`, `ARCHITECTURE.md`, `TODO.md`

## DONE — Block 1: Detection Engine (12–15 Aug)

- [x] Register `InstallReceiver` for `ACTION_PACKAGE_ADDED` **at runtime** via `registerReceiver()` from a foreground service (cannot use manifest since API 26)
- [x] Implement `RiskEvaluator`: read install source via `getInstallSourceInfo()`, read declared permissions via `getPackageInfo(GET_PERMISSIONS)`
- [x] Risk rule: sideloaded (not `com.android.vending`) AND declares `READ_SMS` → HIGH RISK
- [x] On HIGH RISK: show a local notification on the protected device (temporary, before FCM is wired)
- [x] Build test-sample APK (`com.test.undangan`): declares `READ_SMS`, single screen "Undangan Pernikahan", does nothing else
- [x] End-to-end test: install test-sample APK on emulator → RONDA detects and shows notification

**Checkpoint (15 Aug):** Installing the test-sample APK on the emulator produces a local detection notification.

## DONE — Block 2: Soft-Block Overlay (16–17 Aug)

- [x] Implement permission setup screen: guide user to enable `SYSTEM_ALERT_WINDOW` and `PACKAGE_USAGE_STATS`
- [x] Implement `ForegroundAppMonitor`: poll `UsageStatsManager` for current foreground app
- [x] Implement `OverlayService`: draw full-screen warning when flagged app is in foreground
- [x] Overlay is branded as RONDA, no "continue" button, clearly states the app is suspected malware
- [x] Overlay clears when app is uninstalled (`ACTION_PACKAGE_REMOVED` → unflag → service stops)
- [ ] Overlay clears when marked safe — blocked on Block 4: the Mark Safe button exists on `AlertDetailScreen` but is not wired yet. `FlaggedAppStore.unflag()` is the hook it will call.
- [ ] Test: open test-sample APK → RONDA overlay covers it immediately — **not yet run, no AVD on this machine**

**Checkpoint (17 Aug):** Opening the test-sample APK triggers the RONDA warning overlay. Works offline.

---

## ACTIVE — Block 3: Pairing + Alerting (18–19 Aug)

- [x] Implement `RoleSelectionScreen` (Guardian / Protected), store role in SharedPreferences (`RoleStore`, write-once)
- [x] Guardian: generate pairing token, display as QR code (`GuardianPairingScreen` — QR **and** a readable 6-char code)
- [x] Protected: enter pairing code, write pairing record to Firebase RTDB — **changed from "scan QR"**: guardian is usually not in the room, and an emulator camera cannot scan another emulator's screen. QR is still generated; see `docs/ARCHITECTURE.md` §5.
- [x] On HIGH RISK detection: write alert record to Firebase RTDB (`AlertRepository.submit()` from `InstallReceiver`)
- [x] ~~Firebase triggers FCM push to guardian device~~ → **replaced by RTDB listener.** FCM legacy server keys were shut off June 2024; device-to-device push now needs a Cloud Function on the Blaze plan. `GuardianAlertService` holds an RTDB listener instead. Rationale and trade-off in `docs/ARCHITECTURE.md` §4.
- [x] Guardian receives high-priority notification with app name and risk details
- [x] Guardian UI: `AlertDetailScreen` with Uninstall / Mark Safe buttons (buttons rendered; their actions are Block 4)
- [ ] **Blocked on you:** create Firebase project + Realtime Database, put `google-services.json` in `app/`. Steps and DB rules in `docs/ARCHITECTURE.md` §7. Nothing compiles until this exists.
- [ ] End-to-end test: two emulators, pair them, install test APK on the protected one → guardian gets a notification

**Checkpoint (19 Aug):** Two emulators — installing test APK on one causes the other to alert.

## BACKLOG — Block 4: Guardian Response (20 Aug)

- [ ] Guardian taps "Uninstall" → writes command to Firebase RTDB
- [ ] Protected device listens for commands, triggers `Intent.ACTION_DELETE` for flagged package
- [ ] Guardian taps "Mark Safe" → adds package to local allowlist, clears overlay
- [ ] Protected home screen shows uninstall confirmation prompt (large text, high contrast)

## BACKLOG — Block 5: Demo (21 Aug)

- [ ] Record 60-second demo video: two devices side by side
- [ ] Final submission
