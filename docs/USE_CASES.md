# RONDA Use Case Scenarios

This document maps RONDA's main flows and how each one handles the less-than-ideal paths (edge cases and failures). It describes what the code on `main` actually does.

Terms used throughout: the **Rondor** is the guardian's phone (the one that receives alerts); the **Rondee** is the protected phone (the one that runs detection and the overlay). One APK holds both roles; the role is picked during setup.

Most scenarios can be run end to end on the emulators with `scripts/ronda scenario <id>`; the id is given under each heading. `scripts/ronda scenario` with no id lists them all.

---

## UC01: Setup, Pairing and Device Management

**Normal Scenario: First Pairing (QR Code or Typed Code)** — `uc01-pair`, `uc01-qr`

| No | Actor action | System response |
| :--- | :--- | :--- |
| 1 | User 1 opens RONDA, chooses the "Rondor" role, enters their own name and what they call the person being guarded (a preset such as "Mum", or any name typed in) | The system creates a pairing record in Firebase and shows a *QR code* and a 6-character *text code* (`GuardianPairingScreen`). The code shows at once; a live countdown shows how long it stays valid (10 minutes). |
| 2 | User 2 opens RONDA, chooses "Rondee", then **scans the QR code** or **types the text code** | The system validates the input, claims the pairing in Firebase (`PairingRepository`) and links the two phones. Both phones show a confirmation toast. |
| 3 | (The Rondee continues) | The Rondee is taken to the *Initial Scan* screen (UC02), then the permission wizard (UC05), then its home screen. The Rondor lands on its home screen with the new phone listed. |

<br>

**Alternative Scenario 1: Rondor Adds Another Rondee (Multiple Rondees)** — `uc01-multi`

| No | Actor action | System response |
| :--- | :--- | :--- |
| 1 | The Rondor opens Settings and taps "Add another device" | The system shows the same name form as the first setup. |
| 2 | The Rondor fills in the name and taps Continue | The system issues a new QR code / text code for the extra phone, without disconnecting the existing Rondee. |
| 3 | The new Rondee enters the code | The system links the new Rondee to the same Rondor. The Rondor's Settings now list two or more connected phones, and its home screen shows alerts from all of them. |

<br>

**Alternative Scenario 2: Disconnecting (Unpair)** — `uc01-disconnect-rondee`, `uc01-disconnect-rondor`

| No | Actor action | System response |
| :--- | :--- | :--- |
| 1 | The Rondor opens Settings, finds one of its connected phones and taps "Disconnect" on it — or the Rondee taps "Disconnect" in its own Settings | The system asks for confirmation for that one pairing. |
| 2 | The user confirms | The system sends a disconnect command through Firebase and removes the pairing locally, without touching the Rondor's other pairings (if any). |
| 3 | (On the other phone) | The other phone sees the disconnect in real time, updates its screen and shows a notification (or a toast, if RONDA is open) that the other side has ended the connection. |
| 4 | (What each phone does next) | **A Rondee** whose pairing ended — from either side — starts over from the language screen, free to be set up as either role. **A Rondor** starts over only when its *last* Rondee is gone; while it still guards another phone it just shows the notice and stays on its home screen. |
| 5 | (Protection on the former Rondee) | Starting over never uncovers anything: apps the Rondee had flagged stay blocked by the overlay, and new installs are still detected and blocked locally, with nobody told, until the phone is set up again. Only choosing the Rondor role ends that local protection. |

<br>

**Alternative Scenario 3: Invalid Pairing Code or QR Code** — `uc01-invalid`

| No | Actor action | System response |
| :--- | :--- | :--- |
| 1 | The Rondee types a code that is badly formed, or one that no Rondor created | The system rejects it with a specific message: "The code is 6 letters or digits." for a malformed code, or "Code not found. Check what you typed." for an unknown one. The typed code stays in the field. |
| 2 | The Rondee scans a QR code that is not a RONDA link | `QrCodeUtils.codeFromLink()` returns nothing, so the field is not filled and no pairing is attempted. |

