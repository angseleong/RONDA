# RONDA - Complete User Flow

## 1. App Initialization & Role Setup
* **Splash Screen:** Displays RONDA logo.
* **Language Selection:** Choose Indonesian or English.
* **Onboarding Intro:** Brief value proposition.
* **Role Selection:** User selects Guardian or Protected. Stored locally.
* **Permissions Setup (Protected):** Guided flow to Android Settings to enable `SYSTEM_ALERT_WINDOW` and `PACKAGE_USAGE_STATS`.
* **Permissions Setup (Guardian):** Standard runtime prompt for `POST_NOTIFICATIONS`.
* **Identity Setup:** Input user name and profile details.

## 2. Device Pairing Flow
* **Guardian Device:** Enters `GuardianPairingScreen`. Displays 6-character code and QR code. Shows "Waiting for connection" state.
* **Protected Device:** Enters `ProtectedPairingScreen`. User types the 6-character code read aloud by the Guardian.
* **Verification:** Validates code against Firebase RTDB.
* **Success:** Both devices transition to their respective Home screens.

## 3. Guardian Flow (HP Penjaga)
* **Navigation:** Bottom tab bar with Alerts, History, Settings.
* **Home (Alerts Tab):** 
    * Top Bar: Profile picture and connected Protected device name.
    * State 1 (Initial Scan): Scanning indicator for existing apps.
    * State 2 (Empty): "All Clear" or safe status badge.
    * State 3 (Active): List of suspicious apps, newest first.
* **Alert Detail Screen:** 
    * Entry: Tapping a list item or tapping a high-priority push notification.
    * Display: Threat evidence (install source, flagged permissions).
    * Actions: **Uninstall** (Red CTA) or **Mark Safe** (Secondary CTA).
* **History Tab:** List of past alerts with outcomes (uninstalled or marked safe).
* **Settings Tab:** Profile edit, manage/disconnect Protected devices, theme toggle.

## 4. Protected Flow (HP Orang Tua)
* **Home Screen (`SetupScreen`):**
    * Design: Minimalist, zero user decisions.
    * Display: Large status card indicating active protection and Guardian's name.
    * Top Bar: Profile icon for editing identity or disconnecting from Guardian.
* **Threat State (Soft-Block Overlay):**
    * Trigger: User brings a flagged malicious app to the foreground.
    * Display: Full-screen warning overlay covers the scam app.
    * Constraints: No "continue" button, bypassable only via Home button.

## 5. Incident Response Flow (End-to-End)
* **Detection:** Malicious APK installed on Protected device.
* **Alert Delivery:** Guardian receives a high-priority notification.
* **Guardian Action:** Guardian opens notification, reviews evidence on `AlertDetailScreen`, and taps **Uninstall**.
* **Protected Execution (`UninstallPromptScreen`):** 
    * Protected device receives the command.
    * Screen takes over to explain the Guardian requested removal.
    * User confirms the Android system uninstall dialog.
* **Resolution:** Overlay clears automatically once the app is removed or the Guardian marks it safe.

## 6. Disconnect Flow
* **Trigger:** Initiated from Top Bar / Settings by either role.
* **Confirmation:** Requires local confirmation to prevent accidental unpairing.
* **Action:** Revokes pairing in RTDB, returns device to `RoleSelectionScreen` or `PairingScreen`.