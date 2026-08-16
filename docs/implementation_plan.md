# Rencana Implementasi RONDA — Cakupan POC HackNusa

**Track:** Human-Centric Security
**Status:** Block 0–4 sudah selesai & terverifikasi. Dokumen ini mengatur apa yang
masih dikerjakan **sebelum Block 5 (Demo & Video)**.
**Perangkat POC:** dua **emulator Android Studio** (AVD `Pixel_6` sebagai HP orang
tua, `RONDA_Guardian` sebagai HP penjaga), dijalankan lewat `scripts/ronda`.
**Belum ada pengujian di HP fisik** — lihat "Batasan perangkat" di bawah.

---

## Keputusan cakupan (16 Agustus 2026)

Rencana awal berisi enam block tambahan (4A–4F, ±11,5 jam). Untuk POC yang
disubmit, cakupan **dipersempit menjadi dua block saja**:

| Block | Isi | Estimasi | Status |
|---|---|---|---|
| **4A** | Detection Engine Upgrade — 7 izin berbahaya, teks bahasa manusia, 4 varian APK uji | ~2,5 jam | **MASUK POC** |
| **4S** | Matriks Skor Bahaya — angka 0–100 + tiga pita keparahan yang bisa dijelaskan | ~2 jam | **MASUK POC** |
| 4B, 4C, 4D, 4E, 4F | Initial scan, multi-ortu, resilience, dokumen strategi, dashboard | ~9 jam | **BACKLOG** — lihat bagian bawah |

**Alasan pemersempitan.** Dua block ini adalah satu-satunya yang mengubah apa
yang **terlihat oleh juri** dalam video 3 menit. 4A memperlebar apa yang bisa
dideteksi; 4S menjawab pertanyaan yang pasti muncul di benak juri saat melihat
overlay merah — *"seberapa yakin sistem ini, dan kenapa?"* Sisanya adalah
kematangan produk, bukan bukti konsep, dan lebih baik diceritakan sebagai
roadmap di menit 2:05 daripada dikerjakan setengah jadi.

**Hubungan antar keduanya:** 4A memperluas *apa* yang dideteksi (2 → 7 izin);
4S mengubah keluaran deteksi dari biner (HIGH/LOW) menjadi terukur. 4S bergantung
pada 4A — kerjakan 4A dulu.

## Batasan perangkat — dua emulator, bukan HP fisik

Seluruh POC dijalankan di dua AVD Android Studio. Ini keputusan sadar, bukan
kekurangan yang disembunyikan, dan harus disebutkan terbuka di video maupun saat
tanya jawab juri.

**Yang tetap valid di emulator** — dan ini yang dibuktikan POC:

- AVD memakai *system image* AOSP/Google APIs yang sama dengan HP produksi.
  `getInstallSourceInfo()` dan `getPackageInfo(GET_PERMISSIONS)` adalah API
  publik yang berperilaku identik; tidak ada jalur khusus emulator di kode kami.
- Seluruh mesin deteksi, penilaian skor, dan overlay berjalan di dalam sandbox
  aplikasi biasa. Tidak ada root, tidak ada custom ROM, tidak ada perintah shell
  yang dipanggil aplikasi.
- Pairing dan alert lewat Firebase RTDB melintasi jaringan sungguhan — dua
  emulator itu dua klien terpisah, bukan dua proses yang saling memanggil.

**Yang belum terbukti dan jangan diklaim:**

| Belum diuji | Kenapa penting | Kapan diselesaikan |
|---|---|---|
| Pembunuh proses latar belakang khas OEM (Xiaomi, Oppo, Vivo) | Ini penyebab kegagalan nomor satu di HP Indonesia sungguhan; emulator tidak punya perilaku ini | Block 4D (backlog) |
| Battery optimization sungguhan | Emulator tidak pernah masuk mode hemat baterai agresif | Block 4D (backlog) |
| Instalasi APK oleh WhatsApp asli | Di emulator, atribusi installer disimulasikan lewat `adb install -i` | Uji HP fisik, pasca-POC |
| Ketahanan `UsageStatsManager` saat memori ditekan | Emulator lapang; HP low-end tidak | Uji HP fisik, pasca-POC |

Kesimpulan yang jujur dan tetap kuat: **emulator sudah cukup untuk membuktikan
logika deteksi dan alur keputusan keluarga; HP fisik dibutuhkan untuk
membuktikan daya tahan.** Yang pertama adalah isi POC ini; yang kedua adalah
langkah berikutnya.

---

# Block 4A: Detection Engine Upgrade

> Perluas kriteria deteksi + perbarui semua teks yang dilihat pengguna + sediakan
> APK contoh uji per varian.

**Estimasi: ~2,5 jam**

## Mesin deteksi

### [UBAH] `app/src/main/java/com/ronda/app/detection/RiskEvaluator.kt`

Perluas `DANGEROUS_PERMISSIONS` dari 2 → 7 (berdasarkan riset pola malware
perbankan terkini):

```kotlin
private val DANGEROUS_PERMISSIONS = setOf(
    "android.permission.READ_SMS",
    "android.permission.RECEIVE_SMS",
    "android.permission.SEND_SMS",
    "android.permission.BIND_ACCESSIBILITY_SERVICE",
    "android.permission.BIND_NOTIFICATION_LISTENER_SERVICE",
    "android.permission.SYSTEM_ALERT_WINDOW", // Serangan overlay / layar palsu
    "android.permission.BIND_DEVICE_ADMIN"    // Kunci layar, wipe data, cegah uninstall
)
```

