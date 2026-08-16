# RONDA — Naskah Video Submisi

**Track:** Human-Centric Security
**Target durasi:** 2:50 (batas 2–3 menit)
**Tim:** Dhanes, Alek, Malik
**Status:** draf untuk penyuntingan
**Cakupan POC yang direkam:** Block 0–4 (sudah jalan) + Block 4A (7 izin
berbahaya) + Block 4S (Matriks Skor Bahaya). Lihat `implementation_plan.md`.
**Perangkat:** dua **emulator Android Studio** (AVD `Pixel_6` = HP orang tua,
`RONDA_Guardian` = HP penjaga), dikendalikan lewat `scripts/ronda`. Belum ada
HP fisik — cara menyampaikannya ada di §3.

> **Aturan bahasa dokumen ini.** Seluruh penjelasan, arahan panggung, dan catatan
> produksi ditulis dalam bahasa Indonesia supaya satu tim langsung paham.
> **Hanya kalimat yang benar-benar diucapkan di depan kamera** (di §5, di dalam
> blok kutip `>`) yang berbahasa Inggris — juri menonton dalam bahasa Inggris,
> jadi bagian itu jangan diterjemahkan.

---

## 1. Sudut pitch yang kita ambil

Hampir semua peserta lain di track ini akan membangun alat *pelatihan* — kuis,
simulasi, permainan kesadaran phishing. Semuanya berangkat dari asumsi yang sama:
pengguna bisa dibuat lebih pintar.

**Tesis kita justru kebalikannya, dan inilah seluruh isi pitch:** orang berumur
70 tahun yang sedang ditelepon penipu tidak bisa dilatih keluar dari situasi itu.
Maka RONDA berhenti mencoba. RONDA memindahkan keputusan keamanan ke satu orang
di keluarga yang memang sudah mampu mengambilnya — anaknya yang sudah dewasa —
dan melakukannya tanpa meminta orang tua memahami apa pun.

Itu jawaban yang berpusat pada manusia, bukan jawaban teknis. Ucapkan persis
dengan kerangka itu, karena itulah yang memisahkan kita dari 200 submisi lain.

Deskripsi track memberi kita dua keuntungan. Pakai keduanya:

- Track ini secara eksplisit menyebut *"enhancing user experience (UX) design to
  make secure choices more intuitive and accessible for everyone"* — itu RONDA,
  kata per kata.
- Angka 64% human error di deskripsi track adalah **studi milik Kaspersky
  sendiri**, dan Kaspersky ikut menilai. Membuka dengan angka itu berarti
  membuka di lapangan mereka.

---

## 2. Konsep video

**Format.** Tiga suara, satu argumen yang menyambung. Bukan tiga presentasi
terpisah — setiap pembicara menyerahkan satu kalimat untuk diselesaikan pembicara
berikutnya.

**Tulang punggung visual.** Video ini tidak pernah menampilkan slide. Yang
ditampilkan adalah dua layar Android yang berjalan berdampingan. Setiap klaim
abstrak langsung dipotong ke bendanya: berkas penipuan yang masuk, blokir yang
muncul, layar penjaga yang menyala.

**Dua emulator, dan kita mengatakannya.** POC ini berjalan di dua AVD Android
Studio, bukan dua HP fisik. Jangan pernah menyebut atau menyiratkan "two real
devices" di narasi, dan jangan membingkai gambarnya seolah kamera sedang
menyorot HP di atas meja. Sebut apa adanya: *two Android emulators, running side
by side.* Alasannya bukan sekadar kejujuran, tapi juga taktik — juri keamanan
yang melihat bezel emulator lalu mendengar klaim "perangkat sungguhan" akan
mencurigai seluruh sisa video. Sebaliknya, tim yang menyebut batasannya lebih
dulu terdengar seperti tim yang tahu apa yang sedang dikerjakannya. Argumen
lengkap kenapa emulator sudah cukup untuk membuktikan konsep ini ada di
`implementation_plan.md` bagian "Batasan perangkat".

