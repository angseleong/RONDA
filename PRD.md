# Project Requirements Document (PRD)

## 1. Project Overview

- **Project Name:** RONDA — Real-time On-Device Detection Agent
- **Project ID:** 1
- **Date:** 10 August 2026
- **Version:** 1.1
- **Prepared By:** Axeleon, Dhanes
- **Approved By:** Malik
- **Context:** HackNusa 2026 — Telkom University × Kaspersky. Track: Human-Centric Design.

### 1.1 Purpose of the Project

RONDA protects non-technical Android users from social-engineering malware delivered through WhatsApp.

The core insight: existing antivirus warns the victim. The victim is exactly the person who has already been socially engineered and will ignore the warning. RONDA routes the alert to a **trusted guardian** instead — someone who is not under the scammer's influence and can intervene.

High-level objectives:

1. Detect malicious sideloaded APKs at install time, before the victim grants any permission.
2. Deliver a real-time alert to a paired guardian device.
3. Block the flagged app on-device so the victim cannot use it while the guardian is being alerted.
4. Let the guardian act remotely (trigger uninstall).
5. Keep false positives near zero so guardians do not learn to ignore alerts.

### 1.2 Project Background

A widespread scam in Indonesia works like this:

1. A scammer contacts the victim on WhatsApp, posing as a courier, bank officer, or wedding guest.
2. They send an APK file disguised as a wedding invitation (`undangan.apk`) or delivery receipt (`resi.apk`).
3. The victim installs it and grants SMS permission.
4. The malware reads incoming banking OTPs and forwards them to the attacker.
5. The attacker drains the victim's account.

**Why antivirus fails here:**

- No exploit is used. The victim installs the app and grants the permission voluntarily. From the OS perspective, nothing abnormal happened.
- Attackers repackage the APK for every campaign, so signature-based detection always lags.
- Warnings do not work. By the time a warning appears, the victim has been talking to the "officer" for twenty minutes and trusts them. Scammers pre-script the warning away: *"A warning will pop up, that's normal, just tap continue."*

Antivirus loses the trust battle, not the technical one. RONDA does not try to win that battle — it hands the decision to someone who already has the victim's trust.

### 1.3 Scope of the Project

**In-Scope (POC, deadline 21 August 2026):**

- Android native app (Kotlin), single APK, two selectable roles.
- Install-time detection: read install source + declared manifest permissions.
- Risk rule: sideloaded (non-Play Store) **AND** declares `READ_SMS` → flag as high risk.
- Guardian ↔ Protected device pairing via QR code, performed once during setup.
- On-device soft-block: a full-screen warning overlay that covers the flagged app whenever it is opened, using `PACKAGE_USAGE_STATS` + `SYSTEM_ALERT_WINDOW`.
- Real-time push alert from protected device to guardian device via Firebase Cloud Messaging (FCM).
- Guardian-triggered remote uninstall (opens the system uninstall dialog on the protected device).
- Benign test-sample APK that declares `READ_SMS` and does nothing, used for demos.

**Out-of-Scope (POC):**

- iOS. The scam is Android-specific; iOS blocks both the attack and any possible defense (see §7).
- Device Owner hard-block (`setPackageSuspended`). Requires provisioning on a new or factory-reset device. Documented as roadmap; the POC ships the soft-block instead (FR-8).
- Network exfiltration monitoring via `VpnService`. Roadmap; mocked in UI if shown at all.
- Defense against OS-level exploits, zero-days, or APT-grade malware. RONDA targets social-engineering install-and-grant scams only.
- Reading WhatsApp message content. Explicitly excluded — see §5 Security.
- Play Store publication, analytics dashboards, multi-guardian management, monetization.

---

## 2. Stakeholders

**Primary:**

| Role | Description |
|---|---|
| Guardian | Tech-literate trusted person (adult child, relative, neighbour). Installs and configures RONDA. Receives alerts and makes decisions. |
| Protected person | Elderly or non-technical user at risk. Consents to protection during setup. Owns the monitored device. |
| Development team | Builds the POC and presents at HackNusa. |

**Secondary:**

