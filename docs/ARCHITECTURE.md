# RONDA Architecture

## 1. Package Structure

```
com.ronda.app/
├── MainActivity.kt              # Single Activity, routes on saved role + pairing state
├── RondaApp.kt                  # Application class, enables RTDB disk persistence
├── Permissions.kt               # The three permissions and how to request each
├── RtdbExt.kt                   # Coroutine adapters for the Realtime Database SDK
│
├── detection/
│   ├── DetectionService.kt      # Foreground Service, registers InstallReceiver at runtime
│   ├── InstallReceiver.kt       # BroadcastReceiver for ACTION_PACKAGE_ADDED (runtime only, not manifest)
│   ├── RiskEvaluator.kt         # Reads install source + permissions, returns risk level
│   ├── RiskResult.kt            # Detection result + RiskLevel enum
│   └── FlaggedAppStore.kt       # Packages flagged HIGH RISK; handoff to the soft-block
│
├── overlay/
│   ├── OverlayService.kt        # Foreground Service drawing SYSTEM_ALERT_WINDOW
│   └── ForegroundAppMonitor.kt  # Polls UsageStatsManager for current foreground app
│
├── pairing/
│   ├── RoleStore.kt             # Role (write-once), device id, pairing id
│   ├── PairingRepository.kt     # Reads/writes pairing data to Firebase RTDB
│   └── QrCodeUtils.kt           # Pairing code generation, validation, QR rendering
│
├── alert/
│   ├── Alert.kt                 # Alert model
│   ├── AlertRepository.kt       # Writes and observes alert records in Firebase RTDB
│   ├── SeenAlertStore.kt        # Alert ids already notified, prevents duplicate buzzing
│   ├── GuardianAlertService.kt  # Guardian-side RTDB listener → high-priority notification
│   └── CommandHandler.kt        # Block 4 — processes guardian commands (uninstall, mark-safe)
│
└── ui/
    ├── onboarding/
    │   └── RoleSelectionScreen.kt
    ├── guardian/
    │   ├── GuardianPairingScreen.kt   # Shows the pairing code + QR, waits for claim
    │   ├── GuardianHomeScreen.kt
    │   └── AlertDetailScreen.kt
    ├── protectedrole/
    │   └── ProtectedPairingScreen.kt  # Enters the code read out by the guardian
    ├── setup/
    │   └── SetupScreen.kt             # Permission guidance, protected device home
    └── theme/
```

**Two deviations from the original plan, both deliberate:**

- **No `NavGraph.kt`.** With five screens and a linear flow, the destination is a
  pure function of role and pairing state. A NavHost would add a second source of
  truth to keep in sync with SharedPreferences for no gain (AGENTS.md §3,
  "Simplicity over Abstraction"). Revisit if the screen count grows.
- **`ui/protectedrole/` not `ui/protected/`.** `protected` is a Kotlin hard
  keyword and cannot be used as a package name without backticks at every import.

## 2. Component Communication Flow

```
┌─────────────────── PROTECTED DEVICE ───────────────────┐
│                                                         │
│  DetectionService (foreground, always running)           │
│       └── registers InstallReceiver at runtime          │
│                                                         │
│  OS broadcasts ACTION_PACKAGE_ADDED                     │
│           │                                             │
│           ▼                                             │
│  InstallReceiver ──► RiskEvaluator                      │
│                       │ reads:                          │
│                       │  - getInstallSourceInfo()       │
│                       │  - getPackageInfo(GET_PERMISSIONS)
│                       ▼                                 │
│              Risk == HIGH?                              │
│              (sideloaded + READ_SMS)                    │
│               │              │                          │
│            NO │           YES│                          │
│            (log)             ▼                          │
│                     ┌───────────────┐                   │
│                     │ AlertRepository│──── writes ────┐ │
│                     └───────────────┘                 │ │
│                              │                        │ │
│                     ┌────────▼────────┐               │ │
│                     │ OverlayService  │               │ │
│                     │ (blocks app UI) │               │ │
│                     └─────────────────┘               │ │
└───────────────────────────────────────────────────────│─┘
                                                        │
                                              Firebase RTDB
                                                        │
┌─────────────────── GUARDIAN DEVICE ───────────────────│─┐
│                                                       │  │
│                     FCM push notification ◄───────────┘  │
│                              │                           │
│                     ┌────────▼────────┐                  │
│                     │  FcmService     │                  │
│                     │  (shows notif)  │                  │
│                     └────────┬────────┘                  │
│                              │                           │
│                     ┌────────▼────────┐                  │
│                     │ AlertDetail     │                  │
│                     │ [Uninstall]     │                  │
│                     │ [Mark Safe]     │                  │
│                     └────────┬────────┘                  │
│                              │                           │
│                     CommandHandler ──── writes ──► RTDB  │
│                                                          │
└──────────────────────────────────────────────────────────┘
```