Ganti nama variabel `declaresSms` → `declaresHighRisk` di log. Perbarui KDoc.

### [UBAH] `app/src/main/java/com/ronda/app/detection/RiskResult.kt`

Perbarui KDoc enum `HIGH` agar mencakup ketujuh izin.

## Teks yang dilihat pengguna

### [UBAH] `app/src/main/res/values/strings.xml`

| String | Sekarang | Menjadi |
|--------|----------|---------|
| `overlay_body` | "…membaca kode OTP dari SMS…" | "…mencuri data bank Anda — membaca SMS, menyadap notifikasi, atau mengambil alih kendali HP Anda." |
| `overlay_reason` | "…meminta izin untuk membaca SMS Anda." | "…meminta izin berbahaya yang biasa dipakai penipu." |
| `alert_detail_why_body` | "…meminta izin membaca SMS…" | "…meminta izin yang biasa dipakai penipu untuk mencuri data bank (contoh: membaca SMS, menyadap notifikasi, atau mengendalikan layar)." |
| `uninstall_prompt_body` | "…membaca kode OTP dari SMS Anda." | "…mencuri data bank Anda." |

String baru untuk nama izin yang bisa dibaca manusia:

```xml
<string name="perm_read_sms">Membaca SMS</string>
<string name="perm_receive_sms">Menerima SMS</string>
<string name="perm_send_sms">Mengirim SMS</string>
<string name="perm_accessibility">Mengendalikan layar (Aksesibilitas)</string>
<string name="perm_notification_listener">Membaca semua notifikasi</string>
<string name="perm_system_alert_window">Menampilkan layar palsu (Overlay)</string>
<string name="perm_device_admin">Mengunci layar &amp; mencegah uninstall (Device Admin)</string>
```

### [UBAH] `app/src/main/java/com/ronda/app/ui/guardian/AlertDetailScreen.kt`

Tambah helper `humanReadablePermission()` — memetakan izin teknis ke bahasa
Indonesia di `EvidenceRow` permissions. Penjaga melihat "Membaca SMS,
Mengendalikan layar", bukan `READ_SMS, BIND_ACCESSIBILITY_SERVICE`.

## APK contoh uji — Build Flavors

### [UBAH] `RondaTestSample/app/build.gradle.kts`

Tambahkan 4 product flavor (mewakili 4 modus pencurian utama):

| Flavor | `applicationIdSuffix` | Izin dominan | Nama app di launcher |
|--------|----------------------|--------------|----------------------|
| `sms` | `.sms` | `READ_SMS` | "Undangan Pernikahan" |
| `accessibility` | `.accessibility` | `BIND_ACCESSIBILITY_SERVICE` | "Coretax DJP" |
| `notification` | `.notification` | `BIND_NOTIFICATION_LISTENER_SERVICE` | "Resi JNT Express" |
| `overlay` | `.overlay` | `SYSTEM_ALERT_WINDOW` | "Update Android" |

### [BARU] Manifest overlay per flavor

- `app/src/sms/AndroidManifest.xml` → `READ_SMS`
- `app/src/accessibility/AndroidManifest.xml` → `BIND_ACCESSIBILITY_SERVICE`
- `app/src/notification/AndroidManifest.xml` → `BIND_NOTIFICATION_LISTENER_SERVICE`
- `app/src/overlay/AndroidManifest.xml` → `SYSTEM_ALERT_WINDOW`

Manifest utama dihapus `READ_SMS` (pindah ke flavor `sms`).

### [BARU] String overlay per flavor

- `app/src/sms/res/values/strings.xml` → `app_name = "Undangan Pernikahan"`
- `app/src/accessibility/res/values/strings.xml` → `app_name = "Coretax DJP"`
- `app/src/notification/res/values/strings.xml` → `app_name = "Resi JNT Express"`
- `app/src/overlay/res/values/strings.xml` → `app_name = "Update Android"`

Build semua:

```bash
cd RondaTestSample && ./gradlew assembleSmsDebug assembleAccessibilityDebug \
    assembleNotificationDebug assembleOverlayDebug
```

> **Catatan etika — jangan dihapus dari narasi.** Keempat APK ini **tidak berisi
> kode berbahaya sama sekali**. Masing-masing hanya mendeklarasikan satu izin di
> manifest dan menampilkan satu layar kosong. RONDA mendeteksi *deklarasi izin*,
> bukan perilaku runtime, sehingga contoh yang tidak berbahaya sudah cukup untuk
> membuktikan konsep. Kami tidak pernah memakai malware sungguhan — sebutkan ini
> di video.

## Verifikasi Block 4A

Dijalankan di AVD `Pixel_6` (peran orang tua). Pasang dengan
`scripts/ronda attack com.whatsapp` agar atribusi installer konsisten dengan
skenario video.