<br>

**Alternative Scenario 4: Pairing Code Expired or Already Used** — `uc01-expired`, `uc01-used`

| No | Actor action | System response |
| :--- | :--- | :--- |
| 1 | (On the Rondor, 10 minutes after the code was made) | The countdown reaches zero and the code card is replaced by "This code has expired" with a **Make a new code** button. The names entered on the step before are kept. |
| 2 | The Rondee enters a code created more than 10 minutes ago, or one already claimed by another phone | `PairingRepository.claimPairing()` reads the pairing record and refuses the claim (`Expired` / `AlreadyUsed`). The system shows a specific message: "This code has expired. Ask for a new one." or "This code has already been used. Ask for a new one." |
| 3 | The Rondor taps Make a new code and reads it out again | The system issues a new code valid for 10 minutes, and pairing continues as in the Normal Scenario. |

<br>

**Alternative Scenario 5: No Internet During Pairing** — `uc01-offline`

| No | Actor action | System response |
| :--- | :--- | :--- |
| 1 | The Rondee taps Connect while it has no internet | The system cannot reach Firebase (`ClaimResult.Failed`) and shows "Could not reach the server. Check the internet connection, then try again." The typed code stays in the field. |
| 2 | (On the Rondor, when the code cannot be saved) | The code is shown immediately, but if the server has not confirmed it within 10 seconds, `GuardianPairingScreen` replaces it with a failure card and a "Try again" button that issues a new code. |

<br>

**Alternative Scenario 6: Scanning the QR Code with the Camera App (Deep Link)** — `uc01-qr`

| No | Actor action | System response |
| :--- | :--- | :--- |
| 1 | The Rondee scans the Rondor's QR code with the phone's camera or any QR scanner | The QR holds the link `ronda://pair/XXXXXX`. The OS opens RONDA (or hands the link to the running RONDA through `onNewIntent`), and `QrCodeUtils.codeFromLink()` extracts the code. |
| 2 | (Automatic) | The code is filled into `ProtectedPairingScreen` with a note that it came from the QR but is not connected yet. If the Rondee is still on the language, intro or role screen, the code is held and filled in once the pairing screen opens. |
| 3 | The Rondee taps Connect | Pairing continues as in the Normal Scenario. A link never pairs the phone on its own without a tap from the phone's owner, so consent is always a conscious act (PRD FR-2). |

<br>

**Alternative Scenario 7: Going Back During Setup**

| No | Actor action | System response |
| :--- | :--- | :--- |
| 1 | The user taps the back arrow (or the system Back gesture) on any setup screen: intro, role, pairing | The system steps back one screen, undoing the single choice that moved it forward. A wrong tap on the role screen — Rondee instead of Rondor — is undone without clearing the app's data. |
| 2 | The Rondee taps back on the *Initial Scan* screen, before starting the scan | The pairing is undone (the Rondor is told) and the Rondee returns to the code screen, ready for the right code. Back is not offered while a scan or a pairing claim is in progress. |

---

## UC02: Detecting Malicious Apps (Initial Scan & Real Time)

**Normal Scenario 1: Initial Scan (After Pairing)** — `uc02-initial`

| No | Actor action | System response |
| :--- | :--- | :--- |
| 1 | The Rondee has just finished pairing (UC01) and taps **Scan now** on the *Initial Scan* screen | `DetectionService` scans every non-system app already installed on the phone. The screen waits for the real result (at least a short moment, at most 30 seconds). |
| 2 | (Automatic) | `RiskEvaluator` checks each app's install source and declared permissions. A sideloaded app with dangerous permissions is judged **HIGH RISK**. |
| 3 | (Automatic) | The system records problem apps in `FlaggedAppStore` and sends an alert for each to the Rondor through Firebase. Apps that were already flagged before this pairing (for example on a phone set up again with a new Rondor) are reported too, so the new Rondor learns about them. Apps an earlier Rondor cleared stay cleared. |
| 4 | (Automatic) | A toast says how the scan ended: "Scan done: no dangerous apps" or "Scan done: N dangerous apps found". |