- HackNusa judges (Telkom University, Kaspersky) — evaluate technical soundness and social impact.
- Indonesian banks and e-wallet providers — indirect beneficiaries of reduced fraud losses.

**Communication plan:** Daily async check-in during the build sprint; a shared repo with `TODO.md` as the single source of active work.

---

## 3. Objectives and Goals

**Key objectives:**

1. Detect a malicious APK within 5 seconds of installation completing.
2. Deliver the guardian alert within 10 seconds of detection.
3. Achieve zero false positives on a set of 20 legitimate apps (10 Play Store, 10 legitimately sideloaded).
4. Produce a working two-device demo that a judge can trigger themselves.

**Success criteria:**

| Criterion | Target |
|---|---|
| Detection accuracy on known malicious sample | 100% |
| False positives on benign app set | 0 |
| End-to-end latency (install → guardian phone alerts) | < 15 s |
| Demo reliability | 5 consecutive successful runs |
| Proposal submitted | ✅ Done |

---

## 4. Functional Requirements

### FR-1 — Role selection at first launch

On first launch, the user chooses **Guardian** or **Protected**. One APK, one codebase, two behaviours. The role is stored locally and cannot be silently changed.

### FR-2 — Device pairing

- Guardian device generates a unique pairing token and displays it as a QR code.
- Protected device scans the QR code and registers the link in Firebase.
- Pairing requires physical co-presence. This is intentional — it prevents remote, non-consensual installation.
- The protected device displays a persistent indicator that it is being monitored, and by whom.

### FR-3 — Install-time detection (core)

- A `BroadcastReceiver` listens for `ACTION_PACKAGE_ADDED`.
- On trigger, the app reads:
  - **Install source** via `PackageManager.getInstallSourceInfo()`. Anything other than `com.android.vending` is a risk signal.
  - **Declared permissions** via `getPackageInfo(PackageManager.GET_PERMISSIONS)`. Presence of `READ_SMS` or `RECEIVE_SMS` is a risk signal.
- **Risk rule:** both signals present → HIGH RISK. One signal only → log, do not alert.
- Detection reads *declared* permissions from the manifest, not *granted* permissions. This means RONDA flags intent before the victim can grant anything.

### FR-4 — Guardian alert

- On HIGH RISK, the protected device writes an alert record (app name, package name, install source, flagged permissions, timestamp) to Firebase.
- Firebase pushes to the guardian's FCM token.
- The guardian device raises a high-priority notification that bypasses silent mode.

### FR-5 — Guardian response

- The guardian notification offers two actions: **Uninstall** or **Mark as safe**.
- **Uninstall** sends a command back to the protected device, which invokes `Intent.ACTION_DELETE` for the flagged package.
- The final confirmation tap happens on the protected device — Android does not permit silent uninstall without Device Owner privileges. This limitation is stated openly in the pitch.
- **Mark as safe** adds the package to a local allowlist.

### FR-6 — Protected-device UI

- Minimal by design. The protected person is not asked to make security judgements.
- Shows: monitoring status, paired guardian name, and (when relevant) a large high-contrast prompt confirming the uninstall the guardian requested.

### FR-8 — On-device soft-block (overlay)

- On HIGH RISK, the protected device begins monitoring the foreground app via `UsageStatsManager`.
- If the flagged package is brought to the foreground, RONDA draws a full-screen warning over it using `SYSTEM_ALERT_WINDOW`.
- The overlay states plainly that the app is suspected of stealing banking codes and that the guardian has been notified. It offers no "continue anyway" path — the only exits are Home and waiting for the guardian.
- The overlay runs entirely on-device and requires no network. If FCM delivery fails or the device is offline, the block still functions.
- Both permissions (`PACKAGE_USAGE_STATS`, `SYSTEM_ALERT_WINDOW`) must be granted manually through system Settings during guardian-assisted setup, with an on-screen explanation of why each is needed.
- The overlay is cleared when the guardian marks the app safe, or when the app is uninstalled.

**Known limitation, to be stated openly:** this is a soft-block. The underlying app is not frozen, and a determined user can dismiss the overlay and reopen the app. It is designed to hold for the minutes needed for the guardian to intervene, not indefinitely. Hard-block via Device Owner is roadmap.