**Nada.** Tenang dan spesifik. Tidak ada stok video hacker berkerudung, tidak ada
hentakan musik dramatis, tidak ada transisi glitch. Subjeknya adalah ibu
seseorang yang kehilangan tabungannya — perlakukan dengan serius, dan video ini
akan menonjol justru karena tidak berteriak.

**Bahasa.** Narasi bahasa Inggris. Aplikasi tetap **berbahasa Indonesia** di
layar dengan subtitle Inggris — fakta bahwa teksnya ditulis dalam bahasa
Indonesia yang sederhana untuk lansia **itu sendiri** adalah argumen
human-centric-nya, jadi biarkan juri melihatnya.

**Satu risiko yang kita ambil.** Kita membuka dengan artefak penipuannya
langsung: tanpa logo, tanpa title card, tanpa perkenalan tim. Hanya berkas yang
masuk. Title card baru muncul di 0:36.

**Garis yang tidak boleh kabur.** Hook (0:00–0:12) adalah *ilustrasi* skenario
penipuan; demo (0:56–2:10) adalah *bukti*. Keduanya harus terlihat berbeda —
beri hook grade warna yang sedikit lebih redup dan caption `Ilustrasi skenario`
di pojok. Kalau penonton mengira chat WhatsApp di detik ke-3 adalah bagian dari
demo teknis, kita kehilangan kredibilitas yang justru sedang kita bangun.

---

## 3. Yang baru di POC ini: Skor Bahaya

Ini penambahan terbesar sejak draf pertama naskah, dan **harus masuk video**.
Rincian lengkap ada di `implementation_plan.md` Block 4S.

Sebelumnya keluaran RONDA biner: BAHAYA atau tidak. Masalahnya, aplikasi yang
sekadar sideload dan minta izin overlay mendapat layar merah yang sama dengan
trojan perbankan lengkap. Penjaga yang beberapa kali melihat alarm untuk hal
sepele akan berhenti membaca alarm berikutnya. Itu **alarm fatigue** — dan itu
kegagalan human-centric, bukan kegagalan teknis.

Sekarang setiap aplikasi mendapat **skor 0–100** dari tiga dimensi:

| Dimensi | Maks | Yang dijawab |
|---|---|---|
| **Kemampuan** | 60 | Apa yang bisa dilakukan aplikasi ini kalau memang jahat |
| **Asal pasang** | 30 | Dari mana ia datang — WhatsApp, peramban, atau Play Store |
| **Penyamaran** | 10 | Apakah namanya berpura-pura jadi instansi resmi |

Tiga pita keparahan:

| Skor | Pita | Yang dilakukan RONDA |
|---|---|---|
| 70–100 | **BAHAYA** | Blokir layar + alert prioritas tinggi |
| 45–69 | **WASPADA** | Alert ke penjaga, **tanpa** blokir — penjaga yang memutuskan |
| 0–44 | **AMAN** | Hanya dicatat, tidak ada notifikasi |

**Cara menceritakannya di video — ini yang penting.** Jangan jual skornya sebagai
"kami punya algoritma". Jual **rinciannya**. Aturan desainnya: *skor tidak pernah
muncul tanpa alasannya.* Angka telanjang mengubah penjaga menjadi tombol "OK".
Angka beserta tiga alasannya membuat penjaga jadi pengambil keputusan — dan itu
persis definisi human-centric security.

Momen pita **WASPADA** di demo adalah kartu truf kita untuk kriteria USP: kita
satu-satunya yang menunjukkan sistemnya **menahan diri untuk tidak memblokir**.
Jangan dipotong.

---

## 4. Anggaran waktu

> ⚠ **Naskah di §5 saat ini KEPANJANGAN. Baca bagian ini sebelum merekam.**

Hitungan kata sebenarnya dari naskah yang tertulis di §5, bukan target:

| # | Bagian | Pembicara | Kata aktual | Durasi @155 wpm |
|---|---|---|---|---|
| 1 | Hook | Dhanes | 40 | 0:16 |
| 2 | Masalah | Dhanes | 64 | 0:25 |
| 3 | Solusi | Malik | 58 | 0:22 |
| 4 | Demonstrasi | Alek | 205 | 1:19 |
| 5 | Kematangan & jalan ke depan | Malik | 131 | 0:51 |
| 6 | Penutup | Dhanes | 57 | 0:22 |
| | **Total** | | **555** | **3:35** |

