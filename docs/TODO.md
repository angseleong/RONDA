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

## ACTIVE — Block 1: Detection Engine (12–15 Aug)

- [ ] Register `InstallReceiver` for `ACTION_PACKAGE_ADDED` **at runtime** via `registerReceiver()` from a foreground service (cannot use manifest since API 26)
- [ ] Implement `RiskEvaluator`: read install source via `getInstallSourceInfo()`, read declared permissions via `getPackageInfo(GET_PERMISSIONS)`
- [ ] Risk rule: sideloaded (not `com.android.vending`) AND declares `READ_SMS` → HIGH RISK
- [ ] On HIGH RISK: show a local notification on the protected device (temporary, before FCM is wired)
- [ ] Build test-sample APK (`com.test.undangan`): declares `READ_SMS`, single screen "Undangan Pernikahan", does nothing else
- [ ] End-to-end test: install test-sample APK on emulator → RONDA detects and shows notification

**Checkpoint (15 Aug):** Installing the test-sample APK on the emulator produces a local detection notification.

---

## BACKLOG — Block 2: Soft-Block Overlay (16–17 Aug)

- [ ] Implement permission setup screen: guide user to enable `SYSTEM_ALERT_WINDOW` and `PACKAGE_USAGE_STATS`
- [ ] Implement `ForegroundAppMonitor`: poll `UsageStatsManager` for current foreground app
- [ ] Implement `OverlayService`: draw full-screen warning when flagged app is in foreground
- [ ] Overlay is branded as RONDA, no "continue" button, clearly states the app is suspected malware
- [ ] Overlay clears when app is uninstalled or marked safe
- [ ] Test: open test-sample APK → RONDA overlay covers it immediately

**Checkpoint (17 Aug):** Opening the test-sample APK triggers the RONDA warning overlay. Works offline.

## BACKLOG — Block 3: Pairing + Alerting (18–19 Aug)

- [ ] Implement `RoleSelectionScreen` (Guardian / Protected), store role in SharedPreferences
- [ ] Guardian: generate pairing token, display as QR code
- [ ] Protected: scan QR code, write pairing record to Firebase RTDB
- [ ] On HIGH RISK detection: write alert record to Firebase RTDB
- [ ] Firebase triggers FCM push to guardian device
- [ ] Guardian receives high-priority notification with app name and risk details
- [ ] Guardian UI: `AlertDetailScreen` with Uninstall / Mark Safe buttons

**Checkpoint (19 Aug):** Two emulators — installing test APK on one causes the other to alert.

## BACKLOG — Block 4: Guardian Response (20 Aug)

- [ ] Guardian taps "Uninstall" → writes command to Firebase RTDB
- [ ] Protected device listens for commands, triggers `Intent.ACTION_DELETE` for flagged package
- [ ] Guardian taps "Mark Safe" → adds package to local allowlist, clears overlay
- [ ] Protected home screen shows uninstall confirmation prompt (large text, high contrast)

## BACKLOG — Block 5: Demo (21 Aug)

- [ ] Record 60-second demo video: two devices side by side
- [ ] Final submission
