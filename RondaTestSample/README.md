# RondaTestSample — bait APKs for testing RONDA's detection engine

A set of **harmless** APKs with a single job: to *declare* the signals RONDA reads
through `PackageManager`, and then **do nothing at all**. No networking code, no
SMS reading, no active service, no files written. This follows PRD **FR-9** —
real malware is never used for development or demos.

It is one project split into **product flavors**. Each flavor tests one group of
signals in isolation, and each has its own `applicationId` so they can all be
installed side by side on one phone.

The app names and on-screen text are in Indonesian on purpose — they imitate the
lures that actually reach elderly people in Indonesia. English glosses are given
in brackets.

## Flavor map

| Flavor | applicationId | App name | Declared signals | Combo | Score* | Band |
|---|---|---|---|---|---|---|
| `sms` | `com.ronda.testsample` | Undangan Pernikahan [Wedding Invitation] | `READ_SMS` + `INTERNET` | SMS_READ+INTERNET | ~85 | PERINGATAN [warning] |
| `accessibility` | `….accessibility` | Update Sistem [System Update] | `BIND_ACCESSIBILITY_SERVICE` + `SYSTEM_ALERT_WINDOW` | ACCESSIBILITY+OVERLAY | ~100 | DARURAT [emergency] |
| `notification` | `….notification` | Cek Resi Kilat [Quick Parcel Tracking] | `BIND_NOTIFICATION_LISTENER_SERVICE` + `INTERNET` | NOTIF_LISTENER+INTERNET | ~70 | PERINGATAN |
| `overlay` | `….overlay` | Senter Super [Super Flashlight] | `SYSTEM_ALERT_WINDOW` only | — | ~43 | RENDAH [low] |
| `deviceadmin` | `….deviceadmin` | Layanan Keamanan [Security Service] | `BIND_DEVICE_ADMIN` + no launcher icon | DEVICE_ADMIN+NO_LAUNCHER | ~88 | PERINGATAN |
| `dropper` | `….dropper` | Info Paket [Package Info] | `REQUEST_INSTALL_PACKAGES` + `INTERNET` | INSTALL_PKG+SRC_SIDELOAD | ~63 | PERINGATAN |

## Telling the six apart

The six APKs are deliberately **unlike each other**, because a tester who installs
them one after another easily mixes them up. Each flavor has its own icon,
colour, title and screen:

| Flavor | Icon | Colour | Screen |
|---|---|---|---|
| `sms` | heart | rose red | the wedding invitation of Andi & Sari |
| `accessibility` | download arrow | blue | "Versi 14.2.1 — pembaruan keamanan" [version 14.2.1 — security update] |
| `notification` | lightning bolt | orange | "Resi JX88412907 — paket dalam perjalanan" [tracking JX88412907 — parcel on its way] |
| `overlay` | flashlight | purple | "Mode terang — ketuk untuk menyalakan" [bright mode — tap to turn on] |
| `deviceadmin` | shield | dark green | "Perangkat terlindungi — status aktif" [device protected — status active] |
| `dropper` | cardboard box | brown | "2 paket menunggu" [2 parcels waiting] |

Most importantly: **the last line of every screen prints the flavor and its
signals**, for example `flavor: accessibility | ACCESSIBILITY + OVERLAY`. When in
doubt about which APK is open, read that line — do not guess from the app name.

Every screen is static and does nothing; only the text, colour and icon differ.
The warning "SAMPEL UJI RONDA — bukan malware" [RONDA TEST SAMPLE — not malware]
is printed on every flavor and must never be removed.

\* Scores assume the APK is **sideloaded** (installed with adb → the
`SRC_SIDELOAD` signal, ×1.25) and signed with a **self-signed** debug certificate
(`CERT_SELF_SIGNED`, ×1.15). Installed as if from the Play Store
(`-i com.android.vending`), the trust multiplier drops to ×0.45 and almost every
flavor falls below the threshold — exactly what we want to prove (detection is
not only about permissions, but also about where the app came from).

The guardian is called at a **score of 60 or more**. The `overlay` flavor sits
below the threshold on purpose, to prove that a single mid-weight capability on
its own does **not** raise a false alert.

## Why it is safe

- **Permissions are only declared in the manifest**, never requested at runtime.
- **Services and receivers are empty stubs** with no intent filter or metadata,
  so the system never binds them and the user cannot enable them
  (`AccessibilityStubService`, `NotifStubService`, `AdminStubReceiver`).
- **No networking code.** `INTERNET` is completely unused.
- The UI is a single static screen.

What keeps these APKs harmless is **the absence of code**, not the absence of
permission lines.

## Build

```bash
# JAVA_HOME must point to a JDK (e.g. the JBR bundled with Android Studio)
./gradlew assembleSmsDebug            # one flavor
./gradlew assembleDebug               # every flavor at once
```

APKs are written to:

```
app/build/outputs/apk/<flavor>/debug/app-<flavor>-debug.apk
```

## Install & test (one PROTECTED phone / emulator)

Sideloaded (triggers `SRC_SIDELOAD`, high score):

```bash
adb install -r -t app/build/outputs/apk/accessibility/debug/app-accessibility-debug.apk
```

As if from the Play Store (triggers `SRC_PLAY`, low score — for negative tests):

```bash
adb install -r -t -i com.android.vending \
  app/build/outputs/apk/accessibility/debug/app-accessibility-debug.apk
```

Open the flavor with no launcher icon (`deviceadmin`) through adb:

```bash
adb shell am start -n com.ronda.testsample.deviceadmin/com.ronda.testsample.MainActivity
```

Through the demo script (see `scripts/ronda help`), which builds the flavor
first and always installs the fresh APK:

```bash
scripts/ronda attack                          # flavor sms, sideloaded → alert
scripts/ronda attack accessibility            # sideloaded → DARURAT alert
scripts/ronda attack accessibility playstore  # as if from the Play Store → no alert
scripts/ronda attack sms --on rondee2         # onto the second protected phone
scripts/ronda victim                          # open the last bait → overlay covers it
```

## Adding a new test case

Signals that do not have their own flavor yet (e.g. `AUDIO`, `CAMERA`,
`LOCATION`, `CONTACTS`, `CALL`, `QUERY_PKGS`, `NAME_MIMIC`, `LEGACY_SDK`) can be
added in four steps:

1. `create("<name>") { dimension = "signal"; applicationIdSuffix = ".<name>" }`
   in `app/build.gradle.kts`.
2. `app/src/<name>/AndroidManifest.xml` — declare the signal.
3. `app/src/<name>/res/values/strings.xml` — `app_name`.
4. Stub components (if a service or receiver is needed).
5. Add the flavor to `FLAVORS` in `scripts/ronda` so `attack` and `victim` accept it.

The official weights and multipliers live in
`app/src/main/java/com/ronda/app/core/Signal.kt` in the main RONDA project.
