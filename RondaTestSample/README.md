# RondaTestSample — APK umpan untuk menguji mesin deteksi RONDA

Kumpulan APK **tidak berbahaya** yang tugasnya cuma satu: *dideklarasikan* punya
sinyal yang dibaca RONDA lewat `PackageManager`, lalu **tidak melakukan apa pun**.
Tidak ada kode jaringan, tidak ada baca SMS, tidak ada service yang aktif, tidak
ada file yang ditulis. Ini sesuai PRD **FR-9** — malware asli tidak pernah dipakai
untuk pengembangan atau demo.

Semuanya satu proyek, dipisah dengan **product flavors**. Tiap flavor menguji satu
kelompok parameter secara terisolasi, dan tiap flavor punya `applicationId` sendiri
supaya bisa dipasang berdampingan di satu HP.

## Peta flavor

| Flavor | applicationId | Nama app | Sinyal yang dideklarasikan | Kombo | Skor* | Band |
|---|---|---|---|---|---|---|
| `sms` | `com.ronda.testsample` | Undangan Pernikahan | `READ_SMS` + `INTERNET` | SMS_READ+INTERNET | ~85 | PERINGATAN |
| `accessibility` | `…​.accessibility` | Update Sistem | `BIND_ACCESSIBILITY_SERVICE` + `SYSTEM_ALERT_WINDOW` | ACCESSIBILITY+OVERLAY | ~100 | DARURAT |
| `notification` | `…​.notification` | Cek Resi Kilat | `BIND_NOTIFICATION_LISTENER_SERVICE` + `INTERNET` | NOTIF_LISTENER+INTERNET | ~70 | PERINGATAN |
| `overlay` | `…​.overlay` | Senter Super | `SYSTEM_ALERT_WINDOW` saja | — | ~43 | RENDAH |
| `deviceadmin` | `…​.deviceadmin` | Layanan Keamanan | `BIND_DEVICE_ADMIN` + tanpa ikon launcher | DEVICE_ADMIN+NO_LAUNCHER | ~88 | PERINGATAN |
| `dropper` | `…​.dropper` | Info Paket | `REQUEST_INSTALL_PACKAGES` + `INTERNET` | INSTALL_PKG+SRC_SIDELOAD | ~63 | PERINGATAN |

\* Skor mengasumsikan APK **di-sideload** (dipasang lewat adb → sinyal
`SRC_SIDELOAD` ×1.25) dan ditandatangani sertifikat debug **self-signed**
(`CERT_SELF_SIGNED` ×1.15). Kalau dipasang seolah dari Play Store
(`-i com.android.vending`), pengali kepercayaan turun ke ×0.45 dan hampir semua
flavor jatuh ke bawah ambang — persis yang mau kita buktikan (deteksi bukan cuma
soal izin, tapi juga soal asal-usul).

Ambang penjaga dipanggil: **skor ≥ 60**. Flavor `overlay` sengaja di bawah ambang
untuk membuktikan satu kemampuan mid-weight sendirian **tidak** memicu false alert.

## Kenapa aman

- **Permission hanya dideklarasikan di manifest**, tidak pernah diminta saat runtime.
- **Service & receiver adalah stub kosong** tanpa intent-filter/meta-data, jadi
  tidak pernah diikat sistem dan tidak bisa diaktifkan pengguna
  (`AccessibilityStubService`, `NotifStubService`, `AdminStubReceiver`).
- **Tidak ada kode jaringan.** `INTERNET` mati total.
- UI-nya cuma satu layar statis "Undangan Pernikahan".

Yang menjaga APK ini tidak berbahaya adalah **ketiadaan kode**, bukan ketiadaan
baris permission.

## Build

```bash
# JAVA_HOME harus menunjuk ke JDK (mis. JBR bawaan Android Studio)
./gradlew assembleSmsDebug            # satu flavor
./gradlew assembleDebug               # semua flavor sekaligus
```

APK keluar di:

```
app/build/outputs/apk/<flavor>/debug/app-<flavor>-debug.apk
```

## Pasang & uji (satu HP / emulator PROTECTED)

Sideload (memicu `SRC_SIDELOAD`, skor tinggi):

```bash
adb install -r -t app/build/outputs/apk/accessibility/debug/app-accessibility-debug.apk
```

Seolah dari Play Store (memicu `SRC_PLAY`, skor rendah — untuk uji negatif):

```bash
adb install -r -t -i com.android.vending \
  app/build/outputs/apk/accessibility/debug/app-accessibility-debug.apk
```

Buka flavor tanpa ikon (`deviceadmin`) lewat adb:

```bash
adb shell am start -n com.ronda.testsample.deviceadmin/com.ronda.testsample.MainActivity
```

Lewat skrip demo (lihat `scripts/ronda help`):

```bash
scripts/ronda sample accessibility                    # sideload → alert
scripts/ronda sample accessibility com.android.vending # seolah Play Store
scripts/ronda attack                                   # tetap = flavor sms (demo)
```

## Menambah kasus uji baru

Sinyal yang belum punya flavor sendiri (mis. `AUDIO`, `CAMERA`, `LOCATION`,
`CONTACTS`, `CALL`, `QUERY_PKGS`, `NAME_MIMIC`, `LEGACY_SDK`) tinggal ditambahkan:

1. `create("<nama>") { dimension = "signal"; applicationIdSuffix = ".<nama>" }`
   di `app/build.gradle.kts`.
2. `app/src/<nama>/AndroidManifest.xml` — deklarasikan sinyalnya.
3. `app/src/<nama>/res/values/strings.xml` — `app_name`.
4. Komponen stub (jika perlu service/receiver).

Daftar bobot & pengali resmi ada di `app/src/main/java/com/ronda/app/core/Signal.kt`
pada proyek RONDA utama.
