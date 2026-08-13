# RONDA Architecture

## 1. Package Structure

```
com.ronda.app/
├── MainActivity.kt              # Single Activity, hosts Compose NavGraph
├── RondaApp.kt                  # Application class (FCM init, WorkManager)
│
├── detection/
│   ├── DetectionService.kt      # Foreground Service, registers InstallReceiver at runtime
│   ├── InstallReceiver.kt       # BroadcastReceiver for ACTION_PACKAGE_ADDED (runtime only, not manifest)
│   └── RiskEvaluator.kt         # Reads install source + permissions, returns risk level
│
├── overlay/
│   ├── OverlayService.kt        # Foreground Service drawing SYSTEM_ALERT_WINDOW
│   └── ForegroundAppMonitor.kt  # Polls UsageStatsManager for current foreground app
│
├── pairing/
│   ├── PairingRepository.kt     # Reads/writes pairing data to Firebase RTDB
│   └── QrCodeUtils.kt           # Generate and parse QR content
│
├── alert/
│   ├── AlertRepository.kt       # Writes alert records to Firebase RTDB
│   ├── FcmService.kt            # Extends FirebaseMessagingService, handles incoming pushes
│   └── CommandHandler.kt        # Processes guardian commands (uninstall, mark-safe)
│
└── ui/
    ├── navigation/
    │   └── NavGraph.kt          # Single NavHost, routes based on saved role
    ├── onboarding/
    │   └── RoleSelectionScreen.kt
    ├── guardian/
    │   ├── GuardianHomeScreen.kt
    │   └── AlertDetailScreen.kt
    └── protected/
        └── ProtectedHomeScreen.kt
```

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

### `pairings/{pairingId}`

| Field               | Type    | Description                                  |
|----------------------|---------|----------------------------------------------|
| `guardianDeviceId`   | String  | UUID generated on guardian device             |
| `guardianFcmToken`   | String  | FCM token of the guardian device              |
| `protectedDeviceId`  | String  | UUID generated on protected device            |
| `protectedFcmToken`  | String  | FCM token of the protected device             |
| `status`             | String  | `"pending"` → `"active"` → `"revoked"`       |
| `createdAt`          | Long    | Server timestamp (ms)                         |
| `expiresAt`          | Long    | `createdAt + 600000` (10 min, for pending)    |

**Rules:**
- Pairing token is single-use. Once status becomes `"active"`, it cannot return to `"pending"`.
- Tokens with status `"pending"` older than 10 minutes are treated as expired.

### `alerts/{alertId}`

| Field               | Type    | Description                                  |
|----------------------|---------|----------------------------------------------|
| `pairingId`          | String  | Links to the pairing record                  |
| `packageName`        | String  | e.g. `"com.penipu.undangan"`                 |
| `appLabel`           | String  | Human-readable name, e.g. `"Undangan Nikah"` |
| `installSource`      | String  | e.g. `"com.whatsapp"`, `"manual"`            |
| `flaggedPermissions` | List    | e.g. `["READ_SMS", "RECEIVE_SMS"]`           |
| `status`             | String  | `"pending"` → `"uninstalled"` or `"safe"`    |
| `timestamp`          | Long    | Server timestamp (ms)                         |

### `commands/{commandId}`

| Field               | Type    | Description                                  |
|----------------------|---------|----------------------------------------------|
| `pairingId`          | String  | Links to the pairing record                  |
| `alertId`            | String  | Links to the alert that triggered this        |
| `action`             | String  | `"uninstall"` or `"mark_safe"`               |
| `executedAt`         | Long    | Null until protected device processes it      |

## 4. FCM Payload Structure

**Alert to Guardian** (sent from protected device via Firebase):
```json
{
  "to": "<guardian_fcm_token>",
  "priority": "high",
  "data": {
    "type": "new_alert",
    "alertId": "alert_abc123",
    "appLabel": "Undangan Pernikahan",
    "packageName": "com.penipu.undangan"
  }
}
```

**Command to Protected** (sent from guardian action):
```json
{
  "to": "<protected_fcm_token>",
  "priority": "high",
  "data": {
    "type": "guardian_command",
    "commandId": "cmd_xyz789",
    "action": "uninstall",
    "packageName": "com.penipu.undangan"
  }
}
```

## 5. UI Screens per Role

### Onboarding (Both roles)
1. **RoleSelectionScreen** — Choose Guardian or Protected. Stored in local SharedPreferences. Cannot be changed without app reinstall.

### Guardian
1. **GuardianHomeScreen** — Shows pairing QR code (if unpaired) or paired status + alert list.
2. **AlertDetailScreen** — Shows flagged app info + two buttons: **Uninstall** / **Mark Safe**.

### Protected
1. **ProtectedHomeScreen** — Shows monitoring status, paired guardian name. Minimal UI, large text, high contrast.
2. **WarningOverlay** (not a Compose screen) — Full-screen `SYSTEM_ALERT_WINDOW` drawn by `OverlayService`. Branded as RONDA. No "continue" button. Only exits: Home button or guardian marks safe.

## 6. Permission Setup Flow

These permissions cannot be requested via the normal runtime permission dialog. The app must guide the user to the system Settings screen.

| Permission             | How to request                                  | When              |
|------------------------|--------------------------------------------------|-------------------|
| `SYSTEM_ALERT_WINDOW`  | `Settings.ACTION_MANAGE_OVERLAY_PERMISSION`      | During setup      |
| `PACKAGE_USAGE_STATS`  | `Settings.ACTION_USAGE_ACCESS_SETTINGS`          | During setup      |
| `POST_NOTIFICATIONS`   | Standard runtime permission (API 33+)            | During setup      |

The app checks permission state on every launch. If any required permission is missing, the app shows a setup screen guiding the user to re-enable it.