<br>

**Normal Scenario 2: Real-Time Detection of a New Install (True Positive)** — `uc02-realtime`

| No | Actor action | System response |
| :--- | :--- | :--- |
| 1 | The Rondee installs an APK received over WhatsApp (outside the Play Store) | Android broadcasts `ACTION_PACKAGE_ADDED`. The system (`InstallReceiver`) starts the evaluation in the background. |
| 2 | (Automatic, no user action) | `RiskEvaluator` extracts the app's signals. Because it declares dangerous permissions (for example `READ_SMS`), it is judged **HIGH RISK**: the Rondee gets a local notification and a toast, the app is covered by the overlay (UC03), and an alert is sent to the Rondor, who gets a notification and a toast. |

<br>

**Alternative Scenario 1: Manual Rescan (Scan Again)** — `uc02-scan-again`

| No | Actor action | System response |
| :--- | :--- | :--- |
| 1 | The Rondee taps "Scan again" on its home screen — or the Rondor taps "Scan this phone now" in Settings | The system scans all installed apps again. |
| 2 | (Automatic) | Newly dangerous apps are flagged and reported to the Rondor at once. Apps already flagged are skipped, since this Rondor already has them. A toast reports the result on the Rondee. |

<br>

**Alternative Scenario 2: Installing a Safe App (True Negative)** — `uc02-safe-install`

| No | Actor action | System response |
| :--- | :--- | :--- |
| 1 | The Rondee installs an app from the Play Store (even one asking for SMS permission), OR a sideloaded app without dangerous permissions | `RiskEvaluator` runs. Because the app does not meet both conditions for danger, it scores below the alert threshold. Nothing is flagged and **no** alert is sent to the Rondor. (Only an app the Rondor has explicitly marked safe goes into `SafeAppStore`.) |

<br>

**Alternative Scenario 3: The Rondee Is Offline When a Malicious App Is Detected** — `uc02-offline`

| No | Actor action | System response |
| :--- | :--- | :--- |
| 1 | The Rondee installs a malicious APK while it has no internet | Detection runs fully on the device: `RiskEvaluator` judges it **HIGH RISK**, the system shows a local warning notification, records the app in `FlaggedAppStore` and starts the overlay (UC03). |
| 2 | (Automatic) | Writing the alert to Firebase is not confirmed within 8 seconds. Firebase Realtime Database persistence keeps the alert in a local queue. |
| 3 | The Rondee is back online | Firebase sends the queued alert, and the Rondor receives it as in Normal Scenario 2. |

<br>

**Alternative Scenario 4: A Flagged App Is Updated** — `uc02-update`

| No | Actor action | System response |
| :--- | :--- | :--- |
| 1 | An app already on the **HIGH RISK** list is updated to a new version | Android sends `ACTION_PACKAGE_REMOVED` then `ACTION_PACKAGE_ADDED`, both with `EXTRA_REPLACING`. `InstallReceiver` ignores both for a flagged app: the **HIGH RISK** status and the block stay, no second alert is sent, and the Rondor's decision about the app stands. An update to an app that was *not* flagged is still evaluated, since the new version may declare new permissions. |

<br>

**Alternative Scenario 5: An App Previously Marked Safe Is Reinstalled** — `uc02-reinstall-safe`

| No | Actor action | System response |
| :--- | :--- | :--- |
| 1 | The Rondee removes an app the Rondor had marked safe | `InstallReceiver` removes it from `SafeAppStore`, because a reinstall is a new question. The Rondor is told the app was removed; its history keeps the "marked safe" entry and adds "removed". |
| 2 | The Rondee installs an app with the same package name again | The app is evaluated from scratch by `RiskEvaluator`. If it is **HIGH RISK**, the Rondor receives a new alert — a new card to decide on, while the history of the first install stays. |

---

## UC03: Blocking Malicious Apps (Soft-Block Overlay)

**Normal Scenario: The Overlay Appears When the App Is Opened** — `uc03-overlay`

