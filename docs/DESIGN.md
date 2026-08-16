# RONDA — Design System & Screen Specification

> **Status dokumen.** Ini dokumen **spesifikasi**, bukan perubahan kode.
> Per 15 Agustus 2026, **tidak ada UI yang saya ubah** — rekan tim sedang
> mengerjakan fitur tambahan, dan dokumen ini sengaja tidak menyentuh file
> apa pun agar tidak bertabrakan.
>
> Setiap bagian ditandai:
> - **[TERPASANG]** — sudah ada di kode hari ini, didokumentasikan apa adanya
> - **[USULAN]** — belum ada, perlu keputusan tim sebelum dikerjakan
>
> Sumber kebenaran tetap `docs/PRD.md` dan `docs/ARCHITECTURE.md`. Kalau dokumen
> ini bertabrakan dengan keduanya, keduanya yang menang.
>
> Referensi visual: `ronda design.pdf` di root repo (6 tangkapan layar dari
> emulator, 14–15 Agustus 2026).

---

## 1. Prinsip desain

Lima prinsip ini diturunkan langsung dari PRD, bukan dari selera.

**1. Dua pengguna, dua rezim desain.** RONDA punya dua pengguna dengan kebutuhan
berlawanan dalam satu APK. Layar terlindungi dirancang untuk orang berusia 60+
dengan literasi teknologi rendah yang mungkin sedang panik. Layar penjaga
dirancang untuk anak dewasa yang melek teknologi dan memutuskan cepat sambil
berjalan. Merata-ratakan keduanya menghasilkan desain yang salah untuk dua-duanya.
*(PRD §5 Usability)*

**2. Pengguna terlindungi tidak pernah diminta menilai keamanan.** Mereka tidak
ditanya "apakah aplikasi ini aman?". Mereka diberi tahu apa yang terjadi dan apa
yang harus dilakukan. Penilaian adalah tugas penjaga. *(PRD FR-6)*

**3. Penjagaan harus terlihat.** Orang yang dilindungi harus bisa melihat kapan
saja bahwa HP-nya dijaga dan oleh siapa. Ini garis pemisah antara alat
perlindungan yang disetujui dan stalkerware — dan itu urusan desain, bukan
sekadar urusan dokumen. *(PRD §5 Security no. 5, FR-2)*

**4. Overlay tidak boleh menyerupai UI sistem.** Meniru dialog Android adalah
teknik trojan perbankan. Overlay RONDA harus jelas milik RONDA. *(PRD §5
Security no. 8)*

**5. Bahasa Indonesia sederhana, tanpa jargon keamanan.** Tidak ada "malware",
"permission", "risk score", "sideload". Copy ditulis untuk orang tua seseorang.
*(PRD §5 Usability)*

---

## 2. Sistem warna

### 2.1 Riwayat keputusan — **[SELESAI]**

Bagian ini menyimpan dua kondisi yang sudah dilewati, karena keduanya menjelaskan
kenapa palet sekarang berbentuk seperti ini.

**Kondisi 1 — template Android Studio.** `Theme.kt` memakai `dynamicColor = true`
dan `Color.kt` masih berisi `Purple40` / `Purple80`. Akibatnya RONDA mengambil
warna dari wallpaper pengguna (Material You): identitas tidak stabil antar-HP,
kontras tidak terkendali, dan tangkapan layar tidak bisa direproduksi. Warna
biru-teal di `ronda design.pdf` ternyata bukan palet rancangan — itu kebetulan
turunan wallpaper emulator.

**Kondisi 2 — "night watchman" gelap.** Diganti menjadi palet gelap kustom:
`night #12161F` dengan aksen oranye `lamp #E8A33D`. Ini memperbaiki masalah
stabilitas, tetapi memunculkan dua masalah baru:

- **Salah nada.** Hitam-oranye terbaca sebagai panel alarm. Padahal yang membuka
  aplikasi ini adalah penjaga di sore hari yang biasanya menemukan tidak ada apa-apa.
- **Salah untuk mata lansia.** Sensitivitas kontras menurun seiring usia dan
  hamburan cahaya di dalam mata meningkat, sehingga teks terang di atas latar
  gelap justru *mekar* dan makin sulit dibaca — kebalikan dari yang dijanjikan
  tema gelap.

Ada juga cacat yang tidak disengaja: `darkColorScheme()` hanya mengisi sembilan
peran. `primaryContainer` dan `errorContainer` — yang dipakai `SetupScreen` dan
`UninstallPromptScreen` — jatuh ke nilai baku Material, sehingga kedua layar itu
menampilkan **ungu Material di atas latar nyaris hitam**. Ini alasan §2.3
mewajibkan semua peran diisi.

Yang tidak pernah terpengaruh sepanjang kedua kondisi: `WarningOverlay`. Ia
memakai warna hardcoded di `res/values/colors.xml`, di luar sistem tema, jadi
merahnya selalu konsisten.

### 2.2 Palet terpasang **[TERPASANG — `ui/theme/Color.kt`]**

Terang saja, dynamic color mati. Tiga alasan, berurutan menurut bobotnya:

1. **Mata lansia.** Lihat §2.1 kondisi 2 — latar terang menang untuk pembaca
   berusia 70-an, dan itu separuh pengguna produk ini.
2. **Skor harus berarti sama di mana pun.** Dynamic color akan mengecat ulang
   pita risiko dari wallpaper, dan DARURAT yang keluar berwarna lila di satu HP
   bukan lagi sinyal.