Ditambah jeda diam yang wajib ada — tahan 2 detik di overlay, 3 detik di Kartu
Skor, 2 detik di caption pairing — durasi sebenarnya mendekati **3:45**. Batasnya
3:00.

**Anggaran yang muat:** ±428 kata terucap (2:46) + ±10 detik jeda visual =
**2:56**. Artinya **127 kata harus dipotong**. Daftar potongan yang persis ada di
§8 — itu bukan opsi cadangan, itu pekerjaan wajib sebelum rekam.

Penyebabnya bukan satu bagian yang gemuk: bagian 4 bertambah karena penambahan
Skor Bahaya dan kalimat kejujuran soal emulator, sementara bagian 5 memang sudah
melebihi anggaran sejak draf sebelumnya (104 kata untuk jatah 68).

> **Jangan lewat 3:00.** Juri yang menyaring ratusan entri akan menghentikan
> video di batas waktu. Sebuah penutup yang terpotong di tengah kalimat merusak
> kesan lebih parah daripada satu klaim yang hilang.

---

## 5. Naskah

Arahan panggung ditulis *miring* dan berbahasa Indonesia. Teks di layar ditulis
dalam `format kode`. **Kalimat di dalam blok kutip `>` adalah yang diucapkan —
biarkan berbahasa Inggris.**

---

### 1 — HOOK · Dhanes · 0:00–0:12

*Cold open. Tanpa logo. Rekaman layar penuh sebuah chat WhatsApp. Sebuah berkas
masuk:* `Undangan Pernikahan.apk`. *Kursor/ibu jari melayang di atasnya. Freeze.*
*Caption kecil di pojok:* `Ilustrasi skenario` *— lihat §2, hook bukan demo.*

> This is a wedding invitation.
>
> It is also how Indonesian families are losing their savings.
>
> She didn't click a phishing link. She didn't reuse a password. She installed an
> app — because someone she trusted asked her to.

`64% of cyber incidents come from human error` *— muncul perlahan di sepertiga bawah*
`— Kaspersky, 2023`

> **Catatan produksi.** Kalau kalian menemukan kasus nyata yang benar-benar
> pernah dilaporkan beserta sumbernya, sebutkan nominalnya dan tampilkan
> sitasinya di layar — dampaknya jauh lebih kuat. **Jangan** mengarang angka atau
> mengarang korban. Anekdot fiktif dalam kompetisi keamanan adalah cara tercepat
> kehilangan kepercayaan juri.
>
> **Sumber gambar hook.** Karena kita belum memakai HP fisik, chat WhatsApp ini
> paling praktis direkam dari WhatsApp Web di layar laptop, atau dari WhatsApp
> yang dipasang di emulator ber-Play Store. Keduanya sah selama caption
> `Ilustrasi skenario` terpasang. Yang tidak boleh: memotong gambar ini
> berdampingan dengan gambar demo tanpa pembatas, sehingga tampak satu rangkaian
> rekaman yang sama.

---

### 2 — MASALAH · Dhanes · 0:12–0:36

*Potong ke Dhanes, menghadap kamera. Latar polos.*

> The usual answer to that number is training. Teach people to spot the scam.
>
> But you cannot train your way out of this one. She's seventy. The scammer is on
> the phone with her *right now* — sounding official, sounding urgent. Every
> second she hesitates, he pushes harder.
>
> The security decision sits with the person least able to make it, at the worst
> possible moment.

*Jeda. Tahan di kalimat itu.*

---

### 3 — SOLUSI · Malik · 0:36–0:56

*Title card: logo RONDA, lalu* `RONDA — Pelindung Keluarga`

> So we stopped trying to make her an expert. We move the decision to her son.
>
> This is RONDA — named after the Indonesian tradition where neighbours take
> turns keeping watch at night, so everyone else can sleep safely.
>
> Two phones, one pairing. When a dangerous app lands on her phone, RONDA blocks
> it there — and wakes him.

