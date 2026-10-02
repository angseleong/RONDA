# RONDA — Project Report

*(Cover page — excluded from the 8–10 page count)*

**RONDA — Real-time On-Device Detection Agent**
*Pelindung Keluarga — a second pair of eyes for the people scammers target*

**Track:** Human-Centric Security
**Event:** HackNusa 2026 — Telkom University × Kaspersky
**Team:** Dhanes, Axeleon (Alek), Malik
**Repository:** https://github.com/angseleong/RONDA
**Platform:** Android native (Kotlin, Jetpack Compose), minSdk 30
**Date:** September 2026

---

# Chapter 1 — Introduction & Background

RONDA (Real-time On-Device Detection Agent) is an Android application that
addresses one of Indonesia's most damaging fraud vectors: malicious APKs
delivered over WhatsApp to elderly users. The Indonesia Anti-Scam Centre (IASC)
recorded **Rp9.1 trillion in losses across 432,637 reports** between November
2024 and January 2026 [1]. Rather than warning the victim who is already under
a scammer's influence, RONDA moves the security decision to a second, trusted
person — an adult child or guardian — who is not being manipulated.

## 1.1 The attack we are defending against

A scam that is now routine in Indonesia works like this. A stranger contacts an
elderly person on WhatsApp, posing as a courier, a bank officer, a civil servant,
or a wedding guest. They send a file — `Undangan Pernikahan.apk`, `resi.apk`,
`surat tilang.apk`. They stay on the line and talk the victim through installing
it. The victim grants SMS permission because the caller asks them to. The app
forwards incoming banking OTPs to the attacker, and the account is drained.

No software vulnerability is exploited anywhere in that sequence. The victim
installs the app. The victim grants the permission. From the operating system's
point of view, nothing abnormal has happened. This is why it works, and why the
existing defences do not.


## 1.2 Why the current answer fails

The industry's answer is a warning on the victim's screen, and it fails for a
reason that is behavioural rather than technical:

1. **The warning arrives too late in the relationship.** By the time the dialog
   appears, the victim has been speaking to the "officer" for twenty minutes and
   trusts them more than they trust an unexpected pop-up.
2. **Scammers pre-empt the warning.** The script includes the line *"a warning
   will appear, that is normal, just tap continue."* A warning the attacker has
   already explained away is not a defence; it is a step in their funnel.
3. **Signature detection always lags.** The APK is repackaged for every campaign,
   so hash- and signature-based antivirus is structurally behind.

Kaspersky's own finding frames the problem precisely: **64% of cyber incidents
originate in human error (Kaspersky, 2023) [3].** The failure is not in the phone. It
is in who the phone is asking.

## 1.3 The scale of the problem in Indonesia

| Finding | Figure | Source |
|---|---|---|
| Total digital fraud losses (IASC) | **Rp9.1 trillion** across 432,637 reports (Nov 2024 – Jan 2026) | OJK / IASC [1] |
| Total funds recovered / frozen by IASC | **Rp161 billion** — only **1.77%** of the Rp9.1T stolen | OJK / IASC [1] |
| APK-via-WhatsApp fraud specifically | **3,684 reports, Rp134 billion** — a top-10 reported method | OJK / Satgas PASTI [4] |
| **Average loss per APK victim** | **≈ Rp36.4 million** *(calculated: Rp134B ÷ 3,684 reports)* | derived from [4] |
| Elderly population (aged 60+) | **11.97% of population (≈34 million)** — an ageing society | BPS, 2025 [2] |
| Elderly owning mobile phones | **52.23%** (more than half of all elderly) | BPS, 2025 [2] |
| Elderly internet adoption rate | **34.13%**, surging **+7.7 percentage points** in one year | BPS, 2025 [2] |



Three conclusions follow, and they shape the entire design of RONDA:

- **Recovery does not work.** Under 2% of stolen funds come back. All meaningful
  value sits in the minutes *before* the transfer — which is exactly the window
  between installation and the victim granting permissions.
- **The reported figure is a floor, not a ceiling.** Elderly victims frequently
  do not know how to report, are ashamed to, or never connect the "invitation"
  they opened to the money that disappeared.
- **The exposed population is growing on two axes at once.** The number of
  elderly Indonesians is rising, and the share of them who are online is rising
  faster. Every newly-connected elderly user is an untrained target.

## 1.4 Relevance to the Human-Centric Security track

The track asks for *"UX design to make secure choices more intuitive and
accessible for everyone."* RONDA's reading of that brief is deliberately literal
and slightly contrarian.

Most entries in a human-centric track will try to make the victim a better
decision-maker: clearer warnings, better microcopy, gentler onboarding, security
literacy. We believe that approach has a ceiling that this particular attack sits
above. You cannot design a warning good enough to beat a human being who is on
the phone with the victim right now, coaching them past it.

So RONDA does not try to improve the victim's decision. **It moves the decision
to a different human being** — an adult child, a relative, a neighbour — who is
not under the scammer's influence and can be trusted to think clearly. The
human-centric insight is not "explain the risk better." It is *"the right person
is not holding this phone."*