3. **Penjaga dan orang tua harus bisa saling menjelaskan lewat telepon.** "Yang
   merah di atas" harus merujuk benda yang sama di kedua HP.

**Netral dan merek**

| Nama | Nilai | Peran M3 | Dipakai untuk |
|---|---|---|---|
| `paper` | `#F5F8F9` | `background` | Latar layar |
| `card` | `#FFFFFF` | `surface` | Kartu, sheet |
| `cardSunken` | `#EAEFF1` | `surfaceVariant` | Ubin cekung, panel teknis |
| `ink` | `#14191B` | `onSurface` | Teks utama — 16.4:1 di atas `card` |
| `inkSoft` | `#4A5457` | `onSurfaceVariant` | Teks sekunder — 7.9:1 |
| `hairline` | `#D5DDE0` | `outlineVariant` | Garis kartu. Bukan untuk teks |
| `hairlineStrong` | `#78868A` | `outline` | Garis tombol outlined — 4.0:1 |
| `teal` | `#0B4F60` | `primary` | Tombol utama, wordmark — 8.9:1 |
| `tealSoft` | `#D2ECF4` | `primaryContainer` | Kartu peran utama |
| `tealInk` | `#002731` | `onPrimaryContainer` | Teks di atasnya |

`teal` bertahan dari usulan sebelumnya. Alasan ia mengalahkan biru: ia tidak bisa
tertukar dengan satu pun dari empat warna pita di bawah.

**Pita risiko** — satu warna per `RiskLevel`, masing-masing berpasangan dengan
tint untuk permukaan di belakangnya.

| Pita | Warna | Tint | Kontras di atas `card` |
|---|---|---|---|
| `AMAN` | `#186B45` | `#D8EFE3` | 5.9:1 |
| `RENDAH` | `#3F5A66` | `#DFE8EC` | 7.3:1 |
| `PERINGATAN` | `#8A5300` | `#FCE6C4` | 6.4:1 |
| `DARURAT` | `#8C1010` | `#FADCD9` | 9.0:1 |

Keempatnya dipisahkan **dalam terang, bukan hanya dalam warna**. Itu yang
menjaganya tetap terbedakan bagi pembaca buta warna merah-hijau — yang justru
orang paling dirugikan oleh pasangan hijau/merah.

`#8C1010` dipertahankan dari overlay yang sudah terpasang, sehingga DARURAT di HP
penjaga dan layar merah di HP orang tua terbaca sebagai kejadian yang sama.

`#8A5300` adalah satu-satunya sisa oranye lama, dan sekarang ia **hanya** berarti
"perlu perhatian" — bukan warna merek.

### 2.3 Aturan yang mengikat

1. **Isi semua peran M3.** Peran yang dibiarkan kosong muncul kembali sebagai ungu
   Material. Ini bukan hipotesis; lihat §2.1 kondisi 2. Tambah peran, jangan
   pernah hapus.
2. **Layar tidak boleh mengimpor warna mentah.** Pakai `MaterialTheme.colorScheme`.
   Satu-satunya pengecualian adalah `scoreColor()` / `scoreTint()` dan konstanta
   pita — di sana warnanya memang informasi, bukan gaya.
3. **Warna tidak pernah berjalan sendiri.** Setiap pita selalu disertai namanya
   sebagai teks (`BandChip`). Setiap status izin disertai lambang `✓` / `!`.
4. **Tint adalah warna tetap, bukan alpha.** Alpha bertumpuk terhadap apa pun di
   bawahnya, sedangkan blok yang sama muncul di atas `card` maupun `paper`.
5. **Merah dibelanjakan di tiga tempat saja:** overlay, `UninstallPromptScreen`,
   dan tombol "Tandai berbahaya". Di luar itu merah kehilangan arti.

---

## 3. Skala tipografi

Dua skala, sesuai prinsip no. 1. PRD menetapkan **minimum 20sp untuk layar
terlindungi** dan tidak menetapkan minimum untuk layar penjaga.

Dua muka: **Archivo** untuk judul, angka skor, dan kata vonis; **Inter** untuk
kalimat yang dibaca. Keduanya font variabel yang dibundel di dalam APK, bukan
lewat penyedia downloadable-font — jalur demo harus jalan offline.

### Layar terlindungi **[TERPASANG — `ui/theme/Type.kt`]**

Diterapkan sebagai satu set gaya terpisah (`ProtectedTitle`, `ProtectedSubtitle`,
`ProtectedBody`), bukan sebagai kenaikan global, karena daftar pantau penjaga
akan pecah kalau isinya dipaksa 20sp.

| Gaya | Ukuran | Berat |
|---|---|---|
| `ProtectedTitle` | 30sp | Bold |
| `ProtectedSubtitle` | 22sp | SemiBold |
| `ProtectedBody` | 20sp | Regular |
| Teks tombol | 18–20sp | Medium |

Layar yang termasuk: `RoleSelectionScreen`, `ProtectedPairingScreen`,
`SetupScreen`, `UninstallPromptScreen`, `WarningOverlay`.

### Layar penjaga **[TERPASANG — `Typography`]**

Dinaikkan satu langkah dari draf pertama. Penjaga membaca layar ini dalam
sembilan puluh detik antara notifikasi dan keputusan, sering sambil berjalan.