| No | Actor action | System response |
| :--- | :--- | :--- |
| 1 | The Rondee tries to open an app that was just flagged **HIGH RISK** | `ForegroundAppMonitor` sees the package come to the foreground. |
| 2 | (Automatic) | `OverlayService` immediately draws a full-screen, RONDA-branded warning over the app. There is no close button; the only way out is the OS Home button. |

<br>

**Alternative Scenario 1: The Rondor Has Marked the App Safe** — `uc04-safe`

| No | Actor action | System response |
| :--- | :--- | :--- |
| 1 | The Rondee opens an app that was blocked but that the Rondor has since marked safe | The app is no longer in `FlaggedAppStore`, so `ForegroundAppMonitor` does not cover it. The app works normally. |

<br>

**Alternative Scenario 2: Overlay or Usage Access Permission Missing or Revoked** — `uc03-permission`

| No | Actor action | System response |
| :--- | :--- | :--- |
| 1 | A **HIGH RISK** app is detected, but `SYSTEM_ALERT_WINDOW` or `PACKAGE_USAGE_STATS` was never granted, or was revoked by OEM power management | `Permissions.canBlock()` is *false*. The overlay does not start, or `OverlayService` stops itself if the permission is revoked while it runs. Detection and alerts to the Rondor keep working. |
| 2 | The Rondee opens RONDA | The Rondee home screen turns into a yellow card with a "Continue setup" button that opens the permission wizard (UC05). |
| 3 | The Rondee grants the permission again and returns to RONDA | The system rechecks permissions in `onResume`. Because `FlaggedAppStore` still holds apps, `OverlayService` starts again and blocking resumes. |

<br>

**Alternative Scenario 3: The Rondee Restarts** — `uc03-reboot`

| No | Actor action | System response |
| :--- | :--- | :--- |
| 1 | The Rondee is restarted (or RONDA is updated, which also stops its services) while a **HIGH RISK** app is still unhandled | The flagged list stays in `FlaggedAppStore` on local storage. |
| 2 | (Automatic, without opening RONDA) | `StartupReceiver` receives `BOOT_COMPLETED` / `MY_PACKAGE_REPLACED` and restarts `DetectionService` and, if permissions allow, `OverlayService`. The malicious app is covered again, and new installs are detected, without anyone opening RONDA. On a Rondor, the alert listener is restarted the same way. |

<br>

**Alternative Scenario 4: The User Presses Back on the Overlay** — `uc03-back`

| No | Actor action | System response |
| :--- | :--- | :--- |
| 1 | The Rondee presses Back or taps the screen while the overlay is showing | The overlay absorbs the touches and the Back key, so neither reaches the malicious app underneath. Home remains the only way out. |

---

## UC04: The Rondor Handles an Alert & Uninstall Sync

**Normal Scenario: The Rondor Requests an Uninstall and the Status Syncs** — `uc04-uninstall`

| No | Actor action | System response |
| :--- | :--- | :--- |
| 1 | The Rondor opens the alert notification (delivered by `GuardianAlertService`, which keeps a live Realtime Database listener open) | The system shows the app's detail screen (`AlertDetailScreen`): the app name, the score, and why it is dangerous, in the Rondor's own words for the protected person. |
| 2 | The Rondor taps "Remove this app" | The system sends an *uninstall command* through Firebase and confirms with a toast. |
| 3 | (On the Rondee) | `CommandHandler` stores the request in `PendingUninstallStore` and shows a high-priority notification. When RONDA is open, `UninstallPromptScreen` takes over the screen and explains in plain words that the guardian asked for the app to be removed. |
| 4 | The Rondee taps "Remove this app", then "OK" in Android's system dialog | The app is removed. Android broadcasts `ACTION_PACKAGE_REMOVED`. |
| 5 | (Automatic) | `InstallReceiver` catches the removal, sets the alert's status in Firebase to `STATUS_UNINSTALLED` and lifts the local block. |
| 6 | (On the Rondor) | The Rondor's screen updates by itself and moves the alert from the active list to the History tab, and `GuardianAlertService` shows a success notification that the app was removed from the Rondee's phone. |