---

### 4 — DEMONSTRASI · Alek · 0:56–2:10

*Dua jendela emulator Android Studio bersebelahan, direkam sebagai satu tangkapan
layar menyambung. Beri label di layar:* `HP Orang Tua — emulator` *dan*
`HP Penjaga — emulator`. *Pairing tidak diperagakan — tampilkan caption*
`Sudah dipasangkan — kode 6 huruf, dibacakan lewat telepon` *selama 2 detik.*

> Two Android emulators, running live. Left, the parent. Right, the guardian —
> in real life, another city entirely.
>
> I install our test app. It is harmless — one permission in the manifest, an
> empty screen, nothing else. We never use real malware, and we simulate the
> delivery, not the detection.

*Instalasi berjalan lewat* `scripts/ronda attack com.whatsapp`.
*Caption kecil, tahan 2 detik:* `Instalasi diatribusikan ke WhatsApp — adb -i`

> RONDA scores it. Not a yes-or-no verdict — a number, out of a hundred, built
> from three things.

*Kartu Skor muncul di HP penjaga. Zoom pelan. Tahan 3 detik penuh — tiap baris
harus terbaca.*

`80 / 100 — BAHAYA`
`Kemampuan 40/60 — bisa membaca SMS dan kode OTP`
`Asal pasang 30/30 — dikirim lewat WhatsApp, bukan dari Play Store`
`Penyamaran 10/10 — memakai nama undangan`

> What it can do. Where it came from. And whether it is pretending to be
> something official. Eighty out of a hundred.
>
> That middle number is the one we care about. RONDA is not guessing the channel
> — it reads the installer Android itself recorded.
>
> Watch both screens.

*Emulator orang tua membuka aplikasinya. Overlay merah RONDA menutupinya. Layar
emulator penjaga menampilkan alert prioritas tinggi.* ***Tahan shot ini dua detik
penuh — ini frame terpenting di seluruh video.***

*Emulator tidak bergetar dan notifikasinya tidak berbunyi seperti HP sungguhan.
Ganti isyarat itu dengan visual: satu hentakan zoom halus ke jendela penjaga saat
notifikasi muncul. Jangan menambahkan sound effect getaran — itu memalsukan
sesuatu yang tidak terjadi.*

> Covered on her phone. Offline, in under a second. And he already has the
> reasons — not just the score.
>
> He decides. Uninstall.

*Alek menekan* Hapus aplikasi. *HP orang tua menampilkan konfirmasi.*

> She confirms — Android requires that. No app can delete another silently. So we
> tell her exactly what is about to happen.

*Konfirmasi. Aplikasi hilang. Overlay bersih. Layar penjaga ter-update.*

> Gone. And he sees it.

*Potong ke aplikasi kedua yang sudah disiapkan — hanya `SYSTEM_ALERT_WINDOW`,
nama netral, dipasang **tanpa** flag `-i` sehingga terbaca sebagai unduhan biasa
di luar Play Store. Skor 60.*

> Now the part that matters most. This app scores sixty. RONDA does not block it.
> It only tells him.
>
> A system that shouts at everything gets ignored. So ours only shouts when it
> has the evidence to.

> **Catatan produksi.** Beat terakhir ini (~15 detik) adalah pembeda kita. Kalau
> waktunya mepet, potong kalimat konfirmasi Android lebih dulu, jangan yang ini.

---

### 5 — KEMATANGAN & JALAN KE DEPAN · Malik · 2:10–2:36

*Grafis sederhana: empat balok — Detect · Score · Block · Respond — semuanya
terisi penuh. Lalu tiga balok kosong di bawahnya.*

> That is the MVP, running today on a stock Android system image. No root, no
> custom ROM, no server-side model — the scoring runs on the phone, offline.
>
> RONDA reads only what Android already exposes: the installer, the manifest, the
> app label. No message content ever leaves the phone.
>
> And one thing we will never add. RONDA uses no accessibility service and asks
> for no SMS permission — that is the exact toolkit the trojans use. We refuse to
> ship the weapon we are defending against.
>
> What you just watched ran on emulators, so what we have proven is the logic,
> not yet the endurance. Next is hardware — the aggressive battery managers
> Indonesian phones ship with. Then a hard block through Device Owner, and
> anonymous score data feeding the national fraud centre.