| Peran M3 | Ukuran | Berat |
|---|---|---|
| `displayMedium` | 30sp | SemiBold |
| `headlineSmall` | 24sp | SemiBold |
| `titleLarge` | 21sp | SemiBold |
| `titleMedium` | 17sp | SemiBold |
| `bodyLarge` | 17sp | Regular |
| `bodyMedium` | 15sp | Regular |
| `labelLarge` (tombol) | 16sp | Medium |
| `ScoreNumeral` | 72sp | Bold, `tnum` |
| `ScoreBadgeNumeral` | 28sp | Bold, `tnum` |
| `Eyebrow` | 12sp | Medium, tracking 1.2 |

`tnum` (tabular figures) wajib pada kedua gaya angka: 96 dan 100 harus selebar
sama, agar kolom skor di daftar tidak bergoyang saat nilainya berubah.

### Aturan lintas layar

- **15sp adalah lantai.** Apa pun yang lebih kecil adalah kekeliruan.
  `Eyebrow` 12sp satu-satunya pengecualian, dan ia hanya membawa label yang
  kalimat di bawahnya ulangi lengkap.
- Hanya berat Regular, Medium, SemiBold, dan Bold. Tidak ada Light atau Thin.
- Target sentuh: **56–64dp** di layar terlindungi, **48–58dp** di layar penjaga.
- Tidak ada tombol hanya-ikon di mana pun. Setiap aksi punya label teks.

---

## 4. Inventaris komponen **[TERPASANG]**

Diambil dari kode yang sudah jalan, supaya rekan tim punya rujukan yang sama.

Diambil dari kode yang sudah jalan, supaya rekan tim punya rujukan yang sama.

**Bahasa bentuk.** Satu keluarga radius, dipakai konsisten: **20–22dp** untuk
kartu hero, **16–18dp** untuk kartu isi, **14dp** untuk tombol dan panel, **10dp**
untuk chip. Kartu di atas `card` memakai garis 1dp `outlineVariant`, bukan
bayangan — bayangan M3 nyaris tak terlihat di latar terang dan hilang sama sekali
setelah dikompres YouTube.

| Komponen | Berkas | Bentuk | Dipakai di |
|---|---|---|---|
| **`StatusHero`** | `guardian/components/StatusHero.kt` | Kartu hero, tint mengikuti keadaan, lingkaran lambang + judul `headlineSmall` + kalimat + tiga ubin angka | `WatchListScreen` |
| **`SegmentedTabs`** | `guardian/WatchListScreen.kt` | Pil terisi di dalam alur `surfaceVariant`, label + jumlah | `WatchListScreen` |
| **`AlertRow`** | `guardian/components/AlertRow.kt` | Kartu bergaris: `ScoreBadge`, nama, `BandChip`, kalimat pertama, waktu · status, `›` | `WatchListScreen` |
| **`ScoreBadge`** | `guardian/components/ScoreBadge.kt` | 58dp, tint pita + cincin 1.5dp, angka `tnum` | `AlertRow` |
| **`ScoreBlock`** | `guardian/components/ScoreBlock.kt` | Angka 72sp + jalur 0–100 + nama pita, di dalam kartu tint pita | `AlertDetailScreen` |
| **`BandChip`** | `guardian/components/BandChip.kt` | Pil tint pita berisi nama pita | `AlertRow`, `ScoreBlock` |
| **`ExplanationStack`** | `guardian/components/ExplanationStack.kt` | Satu kartu bergaris per sinyal: eyebrow + satu kalimat utuh | `AlertDetailScreen` |
| **Banner status** | `setup/SetupScreen.kt` | Kartu tint hijau/amber, lingkaran `✓`/`!` + judul + kalimat | `SetupScreen` |
| **Kartu izin** | `setup/SetupScreen.kt` | Kartu bergaris: titik status, judul, alasan, status, tombol 56dp | `SetupScreen` |
| **Kartu pilihan** | `onboarding/RoleSelectionScreen.kt` | Kartu; yang utama terisi `primaryContainer`, yang kedua outlined | `RoleSelectionScreen` |
| **Input kode** | `protectedrole/ProtectedPairingScreen.kt` | `OutlinedTextField` 34sp monospace bold, tracking 8sp, rata tengah | `ProtectedPairingScreen` |

**Komponen yang sengaja tidak dipakai:**

- **`TabRow` M3.** Indikator garis rambut di bawah label membuat tab terpilih dan
  tak terpilih nyaris sama dari jarak lengan. Diganti pil terisi.
- **Tombol hanya-ikon.** Lihat §3.
- **Bayangan / elevation.** Lihat catatan bentuk di atas.

---

## 5. Spesifikasi layar

Delapan layar. Rujukan struktur: `ARCHITECTURE.md` §5.

### 5.1 RoleSelectionScreen **[TERPASANG]**
*Referensi: PDF "Foto sebelum pairing"*

Layar pertama saat aplikasi dibuka. Judul "Siapa yang pakai HP ini?", subjudul
yang memperingatkan pilihan bersifat permanen, lalu dua kartu berbobot visual
sama — tidak ada yang disarankan, tidak ada preseleksi.

Peran tersimpan **write-once** (`RoleStore.chooseRole()` no-op kalau sudah
terisi). Alasannya bukan teknis: penipu yang menuntun korban lewat telepon tidak
boleh bisa membalik HP korban menjadi mode Penjaga dan memutus penjaga asli.

### 5.2 GuardianPairingScreen **[TERPASANG]**
*Referensi: PDF "Foto ketika setelah pencet guardian" (kanan)*