The name carries the thesis. *Ronda* is the Indonesian practice of neighbours
taking turns keeping watch at night so that everyone else can sleep. Security as
a shared social responsibility, not an individual technical skill.

---

# Chapter 2 — Solution Overview & Market Differentiation

## 2.1 What RONDA does

RONDA is a single Android APK that runs in one of two roles, chosen once at first
launch and paired between two phones:

- **The protected phone** (the elderly parent) runs detection and on-device
  blocking. Its interface is deliberately minimal; it never asks its owner to
  make a security judgement.
- **The guardian phone** (the adult child) receives alerts, sees the evidence in
  plain language, and makes the decision.

The end-to-end sequence:

```
1. DETECT   An APK is installed on the protected phone. Within seconds RONDA
            reads its declared manifest permissions and its install source,
            and produces a risk score from 0 to 100 — entirely offline.

2. BLOCK    Scoring ≥ 60 (Peringatan & Darurat), RONDA covers the app with
            a full-screen warning every time it is opened. (Scores of 30–59 enter
            "Terpantau" — passively monitored on the guardian app without intrusive
            blocking, preventing alert fatigue). Works entirely offline.

3. ALERT    For scores ≥ 60, the verdict, score, and plain-Indonesian reasons
            are pushed to the guardian's "Perlu Diperiksa" queue.

4. DECIDE   The guardian chooses: request uninstall, or mark safe.

5. ACT      An uninstall request takes over the protected phone's screen and
            explains who asked and why, then opens the system dialog.
```

Critically, detection reads *declared* permissions from the manifest rather than
*granted* ones. RONDA therefore flags the app's **intent at install time — before
the victim has granted anything.** That is the window the whole product lives in.

## 2.2 Unique selling proposition

> **Everyone else warns the victim. RONDA is the only one that tells somebody else.**

This is not a feature difference; it is a category difference. Every deployed
defence in this market — Google's, the banks', the telcos' — terminates at a
dialog on the screen of the person who is currently being manipulated. RONDA
introduces a second human into the loop, and that human is unreachable by the
attacker.

A second differentiator sits underneath it: **RONDA scores rather than verdicts.**
A binary "malicious / safe" label from a system that sometimes cries wolf trains
guardians to ignore it. A 0–100 score with named reasons lets a quiet app read
quiet, which is what keeps a loud one credible.

A third differentiator is **ecosystem complementarity rather than competition.**
RONDA does not attempt to replace Google Play Protect, banking security, or telco
firewalls. Instead, it bridges the structural blind spots each of them faces:
- **Banks** cannot inspect external APKs outside their sandbox boundary.
- **Telcos** cannot inspect end-to-end encrypted WhatsApp payloads.
- **Google** does not know who the victim's trusted adult children are.

RONDA is designed as the collaborative "last-mile" protection layer that banks
(under POJK 12/2024 fraud mandates), telcos (under Komdigi directives), and
cybersecurity providers (such as Kaspersky) can adopt or bundle to safeguard
their mutual end-users before financial transactions happen.

## 2.3 Competitive analysis

| Player | What they do | Gap |
|---|---|---|
| **Google Play Protect — Enhanced Fraud Protection** (Indonesia, Feb 2025) [5] | Auto-blocks sideloaded installs requesting SMS/Accessibility permissions | Can be switched off; scammer's script pre-empts the prompt. **Notifies nobody but the victim.** |
| **Android Developer Verification** (Indonesia, 30 Sep 2026) [6] | Unverified installs trigger a 24-hour "advanced flow" | First wave covers **app stores only** — WhatsApp sideloading untouched until 2027. Verification proves *identity*, not safety. |
| **Android 17 Live Threat Detection** (2026) | On-device AI flags SMS-forwarding and overlay/accessibility abuse | Targets flagship hardware; Rp1–2 million handsets used by elderly Indonesians wait years, if ever. |
| **Bank apps** (BRImo, Livin', myBCA) | Block accessibility services; malware check at transaction time | Protects *that app* at *transaction time*. The OTP has already leaked. Warns the victim. |
| **Telcos** (Siscamling, SATSPAM) | AI filtering of calls and links at the network layer | Blind to what is installed on the handset. WhatsApp is E2E-encrypted; the APK never crosses their filter. |
| **Seraph Secure / Scammer Guardian (US)** | Paid services that notify family when an elderly user is under attack | **Proof the guardian model sells.** US-only, call-centric, absent from Indonesia. |

The gap is consistent: the industry builds better filters *for the victim*.
Nobody builds the channel *to somebody else*. RONDA occupies that column
alone — by design, not by accident.

## 2.4 Positioning against Google's 2026–2027 changes

The sharpest question a judge can ask is: *"Google is closing sideloading — why
does RONDA matter?"* Four answers, ordered from most temporary to most permanent:

1. **The window is open now.** The 30 September 2026 enforcement covers app
   stores. APKs delivered over WhatsApp — the actual vector — are not addressed
   until the 2027 global rollout, with no announced date for all install sources.
2. **Verified is not safe.** Verification binds an identity to an app. A
   syndicate capable of running a call centre can obtain an identity. Kaspersky's
   own malware analysis lead has stated that *"attackers will likely find ways to
   bypass verification."* RONDA reads what an app **can do**, not who signed it.
3. **The next scam needs no malicious APK at all.** The emerging "share screen"
   fraud uses AnyDesk or TeamViewer — legitimate, verified, Play Store apps that
   pass every Google filter. RONDA treats *"a remote-access app just appeared on
   Mum's phone"* as a signal and lets a human judge the context.
4. **Google will always ask the victim.** Even the new advanced flow asks *"is
   someone pressuring you to enable this?"* — of the person being pressured. As
   long as the decision lives on the victim's handset, the victim can be talked
   through it. RONDA moves the decision off that handset. No Android update can
   do this, because Google does not know who your son is.

> **Google is strengthening the door. RONDA changes who opens it.**

---

# Chapter 3 — Proof of Concept (PoC) Implementation

## 3.1 Repository and scale

**Repository:** https://github.com/angseleong/RONDA (Kotlin, Gradle, Android
Studio). At the time of writing: **58 Kotlin source files, ≈9,970 lines** in
`app/src/main`, **30 JVM unit tests**, 54 commits across 3 contributors.

