# Product

<!-- impeccable:product-schema 1 -->

## Platform

android

## Users

- **Guardian** (HP Penjaga): a tech-literate adult — usually the adult child — who installs RONDA, pairs both phones, receives alerts and decides what happens to a flagged app. Often in another city; pairing typically happens over a phone call.
- **Protected person** (HP Orang Tua): an elderly or non-technical parent whose phone is monitored with their consent. They are never asked to make a security judgement. They are the person a scammer is already talking to.

## Product Purpose

RONDA (Real-time On-Device Detection Agent) protects non-technical Android users from social-engineering malware delivered through WhatsApp — the `undangan.apk` / `resi.apk` scam that steals banking OTPs. It detects a malicious sideloaded APK at install time, soft-blocks it on the protected phone, and routes the alert to the paired guardian instead of warning the victim. Success: detection within seconds, guardian alerted within ~10 s, zero false positives, a two-device demo a judge can trigger. Built for HackNusa 2026 (Telkom University × Kaspersky), Human-Centric Design track.

## Positioning

Antivirus warns the victim; the victim has already been socially engineered and taps "continue". RONDA hands the decision to someone the scammer cannot reach. The mechanism is guardian-mediated intervention, not enforcement: the overlay is a delay, the guardian is the defence.

## Operating Context

- Single APK, two roles chosen once at first launch (write-once). Role and pairing state live in SharedPreferences; the screen shown is a function of that state (no NavGraph).
- Pairing: guardian shows a 6-character code + QR; protected person types the code (camera is never required). The guardian's first name travels in the pairing record so the protected phone can say who is guarding it ("Dijaga oleh Rina").
- Detection + soft-block run fully offline. Alerting goes through Firebase RTDB (held-open listener, not FCM). Commands come back the same way.
- Android cannot silently uninstall: the guardian's "uninstall" opens the system dialog on the protected phone, where the final tap happens. Stated openly, never hidden.
- Demo path: pairing code `DEMO01` serves a five-app fixture (`FakeGuardianRepository`) with no network.
- Development on emulators (Pixel 6 / API 33); physical low-end devices for the on-site final.

## Capabilities and Constraints

- Risk score 0–100 with bands AMAN (0–29) / RENDAH (30–59) / PERINGATAN (60–89) / DARURAT (90–100), shown under exactly those names (Safe / Low / Warning / Danger in English). Guardian threshold is 60. Each signal has a plain-Indonesian consequence sentence; combos replace their members.
- Guardian decisions: uninstall (request; protected confirms) or mark safe (confirmed, undoable for 10 s). Override notice when the protected person reopened a flagged app.
- Protected permissions requiring a trip to system Settings: draw-over-apps, usage access, notifications, battery exemption. Re-checked on every launch.
- **Hard security rules (violation = disqualification):** never request SMS permissions; never use AccessibilityService; never read message content; overlay must be branded RONDA and never mimic a system dialog; overlay only covers packages the engine flagged; monitoring is visible and consensual (never stalkerware); transmit package metadata only.
- Protected screens: ≥20 sp text, high contrast, no jargon, at most one decision per screen; no destructive or pairing-change buttons.
- Guardian screens may be information-dense.
- Tech: Kotlin, Jetpack Compose Material 3, Coroutines/Flow, minSdk 30, AndroidX + Firebase only. Fonts must ship in the APK (offline demo).
- Languages: Indonesian (default) and English, selectable at first launch and in guardian settings. Decided in this pass.
- Disconnect: local only (RTDB rules do not allow writing `revoked`); requires local confirmation. Not offered on protected screens in this pass (open decision — DESIGN.md forbids pairing changes there, USERFLOW.md mentions it).

## Brand Commitments

- Name: **RONDA**, wordmark set in the app's own type; "Pelindung Keluarga" tagline on the overlay.
- Visual authority: `docs/DESIGN.md` (team-authored, binding): Duolingo structural DNA re-tuned to "trusted guardian" — tactile 3D-shadow buttons, thick 2.5 dp borders, 20 dp cards, Nunito, forest green / danger red / trust blue, light + dark (deep navy, never black), custom stroke icons, no emoji, no gradients/glass.
- Flow authority: `docs/USERFLOW.md` (splash → language → intro → role → permissions/identity → pairing → role homes; guardian bottom nav Alerts / History / Settings).
- Voice: plain Indonesian, "Anda" to the protected person, second person to the guardian with the protected person's nickname ("HP Ibu"), consequence over capability, no security jargon.

## Evidence on Hand

- Real explanation catalogue: `app/src/main/res/values/strings_capabilities.xml`.
- Demo fixture with realistic Indonesian scam apps: `FakeGuardianRepository` (Senter Super Terang, Info BCA Mobile, Cek Resi Kilat, WhatsApp, Sudoku Offline).
- No screenshots, testimonials, or metrics exist; none may be fabricated.

## Product Principles

1. The victim is never the decision-maker; the guardian is. Every screen respects which of the two is holding the phone.
2. Zero false positives beats zero misses: quiet apps must read quiet, so loud ones stay credible.
3. Say the consequence, not the capability. If a word would not appear in a WhatsApp message to a friend, it does not belong.
4. Honest limits, stated in the UI: soft-block, protected person taps the final "Hapus", local-only disconnect.
5. Consensual and visible: the protected phone always shows that it is guarded and by whom.

## Accessibility & Inclusion

Protected-side users are elderly: ≥20 sp body text, 56 dp primary targets, high contrast in both themes, one action per screen, large-print pairing code readable aloud (alphabet excludes O/0, I/1, S/5, B/8). TalkBack labels merge score + band into one announcement.