Kode 6 karakter adalah elemen utama: monospace besar dalam kartu `primaryContainer`.
QR ada di bawahnya, lebih kecil dan lebih tenang.

**Kenapa kode lebih utama daripada QR.** Sesi pairing yang realistis adalah
panggilan telepon ke orang tua di kota lain. Kode bisa didiktekan; kamera tidak
membantu. Alfabetnya membuang O/0, I/1, S/5, B/8 karena kode ini dibacakan lewat
telepon. QR dipertahankan untuk saat kedua HP berada di satu ruangan, dan payload-
nya `ronda://pair/{code}` biasa sehingga aplikasi pemindai bawaan bisa membacanya
— tanpa izin `CAMERA` di manifes aplikasi keamanan.

### 5.3 ProtectedPairingScreen **[TERPASANG]**
*Referensi: PDF "Foto ketika setelah pencet victim" (kiri)*

Satu input besar, satu tombol. Satu keputusan per layar.

Empat pesan galat terpisah, masing-masing menuntut tindakan berbeda: kode salah
format, tidak ditemukan, sudah dipakai, kedaluwarsa. Menggabungkannya jadi satu
galat generik akan membuat pengguna lansia mengetik ulang kode yang tidak akan
pernah berhasil.

### 5.4 SetupScreen — juga beranda HP terlindungi **[TERPASANG + 1 USULAN]**
*Referensi: PDF "Aplikasi RONDA pada victim ketika sudah pairing"*

Banner status di atas, lalu tiga kartu izin, lalu catatan privasi.

Setiap kartu izin menjelaskan **manfaat bagi pengguna**, bukan API Android-nya —
"Agar RONDA tahu aplikasi mana yang sedang dibuka. Yang terbaca hanya nama
aplikasi, bukan isinya." Kalimat kedua sama pentingnya dengan yang pertama:
pengguna sedang diminta memberi izin yang terdengar invasif, dan berhak tahu
batasannya.

Status dicek ulang di setiap `onResume()` karena manajemen daya OEM mencabut izin
diam-diam.

**[USULAN] Kartu pengungkapan penjagaan.** Layar ini belum menampilkan siapa
penjaga HP ini. FR-2 mensyaratkan indikator permanen bahwa perangkat sedang
dijaga **dan oleh siapa**; §5 Security no. 5 mensyaratkan hal itu terlihat kapan
saja. Usulan penempatan: tepat di bawah banner status.

```
┌────────────────────────────────────┐
│ HP ini dijaga oleh                 │  20sp bold
│ Anak Anda — kode QTDEZ3            │  22sp bold
│ Mereka akan diberi tahu kalau ada  │  18sp
│ aplikasi berbahaya dipasang di     │
│ HP ini.                            │
│ Hentikan penjagaan                 │  18sp, text link
└────────────────────────────────────┘
```

Ini bukan detail kosmetik. Kalau juri bertanya "apa bedanya RONDA dengan aplikasi
mata-mata?", jawabannya harus terlihat di layar, bukan hanya tertulis di PRD.

### 5.5 UninstallPromptScreen **[TERPASANG]**

Mengambil alih layar saat penjaga meminta penghapusan. Menjelaskan siapa yang
meminta dan kenapa, lalu membuka dialog sistem.

Bagian "apa yang terjadi berikutnya" diberi kotak tersendiri: *"Setelah Anda
menekan tombol di bawah, HP akan menanyakan sekali lagi. Pilih OK atau Hapus pada
pertanyaan itu."* Dialog uninstall Android sangat singkat dan membingungkan bagi
pengguna lansia — layar inilah konteks yang tidak diberikan dialog itu.

"Nanti saja" harus selalu tersedia dan tidak diredupkan. Orang yang memegang HP
selalu memegang keputusan akhir — Android memang tidak memberi jalan lain, dan
RONDA tidak berpura-pura sebaliknya.

Tipografi terbesar di seluruh aplikasi ada di layar ini: muncul di momen menegangkan
dan mungkin dibaca tanpa kacamata.

### 5.6 GuardianHomeScreen **[TERPASANG — akan diganti Dashboard, lihat §6]**
*Referensi: PDF "Aplikasi RONDA pada device guardian ketika sudah pairing"*

Judul, banner "Terhubung dengan HP orang tua Anda", lalu daftar peringatan.

Keadaan kosong ditulis menenangkan, bukan dibiarkan blank — inilah yang paling
sering dilihat penjaga.

### 5.7 AlertDetailScreen **[TERPASANG]**
*Referensi: PDF "Isi di dalem attack pada victim"*

Layar keputusan. Tiga baris bukti (dipasang dari / izin yang diminta / nama
teknis), kartu penjelasan, lalu dua aksi.

**Bukti ditampilkan, bukan skor.** Penjaga mungkin satu-satunya orang di keluarga
yang bisa menilai apakah aplikasi yang baru dipasang orang tuanya wajar. Skor
risiko menghilangkan justru informasi yang dibutuhkan untuk penilaian itu.

"Hapus aplikasi" adalah aksi primer meski destruktif — itu default yang aman
ketika sebuah aplikasi diduga mencuri kode OTP bank.

Tombol digantikan oleh banner sesuai keadaan: **menunggu** setelah keputusan
dikirim, **hasil** setelah HP terlindungi mengonfirmasi. Penjaga tidak pernah
diberi tahu aplikasi sudah terhapus hanya karena perintah terkirim — hanya
setelah OS menyiarkan penghapusan.

### 5.8 WarningOverlay **[TERPASANG]**