The PoC is complete end-to-end: a judge can install a decoy APK on one phone and
watch the other phone alert, then act on it.

## 3.2 Delivered features

**1. Risk scoring engine — `core/RiskEvaluator.kt`, `core/Signal.kt`**
A pure, deterministic, fully offline scorer (no Android dependencies, no `Context`, no network) built on a two-axis model of **23 signals and 5 combinations**. Each signal is mapped to its **MITRE ATT&CK for Mobile** technique ID, verified against attack.mitre.org.

**Impact signals** — what the app is capable of:

| Signal | Weight | MITRE ID | Threat |
|---|---|---|---|
| `ACCESSIBILITY` | 45 | T1516 | Input Injection |
| `DEVICE_ADMIN` | 40 | T1626.001 | Device Admin Abuse |
| `SMS_READ` | 40 | T1636.004 | OTP Interception |
| `INSTALL_PKG` | 30 | — | Dropper Chain |
| `OVERLAY` | 30 | T1417.002 | GUI Input Capture |
| `NOTIF_LISTENER` | 30 | T1517 | Notification Hijack |
| `AUDIO` | 25 | T1429 | Audio Capture |
| `CALL` | 25 | T1616 | Call Control |
| `CONTACTS` | 20 | T1636.003 | Contact Harvesting |
| `CAMERA` | 20 | T1512 | Video Capture |
| `LOCATION` | 20 | T1430 | Location Tracking |
| `PHONE_STATE` | 15 | T1426 | System Discovery |
| `QUERY_PKGS` | 15 | T1418 | Software Discovery |
| `BOOT` | 10 | T1398 | Persistence on Boot |
| `INTERNET` | 10 | — | Exfiltration Channel |
| `FG_SERVICE` | 5 | T1541 | Foreground Persistence |

**Trust signals** — where it came from and how it presents itself:

| Signal | Multiplier | Meaning |
|---|---|---|
| `SRC_PLAY` | ×0.45 | Verified Play Store install |
| `SRC_KNOWN_STORE` | ×0.80 | Known third-party store |
| `SRC_SIDELOAD` | ×1.25 | Unknown sideload source |
| `CERT_SELF_SIGNED` | ×1.15 | Self-signed certificate |
| `NO_LAUNCHER` | ×1.25 | Hidden from launcher |
| `LEGACY_SDK` | ×1.15 | Targets old API level |
| `NAME_MIMIC` | ×1.20 | Mimics a known brand name |

**Intent combinations** — signal pairs that reveal attack intent no single permission expresses:

| Combination | Bonus | Attack Pattern |
|---|---|---|
| `SMS_READ + INTERNET` | +15 | Complete OTP theft path |
| `ACCESSIBILITY + OVERLAY` | +15 | Classic banking trojan |
| `NOTIF_LISTENER + INTERNET` | +15 | Notification exfiltration |
| `INSTALL_PKG + SRC_SIDELOAD` | +10 | Dropper self-replication |
| `DEVICE_ADMIN + NO_LAUNCHER` | +15 | Hides and resists removal |

---

**2. Plain-language explanation layer — `core/ReasonBuilder.kt`**
23 curated sentences (in Indonesian) translate each signal into its real-world consequence for the reader — *"This app can read all your SMS, including OTP codes from your bank"* rather than *"declares READ_SMS."* A score of 85 alone tells no one what to do; this layer is what makes the alert actionable.

**3. On-device soft-block — `overlay/OverlayService.kt`**
`UsageStatsManager` polls the foreground app every second. When a flagged package surfaces, RONDA draws a full-screen `SYSTEM_ALERT_WINDOW` with no "continue anyway" path. **Critically, this works with the network completely off** — the block cannot be defeated by disabling Wi-Fi.