- [ ] Install contoh uji `sms` → BAHAYA ✓ (regresi, harus tetap lolos)
- [ ] Install contoh uji `accessibility` → BAHAYA ✓
- [ ] Install contoh uji `notification` → BAHAYA ✓
- [ ] Install contoh uji `overlay` → BAHAYA ✓
- [ ] Teks overlay cocok dengan jenis ancaman
- [ ] `AlertDetailScreen` menampilkan izin dalam bahasa Indonesia

> `scripts/ronda` saat ini hanya mengenal satu `SAMPLE_PKG`
> (`com.ronda.testsample`). Empat flavor Block 4A membawa `applicationIdSuffix`
> berbeda, jadi `cmd_attack` dan `cmd_reset` perlu menerima argumen flavor —
> masukkan ini ke pekerjaan 4A, jangan sampai ketahuan saat merekam.

---

# Block 4S: Matriks Skor Bahaya

> Ubah keluaran deteksi dari biner (HIGH/LOW) menjadi **angka 0–100 dengan
> rincian yang bisa dijelaskan**, supaya penjaga tahu seberapa yakin sistem ini
> dan atas dasar apa.

**Estimasi: ~2 jam** · **Ketergantungan: Block 4A**

## Kenapa fitur ini ada

Masalah dengan keluaran biner: overlay merah bilang "BAHAYA" untuk aplikasi yang
sekadar sideload dan minta izin overlay — sama merahnya dengan trojan perbankan
lengkap. Penjaga yang beberapa kali melihat alarm untuk hal sepele akan berhenti
membaca alarm berikutnya. Itu **alarm fatigue**, dan itu adalah kegagalan
human-centric, bukan kegagalan teknis.

Skor menyelesaikan tiga hal sekaligus:

1. **Memberi bobot pada perhatian penjaga.** Hanya skor ≥70 yang memblokir layar.
2. **Membuat keputusan bisa dipertanggungjawabkan.** Penjaga melihat rincian tiga
   dimensi, bukan sekadar vonis. Ia bisa tidak setuju dengan alasan.
3. **Membuat sistem bisa diaudit.** Setiap komponen skor berasal dari data yang
   sudah kami baca — tidak ada model kotak hitam, tidak ada data baru yang
   dikumpulkan.

Prinsip desain yang mengikat: **skor tidak boleh muncul tanpa rinciannya.**
Angka telanjang mengubah penjaga menjadi tombol "OK". Angka beserta tiga
alasannya membuat penjaga jadi pengambil keputusan.

## Matriks

Skor akhir = **Kemampuan + Asal Pasang + Penyamaran**, dibatasi 0–100.

### Dimensi 1 — Kemampuan izin (maks 60)

*Apa yang bisa dilakukan aplikasi ini kalau memang jahat.*

| Kategori | Izin | Bobot |
|---|---|---|
| Kendali layar | `BIND_ACCESSIBILITY_SERVICE` | 45 |
| Kunci perangkat | `BIND_DEVICE_ADMIN` | 45 |
| Baca pesan / OTP | `READ_SMS`, `RECEIVE_SMS` | 40 |
| Sadap notifikasi | `BIND_NOTIFICATION_LISTENER_SERVICE` | 40 |
| Layar palsu | `SYSTEM_ALERT_WINDOW` | 35 |
| Kirim SMS (biaya) | `SEND_SMS` | 30 |

Rumus: `min(60, bobotTertinggi + 10 × (jumlahKategori − 1))`

Bonus +10 per kategori tambahan adalah inti dari matriks ini. Satu izin bisa
dijelaskan; **kombinasi** izinlah yang membentuk rantai pencurian. Membaca
notifikasi saja hanya mengintip. Membaca notifikasi *sambil* bisa mengendalikan
layar berarti mampu mengambil alih rekening tanpa korban menyentuh apa pun.

### Dimensi 2 — Asal pasang (maks 30)

*Dari mana aplikasi ini datang.* Dibaca dari `getInstallSourceInfo()`.

| Sumber | Nilai | Alasan |
|---|---|---|
| Aplikasi chat (WhatsApp, Telegram) | 30 | Ini jalur penipuan yang sebenarnya — dikirim orang yang dipercaya |
| Peramban, pengelola berkas, `manual`, `unknown` | 25 | Sideload biasa |
| Play Store / toko bawaan vendor | 0 | Sudah lewat pemindaian Google Play Protect |

> **Cara mendapatkan nilai ini di emulator.** POC ini dijalankan di dua AVD
> Android Studio, bukan HP fisik, jadi tidak ada WhatsApp sungguhan yang memasang
> APK. `scripts/ronda attack com.whatsapp` memakai `adb install -i com.whatsapp`,
> yang membuat `getInstallSourceInfo().installingPackageName` benar-benar bernilai
> `com.whatsapp` — RONDA membaca API yang sama persis seperti di HP fisik, nilainya
> pun asli dari sistem. Yang disimulasikan adalah **kanal pengirimannya**, bukan
> pembacaannya.
>
> Tanpa flag `-i`, `adb install` biasa menghasilkan `manual` / `com.android.shell`
> → bernilai **25**, bukan 30. Selisih 5 poin ini mengubah hasil pada satu kasus,
> lihat peringatan di tabel contoh di bawah.

### Dimensi 3 — Penyamaran (maks 10)

*Apakah namanya berpura-pura jadi sesuatu yang resmi.*