Bukan layar Compose. `res/layout/overlay_warning.xml`, digambar `OverlayService`
lewat `WindowManager`, jadi warnanya di luar sistem tema.

Latar merah `#8C1010` penuh, branding RONDA di atas, ikon peringatan, judul
"JANGAN BUKA APLIKASI INI", nama aplikasi, alasan, dan penenangan bahwa penjaga
sudah diberi tahu.

**Tidak ada tombol apa pun di layar ini.** Tidak ada "lanjutkan", "tutup",
"saya mengerti", atau ikon X. Satu-satunya jalan keluar adalah tombol Home dan
keputusan penjaga. *(PRD FR-8)*

Desainnya sengaja tidak menyerupai apa pun yang digambar Android sendiri.
*(PRD §5 Security no. 8)*

---

## 6. Dashboard Penjaga **[USULAN — belum ada di kode]**

Menggantikan `GuardianHomeScreen` saat ini. Tujuannya menjawab tiga pertanyaan
yang belum terjawab hari ini: *apakah perlindungan sedang berjalan? apa yang
sudah terjadi? adakah yang menunggu keputusan saya?*

Spesifikasi di bawah menggabungkan usulan **Block 4F** dari tim dengan empat
koreksi yang ditandai **⚠ Koreksi**. Arah usulan itu lebih baik daripada
rancangan awal dokumen ini: menyinkronkan keadaan izin sesungguhnya jauh lebih
berguna daripada sekadar detak hidup. Keempat koreksi menyangkut hal yang
membuatnya gagal diam-diam kalau dikerjakan apa adanya.

### 6.0 Model data

Usulan Block 4F menaruh field baru di dalam `Pairing`. **Jangan** — lihat
⚠ Koreksi 1. Taruh di node terpisah dengan bentuk yang sama seperti
`alerts/` dan `commands/`:

```
status/{pairingId}/
  protectedLabel     : String    "HP Ibu"
  hasOverlay         : Boolean
  hasUsageStats      : Boolean
  hasNotifications   : Boolean
  isBatteryExempt    : Boolean
  lastSeenAt         : Long      ServerValue.TIMESTAMP
```

```kotlin
enum class SecurityLevel { SAFE, WARNING, DANGER, UNKNOWN }

data class ProtectedStatus(
    val protectedLabel: String? = null,
    val hasOverlay: Boolean = false,
    val hasUsageStats: Boolean = false,
    val hasNotifications: Boolean = false,
    val isBatteryExempt: Boolean = false,
    val lastSeenAt: Long = 0L
)
```

**Penamaan.** Usulan aslinya memakai `isBatteryOptimized`, tetapi logikanya
memperlakukan `false` sebagai buruk. Artinya field itu sebenarnya berarti
"dikecualikan dari optimasi baterai" — kebalikan dari yang dibaca namanya.
Nama yang terbalik seperti ini adalah sumber bug yang akan terbawa berbulan-bulan,
jadi dipakai `isBatteryExempt`.

Sumbernya perlu fungsi baru di `Permissions.kt`, yang saat ini belum ada:

```kotlin
fun isBatteryExempt(context: Context): Boolean {
    val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    return pm.isIgnoringBatteryOptimizations(context.packageName)
}
```

PRD §5 Performance sudah meminta pengecualian optimasi baterai diminta saat
setup dengan penjelasan yang jelas, jadi ini melengkapi ketentuan yang memang
sudah ada — bukan penambahan scope.

### 6.0.1 ⚠ Koreksi 1 — aturan RTDB akan memblokir penulisan ini

Ini yang akan membuat fitur gagal tanpa pesan galat. Aturan `pairings` saat ini
(`ARCHITECTURE.md` §7):

```json
".write": "!data.exists() || (data.child('status').val() === 'pending' && newData.child('status').val() === 'active')"
```

Begitu pairing berstatus `active`, **setiap** penulisan ke node itu ditolak.
Aturan itu memang sengaja dibuat begitu agar kode pairing sekali pakai. Kalau
field status ditaruh di dalam `Pairing`, HP terlindungi tidak akan pernah bisa
memperbarui `lastSeenAt` — dan karena `awaitUpdate()` tidak memeriksa hasil,
kegagalannya tidak terlihat di mana pun. Dashboard akan selamanya menampilkan
nilai default: semua `false`, artinya 🔴 DANGER palsu untuk semua orang.

Karena itu node `status/{pairingId}` terpisah, dengan aturan sederhana yang sama
seperti node lain:

```json
"status": {
  "$pairingId": {
    ".read": true,
    ".write": true
  }
}
```

Alternatifnya menambah aturan per-anak di dalam `pairings/$code`, tapi itu
mengutak-atik satu-satunya aturan yang benar-benar menegakkan keamanan di
proyek ini. Tidak sepadan.

**Catatan keamanan.** Node ini bisa ditulis siapa pun yang tahu kode pairing —
sama seperti `alerts` dan `commands`. Konsekuensinya khusus: penyerang bisa
memalsukan status 🟢 SAFE padahal perlindungan mati. Ini memperluas celah yang
sudah dicatat di `ARCHITECTURE.md` §7 dan perlu ikut disebut di sana.

### 6.0.2 ⚠ Koreksi 2 — pemetaan tingkat keparahan tidak cocok dengan kode

Usulan aslinya:

```kotlin
!hasOverlay || !hasNotifications  -> DANGER
!hasUsageStats || !isBatteryExempt -> WARNING
```