**4. Pairing — `pairing/`, QR deep link**
The Rondor generates a 6-character code (alphabet excludes O/0, I/1, S/5, B/8 for phone-call readability) and a QR encoding `ronda://pair/{CODE}`. The Rondee types the code or scans the QR. **RONDA requests no camera permission** — scanning is handled by the OS default QR reader.

**5. Alert pipeline & guardian response — `alert/`**
Firebase Realtime Database carries alerts to the guardian in real time and routes commands back. The Rondor can **request uninstall** or **mark safe** (with a 10-second undo window) from their own phone.

**6. Initial scan of existing apps — `detection/DetectionService.kt`**
On first pairing, RONDA immediately scans all pre-installed non-system apps on the protected device. Threats installed *before* RONDA arrived are flagged at setup time, not on next launch.

**7. Multi-Rondee support — `ui/guardian/GuardianRepository.kt`**
A single Rondor can pair with and monitor multiple Rondees simultaneously. Alerts are keyed by `pairingId`, so one guardian phone can watch over an entire family.

**8. Asymmetric whitelisting — `detection/SafeAppStore.kt`**
Only the Rondor can mark an app as "Safe". The Rondee phone has no whitelist control. This asymmetry closes the social-engineering attack where a scammer on a call walks a victim through disabling their own protection.

**9. Persistent uninstall queue — `detection/PendingUninstallStore.kt`**
Because Android requires user confirmation to uninstall, a dismissed system dialog would normally lose the guardian's request. RONDA persists the request and re-prompts until the OS confirms the package is gone.

**10. Audit trail & history — `detection/ProtectedHistoryStore.kt`**
Both the Rondee and Rondor have a dedicated History tab recording every verdict, uninstallation, and safe-marking — giving families a transparent log of what was found and what was done about it.

## 3.3 Verification performed

- **30 JVM unit tests**, including a 16-case calibration table pinning the
  scoring engine to known verdicts, 8 cases covering the pairing deep-link
  parser, and 5 asserting every signal has a human-readable explanation.
- **Calibrated outcomes** the tests hold in place: genuine WhatsApp from the Play
  Store scores **45 (RENDAH — silent)**; a sideloaded, self-signed APK requesting
  SMS + Internet scores **85 (PERINGATAN — guardian alerted)**; a sideloaded
  accessibility + overlay app reaches **100 (DARURAT)**. The `overlay`-only decoy
  scores **43** and deliberately triggers nothing.
- The 0.4 diminishing-returns factor on secondary signals is the load-bearing
  design choice: naive summation would push any permission-heavy legitimate app
  straight to 100.
- **Benign decoy APKs (`RondaTestSample/`)**: Six product flavors were used for validation, each *declaring* one group of signals and doing nothing else: `sms`, `accessibility`, `notification`, `overlay`, `deviceadmin`, `dropper`. Each has its own icon, colour scheme, and on-screen text, printing the flavor and signals it exercises at the foot of its screen. **Real malware is never used in development or demonstration**, in line with requirement FR-9.

## 3.4 Scope decisions and engineering roadmap

| Scope Boundary | Why It's Bounded | Mitigation / Next Step |
|---|---|---|
| Physical hardware stress test | Emulator (Pixel 6, API 33) sufficient to prove logic; OEM battery kill needs per-device lab. | `WorkManager` + battery-optimization exemption already in place. OEM testing → Phase 1. |
| Production RTDB security rules | Open rules speed up demo; Firebase Auth integration is out of hackathon scope. | Full per-user rules documented in §5.4, ready to apply pre-launch. |
| Hard-block (app suspension) | Requires Device Owner — a distribution constraint, not a code limitation. | Soft-block (`SYSTEM_ALERT_WINDOW`) covers the same window; Device Owner path in roadmap. |
| Silent uninstall | Impossible without Device Owner, by OS design. | `PendingUninstallStore` re-prompts until OS confirms removal — no request is silently lost. |
| Large-scale field metrics | No real-world cohort yet; fabricating figures would be dishonest. | Validated against 6 decoy APKs (`RondaTestSample`). No claims beyond what data supports. |

---

# Chapter 4 — Technical Architecture & Feasibility

## 4.1 System overview

```
┌──────────────── PROTECTED PHONE ─────────────────┐
│  DetectionService (foreground)                    │
│    └─ registers InstallReceiver at runtime        │
│                                                   │
│  OS: ACTION_PACKAGE_ADDED                         │
│            ↓                                      │
│  SignalExtractor  ── PackageManager only          │
│    · getInstallSourceInfo()      → provenance     │
│    · getPackageInfo(GET_PERMISSIONS/SERVICES/     │
│      RECEIVERS/SIGNING_CERTIFICATES) → capability │
│            ↓                                      │
│  RiskEvaluator → Verdict(score, band, reasons)    │
│            ↓                                      │
│   score ≥ 60 ──┬── OverlayService (offline block) │
│                └── AlertRepository ──┐            │
└──────────────────────────────────────│────────────┘
                                       ↓
                            Firebase Realtime Database
                          alerts/{pairingId}/{alertId}
                        commands/{pairingId}/{commandId}
                                       ↓
┌──────────────── GUARDIAN PHONE ──────│────────────┐
│  GuardianAlertService (held-open listener)        │
│            ↓                                      │
│  High-priority notification → AlertDetailScreen   │
│            ↓                                      │
│  Uninstall / Mark safe → CommandRepository ───────┘
└───────────────────────────────────────────────────┘
```

