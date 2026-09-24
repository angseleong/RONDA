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
originate in human error (Kaspersky, 2023).** The failure is not in the phone. It
is in who the phone is asking.

## 1.3 The scale of the problem in Indonesia

| Finding | Figure | Source |
|---|---|---|
| Digital fraud losses reported to IASC | **Rp9.1 trillion** across 432,637 reports (Nov 2024 – Jan 2026) | OJK / IASC |
| APK-via-WhatsApp fraud specifically | **3,684 reports, Rp134 billion** — a top-10 reported method | OJK / Satgas PASTI |
| **Average loss per APK report** | **≈ Rp36.4 million** *(our calculation: 134bn ÷ 3,684)* | derived |
| Funds recovered by IASC | Rp161 billion — **under 2%** of losses | OJK press release |
| Indonesians aged 60+ | **11.97% of the population (≈34 million)** — officially an ageing population | BPS, 2025 |
| Elderly who own a mobile phone / use the internet | 52.23% / **34.13%, up 7.7 points in one year** | BPS, 2025 |

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

2. BLOCK    Scoring ≥ 60, RONDA covers the app with a full-screen warning
            every time it is opened. Works with no network at all.

3. ALERT    The verdict, its score, and plain-Indonesian reasons are pushed
            to the paired guardian's phone.

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

## 2.3 Competitive analysis

| Player | What they do | Why the elderly-WhatsApp-APK case still gets through |
|---|---|---|
| **Google Play Protect — Enhanced Fraud Protection** (live in Indonesia, Feb 2025) | Auto-blocks sideloaded installs requesting SMS / Notification Listener / Accessibility | Can be switched off, and the scammer's script already covers the prompt. Four permissions only. **Notifies nobody but the victim.** |
| **Android Developer Verification** (Indonesia, from 30 Sep 2026) | Apps must be registered to a verified developer; unverified installs go through a 24-hour "advanced flow" | First wave covers **app stores only** — WhatsApp/browser sideloading is untouched until the 2027 global rollout. Verification proves *identity*, not safety. The anti-coercion dialog is shown *to the person being coerced*. |
| **Android 17 Live Threat Detection** (2026) | On-device AI flags SMS-forwarding and overlay/accessibility abuse | Reaches flagship devices first. The Rp1–2 million handsets actually used by elderly Indonesians wait years, or never. |
| **Bank apps: BRImo, Livin', myBCA** | Block accessibility services; malware checks when the banking app opens | Protects *that app*, at *transaction time*. The malware is already installed and the OTP has already leaked. Again: warns the victim. |
| **Telcos: Siscamling (Telkomsel), SATSPAM (Indosat)** | AI filtering of calls, SMS and links at the network layer | Network-side, so blind to what is installed on the handset. WhatsApp is end-to-end encrypted; the APK never crosses their filter. |
| **ScamShield (Singapore)** | Government app + hotline + banking "kill switch" | A strong model, but still self-service — the victim must decide to pull the switch. |
| **Seraph Secure / Scammer Guardian (US)** | Paid services that notify family when an elderly user appears to be under attack | **Proof that the guardian model sells.** US-only, call-centric, absent from Indonesia. |

The gap is consistent: the industry is building better filters *for the victim*.
Nobody is building the channel *to somebody else*. RONDA occupies that column
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
Studio). At the time of writing: **54 Kotlin source files, ≈8,380 lines** in
`app/src/main`, **30 JVM unit tests**, 41 commits across 3 contributors.

The PoC is complete end-to-end: a judge can install a decoy APK on one phone and
watch the other phone alert, then act on it.

## 3.2 Delivered features

**1. Risk scoring engine — `core/RiskEvaluator.kt`, `core/Signal.kt`**
A pure, deterministic, fully offline scorer with no Android dependencies, no
`Context`, and no network. It replaced the original binary rule (*sideloaded AND
requests SMS*) with a two-axis model of **23 signals and 5 combinations**:

- **16 impact signals** — what the app is capable of: `ACCESSIBILITY` (45),
  `DEVICE_ADMIN` (40), `SMS_READ` (40), `INSTALL_PKG` (30), `OVERLAY` (30),
  `NOTIF_LISTENER` (30), down to `FOREGROUND_SERVICE` (5). Each is mapped to its
  **MITRE ATT&CK for Mobile** technique ID (e.g. `ACCESSIBILITY` → T1516 Input
  Injection), verified against attack.mitre.org.
- **7 trust signals** — where it came from and how it presents: Play Store
  (×0.45), known store (×0.80), sideloaded (×1.25), self-signed certificate
  (×1.15), no launcher icon (×1.25), legacy target SDK (×1.15), brand-mimicking
  name (×1.20).
- **5 combinations** that express intent no single permission does:
  `SMS_READ + INTERNET` (+15, the complete OTP theft path),
  `ACCESSIBILITY + OVERLAY` (+15, the classic banking trojan),
  `NOTIF_LISTENER + INTERNET` (+15), `INSTALL_PKG + SRC_SIDELOAD` (+10, dropper),
  `DEVICE_ADMIN + NO_LAUNCHER` (+15, hides and resists removal).

**2. Plain-language explanation layer — `core/ReasonBuilder.kt`**
23 curated sentences translating each signal into its consequence for the reader,
in Indonesian, with zero jargon. *"This app can read all your SMS, including OTP
codes from your bank"* — not *"declares READ_SMS."* A score alone is useless to a
guardian; 85 does not tell anyone what to do.