Ini keliru menurut kode yang sudah jalan. `Permissions.kt:54`:

```kotlin
fun canBlock(context: Context) = hasOverlay(context) && hasUsageStats(context)
```

Tanpa `PACKAGE_USAGE_STATS`, `OverlayService` berhenti sendiri dan **pemblokiran
tidak berfungsi sama sekali** — sama parahnya dengan izin overlay dicabut. Itu
DANGER, bukan WARNING.

Sebaliknya, tanpa `POST_NOTIFICATIONS` pemblokiran tetap jalan dan penjaga tetap
menerima alert lewat RTDB; yang hilang hanya notifikasi lokal di HP terlindungi.
Itu WARNING, bukan DANGER.

Pemetaan yang benar:

```kotlin
val securityLevel: SecurityLevel get() = when {
    isStale                            -> SecurityLevel.UNKNOWN   // lihat Koreksi 3
    !hasOverlay || !hasUsageStats      -> SecurityLevel.DANGER    // tidak bisa memblokir
    !hasNotifications || !isBatteryExempt -> SecurityLevel.WARNING // jalan, tapi rapuh
    else                               -> SecurityLevel.SAFE
}
```

### 6.0.3 ⚠ Koreksi 3 — "panggil saat app dibuka" meniadakan gunanya fitur ini

Usulan aslinya memperbarui status **saat aplikasi dibuka**. Masalahnya, justru
kegagalan yang ingin ditangkap fitur ini adalah kegagalan di mana **tidak ada
yang membuka aplikasi**:

- Izin dicabut, RONDA tidak pernah dibuka lagi → penjaga tetap melihat 🟢 SAFE
- HP mati tiga hari → penjaga tetap melihat 🟢 SAFE
- OEM membunuh service → penjaga tetap melihat 🟢 SAFE

Nilai tersimpan hanya bercerita tentang **kapan terakhir kali diperbarui**, bukan
tentang sekarang. Karena itu **basi harus mengalahkan isi**:

```kotlin
private const val STALE_AFTER_MS = 6 * 60 * 60 * 1000L   // 6 jam
val isStale: Boolean get() =
    lastSeenAt <= 0L || System.currentTimeMillis() - lastSeenAt > STALE_AFTER_MS
```

Status `UNKNOWN` ditampilkan abu-abu, **bukan hijau**, dengan kalimat jujur:
"Belum terdengar dari HP orang tua Anda sejak 3 jam lalu." Tidak menuduh, tidak
menenangkan.

Penulisannya juga tidak boleh hanya saat `MainActivity` dibuka. Minimal
`DetectionService.onStartCommand()` ikut menulis, sehingga setiap kali service
dihidupkan ulang oleh sistem, detaknya ikut terbarui. Idealnya `WorkManager`
periodik tiap 1–2 jam, tapi itu bisa menyusul setelah demo.

Ini masalah yang sama dengan yang dicatat di §6.2 dalam bentuk berbeda: layar
yang menampilkan rasa aman padahal tidak tahu apa-apa lebih buruk daripada layar
yang mengaku tidak tahu.

### 6.1 Susunan

```
┌──────────────────────────────────────┐
│ RONDA Penjaga                        │  28sp bold
│                                      │
│ ┌──────────────────────────────────┐ │
│ │  ▣  HP Ibu                       │ │  22sp bold
│ │     Aman Terlindungi             │ │  20sp bold
│ │     Terakhir aktif 2 menit lalu  │ │  15sp
│ └──────────────────────────────────┘ │  warna ikut securityLevel
│                                      │
│ Butuh Tindakan Segera                │  20sp bold
│ ┌──────────────────────────────────┐ │
│ │ Undangan Pernikahan              │ │  20sp bold
│ │ 2 menit lalu         [Menunggu]  │ │  15sp + chip
│ └──────────────────────────────────┘ │  errorContainer
│                                      │
│ Riwayat Pengawasan                   │  20sp bold
│ ┌──────────────────────────────────┐ │
│ │ Aplikasi Bonus                   │ │  18sp
│ │ Kemarin, 14:30        Dihapus    │ │  15sp
│ └──────────────────────────────────┘ │  surfaceVariant
└──────────────────────────────────────┘
```

### 6.2 Kartu status perangkat

Menggantikan banner "Terhubung" yang ada sekarang. Banner itu menampilkan
*status pairing*, bukan *status perlindungan* — kalau `DetectionService` dibunuh
manajemen daya OEM, penjaga tetap melihat "Terhubung" padahal tidak ada yang
mengawasi apa pun. Risiko R6a di PRD sudah menyebut skenario ini.

Empat keadaan, dihitung menurut §6.0.2 dan §6.0.3:

| Tingkat | Kalimat | Wadah |
|---|---|---|
| 🟢 `SAFE` | "Aman Terlindungi" | `primaryContainer` |
| 🟡 `WARNING` | "Perlu Perhatian" + sebab spesifik | `secondaryContainer` |
| 🔴 `DANGER` | "Tidak Aktif — RONDA tidak bisa memblokir" | `errorContainer` |
| ⚪ `UNKNOWN` | "Belum terdengar sejak 3 jam lalu" | `surfaceVariant` |

**Sebutkan sebabnya, jangan hanya tingkatnya.** "Perlu Perhatian" tanpa
keterangan tidak bisa ditindaklanjuti penjaga yang berada di kota lain. Baris
kedua harus menyebut apa yang kurang: "Izin tampilkan di atas aplikasi lain
dicabut" atau "Optimasi baterai masih aktif — RONDA bisa dimatikan sistem".