## 3. Firebase Realtime Database Schema

Alerts and commands are **nested under the pairing that owns them**, rather than
kept in one flat list keyed by id. The guardian then subscribes to a single node
and receives only its own family's data — no `orderByChild` query, no `.indexOn`
rule, and no path from which one family can read another's alerts.

### `pairings/{pairingCode}`

The pairing code *is* the key, so the protected device goes straight to the node
after the code is typed. No lookup, no index.

| Field               | Type    | Description                                   |
|----------------------|---------|-----------------------------------------------|
| `guardianDeviceId`   | String  | UUID generated on guardian device              |
| `protectedDeviceId`  | String  | UUID generated on protected device, null while pending |
| `status`             | String  | `"pending"` → `"active"` → `"revoked"`        |
| `createdAt`          | Long    | Client timestamp (ms)                          |
| `expiresAt`          | Long    | `createdAt + 600000` (10 min, for pending)     |

**Rules:**
- Pairing code is single-use. Once status becomes `"active"`, it cannot return to `"pending"` — enforced by the database rules in §7, not by the client.
- Codes with status `"pending"` older than 10 minutes are treated as expired.
- Code alphabet excludes `O/0`, `I/1`, `S/5`, `B/8` so it survives being read aloud over a phone call.

### `alerts/{pairingId}/{alertId}`

| Field               | Type    | Description                                  |
|----------------------|---------|----------------------------------------------|
| `packageName`        | String  | e.g. `"com.penipu.undangan"`                 |
| `appLabel`           | String  | Human-readable name, e.g. `"Undangan Nikah"` |
| `installSource`      | String  | e.g. `"com.whatsapp"`, `"manual"`            |
| `flaggedPermissions` | List    | e.g. `["READ_SMS", "RECEIVE_SMS"]`           |
| `status`             | String  | `"pending"` → `"uninstalled"` or `"safe"`    |
| `timestamp`          | Long    | `ServerValue.TIMESTAMP` — server clock, never the phone's |

### `commands/{pairingId}/{commandId}` — Block 4

| Field               | Type    | Description                                  |
|----------------------|---------|----------------------------------------------|
| `alertId`            | String  | Links to the alert that triggered this        |
| `action`             | String  | `"uninstall"` or `"mark_safe"`               |
| `executedAt`         | Long    | Null until protected device processes it      |

## 4. Alert Delivery — RTDB listener, not FCM

The original plan called for FCM. It was replaced during Block 3 for a reason
that is not a matter of taste: **Google shut off the FCM legacy server key in
June 2024.** A device can no longer push to another device on its own, and the
HTTP v1 API requires a backend to sign the request. The remaining FCM path is a
Cloud Function, which needs the Blaze billing plan.

What RONDA does instead:

```
PROTECTED DEVICE                RTDB                 GUARDIAN DEVICE

InstallReceiver
  └─► AlertRepository ──write──► alerts/{pairingId}/{alertId}
                                        │
                                        │ value event, held-open socket
                                        ▼
                                 GuardianAlertService (foreground)
                                        │  filters ids in SeenAlertStore
                                        ▼
                                 NotificationManager
                                 IMPORTANCE_HIGH + CATEGORY_ALARM
                                        │  tap
                                        ▼
                                 MainActivity → AlertDetailScreen
```

**What this buys:** no backend, no billing plan, sub-second delivery, and one
mechanism serving both directions — Block 4's `commands/` node is the same
pattern with the roles reversed.

**What it costs, stated openly:** delivery only holds while
`GuardianAlertService` is alive. An OEM that kills the foreground service delays
alerts until RONDA is next opened. FCM survives that, and is the correct upgrade
once there is a backend to sign the sends. The persistent notification the
service must display is the visible price of not having one.