---

### 6 — PENUTUP · Dhanes · 2:36–2:56

*Kembali ke kamera. Lalu dorongan pelan ke dua jendela emulator yang bersebelahan.*

> Sixty-four percent of incidents come from human error.
>
> RONDA's answer isn't a better warning. It's a second pair of eyes — someone who
> loves you, who is not on the phone with the scammer, and who can say no on your
> behalf.
>
> That's *ronda*. Neighbours taking turns to keep watch.
>
> Now it works on your parents' phone.

*Potong ke hitam. Logo.*

`RONDA — Pelindung Keluarga`

---

## 6. Pemenuhan kriteria penilaian

Periksa tabel ini sebelum mengunggah. Keenam kriteria harus terbaca jelas oleh
orang yang menonton **satu kali saja**.

### Kriteria 1 — Kesesuaian dengan track (Human-Centric Security)

| Di menit | Yang membuktikan |
|---|---|
| 0:12–0:36 | Tesis dinyatakan langsung: manusia adalah permukaan serangan, dan pelatihan tidak menyelesaikannya |
| 0:36–0:56 | Solusinya sosial, bukan teknis — keputusan dipindahkan, bukan korban dilatih |
| 1:20–2:10 | Rincian skor berbahasa manusia, teks aplikasi bahasa Indonesia sederhana, konfirmasi jujur ke orang tua |
| 2:36–2:56 | Ditutup dengan metafora budaya, bukan dengan fitur |

Track meminta *"UX design to make secure choices more intuitive and accessible"*.
Kutipan itu boleh ditempel di layar pada 0:36 sebagai teks kecil — bukti bahwa
kita membaca briefnya.

### Kriteria 2 — Unique Selling Proposition

| Di menit | Yang membuktikan |
|---|---|
| 0:12–0:36 | **USP utama:** semua peserta lain melatih pengguna; kita memindahkan keputusannya. Nyatakan sebagai kontras eksplisit |
| 1:55–2:10 | **USP kedua:** skor 60 → tidak diblokir. Kita satu-satunya yang menunjukkan sistemnya menahan diri |
| 0:36–0:44 | Nama & metafora *ronda* — konteks budaya Indonesia yang tidak bisa ditiru solusi impor |

### Kriteria 3 — Kelayakan teknis

| Di menit | Yang membuktikan |
|---|---|
| 2:10–2:18 | "stock Android system image, no root, no custom ROM, no server-side model" — dinyatakan lisan |
| 1:00–1:15 | Semua sinyal berasal dari API Android publik: `getInstallSourceInfo()`, `getPackageInfo(GET_PERMISSIONS)` |
| Sepanjang demo | Rekaman layar asli, bukan mockup. Ini yang paling meyakinkan |

Sinyalnya terbatas dan disengaja: kita membaca **deklarasi izin**, bukan perilaku
runtime. Itulah sebabnya APK contoh yang tidak berbahaya sudah cukup untuk
membuktikan konsep — dan sebabnya deteksi bisa berjalan offline dalam waktu di
bawah satu detik.

**Kalau juri mempersoalkan emulator** (siapkan jawabannya, kemungkinan besar
ditanya): AVD memakai *system image* AOSP/Google APIs yang sama dengan HP
produksi, dan kedua API di atas adalah API publik yang berperilaku identik. Tidak
ada satu baris pun jalur khusus emulator di kode kami, tidak ada root, tidak ada
perintah shell yang dipanggil aplikasi. Pairing dan alert pun melintasi Firebase
RTDB sungguhan — dua emulator itu dua klien jaringan terpisah, bukan dua proses
yang saling memanggil. Yang **belum** terbukti adalah daya tahannya terhadap
pembunuh proses latar belakang khas OEM; itu kami sebut sendiri di 2:24, bukan
menunggu ditanya.

### Kriteria 4 — Proof of Concept