## 4.2 The scoring model

```
impact = heaviest signal + 0.4 × Σ(remaining signals) + combo bonuses   → cap 100
trust  = product of all trust multipliers                → clamped 0.45 … 1.6
score  = min(100, round(impact × trust))
```

The mathematical architecture adapts the **CVSS specification** (an intrinsic impact score modulated by environmental/provenance multipliers), mapping permissions directly to **MITRE ATT&CK for Mobile** techniques:

- **Diminishing returns (0.4 factor):** A naive summation would push any complex, legitimate app to 100. Multiplying all secondary permissions by 0.4 ensures that the worst capability dominates while lesser permissions only reinforce.
- **Intent combos (+15 bonus):** Certain permissions are harmless in isolation but dangerous together. Pairs like `SMS_READ + INTERNET` (MITRE T1636.004 OTP exfiltration) or `ACCESSIBILITY + OVERLAY` (MITRE T1516 clickjacking) trigger an intent bonus that separates RONDA from simple permission checkers.
- **Trust multipliers (0.45 … 1.6×):** Provenance checked via `getInstallSourceInfo()`. Google Play Store origin applies a steep safety discount (0.45×), whereas sideloaded APKs (`SRC_SIDELOAD` 1.25×) signed with debug or self-signed certs (`CERT_SELF_SIGNED` 1.15×) heavily amplify the risk score.

### Score Bands & Action Mapping

| Band | Score | Target / UI Label | Protected Phone Action | Guardian Action |
|---|---|---|---|---|
| **AMAN** | 0–29 | Safe | None | Ignored completely |
| **RENDAH** | 30–59 | *Terpantau* (Monitored) | No overlay / unblocked | Listed passively in "Terpantau" tab (no noisy notifications, preventing alert fatigue) |
| **PERINGATAN** | 60–89 | *Perlu Diperiksa* | Full-screen persistent overlay | Standard push alert to review evidence and rule |
| **DARURAT** | 90–100 | *Perlu Diperiksa* (Critical) | Full-screen persistent overlay | High-priority alarm with sound and vector breakdown |

### Calibration & Proof of Calibration (Tested on JVM)

The model is calibrated and verified via unit tests against real-world profiles:
- **Case 1 (Legitimate app — WhatsApp via Play Store):** Declares 10 heavy permissions (SMS, Contacts, Camera, Location, Audio, Foreground). Calculated impact hits the 100 cap. Applied Play Store trust factor ($100 \times 0.45$) yields **Score 45 (`RENDAH`)** $\rightarrow$ classified as *Terpantau*, completely avoiding false-positive disruption.
- **Case 2 (Benign sideloaded game):** Internet access only + sideloaded self-signed cert. Impact is 10; trust is $1.44 \rightarrow$ **Score 14 (`AMAN`)** $\rightarrow$ proves sideloading alone never triggers false alarms.
- **Case 3 (WhatsApp Wedding Invitation / Resi Trojan):** Declares only `SMS_READ` (40) + `INTERNET` (10) + OTP combo bonus (15) = impact 59. Multiplied by sideload and self-signed provenance ($59 \times 1.44$) yields **Score 85 (`PERINGATAN`)** $\rightarrow$ immediate on-device block and guardian escalation.

Every verdict also emits a compact vector string for auditability, e.g. `RONDA:1.0/ACCESSIBILITY:45/OVERLAY:30/CMB:15/SRC:SIDELOAD/CERT:SELF=100`. Because the evaluator is pure Kotlin with no Android imports, it executes in under 2ms on low-end hardware.

## 4.3 Alert delivery: why RTDB and not FCM

The original design called for Firebase Cloud Messaging. It was replaced during
implementation for a concrete reason, not a stylistic one: **Google retired the
FCM legacy server key in June 2024.** A device can no longer push to another
device by itself, and the HTTP v1 API requires a backend to sign each request —
which means a Cloud Function and the paid Blaze plan.

RONDA instead writes to RTDB and the guardian holds an open listener socket.

- **Gains:** no backend, no billing plan, sub-second delivery, and one mechanism
  serving both directions (the `commands/` node is the same pattern reversed).
- **Costs, stated openly:** delivery only holds while `GuardianAlertService` is
  alive; an OEM that kills the foreground service delays alerts until RONDA is
  reopened. The permanent service notification is the visible price of having no
  backend. FCM is the correct upgrade once a backend exists.