### FR-9 — Test sample APK

A separate, harmless APK that declares `READ_SMS` and performs no action. Used to demonstrate detection without handling real malware. Real malware is never used in development or demos.

---

## 5. Non-Functional Requirements

### Performance

- Detection logic completes in < 2 s on a low-end device (2 GB RAM).
- Background receiver must survive OS battery optimization. Use `WorkManager` for deferred work; request battery-optimization exemption during setup with a clear explanation.
- Alert delivery target: < 10 s end to end.

### Security

These are hard rules. Any implementation that violates them is rejected regardless of feature benefit.

1. **RONDA must never request `READ_SMS`, `RECEIVE_SMS`, or any SMS permission.** RONDA detects apps that request SMS access; it must not request it itself. Requesting it would make RONDA indistinguishable from the malware it detects.
2. **RONDA must never use Accessibility Service.** Accessibility is the primary abuse vector used by Android banking trojans. Using it would replicate the attack pattern and would be correctly criticized by security judges. All required detection works through `PackageManager` alone.
3. **RONDA must never read message content** from WhatsApp or any other app. Detection happens at install time and does not require it.
4. **Data minimization.** Only package metadata is transmitted: package name, app label, install source, declared permissions, timestamp. No message content, contacts, location, files, or SMS.
5. **Consent is mandatory and visible.** The protected person consents during pairing and can see at any time that monitoring is active and who the guardian is. RONDA must not operate as hidden or covert monitoring — that is stalkerware, and it is out of scope permanently, not just for the POC.
6. **No unpairing without local confirmation.** Pairing changes require physical access to the protected device.
7. **Least privilege for blocking.** The soft-block uses `PACKAGE_USAGE_STATS` (which reveals only the foreground package name) and `SYSTEM_ALERT_WINDOW` (which draws on top). Neither can read screen content, keystrokes, or app data. Accessibility Service would achieve a stronger block but grants full screen-reading and input-injection capability, and is prohibited under rule 2. RONDA accepts a weaker block in exchange for a far smaller privilege footprint.
8. **The overlay must never impersonate a system dialog.** It is clearly branded as RONDA. Mimicking Android system UI is the technique used by overlay-based banking trojans and must not be reproduced.
9. **The overlay may only target packages the detection engine has flagged.** It must never be applied to arbitrary apps, and never as a general-purpose app blocker.
10. All Firebase traffic uses TLS. Pairing tokens are single-use and expire after 10 minutes.

### Usability

- Protected-device screens: minimum 20sp text, high contrast, no jargon, no more than one decision per screen.
- Guardian-device screens: information-dense is acceptable; the guardian is tech-literate.
- Setup must be completable by a guardian in under 3 minutes.
- Alert copy uses plain Indonesian, not security terminology.

### Availability

- Detection works fully offline. Only the alert delivery requires connectivity.
- If the protected device is offline at detection time, the alert is queued and sent when connectivity returns.

### Compliance

- Android permission model respected in full; no root, no privilege escalation, no OS modification.
- Aligned with Google Play policy on device and network abuse — notably the prohibition on covert monitoring, which §5.5 satisfies.
- UU PDP (Indonesian Personal Data Protection Law) alignment through data minimization and explicit consent.

---

## 6. Assumptions

1. Malware targeting banking OTPs must declare an SMS-reading permission in its manifest. This is enforced by the Android permission model and cannot be bypassed without an OS exploit.
2. Legitimate apps rarely declare `READ_SMS`. Google has restricted it on the Play Store since 2019, which is what makes the two-signal rule accurate.
3. The guardian has a reliable smartphone and internet connection.
4. Pairing happens in person (e.g. during a family visit).
5. A time gap exists between installation and the victim granting permissions — long enough for the guardian to intervene.
6. The victim is non-technical and will not attempt to circumvent a full-screen warning, even though doing so is technically possible.
7. The team can develop and test entirely on Android emulators; physical devices are needed only for the final on-site demo.

---

## 7. Constraints