| Di menit | Yang membuktikan |
|---|---|
| 0:56–2:10 | Satu take menyambung, dua emulator berjalan bersamaan, deteksi sungguhan, tanpa potongan yang menyembunyikan kegagalan |
| 1:45–1:55 | Alur respons penuh: penjaga memutuskan → orang tua konfirmasi → aplikasi hilang → penjaga melihat hasilnya |
| 2:24 | Batasan perangkat disebut sendiri, bukan disembunyikan |

Empat block sudah terverifikasi end-to-end (log verifikasi ada di `TODO.md`).
Kalau ada slide penutup opsional, cantumkan satu baris: `Block 0–4 verified ·
4A + 4S shipped · 5 consecutive clean runs · 2 AVD, Android 13`.

**Yang menjadikan ini PoC dan bukan prototipe:** yang dibuktikan adalah rantai
keputusannya berjalan utuh dari pemasangan APK sampai aplikasi terhapus, melintasi
dua perangkat terpisah dan satu basis data sungguhan. Perangkatnya virtual;
rantainya tidak.

### Kriteria 5 — Tingkat keamanan teknologi & potensi paten

Ini kriteria yang paling mudah terlewat. **Jangan andalkan penonton
menyimpulkannya sendiri** — Malik menyebutkannya lisan di 2:18–2:30.

| Klaim | Cara membuktikan di video |
|---|---|
| **Permukaan serangan minimal** | "no accessibility service, no SMS permission — that is the exact toolkit the trojans use". Ini janji desain yang bisa diperiksa siapa pun dari manifest kita |
| **Data minimization** | "RONDA reads only what Android already exposes: the installer, the manifest, the app label. No message content ever leaves the phone." |
| **Skor bisa diaudit** | Tidak ada model kotak hitam. Tiga dimensi, bobot tetap, bisa diperiksa dan dibantah — penting untuk keamanan *dan* untuk klaim paten |
| **Konfirmasi tidak bisa dilewati** | Kita akui Android mewajibkan konfirmasi pengguna. Mengakui batasan secara terbuka bernilai lebih tinggi di mata juri keamanan daripada 8 detik yang dihemat |

**Sudut patentabilitas — sebutkan kalau ada sesi tanya jawab, jangan di video
(tidak cukup waktu).** Yang berpotensi dilindungi bukan pembacaan izinnya (itu
sudah umum), melainkan kombinasinya: *matriks skor tiga dimensi yang menyertakan
saluran distribusi sebagai dimensi berbobot penuh, digabung dengan delegasi
keputusan ke perangkat wali beserta rinciannya, di mana pita menengah sengaja
memberi peringatan tanpa memblokir.* "Asal pasang bernilai 30 dari 100" adalah
klaim yang tidak lazim — antivirus konvensional memperlakukan asal pasang sebagai
filter biner, bukan sebagai bobot.

Jangan pernah menyebut kata "patented" di video. Belum diajukan; mengklaimnya
adalah kebohongan yang bisa dicek.

### Kriteria 6 — Skalabilitas & kesiapan deployment

| Di menit | Yang membuktikan |
|---|---|
| 2:10–2:18 | Berjalan di Android bawaan, minSdk 30 — mencakup mayoritas HP yang beredar di Indonesia tanpa perangkat keras khusus |
| 2:14–2:18 | Skoring berjalan **di perangkat**, offline. Biaya server tidak naik seiring jumlah pengguna — hanya sinkronisasi alert yang menyentuh jaringan |
| 2:24–2:30 | Langkah kesiapan berikutnya disebut spesifik: pengujian di HP fisik terhadap pembunuh proses OEM |
| 2:30–2:36 | Roadmap deployment: Device Owner, feed data anonim ke pusat penanganan penipuan nasional |

Kriteria ini adalah satu-satunya tempat di mana emulator benar-benar melemahkan
posisi kita — "deployment readiness" sulit diklaim tanpa uji perangkat keras.
Karena itu jangan menghindarinya, **balikkan**: tim yang menyebutkan bahwa
pembunuh proses Xiaomi/Oppo/Vivo adalah risiko deployment nomor satu di Indonesia
justru menunjukkan paham medan sebenarnya. Jauh lebih meyakinkan daripada tim
yang mengklaim "siap deploy" tanpa pernah menyebut masalah itu ada.

