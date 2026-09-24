# RONDA — Strategi Kemitraan & Monetisasi

**Status:** Draft v1 · 18 September 2026 · Disusun untuk tim RONDA dan juri HackNusa 2026
**Posisi dokumen:** menjawab satu pertanyaan juri — *"Setelah hackathon, ini mau dibawa ke mana, dan siapa yang bayar?"*
**Dokumen terkait:** [PRD.md](PRD.md) (apa yang dibangun), [ARCHITECTURE.md](ARCHITECTURE.md) (bagaimana), [TODO.md](TODO.md) (kapan)

Semua angka di dokumen ini punya sumber di §10. Angka yang merupakan hitungan kami sendiri dari data publik ditandai **(estimasi)**. Tidak ada metrik produk RONDA yang dikutip, karena belum ada — POC baru berjalan di emulator.

---

## 0. Ringkasan satu halaman

**Tesis.** Setiap pemain yang ada — Google, bank, operator seluler — memperingatkan *korban*. Korban adalah orang yang sedang ditelepon penipu dan sudah percaya. RONDA satu-satunya yang memindahkan keputusan ke *penjaga* (guardian): anak, keponakan, tetangga yang tidak sedang dibujuk siapa pun. Itu bukan fitur; itu kategori produk yang belum ada di Indonesia.

**Kenapa sekarang.**
- Kerugian scam digital yang dilaporkan ke IASC: **Rp9,1 triliun** dari 432.637 laporan (Nov 2024 – Jan 2026). Modus file APK lewat WhatsApp saja: **3.684 laporan, Rp134 miliar → ±Rp36 juta per korban (estimasi)**.
- Indonesia resmi *ageing population*: **11,97 % penduduk lansia (±34 juta jiwa)**, 52 % di antaranya sudah pegang ponsel, 34 % mengakses internet — dan angka itu naik 7,7 poin dalam setahun. Wamenkomdigi menyebut lansia sebagai korban utama scam berbasis AI.
- Regulator sedang mencari alat, bukan sekadar edukasi: POJK 12/2024 mewajibkan seluruh lembaga jasa keuangan punya strategi anti-fraud; Komdigi (Feb & Jul 2026) meminta *semua* operator seluler memasang fitur anti-scam; Kaspersky–BSSN memperbarui MoU (Apr 2026).

**Jawaban jujur soal Google.** Google sudah memblokir sebagian APK berbahaya di Indonesia (Play Protect *enhanced fraud protection*, Feb 2025) dan mulai 30 Sept 2026 mewajibkan verifikasi developer. Kami tidak berpura-pura ini tidak ada. Tapi: (1) gelombang pertama hanya menyentuh app store, sideload via WhatsApp/browser baru diatur 2027; (2) developer terverifikasi ≠ aplikasi aman; (3) aplikasi remote-access resmi (AnyDesk dkk.) yang dipakai scam "share screen" lolos semua filter itu; (4) semua mekanisme Google tetap bertanya ke korban. RONDA tidak bersaing dengan pintu yang Google perkuat — RONDA mengganti *siapa yang membuka pintu*.

**Model bisnis.** Korban tidak pernah bayar. Penjaga hampir tidak pernah bayar. Yang bayar adalah institusi yang menanggung kerugiannya: **bank (B2B2C), operator seluler (bundling), asuransi siber personal (syarat polis)**. Aplikasi konsumen gratis adalah mesin distribusi, bukan sumber pendapatan utama.

**Aset jangka panjang.** Jaringan pasangan penjaga–terlindungi yang saling percaya, ditambah sinyal deteksi teragregasi dari ribuan HP → umpan pola serangan baru untuk IASC/bank, lebih cepat dari satu titik deteksi mana pun (USP kedua di naskah pitch).

---

## 0.5 Selling points untuk pitch

Enam poin, urutan sesuai alur pitch (masalah → solusi → kenapa kami → kenapa sekarang → bisnis → visi). Tiap poin: **klaim** yang diucapkan, *bukti* yang ditampilkan di layar, dan bagian dokumen yang mendalaminya. Ambil yang cocok dengan durasi; poin 2 dan 3 tidak boleh dipotong.

**1. Satu korban APK kehilangan rata-rata Rp36 juta — dan hampir tidak ada yang kembali.**
*Bukti:* IASC — 3.684 laporan APK via WhatsApp, Rp134 M; dana yang berhasil dikembalikan IASC hanya <2 % dari total kerugian. → §1
*Di layar:* `Rp36 juta / korban` · `<2 % kembali`

**2. Semua orang memperingatkan korban. RONDA satu-satunya yang memberi tahu orang lain.**
*Bukti:* Google Play Protect, BRImo/Livin'/myBCA, Siscamling, SATSPAM, ScamShield — semua bertanya ke orang yang sedang ditelepon penipu. Tidak ada satu pun produk di Indonesia yang mengirim keputusan ke penjaga. → §2
*Di layar:* tabel 5 pemain, satu kolom kosong: "memberi tahu orang lain?"

**3. Google memperkuat pintunya. RONDA mengganti siapa yang membukanya.**
*Bukti:* Developer Verification Indonesia mulai 30 Sept 2026 — gelombang pertama hanya app store, WhatsApp/browser baru 2027. Dan setelah itu pun dialog "apakah ada yang mendesak Anda?" tetap ditanyakan ke orang yang sedang didesak. AnyDesk (scam share-screen) lolos semua filter Google karena resmi. → §3
*Di layar:* `30 Sep 2026: app store saja` · `2027: masih bertanya ke korban`