| Constraint | Impact |
|---|---|
| **Deadline: 21 August 2026** (proposal + POC) | Forces scope cuts. Device Owner and VPN monitoring are deferred. |
| **Android cannot silently uninstall apps** without Device Owner | Final uninstall tap stays on the protected device. Disclosed openly rather than hidden. |
| **Android cannot suspend other apps** without Device Owner | POC ships a soft-block overlay instead. Hard-block is roadmap. |
| **Overlay is bypassable** (Home button, rapid reopen) | Positioned as a delay mechanism, not a lock. Stated openly rather than overclaimed. |
| **OEM skins restrict overlays** (Xiaomi, Oppo, Vivo) | `SYSTEM_ALERT_WINDOW` must be enabled manually; guardian handles this during setup. Test on at least one OEM-like configuration. |
| **iOS is architecturally impossible** | iOS apps cannot enumerate installed apps, detect installations, read others' permissions, or trigger uninstalls. Android-only is a hard requirement, not a preference. |
| **No team member owns an Android device** | Development on Android Studio emulators. Physical devices sourced before the October on-site final. |
| **Battery optimization** on OEM Android skins (Xiaomi, Oppo, Vivo) aggressively kills background services | Requires exemption request during setup; must be tested. |
| **No budget** | Restricted to free tiers: Firebase Spark plan, Android Studio, GitHub free. |

---

## 8. Dependencies

**External services:**

- **Firebase Cloud Messaging** — alert delivery. Free tier sufficient. *Single point of failure for the core differentiator.*
- **Firebase Firestore / Realtime Database** — pairing records and alert queue.

**Platform:**

- Android API 30+ (`getInstallSourceInfo()` requires API 30; the deprecated `getInstallerPackageName()` is the fallback for older versions).
- Android Studio, Kotlin, Gradle.

**Internal:**

- The test-sample APK must exist before end-to-end testing can begin. Build it during the detection sprint, not later.

---

## 9. Risks

| # | Risk | Impact | Likelihood | Mitigation |
|---|---|---|---|---|
| R1 | Background receiver killed by OEM battery optimization | High | High | `WorkManager` + battery-exemption request at setup. Test on emulator with restrictions enabled. |
| R2 | FCM setup consumes more time than budgeted | High | Medium | Build detection (Block 1) first — it demos standalone. FCM is Block 3. |
| R3 | Live demo fails on stage | High | Medium | Record a backup video. Rehearse 5 consecutive runs. |
| R4 | Judges challenge the Accessibility/stalkerware angle | Medium | High | §5 rules are the answer. Address it proactively in the pitch rather than waiting to be asked. |
| R5 | False positives on legitimately sideloaded apps | Medium | Medium | Two-signal rule, not one. Guardian allowlist. Test against 10 benign sideloaded apps. |
| R6a | Overlay permissions blocked or revoked by OEM power management | Medium | Medium | Verify permission state on every launch; alert guardian if revoked. Alerting (FR-4) still works without overlay. |
| R6b | Overlay work overruns and squeezes the FCM pipeline | High | Medium | Overlay is local and independent of FCM — build it after detection, and cut remote uninstall (D7) first if behind. |
| R6 | Scope creep (Device Owner, VPN, polished UI) | High | High | `TODO.md` gates active work. Anything not in §1.3 In-Scope is rejected until after the 30-finalist announcement. |
| R7 | Team unfamiliar with Kotlin/Android | Medium | Medium | Emulator setup on day 1–2, before feature work begins. |

---

## 10. Deliverables

| # | Deliverable | Target date | Status |
|---|---|---|---|
| D1 | Project proposal (submitted via HackNusa form) | 21 Aug 2026 | ✅ Submitted |
| D2 | Context documents (`README`, `PRD`, `SECURITY`, `ARCHITECTURE`, `USER_FLOW`, `PROMPT`, `DESIGN`, `TESTING`, `TODO`, `CHANGELOG`) | 12 Aug 2026 | In progress |
| D3 | Detection engine — install-time scanning + risk rule | 15 Aug 2026 | Not started |
| D4 | Test-sample APK | 15 Aug 2026 | Not started |
| D5 | Soft-block overlay (FR-8) | 17 Aug 2026 | Not started |
| D6 | QR pairing flow | 18 Aug 2026 | Not started |
| D7 | FCM alert pipeline | 20 Aug 2026 | Not started |
| D7b | Remote uninstall action | 20 Aug 2026 | Not started |
| D8 | Demo video (60 s, two devices side by side) | 21 Aug 2026 | Not started |
| D9 | Final presentation, Bandung | 3 Oct 2026 | Conditional on advancing |