Cocokkan `appLabel` (huruf kecil) dengan daftar kata kunci: `bri`, `bca`,
`mandiri`, `bni`, `dana`, `ovo`, `gopay`, `m-banking`, `mbanking`, `djp`,
`coretax`, `pajak`, `samsat`, `bpjs`, `resi`, `paket`, `jnt`, `jne`, `undangan`,
`update`, `pembaruan`, `sistem`, `android`.

| Kondisi | Nilai |
|---|---|
| Nama memuat ≥1 kata kunci | 10 |
| Tidak cocok | 0 |

> **Batasan yang harus kami akui.** Ini pencocokan kata kunci, bukan pengenalan
> merek. Aplikasi BCA yang asli dari Play Store juga akan kena +10 di dimensi
> ini — tetapi karena dimensi asal pasangnya 0, total tetap jauh di bawah ambang.
> Bobotnya sengaja dibuat kecil (maks 10) agar tidak pernah bisa sendirian
> mendorong sebuah aplikasi melewati ambang.

### Pita keparahan

| Skor | Pita | Warna | Tindakan RONDA |
|---|---|---|---|
| 70–100 | **BAHAYA** | `error` `#8C1010` | Blokir layar (overlay) + alert prioritas tinggi ke penjaga |
| 45–69 | **WASPADA** | `tertiary` kuning | Alert ke penjaga, **tanpa** blokir. Penjaga yang memutuskan |
| 0–44 | **AMAN** | `surfaceVariant` | Hanya dicatat di log. Tidak ada notifikasi |

### Contoh perhitungan — dipakai di video

Kolom "Total" memakai atribusi WhatsApp (`-i com.whatsapp`, asal = 30). Kolom
"adb polos" adalah hasil kalau flag `-i` lupa dipasang (asal = 25).

| Aplikasi | Kemampuan | Asal | Penyamaran | Total | Pita | adb polos |
|---|---|---|---|---|---|---|
| "Undangan Pernikahan" (`READ_SMS`) | 40 | 30 | 10 | **80** | BAHAYA | 75 |
| "Coretax DJP" (`BIND_ACCESSIBILITY_SERVICE`) | 45 | 30 | 10 | **85** | BAHAYA | 80 |
| "Resi JNT Express" (`NOTIFICATION_LISTENER`) | 40 | 30 | 10 | **80** | BAHAYA | 75 |
| "Update Android" (`SYSTEM_ALERT_WINDOW`) | 35 | 30 | 10 | **75** | BAHAYA | ⚠ **70** |
| Trojan lengkap (aksesibilitas + SMS + notifikasi, nama "BRImo Update") | 60 | 30 | 10 | **100** | BAHAYA | 95 |
| Aplikasi senter sideload dari peramban (`SYSTEM_ALERT_WINDOW` saja) | 35 | 25 | 0 | **60** | WASPADA | 60 |
| Aplikasi SMS asli dari Play Store (`READ_SMS`, `RECEIVE_SMS`) | 40 | 0 | 0 | **40** | AMAN | 40 |

> ⚠ **Titik rapuh — varian `overlay`.** Tanpa flag `-i`, "Update Android" jatuh
> tepat di **70**, persis di ambang BAHAYA. Satu perubahan bobot sekecil apa pun
> akan menjatuhkannya ke WASPADA dan membuat verifikasi Block 4A gagal. Dua
> pilihan penanganan, putuskan sebelum mulai koding:
> 1. Selalu pasang varian ini dengan `-i com.whatsapp` (jadi 75). Paling sederhana.
> 2. Naikkan bobot `SYSTEM_ALERT_WINDOW` dari 35 → 40. Konsekuensinya aplikasi
>    senter di baris keenam ikut naik ke 65 — masih WASPADA, jadi beat terkuat
>    video tetap aman.
>
> Rekomendasi: **pilihan 1.** Jangan mengubah bobot hanya demi meloloskan satu
> contoh uji; itu membuat matriksnya jadi hasil kalibrasi ke demo, bukan ke
> ancaman.

Baris terakhir adalah pembenaran keberadaan matriks ini: dengan aturan biner
lama, aplikasi SMS asli dari Play Store lolos hanya karena kebetulan dicek
sumbernya; sekarang ia lolos karena **skornya memang rendah**, dan alasannya
bisa dibaca.

## Perubahan kode

### [BARU] `app/src/main/java/com/ronda/app/detection/RiskScore.kt`

```kotlin
package com.ronda.app.detection

/** Rincian skor bahaya — selalu dikirim bersama angkanya, tidak pernah sendirian. */
data class RiskScore(
    val capability: Int,   // 0..60
    val provenance: Int,   // 0..30
    val disguise: Int,     // 0..10
    val categories: List<String> = emptyList() // kategori kemampuan yang terdeteksi
) {
    val total: Int get() = (capability + provenance + disguise).coerceIn(0, 100)

    val band: RiskBand get() = when {
        total >= 70 -> RiskBand.BAHAYA
        total >= 45 -> RiskBand.WASPADA
        else        -> RiskBand.AMAN
    }
}

enum class RiskBand { BAHAYA, WASPADA, AMAN }
```

### [UBAH] `RiskResult.kt`