Kalimat penutup Malik menyebut "national fraud centre" — itu jalur kemitraan
IASC/OJK. Kalau ditanya juri, jawabannya: **seluruh model kemitraan bekerja
dengan data yang sudah ada di record alert** (`packageName`, `installSource`,
`flaggedPermissions`, `riskScore`). Tidak ada data baru yang perlu dikumpulkan,
tidak ada data pribadi yang berpindah. Detail di `implementation_plan.md` Block
4E (backlog).

---

## 7. Sebelum menekan tombol rekam

**Hal yang memblokir — demo gagal tanpa ini:**

- [ ] Block 4A selesai dan keempat varian APK terbukti terdeteksi
- [ ] Block 4S selesai; skor di HP penjaga **sama persis** dengan di HP orang tua
- [ ] Siapkan APK "pita WASPADA" (hanya `SYSTEM_ALERT_WINDOW`, nama netral,
      dipasang dari peramban) dan pastikan skornya benar-benar 60 — kalau
      angkanya meleset, seluruh beat terkuat di video ikut gagal
- [ ] Jalankan alur penuh **lima kali berturut-turut tanpa satu pun kegagalan**
      sebelum merekam. Ini syarat dari PRD
- [ ] Ketiga izin sudah diberikan di AVD orang tua **sebelum** perekaman dimulai
- [ ] Terapkan pemotongan §8 dan hitung ulang durasinya — naskah mentahnya 3:35

**Persiapan emulator:**

- [ ] `scripts/ronda up` menyalakan kedua AVD (`Pixel_6`, `RONDA_Guardian`);
      pastikan keduanya sudah **cold boot**, bukan lanjut dari snapshot — state
      sisa demo sebelumnya adalah penyebab paling sering "kok tadi bisa"
- [ ] `cmd_attack` / `cmd_reset` di `scripts/ronda` sudah menerima argumen flavor
      (lihat catatan di `implementation_plan.md` Block 4A) — kalau belum, skrip
      hanya mengenal satu package dan demo empat varian akan macet
- [ ] Verifikasi atribusi installer: jalankan `adb shell dumpsys package
      com.ronda.testsample | grep installerPackageName` dan pastikan hasilnya
      `com.whatsapp`. Kalau bukan, skor turun 5 poin dan angka di layar tidak
      cocok dengan naskah
- [ ] Kedua emulator punya akses internet (Firebase RTDB). Emulator di balik VPN
      host sering gagal diam-diam — uji dengan satu alert percobaan
- [ ] Tutup semua jendela Android Studio, logcat, dan terminal dari area rekaman.
      Yang terlihat hanya dua jendela emulator
- [ ] Susun kedua jendela emulator berdampingan pada tinggi yang sama, jarak
      antar-jendela rapat. Latar desktop polos gelap

**Kerapian:**

- [ ] Rekam dengan perekam layar desktop (OBS) pada satu region yang mencakup
      kedua jendela emulator. Jangan merekam kamera yang diarahkan ke monitor
- [ ] Set ukuran font sistem kedua AVD ke default agar tata letaknya sesuai
      rancangan
- [ ] Emulator tidak bergetar dan notifikasinya sunyi — pastikan momen alert
      ditegaskan secara visual (zoom halus), bukan dengan efek suara palsu
- [ ] Rekam narasi terpisah dari tangkapan layar, lalu disinkronkan. Narasi
      langsung di atas demo langsung menghasilkan satu take layak pakai dari dua
      puluh
- [ ] Subtitle untuk seluruh video. Juri mungkin menonton tanpa suara
- [ ] Angka pada Kartu Skor harus terbaca setelah dikompres YouTube — rekam pada
      resolusi jendela emulator yang cukup besar, lalu cek dengan memutar di
      ponsel sebelum finalisasi
- [ ] Semua label di layar menyebut "emulator", tidak ada satu pun frame yang
      menyiratkan HP fisik