**4. Regulator sudah minta alat ini — mereka belum tahu namanya.**
*Bukti:* POJK 12/2024 mewajibkan strategi anti-fraud semua lembaga keuangan; Wamenkomdigi (Jul 2026) meminta *seluruh* operator memasang fitur anti-scam "berbentuk aplikasi atau sistem lain"; Kaspersky–BSSN memperbarui MoU (Apr 2026); asuransi siber personal sudah dijual Rp60–150 rb/tahun dan mencakup malware & social engineering. → §5
*Di layar:* empat logo/nama: OJK · Komdigi · Kaspersky×BSSN · Chubb/MSIG

**5. Lansia tidak pernah bayar. Yang bayar adalah yang hari ini menanggung Rp36 juta-nya.**
*Bukti:* pada Rp18 rb/perangkat/tahun, satu kasus yang dicegah per 2.000 perangkat sudah impas bagi bank (estimasi). Tiga pembayar: bank (B2B2C), operator (bundling — mekanisme VAS Siscamling sudah ada), penanggung (syarat polis). Konsumen gratis selamanya untuk 1↔1. → §6
*Di layar:* `Rp18 rb/tahun` vs `Rp36 juta/kasus` · `1 : 2.000`

**6. Ribuan HP lansia = sensor terdistribusi untuk kampanye APK berikutnya.**
*Bukti:* setiap deteksi RONDA menghasilkan metadata paket (hash, sertifikat, izin, sumber) tanpa data pribadi — teragregasi, itu umpan pola serangan baru untuk IASC dan Kaspersky, jam-jam sebelum masuk database signature. Ini jawaban "partnering with institutions to catch zero-day scam patterns" di naskah §5. → §4 Fase 4
*Di layar:* peta Indonesia, titik-titik deteksi menyala

**Versi 30 detik (kalau cuma dapat satu slide):**
> Korban APK di Indonesia rata-rata kehilangan Rp36 juta, dan kurang dari 2 % kembali. Semua solusi yang ada — Google, bank, operator — memperingatkan korban, orang yang sedang ditelepon penipu. RONDA satu-satunya yang mengirim keputusan ke penjaga. Google sedang memperkuat pintunya; RONDA mengganti siapa yang membukanya. Regulator sudah meminta alat ini lewat POJK 12/2024 dan mandat Komdigi ke operator. Lansia tidak pernah bayar — bank, operator, dan asuransi yang hari ini menanggung kerugiannya yang bayar. Dan setiap HP yang dijaga jadi sensor untuk kampanye scam berikutnya.

---

## 1. Masalah dalam angka