Tambah field `score: RiskScore`. Pertahankan `riskLevel` sebagai turunan
(`HIGH` bila `band == BAHAYA`) supaya `OverlayService`, `FlaggedAppStore`, dan
`InstallReceiver` yang sudah jalan tidak perlu diubah — ini menjaga regresi
Block 1–4 tetap hijau.

### [UBAH] `RiskEvaluator.kt`

Hitung ketiga dimensi lalu isi `RiskScore`. Aturan blokir menjadi
`score.band == RiskBand.BAHAYA` — menggantikan `isSideloaded && declaresHighRisk`.
Untuk pita `WASPADA`: kirim alert, **jangan** panggil `FlaggedAppStore.flag()`
(tanpa flag berarti tanpa overlay).

### [UBAH] `alert/Alert.kt`

Tambah field yang ikut dikirim ke RTDB — penjaga di kota lain harus melihat
rincian yang sama:

```kotlin
val riskScore: Int = 0,
val scoreCapability: Int = 0,
val scoreProvenance: Int = 0,
val scoreDisguise: Int = 0,
val band: String = "",
```

Semuanya punya nilai default → node RTDB lama tetap bisa dibaca (Firebase
memerlukan konstruktor tanpa argumen; field baru pada alert lama akan bernilai 0).

### [UBAH] `ui/guardian/AlertDetailScreen.kt`

Tambah **Kartu Skor** di paling atas, sebelum baris bukti yang sudah ada:

- Angka besar (`displayLarge`, ≥48sp): `85`, dengan `/100` lebih kecil di sampingnya
- Label pita di bawahnya: `BAHAYA`
- Tiga baris rincian, masing-masing kalimat lengkap berbahasa manusia:
  - `Kemampuan 45/60 — bisa mengendalikan layar HP`
  - `Asal pasang 30/30 — dikirim lewat WhatsApp, bukan dari Play Store`
  - `Penyamaran 10/10 — memakai nama instansi resmi ("Coretax DJP")`
- Warna kartu mengikuti pita (`errorContainer` / kuning / `surfaceVariant`)

Aksesibilitas: angka skor **tidak boleh** menjadi satu-satunya pembawa makna —
label pita berupa teks wajib selalu ada di samping angka, dan warna bukan
satu-satunya pembeda (lihat `DESIGN.md` §7).

### [UBAH] `ui/guardian/GuardianHomeScreen.kt`

Baris alert menampilkan lencana skor kecil di sisi kanan (`80`), diwarnai
menurut pita. Penjaga bisa memilah daftar tanpa membuka satu per satu.

### [UBAH] `overlay/OverlayService.kt` + `res/values/strings.xml`

Overlay di HP orang tua menampilkan skor, tetapi **dengan kalimat, bukan angka
telanjang** — orang tua tidak diminta menafsirkan angka:

```xml
<string name="overlay_score">Tingkat bahaya: %1$d dari 100</string>
<string name="band_bahaya">BAHAYA</string>
<string name="band_waspada">WASPADA</string>
<string name="band_aman">AMAN</string>
<string name="score_capability_label">Kemampuan</string>
<string name="score_provenance_label">Asal pasang</string>
<string name="score_disguise_label">Penyamaran</string>
```

## Verifikasi Block 4S

Semua dipasang dengan `-i com.whatsapp` kecuali baris WASPADA, yang justru harus
dipasang **tanpa** flag itu (mensimulasikan unduhan dari peramban).

- [ ] Contoh uji `sms` → skor 80, pita BAHAYA, overlay muncul
- [ ] Contoh uji `accessibility` → skor 85, pita BAHAYA
- [ ] Contoh uji `notification` → skor 80, pita BAHAYA
- [ ] Contoh uji `overlay` → skor 75, pita BAHAYA
- [ ] APK dengan `SYSTEM_ALERT_WINDOW` saja + nama netral, dipasang tanpa `-i` →
      skor 60, pita WASPADA, **alert terkirim tetapi overlay tidak muncul**
- [ ] `AlertDetailScreen` menampilkan ketiga rincian, bukan hanya angka
- [ ] Alert lama di RTDB (tanpa field skor) tetap terbuka tanpa crash
- [ ] Skor di AVD penjaga sama persis dengan skor di AVD orang tua
- [ ] Ulangi seluruh rangkaian setelah `cold boot` kedua AVD — status yang
      tertinggal di emulator adalah sumber "berhasil di percobaan kedua" yang
      paling sering menipu

---

# Dokumentasi yang ikut diperbarui

### [UBAH] `docs/PRD.md`
- FR-3: aturan risiko → 7 izin + matriks skor tiga dimensi
- Tambah FR-10: Skor Bahaya dan tiga pita keparahan
- Tambah FR-11: pita WASPADA memberi alert tanpa memblokir

### [UBAH] `docs/ARCHITECTURE.md`
- Perbarui narasi `DANGEROUS_PERMISSIONS`
- Tambah `RiskScore` ke diagram alur deteksi
- Tambah field skor ke skema `alerts/`

### [UBAH] `docs/DESIGN.md`
- §5.7 `AlertDetailScreen`: spesifikasi Kartu Skor
- §5.8 `WarningOverlay`: baris tingkat bahaya
- §2.2: tambah peran warna kuning untuk pita WASPADA

### [UBAH] `docs/VIDEO_SCRIPT.md`
- Skor masuk ke bagian demonstrasi — **sudah dikerjakan**