**3. On-device soft-block — `overlay/OverlayService.kt`**
`UsageStatsManager` polls the foreground app; when a flagged package surfaces,
RONDA draws a full-screen `SYSTEM_ALERT_WINDOW` warning branded as RONDA. It
offers no "continue anyway" path. **It works with the network completely off.**

**4. Pairing — `pairing/`, QR deep link**
The guardian displays a 6-character code and a QR encoding `ronda://pair/{CODE}`;
the protected person types the code, or scans the QR to have it filled in
automatically. The code alphabet excludes O/0, I/1, S/5, B/8 so it survives being
read aloud on a phone call. RONDA requests **no camera permission at all.**

**5. Alert pipeline and guardian response — `alert/`**
Firebase Realtime Database carries alerts to the guardian and commands back.
Guardian actions: **request uninstall** or **mark safe** (undoable for 10s).

**6. Benign decoy APKs — `RondaTestSample/`**
Six product flavors, each *declaring* one group of signals and doing nothing at
all: `sms`, `accessibility`, `notification`, `overlay`, `deviceadmin`, `dropper`.
Each has its own icon, colour scheme, and on-screen text, and prints the flavor
and signals it exercises at the foot of its screen. **Real malware is never used
in development or demonstration**, in line with requirement FR-9.

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

## 3.4 Stated honestly: what is not done

| Not implemented | Status |
|---|---|
| Tested on physical hardware | Development ran on Android emulators (Pixel 6, API 33). Logic is proven; endurance under OEM battery management is not. |
| Production database rules | Current RTDB rules are **POC-grade and must not ship** — see §5.4. |
| Hard-block (app suspension) | Requires Device Owner provisioning on a factory-reset device. Roadmap. |
| Silent uninstall | Impossible on Android without Device Owner, by design. The final tap stays with the user, and we say so openly. |
| Measured field metrics | No real-world accuracy, latency or retention figures exist yet. **None are claimed in this report.** |

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

Bands: **AMAN** 0–29 · **RENDAH** 30–59 · **PERINGATAN** 60–89 · **DARURAT**
90–100. The guardian threshold is 60. The structure is adapted from CVSS — an
impact term modified by a provenance term — which makes it auditable and
explainable rather than an opaque model. Every verdict also emits a compact
vector string for logs and review, e.g.
`RONDA:1.0/ACCESSIBILITY:45/OVERLAY:30/CMB:15/SRC:SIDELOAD/CERT:SELF=100`.

Because the evaluator is pure Kotlin with no Android imports, it is unit-testable
on the JVM and runs in well under the 2-second budget on low-end hardware.

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

## 5.5 Intellectual property potential

**What is genuinely novel.** The defensible idea is not the permission scanner —
prior art there is extensive. It is the **architecture of guardian-mediated
intervention**: routing a device-local security verdict to a second, pre-paired
human device, blocking locally while awaiting that human's decision, and
executing their decision back on the originating device. Combined with the
two-axis scoring model whose combination bonuses encode attacker *intent*, and an
explanation layer that renders machine verdicts into consequence-language for a
non-expert decision-maker, this forms a coherent and non-obvious system.

**Realistic protection strategy.** Under Indonesian patent law (UU No. 13/2016),
computer programs *as such* are excluded from patentability, though
implementations producing a concrete technical effect may qualify. RONDA's
strongest claims would be framed as a technical system — the inter-device
verdict-and-command protocol with local enforcement pending remote authorisation
— rather than as a business method or a piece of software. Given a student team's
budget and the 12-month horizon, we assess the pragmatic order as:

1. **Copyright registration of the source code with DJKI** — inexpensive,
   immediate, and automatic on creation in any case.
2. **Trademark registration of "RONDA" and the wordmark** in the relevant Nice
   classes — the brand is the asset users will actually recognise.
3. **Defensive publication** of the architecture, via this report and the public
   repository, establishing prior art that prevents a third party from patenting
   the mechanism and excluding us from our own work.
4. **Patent consultation** on the inter-device protocol only if a commercial
   partner (§6.5) makes the filing cost rational. Formal patent counsel would be
   required before any filing; nothing here constitutes legal advice.
5. **Trade secret** over the tuned signal weights and calibration table, which
   represent the accumulated field knowledge that is hardest to replicate.

**The compounding asset.** Long term the defensible position is not the
algorithm — it is the **network of consenting, pre-paired guardian relationships**
and the aggregated detection signal that flows across it (§6.6).

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
| **0 — PoC** | Complete | Detection, soft-block, pairing, alerting, guardian response, decoy APKs |
| **1 — Trustworthy MVP** | Oct 2026 – Mar 2027 | Physical devices (Xiaomi/Oppo/Vivo, Android 11–14); 100-app false-positive study; initial device scan; multi-parent support; Play Store listing; developer verification |
| **2 — Second and third signals** | Q2–Q3 2027 | Remote-access/screen-share app installed; accessibility enabled for a sideloaded app; new device-admin; default SMS app changed — all still via `PackageManager`, **no new permissions** |
| **3 — Guardian as second approval** | 2027–2028 | With a bank partner: the guardian is notified of out-of-pattern transfers on an elderly account and can hold one for 30 minutes. A ScamShield-style kill switch, pressed by someone who is not being manipulated. |
| **4 — Signal network** | 2028+ | Opt-in, fully anonymised aggregate detection metadata (hashes, certificates, permissions, install source — **no personal data**) as an early-warning feed for IASC, banks and Kaspersky |

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