| Fakta | Angka | Sumber |
|---|---|---|
| Kerugian scam digital dilaporkan ke IASC | Rp9,1 T · 432.637 laporan (22 Nov 2024 – 11 Jan 2026) | [S1] |
| Laporan ke IASC (periode lebih panjang) | 579.459 laporan s.d. Mei 2026; Jabar tertinggi 119.750, DKI 84.845 | [S2] |
| Modus APK via WhatsApp/Telegram | 3.684 laporan · Rp134 M (Nov 2024 – 15 Okt 2025), masuk 10 modus teratas | [S3] |
| **Rata-rata kerugian per laporan APK** | **±Rp36,4 juta (estimasi: 134 M ÷ 3.684)** | hitungan kami dari [S3] |
| Modus "impersonasi via telepon palsu" | 47.269 laporan (#2 terbanyak) | [S2] |
| Dana yang berhasil dikembalikan IASC | Rp161 M — <2 % dari kerugian | [S4] |
| Nomor scam dilaporkan ke Komdigi | >30.000 nomor (Jan–Jul 2026) | [S5] |
| Penduduk lansia | 11,97 % (±34 juta jiwa, **estimasi** dari populasi ±284 juta) | [S6] |
| Lansia punya ponsel / akses internet | 52,23 % / 34,13 % (2025), naik dari 26,42 % (2024) | [S7] |
| Penetrasi internet usia 61–79 th | 59,4 % | [S8] |

Tiga hal yang angka ini katakan:

1. **Pemulihan tidak bekerja.** IASC hebat, tapi <2 % dana kembali. Nilai ada di *pencegahan sebelum transfer*, dan itulah jendela waktu RONDA (install → sebelum izin diberikan).
2. **Laporan APK pasti *under-reported*.** Korban lansia sering tidak tahu cara lapor, malu, atau tidak sadar bahwa "undangan" itu penyebabnya. 3.684 laporan adalah lantai, bukan langit-langit.
3. **Populasi target tumbuh dua arah.** Jumlah lansia naik, dan porsi lansia yang online naik lebih cepat (7,7 poin/tahun). Setiap lansia yang baru online adalah korban potensial yang belum pernah dilatih.

---

## 2. Lanskap: siapa sudah bergerak, dan di mana celahnya

Tabel ini sengaja jujur. Juri Kaspersky akan tahu semua ini; lebih baik kami yang menyebut duluan.

| Pemain | Apa yang mereka lakukan | Batasnya (untuk kasus lansia + WhatsApp APK) |
|---|---|---|
| **Google Play Protect — Enhanced Fraud Protection** (Indonesia, Feb 2025) | Blokir otomatis install dari sideload jika app minta RECEIVE_SMS / READ_SMS / Notification Listener / Accessibility | Bisa dimatikan; scammer sudah menaruh "kalau ada peringatan, tekan lanjut" di skrip. Tidak memberi tahu siapa pun selain korban. Hanya empat izin. |
| **Android Developer Verification** (Indonesia, mulai 30 Sept 2026) | App di perangkat bersertifikat harus dari developer terverifikasi; app tak terverifikasi masuk *advanced flow* dengan jeda 24 jam | Gelombang 1 **hanya app store** (Play, Galaxy Store, GetApps, dll.); sideload dari WhatsApp/browser baru 2027. Tidak berlaku di perangkat non-sertifikasi. Verifikasi = identitas, bukan keamanan — Kaspersky sendiri memperkirakan penyerang akan mencari jalan pintas. |
| **Android 17 Live Threat Detection** (2026) | Deteksi on-device untuk SMS forwarding & penyalahgunaan overlay/accessibility | Pixel dulu; HP Rp1–2 juta yang dipakai lansia di Indonesia menunggu bertahun-tahun, atau tidak pernah. |
| **Bank: BRImo, Livin', myBCA** | Blokir accessibility service, deteksi malware saat app bank dibuka | Melindungi **app bank itu saja**, pada **saat transaksi**. Malware sudah terpasang, OTP sudah bocor ke penipu sebelum app bank dibuka. Sekali lagi: yang diberi tahu korban. |
| **Operator: Siscamling (Telkomsel), SATSPAM (Indosat)** | Filter panggilan/SMS/link berbahaya di jaringan | Berbasis jaringan → tidak melihat apa yang terpasang di HP. WhatsApp terenkripsi end-to-end, file APK tidak lewat filter mereka. |
| **ScamShield (Singapura)** | App + hotline + *kill switch* rekening, dikelola pemerintah | Model bagus, tapi tetap *self-service*: korban yang harus memutuskan menekan kill switch. |
| **Seraph Secure, Scammer Guardian (AS)** | Layanan berbayar yang memberi tahu keluarga saat lansia tampak sedang ditipu | Bukti bahwa **model penjaga laku di pasar** — tapi US-only, berbasis panggilan, tidak ada di Indonesia. |

**Celahnya konsisten:** semua orang membangun filter yang lebih pintar untuk *korban*. Tidak ada yang membangun jalur ke *orang lain*. RONDA berdiri sendirian di kolom itu, dan itu bukan kebetulan — itu keputusan desain sejak §1.1 PRD.

---

## 3. Posisi RONDA setelah Google menutup pintu depan

Ini bagian yang paling mungkin ditanyakan juri: *"Kalau Google sudah memblokir sideload, buat apa RONDA?"*

**Empat jawaban, dari yang paling sementara ke yang paling permanen:**

1. **Jendela 2026–2027 masih terbuka.** Enforcement 30 Sept 2026 hanya menyentuh app store. APK yang dikirim lewat WhatsApp — vektor scam utama — belum tersentuh sampai perluasan global 2027, dan Google belum menyebut tanggal untuk "semua sumber instalasi".
2. **Terverifikasi bukan berarti aman.** Verifikasi mengikat identitas ke aplikasi; sindikat yang mampu mengoperasikan call-center bisa membeli identitas. Kaspersky Malware Analyst Team Lead: *"Attackers will likely find ways to bypass verification."* RONDA membaca apa yang aplikasi *bisa lakukan* (izin yang dideklarasikan), bukan siapa yang menandatanganinya.
3. **Scam berikutnya tidak butuh APK jahat.** Modus "share screen" memakai AnyDesk/TeamViewer — resmi, terverifikasi, ada di Play Store. Semua filter Google lolos. RONDA melihat "aplikasi remote-access baru terpasang di HP Ibu" sebagai sinyal, dan penjaga yang menilai konteksnya.
4. **Yang paling permanen: Google tetap bertanya ke korban.** Dialog "apakah ada yang sedang mendesak Anda?" di *advanced flow* ditanyakan ke orang yang sedang didesak. Jeda 24 jam bagus, tapi penipu bisa menelepon lagi besok. Selama keputusan ada di HP korban, korban bisa dibujuk. RONDA memindahkan keputusan itu keluar dari HP korban. Tidak ada pembaruan Android yang bisa melakukan itu, karena Google tidak tahu siapa anak Anda.

**Kalimat untuk pitch:** *Google memperkuat pintunya. RONDA mengganti siapa yang membukanya.*

Konsekuensi strategisnya: **aset RONDA bukan detektor APK — aset RONDA adalah relasi penjaga–terlindungi yang sudah dipasangkan dan dipercaya.** Detektor APK adalah sinyal pertama yang lewat jalur itu. Sinyal kedua, ketiga, dan seterusnya (§4) yang membuat RONDA tetap relevan setelah 2027.

---

## 4. Roadmap produk (yang mendasari roadmap uang)

| Fase | Waktu | Apa | Kenapa ini dulu |
|---|---|---|---|
| **0 — POC** | selesai 21 Agu 2026 | Deteksi install-time, soft-block overlay, pairing, alert ke penjaga, remote uninstall. Emulator. | Membuktikan mekanisme inti. |
| **1 — MVP terpercaya** | Okt 2026 – Mar 2027 | HP fisik (Xiaomi/Oppo/Vivo, Android 11–14). Pemindaian awal (Block 4B). Multi-ortu (4C). QR deep link (4D). Listing Play Store — **RONDA sendiri wajib jadi developer terverifikasi** sebelum 2027. Pengujian *false positive* pada 100 app sah. | Tanpa Play Store dan verifikasi, RONDA tidak bisa direkomendasikan bank/Komdigi. Tanpa uji FP, penjaga belajar mengabaikan alert. |
| **2 — Sinyal ke-2 dan ke-3** | Q2–Q3 2027 | (a) App remote-access/screen-share baru terpasang, dari sumber mana pun. (b) Accessibility service diaktifkan untuk app sideload. (c) Device-admin baru. (d) Perubahan default SMS app. Semua tetap lewat `PackageManager` + Settings, tanpa izin baru. | Ini jawaban terhadap verifikasi Google 2027: vektor scam bergeser ke app sah yang disalahgunakan. |
| **3 — Penjaga sebagai *second approval*** | 2027–2028, via mitra bank | Bank mengirim notifikasi ke penjaga (lewat RONDA) saat rekening lansia melakukan transfer di luar pola; penjaga bisa "tahan 30 menit". Semacam *kill switch* ScamShield, tapi ditekan orang yang tidak sedang ditipu. | Ini fitur yang bank bisa jual ke nasabah prioritasnya, dan yang membuat RONDA bagian dari strategi anti-fraud bank (POJK 12/2024), bukan sekadar app pihak ketiga. |
| **4 — Jaringan sinyal** | 2028+ | Metadata deteksi teragregasi (hash paket, sertifikat, izin, sumber install; **tanpa data pribadi**) → umpan pola serangan ke IASC, bank, Kaspersky. Opt-in per penjaga. | Ribuan HP lansia = sensor terdistribusi untuk kampanye APK baru, jam-jam sebelum masuk database signature. |

Empat hal yang **tidak** ada di roadmap, dan tidak akan ada: iOS (mustahil secara arsitektur, PRD §7), Accessibility Service, izin SMS, dan pemantauan tersembunyi. Ini pagar produk sekaligus argumen kepercayaan ke regulator.

---

## 5. Kemitraan: siapa, kenapa mereka peduli, apa yang kami minta

Prinsip: setiap mitra didekati dengan **masalah yang sudah mereka akui secara publik**, bukan dengan produk kami. Urutan di bawah adalah urutan prioritas.

### 5.1 Bank & e-wallet — mitra komersial utama

**Kenapa mereka peduli:** POJK 12/2024 mewajibkan strategi anti-fraud dan pelaporan insiden ke OJK; POJK 22/2023 menuntut keamanan sistem dan perlakuan adil ke konsumen. Bank sudah bergerak sendiri (BRImo pop-up accessibility, Livin' blokir app sideload) — artinya budget dan mandat internalnya sudah ada. Kerugian nasabah lansia adalah biaya reputasi + biaya *dispute* + tekanan OJK.

**Apa yang RONDA beri:** lapisan *upstream* dari proteksi mereka. Bank melihat malware saat app bank dibuka; RONDA melihatnya saat terpasang, sebelum OTP bocor. Ditambah Fase 3: penjaga sebagai *second approval* — fitur yang belum dimiliki bank mana pun di Indonesia.

**Apa yang kami minta:** pilot 6 bulan dengan 500–2.000 nasabah lansia yang punya anak nasabah bank yang sama (bank sudah tahu relasi keluarga dari data KYC/rekening bersama). Bank yang mendistribusikan lewat kanal CS/cabang; RONDA menyediakan app dan dasbor agregat.

**Pintu masuk:** bukan RFP bank besar (siklus 12–18 bulan). Mulai dari **bank digital / BPR / fintech** yang siklusnya pendek dan nasabahnya sudah biasa dengan bundling (Jenius sudah bundling asuransi siber MSIG; blu by BCA Digital menulis soal *personal cyber insurance*). Bank besar menyusul setelah ada angka dari pilot.

**Kandidat awal:** blu (BCA Digital), Jenius (SMBC), SeaBank, Bank Jago; e-wallet: DANA, GoPay (ekosistem keluarga). BRI sebagai target bank besar pertama karena basis nasabah pedesaan & lansia paling besar dan sudah paling agresif di BRImo.

### 5.2 OJK / IASC / Satgas PASTI — legitimasi & data

**Kenapa mereka peduli:** IASC dibangun untuk *respons* (pembekuan rekening), dan hasilnya <2 % dana kembali. OJK butuh cerita *pencegahan*. IASC sudah mengumpulkan pelaporan per modus, termasuk "APK via WhatsApp" — RONDA adalah sensor di ujung yang belum mereka punya.

**Apa yang RONDA beri:** (a) statistik deteksi teragregasi per wilayah/per modus (Fase 4); (b) studi kasus konkret untuk kampanye edukasi OJK; (c) validasi bahwa pendekatan "penjaga" bisa diregulasi — konsen eksplisit, tanpa SMS/Accessibility, data minimal.

**Apa yang kami minta:** bukan uang. **Surat dukungan / pengakuan sebagai inisiatif yang selaras dengan Satgas PASTI**, dan slot di kanal komunikasi IASC (situs, media sosial). Ini yang membuka pintu bank (§5.1) — bank bergerak jauh lebih cepat kalau OJK sudah mengangguk.

**Pintu masuk:** OJK Regional Jawa Barat (laporan IASC tertinggi: 119.750) — bisa jadi wilayah pilot. Tim RONDA berbasis di Bandung (final HackNusa di Telkom University).

### 5.3 Komdigi — distribusi & mandat operator

**Kenapa mereka peduli:** Komdigi punya program literasi digital lansia (2026: 100 pemandu literasi, target 5.000 orang) dan secara publik (Feb & Jul 2026) meminta *seluruh* operator seluler memasang fitur anti-scam, dengan kebebasan memilih bentuknya "aplikasi atau sistem keamanan lain". Itu kalimat yang persis membuka ruang RONDA.

**Apa yang RONDA beri:** alat konkret untuk program literasi lansia — sesi literasi berakhir dengan "pasangkan HP Bapak/Ibu dengan HP anak", bukan hanya "hati-hati ya". Pemandu literasi = penjaga cadangan bagi lansia yang anaknya tidak melek teknologi.

**Apa yang kami minta:** (a) RONDA masuk kurikulum/toolkit pemandu literasi digital lansia; (b) pengenalan formal ke operator sebagai opsi memenuhi permintaan Komdigi.

### 5.4 Operator seluler — kanal distribusi terbesar

**Kenapa mereka peduli:** ada tekanan Komdigi, dan produk mereka (Siscamling, SATSPAM) hanya menutup panggilan/SMS. Celah "apa yang terpasang di HP" belum mereka isi. Telkomsel sudah menjual Siscamling sebagai VAS — mekanisme *billing* dan *bundling*-nya sudah ada.

**Apa yang RONDA beri:** lapisan *on-device* pelengkap Siscamling/SATSPAM. Cerita marketingnya bersih: "Siscamling menjaga jaringan, RONDA menjaga HP-nya."

**Apa yang kami minta:** bundling di paket keluarga / paket lansia, dengan *revenue share* (§6, Model B). Pre-load di HP bundling operator menyelesaikan masalah terbesar kami: OEM mematikan layanan latar belakang.

**Kandidat:** Telkomsel (basis pedesaan/lansia terbesar, VAS mapan), Indosat (paling vokal soal anti-scam, sudah punya partner AI Tanla).

### 5.5 Asuransi siber personal — insentif ekonomi yang paling rapi

**Kenapa mereka peduli:** produk sudah ada dan tumbuh — Chubb×DBS Cyber Guard (Rp60–150 rb/tahun, pertanggungan s.d. Rp50 juta), MSIG×Jenius (mulai Rp70 rb/tahun), BCAinsurance (mulai Rp8.750). Polis ini **mencakup social engineering dan malware** — persis kerugian yang RONDA cegah. Setiap klaim yang tidak terjadi adalah margin penanggung.

**Apa yang RONDA beri:** pengurangan risiko yang bisa dibuktikan (status "RONDA aktif & penjaga terpasang" bisa dicek saat *underwriting*), seperti telematika di asuransi mobil.

**Apa yang kami minta:** RONDA aktif sebagai **syarat diskon premi** atau syarat polis untuk tertanggung usia >60. Penanggung membayar RONDA per polis aktif (§6, Model C).

### 5.6 Kaspersky — sponsor, intelijen ancaman, pintu ke BSSN

**Kenapa mereka peduli:** Kaspersky mencatat 14,9 juta serangan berbasis internet ke pengguna Indonesia sepanjang 2025, memperbarui MoU dengan BSSN (Apr 2026) yang mencakup "inisiatif kesadaran publik", dan punya *Kaspersky Fraud Prevention* SDK untuk bank. RONDA adalah lapisan keluarga yang tidak mereka punya, dan HackNusa adalah jalur hubungan yang sudah terbuka.

**Apa yang RONDA beri:** kanal ke segmen yang tidak pernah membeli antivirus (lansia pedesaan) lewat orang yang mungkin membelinya (anaknya). Sinyal deteksi dari lapangan (Fase 4).

**Apa yang kami minta:** (a) akses umpan reputasi hash/sertifikat untuk memperkaya skor (opsional, deteksi inti tetap offline); (b) pengenalan ke BSSN lewat MoU mereka; (c) co-branding "didukung Kaspersky" untuk pilot pertama.

### 5.7 OEM (Xiaomi, Oppo, Vivo, Samsung) — masalah teknis jadi peluang

Tiga OEM Tiongkok menguasai HP kelas Rp1–3 juta yang dipakai lansia Indonesia, dan *skin* mereka yang membunuh layanan latar belakang adalah risiko R1 di PRD. Pre-load atau *whitelist* resmi menyelesaikannya sekaligus. Bukan prioritas 12 bulan pertama — butuh volume dulu — tapi dicatat karena jalur operator (§5.4) biasanya membuka jalur OEM.

### 5.8 Komunitas: RT/RW, posyandu lansia, Karang Taruna, Kemensos

Bukan mitra komersial, tapi menjawab pertanyaan yang pasti muncul: **"Bagaimana dengan lansia yang tidak punya anak melek teknologi?"** Jawabannya ada di nama produk: *ronda*. Relawan Karang Taruna atau kader posyandu jadi penjaga untuk 5–10 lansia di RT-nya (Fase 1 multi-ortu memungkinkan ini). Komdigi/Kemensos bisa jadi sponsor programnya. Ini yang membuat RONDA cocok dengan *Human-Centric Security* — keamanan sebagai praktik sosial, bukan produk individual.

---

## 6. Monetisasi

### Prinsip

1. **Yang terlindungi tidak pernah bayar, tidak pernah melihat harga, tidak pernah ditawari upgrade.** Layar lansia bebas dari apa pun yang bisa dimanfaatkan penipu ("pak, bayar dulu biar aman").
2. **Yang bayar adalah pihak yang menanggung kerugiannya** — bank, penanggung, operator. Mereka sudah membayar biaya scam hari ini; RONDA hanya mengubahnya dari biaya *setelah* jadi biaya *sebelum*.
3. **App konsumen gratis selamanya untuk 1 penjaga ↔ 1 terlindungi.** Ini mesin distribusi dan sumber kepercayaan; memonetisasinya mematikan keduanya.
4. **Tidak ada iklan, tidak ada penjualan data personal, tidak ada pemantauan tersembunyi.** Selain etika, ini syarat kelayakan bagi setiap mitra di §5.

### Jangkar harga: berapa nilai satu pencegahan?

Dari data IASC: Rp134 M ÷ 3.684 laporan = **±Rp36 juta per kasus APK (estimasi)**. Bandingkan dengan asuransi siber personal Rp60–150 rb/tahun untuk pertanggungan Rp10–50 juta. Artinya pasar sudah menetapkan harga "ketenangan" di kisaran **Rp5–12 rb/bulan per orang**. RONDA di sisi B2B harus lebih murah dari itu, karena RONDA mengurangi klaim, bukan membayar klaim.

### Lima model, diurutkan berdasarkan prioritas

| # | Model | Siapa bayar | Bentuk | Kapan | Catatan |
|---|---|---|---|---|---|
| **A** | **Lisensi B2B2C bank / e-wallet** | Bank | Per perangkat terlindungi per tahun, **Rp12–24 rb** (estimasi), minimum komitmen pilot. Fase 3 (*second approval*) sebagai tier premium. | Pilot Q1 2027, komersial 2028 | Sumber pendapatan utama. Break-even bagi bank: pada Rp18 rb/perangkat, 2.000 perangkat = Rp36 juta/tahun → **satu** kasus Rp36 juta yang dicegah per 2.000 perangkat sudah impas (estimasi). |
| **B** | **Bundling operator** | Operator (dan sebagian pelanggan lewat paket) | Rev-share VAS 30–50 % dari tarif Rp3–5 rb/bulan di paket keluarga/lansia, atau lisensi flat. | Q3 2027 | Volume terbesar, margin terkecil. Menyelesaikan masalah OEM (pre-load). |
| **C** | **Syarat polis asuransi siber** | Penanggung | Rp5–10 rb per polis aktif per tahun, atau diskon premi yang dibiayai penanggung. | 2027 (bareng A, lewat bank digital yang sudah bundling asuransi) | Insentif paling rapi: penanggung untung setiap klaim yang tidak terjadi. |
| **D** | **Freemium konsumen "RONDA Keluarga"** | Penjaga | Gratis: 1↔1. Berbayar Rp15–25 rb/bulan: hingga 5 terlindungi, riwayat 12 bulan, penjaga cadangan, sinyal Fase 2. | Setelah Play Store, Q2 2027 | Sengaja kecil. ARPU konsumen Indonesia rendah; fungsi utamanya validasi *willingness to pay* dan pendapatan awal sebelum kontrak B2B pertama. |
| **E** | **Umpan intelijen ancaman** | IASC, bank, vendor keamanan | Langganan data agregat (hash, sertifikat, izin, sumber, wilayah; tanpa identitas). Opt-in per penjaga. | 2028+ | Baru bernilai pada puluhan ribu perangkat. Batasannya UU PDP dan PRD §5.4: metadata paket saja. |

**Pendanaan non-komersial (bukan model bisnis, tapi jembatan 12 bulan pertama):** hibah/insentif program Komdigi & BSSN, dana CSR bank (program lansia), hadiah kompetisi. Digunakan untuk: HP fisik, biaya verifikasi developer & listing, pilot pertama.

### Proyeksi tahapan (target, bukan ramalan)

| Tahap | Perangkat terlindungi | Sumber pendapatan | Tujuan tahap |
|---|---|---|---|
| Okt 2026 – Mar 2027 | 0 → 1.000 (komunitas, keluarga tim, program literasi) | Rp0; hibah/CSR | Angka *false positive* nyata, *time-to-decision* penjaga, 5 studi kasus |
| Apr – Des 2027 | 1.000 → 20.000 (pilot 1–2 bank digital + program Komdigi) | Model A pilot + D | Kontrak B2B pertama, bukti pengurangan insiden pada kohort pilot |
| 2028 | 20.000 → 200.000 (1 bank besar + 1 operator) | A + B + C | Fase 3 *second approval* live; ARR Rp2–4 M (estimasi pada Rp12–24 rb/perangkat) |
| 2029+ | >1 juta | A + B + C + E | Jaringan sinyal (Fase 4) jadi produk sendiri |

### Metrik yang kami pegang (dan yang akan diminta mitra)

- **Presisi alert** (target >95 %): penjaga tidak boleh belajar mengabaikan.
- **Waktu deteksi → keputusan penjaga** (target median <10 menit): mengukur apakah jalur penjaga benar-benar lebih cepat dari penipu.
- **Tingkat pemasangan yang tetap aktif setelah 30 hari** (target >80 %): mengukur apakah OEM membunuh kami dan apakah lansia mencabutnya.
- **Estimasi kerugian yang dicegah** = alert DARURAT yang berakhir *uninstall* × Rp36 juta. Ini angka yang dibawa ke bank.

---

## 7. Risiko strategis

| Risiko | Dampak | Mitigasi |
|---|---|---|
| **Google memperluas verifikasi ke semua sumber (2027)** dan sebagian besar APK-via-WA mati | Sinyal pertama RONDA melemah | Fase 2 dibangun *sebelum* itu terjadi. Pesan sejak awal: RONDA = jalur penjaga, bukan detektor APK. |
| **Kebijakan Play Store** menolak `PACKAGE_USAGE_STATS` / `SYSTEM_ALERT_WINDOW` | Tidak bisa listing | Justifikasi *core functionality* + video demo; RONDA sudah patuh pada aturan anti-stalkerware (konsen visible). Cadangan: distribusi via operator/bank (sideload resmi dari developer terverifikasi — ironis, tapi sah). |
| **Persepsi stalkerware** dari media/juri/regulator | Reputasi, akses ke bank & OJK | PRD §5.5–5.6: pairing tatap muka, indikator permanen di HP terlindungi, tidak ada unpair jarak jauh, tidak ada pembacaan konten. Dokumentasikan dan ucapkan lebih dulu. |
| **Penipu beradaptasi**: memandu korban mencopot RONDA atau mencabut izin overlay | Soft-block gagal | Pencabutan izin/uninstall RONDA sendiri = alert ke penjaga (sudah masuk R6a PRD). Fase 3 (bank menahan transfer) tidak bergantung pada HP korban sama sekali. |
| **Siklus pengadaan bank 12–18 bulan** | Kehabisan napas sebelum kontrak | Mulai dari bank digital/fintech/asuransi (§5.1, §5.5); pendanaan jembatan dari hibah/CSR; freemium untuk arus kas kecil. |
| **OEM membunuh layanan latar belakang** | Perlindungan diam-diam mati | Pengecualian baterai di setup (sudah dibangun), *heartbeat* ke penjaga ("HP Ibu terakhir terlihat 3 hari lalu"), jangka panjang pre-load via operator. |
| **UU PDP** | Sanksi, kehilangan mitra | Data minimal by design (metadata paket saja), konsen eksplisit, Fase 4 hanya agregat opt-in. Audit oleh mitra bank pertama sebagai bagian pilot. |
| **Ketergantungan Firebase** | Satu titik gagal untuk alert | Deteksi + overlay sudah offline. Rencana: abstraksi transport agar bisa dipindah ke infrastruktur mitra (bank/operator biasanya mensyaratkan ini). |

---

## 8. 90 hari setelah HackNusa (Okt – Des 2026)

Urutan ini dipilih agar setiap langkah membuka langkah berikutnya.

1. **Minggu 1–2 — Verifikasi developer & HP fisik.** Daftar sebagai developer terverifikasi (wajib sebelum 2027; ini juga bukti kepatuhan saat bicara dengan bank). Beli 2–3 HP Xiaomi/Oppo bekas kelas Rp1–2 juta.
2. **Minggu 2–6 — Uji *false positive* 100 app** (50 Play Store, 50 sideload sah: app bank tua, app pemerintah, game). Publikasikan hasilnya di repo. Ini dokumen pertama yang diminta bank.
3. **Minggu 3–8 — Blok 4B/4C/4D** (pemindaian awal, multi-ortu, QR deep link) → MVP yang bisa dipakai relawan.
4. **Minggu 6–10 — Pilot komunitas 50–100 lansia** via posyandu lansia / RT di Bandung dengan relawan mahasiswa sebagai penjaga cadangan. Kumpulkan: presisi, waktu keputusan, retensi 30 hari, dan **cerita**.
5. **Minggu 8–12 — Tiga percakapan:** (a) OJK Regional Jabar / IASC, membawa data pilot; (b) satu bank digital atau insurtech (Jenius/blu/MSIG) untuk pilot berbayar 2027; (c) Kaspersky, menindaklanjuti HackNusa untuk pengenalan ke BSSN.
6. **Sepanjang periode — Listing Play Store** sebagai target Desember 2026.

Definisi "berhasil" di hari ke-90: satu surat dukungan institusi, satu pilot berbayar yang dijadwalkan, dan angka presisi nyata yang bisa dikutip.

---

## 9. Jawaban siap pakai untuk pertanyaan juri

- *"Google sudah melakukan ini."* → §3. Google memperkuat pintunya; RONDA mengganti siapa yang membukanya. Gelombang verifikasi 2026 belum menyentuh WhatsApp; 2027 pun tetap bertanya ke korban.
- *"Siapa yang bayar?"* → §6. Bank, operator, penanggung — pihak yang sudah menanggung Rp36 juta per kasus hari ini. Lansia tidak pernah bayar.
- *"Ini stalkerware."* → PRD §5. Konsen tatap muka, indikator permanen, tanpa SMS/Accessibility/konten. Semua keputusan desain itu adalah *syarat masuk* ke setiap mitra di §5.
- *"Bagaimana lansia yang anaknya tidak melek teknologi?"* → §5.8. Ronda, secara harfiah: relawan RT/posyandu sebagai penjaga cadangan.
- *"Kenapa Android saja?"* → PRD §7. Scam-nya Android, pertahanannya hanya mungkin di Android.
- *"Apa yang terjadi kalau RONDA sukses besar?"* → §4 Fase 4. Ribuan HP lansia jadi sensor terdistribusi untuk kampanye APK baru — itu kemitraan dengan IASC dan Kaspersky, bukan sekadar app.

---

## 10. Sumber

**Skala masalah**
- [S1] TIMES Indonesia, "Penipuan WhatsApp Meningkat, Kerugian Capai Rp9,1 Triliun" — data IASC 22 Nov 2024 – 11 Jan 2026. https://jogja.times.co.id/news/kriminal/zHU1rbTfC/penipuan-whatsapp-meningkat-kerugian-capai-rp91-triliun-pakar-ugm-ingatkan-bahaya-file-apk
- [S2] Kompas, "Komdigi Sebut Banyak Lansia Jadi Korban Scam AI" (2 Jul 2026) — 579.459 laporan IASC, top-5 modus, pernyataan Wamenkomdigi soal operator. https://nasional.kompas.com/read/2026/07/02/20330831/komdigi-sebut-banyak-lansia-jadi-korban-scam-ai-yang-tiru-suara-pejabat
- [S3] Radar Surabaya / Jawa Pos, "Kerugian Akibat Scam Digital Tembus Rp7 Triliun, OJK Ungkap 10 Modus Utama" — APK via WhatsApp 3.684 laporan, Rp134 M (Nov 2024 – 15 Okt 2025). https://radarsurabaya.jawapos.com/ekonomi/776724733/kerugian-akibat-scam-digital-di-indonesia-tembus-rp7-triliun-ojk-ungkap-10-modus-utama
- [S4] OJK, Siaran Pers "IASC Berhasil Kembalikan Rp161 Miliar Dana Masyarakat Korban Scam". https://ojk.go.id/id/berita-dan-kegiatan/siaran-pers/Pages/IASC-Berhasil-Kembalikan-Rp161-Miliar-Dana-Masyarakat-Korban-Scam.aspx
- [S5] Kompas, "Komdigi Terima Laporan 30.000 Nomor Scamming Sepanjang Januari–Juli 2026". https://nasional.kompas.com/read/2026/07/23/17250811/komdigi-terima-laporan-30000-nomor-scamming-sepanjang-januari-juli-2026
- [S6] BPS, Statistik Penduduk Lanjut Usia 2025 (11,97 %). https://www.bps.go.id/id/publication/2025/12/12/868d335b088dcddc3ddee052/statistik-penduduk-lanjut-usia-2025.html
- [S7] Dataloka (mengutip BPS), "Persentase Penduduk Lansia yang Mengakses Internet 2025". https://dataloka.id/humaniora/5794/persentase-penduduk-lansia-yang-mengakses-internet-2025-terus-meningkat-dalam-6-tahun-terakhir/
- [S8] GoodStats (mengutip APJII 2025), penetrasi internet per generasi. https://data.goodstats.id/statistic/penetrasi-internet-indonesia-menurut-generasi-2025-milenial-dan-gen-z-terdepan-jw8fn

**Lanskap platform**
- Google Security Blog, "Piloting new ways of protecting Android users from financial fraud" (Feb 2024). https://security.googleblog.com/2024/02/piloting-new-ways-to-protect-Android-users-from%20financial-fraud.html
- Kompas, "Google Rilis Fitur Enhanced Fraud Protection di Indonesia" (19 Feb 2025). https://amp.kompas.com/tren/read/2025/02/19/130000665/google-rilis-fitur-enhanced-fraud-protection-di-indonesia-cegah-penipuan
- Google, "Learn about Android developer verification" — cakupan perangkat & negara. https://support.google.com/android/answer/17065026
- Android Authority, "Google details when Android's new sideloading changes will start affecting users" — gelombang 30 Sept 2026 hanya app store. https://www.androidauthority.com/android-sideloading-changes-timeline-3679204/
- Android Authority, "Android's new sideloading rules … 24-hour lock". https://www.androidauthority.com/google-android-sideloading-unverified-apps-new-rules-3650343/
- The Hacker News, "Google Sets Sept. 30 Deadline for Android Developer Verification in Four Countries" (Jun 2026). https://thehackernews.com/2026/06/google-sets-sept-30-deadline-for.html
- Kaspersky, "Fake apps, NFC skimming attacks, and other Android issues in 2026" — kutipan soal bypass verifikasi. https://www.kaspersky.com/blog/growing-2026-android-threats-and-protection/55191/
- Google, "What's new in Android security & privacy 2026" — Live Threat Detection, verified financial calls. https://blog.google/security/whats-new-in-android-security-privacy-2026/

**Bank, operator, asuransi**
- detik, "BRImo Terapkan Proteksi Berlapis dan Fitur Anti-Malware Terbaru". https://inet.detik.com/security/d-7715974/brimo-terapkan-proteksi-berlapis-dan-fitur-anti-malware-terbaru
- Kontan, "Cara Kelola Fitur Accessibility biar BRImo Aman" — praktik Livin'/myBCA. https://momsmoney.kontan.co.id/news/wajib-tahu-ini-cara-kelola-fitur-accessibility-biar-brimo-aman-digunakan-di-2025
- Bisnis, "Komdigi Minta Semua Operator Seluler Punya Fitur Antispam dan Antiscam" (6 Feb 2026). https://teknologi.bisnis.com/read/20260206/101/1950761/komdigi-minta-semua-operator-seluler-punya-fitur-antispam-dan-antiscam
- Telkomsel, halaman VAS Siscamling. https://www.telkomsel.com/vas/siscamling
- Selular, "Indosat Klaim Blokir 2 Miliar Spam dan Scam dalam Enam Bulan" (Feb 2026). https://selular.id/2026/02/indosat-klaim-blokir-2-miliar-spam-dan-scam-dalam-enam-bulan/
- CNBC Indonesia, "Rekening Dibobol Hacker, Uang Hilang Bisa Diganti Asuransi?" (Agu 2026) — premi Chubb×DBS, MSIG×Jenius. https://www.cnbcindonesia.com/mymoney/20260813122142-72-758911/rekening-dibobol-hacker-uang-hilang-bisa-diganti-asuransi
- BCAinsurance, Personal Cyber Insurance. https://www.bcainsurance.co.id/product/detail/asuransi-siber-pribadi-personal-cyber-insurance

**Regulasi**
- POJK 12/2024, Penerapan Strategi Anti Fraud bagi Lembaga Jasa Keuangan. https://ojk.go.id/id/regulasi/Pages/Penerapan-Strategi-Anti-Fraud-Bagi-Lembaga-Jasa-Keuangan.aspx
- POJK 22/2023, Pelindungan Konsumen dan Masyarakat di Sektor Jasa Keuangan. https://ojk.go.id/id/regulasi/Pages/Pelindungan-Konsumen-dan-Masyarakat-di-Sektor-Jasa-Keuangan.aspx
- OJK, Siaran Pers Satgas PASTI (Nov 2025). https://www.ojk.go.id/id/berita-dan-kegiatan/info-terkini/Documents/Pages/Satgas-PASTI-Imbau-Masyarakat-Waspadai-Penipuan-Menggunakan-AI/SP-08%20Satgas%20PASTI%20November%202025.pdf

**Kaspersky / BSSN / pembanding luar negeri**
- Pasardana, "Kaspersky dan BSSN Perbarui MoU" (13 Apr 2026). https://pasardana.id/news/2026/4/13/kaspersky-dan-bssn-perbarui-mou-untuk-memperkuat-ketahanan-siber-indonesia/
- Kaspersky Fraud Prevention. https://www.kaspersky.com/enterprise-security/fraud-prevention
- ScamShield (Singapura), Kill Switch. https://www.scamshield.gov.sg/kill-switch/
- Seraph Secure (AS). https://www.seraphsecure.com/ · Scammer Guardian (AS). https://www.scammerguardian.com/about/
