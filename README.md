# RONDA

**Real-time On-Device Detection Agent** — an Android app that catches malicious sideloaded APKs the moment they are installed, and sends the decision to a trusted guardian instead of the person the scammer is already talking to.

HackNusa 2026 · Human-Centric Security track · Team: Dhanes, Alek, Malik

> *Not a better warning. A second pair of eyes.*

---

## The problem

A routine scam in Indonesia: a stranger on WhatsApp sends `Undangan Pernikahan.apk` (a "wedding invitation"), stays on the phone, and walks the victim — usually an elderly parent — through installing it and granting SMS access. The app forwards the bank's OTP codes and the account is drained. APK scams over WhatsApp alone cost **Rp134 billion across 3,684 reports**, about Rp36 million per victim, and less than 2 % of scam losses are ever recovered (OJK/IASC).

Every existing defence ends in a warning on the victim's screen — and the scammer's script already says *"a warning will appear, just tap continue."* The decision lands on the person least able to make it, at the worst possible moment.

## What RONDA does

One APK, two roles, picked during setup:

- **Rondee** — the protected phone (the parent's). It detects and blocks, and is never asked to make a security judgement.
- **Rondor** — the guardian's phone (an adult child, a relative, a neighbourhood volunteer). It receives the alert, sees the reasons, and decides.

```
RONDEE (protected)                                     RONDOR (guardian)
1. DETECT  APK installed → score 0–100, on-device, offline, seconds
2. BLOCK   score ≥ 60 → full-screen RONDA warning over the app, every time it opens
3. ALERT   ─────────── score + plain-language reasons ───────────►
4.                                                      DECIDE  Remove  /  Mark safe
5. ACT     ◄───────────────────── command ─────────────────────
           "Your guardian asked for this app to be removed"
           → Android's uninstall dialog → app gone → Rondor sees "removed"
```

RONDA reads the permissions an app *declares* in its manifest — not the ones already granted — so it flags intent at install time, before the victim has granted anything.

## Features

**Detection & blocking (Rondee, fully offline)**
- Install-time detection through `PackageManager` only: install source, declared permissions, services, receivers, signing certificate
- A 0–100 risk score from 23 signals and 5 intent combinations, each mapped to MITRE ATT&CK for Mobile; bands AMAN / RENDAH / PERINGATAN / DARURAT; the guardian is involved from 60
- An initial scan of every app already installed, run after each pairing, plus "Scan again" on demand
- A RONDA-branded full-screen overlay over flagged apps (`SYSTEM_ALERT_WINDOW` + `PACKAGE_USAGE_STATS`); Back and taps are absorbed, Home is the only way out
- Protection restored by itself after a restart or an app update — no need to open RONDA
- Alerts written while offline are queued and delivered on reconnect

**Guardian (Rondor)**
- A live alert list with the score, the band and consequence sentences in plain Indonesian ("this app can read Mum's SMS…")
- Remove (the Rondee confirms in Android's dialog) or Mark safe (confirmed, undoable for 10 seconds — undo re-covers the app on the Rondee)
- History that keeps every ruling, including across reinstalls
- One Rondor can guard several Rondees
- An offline marker, so an empty list is never mistaken for "all clear"

**Pairing & setup**
- A 6-character code with a QR (`ronda://pair/CODE`) and a live 10-minute countdown; an expired code offers "make a new code" without starting over. No camera permission is needed — the code can be read out over a phone call
- Animated splash → language → intro → role → pairing, with a back arrow on every step
- Either side can disconnect; the other side is told at once. A Rondee (and a Rondor whose last Rondee is gone) starts setup over, and a former Rondee keeps covering the apps it flagged
- A 4-step permission wizard on the Rondee, one permission per screen

**Feedback**
- A toast for every change: pairing, scan finished, new alert, app removed or cleared, request sent, disconnect
- The RONDA mark on every notification, with a sound for each situation: danger, warning, resolved, info
- Tactile buttons that dip under the thumb with a haptic tick

## Hard rules

These are non-negotiable (PRD §5, [AGENTS.md](AGENTS.md)); breaking one would make RONDA indistinguishable from the malware it detects.

- Never declares or requests an SMS permission
- Never uses an Accessibility Service
- Never reads message content from WhatsApp or any other app
- Transmits package metadata only: package name, app label, install source, declared permissions, timestamp
- The overlay is visibly branded RONDA, never imitates a system dialog, and covers only packages the engine flagged
- Monitoring is consensual and visible: the Rondee always shows that it is guarded, and by whom

## Tech stack

| Layer | Choice |
|---|---|
| Platform | Android native, Kotlin, minSdk 30, compile/target SDK 37 |
| UI | Jetpack Compose, Material 3, a custom design system (Nunito, light + dark) |
| Async | Coroutines + Flow |
| Backend | Firebase Realtime Database with disk persistence — no server of our own |
| QR | ZXing core, generation only |
| Build | Gradle 9.5 wrapper, AGP 9.3.1, Kotlin 2.2.10, version catalog in `gradle/libs.versions.toml` |
| Languages | Indonesian (default) and English |

Why a Realtime Database listener instead of FCM, and the rest of the design: [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md).

## Getting started

### Requirements

- Android Studio (its bundled JDK is used for Gradle)
- Android SDK with an emulator image, API 30 or newer (development used Pixel 6 / API 33)
- A Firebase project with **Realtime Database** enabled. `app/google-services.json` is committed; to use your own project, replace it and apply the rules in [ARCHITECTURE.md §7](docs/ARCHITECTURE.md)

### Build

Neither the JDK nor the SDK is on the PATH by default; export both first:

```bash
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
export ANDROID_HOME="$HOME/Library/Android/sdk"

./gradlew :app:assembleDebug            # build the debug APK
./gradlew :app:installDebug             # build and install on a running device
./gradlew :app:testDebugUnitTest        # JVM unit tests
./gradlew :app:lintDebug                # Android Lint
```

### Try it without a second phone

`scripts/ronda fixture` puts the Rondor emulator straight into the **`DEMO01`** demo pairing: the guardian UI is then served from a built-in five-app fixture, with no network and no second device. (DEMO01 cannot be reached from the UI — Rondor codes are random — so the script writes the state through `run-as`; debug builds only.)

## Demo script: `scripts/ronda`

A driver for the two-emulator simulation. The first phone (`Pixel_6`) plays the Rondee, the second (`RONDA_Guardian`) the Rondor, and an optional third (`RONDA_Rondee2`) a second Rondee. Override the AVD names with `RONDA_PROTECTED_AVD`, `RONDA_GUARDIAN_AVD` and `RONDA_PROTECTED2_AVD`.

```bash
scripts/ronda demo              # from nothing: boot, install, reset, pair, sideload a bait APK
scripts/ronda victim            # open the bait → the RONDA overlay covers it
scripts/ronda scenario          # list every use-case scenario
scripts/ronda scenario uc02-realtime
```

| Command | What it does |
|---|---|
| `up` / `install` / `reset` / `perms` / `open` | Boot the emulators, install RONDA, clear its data, grant permissions through adb, open the app |
| `role` / `pair` | Walk both phones through onboarding, then pair them by reading the code off the Rondor |
| `attack [flavor] [playstore\|not_playstore] [--on rondee2]` | Build and install a bait APK (`sms`, `accessibility`, `notification`, `overlay`, `deviceadmin`, `dropper`) |
| `victim [flavor] [--on rondee2]` | Open the bait and check the overlay covered it |
| `add-rondee [--auto]` | Bring up a third phone as a second Rondee; without `--auto` it stops after install so setup can be shown by hand |
| `scenario [id]` | Run one scenario from [docs/USE_CASES.md](docs/USE_CASES.md) end to end, with pauses where a person has to tap and a ✓/✗ check at the end |
| `fixture` | Put the Rondor into the offline `DEMO01` demo (five sample apps, no network) |
| `status` / `logs` / `shot` | Role and pairing per phone, combined RONDA logcat, screenshots of every phone |

To prove the two-signal rule, install the bait as if from the Play Store — it scores below the threshold and no alert is sent:

```bash
scripts/ronda attack sms playstore
```

The bait APKs are harmless by construction — they declare signals and do nothing. See [RondaTestSample/README.md](RondaTestSample/README.md).

## Project layout

```
app/src/main/java/com/ronda/app/
├── core/          RiskEvaluator, Signal, Verdict — pure Kotlin, no Android imports
├── detect/        SignalExtractor — reads apps through PackageManager
├── detection/     DetectionService, InstallReceiver, flagged / safe / history stores
├── overlay/       OverlayService, ForegroundAppMonitor
├── pairing/       RoleStore, PairingRepository, QrCodeUtils
├── alert/         Alerts and commands over RTDB, GuardianAlertService, CommandHandler
├── ui/            Compose screens: onboarding, guardian, protectedrole, setup, components, theme
├── MainActivity   single Activity; the screen is a function of stored state
├── RondaServices  which services a phone runs + StartupReceiver (boot, self-update)
└── RondaNotifications  channels, status-bar mark, per-situation sounds
RondaTestSample/   the six bait APK flavors
scripts/ronda      the emulator demo driver
docs/              product, architecture, use cases, strategy, pitch material
```

## Documentation

| Document | What it covers |
|---|---|
| [docs/PRD.md](docs/PRD.md) | Product requirements, scope, security rules |
| [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) | Package layout, RTDB schema, alert delivery, Firebase setup |
| [docs/USE_CASES.md](docs/USE_CASES.md) | Every flow and edge case, each with its `scripts/ronda scenario` id |
| [docs/USERFLOW.md](docs/USERFLOW.md) | Screen-by-screen flow per role |
| [DESIGN.md](DESIGN.md) · [docs/DESIGN.md](docs/DESIGN.md) | Design system and UI guidance |
| [docs/STRATEGY.md](docs/STRATEGY.md) | Partnerships and monetisation |
| [docs/PROJECT_REPORT.md](docs/PROJECT_REPORT.md) | The written project report |
| [docs/PRESENTATION_DECK.md](docs/PRESENTATION_DECK.md) · [docs/finalscript.md](docs/finalscript.md) | Pitch deck material and the video script |
| [docs/TODO.md](docs/TODO.md) | Task log |

## Known limitations

Stated openly, as in the app itself:

- **Soft-block, not a lock.** The overlay can be left with Home; it holds for the minutes the guardian needs. A real lock needs Device Owner (roadmap).
- **The final uninstall tap is the Rondee's.** Android does not let any app remove another silently.
- **Alert delivery depends on `GuardianAlertService` staying alive.** An OEM that kills it delays alerts until it runs again (on boot or when RONDA opens). FCM is the upgrade once there is a backend.
- **The Firebase rules are PoC-grade** and must not ship — there is no authentication binding a node to a device yet (fix: Anonymous Auth; see ARCHITECTURE.md §7).
- **Tested on emulators only** (Pixel 6 / API 33) so far; low-end physical phones are next.