- [ ] Unggah sebagai unlisted, lalu **buka tautannya di jendela penyamaran** untuk
      memastikan bisa diputar tanpa login

**Cadangan:**

- [ ] Simpan satu rekaman bersih khusus bagian demo. Kalau take langsung gagal di
      hari H, bagian ini bisa disisipkan tanpa merekam ulang bagian bicara

---

## 8. Pemotongan wajib — 127 kata

Ini bukan daftar cadangan. Naskah §5 berdurasi 3:35; tanpa pemotongan ini video
melewati batas dan dihentikan juri. Kerjakan dari atas, lalu hitung ulang.

| # | Yang dipotong | Di mana | Kata |
|---|---|---|---|
| 1 | Seluruh kalimat asal-usul nama *ronda* ("named after the Indonesian tradition… sleep safely") | §3 Solusi | −23 |
| 2 | "Every second she hesitates, he pushes harder." | §2 Masalah | −8 |
| 3 | "She didn't reuse a password." | §1 Hook | −5 |
| 4 | Kalimat berdiri sendiri "That middle number is the one we care about…" — gabung jadi satu anak kalimat pendek: *"— read from the installer Android itself recorded."* | §4 Demo | −16 |
| 5 | Rapikan kalimat pembuka demo jadi *"Two Android emulators, live. Left, the parent. Right, the guardian — in real life, another city."* | §4 Demo | −3 |
| 6 | Rapikan kalimat APK umpan jadi *"I install our test app. Harmless — one permission, an empty screen, nothing else. We never use real malware. We simulate the delivery, never the detection."* | §4 Demo | −6 |
| 7 | Gabung dua kalimat pertama §5 menjadi satu: *"That MVP runs today on a stock Android image — no root, no server, no cloud model. It reads only what Android already exposes, and no message content ever leaves the phone."* | §5 Jalan ke depan | −29 |
| 8 | Rapikan kalimat "never add" jadi *"One thing we will never add: RONDA uses no accessibility service and no SMS permission — the trojans' own toolkit."* | §5 Jalan ke depan | −17 |
| 9 | Rapikan kalimat penutup §5 jadi *"This ran on emulators. We've proven the logic, not yet the endurance. Next: real hardware, a Device Owner hard block, and anonymous score data feeding the national fraud centre."* | §5 Jalan ke depan | −16 |
| 10 | Rapikan penutup jadi *"…it's a second pair of eyes. Someone who loves you, who is not on the phone with the scammer, and who can say no for you."* | §6 Penutup | −4 |
| | **Total** | | **−127** |

Hasil: ±428 kata ≈ 2:46 terucap + ±10 detik jeda visual = **2:56**.

**Catatan pemotongan #1.** Metafora *ronda* tetap utuh di penutup (2:40), jadi
yang hilang hanya pengulangannya. Justru lebih baik: metafora yang muncul sekali
di akhir lebih menancap daripada yang dijelaskan di awal lalu diulang.

**Catatan pemotongan #7–9.** Bagian 5 memikul dua kriteria penilaian sekaligus
(Kriteria 5 keamanan, Kriteria 6 skalabilitas), jadi jangan menghapus *klaimnya*
— hanya rapatkan kalimatnya. Keempat klaim harus tetap terdengar: berjalan di
Android bawaan, data minimal, tanpa aksesibilitas/SMS, dan langkah berikutnya
adalah HP fisik.

**Kalau setelah semua ini masih lewat 3:00**, potong berikutnya:

1. Kalimat "She confirms — Android requires it…" (−20). Sayang, tapi bisa
   digantikan caption di layar `Android mewajibkan konfirmasi pengguna` sehingga
   pengakuan batasannya tidak hilang sepenuhnya.
2. Caption pairing di awal demo — mulai langsung dari instalasi (−2 detik visual).

**Jangan pernah dipotong:** hook, tiga baris rincian Kartu Skor, beat pita
WASPADA, tahan dua detik saat overlay muncul, dan kalimat pengakuan emulator di
§5. Lima hal itu yang menopang seluruh video — empat pertama membangun klaimnya,
yang kelima menjaga klaim itu tetap dipercaya.