### [UBAH] `docs/TODO.md`
- Tambah Block 4A dan 4S di atas garis ACTIVE

---

# Urutan eksekusi POC

| Urutan | Block | Estimasi | Ketergantungan |
|---|---|---|---|
| 1 | **4A** Detection Upgrade | ~2,5 jam | — |
| 2 | **4S** Matriks Skor Bahaya | ~2 jam | 4A |
| 3 | **5** Demo & rekaman video | ~3 jam | 4A + 4S |

**Total ±7,5 jam.** Dibanding rencana lama (~11,5 jam untuk enam block), ini
memberi ruang untuk syarat PRD: **lima kali alur penuh berhasil berturut-turut
tanpa satu pun kegagalan** sebelum tombol rekam ditekan.

---

# BACKLOG — di luar cakupan POC

> Bagian di bawah ini **tidak dikerjakan** sebelum submisi. Isinya dipertahankan
> utuh karena menjadi bahan bagian "roadmap" di video (menit 2:05) dan bahan
> jawaban saat sesi tanya jawab juri.

## Block 4B: Initial Scan

> Scan semua aplikasi yang sudah terpasang saat setup/pairing selesai. Menangkap
> malware yang sudah ada sebelum RONDA dipasang.

**Estimasi: ~45 menit**

### [UBAH] `detection/DetectionService.kt`

Tambah method `scanExistingApps()`:

```kotlin
private fun scanExistingApps() {
    val pm = packageManager
    val evaluator = RiskEvaluator(this)
    val installed = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS)

    for (pkg in installed) {
        // Lewati aplikasi sistem dan RONDA sendiri
        if (pkg.applicationInfo?.flags?.and(ApplicationInfo.FLAG_SYSTEM) != 0) continue
        if (pkg.packageName == packageName) continue
        if (SafeAppStore(this).isAllowed(pkg.packageName)) continue
        if (FlaggedAppStore(this).isFlagged(pkg.packageName)) continue

        val result = evaluator.evaluate(pkg.packageName)
        if (result.riskLevel == RiskLevel.HIGH) {
            // Alur sama dengan InstallReceiver.onPackageAdded()
            FlaggedAppStore(this).flag(pkg.packageName)
            if (Permissions.canBlock(this)) OverlayService.start(this)
            publishAlert(result)
        }
    }
}
```

Panggil `scanExistingApps()` **sekali** di `onCreate()` dengan penjaga
SharedPreferences `KEY_INITIAL_SCAN_DONE`. Jangan scan ulang setiap service
restart.

### [UBAH] `detection/InstallReceiver.kt`

Ekstrak `publishAlert()` menjadi method yang juga bisa dipanggil dari
`DetectionService`. Saat ini `publishAlert` privat di `InstallReceiver` dan
memakai `goAsync()` yang hanya tersedia di `BroadcastReceiver`.

Solusi paling sederhana: buat fungsi top-level `publishAlertToGuardian(context,
result)` yang bisa dipanggil dari kedua tempat. Di `DetectionService` pakai
coroutine scope milik service, bukan `goAsync()`.

### Verifikasi Block 4B
- [ ] Install contoh uji APK **sebelum** RONDA dipasang
- [ ] Install & setup RONDA → pairing → scan otomatis berjalan
- [ ] Contoh uji APK langsung ditandai + overlay aktif + alert ke penjaga
- [ ] Scan tidak berjalan ulang setelah restart

---

## Block 4C: Multi-Ortu

> Satu penjaga melindungi banyak HP keluarga. Setiap HP punya label ("HP Ibu",
> "HP Bapak").

**Estimasi: ~3,5 jam**

### [UBAH] `pairing/RoleStore.kt`

Sisi penjaga — ganti `pairingId` tunggal menjadi banyak:

```kotlin
// Lama (kompatibilitas mundur untuk sisi protected)
var pairingId: String?

// Baru — khusus penjaga
val pairingIds: Set<String>
    get() = prefs.getStringSet(KEY_PAIRING_IDS, emptySet()) ?: emptySet()

fun addPairing(code: String, label: String) {
    prefs.edit()
        .putStringSet(KEY_PAIRING_IDS, pairingIds + code)
        .putString("label_$code", label)
        .apply()
    // Jaga pairingId tetap sinkron demi kompatibilitas mundur
    if (pairingId == null) pairingId = code
}

fun pairingLabel(code: String): String =
    prefs.getString("label_$code", null) ?: "HP Ortu"

val isPaired: Boolean
    get() = if (role == Role.GUARDIAN) pairingIds.isNotEmpty() else pairingId != null
```

### [UBAH] `pairing/PairingRepository.kt`

Tambah `protectedLabel: String?` ke data class `Pairing` dan ke `createPairing()`.

### [UBAH] `alert/GuardianAlertService.kt`

`watchAlerts()` meluncurkan satu coroutine per `pairingId`:

```kotlin
private fun watchAlerts(pairingIds: Set<String>) {
    for (pairingId in pairingIds) {
        scope.launch {
            AlertRepository().observeAlerts(pairingId)
                .catch { Log.e(TAG, "Alert stream failed for $pairingId", it) }
                .collect { alerts ->
                    val label = RoleStore(this@GuardianAlertService).pairingLabel(pairingId)
                    alerts
                        .filter { it.alertId.isNotEmpty() && !seenAlerts.isNotified(it.alertId) }
                        .sortedBy { it.timestamp }
                        .forEach { alert ->
                            notifyAlert(alert, label)
                            seenAlerts.markNotified(alert.alertId)
                        }
                }
        }
    }
}
```