Offline behaviour on the protected side is covered by
`setPersistenceEnabled(true)` in `RondaApp`: an alert written while the phone has
no signal is queued on disk and flushed on reconnect. Telling the victim to
switch off mobile data does not suppress the alert, it only delays it.

## 5. UI Screens per Role

### Onboarding (Both roles)
1. **RoleSelectionScreen** — Choose Guardian or Protected. Stored in local SharedPreferences. Cannot be changed without app reinstall.

### Guardian
1. **GuardianPairingScreen** — Shown while unpaired. Large pairing code **and** a QR of `ronda://pair/{code}`.
2. **GuardianHomeScreen** — Paired status + alert list, newest first.
3. **AlertDetailScreen** — Flagged app evidence + two buttons: **Uninstall** / **Mark Safe**.

### Protected
1. **ProtectedPairingScreen** — Shown while unpaired. Types the code the guardian reads out.
2. **SetupScreen** — Doubles as the protected home: permission status and what RONDA is doing.
3. **WarningOverlay** (not a Compose screen) — Full-screen `SYSTEM_ALERT_WINDOW` drawn by `OverlayService`. Branded as RONDA. No "continue" button. Only exits: Home button or guardian marks safe.

**Why pairing does not use a camera.** The plan said "protected scans QR". It is
built the other way round — guardian *displays*, protected *types* — because the
guardian is usually not in the room. The realistic pairing session is a phone
call to a parent in another city, where a six-character code works and a camera
does not. The QR is kept for when the two phones are together, and any generic
scanner app can read it since the payload is a plain `ronda://pair/{code}` URI.
This also removes the `CAMERA` permission from a security app's manifest, and
makes the two-emulator demo possible at all.

## 6. Permission Setup Flow

These permissions cannot be requested via the normal runtime permission dialog. The app must guide the user to the system Settings screen.

| Permission             | How to request                                  | When              |
|------------------------|--------------------------------------------------|-------------------|
| `SYSTEM_ALERT_WINDOW`  | `Settings.ACTION_MANAGE_OVERLAY_PERMISSION`      | During setup      |
| `PACKAGE_USAGE_STATS`  | `Settings.ACTION_USAGE_ACCESS_SETTINGS`          | During setup      |
| `POST_NOTIFICATIONS`   | Standard runtime permission (API 33+)            | During setup      |

The app checks permission state on every launch. If any required permission is missing, the app shows a setup screen guiding the user to re-enable it.

Permissions are requested **per role**. A guardian phone needs only
`POST_NOTIFICATIONS`; it never runs detection or the overlay. A protected phone
needs all three. `MainActivity.refreshStatus()` starts `DetectionService` only on
a protected device and `GuardianAlertService` only on a paired guardian device.

## 7. Firebase Setup

RONDA needs a Firebase project with **Realtime Database** enabled. Creating the
project alone is not enough — the RTDB URL only lands in `google-services.json`
after the database itself exists, so create the database *first*, then download
the file to `app/google-services.json`.

### Database rules

Paste these into **Realtime Database → Rules**. They are what actually makes a
pairing code single-use: the client checks in `PairingRepository.claimPairing()`
exist to produce readable errors, not to enforce anything.

```json
{
  "rules": {
    "pairings": {
      "$code": {
        ".read": true,
        ".write": "!data.exists() || (data.child('status').val() === 'pending' && newData.child('status').val() === 'active')"
      }
    },
    "alerts": {
      "$pairingId": {
        ".read": true,
        ".write": true
      }
    },
    "commands": {
      "$pairingId": {
        ".read": true,
        ".write": true
      }
    }
  }
}
```

**These rules are POC-grade and must not ship.** Anyone who guesses a pairing
code can read that family's alerts, because there is no authentication to bind a
node to a device. The honest fix is Firebase Anonymous Auth, storing
`guardianDeviceId`/`protectedDeviceId` as `auth.uid`, and rewriting the rules
as `auth.uid === data.child('guardianDeviceId').val()`. That is a post-hackathon
task — but if a judge asks how RONDA secures its own backend, this paragraph is
the answer, not a claim that it is secure.

`app/google-services.json` is committed to the repository on purpose. It holds
client configuration, not secrets, and without it no teammate can build.