**Offline resilience:** `setPersistenceEnabled(true)` queues alerts written while
the protected phone is offline and flushes them on reconnect. Telling the victim
to switch off mobile data does not suppress the alert — it only delays it. And
detection plus the soft-block never touch the network at all.

## 4.4 Data model

Alerts and commands are nested **under the pairing that owns them** rather than
held in one flat list. The guardian subscribes to a single node and receives only
its own family's data — no `orderByChild`, no `.indexOn`, and no path from which
one family could read another's alerts.

| Node | Key fields |
|---|---|
| `pairings/{code}` | `guardianDeviceId`, `protectedDeviceId`, `status` (pending→active→revoked), `createdAt`, `expiresAt` (+10 min) |
| `alerts/{pairingId}/{alertId}` | `packageName`, `appLabel`, `installSource`, `flaggedPermissions`, `status`, `timestamp` (**server clock**, never the phone's) |
| `commands/{pairingId}/{commandId}` | `alertId`, `action`, `packageName`, `createdAt`, `executedAt` |

Two deliberate decisions worth noting. `executedAt` means *delivered and
surfaced*, never *"the app is gone"* — removal is only confirmed when the OS
broadcasts `ACTION_PACKAGE_REMOVED`. A guardian must never be told an app was
removed on the strength of a command that was merely received. And `packageName`
is duplicated onto the command rather than read back from the alert, because
acting on the wrong package would be unrecoverable.

## 4.5 Feasibility and constraints

| Constraint | How RONDA handles it |
|---|---|
| Android cannot silently uninstall | The guardian *requests*; the protected phone shows who asked and why; the user taps. Disclosed openly, never hidden. |
| Android cannot suspend apps without Device Owner | PoC ships a soft-block overlay, positioned as a **delay** that buys the guardian minutes — not a lock. |
| Overlay is dismissible with Home | Accepted and stated. The guardian is the defence; the overlay is the alarm. |
| OEM battery management kills services | Battery-optimisation exemption requested during setup with plain-language justification; permissions re-verified on every launch. |
| iOS impossible | iOS cannot enumerate installed apps, detect installations, read another app's permissions, or trigger uninstalls. Android-only is a hard architectural fact, not a preference. |
| No budget | Firebase Spark free tier, no server, no cloud model, no paid APIs. |

Notably, RONDA requires **no root, no OS modification, no custom ROM, and no
privilege escalation.** It runs on a stock Android image today.

---

# Chapter 5 — Security Architecture & Intellectual Property Potential

## 5.1 The governing principle

A security app for vulnerable users must be held to a higher standard than the
malware it detects, because it is asking for the same trust the attacker is.
RONDA's project rules treat the following as **disqualifying violations**, not
preferences:

1. **RONDA must never request any SMS permission.** It detects apps that read
   SMS; requesting it itself would make RONDA indistinguishable from the malware.
2. **RONDA must never use an AccessibilityService.** Accessibility is *the*
   primary abuse vector for Android banking trojans. Using it would reproduce the
   attack pattern. All detection is achieved through `PackageManager` alone.
3. **RONDA must never read message content** from WhatsApp or anywhere else.
4. **Data minimisation.** Only package metadata is transmitted: package name, app
   label, install source, declared permissions, timestamp. No message content, no
   contacts, no location, no files, no SMS.
5. **Consent is mandatory and visible.** The protected person consents during
   pairing and can always see that monitoring is active and who the guardian is.

The cost is accepted deliberately: an AccessibilityService would give a far
stronger block. **RONDA takes the weaker block in exchange for a dramatically
smaller privilege footprint.** The entire detection surface is
`PackageManager` — a read-only API that reveals no screen content, no keystrokes,
and no application data.

## 5.2 Threat model

| Threat | Mitigation |
|---|---|
| **RONDA becomes stalkerware** | Pairing requires physical co-presence and a locally-typed code. A permanent indicator shows the protected person they are guarded and by whom. No remote unpairing. No covert mode — permanently out of scope, not merely deferred. |
| **Attacker coaches victim to uninstall RONDA or revoke overlay permission** | Permission state is re-verified on every launch; loss of capability is itself reportable to the guardian. Long-term, the banking "second approval" path (§6.5) does not depend on the victim's handset at all. |
| **Overlay impersonates a system dialog** | Forbidden. The overlay is explicitly branded RONDA. Mimicking system UI is the banking-trojan technique and must not be reproduced. |
| **Overlay abused as a general app blocker** | It may only target packages the detection engine has flagged. Never arbitrary apps. |
| **Malicious deep link pairs a device silently** | `ronda://pair` **pre-fills the field only.** Claiming a code always requires an explicit tap on the protected device. A link arriving by itself can never pair a phone. |
| **Pairing code brute-forced / reused** | Single-use, 10-minute expiry, enforced by database rules rather than by the client. |
| **False positives train guardians to ignore alerts** | Two-axis scoring with diminishing returns, an explicit quiet band, a guardian allowlist, and a calibration test suite that fails the build if a known-safe case drifts upward. |

## 5.3 Compliance posture

- **Android permission model** respected in full; no root, no escalation.
- **Google Play device-and-network-abuse and stalkerware policies** — satisfied by
  the visible-consent requirement in §5.1.5.
- **UU PDP (Indonesian Personal Data Protection Law)** — addressed through data
  minimisation by design and explicit, revocable consent at pairing.
- **Android Developer Verification** — RONDA must itself register as a verified
  developer before the 2027 global rollout; this is on the 90-day plan.

## 5.4 Known weakness, disclosed

The current Firebase Realtime Database rules are **PoC-grade and must not ship.**
There is no authentication binding a node to a device, so anyone who guessed a
pairing code could read that family's alerts. The honest fix is Firebase
Anonymous Auth, storing `guardianDeviceId` / `protectedDeviceId` as `auth.uid`,
and rewriting the rules as
`auth.uid === data.child('guardianDeviceId').val()`.

We state this plainly because a security product that overstates its own posture
has already failed its users. It is the first item of post-hackathon work.

## 5.5 Intellectual Property Potential

The defensible idea is not the permission scanner — prior art is extensive. It
is the **architecture of guardian-mediated intervention**: routing a device-local
security verdict to a second pre-paired human device, blocking locally while
awaiting that human's decision, and executing their response back on the
originating device. Combined with the two-axis scoring model and consequence-
language explanation layer, this forms a coherent and non-obvious system.

Pragmatic IP strategy for a student team: (1) **Copyright registration** with
DJKI — inexpensive and automatic. (2) **Trademark registration** of "RONDA" —
the brand is the asset users recognise. (3) **Defensive publication** via this
report and the public repository, preventing a third party from patenting the
mechanism and excluding us. Long term, the defensible moat is not the algorithm
but the **network of consenting, pre-paired guardian relationships** and the
aggregated detection signal flowing across it.

---

# Chapter 6 — Scalability & Deployment Readiness

## 6.1 Infrastructure requirements

RONDA is deliberately close to serverless. Today: **no backend, no cloud model,
no paid API** — Firebase Realtime Database on the free Spark tier, with all
detection and blocking performed on-device.

| Stage | Devices | Infrastructure | Est. monthly cost |
|---|---|---|---|
| PoC (today) | < 10 | RTDB Spark (free) | Rp0 |
| Pilot | 1,000–20,000 | RTDB Blaze + Anonymous Auth + FCM via Cloud Functions | Low, usage-based |
| Production | 200,000+ | Firestore or sharded RTDB, regional (asia-southeast2), monitoring | Scales with active pairings |

Payload sizes are tiny — an alert is a few hundred bytes of metadata, written
only when a risky install actually occurs, not continuously. The scaling profile
is therefore driven by concurrent listener connections rather than data volume,
which is the specific reason migrating alert delivery to FCM matters at scale:
it removes the held-open socket per guardian.

## 6.2 Performance characteristics

Design targets from the requirements, with current status stated honestly:

| Metric | Target | Status |
|---|---|---|
| Detection latency (install → verdict) | < 2 s on a 2 GB device | Scoring is pure in-memory arithmetic over ~23 signals; no network, no I/O beyond `PackageManager` |
| Alert delivery (detection → guardian notified) | < 10 s | Sub-second in emulator testing; **not yet measured on physical hardware over mobile data** |
| False positives on legitimate apps | 0 | Held by a 16-case calibration suite; a **100-app field test is the first post-hackathon task** |
| Battery impact | Negligible | Detection is broadcast-driven, not polling. `UsageStatsManager` polling runs only while an app is actually flagged. |

We deliberately publish no measured accuracy, latency or retention figures,
because none exist yet outside the emulator. Fabricating them would undermine
the one thing a security product cannot afford to lose.

## 6.3 Deployment blockers and how each is handled

| Blocker | Plan |
|---|---|
| **Play Store review of `SYSTEM_ALERT_WINDOW` + `PACKAGE_USAGE_STATS`** | Both are core-functionality justified, with a demonstration video and full stalkerware-policy compliance. Fallback: distribution through an operator or bank partner. |
| **Developer verification (Indonesia, live now; global 2027)** | Register as a verified developer immediately — also a prerequisite for any bank conversation. |
| **OEM background-service termination (Xiaomi/Oppo/Vivo)** | Battery exemption at setup; a heartbeat so the guardian sees *"Mum's phone last seen 3 days ago"*; long-term, pre-load via an operator bundle. |
| **Stalkerware perception** | Addressed proactively in §5.2 rather than defensively when challenged. |
| **Firebase single point of failure** | Detection and blocking already work fully offline. The transport layer is to be abstracted so it can be relocated onto a partner's infrastructure — which banks and telcos typically require anyway. |

## 6.4 Roadmap

| Phase | Window | Scope |
|---|---|---|
| **0 — PoC** | Complete | Detection, soft-block, pairing, alerting, guardian response |
| **1 — Trustworthy MVP** | Oct 2026 – Mar 2027 | Physical devices (Xiaomi/Oppo/Vivo); 100-app false-positive study; Play Store listing; developer verification |
| **2 — Second signals** | Q2–Q3 2027 | Remote-access/screen-share detection; new device-admin; accessibility enabled for sideloaded app — **no new permissions** |
| **3 — Guardian as second approval** | 2027–2028 | With bank partner: guardian holds out-of-pattern transfers for 30 min — a kill switch pressed by someone who is not being manipulated |
| **4 — Signal network** | 2028+ | Opt-in anonymised detection metadata as early-warning feed for IASC, banks, and Kaspersky |

## 6.5 Commercial deployment

**The elderly user never pays.** The protected phone shows no price and no
upgrade prompt — anything monetisable on that screen is something a scammer can
exploit (*"pak, bayar dulu biar aman"*). The consumer app stays free for one
guardian-to-one-protected, permanently: it is the distribution engine, not the
revenue.

Revenue comes from institutions that already absorb the loss today:

| Model | Payer | Structure |
|---|---|---|
| **B2B2C licence** *(primary)* | Banks / e-wallets | Per protected device per year. Anti-fraud budget and mandate already exist under **POJK 12/2024**, which obliges financial institutions to run an anti-fraud strategy. |
| **Operator bundling** | Telcos | Revenue share within family/elderly plans. Komdigi has publicly asked *all* operators to ship anti-scam features "as an application or other security system" — and Telkomsel's Siscamling proves the VAS billing rails exist. |
| **Cyber-insurance requirement** | Insurers | Personal cyber policies already sell in Indonesia from ~Rp60–150k/year covering social engineering and malware. Every claim that does not occur is insurer margin. |

The unit economics are favourable and simple to state: against an average
**Rp36.4 million loss per reported APK case**, a per-device annual licence in the
tens of thousands of rupiah means a bank breaks even by preventing roughly **one
incident per two thousand protected devices.**

**Partnership targets,** each approached with a problem they have already
acknowledged publicly: banks and e-wallets (POJK 12/2024); **OJK / IASC /
Satgas PASTI** for legitimacy and a regional pilot; **Komdigi** for its elderly
digital-literacy programme; **telcos** under the Komdigi anti-scam mandate;
insurers; and **Kaspersky**, whose renewed BSSN memorandum of understanding
(April 2026) explicitly covers public-awareness initiatives.

For elderly people without a tech-literate child — the obvious objection — the
answer is in the product's name: RT/RW volunteers, Karang Taruna members or
posyandu cadres act as guardians for several neighbours, which the multi-parent
support in Phase 1 enables directly.

## 6.6 Next 90 days

1. Register as a verified Android developer; acquire 2–3 low-end physical devices.
2. Run the 100-app false-positive study and publish the results in the repository
   — this is the first document any bank will ask for.
3. Complete Phase 1 features; target a Play Store listing by December 2026.
4. Run a 50–100 person community pilot in Bandung via posyandu lansia, with
   student volunteers as backup guardians.
5. Open three conversations: OJK Regional West Java (the province with the
   highest IASC report count), one digital bank or insurtech, and Kaspersky.

Success at day 90 is defined as: one institutional letter of support, one paid
pilot scheduled, and real precision figures we are able to quote.

---

## Closing

Sixty-four percent of cyber incidents begin with human error — because the
decision lands on the person least equipped to make it, at the worst possible
moment. RONDA does not try to train that person out of it. It scores the app,
covers it, and hands the decision to someone the scammer cannot reach.

Not a better warning. A second pair of eyes.

---

## References

[1] OJK / Indonesia Anti-Scam Centre (IASC). *Statistik Penanganan Penipuan
Digital.* Data per 14 Januari 2026. Dilaporkan oleh CNBC Indonesia & Jawapos,
Januari 2026. Dapat diakses melalui https://iasc.ojk.go.id.

[2] Badan Pusat Statistik (BPS). *Statistik Penduduk Lanjut Usia 2025.*
Jakarta: BPS, 2025. Mencakup data proporsi lansia (11,97%), penggunaan telepon
seluler (52,23%), dan akses internet (34,13%) di kalangan penduduk usia 60+.

[3] Kaspersky. *"Redefining the Human Factor in Cybersecurity."* Kaspersky
Human Factor Survey Report, 2023. Tersedia di https://kaspersky.com. Laporan
menyimpulkan bahwa 64% insiden siber bersumber dari human error.

[4] OJK / Satgas PASTI. *Waspada Modus Penipuan File APK melalui WhatsApp.*
Siaran pers dan laporan Satgas PASTI, 2024–2025. Tersedia melalui
https://sipasti.ojk.go.id.

[5] Google. *"Google expands Enhanced Fraud Protection for Android to
Indonesia."* Google Security Blog, Februari 2025. Mencakup pemblokiran otomatis
instalasi sideload yang meminta izin SMS/Aksesibilitas.

[6] Google / Android. *"Android Developer Verification — Indonesia enforcement
begins September 30, 2026."* android.com & Google Developer Blog, 2026.
Tersedia di https://android.com/developer-verification.
