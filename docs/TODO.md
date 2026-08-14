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
- [x] End-to-end test: install test-sample APK on emulator → RONDA detects and shows notification — **verified 14 Aug**: `RESULT for com.ronda.testsample: source=manual, isSideloaded=true, declaresSms=true, risk=HIGH`

**Checkpoint (15 Aug):** Installing the test-sample APK on the emulator produces a local detection notification. ✅ **Verified 14 Aug.**

## DONE — Block 2: Soft-Block Overlay (16–17 Aug)

- [x] Implement permission setup screen: guide user to enable `SYSTEM_ALERT_WINDOW` and `PACKAGE_USAGE_STATS`
- [x] Implement `ForegroundAppMonitor`: poll `UsageStatsManager` for current foreground app
- [x] Implement `OverlayService`: draw full-screen warning when flagged app is in foreground
- [x] Overlay is branded as RONDA, no "continue" button, clearly states the app is suspected malware
- [x] Overlay clears when app is uninstalled (`ACTION_PACKAGE_REMOVED` → unflag → service stops)
- [x] Overlay clears when marked safe — done in Block 4: `CommandHandler.markSafe()` calls `FlaggedAppStore.unflag()`, and `OverlayService` stops itself once nothing is flagged.
- [x] Test: open test-sample APK → RONDA overlay covers it immediately — **verified 14 Aug** on `Pixel_6` (API 33): `OverlayService: Overlay shown over com.ronda.testsample`

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
- [x] Firebase project `ronda-b7ba0` + Realtime Database (Singapore), `app/google-services.json` in place, rules published
- [x] End-to-end test: two emulators, pair them, install test APK on the protected one → guardian gets a notification — **verified 14 Aug**

**Checkpoint (19 Aug):** Two emulators — installing test APK on one causes the other to alert. ✅ **Verified 14 Aug.**

**Verification log (14 Aug, two Pixel 6 / API 33 emulators):**
1. Guardian picked its role → code `QTDEZ3` published to `pairings/QTDEZ3`, QR rendered
2. Protected typed the code → `status` became `active`, both devices left their pairing screens
3. Role gating held: protected ran only `DetectionService`, guardian only `GuardianAlertService`
4. Test APK installed → `source=manual, isSideloaded=true, declaresSms=true, risk=HIGH`
5. Alert `-OzwnOGG6SZEySyUA3lG` written to `alerts/QTDEZ3/`
6. Guardian raised `importance=4, category=alarm`: "BAHAYA: aplikasi mencurigakan dipasang — Undangan Pernikahan dipasang di HP orang tua Anda."
7. Opening the test APK on protected → RONDA overlay covered it

Bug found and fixed during this run: `Pairing.isActive` was a derived property without
`@get:Exclude`, so the SDK wrote a junk `active` boolean into `pairings/` alongside
`status` and logged a `ClassMapper` warning on every read.

## ACTIVE — Block 4: Guardian Response (20 Aug)

- [x] Guardian taps "Uninstall" → writes command to Firebase RTDB (`CommandRepository.send()`)
- [x] Protected device listens for commands, triggers `Intent.ACTION_DELETE` for flagged package (`CommandHandler`, run from `DetectionService`)
- [x] Guardian taps "Mark Safe" → adds package to local allowlist (`SafeAppStore`), clears overlay
- [x] Protected home screen shows uninstall confirmation prompt (large text, high contrast) — `UninstallPromptScreen`
- [x] End-to-end test: guardian taps each button → protected acts, guardian sees the outcome — **verified 14 Aug**

**Verification log (14 Aug, two Pixel 6 / API 33 emulators):**

*Uninstall*
1. Guardian tapped "Hapus aplikasi" → command written to `commands/QTDEZ3/`
2. Protected `CommandHandler` picked it up, raised `importance=4, category=alarm`, stored the request to disk
3. `UninstallPromptScreen` explained the request, then opened the system dialog
4. Confirmed → package gone, block cleared, `OverlayService` stopped itself
5. `InstallReceiver` reported `status = uninstalled`; guardian's screen showed "Aplikasi sudah dihapus dari HP orang tua Anda"

*Mark safe*
6. Test APK reinstalled → fresh alert, overlay covering it
7. Guardian tapped "Tandai aman" → overlay disappeared **while the app was still on screen**, no user action on the protected phone
8. `SafeAppStore` persisted; reinstalling again logged "Package is on the guardian's allowlist, skipping"

**Two bugs found by running it, neither catchable by a build:**
- `ACTION_DELETE` was refused silently — `REQUEST_DELETE_PACKAGES` was not declared in the manifest. No crash, no dialog, no log unless watching `UninstallerActivity`.
- The uninstall prompt only appeared after a fresh `onResume`. If RONDA was already open when the guardian decided, the screen never changed. `PendingUninstallStore.observe()` now drives it reactively.

**Bug found on a from-scratch run (14 Aug, second pass).** Everything above was
verified on devices that were *already paired* when `DetectionService` first
started. On a phone set up from zero the order is role → service start →
pairing, and the service read `RoleStore.pairingId` only in `onCreate` — so it
logged "Not paired" once and never looked again. No guardian command was ever
collected: **Uninstall** produced no prompt and **Mark safe** never lifted the
overlay, with nothing in the logs but that one line. The lookup moved to
`onStartCommand` (idempotent via `commandJob`), which MainActivity re-triggers on
every resume. Re-verified end to end on two freshly reset emulators.

Also added: the guardian now gets a notification when the protected phone
actually completes the removal (`status = uninstalled` → "Aplikasi berbahaya
sudah dihapus"), and that alert's original red notification is cancelled. Before
this, the outcome was only visible if the guardian happened to have the alert
open.

**Design note.** `executedAt` on a command means *delivered*, not *done*. An
uninstall is only reported as complete when the OS broadcasts
`ACTION_PACKAGE_REMOVED` and `InstallReceiver` writes `status = uninstalled`. The
guardian is never told an app was removed because a command was received —
Android requires the person holding the phone to confirm in a system dialog, and
they may decline.

## BACKLOG — Block 5: Demo (21 Aug)

- [ ] Record 60-second demo video: two devices side by side
- [ ] Final submission
