# RONDA — Presentation Deck (Final Pitching)

**Draft v4** · 30 Sep 2026 · Final Pitching HackNusa 2026, Telkom University, 3 Okt 2026
**Waktu:** 12 menit total → **±7 menit pitch + ±5 menit Q&A** · **Bahasa:** Inggris (Guideline FAQ #7)

---

## Aturan deck (dari riset pitch pemenang hackathon)

1. **Buka dengan cerita, bukan perkenalan.** 20–30 detik pertama menentukan perhatian juri. Perkenalan tim masuk *setelah* hook.
2. **Satu ide per slide.** Judul slide = kesimpulannya (misalnya *"Every safeguard asks the person being tricked"*), bukan label (*"Problem"*).
3. **Maksimal ±15 kata per slide.** Yang lain diomongin. Slide bukan dokumentasi: juri harus *mendengar*, bukan *membaca*.
4. **Angka besar, gambar besar.** Screenshot di-crop rapi, font ≥ 28 pt, kontras tinggi.
5. **Demo adalah pusatnya.** Satu alur user utuh (install → blok → alert → hapus) dalam **video rekaman** (tidak bergantung pada Wi-Fi venue), lalu satu slide rekap screenshot supaya juri bisa melihat detail layarnya.
6. **Tutup dengan kembali ke cerita pembuka** (*bookend*), lalu satu kalimat yang diingat.
7. **Ikuti kriteria juri.** Tiap slide diberi label **Bagian:** yang menunjukkan bagian mana dari format panitia yang dibahas. Keenam kriteria penilaian juri semuanya tercakup.
8. **Jangan pakai angka tanpa sumber.** Semua angka di bawah punya sumber (Lampiran C). Jangan janjikan tanggal/tahun untuk rencana ke depan.

---

## Alur & waktu

| # | Slide | Membahas bagian | Siapa | Waktu |
|---|---|---|---|---|
| 1 | Hook: kasus Batang + headline berita | Problem (cerita) | Dhanes | 0:40 |
| 2 | Data | Problem (data) | Dhanes | 0:25 |
| 3 | Kami RONDA | Title & tim | Dhanes | 0:25 |
| 4 | Semua pengaman bertanya ke korban | Problem statement & track | Dhanes | 0:40 |
| 5 | Solusi: Connect → Detect → Block → Alert → Decide | Solution overview | Malik | 0:35 |
| 6 | Keunggulan | USP | Malik | 0:30 |
| 7 | Video demo | PoC (demo) | Dhanes (narasi) | 1:15 |
| 8 | Rekap screenshot | PoC (detail layar) | Dhanes | 0:10 |
| 9 | Fitur lain yang sudah jadi | PoC (fitur) | Dhanes | 0:25 |
| 10 | Cara RONDA menghitung skor | PoC (mesin skor) | Dhanes | 0:35 |
| 11 | Arsitektur & stack | Tech architecture & stack | Alek | 0:25 |
| 12 | Keamanan | Security (+ paten) | Alek | 0:30 |
| 13 | Rencana ke depan | Scalability & deployment | Malik | 0:20 |
| 14 | Dampak & langkah berikutnya | Impact | Malik | 0:30 |
| 15 | Penutup | Conclusion | Alek | 0:20 |
| 16 | Q&A | Q&A | semua | ±5:00 |

Total pitch ±7:45 — **lewat ±45 detik dari target 7 menit.** Kalau kepanjangan, potong berurutan: (1) slide 8 cukup ditampilkan tanpa dibahas, (2) slide 11 cukup satu kalimat, (3) slide 13 digabung ke slide 14.

> **Catatan jumlah slide:** panitia *merekomendasikan* 10–15 slide. Deck ini 16 (termasuk Q&A). Kalau mau pas 15, gabung slide 13 (rencana) ke slide 14 (dampak).

---

# SLIDE 1 — Hook: *"Two years later, still gone."*

**Bagian:** Problem (cerita pembuka)

**Di slide:**
> **Headline berita** (kliping, bahasa asli) + terjemahan kecil di bawahnya
> `Undangan.apk`
> **Rp1.3 billion → Rp100 thousand**
> *February 2026: still waiting.*

**Headline yang dipakai (pilih satu, utamakan yang menyebut APK):**
1. **Utama:** *"Klik File APK Berujung Petaka, Simpanan Wakil Ketua DPRD Batang Rp1,3 Miliar Raib"* — Ayo Batang, Feb 2026
   Terjemahan kecil: *"One click on an APK file: Batang deputy speaker's Rp1.3 billion savings gone"*
2. Cadangan: *"Tabungan Wakil Ketua DPRD Batang Junaenah Hilang Rp 1,3 miliar di BRI"* — Disway, 16 Feb 2026
3. Cadangan: *"Rekening Wakil Ketua DPRD Batang Diduga Dibobol, Saldo Rp 1,3 M Tersisa Rp 100 Ribu"* — Tribun Lampung

**Visual:** kliping berita di bagian atas (kotak putih bergaya potongan koran, sedikit miring, dengan nama media + tanggal). Di bawahnya bubble chat `Undangan.apk` di kiri, dan saldo turun **Rp1.3 billion → Rp100 thousand** di kanan. Baris penutup kuning: *February 2026: still waiting.*

**Yang diomongin:**
> February 2026. This headline ran in Central Java: a deputy speaker of the regional parliament lost one point three billion rupiah — from one click on an APK file.
>
> Two years earlier, on a Saturday night, she opened what looked like a wedding invitation. It was an app. Her phone froze. By Monday, her bank statement showed a string of transfers to accounts she'd never heard of. One hundred thousand rupiah left.
>
> Two years on, she's still waiting for answers. She's not careless — she's a public official. She just had nobody watching.

**Catatan tim:**
- **Ambil screenshot artikel aslinya** (Ayo Batang) untuk kliping, dan cek tanggal terbitnya di halaman itu. Aku tidak bisa membuka situs Ayo Batang dari sini. Teks headline di atas diambil dari hasil pencarian, dan dari isi beritanya terbitnya sekitar Februari 2026. Kalau ragu soal tanggal, pakai headline Disway (tanggal 16 Feb 2026 sudah terverifikasi).
- Headline memuat nama korban dan nama bank karena itu judul berita publik. **Di naskah, jangan sebut nama korban atau nama bank.**
- **Jujur soal tanggal:** kejadiannya **20 Januari 2024** (malam Minggu). Yang terjadi 2026 adalah laporan ke Polda dan beritanya.
- **Opsi paling kuat:** kalau ada anggota tim atau keluarga yang **benar-benar** pernah kena modus ini, pakai cerita itu. Hanya kalau benar terjadi.

---

# SLIDE 2 — Data: *"She's not the exception."*

**Bagian:** Problem (data)

**Di slide (3 angka besar, tanpa kalimat):**
> **Rp134 billion** · lost to fake-APK scams via chat apps
> **≈ Rp36 million** · per report
> **< 2%** · ever recovered

**Visual:** tiga angka raksasa berjejer, label kecil di bawah masing-masing. Sumber kecil di footer: *OJK / IASC 2025–2026*.

**Yang diomongin:**
> She's not the exception — and her missing money isn't either. Indonesia's Anti-Scam Centre logged three thousand six hundred reports of this exact trick — fake APKs sent over chat — worth 134 billion rupiah. About 36 million per report. And of all scam losses reported, less than two percent ever comes back.
>
> Once the money moves, it's gone. The only moment that matters is *before*.

**Cadangan (simpan untuk Q&A):** total kerugian yang dilaporkan ke IASC Rp9.1 T (432,637 laporan) · penduduk lansia 11.97% (≈ 34 juta) · lansia online 34.13%, naik 7.7 poin dalam setahun · *"64% of cyber incidents come from human error"* (Kaspersky 2023).

---

# SLIDE 3 — *"We are RONDA."*

**Bagian:** Title, nama tim, tagline

**Di slide:**
> **RONDA**
> *A second pair of eyes on your parents' phone*
> Dhanes · Alek · Malik — Human-Centric Security

**Visual:** logo RONDA besar di tengah (`ronda_wordmark.png`), latar putih. Di bawahnya definisi kecil *ronda (n.)*, nama tim, dan chip track.

**Yang diomongin:**
> If this can happen to a public official, think about the people closest to us who didn't grow up with smartphones — our parents, our grandparents. They trust a message from someone they know. They tap what the screen tells them to tap. And when something goes wrong, they call us.
>
> That's why we built RONDA. In Indonesia, *ronda* means neighbours taking turns keeping watch at night, so everyone else can sleep. That Saturday night in Batang, nobody was on watch. We want someone to be.
>
> We're Dhanes, Alek and Malik — and like many of you, we're the ones our parents call. We're the guardians.

**Catatan tim:** kalimat pembuka ("If this can happen to a public official…") menjembatani berita ke orang tua kita, jadi jangan dipotong. Kalimat "we're the guardians" adalah jawaban untuk *why us*.

---

# SLIDE 4 — *"Every safeguard asks the person being tricked."*

**Bagian:** Problem statement & kesesuaian track

**Di slide:**
> `Undangan.apk` → **Allow** → **Install** → **Allow**
> Every question goes to **the one person being tricked.**

Kotak track di bawah (lengkap):
> **Track: Human-Centric Security** — *"Design a system that changes or influences user behavior to reduce and manage cybersecurity risks."*
> 64% of cyber incidents come from human error (Kaspersky, 2023)
> **Our answer: change who decides.**

**Visual:** alur 4 layar HP kecil dari kiri ke kanan, masing-masing dengan kursor jari yang menekan:
1. Chat: file `Undangan.apk` dengan pesan *"Mohon dibuka undangannya ya"*
2. Android: *"Install unknown apps — Allow from this source"* → toggle ditekan
3. Android: *"Do you want to install this app?"* → **Install**
4. App palsu: *"Allow access to your SMS?"* → **Allow**

Tandai titik di antara layar 3 dan 4 dengan penanda kecil RONDA: *"RONDA steps in here"* (setelah Install, sebelum izin apa pun diberikan).

**Yang diomongin:**
> Back to that invitation. When she opens it, Android does try to protect her. It asks: allow apps from this source? Then: install this app? Then the app itself asks: allow access to your messages?
>
> Every one of those questions goes to the same person — the one who trusts the message and just wants to see the invitation. So she taps Allow, Install, Allow. From the phone's point of view, she chose all of it.
>
> That's the real problem. Our protection is a series of questions, and every question goes to the person being tricked.
>
> This track asks us to change behaviour to reduce cyber risk. We don't try to turn her into a security expert. We change *who decides*.

**Catatan tim:**
- **Pemicu RONDA** (sudah dicek di kode `InstallReceiver`): RONDA menilai app **tepat setelah instalasi selesai**. Itu sebelum app dibuka dan sebelum izin apa pun diberikan. Jadi di alur ini, RONDA masuk di antara layar 3 (Install) dan layar 4 (Allow SMS).
- Teks prompt Android di atas adalah versi umum (Android 8+). Kata persisnya berbeda-beda tergantung merek HP. Di slide cukup tulis versi pendek.

---

# SLIDE 5 — *"Move the decision to someone the scammer can't reach."*

**Bagian:** Solution overview

**Di slide:**
> **Connect → Detect → Block → Alert → Decide**

| Langkah | Keterangan singkat (2–4 kata) |
|---|---|
| **Connect** | Pair once: 6-letter code or QR |
| **Detect** | Scores every new app |
| **Block** | Covers it, even offline |
| **Alert** | Guardian gets the reasons |
| **Decide** | Remove it or mark it safe |

**Visual:** HP orang tua (kiri) dan HP penjaga (kanan). Langkah **Connect** digambar sebagai garis/ikon rantai yang menghubungkan kedua HP. Empat langkah lain berjajar di antaranya. Chip kecil di bawah: *Flags the app at install — before any permission is granted.*

**Yang diomongin:**
> RONDA is one app on two phones. First, the parent's phone and the guardian's phone connect once, with a six-letter code the guardian can read out over a phone call, or a QR code.
>
> From then on, whenever an app is installed on the parent's phone, RONDA scores it on the device, in seconds, before any permission is granted. If it's dangerous, RONDA covers the app every time it's opened, even offline, and alerts the guardian with plain-language reasons. The guardian decides: remove it, or mark it safe.

---

# SLIDE 6 — *"Everyone else asks the victim. RONDA asks someone else."*

**Bagian:** USP (keunggulan dibanding solusi lain)

**Di slide (tabel 2 kolom, tanpa penjelasan):**

| | Who decides? |
|---|---|
| Google Play Protect | Victim |
| Android dev verification (2026) | Victim |
| Bank apps | Victim |
| Telco filters | — |
| **RONDA** | **Guardian** |

Satu baris di bawah: *Google strengthens the door. RONDA changes who opens it.*

**Visual:** semua baris abu-abu. Baris RONDA menyala teal dengan chip emas "Guardian".

**Yang diomongin:**
> Every defence deployed in Indonesia — Google's, the banks', the telcos' — ends in a question on the screen of the person being tricked. RONDA is the only one that brings in a second person the scammer can't reach.
>
> Two more things set it apart. RONDA gives a score, not a yes-or-no, so a harmless app stays quiet and a dangerous one stays credible. And it speaks in consequences: not "declares READ_SMS", but "this app can read your mother's bank codes".

**Siapkan untuk Q&A** (paling mungkin ditanya): *"Google already does this."* Lihat Lampiran A #1.

---

# SLIDE 7 — Demo video: *"Watch it happen."*

**Bagian:** PoC (demo fitur yang sudah jadi)

**Di slide:** video **layar penuh**, tanpa teks tambahan di slide. Semua keterangan ditempel di dalam videonya.

**Isi video (±60–75 detik, split-screen: kiri HP orang tua, kanan HP penjaga):**

| Detik | Yang terlihat | Caption di video (maks 4 kata) |
|---|---|---|
| 0–5 | Dua HP berdampingan, sudah terhubung | `Parent` · `Guardian` |
| 5–15 | APK umpan "Undangan Pernikahan" dipasang di HP orang tua | `Fake invitation installed` |
| 15–25 | HP penjaga berbunyi, notifikasi masuk | `Guardian alerted` |
| 25–35 | Orang tua membuka app → overlay merah RONDA menutupinya | `Blocked — offline` |
| 35–45 | Penjaga membuka alert: skor **85 / 100** + alasan | `Score 85 + reasons` |
| 45–55 | Penjaga menekan *"Minta Ibu menghapus aplikasi ini"* → layar permintaan muncul di HP orang tua | `Guardian asks` |
| 55–65 | Orang tua konfirmasi → dialog uninstall Android → app hilang | `Removed` |
| 65–75 | Tab Riwayat penjaga: *"sudah dihapus"* | `Guardian confirmed` |

Badge kecil di pojok sepanjang video: *Harmless test app — no real malware*

**Yang diomongin (dinarasikan live di atas video):**
> Left, the parent's phone. Right, the guardian's. They're already connected.
>
> A fake wedding invitation lands and gets installed. It's our harmless test app — it only *declares* SMS access and does nothing else.
>
> The guardian's phone rings. The parent opens the app, and RONDA covers it instantly, even offline. The guardian sees why: 85 out of 100, and the app can steal bank codes.
>
> One tap: "ask Mum to remove it." Her phone explains who asked and why. She confirms, because Android requires it. Gone — and the guardian is told only once it's really removed.

**Catatan tim — produksi video:**
- Rekam pakai `scripts/ronda demo` → `scripts/ronda attack com.whatsapp` → `scripts/ronda victim`.
- Bagian yang dipercepat diberi badge `2×`. Momen blok (overlay muncul) **jangan dipercepat**.
- Video tanpa audio (narasi diucapkan live). Simpan juga file `.mp4` terpisah kalau embed di PPT gagal.

---

# SLIDE 8 — Screenshot recap: *"Parent does nothing. Guardian decides."*

**Bagian:** PoC (rekap layar setelah video, supaya juri bisa melihat detailnya)

**Di slide:** 6 screenshot berurutan, tiap screenshot dengan label 1–2 kata dan penanda HP siapa:
① **Installed** (orang tua) · ② **Blocked** (orang tua) · ③ **Alerted** (penjaga) · ④ **Score & reasons** (penjaga) · ⑤ **Asked** (orang tua) · ⑥ **Removed** (penjaga)

**Yang diomongin:**
> Here's the same flow, frozen. On the parent's screens, she never makes a security decision. The only choice she gets is to confirm what her guardian asked. All the judgement happens on the guardian's phone — the one the scammer can't reach.

**Catatan tim:** kalau waktu mepet, tampilkan 5 detik tanpa dibahas, lalu pakai lagi saat Q&A.

---

# SLIDE 9 — *"Built for real families, not one demo."*

**Bagian:** PoC (fitur lain yang sudah jadi)

**Di slide — 6 kotak fitur (ikon + 2–4 kata):**

| Ikon | Fitur | Keterangan singkat |
|---|---|---|
| 👥 | **One guardian, many parents** | Mum, Dad, Grandma — one app |
| 🔍 | **Scans apps already installed** | the moment phones connect, plus "Scan again" |
| 🛡 | **Catches more than SMS theft** | screen control · fake login screens · notification reading · hidden icons · fake bank names · apps that install other apps |
| 💬 | **Plain-language reasons** | Indonesian & English |
| ↩ | **Safe decisions, reversible** | "mark safe" has a 10-second undo |
| 🔗 | **Either side can disconnect** | with confirmation, and the other phone is told |

(Di PPT pakai ikon RONDA sendiri, bukan emoji.)

**Visual:** grid 3×2 kartu. Kartu "Catches more than SMS theft" dibuat paling besar: tampilkan 6 chip kecil di dalamnya (*Screen control, Fake screens, Reads notifications, Hidden icon, Fake bank name, Installs other apps*).

**Yang diomongin:**
> The demo showed one app and one parent. RONDA already does more than that.
>
> One guardian can watch several phones — Mum, Dad, Grandma — from a single app. When a phone first connects, RONDA checks every app that's already on it, not just new ones.
>
> And it catches far more than SMS theft: apps that take control of the screen, show fake login pages, read your notifications, hide their icon, pretend to be a bank, or quietly install other apps.
>
> Every alert explains itself in plain Indonesian or English. And nothing is one-way: marking an app safe can be undone, and either side can disconnect — with the other side told.

**Bukti di repo:** multi-Rondee & initial scan (commit `299e9bc`), `settings_add_device` ("Tambah HP lain"), `home_protected_manual_scan` ("Scan ulang"), `action_undo` ("Batalkan"), disconnect dua arah (`b4ffb1d`), 23 sinyal di `core/Signal.kt`.

---

# SLIDE 10 — *"How RONDA scores an app."*

**Bagian:** PoC (mesin skor)

**Di slide — kiri: rumus bergaya "struk" dengan contoh app demo:**

```
WHAT IT CAN DO
  Reads SMS                       40
  + Internet   (40% of 10)         4
  + Dangerous pair: SMS + internet 15
                                  ────
                                  59
WHERE IT CAME FROM
  × Not from Play Store         × 1.25
  × Unofficial signature        × 1.15
                                  ────
SCORE                         85 / 100  → guardian alerted
```

**Di slide — kanan: matriks skor ringkas (heatmap)** — baris = apa yang bisa dilakukan app, kolom = dari mana asalnya:

| | Play Store | Sideloaded | + unofficial signature | + hidden icon |
|---|---|---|---|---|
| Internet only | 5 | 13 | 14 | 16 |
| Contacts + location | 17 | 48 | 55 | 61 |
| Reads SMS + internet | 27 | 74 | **85** | 94 |
| Screen control + fake screens | 32 | 90 | 100 | 100 |
| Almost every permission | **45** | 100 | 100 | 100 |

Legenda: Safe 0–29 · Low 30–59 · Warning 60–89 · Danger 90–100 · **≥ 60 → guardian alerted**. Sel **85** = "Our demo app", sel **45** = "Real WhatsApp".

**Visual:** kiri struk putih dengan angka tebal, kanan heatmap dengan warna band. Kalau terasa terlalu padat di slide, matriks boleh dipindah ke backup B1 dan struk dibuat lebih besar.

**Yang diomongin:**
> So how does RONDA decide? Two questions.
>
> First: what can this app do? We look at sixteen abilities, each with a weight. The most dangerous ability counts in full, the others count a little, and dangerous pairs get a bonus. Reading SMS plus internet access is the classic way to steal bank codes, so that pair adds extra.
>
> Second: where did it come from? From the Play Store, the score goes down. From a chat message, with an unofficial signature, or with a hidden icon, it goes up.
>
> Our demo app: 59 for what it can do, times where it came from, gives 85. The guardian is alerted. Real WhatsApp has almost the same abilities, but it comes from the Play Store, so it scores 45 and stays silent. That's what keeps guardians listening.
>
> The method is adapted from CVSS, the industry's standard way of scoring vulnerabilities, and every ability is mapped to MITRE ATT&CK. These exact numbers are pinned in our unit tests.

**Catatan tim:** semua angka dihitung dari bobot asli di `core/Signal.kt` + `RiskEvaluator`. Sel 45, 55, 85, 100 dikunci di `RiskEvaluatorTest.kt`.

---

# SLIDE 11 — *"Stock Android. No root. No server."*

**Bagian:** Technical architecture & tech stack

**Di slide (3 kotak saja):**

```
[ Parent's phone ]  ──alert──►  [ Firebase RTDB ]  ──►  [ Guardian's phone ]
 detect · score · block            ◄──command──            decide
   (offline)
```

Baris logo di bawah: **Kotlin · Jetpack Compose · Firebase RTDB · PackageManager API**
Baris kecil: *58 Kotlin files · ~10,000 lines · 30 unit tests · 6 harmless decoy APKs*

**Yang diomongin:**
> Everything that matters happens on the parent's phone: detection, scoring and blocking, all offline, using only Android's built-in app manager. Firebase just carries the alert one way and the guardian's decision back.
>
> No root, no custom ROM, no server of our own, and it runs on a normal Android phone today.

---

# SLIDE 12 — *"We don't use the scammers' tools."*

**Bagian:** Security framework (+ potensi paten)

**Di slide (2 kolom):**

| ✗ RONDA never | ✓ RONDA only |
|---|---|
| reads your messages | looks at app info |
| controls your screen | shows its own warning |
| watches in secret | shows who's guarding |

Baris kecil: *Honest gap: our online database needs a proper lock → first fix after the hackathon*

**Yang diomongin (bahasa sederhana):**
> A security app asks for your trust, just like a scam app does. So we set ourselves stricter rules than the apps we're catching.
>
> RONDA never reads your messages. It never asks for the special permission that lets an app see and press things on your screen — because that's exactly what scam apps use to empty bank accounts. The only thing RONDA looks at is basic app information: what an app asks to do, and where it came from.
>
> It's not a spying app, either. The parent agrees on their own phone when they connect, and their phone always shows that RONDA is on and who is guarding them.
>
> One honest gap: our online database doesn't yet check which phone is asking for data. Locking that down is our first fix after this hackathon.

**Potensi paten (kalau ditanya, satu kalimat):**
> The scanner itself isn't new. What's new is the system: the phone makes a decision, sends it to a second trusted person, blocks the app while that person decides, and then carries out their decision.

---

# SLIDE 13 — *"Rp0 today. Ready to grow."*

**Bagian:** Scalability & deployment readiness

**Di slide (timeline 4 titik, tanpa tahun):**
> **Now** working prototype ✓ → **Next** real phones + Play Store → **Then** bank pilot → **Later** scam-signal network

Baris kecil: *Paid by banks · telcos · insurers — never the elderly user*

**Yang diomongin:**
> Because detection runs on the phone, adding a family costs almost nothing. An alert is a few hundred bytes. Today it runs on Firebase's free tier.
>
> Next, we test on real low-end phones and publish on the Play Store. Then we pilot with a bank. The elderly user never pays: banks, telcos and insurers already carry this loss, and OJK's anti-fraud rule already requires banks to act.

**Cadangan Q&A:** unit economics. Dengan estimasi Rp18 rb/perangkat/tahun, bank impas jika mencegah 1 kasus per 2,000 perangkat.

---

# SLIDE 14 — *"Every case we stop keeps ≈Rp36 million in a family."*

**Bagian:** Impact & langkah berikutnya

**Di slide — 4 kotak dampak (angka besar + 3–5 kata):**

| Untuk siapa | Angka / pesan | Keterangan singkat |
|---|---|---|
| **Families** | **≈ Rp36M** | kept per case we stop |
| **Parents** | **34M** Indonesians aged 60+ | protected without becoming experts |
| **Neighbourhoods** | **1 volunteer → many homes** | RT & posyandu volunteers as guardians |
| **Institutions** | **Early warning** | anonymised detections for IASC & banks |

Baris bawah (tipis):
> **Next steps:** 100-app false-positive study · real low-end phones · community pilot in Bandung
> **We're looking for:** OJK / IASC · a digital bank · Kaspersky

**Visual:** 4 kartu dampak berjajar dengan ikon (rumah, orang tua, lingkungan/peta, gedung). Baris next steps + ask dibuat seperti pita di bawah.

**Yang diomongin:**
> What does RONDA change? For a family, every scam we stop keeps about 36 million rupiah where it belongs — and money that never leaves never needs recovering.
>
> For parents, it's protection without homework. Thirty-four million Indonesians are over sixty, and more of them come online every year. They don't have to become security experts; someone they trust has their back.
>
> For neighbourhoods, it's *ronda*, literally: one guardian can already watch several phones, so an RT volunteer or a posyandu cadre can look after elderly neighbours who don't have a tech-savvy child.
>
> And for institutions, every guarded phone becomes a sensor. In the future, anonymised detections — no personal data — can warn IASC and banks about a new scam wave early.
>
> It also changes behaviour in the family: every alert explains *why* an app is dangerous, so guardians and parents start talking about scams before one hits.
>
> Next: a 100-app false-positive study, testing on real low-end phones, and a community pilot here in Bandung. We're looking for introductions to OJK and IASC, a digital bank, and Kaspersky.

**Catatan tim:**
- Angka 34 juta = estimasi dari 11.97% penduduk (BPS 2025). Tulis "≈" kalau mau aman.
- "Early warning untuk institusi" adalah **rencana**, belum dibangun. Di naskah sudah disebut "in the future".
- Klaim perubahan perilaku ("start talking about scams") adalah **tujuan desain**, belum diukur. Jangan sebut angka.

---

# SLIDE 15 — Penutup: *"Not a better warning. A second pair of eyes."*

**Bagian:** Conclusion (kembali ke cerita pembuka)

**Di slide:**
> **Rp1.3 billion — still there.**
> **Not a better warning. A second pair of eyes.**

**Visual — ilustrasi dua HP (bagian utama slide):**
- **HP orang tua (kiri):** ikon `Undangan.apk` tertutup overlay RONDA (*"Jangan buka aplikasi ini"*) dengan stempel **Blocked**.
- **HP keluarga/penjaga (kanan):** notifikasi RONDA berdering, isinya *"Undangan Pernikahan · 85/100 · blocked on Mum's phone"* dan tombol *"Ask Mum to remove it"*.
- Garis/gelombang kecil dari HP kanan ke HP kiri sebagai tanda "HP keluarga menjaga HP ibu".
- Pakai screenshot asli ② (overlay) dan ③/④ (alert) dari slide 8 kalau sudah ada. Kalau belum, pakai ilustrasi sederhana.

**Yang diomongin:**
> Back to Batang. Same Saturday night, same invitation. This time, RONDA covers the app the moment it's installed, and her family's phone rings. They call her. The app is gone before Monday. The money never moves.
>
> Not a better warning. A second pair of eyes. Thank you.

---

# SLIDE 16 — Q&A

**Bagian:** Q&A

**Di slide:** **Thank you** · RONDA · github.com/angseleong/RONDA (+ QR ke repo/video demo)

**Catatan tim:** taruh backup slides (B1–B6) setelah slide ini supaya bisa dilompati saat menjawab.

---

# LAMPIRAN A — Q&A (jawaban singkat, ±20 detik)

| # | Pertanyaan | Jawaban (EN) |
|---|---|---|
| 1 | *Google already blocks this.* | Google is strengthening the door; RONDA changes who opens it. The 30 Sep 2026 rule covers app stores first. WhatsApp-sent APKs wait until the 2027 global rollout. Verified doesn't mean safe, and every Google dialog still asks the victim. |
| 2 | *Isn't this stalkerware?* | No. Pairing needs consent on the parent's own phone, a permanent notice shows who's guarding, only app metadata is sent, and there's no hidden mode, ever. |
| 3 | *Home button dismisses the overlay.* | Correct, and we say so. It's a delay, not a lock. The overlay is the alarm; the guardian is the defence. A real lock needs Device Owner, which is on the roadmap. |
| 4 | *Why can't the guardian uninstall remotely?* | Android forbids silent uninstalls without Device Owner, by design. The guardian requests, the parent confirms, and we report "removed" only when the OS confirms. |
| 5 | *False positives?* | Scores, not verdicts. WhatsApp scores 45, a sideloaded courier app 55. Alerts start at 60. Safe cases are pinned in unit tests, and a 100-app field study comes next. |
| 6 | *Scammer tells victim to delete RONDA.* | Permissions are re-checked on every launch, and a heartbeat to the guardian is on the roadmap. Long term, a bank can hold a transfer for the guardian, which doesn't depend on the victim's phone at all. |
| 7 | *No tech-savvy child?* | That's the name: RT/RW volunteers or posyandu cadres guard several neighbours. One guardian can already watch several phones in the app today. |
| 8 | *Why Android only?* | The scam is Android-only, and iOS doesn't let apps see other apps' permissions or trigger uninstalls. |
| 9 | *Is your backend secure?* | Honestly, prototype-grade: no device authentication yet. Anonymous Auth with `auth.uid` rules is the first fix. |
| 10 | *Tested on real phones?* | Emulators so far (Pixel 6, API 33). Logic is proven; OEM battery behaviour isn't yet. Real low-end phones are step one. *(Update kalau sudah tes di HP fisik.)* |
| 11 | *Who pays?* | Banks, telcos and insurers. They already carry the loss, and POJK 12/2024 requires anti-fraud action. Never the elderly user. |
| 12 | *Why not AI?* | Explainability. A guardian needs "can read your bank codes", not a confidence value. Our engine is deterministic, offline, auditable and runs on a Rp1-million phone. |
| 13 | *Malware could hide SMS access.* | Android forces it to declare SMS access. Malware that reads notifications instead is caught by a separate signal and its combo. |
| 14 | *What's patentable?* | The inter-device protocol: local verdict → human decision on a second device → executed back. The scanner itself isn't novel. |
| 15 | *When exactly does RONDA step in?* | The moment the installation finishes — before the app is opened and before it can ask for any permission. It reads what the app *declares* it wants, not what it was granted. |

---

# LAMPIRAN B — Backup slides (setelah slide Q&A)

| # | Isi | Untuk pertanyaan |
|---|---|---|
| B1 | Matriks skor lengkap + rumus detail (dipakai kalau matriks dipindah dari slide 10) | A5, A12 |
| B2 | Diagram arsitektur lengkap (lihat [PROJECT_REPORT §4.1](PROJECT_REPORT.md)) | A9 |
| B3 | Tabel 23 sinyal + MITRE ATT&CK (lihat `core/Signal.kt`) | A13 |
| B4 | 6 decoy APK: sms 85 · accessibility 100 · notification 70 · deviceadmin 88 · dropper 63 · overlay 43 (silent) — *cek dulu di emulator* | A5 |
| B5 | Tabel kompetitor lengkap (lihat [STRATEGY §2](STRATEGY.md)) | A1 |
| B6 | Model bisnis + unit economics (lihat [STRATEGY §6](STRATEGY.md)) | A11 |

---

# LAMPIRAN C — Sumber angka

| Angka | Sumber |
|---|---|
| Kasus Batang: Wakil Ketua DPRD, undangan APK malam Minggu 20 Jan 2024, HP hang, saldo ±Rp1.3 M tinggal Rp100 rb, lapor Polda Feb 2026, "dua tahun tanpa kepastian" | [Ayo Batang, 25 Feb 2026](https://www.ayobatang.com/batang-raya/3716776635/lapor-polda-demi-keadilan-wakil-ketua-dprd-batang-junaenah-pertanyakan-keamanan-digital-bri-setelah-tabungan-rp13-miliar-habis) · [Ayo Batang (tanggapan BRI)](https://www.ayobatang.com/batang-raya/3716743561/klik-file-apk-berujung-petaka-simpanan-wakil-ketua-dprd-batang-rp13-miliar-raib-ini-tanggapan-bri-dan-ingatkan-bahaya-social-engineering) · [Disway, 16 Feb 2026](https://jateng.disway.id/disway-pekalongan/read/716471/tabungan-wakil-ketua-dprd-batang-junaenah-hilang-rp-13-miliar-di-bri) · [Tribun Jatim](https://jatim.tribunnews.com/news/533689/uang-rp13-m-raib-usai-ponsel-mati-mendadak-saldo-wakil-ketua-dprd-batang-kini-tinggal-rp100-ribu?page=all) |
| Headline slide 1 | "Klik File APK Berujung Petaka, Simpanan Wakil Ketua DPRD Batang Rp1,3 Miliar Raib" — Ayo Batang (tanggal di halaman belum bisa dicek dari sini, isi berita Feb 2026) · cadangan: "Tabungan Wakil Ketua DPRD Batang Junaenah Hilang Rp 1,3 miliar di BRI" — Disway, 16 Feb 2026 · "Rekening Wakil Ketua DPRD Batang Diduga Dibobol, Saldo Rp 1,3 M Tersisa Rp 100 Ribu" — [Tribun Lampung](https://lampung.tribunnews.com/news/1202250/rekening-wakil-ketua-dprd-batang-diduga-dibobol-saldo-rp-13-m-tersisa-rp-100-ribu) |
| Modus undangan APK marak lagi Mei 2026 (cadangan Q&A) | [Tribun Bogor](https://bogor.tribunnews.com/techno/315713/awas-sampai-2026-penipuan-undangan-apk-di-whatsapp-masih-marak-ini-5-cara-agar-hp-tetap-aman) · [BCA, Jun 2026](https://www.bca.co.id/en/informasi/awas-modus/2026/06/09/16/57/modus-penipuan-paket-tertukar-atau-hilang-yang-perlu-diwaspadai) |
| Rp134 M, 3,684 laporan APK via chat | OJK/IASC via [Radar Surabaya](https://radarsurabaya.jawapos.com/ekonomi/776724733/kerugian-akibat-scam-digital-di-indonesia-tembus-rp7-triliun-ojk-ungkap-10-modus-utama) |
| ≈ Rp36 jt / laporan | Hitungan kami: 134 M ÷ 3,684 (tulis "≈" di slide) |
| < 2% kembali (Rp161 M) | [OJK siaran pers](https://ojk.go.id/id/berita-dan-kegiatan/siaran-pers/Pages/IASC-Berhasil-Kembalikan-Rp161-Miliar-Dana-Masyarakat-Korban-Scam.aspx) |
| 30 Sep 2026 app store dulu, global 2027 | [Android Authority](https://www.androidauthority.com/android-sideloading-changes-timeline-3679204/) · [The Hacker News](https://thehackernews.com/2026/06/google-sets-sept-30-deadline-for.html) |
| Skor 45 / 55 / 85 / 100, 23 sinyal, 30 test | `RiskEvaluatorTest.kt`, `core/Signal.kt` |
| 64% human error · kutipan challenge track | Kaspersky 2023 dan teks track Human-Centric Security, Guideline HackNusa 2026 hlm. 4 |
| 34 juta lansia | Estimasi: 11.97% (BPS 2025) × ±284 juta penduduk |
| Pemicu RONDA = saat instalasi selesai | `InstallReceiver` (`ACTION_PACKAGE_ADDED`) |

**Sudah ada di kode (boleh diklaim, per commit `299e9bc`):** initial scan app yang sudah terpasang saat pairing · satu penjaga untuk beberapa HP (multi-Rondee) · disconnect dua arah.

**Jangan diklaim** (belum ada di kode/data): FCM push, tombol override di overlay, hard-block, uji di HP fisik, angka akurasi atau jumlah pengguna, backend production-ready, dan "patent pending".

---

# LAMPIRAN D — Referensi riset pitch

- Hacktribe — *How to Build a Hackathon Pitch Deck: 5-Minute Structure* (satu ide per slide, satu alur demo, backup demo, jangan abaikan kriteria juri): https://hacktribe.co/blog/how-to-build-a-hackathon-pitch-deck-practical-5-minute-structure
- Devpost — *How to present a successful hackathon demo* (demo = bagian terpenting, lewati layar membosankan, tutup dengan dampak jangka panjang): https://info.devpost.com/blog/how-to-present-a-successful-hackathon-demo
- TAIKAI / LayerX — *How to Create a Winning Hackathon Pitch in 5 Steps* (hook cerita personal, Problem → Solution → Demo → Impact → Ask): https://taikai.network/en/blog/how-to-create-a-hackathon-pitch
- Ink Narrates — *Hackathon Pitch Deck That Judges Remember* (hook 30 detik, kredibilitas tim, ask yang jelas): https://www.inknarrates.com/post/hackathon-pitch-deck
- SlideModel — *How to Make a Presentation for a Hackathon* (jangan jadikan slide dokumentasi; juri harus mendengar, bukan membaca): https://slidemodel.com/hackathon-presentation/