Isi notifikasi: "**Undangan Pernikahan** dipasang di **HP Ibu**."

### [UBAH] `ui/guardian/GuardianPairingScreen.kt`

Tambah `TextField` untuk label sebelum kode dibuat:
- Hint: "Nama HP ini (contoh: HP Ibu)"
- Default kalau kosong: "HP Ortu"
- Callback `onPaired` sekarang menerima `(code: String, label: String)`

### [UBAH] `ui/guardian/GuardianHomeScreen.kt`

- Kartu status: daftar HP yang dilindungi beserta labelnya
- Baris alert: tampilkan label asal ("HP Ibu — Undangan Pernikahan")
- Tombol "Tambah HP keluarga" di bawah daftar HP
- Callback baru `onAddDevice: () -> Unit`

### [UBAH] `MainActivity.kt`

**GuardianFlow:**
- `pairingId == null` **dan** `pairingIds.isEmpty()` → `GuardianPairingScreen`
- `pairingIds.isNotEmpty()` → `GuardianHomeScreen`
- Tambah state `isAddingDevice` — kalau true, tampilkan `GuardianPairingScreen` lagi
- `onPaired()` → `roleStore.addPairing(code, label)`
- Alert dikumpulkan dari semua `pairingIds`

### [UBAH] `res/values/strings.xml`

```xml
<string name="pair_guardian_label_hint">Nama HP ini (contoh: HP Ibu)</string>
<string name="pair_guardian_label_default">HP Ortu</string>
<string name="guardian_home_devices_title">HP yang dilindungi</string>
<string name="guardian_home_add_device">Tambah HP keluarga</string>
<string name="alert_notification_body_multi">%1$s dipasang di %2$s.</string>
<string name="guardian_home_connected_multi">Melindungi %1$d HP keluarga</string>
```

### Verifikasi Block 4C
- [ ] Penjaga pairing dengan emulator protected #1 (label "HP Ibu") → berhasil
- [ ] Penjaga tap "Tambah HP keluarga" → pairing dengan emulator protected #2
      (label "HP Bapak") → berhasil
- [ ] Install APK uji di protected #1 → penjaga dapat alert berlabel "HP Ibu"
- [ ] Install APK uji di protected #2 → alert terpisah berlabel "HP Bapak"
- [ ] Uninstall dari salah satu → status ter-update di layar penjaga yang benar

---

## Block 4D: Resilience

> Battery optimization + QR deep link. Mencegah demo gagal di HP fisik dan
> membuat QR benar-benar berfungsi.

**Estimasi: ~1 jam**

### [UBAH] `Permissions.kt`

```kotlin
fun isBatteryOptimized(context: Context): Boolean {
    val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    return !pm.isIgnoringBatteryOptimizations(context.packageName)
}

fun batteryOptimizationIntent(context: Context): Intent =
    Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
        data = Uri.parse("package:${context.packageName}")
    }
```

### [UBAH] `ui/setup/SetupScreen.kt`

Tambah baris ke-4 di daftar izin:
- Judul: "Tidak dibatasi baterai"
- Alasan: "Agar RONDA tetap berjalan di latar belakang dan tidak dimatikan sistem."
- Status: cek `Permissions.isBatteryOptimized()`

### [UBAH] `res/values/strings.xml`

```xml
<string name="permission_battery_title">Tidak dibatasi baterai</string>
<string name="permission_battery_why">Agar RONDA tetap berjalan di latar belakang dan tidak dimatikan oleh sistem hemat baterai.</string>
```

### [UBAH] `AndroidManifest.xml`

Tambah intent filter di `MainActivity`:

```xml
<intent-filter>
    <action android:name="android.intent.action.VIEW" />
    <category android:name="android.intent.category.DEFAULT" />
    <category android:name="android.intent.category.BROWSABLE" />
    <data android:scheme="ronda" android:host="pair" />
</intent-filter>
```

### [UBAH] `MainActivity.kt`

Tangani deep link di `onCreate()` dan `onNewIntent()`:

```kotlin
private fun handleDeepLink(intent: Intent) {
    val uri = intent.data ?: return
    if (uri.scheme == "ronda" && uri.host == "pair") {
        val code = uri.lastPathSegment ?: return
        deepLinkCode = code   // isi otomatis di ProtectedPairingScreen
    }
}
```

### [UBAH] `ui/protectedrole/ProtectedPairingScreen.kt`

Terima parameter `initialCode: String?` — kalau non-null, isi TextField dan
kirim otomatis.

### Verifikasi Block 4D
- [ ] Layar setup menampilkan status battery optimization
- [ ] Tap "Nyalakan" → dialog sistem terbuka → kembali → status ter-update
- [ ] Scan QR dari aplikasi scanner bawaan → RONDA terbuka dengan kode terisi
- [ ] Deep link `ronda://pair/QTDEZ3` → isi otomatis + pairing berhasil

---

## Block 4E: Dokumen Strategi