`UNKNOWN` **tidak boleh hijau**. Tidak tahu bukan berarti aman.

**Ikon.** Usulan aslinya memakai emoji (🟢🟡🔴). Untuk layar penjaga itu masih
bisa diterima, tapi emoji tidak ikut membesar saat ukuran font sistem dinaikkan,
tampilannya berbeda antar-OEM, dan pembaca layar melafalkannya secara janggal.
Lebih baik pakai `Icon` berwarna + teks. Yang wajib: **jangan sampai tingkat
keamanan hanya disampaikan lewat warna** — selalu ada teksnya.

### 6.3 Pemisahan Butuh Tindakan / Riwayat

Inti dari usulan Block 4F, dan perbaikan nyata dari daftar tunggal yang ada
sekarang:

```kotlin
val activeAlerts  = alerts.filter { it.status == Alert.STATUS_PENDING }
val historyAlerts = alerts.filter { it.status != Alert.STATUS_PENDING }
```

**Butuh Tindakan Segera** — hanya muncul kalau tidak kosong, `errorContainer`,
di paling atas. Inilah satu-satunya hal di layar ini yang menuntut tindakan.

**Riwayat Pengawasan** — selalu muncul, `surfaceVariant`, redup. Menampilkan
hasil: "Dihapus" atau "Ditandai Aman".

Saat ini **semua** baris memakai `errorContainer`, sehingga peringatan yang sudah
selesai ditangani tetap terlihat mendesak. Penjaga yang terbiasa melihat layar
merah padahal tidak ada apa-apa adalah penjaga yang akan melewatkan yang sungguhan.

Perbaikan lain pada baris alert:

- **Chip status** di kanan. Sekarang status tidak terlihat sama sekali di daftar.
- **Waktu relatif** di bawah 24 jam ("2 menit lalu"), tanggal absolut di atasnya.
  Sekarang selalu absolut — "15 Agu 2026, 00:19" memaksa penjaga menghitung
  sendiri apakah itu baru saja atau minggu lalu.

**Jangan tambahkan statistik yang tidak bisa dipertanggungjawabkan.** Tidak ada
"skor keamanan", tidak ada "X ancaman dicegah minggu ini", tidak ada grafik tren.
RONDA hanya tahu apa yang dideteksinya sendiri; angka apa pun di luar itu adalah
karangan, dan juri keamanan akan menanyakannya.

### 6.4 String baru

Mengikuti usulan Block 4F, dengan emoji dikeluarkan dari nilai string (lihat §6.2)
dan sebab spesifik ditambahkan (lihat §6.2):

```xml
<string name="status_safe">Aman Terlindungi</string>
<string name="status_warning">Perlu Perhatian</string>
<string name="status_danger">Tidak Aktif</string>
<string name="status_unknown">Belum terdengar</string>

<string name="status_danger_detail">RONDA tidak bisa menutup aplikasi berbahaya di HP ini.</string>
<string name="status_reason_overlay">Izin tampilkan di atas aplikasi lain dicabut.</string>
<string name="status_reason_usage">Izin akses penggunaan aplikasi dicabut.</string>
<string name="status_reason_notifications">Izin notifikasi dicabut.</string>
<string name="status_reason_battery">Optimasi baterai masih aktif — RONDA bisa dimatikan sistem.</string>
<string name="status_last_seen">Terakhir aktif %1$s</string>
<string name="status_never_seen">Belum pernah terhubung.</string>

<string name="section_active_alerts">Butuh Tindakan Segera</string>
<string name="section_history">Riwayat Pengawasan</string>
<string name="history_uninstalled">Dihapus</string>
<string name="history_safe">Ditandai Aman</string>
```

### 6.5 ⚠ Koreksi 4 — satu perangkat, bukan banyak

Usulan Block 4F menulis "untuk setiap device yang dilindungi". RONDA saat ini
**satu penjaga ↔ satu HP terlindungi**: `RoleStore.pairingId` adalah satu
`String`, dan seluruh skema RTDB memakai satu `pairingId`.

Mendukung banyak perangkat berarti mengubah model data, penyimpanan lokal, dan
alur pairing — jauh melampaui 2,5 jam, dan tidak ada di PRD. Rancang untuk satu
perangkat sekarang; susun kartu status sebagai komponen tersendiri supaya
daftarnya mudah dijadikan banyak nanti.

`protectedLabel` ("HP Ibu") juga belum punya sumber. Paling murah: penjaga
mengetiknya sendiri di `GuardianPairingScreen` sebelum kode dibuat — HP terlindungi
tidak perlu tahu namanya sama sekali.

### 6.6 Keadaan kosong

Dipertahankan apa adanya — sudah tepat. Bagian "Butuh Tindakan Segera"
disembunyikan saat kosong; bagian "Riwayat Pengawasan" menampilkan teks kosong
yang menenangkan, bukan daftar kosong.

### 6.7 Dampak ke PRD

PRD §5.4 menyatakan: *"Only package metadata is transmitted: package name, app
label, install source, declared permissions, timestamp."* Fitur ini menambah
kategori data baru — **keadaan izin perangkat terlindungi**.

Ini masih sejalan dengan minimisasi data: yang dikirim adalah keadaan RONDA
sendiri, bukan data pribadi pengguna. Tapi §5.4 tetap perlu diperbarui supaya
daftarnya jujur. Kalau juri membandingkan dokumen dengan lalu lintas jaringan
sungguhan, selisih yang tidak tercatat lebih merugikan daripada kategori data
tambahan yang diakui terbuka.