**Cut order if behind schedule:** D7b → D5 → D6/D7. D3 is never cut — it is the product.

Note on ordering: D5 (overlay) is local and works with no network, so it is buildable and demoable independently of D6/D7. D6+D7 together carry the project's core differentiator (alerting the guardian) and should not both be cut.

---

## 11. Timeline

| Days | Phase | Output |
|---|---|---|
| 10–11 Aug | Setup | Android Studio, emulator (Pixel, API 33+), GitHub repo, context docs |
| 12–15 Aug | Block 1 — Detection | `ACTION_PACKAGE_ADDED` receiver, install-source + permission reading, risk rule, test-sample APK |
| 16–17 Aug | Block 2 — Soft-block | `UsageStatsManager` foreground polling, overlay screen, permission setup flow |
| 18–19 Aug | Block 3 — Pairing + alerting | Role selection, QR pairing, Firebase records, FCM integration, guardian notification |
| 20 Aug | Block 4 — Response | Remote uninstall trigger |
| 21 Aug | Demo | Recording, submission |

**Checkpoint (15 Aug):** installing the test-sample APK on the emulator produces a local detection notification. If this works, the majority of technical risk is retired.

**Checkpoint (17 Aug):** opening the test-sample APK on the emulator is covered by the RONDA warning overlay. This works offline and is demoable on its own.

**Checkpoint (19 Aug):** two emulators — installing on one causes the other to alert. This is the demo.

---

## 12. Budget

No budget. All tooling on free tiers: Android Studio, Firebase Spark plan, GitHub free.

Anticipated cost before the October final (conditional on advancing): two low-end physical Android devices for on-stage demo, approx. IDR 1,000,000 total. The two-device physical demo is the single highest-leverage presentation asset and should not be substituted with side-by-side emulators.

---

## Appendices

### Glossary

| Term | Definition |
|---|---|
| **APK** | Android Package — the installable app file format. |
| **Sideloading** | Installing an APK from outside the Play Store (via chat app, browser, file manager). |
| **Manifest** | `AndroidManifest.xml` — declares an app's permissions and components. Readable once the app is installed, before permissions are granted. |
| **Declared vs granted permission** | Declared = written in the manifest, visible at install. Granted = the user has actually approved it at runtime. RONDA reads the former. |
| **FCM** | Firebase Cloud Messaging — Google's push notification service. |
| **Device Owner** | An Android privileged mode (Android Enterprise) allowing app suspension and silent uninstall. Requires provisioning on a new or factory-reset device. Roadmap only. |
| **Soft-block** | Covering an app with a warning overlay so it cannot be used comfortably. The app is not frozen and the block is bypassable. |
| **Hard-block** | Genuinely suspending an app so it cannot launch at all. Requires Device Owner. |
| **`SYSTEM_ALERT_WINDOW`** | Permission to draw over other apps. Granted manually in Settings. |
| **`PACKAGE_USAGE_STATS`** | Permission to see which app is in the foreground. Reveals the package name only, not screen content. Granted manually in Settings. |
| **Accessibility Service** | An Android API granting broad screen-reading and input control. Heavily abused by banking trojans. **Explicitly prohibited in RONDA.** |
| **Guardian** | The trusted, tech-literate user who receives alerts and makes decisions. |
| **Protected person** | The at-risk user whose device is monitored, with their consent. |
| **Stalkerware** | Covert monitoring software installed without the subject's knowledge. RONDA is explicitly designed not to be this. |

### References

- Android Developers — `PackageManager.getInstallSourceInfo()`
- Android Developers — Permissions overview; SMS permission restrictions
- Google Play — Device and Network Abuse policy; Stalkerware policy
- Firebase Cloud Messaging documentation
- HackNusa 2026 challenge brief — Human-Centric Design track