> Dokumen selling point dan strategi kemitraan untuk pitch HackNusa.

**Estimasi: ~1 jam**

### [BARU] `docs/STRATEGY.md`

Isi:

1. **Posisi**: RONDA = Infrastruktur Kepercayaan Keluarga (bukan antivirus)
2. **Kemitraan OJK / IASC**: feed data anonim (`packageName`, `installSource`,
   `flaggedPermissions`, `riskScore`) ke IASC tanpa data pribadi
3. **Kemitraan Kominfo**: sensor awal yang melaporkan hash APK malware sebelum viral
4. **Kemitraan bank**: pencegahan pra-fraud — lebih murah daripada mengganti
   kerugian nasabah. Bank merekomendasikan RONDA ke nasabah rentan
5. **Roadmap monetisasi**: freemium (multi-guardian premium), API threat intel
   B2B, sponsorship CSR dari bank

Prinsip: **semua model kemitraan bekerja dengan data yang sudah ada di record
alert.** Tidak perlu mengumpulkan data baru. Consent dan data minimization dari
PRD §5 tetap utuh.

---

## Block 4F: UX Dashboard Penjaga

> Ubah Guardian Home Screen menjadi command center sungguhan. Tambah Security
> Level dan pisahkan alert aktif dari riwayat.

**Estimasi: ~2,5 jam**

### [UBAH] `pairing/PairingRepository.kt`

Ubah data class `Pairing` di Firebase untuk menyimpan status keamanan (diperbarui
oleh HP protected):

```kotlin
data class Pairing(
    val guardianDeviceId: String = "",
    val protectedDeviceId: String? = null,
    val protectedLabel: String? = null,
    val status: String = STATUS_PENDING,
    val createdAt: Long = 0L,
    val expiresAt: Long = 0L,
    // BARU: status setup dari HP protected
    val hasOverlay: Boolean = false,
    val hasUsageStats: Boolean = false,
    val hasNotifications: Boolean = false,
    val isBatteryOptimized: Boolean = false,
    val lastSeenAt: Long = 0L
) {
    @get:Exclude
    val securityLevel: SecurityLevel get() = when {
        !hasOverlay || !hasNotifications -> SecurityLevel.DANGER
        !hasUsageStats || !isBatteryOptimized -> SecurityLevel.WARNING
        else -> SecurityLevel.SAFE
    }
}

enum class SecurityLevel { SAFE, WARNING, DANGER }
```

Di sisi protected, buat fungsi yang memperbarui node `Pairing` di Firebase dengan
nilai `Permissions` saat ini + timestamp `lastSeenAt`.

> Tiga koreksi terhadap rancangan ini sudah dicatat di `DESIGN.md` §6.0.1–6.0.3
> (aturan RTDB memblokir penulisan; pemetaan severity tidak cocok dengan kode;
> "panggil saat app dibuka" meniadakan gunanya fitur). Selesaikan koreksi itu
> sebelum block ini dikerjakan.

### [UBAH] `ui/guardian/GuardianHomeScreen.kt`

Rombak layout untuk setiap perangkat yang dilindungi:
1. **Header HP**: nama HP ("HP Ibu")
2. **Ikon perisai & status** berdasarkan `securityLevel`:
   - 🟢 `SAFE`: "Aman Terlindungi"
   - 🟡 `WARNING`: "Perlu Perhatian (Baterai/Akses belum lengkap)"
   - 🔴 `DANGER`: "Tidak Aktif (Izin dicabut)"
3. **Terakhir aktif**: "Terakhir aktif: X menit yang lalu" dari `lastSeenAt`
4. **Pemisahan alert**:
   - `val activeAlerts = alerts.filter { it.status == Alert.STATUS_PENDING }`
   - `val historyAlerts = alerts.filter { it.status != Alert.STATUS_PENDING }`
5. **Bagian "Butuh Tindakan Segera"**: hanya muncul kalau `activeAlerts` tidak
   kosong. Warna `errorContainer`
6. **Bagian "Riwayat Pengawasan"**: selalu muncul di bawah, warna `surfaceVariant`.
   Kartu riwayat menampilkan hasil: "✅ Dihapus" atau "🛡️ Ditandai Aman"

### [UBAH] `res/values/strings.xml`

```xml
<string name="status_safe">Aman Terlindungi</string>
<string name="status_warning">Perlu Perhatian</string>
<string name="status_danger">Tidak Aktif / Bahaya</string>
<string name="section_active_alerts">Butuh Tindakan Segera</string>
<string name="section_history">Riwayat Pengawasan</string>
<string name="history_uninstalled">✅ Aplikasi Dihapus</string>
<string name="history_safe">🛡️ Ditandai Aman</string>
```

### Verifikasi Block 4F
- [ ] Di HP protected, cabut izin overlay
- [ ] Buka aplikasi RONDA di HP protected
- [ ] Di HP penjaga, status HP tersebut berubah jadi 🔴 "Tidak Aktif / Bahaya"
- [ ] Simulasikan alert malware → masuk ke "Butuh Tindakan Segera" (merah)
- [ ] Klik uninstall di alert tersebut, selesaikan di HP protected
- [ ] Alert berpindah ke "Riwayat Pengawasan" (abu-abu) bertanda "✅ Dihapus"