<br>

**Alternative Scenario 1: The Rondor Allows the App (Mark as Safe)** — `uc04-safe`

| No | Actor action | System response |
| :--- | :--- | :--- |
| 1 | The Rondor taps "Mark safe" on `AlertDetailScreen` and confirms | The system sends a *mark-safe command* through Firebase. |
| 2 | (On the Rondee) | `CommandHandler` removes the app from `FlaggedAppStore`, adds it to `SafeAppStore` and lifts the overlay at once. The Rondee gets a notification that the guardian cleared the app. |
| 3 | (On the Rondor) | The alert's status becomes resolved (safe), it moves to the History tab, and a toast confirms it. |

<br>

**Alternative Scenario 2: The Rondee Cancels the Uninstall**

| No | Actor action | System response |
| :--- | :--- | :--- |
| 1 | The Rondee taps "Cancel" in Android's system uninstall dialog | Android cancels the removal. `MainActivity` keeps the pending uninstall. The app stays on the **HIGH RISK** list, the overlay still covers it when opened, and the Rondor's screen keeps showing "Waiting" until the uninstall actually happens. |

<br>

**Alternative Scenario 3: The Rondee Defers the Request in RONDA ("Not now")** — `uc04-defer`

| No | Actor action | System response |
| :--- | :--- | :--- |
| 1 | The uninstall request arrives while nobody is holding the Rondee | `CommandHandler` stores the request in `PendingUninstallStore` and shows a high-priority notification "Your guardian asks you to remove this app". |
| 2 | The Rondee opens RONDA (or taps the notification) | `UninstallPromptScreen` takes over the screen and explains in simple words that the guardian asked for the app to be removed. |
| 3 | The Rondee taps "Not now" | The system returns to the Rondee home screen. The request stays stored, the app stays blocked by the overlay, and the Rondor's screen keeps waiting. |

<br>

**Alternative Scenario 4: The Rondor Sends the Uninstall Request Again** — `uc04-defer`

| No | Actor action | System response |
| :--- | :--- | :--- |
| 1 | The Rondor opens `AlertDetailScreen` for an app that was requested for removal but is still installed | The system shows that the app is still waiting to be removed on the Rondee's phone, with a "Send the request again" button. |
| 2 | The Rondor taps "Send the request again" | The system sends a new *uninstall command* through Firebase. The Rondee shows the request notification again, and the flow continues from step 3 of the Normal Scenario. |

<br>

**Alternative Scenario 5: The Rondor Takes Back "Mark Safe" (Undo)** — `uc04-undo`

| No | Actor action | System response |
| :--- | :--- | :--- |
| 1 | The Rondor taps "Mark safe" on `AlertDetailScreen` | The system shows a confirmation sheet, because marking an app safe removes protection from the Rondee's phone. |
| 2 | The Rondor confirms | The system sends the *mark-safe command* (as in Alternative Scenario 1) and shows an "Undo" button for 10 seconds. |
| 3 | The Rondor taps "Undo" within 10 seconds | The system sets the alert back to not safe (`STATUS_UNSAFE`) **and** sends a *revoke-safe command* to the Rondee, which removes the app from `SafeAppStore`, flags it again and brings the overlay back. The Rondor can then choose "Remove this app". |

---

## UC05: First-Time Setup and Device Permissions

**Normal Scenario: First Onboarding** — `uc05-onboarding`

| No | Actor action | System response |
| :--- | :--- | :--- |
| 1 | The user opens RONDA for the first time | The system shows the animated splash (the RONDA mark pops in, slides left as the name appears), then the language screen (Indonesian / English). |
| 2 | The user picks a language and taps "Continue" | The system applies the language to the whole app and shows a short introduction (`IntroScreen`). |
| 3 | The user taps the start button | The system shows the role screen: Rondor (this phone guards) or Rondee (this phone is guarded). |
| 4 | The user picks a role | The system saves the role locally (`RoleStore`). For a Rondor it asks for notification permission straight away; for a Rondee it starts `DetectionService`. Both then go to pairing (UC01). Every one of these screens has a back arrow (UC01 Alternative Scenario 7). |