### 6.8 Verifikasi

Mengikuti Block 4F, ditambah dua kasus yang terlewat:

- [ ] Cabut izin overlay di HP terlindungi → buka RONDA → penjaga melihat 🔴
- [ ] Cabut izin akses penggunaan → penjaga melihat 🔴 (bukan 🟡 — lihat §6.0.2)
- [ ] Cabut izin notifikasi saja → penjaga melihat 🟡, pemblokiran tetap jalan
- [ ] Alert baru → masuk "Butuh Tindakan Segera" berwarna merah
- [ ] Uninstall diselesaikan → pindah ke "Riwayat Pengawasan" dengan tanda "Dihapus"
- [ ] **Majukan jam HP penjaga 8 jam** → status jadi ⚪ "Belum terdengar",
      bukan tetap 🟢
- [ ] **Matikan HP terlindungi sepenuhnya** → setelah ambang basi terlewat,
      status jadi ⚪, bukan 🟢

---

## 7. Daftar periksa aksesibilitas

Dipakai saat meninjau layar mana pun sebelum digabung.

- [ ] Isi teks ≥20sp di layar terlindungi, ≥16sp di layar penjaga
- [ ] Rasio kontras ≥7:1 untuk semua teks
- [ ] Target sentuh ≥56dp (terlindungi) / ≥48dp (penjaga)
- [ ] Setiap tombol punya label teks, tidak ada tombol hanya-ikon
- [ ] Tidak ada informasi yang hanya disampaikan lewat warna — status selalu
      punya teks pendamping
- [ ] Layar tetap terbaca saat ukuran font sistem 200%
- [ ] Semua layar bisa di-scroll; tidak ada yang terpotong di layar kecil
- [ ] Copy lolos uji "bisakah ini dibacakan lewat telepon ke orang tua saya?"

---

## 8. Selisih antara desain dan PRD

Tercatat supaya tidak hilang, bukan untuk dikerjakan sekarang.

| # | Selisih | Sumber | Dampak |
|---|---|---|---|
| 1 | Dynamic color aktif — identitas visual tidak stabil, kontras tidak terkendali | §2.1 | Tinggi |
| 2 | `SetupScreen` tidak menampilkan siapa penjaganya | FR-2, §5 Security no. 5 | Tinggi |
| 3 | Teks 15sp di beberapa layar terlindungi, PRD minta ≥20sp | §5 Usability | Sedang |
| 4 | Banner "Terhubung" mengklaim lebih dari yang diketahui — diperbaiki §6.2 | R6a | Sedang |
| 5 | Status alert tidak terlihat di daftar penjaga — diperbaiki §6.3 | — | Rendah |
| 6 | Stempel waktu selalu absolut — diperbaiki §6.3 | — | Rendah |
| 7 | PRD §5.4 belum menyebut keadaan izin sebagai data terkirim | §6.7 | Sedang |
| 8 | `ARCHITECTURE.md` §7 belum menyebut node `status/` bisa dipalsukan | §6.0.1 | Sedang |

Nomor 1 dan 2 yang paling layak dikerjakan lebih dulu setelah demo terekam.
Keduanya menyentuh hal yang akan ditanyakan juri: konsistensi tampilan lintas
perangkat, dan pembeda antara alat perlindungan dan stalkerware.

---

## 9. Urutan kerja yang disarankan

Dokumen ini **tidak** meminta perubahan apa pun sekarang. Saat rekan tim selesai
dengan fitur yang sedang dikerjakan, urutan yang paling kecil risikonya:

1. **Kunci palet** (`Theme.kt` + `Color.kt`) — satu file, tidak menyentuh layar
   mana pun, langsung memperbaiki selisih no. 1
2. **Pemisahan Butuh Tindakan / Riwayat** (§6.3) — murni dari data alert yang
   sudah ada, tanpa Firebase, tanpa izin baru. Bagian paling terlihat dari
   Block 4F dan paling murah
3. **Kartu pengungkapan penjagaan** di `SetupScreen` — satu komponen baru,
   memperbaiki selisih no. 2
4. **Naikkan ukuran teks** di layar terlindungi — perubahan mekanis, selisih no. 3
5. **Aturan RTDB untuk node `status/`** (§6.0.1) — harus lebih dulu, kalau tidak
   langkah 6 gagal tanpa pesan galat
6. **Sinkronisasi status keamanan** (§6.0) — `Permissions.isBatteryExempt()`,
   penulisan dari `DetectionService`, kartu status di dashboard

Langkah 1–4 tidak menyentuh `detection/`, `overlay/`, atau `alert/` sama sekali,
sehingga aman dikerjakan paralel dengan pekerjaan fitur.

Langkah 5–6 menyentuh `DetectionService`, `Permissions`, skema RTDB, dan aturan
Firebase. **Jangan dikerjakan bersamaan dengan perbaikan P0** di
`DetectionService.onCreate()` (lihat catatan review 14 Agustus) — keduanya
mengubah file yang sama dan P0 lebih mendesak.

Estimasi 2,5 jam di usulan Block 4F realistis untuk langkah 2 saja. Langkah 5–6
menambah setidaknya sekali putaran uji dua emulator, karena kegagalan aturan
Firebase tidak muncul sebagai galat — hanya sebagai nilai yang tidak pernah
berubah.