<br>

**Normal Scenario 2: The Permission Wizard on the Rondee** — `uc05-wizard`

| No | Actor action | System response |
| :--- | :--- | :--- |
| 1 | The Rondee has just paired and scanned, and its permissions are incomplete | The system shows `SetupWizardScreen` with 4 steps, one permission per screen: notifications, display over other apps (`SYSTEM_ALERT_WINDOW`), usage access (`PACKAGE_USAGE_STATS`) and the battery-optimisation exemption. Each step explains why the permission is needed. |
| 2 | The user (helped by the Rondor) taps the step's button | The system opens the matching permission dialog or Android Settings page. |
| 3 | The user grants the permission and returns to RONDA | The system rechecks every permission in `onResume`, fills the progress indicator and moves to the next step. |
| 4 | Every permission is granted and the user taps "Done" | The system shows the Rondee home screen with a green "protected" card and the guardian's name. |

<br>

**Alternative Scenario 1: The User Leaves the Wizard Early**

| No | Actor action | System response |
| :--- | :--- | :--- |
| 1 | The Rondee presses Back in the middle of the wizard | The system returns to the Rondee home screen, which shows a yellow card listing the permissions still off and a "Continue setup" button. Detection and alerts keep running, but the overlay cannot block yet (UC03 Alternative Scenario 2). |
| 2 | The user taps "Continue setup" | The system reopens the wizard at the first step that is not done. |

<br>

**Alternative Scenario 2: The Rondee Sees Who Is Guarding (Transparent Monitoring)**

| No | Actor action | System response |
| :--- | :--- | :--- |
| 1 | The Rondee pulls down the notification shade | The system always shows a permanent notification from `DetectionService` stating that RONDA is protecting this phone. |
| 2 | The Rondee opens the profile on its home screen, or its Settings tab | The system shows the guardian's name and the pairing code. Ending the pairing is possible only from Settings, behind a confirmation (UC01 Alternative Scenario 2). |

---

## UC06: Rondor Settings

**Normal Scenario: Changing the Nickname, Theme and Language**

| No | Actor action | System response |
| :--- | :--- | :--- |
| 1 | The Rondor opens the "Settings" tab in the bottom bar | The system shows the guarded phones, appearance, language and the option to disconnect each phone. |
| 2 | The Rondor changes a Rondee's nickname (for example "Mum") | The system saves the new name locally. Every sentence on the Rondor (alert list, details, notifications) uses it immediately, and a toast confirms it. |
| 3 | The Rondor switches the theme (light / dark / follow system) or the language | The system saves the choice and applies it across the app. |

<br>

**Alternative Scenario 1: The Rondor Is Offline** — `uc06-offline`

| No | Actor action | System response |
| :--- | :--- | :--- |
| 1 | The Rondor loses its internet connection while on the Alerts tab | The system reads Firebase's connection state (`.info/connected`) and shows an offline marker in the top bar, so an empty list is not mistaken for "all clear". |
| 2 | The connection returns | The offline marker disappears, and alerts that arrived while offline appear in the list at once. |

---

## Feedback the user sees on every change

These apply across all use cases.

- **Toasts.** A short banner drops in from the top whenever something changes or finishes: pairing, a scan finishing, a new alert or watched app, an app removed or marked safe, a request sent, a disconnect from either side, a rename. On the Rondee it uses large print and stays on screen longer.
- **Notification sounds by situation.** Every notification carries the RONDA mark in the status bar, and its sound says what kind of news it is: *danger* (fast, high, repeated) for an emergency alert or a detection; *warning* (two falling notes) for a lower-risk alert or a lost connection; *resolved* (a rising bell) when an app was removed or cleared; *info* (a soft chime) for a request from the guardian.
- **Tactile controls.** Every button dips under the thumb with a haptic tick and springs back when released.
