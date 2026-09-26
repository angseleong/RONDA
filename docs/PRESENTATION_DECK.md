# RONDA — Materi Presentation Deck (Final Pitching)

**Status:** Draft v1 · 26 September 2026 · bahan mentah untuk difilter tim sebelum masuk PPT
**Acara:** Final Project Pitching HackNusa 2026 — Telkom University, Bandung, 3 Oktober 2026
**Format akhir:** `.pptx` / `.pdf`, 10–15 slide, **12 menit total (pitching + Q&A)**
**Sumber materi:** kode di `app/` + `RondaTestSample/`, [PROJECT_REPORT.md](PROJECT_REPORT.md), [PRD.md](PRD.md), [ARCHITECTURE.md](ARCHITECTURE.md), [STRATEGY.md](STRATEGY.md), [finalscript.md](finalscript.md), Guideline HackNusa 2026, dan cek ulang web (26 Sep 2026)

---

## Cara pakai dokumen ini

- **Bahasa.** Guideline FAQ #7: *"The final pitching session and presentations during the onsite event will also be conducted in English."* Jadi **semua teks slide dan speaker notes ditulis dalam bahasa Inggris**. Catatan untuk tim (arahan visual, alasan, peringatan) ditulis dalam bahasa Indonesia.
- **Isi sengaja kebanyakan.** Tiap slide punya struktur yang sama:
  - **Kriteria juri** yang dikejar slide ini (dari 6 kriteria resmi, lihat tabel di bawah)
  - **Headline** — judul slide (EN)
  - **On-screen** — teks yang tampil di slide (EN). Ambil 3–5 poin saja.
  - **Visual** — saran gambar/diagram/screenshot
  - **Speaker notes** — yang diucapkan (EN)
  - **Cadangan** — materi tambahan kalau ada ruang, atau untuk Q&A
  - **Sumber / bukti di repo**
- **Aturan angka.** Semua angka di sini punya sumber (web atau kode). **Jangan menambah metrik lapangan** (akurasi, latency di HP fisik, jumlah user) — belum ada, dan PROJECT_REPORT sudah berjanji tidak mengklaimnya. Lihat [Lampiran D — Yang TIDAK boleh diklaim](#lampiran-d--yang-tidak-boleh-diklaim).

### Enam kriteria juri → slide

Dari Guideline HackNusa 2026, FAQ #15: *"Projects will be evaluated based on six criteria."*

| # | Kriteria resmi | Slide utama | Slide pendukung |
|---|---|---|---|
| 1 | Accordance with the track | 2 | 3, 10 |
| 2 | Unique selling proposition (USP) | 4 | 3, 11 |
| 3 | Technical feasibility | 7 | 5, 6, 9 |
| 4 | Proof of concept (PoC) | 5, 6 | 7 |
| 5 | Level of security and patentability | 8 | 7 |
| 6 | Scalability and deployment readiness | 9 | 10, 11 |

### Anggaran waktu (usulan)

12 menit mencakup pitching **dan** Q&A. Usulan: **±7 menit pitching, ±5 menit Q&A.**

| Slide | Isi | Pembicara (usulan, ikut finalscript) | Durasi |
|---|---|---|---|
| 1 | Title | Dhanes | 0:15 |
| 2 | Problem & track | Dhanes | 1:00 |
| 3 | Solution overview | Malik | 0:40 |
| 4 | USP | Malik | 0:50 |
| 5–6 | PoC / demo live | Dhanes | 2:00 |
| 7 | Architecture & stack | Alek | 0:40 |
| 8 | Security framework | Alek | 0:40 |
| 9 | Scalability & deployment | Malik | 0:30 |
| 10–11 | Impact & conclusion | Alek | 0:25 |
| 12 | Q&A | semua | ±5:00 |

> **Catatan tim:** kalau demo live dilakukan, slide 5–6 bisa jadi "latar" yang tampil sebelum/sesudah demo. Siapkan **video cadangan** demo (PRD R3) kalau emulator/HP bermasalah di panggung.

---

# SLIDE 1 — Title, Team Name, Tagline

**Kriteria juri:** — (first impression)

**Headline:** RONDA

**On-screen:**
- **RONDA** — Real-time On-Device Detection Agent
- Tagline (pilih satu):
  - *"Not a better warning. A second pair of eyes."* ← **rekomendasi** (dipakai sebagai kalimat penutup video juga)
  - *"Everyone else warns the victim. RONDA tells somebody else."*
  - *"Pelindung Keluarga — family-guarded protection against APK scams"*
- Team: **Dhanes · Axeleon (Alek) · Malik**
- Track: **Human-Centric Security**
- HackNusa 2026 — Telkom University × Kaspersky

**Visual:**
- Logo/wordmark RONDA (`app/src/main/res/drawable-nodpi/ronda_wordmark.png`, `ronda_mark.png`)
- Warna brand: forest green `#1D7F4E` (safe), danger red `#DC2626`, trust blue `#1E40AF`; font Nunito (lihat `docs/DESIGN.md`)
- Opsional: dua siluet HP berdampingan (HP orang tua ↔ HP penjaga) dihubungkan satu garis

**Speaker notes:**
> Good afternoon. We are RONDA — Dhanes, Alek and Malik. In Indonesia, *ronda* is the practice of neighbours taking turns keeping watch at night so everyone else can sleep. We built that idea into an Android app: when a scam app lands on your parent's phone, someone who is not being manipulated gets to decide what happens next.

**Cadangan:**
- Arti nama: *ronda* = siskamling/ronda malam — keamanan sebagai tanggung jawab sosial bersama, bukan skill teknis individu. Ini jembatan langsung ke "Human-Centric".
- Repo: https://github.com/angseleong/RONDA

---

# SLIDE 2 — Problem Statement & Track Alignment

**Kriteria juri:** #1 Accordance with the track

**Headline (pilih satu):**
- *"The attack doesn't break the phone. It goes through the person holding it."*
- *"Indonesia's APK scam: no exploit, just trust."*

**On-screen (versi ringkas):**
- A WhatsApp file called `Undangan Pernikahan.apk` → installed → SMS permission granted → banking OTP forwarded → account drained
- **Rp134 billion** lost to APK-via-WhatsApp scams · **3,684 reports** · **≈ Rp36 million per victim** (OJK/IASC)
- **< 2%** of scam losses ever recovered (IASC)
- **64%** of cyber incidents come from human error (Kaspersky, 2023)
- The warning goes to **the one person the scammer is already talking to**

**Visual:**
- Kiri: mockup chat WhatsApp berisi `Undangan Pernikahan.apk` (caption kecil "Illustrative scenario")
- Kanan: rantai serangan 5 langkah sebagai alur panah:
  `Contact on WhatsApp → APK sent → Victim installs → Grants SMS → OTP stolen → Account drained`
- Bawah: tiga angka besar `Rp36 jt / victim` · `<2% recovered` · `64% human error`

**Speaker notes:**
> Here is a scam that is routine in Indonesia. A stranger on WhatsApp poses as a courier, a bank officer, or a wedding guest. They send a file — a wedding invitation, a delivery receipt. They stay on the phone and walk the victim, usually an elderly parent, through installing it and granting SMS permission. The app forwards the bank's OTP codes. The account is drained.
>
> No vulnerability is exploited. The victim installs it. The victim grants the permission. From the phone's point of view, nothing abnormal happened.
>
> Indonesia's Anti-Scam Centre recorded 134 billion rupiah lost to this one method — about 36 million per report — and less than two percent of scam losses ever come back.
>
> The industry's answer is a warning on the victim's screen. But the scammer's script already says: "a warning will appear, that's normal, just tap continue." The decision lands on the person least able to make it, at the worst possible moment.

**Track alignment (bisa jadi setengah slide sendiri, atau slide 2b):**

- Track brief (Guideline hlm. 4): *"Technology alone is not sufficient… 64% of cyber incidents … were caused by human error."* Challenge: *"design a system that changes or influences user behavior to reduce and manage cybersecurity risks."* Area yang disebut termasuk *"UX solutions that make secure behavior easier and more intuitive."*
- **RONDA's reading of the brief:** most entries try to make the victim a better decision-maker (training, gamification, clearer warnings). That approach has a ceiling this attack sits above — you cannot design a warning good enough to beat a human coaching the victim past it *in real time*.
- **So RONDA changes the behaviour of the *system*, not the victim:** it moves the decision to a second human — a guardian — who is not under the scammer's influence.
- Human-centric on both sides:
  - Protected phone: zero security decisions, ≥20 sp text, one action per screen, plain Indonesian
  - Guardian phone: evidence in consequence-language, not jargon ("can read the bank's OTP codes", not "declares READ_SMS")

**On-screen alternatif untuk bagian track:**
> Track challenge: *"change or influence user behavior to reduce cybersecurity risk"*
> RONDA's answer: **don't retrain the victim — change who decides.**

**Cadangan (angka tambahan):**

| Fakta | Angka | Sumber |
|---|---|---|
| Kerugian scam digital dilaporkan ke IASC | **Rp9.1 trillion**, 432,637 reports (22 Nov 2024 – Jan 2026) | OJK/IASC via TIMES Indonesia [S1] |
| APK via WhatsApp/Telegram | **3,684 reports, Rp134 billion** (Nov 2024 – 15 Oct 2025), top-10 method | OJK via Radar Surabaya/Jawa Pos [S3] |
| Rata-rata kerugian per laporan APK | **≈ Rp36.4 million** (hitungan kami: 134 M ÷ 3.684) | derived |
| Dana yang dikembalikan IASC | **Rp161 billion — < 2%** of losses | OJK press release [S4] |
| Dana yang berhasil diblokir IASC | Rp436.88 billion (s.d. Jan 2026) | OJK/IASC [S1] |
| Laporan IASC s.d. Mei 2026 | 579,459; Jawa Barat tertinggi (119,750) | Kompas [S2] |
| Penduduk lansia (60+) | **11.97% (≈ 34 juta)** — ageing population | BPS 2025 [S6] |
| Lansia punya HP / akses internet | 52.23% / **34.13%**, naik 7.7 poin dalam setahun | BPS via Dataloka [S7] |

**Tiga kesimpulan dari angka (bisa jadi voice-over):**
1. **Recovery doesn't work** — all value is in the minutes *before* the transfer, i.e. between install and permission grant. That is exactly RONDA's window.
2. **Reported numbers are a floor** — elderly victims often don't report, are ashamed, or never connect the "invitation" to the missing money.
3. **The exposed population grows on two axes** — more elderly, and a faster-growing share of them online.

**Kenapa warning gagal (3 alasan, dari PROJECT_REPORT §1.2):**
1. The warning arrives after 20 minutes of trust-building with the "officer".
2. Scammers pre-script it: *"a warning will pop up, just tap continue."*
3. Signature detection lags — the APK is repackaged per campaign.

---

# SLIDE 3 — Proposed Solution Overview

**Kriteria juri:** #1 Track, #2 USP (pengantar)

**Headline:** *"One app, two phones, one decision moved to the right person."*

**On-screen:**
- **One APK, two roles** chosen at first launch:
  - **Protected phone** (the parent) — detects and blocks. Never asked to make a security judgement.
  - **Guardian phone** (the adult child / relative) — receives the alert, sees the reasons, decides.
- **5 steps:** DETECT → BLOCK → ALERT → DECIDE → ACT
- Detection reads **declared** permissions at install time — **before the victim grants anything**

**Visual — alur 5 langkah (inti slide):**

```
PROTECTED PHONE                                         GUARDIAN PHONE
1. DETECT  APK installed → score 0–100, offline, seconds
2. BLOCK   score ≥ 60 → full-screen RONDA warning over the app (works offline)
3. ALERT   ───────── verdict + score + plain-language reasons ─────────►
4.                                                      DECIDE  Uninstall  /  Mark safe
5. ACT     ◄──────────────── command ─────────────────────────
           Screen explains "your guardian asked to remove this app"
           → Android uninstall dialog → app gone → guardian sees "removed"
```

**Speaker notes:**
> RONDA is one Android app that runs in one of two roles. On the parent's phone it watches for new installs. The moment an APK lands, RONDA reads what the app declares it can do and where it came from, and scores it from 0 to 100 — entirely on the device, in seconds.
>
> Above 60, two things happen at once. The app is covered with a full-screen RONDA warning every time it's opened — even with no internet. And the guardian's phone gets an alert with the score and the reasons in plain Indonesian.
>
> The guardian decides: remove it, or mark it safe. If they choose remove, the parent's phone explains who asked and why, and opens Android's uninstall dialog. The guardian sees when it's actually gone.
>
> Crucially, RONDA reads *declared* permissions — what the app asks for in its manifest — not *granted* ones. So it flags intent at install time, before the victim has granted anything. That gap is the window the whole product lives in.

**Cadangan:**
- Pairing: guardian shows a **6-character code + QR** (`ronda://pair/CODE`); the parent types the code or scans the QR. Designed for the realistic case — **a phone call to a parent in another city**. Code alphabet excludes O/0, I/1, S/5, B/8 so it survives being read aloud.
- Bahasa UI: Indonesia (default) dan English, dipilih saat onboarding.
- Soft-block ≠ lock: "the overlay is the alarm, the guardian is the defence".

---

# SLIDE 4 — Unique Selling Proposition (USP)

**Kriteria juri:** #2 USP

**Headline:** *"Everyone else warns the victim. RONDA is the only one that tells somebody else."*

**On-screen:**
- **USP 1 — Guardian-mediated intervention.** The decision leaves the handset the scammer is coaching.
- **USP 2 — Scores, not verdicts.** 0–100 with named reasons, so quiet apps stay quiet and loud ones stay credible.
- **USP 3 — Consequence language.** "This app can read your bank's OTP codes" — not "declares READ_SMS".
- **USP 4 — Minimal privilege.** No SMS permission, no Accessibility Service — RONDA never uses the trojans' own toolkit.

**Visual — tabel kompetitor dengan satu kolom kunci "Who decides?":**

| | What they do | Who decides? |
|---|---|---|
| Google Play Protect – Enhanced Fraud Protection (ID, Feb 2025) | Blocks sideloads requesting SMS / notification / accessibility | Victim (can switch it off) |
| Android Developer Verification (ID, 30 Sep 2026) | Unverified apps → 24-hour "advanced flow" | Victim (*"is someone pressuring you?"*) |
| Android 17 Live Threat Detection | On-device AI flags SMS forwarding / overlay abuse | Victim (flagships first) |
| Bank apps (BRImo, Livin', myBCA) | Malware check when the bank app opens | Victim, at transaction time |
| Telcos (Siscamling, SATSPAM) | Network filtering of calls/SMS/links | Nobody on-device; WhatsApp is E2E |
| ScamShield (Singapore) | Hotline + banking kill switch | Victim (self-service) |
| Seraph Secure / Scammer Guardian (US) | Paid family notification | Family — **proof the model sells**, but US-only |
| **RONDA** | On-device detection + block + guardian alert | **Guardian** |

**Speaker notes:**
> Every defence deployed in Indonesia today — Google's, the banks', the telcos' — ends in a dialog on the screen of the person being manipulated. RONDA is the only one that brings a second human into the loop, and that human is out of the scammer's reach.
>
> That's not a feature difference, it's a category difference. The closest thing we found is in the US — paid services that notify family members. They prove families will pay for this model. Nobody offers it in Indonesia.
>
> Second: RONDA scores instead of issuing verdicts. A binary "malicious/safe" label that sometimes cries wolf trains guardians to ignore it. A number with named reasons lets a harmless app read harmless — which is what keeps a dangerous one credible.

**Positioning terhadap Google 2026–2027 (siapkan — pertanyaan juri paling tajam):**

Headline cadangan: ***"Google is strengthening the door. RONDA changes who opens it."***

1. **The window is open now.** 30 Sep 2026 enforcement in Indonesia (with Brazil, Singapore, Thailand) first covers apps distributed via app stores (Play, Galaxy Store, GetApps, OPPO, vivo, etc.). Global expansion to all apps on certified devices is **2027**. *(dikonfirmasi ulang 26 Sep 2026 — Android Authority, The Hacker News, Android Developers Blog)*
2. **Verified ≠ safe.** Verification binds identity, not behaviour. Kaspersky itself: *"attackers will likely find ways to bypass verification."* RONDA reads what an app **can do**, not who signed it.
3. **The next scam needs no malicious APK.** "Share screen" scams use AnyDesk/TeamViewer — legitimate, verified, on Play Store. RONDA can treat "a remote-access app just appeared on Mum's phone" as a signal for a human to judge (roadmap Phase 2).
4. **Google will always ask the victim.** Even the advanced flow asks *"is someone pressuring you?"* — of the person being pressured. Google doesn't know who your son is. RONDA does.

**Cadangan:**
- Paten/IP (kriteria #5) — hal yang benar-benar baru bukan pemindai izin (prior art banyak), tetapi **arsitektur intervensi via penjaga**: verdict lokal → dikirim ke perangkat manusia kedua yang sudah dipasangkan → blok lokal sambil menunggu → keputusan dieksekusi balik di perangkat asal. Detail di slide 8.

---

# SLIDE 5 — Proof of Concept (1/2): Detection, Scoring & Guardian Alert

**Kriteria juri:** #4 PoC, #3 Technical feasibility

**Headline:** *"Working end to end: install on one phone, the other phone alerts."*

**On-screen:**
- **Risk scoring engine** — 23 signals (16 capability + 7 trust), 5 intent combinations, each capability mapped to **MITRE ATT&CK for Mobile**
- **Plain-language explanations** — 23 curated Indonesian sentences + 5 combo sentences
- **Real-time guardian alert** — score, band, and reasons on the guardian's phone
- Calibrated on real cases (below) — **WhatsApp from Play Store stays quiet; a sideloaded SMS-reader alerts**

**Visual — tabel hasil nyata (bukan mockup; angka dari unit test + fixture + decoy):**

| App (scenario) | Signals | Score | Band | Guardian? |
|---|---|---|---|---|
| Real WhatsApp, Play Store | SMS, contacts, camera, mic, location… + Play | **45** | RENDAH | Silent |
| Sideloaded game, internet only | Internet + sideload + self-signed | **14** | AMAN | Silent |
| Sideloaded courier app | Contacts, location, phone state, internet | **55** | RENDAH | Silent |
| "Senter" flashlight that reads SMS | SMS + internet + sideload + self-signed | **85** | PERINGATAN | **Alert** |
| Banking trojan pattern | Accessibility + SMS + overlay + installer + notif… | **100** | DARURAT | **Alert** |

Band: **AMAN** 0–29 · **RENDAH** 30–59 · **PERINGATAN** 60–89 · **DARURAT** 90–100 · guardian threshold **60**

**Screenshot yang perlu diambil (shotlist):**
1. Kartu skor di `AlertDetailScreen` guardian: `85 / 100 — PERINGATAN`, dengan eyebrow `PENCURIAN OTP` · `BUKAN PLAY STORE` · `TANDA TANGAN TIDAK RESMI`
2. Notifikasi prioritas tinggi di HP guardian: "BAHAYA: aplikasi mencurigakan dipasang"
3. `GuardianHomeScreen` tab Peringatan — daftar dengan skor (pakai kode demo `DEMO01` → fixture 5 app: Senter Super Terang 85, Info BCA Mobile 100, Cek Resi Kilat 83, WhatsApp 45, Sudoku Offline 14)
4. `ExplanationStack` — kalimat konsekuensi ("Aplikasi ini bisa membaca SMS Ibu dan mengirimnya ke internet. Ini pola yang dipakai untuk mencuri kode OTP bank.")

**Speaker notes:**
> This is the scoring engine at work. RONDA reads 23 signals through Android's PackageManager — what the app can do, like reading SMS or controlling the screen, and where it came from — Play Store, sideloaded, self-signed. Combinations carry intent: reading SMS *plus* internet access is the complete OTP-theft path, so it scores higher than either alone.
>
> Look at the first row. Real WhatsApp from the Play Store requests SMS, contacts, camera, microphone — and scores 45. Silent. A sideloaded flashlight that reads SMS scores 85, and the guardian is woken up. That difference is the whole point: a system that alerts on everything is worse than no system, because guardians learn to swipe it away.
>
> And the guardian doesn't get "declares READ_SMS". They get: "This app can read your mother's SMS and send it to the internet. This is the pattern used to steal bank OTP codes."

**Cadangan — detail teknis engine (bisa pindah ke slide 7 atau lampiran):**

```
impact = heaviest signal + 0.4 × Σ(other signals) + combo bonuses   → cap 100
trust  = product of trust multipliers                               → clamp 0.45 … 1.6
score  = min(100, round(impact × trust))
```

- Struktur diadaptasi dari **CVSS** (impact term × provenance term) → auditable, explainable, bukan black-box model.
- Faktor **0.4 diminishing returns** adalah pilihan desain yang menahan beban: penjumlahan naif akan mendorong app sah yang banyak izinnya langsung ke 100.
- Setiap verdict mengeluarkan **vector string** untuk log/audit, contoh nyata dari test:
  `RONDA:1.0/SMS_READ:40/INTERNET:10/CMB:15/SRC:SIDELOAD/CERT:SELF=85`

**Signal & bobot (dari `core/Signal.kt`):**

| Impact signal | Bobot | MITRE ATT&CK Mobile |
|---|---|---|
| ACCESSIBILITY | 45 | T1516 Input Injection |
| DEVICE_ADMIN | 40 | T1626.001 Device Administrator Permissions |
| SMS_READ | 40 | T1636.004 SMS Messages |
| INSTALL_PKG | 30 | (dropper chain) |
| OVERLAY | 30 | T1417.002 GUI Input Capture |
| NOTIF_LISTENER | 30 | T1517 Access Notifications |
| AUDIO | 25 | T1429 Audio Capture |
| CALL | 25 | T1616 Call Control |
| CONTACTS | 20 | T1636.003 Contact List |
| CAMERA | 20 | T1512 Video Capture |
| LOCATION | 20 | T1430 Location Tracking |
| PHONE_STATE | 15 | T1426 System Information Discovery |
| QUERY_PKGS | 15 | T1418 Software Discovery |
| BOOT | 10 | T1398 Boot or Logon Initialization Scripts |
| INTERNET | 10 | (exfiltration channel) |
| FG_SERVICE | 5 | T1541 Foreground Persistence |

| Trust signal | Multiplier |
|---|---|
| Play Store | ×0.45 |
| Known store (Galaxy Store, F-Droid, Amazon) | ×0.80 |
| Sideloaded | ×1.25 |
| Self-signed certificate | ×1.15 |
| No launcher icon (hidden) | ×1.25 |
| Legacy target SDK (< Android 6) | ×1.15 |
| Brand-mimicking name (bca, bri, mandiri, dana, whatsapp…) | ×1.20 |

| Combination (intent) | Bonus |
|---|---|
| SMS_READ + INTERNET — complete OTP theft path | +15 |
| ACCESSIBILITY + OVERLAY — classic banking trojan | +15 |
| NOTIF_LISTENER + INTERNET — OTP theft without SMS permission | +15 |
| DEVICE_ADMIN + NO_LAUNCHER — hides and resists removal | +15 |
| INSTALL_PKG + SIDELOAD — dropper | +10 |

**Bukti di repo:** `app/src/main/java/com/ronda/app/core/RiskEvaluator.kt`, `core/Signal.kt`, `detect/SignalExtractor.kt`, `app/src/test/.../RiskEvaluatorTest.kt`, `ui/guardian/FakeGuardianRepository.kt`, `res/values-in/strings_capabilities.xml`

---

# SLIDE 6 — Proof of Concept (2/2): Block, Decide, Act

**Kriteria juri:** #4 PoC

**Headline:** *"Blocked offline in under a second. Removed on the guardian's word."*

**On-screen:**
- **Offline soft-block** — full-screen RONDA overlay covers the flagged app every time it opens; no "continue anyway" button; works with the network off
- **Guardian decision** — *Request uninstall* or *Mark safe* (confirmed, undoable for 10 s)
- **Remote uninstall request** — parent's phone explains who asked and why, then opens Android's uninstall dialog; guardian is told only when the OS confirms removal
- **Pairing** — 6-char code or QR deep link, no camera permission
- **6 harmless decoy APKs** — we never use real malware

**Visual — alur screenshot berdampingan (kiri HP orang tua, kanan HP penjaga):**
1. Overlay merah RONDA di HP orang tua: *"Jangan buka aplikasi ini"* · *"Penjaga Anda sudah diberi tahu"* · *"Tekan tombol Home untuk keluar"*
2. `AlertDetailScreen` → tombol `Minta Ibu menghapus aplikasi ini`
3. `UninstallPromptScreen` di HP orang tua (teks besar, satu keputusan)
4. Dialog uninstall sistem Android
5. HP penjaga: tab `Riwayat` — "Aplikasi berbahaya sudah dihapus"
6. Pairing: `GuardianPairingScreen` (kode besar + QR) dan `ProtectedPairingScreen`
7. Onboarding: bahasa → intro → pilih peran; wizard izin 4 langkah di HP orang tua

**Tabel decoy APK (`RondaTestSample/`, 6 flavor, masing-masing kosong/tidak berbuat apa-apa):**

| Flavor | Tampil sebagai | Yang dideklarasikan | Skor | Hasil |
|---|---|---|---|---|
| `sms` | Undangan Pernikahan | READ_SMS + INTERNET | **85** | PERINGATAN → alert |
| `accessibility` | Update Sistem | Accessibility service + overlay | **100** | DARURAT → alert |
| `notification` | Cek Resi Kilat | Notification listener + internet | **70** | PERINGATAN → alert |
| `deviceadmin` | Layanan Keamanan | Device admin + hidden icon | **88** | PERINGATAN → alert |
| `dropper` | — | Install packages + internet | **63** | PERINGATAN → alert |
| `overlay` | — | Overlay only | **43** | RENDAH → **deliberately silent** |

*(Skor dihitung dari bobot di `Signal.kt` dengan asumsi sideload + self-signed. Konfirmasi di emulator sebelum dipasang di slide.)*

**Speaker notes (jika demo live):**
> Two phones, live. Left is the parent's, right is the guardian's. They're already paired — a six-letter code, read aloud over a phone call.
>
> I install our test app on the parent's phone. It's harmless — it declares SMS access and does nothing. We never use real malware; we simulate the delivery, not the detection.
>
> Watch both phones. The parent opens the app — covered, offline, in under a second. The guardian has the score and the reasons.
>
> The guardian taps "ask Mum to remove this app." Her phone explains who asked and why, and opens Android's uninstall dialog — Android requires the person holding the phone to confirm; no app can delete another silently, and we say so openly. Confirmed. Gone. And the guardian sees it — only after the OS confirms, never just because a command was sent.

**Rencana demo (dari `scripts/ronda` dan README):**
- `scripts/ronda demo` → nyalakan 2 emulator, pasang, reset, pairing, sideload umpan
- `scripts/ronda attack com.whatsapp` → instalasi diatribusikan ke WhatsApp (`adb -i`)
- `scripts/ronda victim` → buka APK umpan → overlay menutupi
- Bukti aturan kontra: `scripts/ronda attack com.android.vending` → seolah dari Play Store → skor turun, **tanpa alert**
- Kode `DEMO01` → mode fixture offline (5 app) untuk cadangan kalau jaringan venue buruk

**Bukti verifikasi (dari TODO.md, log 14 Agustus, dua emulator Pixel 6 / API 33):**
- Pairing `QTDEZ3` → status `active` di RTDB
- Install APK uji → risk HIGH → alert ditulis ke `alerts/QTDEZ3/` → guardian notifikasi `importance=4, category=alarm`
- Overlay menutupi app uji; *Mark safe* → overlay hilang **saat app masih di layar**, tanpa aksi di HP orang tua
- Uninstall → dialog sistem → paket hilang → guardian menerima "sudah dihapus"
- **Bug nyata yang ditemukan dengan menjalankan, bukan dari build** (bagus untuk kredibilitas): `ACTION_DELETE` ditolak diam-diam tanpa `REQUEST_DELETE_PACKAGES`; prompt uninstall tak muncul kalau RONDA sedang terbuka; `DetectionService` tidak mengambil pairing di HP yang disetel dari nol. Semua diperbaiki dan diverifikasi ulang.

**Angka repo (dihitung ulang 26 Sep 2026):**
- **54** file Kotlin, **≈ 8,380** baris di `app/src/main`
- **30** JVM unit tests: 16 scoring engine (termasuk 5-case calibration table), 8 pairing deep-link parser, 5 explanation coverage, 1 template
- **44** commits, 3 kontributor

**Cadangan — detail yang bisa ditanyakan:**
- Overlay menyerap sentuhan dan tombol Back; satu-satunya jalan keluar adalah Home. Dibranding RONDA, tidak pernah meniru dialog sistem.
- Offline: deteksi + overlay tidak butuh jaringan. Alert yang ditulis saat offline diantrikan (`setPersistenceEnabled(true)`) dan dikirim saat online. *"Telling the victim to turn off mobile data doesn't suppress the alert — it only delays it."*
- Update app yang sudah ditandai tetap diblokir (`EXTRA_REPLACING` diabaikan). App yang pernah ditandai aman lalu di-uninstall → dicabut dari allowlist; instal ulang = pertanyaan baru.
- Restart HP: daftar app berbahaya tersimpan lokal; buka RONDA → layanan dan overlay menyala lagi.
- Transparansi: notifikasi permanen di HP orang tua bahwa RONDA aktif, dan lembar info "Dijaga oleh [nama]" — tanpa tombol ubah/putus pairing di sisi orang tua.

---

# SLIDE 7 — Technical Architecture & Tech Stack

**Kriteria juri:** #3 Technical feasibility

**Headline:** *"Stock Android. No root, no server, no cloud model."*

**On-screen:**
- Detection surface = **`PackageManager` only** (`getInstallSourceInfo()` API 30+, declared permissions, services, receivers, signing certificate)
- Scoring = **pure Kotlin, zero Android imports** → unit-tested on the JVM, deterministic, offline
- Transport = **Firebase Realtime Database**, one mechanism both ways (alerts ↑, commands ↓)
- Cost today = **Rp0** (Firebase Spark free tier)

**Visual — diagram arsitektur (redraw jadi diagram rapi):**

```
┌──────────────── PROTECTED PHONE ─────────────────┐
│  DetectionService (foreground)                    │
│    └─ registers InstallReceiver at runtime        │
│  OS: ACTION_PACKAGE_ADDED                         │
│            ↓                                      │
│  SignalExtractor  ── PackageManager only          │
│    · getInstallSourceInfo()      → provenance     │
│    · getPackageInfo(PERMISSIONS / SERVICES /      │
│      RECEIVERS / SIGNING_CERTIFICATES) → capability│
│            ↓                                      │
│  RiskEvaluator → Verdict(score, band, reasons)    │
│            ↓                                      │
│   score ≥ 60 ──┬── OverlayService (offline block) │
│                └── AlertRepository ──┐            │
└──────────────────────────────────────│────────────┘
                                       ↓
                        Firebase Realtime Database (Singapore)
                          alerts/{pairingId}/{alertId}
                        commands/{pairingId}/{commandId}
                                       ↓
┌──────────────── GUARDIAN PHONE ──────│────────────┐
│  GuardianAlertService (held-open listener)        │
│            ↓                                      │
│  High-priority notification → AlertDetailScreen   │
│            ↓                                      │
│  Uninstall / Mark safe → CommandRepository ───────┘
└───────────────────────────────────────────────────┘
```

**Tech stack (tabel/ikon):**

| Layer | Pilihan |
|---|---|
| Platform | Android native, **Kotlin**, minSdk **30**, target/compile SDK 37 |
| UI | **Jetpack Compose, Material 3**, custom design system (Nunito, light + dark) |
| Async | Kotlin **Coroutines + Flow** |
| Backend | **Firebase Realtime Database** (disk persistence on) — no own server |
| QR | ZXing core (generate only — **no camera permission**) |
| Blocking | `SYSTEM_ALERT_WINDOW` + `PACKAGE_USAGE_STATS` (`UsageStatsManager`, ±700 ms polling while an app is flagged) |
| Tooling | Android Studio, Gradle (AGP 9.3), GitHub, `scripts/ronda` two-emulator demo driver |
| Testing | JUnit (30 JVM tests), 6-flavor decoy APK project |
| Languages | Indonesian + English (per-app locale) |

**Speaker notes:**
> Architecturally, RONDA is deliberately small. On the parent's phone, a foreground service listens for new installs. A signal extractor reads the app through PackageManager — and only PackageManager. A pure-Kotlin evaluator with no Android dependencies turns that into a score, which is why we can pin it with unit tests on a laptop.
>
> Alerts and commands flow through Firebase Realtime Database. We originally designed for Firebase Cloud Messaging, but Google retired the legacy server key in 2024 — device-to-device push now needs a paid backend. So the guardian holds an open listener instead: no server, no billing, sub-second delivery in testing. The trade-off, stated openly: if an OEM kills that service, alerts wait until RONDA reopens. FCM is the upgrade once there's a backend.

**Cadangan — keputusan teknis & alasannya:**

| Keputusan | Alasan |
|---|---|
| RTDB listener, bukan FCM | FCM legacy key dimatikan Juni 2024; HTTP v1 butuh backend (Cloud Functions, Blaze plan). **Gain:** tanpa backend, tanpa billing, sub-detik, satu mekanisme dua arah. **Cost:** bergantung `GuardianAlertService` tetap hidup. |
| Data dinest per pairing | Guardian subscribe satu node, hanya menerima data keluarganya. Tanpa `orderByChild`, tanpa `.indexOn`, tanpa path lintas keluarga. |
| `timestamp` = server clock | Jam HP bisa dimanipulasi; `ServerValue.TIMESTAMP` tidak. |
| `executedAt` = "delivered", bukan "done" | Penghapusan hanya dikonfirmasi oleh broadcast `ACTION_PACKAGE_REMOVED`. Guardian tidak pernah diberi tahu "sudah dihapus" hanya karena perintah diterima. |
| `packageName` diduplikasi ke command | Bertindak pada paket yang salah tidak bisa dibatalkan. |
| `InstallReceiver` didaftarkan runtime | Manifest receiver untuk `PACKAGE_ADDED` tidak lagi bisa sejak API 26. |
| Single Activity, tanpa NavGraph | Layar = fungsi dari peran + status pairing; satu sumber kebenaran. |
| iOS tidak ada | iOS tidak bisa enumerasi app, deteksi instalasi, baca izin app lain, atau memicu uninstall. **Fakta arsitektur, bukan preferensi.** Scam-nya juga khas Android. |

**Data model (RTDB):**

| Node | Field kunci |
|---|---|
| `pairings/{code}` | `guardianDeviceId`, `protectedDeviceId`, `status` (pending→active→revoked), `createdAt`, `expiresAt` (+10 min) |
| `alerts/{pairingId}/{alertId}` | `packageName`, `appLabel`, `installSource`, `flaggedPermissions`, `status`, `timestamp` (server) |
| `commands/{pairingId}/{commandId}` | `alertId`, `action`, `packageName`, `createdAt`, `executedAt` |

---

# SLIDE 8 — Security Framework

**Kriteria juri:** #5 Level of security **and patentability**

**Headline:** *"A security app for vulnerable users must be held to a higher standard than the malware it detects."*

**On-screen — 5 aturan keras (pelanggaran = diskualifikasi, di level aturan proyek):**
1. **Never requests any SMS permission** — it detects apps that do
2. **Never uses an Accessibility Service** — the #1 banking-trojan abuse vector
3. **Never reads message content** — WhatsApp or anywhere else
4. **Data minimisation** — only package metadata leaves the phone (package name, label, install source, declared permissions, timestamp)
5. **Visible consent** — the parent always sees they are protected and by whom; no covert mode, ever

**Visual — "What RONDA asks for vs. what it refuses":**

| RONDA declares | Why | RONDA never declares |
|---|---|---|
| `QUERY_ALL_PACKAGES` | Read other apps' manifests | `READ_SMS` / `RECEIVE_SMS` |
| `SYSTEM_ALERT_WINDOW` | Draw the RONDA warning | `BIND_ACCESSIBILITY_SERVICE` |
| `PACKAGE_USAGE_STATS` | Know the foreground *package name* only | `CAMERA` |
| `REQUEST_DELETE_PACKAGES` | *Request* an uninstall (user confirms) | `READ_CONTACTS`, location, mic |
| `INTERNET`, notifications, foreground service, battery exemption | Alerts & staying alive | Device Owner / root |

*(Dari `app/src/main/AndroidManifest.xml` — bisa ditunjukkan langsung.)*

**Speaker notes:**
> RONDA asks for the same trust the attacker is asking for, so we hold it to a stricter standard. Five rules in our project are treated as disqualifying, not as preferences. RONDA never requests SMS permission — it detects apps that do. It never uses an accessibility service — that's the banking trojans' main tool, and using it would make RONDA look exactly like what it's hunting.
>
> That has a cost we accept deliberately: accessibility would give a much stronger block. We take the weaker block in exchange for a far smaller privilege footprint. Our entire detection surface is PackageManager — it reveals no screen content, no keystrokes, no app data.
>
> And the thing a judge should ask about any family app: is this stalkerware? No. Pairing needs a code confirmed on the parent's own phone, a permanent notification shows monitoring is on and who the guardian is, and there is no covert mode — not deferred, permanently out of scope.

**Threat model (bisa jadi slide 8b atau cadangan Q&A):**

| Ancaman | Mitigasi |
|---|---|
| RONDA dipakai sebagai stalkerware | Pairing butuh kode yang diketik/ditap di HP orang tua; indikator permanen; tanpa unpair jarak jauh; tanpa mode tersembunyi |
| Penipu menyuruh korban mencopot RONDA / mencabut izin overlay | Izin dicek ulang setiap launch; kehilangan kemampuan = dilaporkan ke guardian (roadmap: heartbeat). Fase 3 (bank menahan transfer) tidak bergantung pada HP korban |
| Overlay meniru dialog sistem | Dilarang. Overlay dibranding RONDA — meniru UI sistem adalah teknik trojan |
| Overlay dipakai memblokir app sembarang | Hanya boleh menutup paket yang ditandai engine |
| Deep link jahat memasangkan HP diam-diam | `ronda://pair` **hanya mengisi kolom**; klaim selalu butuh tap eksplisit di HP orang tua |
| Kode pairing di-brute force / dipakai ulang | Sekali pakai, kedaluwarsa 10 menit, ditegakkan oleh database rules (bukan client) |
| False positive melatih guardian mengabaikan alert | Skor dua sumbu + diminishing returns, band "quiet", allowlist guardian, calibration test yang gagal build kalau kasus aman bergeser naik |

**Kelemahan yang kami ungkap sendiri (penting untuk kredibilitas di depan juri Kaspersky):**
> Current Firebase rules are **PoC-grade and must not ship**: there is no authentication binding a node to a device, so anyone who guessed a pairing code could read that family's alerts. Fix: Firebase Anonymous Auth, store device IDs as `auth.uid`, rules `auth.uid === data.child('guardianDeviceId').val()`. First post-hackathon task.
>
> *"A security product that overstates its own posture has already failed its users."*

**Compliance:**
- Android permission model penuh; tanpa root, tanpa eskalasi hak
- Google Play Device & Network Abuse + Stalkerware policy → dipenuhi oleh visible consent
- **UU PDP** (UU 27/2022) → data minimisation by design + consent eksplisit yang bisa dicabut
- **Android Developer Verification** → RONDA sendiri akan registrasi sebagai developer terverifikasi (rencana 90 hari)

**Patentability / IP (kriteria #5 menyebut "patentability" — jangan dilewat):**
- **Yang baru:** bukan pemindai izin (prior art luas), tetapi **arsitektur guardian-mediated intervention** — verdict keamanan lokal dikirim ke perangkat manusia kedua yang sudah dipasangkan, perangkat asal memblokir secara lokal sambil menunggu keputusan manusia itu, lalu keputusan dieksekusi balik di perangkat asal. Ditambah model skor dua sumbu dengan bonus kombinasi yang mengodekan *niat* penyerang, dan lapisan penjelasan berbahasa konsekuensi.
- **Realistis di Indonesia:** UU Paten No. 13/2016 mengecualikan program komputer *semata*, tetapi implementasi dengan efek teknis konkret bisa memenuhi syarat. Klaim terkuat dibingkai sebagai **sistem teknis** (protokol verdict-dan-perintah antar-perangkat dengan penegakan lokal menunggu otorisasi jarak jauh), bukan metode bisnis.
- **Urutan pragmatis:** (1) pencatatan hak cipta kode di DJKI · (2) merek "RONDA" + wordmark · (3) defensive publication (report + repo publik) agar pihak lain tidak bisa mematenkan mekanismenya · (4) konsultasi paten untuk protokol antar-perangkat bila ada mitra komersial · (5) rahasia dagang untuk bobot & tabel kalibrasi yang dituning.
- **Aset yang terus bertambah:** jaringan relasi penjaga–terlindungi yang sudah dipasangkan dan saling percaya, plus sinyal deteksi teragregasi.
- *Bukan nasihat hukum; butuh konsultan paten sebelum filing.*

---

# SLIDE 9 — Scalability & Deployment Readiness

**Kriteria juri:** #6 Scalability & deployment readiness

**Headline:** *"Near-serverless today. A clear path to 200,000 families."*

**On-screen:**
- Detection & blocking run **on-device** → cost does not grow with scans
- Alert = **a few hundred bytes**, written only when a risky app appears
- Scaling driver = concurrent guardian connections, not data volume → **FCM + Anonymous Auth** at pilot stage
- Deployment blockers identified, each with a plan

**Visual — tabel tahapan infrastruktur:**

| Stage | Devices | Infrastructure | Monthly cost |
|---|---|---|---|
| PoC (today) | < 10 | RTDB Spark (free) | **Rp0** |
| Pilot | 1,000–20,000 | RTDB Blaze + Anonymous Auth + FCM via Cloud Functions | Low, usage-based |
| Production | 200,000+ | Firestore or sharded RTDB, region `asia-southeast2` (Jakarta), monitoring | Scales with active pairings |

**Visual kedua — roadmap timeline:**

| Phase | When | Scope |
|---|---|---|
| **0 — PoC** | Done | Detection, soft-block, pairing, alerting, guardian response, decoy APKs |
| **1 — Trustworthy MVP** | Oct 2026 – Mar 2027 | Physical low-end devices (Xiaomi/Oppo/Vivo, Android 11–14); 100-app false-positive study; initial scan of existing apps; one guardian ↔ multiple parents; Play Store listing; developer verification |
| **2 — More signals** | Q2–Q3 2027 | Remote-access / screen-share app installed; accessibility enabled for a sideloaded app; new device admin; default SMS app changed — **no new permissions** |
| **3 — Guardian as second approval** | 2027–2028 | With a bank partner: guardian notified of out-of-pattern transfers on an elderly account and can hold one for 30 min |
| **4 — Signal network** | 2028+ | Opt-in, anonymised detection metadata (hashes, certs, permissions, source — no personal data) as early warning for IASC, banks, Kaspersky |

**Speaker notes:**
> RONDA is close to serverless by design. All detection and blocking happens on the phone, so adding a family adds almost no backend load — an alert is a few hundred bytes, written only when something risky appears. What scales is the number of guardians holding a listener open, which is exactly why the pilot stage moves delivery to FCM and locks the database with anonymous auth.
>
> We know our deployment blockers: Play Store review of our two special permissions, developer verification, and OEMs that kill background services. Each has a plan — and the fallback for all three is distribution through a bank or telco partner.

**Performance — target vs status (jujur):**

| Metrik | Target (PRD) | Status sekarang |
|---|---|---|
| Install → verdict | < 2 s di HP 2 GB | Aritmatika in-memory ±23 sinyal; tanpa jaringan, tanpa I/O selain `PackageManager` |
| Deteksi → guardian ternotifikasi | < 10 s | Sub-detik di emulator; **belum diukur di HP fisik lewat data seluler** |
| False positive pada app sah | 0 | Dijaga calibration suite; **studi 100 app = tugas pertama pasca-hackathon** |
| Baterai | Negligible | Deteksi berbasis broadcast, bukan polling; polling `UsageStatsManager` hanya saat ada app yang ditandai |

**Deployment blockers & rencana:**

| Blocker | Rencana |
|---|---|
| Review Play Store untuk `SYSTEM_ALERT_WINDOW` + `PACKAGE_USAGE_STATS` | Justifikasi core functionality + video demo + kepatuhan stalkerware policy. Cadangan: distribusi via operator/bank |
| Developer verification (ID mulai 30 Sep 2026; global 2027) | Registrasi segera — juga prasyarat bicara dengan bank |
| OEM mematikan service (Xiaomi/Oppo/Vivo) | Pengecualian baterai saat setup (**sudah dibangun**, langkah ke-4 wizard); heartbeat "HP Ibu terakhir terlihat 3 hari lalu"; jangka panjang pre-load via operator |
| Persepsi stalkerware | Dijawab proaktif (slide 8) |
| Firebase single point of failure | Deteksi + blok sudah offline; transport diabstraksi agar bisa pindah ke infrastruktur mitra (bank/telco biasanya mensyaratkan ini) |

**Model bisnis (cadangan — atau jadikan slide 9b kalau waktu cukup):**

> **The elderly user never pays.** Anything monetisable on that screen is something a scammer can exploit (*"Pak, bayar dulu biar aman"*). The 1-guardian ↔ 1-parent app stays free forever — it is the distribution engine.

| Model | Payer | Structure | Why they'd pay |
|---|---|---|---|
| **B2B2C licence** (primary) | Banks / e-wallets | Per protected device per year, est. Rp12–24k | **POJK 12/2024** requires every financial institution to run an anti-fraud strategy |
| **Operator bundling** | Telcos | Revenue share in family/elderly plans | Komdigi (Feb & Jul 2026) asked **all** operators to ship anti-scam features "as an application or other system"; Siscamling proves VAS billing rails exist |
| **Cyber-insurance requirement** | Insurers | Per active policy or premium discount | Personal cyber policies already sell (±Rp60–150k/year) and cover social engineering & malware — every prevented claim is margin |
| Freemium "RONDA Keluarga" | Guardian | Rp15–25k/month for up to 5 parents | Validates willingness to pay |
| Threat-intel feed | IASC, banks, vendors | Aggregated, anonymised | Only valuable at tens of thousands of devices |

**Unit economics (satu kalimat kuat):** at an estimated **Rp18k per device per year**, a bank breaks even by preventing **one Rp36-million case per 2,000 protected devices**. *(estimasi, dari STRATEGY.md §6)*

---

# SLIDE 10 — Impact

**Kriteria juri:** #1 Track (social impact), #6

**Headline:** *"Every protected phone is one less family that loses Rp36 million."*

**On-screen:**
- **Who it protects:** ≈ 34 million Indonesians aged 60+, a fast-growing share of them newly online
- **Where the value is:** before the transfer — the < 2% recovery rate means prevention is the only lever
- **Community model:** no tech-savvy child? RT/RW volunteers, Karang Taruna, posyandu cadres guard several neighbours — *ronda*, literally
- **Network effect:** thousands of guarded phones become a distributed early-warning sensor for new APK campaigns

**Visual:**
- Ikon keluarga: 1 penjaga ↔ 1 orang tua → 1 relawan RT ↔ 5–10 lansia → peta Indonesia dengan titik deteksi (Fase 4)
- Atau: "before / after" — *Before: victim decides alone, on the phone with the scammer* vs *After: guardian decides, out of the scammer's reach*

**Speaker notes:**
> The impact we're after is simple: move the decision before the money moves. Recovery doesn't work — under two percent comes back — so every protected phone is measured in losses that never happen.
>
> The obvious question: what about an elderly person without a tech-savvy child? The answer is in the name. Neighbourhood volunteers — RT/RW, Karang Taruna, posyandu cadres — can each guard several neighbours. Security as a shared social practice, not an individual skill.
>
> And at scale, every guarded phone becomes a sensor. Anonymised detections — hashes, certificates, permissions, no personal data — can warn IASC, banks and Kaspersky about a new campaign hours before it reaches a signature database.

**Metrik yang akan kami ukur (bukan klaim):**
- Alert precision (target > 95%) — guardian tidak boleh belajar mengabaikan
- Waktu deteksi → keputusan guardian (target median < 10 menit)
- Retensi aktif 30 hari (target > 80%) — apakah OEM membunuh kami / lansia mencopotnya
- Estimasi kerugian dicegah = alert DARURAT yang berakhir uninstall × Rp36 juta

**Mitra (siapa, kenapa peduli) — cadangan:**
- **Bank/e-wallet** (POJK 12/2024) — mulai dari bank digital (blu, Jenius, SeaBank, Jago), BRI sebagai target bank besar
- **OJK / IASC / Satgas PASTI** — legitimasi & surat dukungan; OJK Regional Jawa Barat (laporan IASC tertinggi) sebagai wilayah pilot
- **Komdigi** — program literasi digital lansia; mandat anti-scam ke operator
- **Operator** — Telkomsel (Siscamling), Indosat (SATSPAM): *"Siscamling guards the network, RONDA guards the phone."*
- **Asuransi siber** — Chubb×DBS, MSIG×Jenius, BCA Insurance
- **Kaspersky** — MoU dengan BSSN diperbarui April 2026 (mencakup inisiatif kesadaran publik); akses reputasi hash; pengenalan ke BSSN

---

# SLIDE 11 — Conclusion & Next 90 Days

**Kriteria juri:** rekap semua (#2 USP diulang)

**Headline:** *"Not a better warning. A second pair of eyes."*

**On-screen — rekap 3 baris:**
- **Scores** the app on-device, in seconds, offline
- **Covers** it before the victim can use it
- **Hands the decision** to someone the scammer cannot reach

**On-screen — 90 hari ke depan (opsional di slide yang sama):**
1. Register as a verified Android developer; test on 2–3 low-end physical phones
2. Run and publish a **100-app false-positive study**
3. Harden backend (Anonymous Auth + locked rules); target Play Store listing Dec 2026
4. **Community pilot, 50–100 elderly users** in Bandung via posyandu lansia
5. Three conversations: **OJK Regional West Java**, one digital bank or insurtech, **Kaspersky**

**Success at day 90:** one institutional letter of support · one paid pilot scheduled · real precision numbers we can quote

**Speaker notes:**
> Sixty-four percent of incidents come from human error — because the decision lands on the person least able to make it. RONDA doesn't try to train that person out of it. It scores the app, covers it, and hands the decision to someone the scammer can't reach.
>
> You've seen it working on two phones. In the next ninety days we take it to real hardware, publish our false-positive numbers, and run a community pilot here in Bandung.
>
> That's *ronda*: neighbours keeping watch so everyone else can sleep. Now it works on your parents' phone.

**"The ask" (kalau mau ditutup dengan permintaan):**
- Introductions: OJK / IASC, a digital bank, Kaspersky's anti-fraud team
- Mentorship on Play Store policy for `SYSTEM_ALERT_WINDOW` + `PACKAGE_USAGE_STATS`

---

# SLIDE 12 — Q&A

**On-screen:**
- **Thank you — Questions?**
- RONDA · Dhanes · Alek · Malik
- github.com/angseleong/RONDA
- (opsional) QR ke repo / video demo

**Catatan tim:** biarkan slide ini sederhana. Siapkan **backup slides** (Lampiran B) di belakang untuk dilompati saat menjawab pertanyaan spesifik.

---

# LAMPIRAN A — Bank Pertanyaan Juri (Q&A prep)

Jawaban dalam bahasa Inggris, ringkas (±20–30 detik). Kolom kiri = pertanyaan yang kemungkinan besar muncul.

**1. "Google already blocks this. Why does RONDA matter?"**
> Google is strengthening the door; RONDA changes who opens it. The 30 September enforcement covers app stores first — WhatsApp-delivered APKs aren't covered until the 2027 global rollout. Verification proves identity, not safety. The next scam uses legitimate apps like AnyDesk that pass every filter. And every Google mechanism still asks the victim. Google doesn't know who your son is.

**2. "Isn't this stalkerware?"**
> No, by design. Pairing requires a code confirmed on the parent's own phone. A permanent notification shows monitoring is active and who the guardian is. We transmit only package metadata — no messages, contacts, location or files. There is no covert mode, and there never will be.

**3. "The overlay can be dismissed with the Home button. So it doesn't really block."**
> Correct, and we say so. It's a delay, not a lock — the overlay is the alarm, the guardian is the defence. A real lock needs Device Owner, which requires a factory reset; that's on the roadmap. We chose not to use an accessibility service for a stronger block, because that's the trojans' own tool.

**4. "Why can't the guardian just uninstall it remotely?"**
> Android doesn't allow any app to silently remove another without Device Owner privileges — by design. So the guardian requests, the parent's phone explains who asked and why, and the parent taps confirm. We tell the guardian it's removed only when the OS confirms it.

**5. "How do you avoid false positives?"**
> Two-axis scoring with diminishing returns. Real WhatsApp from the Play Store scores 45 — silent. A sideloaded courier app scores 55 — silent. Only above 60 do we involve the guardian. Those cases are pinned in unit tests, so a change that makes a safe case drift upward fails the build. A 100-app field study is our first post-hackathon task.

**6. "What if the scammer tells the victim to uninstall RONDA or revoke the permission?"**
> Permissions are re-verified on every launch, and losing capability is itself something to report to the guardian — our roadmap adds a heartbeat so the guardian sees "Mum's phone last seen 3 days ago". Longer term, Phase 3 lets a bank hold a suspicious transfer for the guardian — which doesn't depend on the victim's phone at all.

**7. "What about elderly people without a tech-savvy child?"**
> That's the name: ronda. RT/RW volunteers, Karang Taruna or posyandu cadres can guard several neighbours. Multi-parent support is in Phase 1.

**8. "Why Android only?"**
> The scam is Android-specific, and so is the defence. iOS doesn't let an app see other installed apps, detect installs, read their permissions, or trigger uninstalls.

**9. "Why Firebase RTDB instead of push notifications?"**
> Google retired the FCM legacy server key in June 2024; device-to-device push now needs a paid backend. RTDB gave us no server, no billing and sub-second delivery. The trade-off is the always-on listener; FCM is the upgrade at pilot stage.

**10. "How secure is your own backend?"**
> Honestly: PoC-grade. There's no auth binding a node to a device yet, so a guessed pairing code could read that family's alerts. The fix is Firebase Anonymous Auth with rules checking `auth.uid` — it's our first post-hackathon task. We'd rather tell you than have you find it.

**11. "Is this tested on real phones?"**
> Development and verification ran on Android emulators — Pixel 6, API 33. The logic is proven; endurance under OEM battery management on Xiaomi/Oppo/Vivo is not yet. Physical low-end devices are step one of our 90-day plan. *(Update jawaban ini kalau tim sudah sempat tes di HP fisik sebelum 3 Oktober.)*

**12. "Who pays?"**
> Not the elderly user — ever. Banks, telcos and insurers, who already absorb the loss today. POJK 12/2024 obliges banks to run anti-fraud strategies; Komdigi has asked every operator to ship anti-scam features. At about Rp18k per device per year, preventing one case per 2,000 devices breaks even for a bank.

**13. "How is the score calculated? Isn't it arbitrary?"**
> It's adapted from CVSS: the heaviest capability, plus 40% of the rest, plus bonuses for combinations that express intent — then multiplied by provenance. Every capability maps to a MITRE ATT&CK for Mobile technique, and every verdict emits an audit string. The weights are calibrated against known cases in unit tests.

**14. "What's patentable here?"**
> Not the permission scanner — there's plenty of prior art. The novel part is the system: a device-local verdict routed to a pre-paired human device, local enforcement while awaiting that human's decision, and the decision executed back on the origin device. Near term we'd register copyright and the RONDA trademark, and publish defensively.

**15. "Couldn't malware avoid declaring SMS permission?"**
> To read SMS it must declare it — Android enforces that. Malware that switches to reading notifications instead is covered by the notification-listener signal and its combo. And the engine is signal-based, so new signals — remote-access apps, device admin changes — slot in without new permissions.

**16. "Why not use AI / machine learning?"**
> Explainability. A guardian needs to know *why* — "can read your bank's OTP codes" — not a confidence value. Our model is small, deterministic, offline, auditable, and runs on a Rp1-million phone. ML can be layered on later, e.g. over the aggregated signal network.

**17. "What happens when the parent's phone is offline?"**
> Detection and blocking never touch the network. The alert is queued on disk and delivered on reconnect. Turning off mobile data delays the alert; it doesn't suppress it.

---

# LAMPIRAN B — Backup Slides (taruh setelah slide Q&A)

| # | Judul backup | Isi | Dipakai kalau ditanya |
|---|---|---|---|
| B1 | Scoring formula & worked example | Rumus + hitungan kasus "Senter" 40 + 0.4×10 + 15 = 59 × (1.25×1.15) = 84.8 → **85** | Q13 |
| B2 | Signal catalogue + MITRE mapping | Tabel 16 impact + 7 trust + 5 combo (slide 5 cadangan) | Q13, Q15 |
| B3 | Calibration & decoy results | Tabel 5 kasus kalibrasi + 6 decoy | Q5 |
| B4 | Permission footprint | Tabel "asks for vs refuses" (slide 8) | Q2 |
| B5 | Threat model | Tabel 7 ancaman (slide 8) | Q2, Q6, Q10 |
| B6 | Data model & RTDB decision | Skema 3 node + alasan RTDB vs FCM | Q9, Q10 |
| B7 | Honest limitations | Tabel Lampiran D | Q3, Q4, Q11 |
| B8 | Business model & unit economics | Tabel 5 model + 1:2,000 | Q12 |
| B9 | Competitive landscape (lengkap) | Tabel 7 pemain + kolom "limit" | Q1 |

**Worked example B1 (siap tempel):**
```
"Senter Super Terang" — sideloaded flashlight that reads SMS

Impact:  SMS_READ 40  +  0.4 × INTERNET 10  +  combo SMS_READ+INTERNET 15  = 59
Trust:   SIDELOAD ×1.25  ×  SELF-SIGNED ×1.15                              = 1.4375
Score:   59 × 1.4375 = 84.8  →  85  →  PERINGATAN  →  guardian alerted

Vector:  RONDA:1.0/SMS_READ:40/INTERNET:10/CMB:15/SRC:SIDELOAD/CERT:SELF=85
```

```
Real WhatsApp — Play Store

Impact:  40 + 0.4 × 140 + 15 = 111 → capped 100
Trust:   PLAY ×0.45
Score:   100 × 0.45 = 45  →  RENDAH  →  silent
```

---

# LAMPIRAN C — Cek fakta: dari mana setiap angka

| Angka di slide | Asal | Status |
|---|---|---|
| 64% human error | Kaspersky 2023, dikutip di Guideline HackNusa hlm. 4 | ✅ |
| Rp9.1 T, 432,637 laporan | OJK/IASC (22 Nov 2024 – Jan 2026) [S1] | ✅ dicek ulang 26 Sep 2026 |
| Rp134 M, 3,684 laporan APK | OJK/IASC (Nov 2024 – 15 Okt 2025) [S3] | ✅ dicek ulang 26 Sep 2026 |
| ≈ Rp36.4 jt / laporan | Hitungan kami 134 M ÷ 3,684 | ⚠️ tandai "est." di slide |
| < 2% kembali (Rp161 M) | Siaran pers OJK [S4] | ✅ |
| 11.97% lansia, ≈ 34 jt | BPS 2025 [S6]; "34 jt" = estimasi dari ±284 jt penduduk | ⚠️ "≈" |
| 34.13% lansia online, +7.7 poin | BPS via Dataloka [S7] | ✅ |
| 30 Sep 2026 verifikasi, app store dulu, global 2027 | Android Authority, The Hacker News, Android Developers Blog | ✅ dicek ulang 26 Sep 2026 |
| 23 signals, 5 combos, bobot | `core/Signal.kt` | ✅ kode |
| Skor 45 / 14 / 55 / 85 / 100 | `RiskEvaluatorTest.kt` | ✅ test |
| Fixture 85 / 100 / 83 / 45 / 14 | `FakeGuardianRepository.kt` (dihitung lewat RiskEvaluator asli) | ✅ kode |
| Decoy 85 / 100 / 70 / 88 / 63 / 43 | Manifest flavor + bobot | ⚠️ cek di emulator |
| 54 file, ≈ 8,380 baris, 30 test, 44 commit | Dihitung 26 Sep 2026 | ✅ (hitung ulang sebelum final) |
| Rp18 rb/perangkat/tahun, 1 : 2,000 | STRATEGY.md §6 | ⚠️ estimasi, sebut "est." |
| ±700 ms polling | `ForegroundAppMonitor.POLL_ESTIMATE_MS` | ✅ kode (estimasi log) |

**Koreksi kecil terhadap PROJECT_REPORT.md** (supaya slide tidak mewarisi):
- Report menulis *"a 16-case calibration table"* — yang benar: **16 test scoring engine, 5 di antaranya calibration table**, sisanya edge case & plumbing.
- Report menulis **41 commits** — sekarang **44**.

---

# LAMPIRAN D — Yang TIDAK boleh diklaim

Beberapa dokumen (terutama `docs/USE_CASES.md`) menggambarkan fitur yang **belum ada di kode**. Jangan tampilkan sebagai fitur jadi:

| Jangan klaim | Status sebenarnya |
|---|---|
| Initial scan app yang sudah terpasang | **Backlog** (Block 4B) — belum diimplementasi. Sebut sebagai roadmap Fase 1. |
| Satu guardian memantau banyak orang tua (multi-Rondee) | **Backlog** (Block 4C). Roadmap Fase 1. |
| Notifikasi via FCM | Diganti RTDB listener. Jangan sebut "FCM push". |
| Tombol "Lanjutkan Saja" / override di overlay | Overlay **tidak punya** tombol lanjut. Data `overrodeAt` hanya ada di fixture demo. |
| Hard-block / app benar-benar dibekukan | Butuh Device Owner — roadmap. |
| Uninstall diam-diam | Tidak mungkin di Android tanpa Device Owner. |
| Diuji di HP fisik | Belum (per 26 Sep) — emulator Pixel 6 / API 33. Update kalau sudah. |
| Akurasi %, latency di HP fisik, jumlah pengguna, testimoni | **Tidak ada datanya.** Jangan dibuat-buat. |
| Backend aman / production-ready | Rules RTDB masih PoC-grade — akui di slide 8. |
| "Patented" / "patent pending" | Belum ada filing apa pun. Sebut "patentability potential". |

---

# LAMPIRAN E — Sumber

**Skala masalah**
- [S1] TIMES Indonesia — data IASC Rp9,1 T. https://jogja.times.co.id/news/kriminal/zHU1rbTfC/penipuan-whatsapp-meningkat-kerugian-capai-rp91-triliun-pakar-ugm-ingatkan-bahaya-file-apk
- [S2] Kompas (2 Jul 2026) — 579.459 laporan IASC, lansia korban scam. https://nasional.kompas.com/read/2026/07/02/20330831/komdigi-sebut-banyak-lansia-jadi-korban-scam-ai-yang-tiru-suara-pejabat
- [S3] Radar Surabaya / Jawa Pos — APK via WhatsApp 3.684 laporan, Rp134 M. https://radarsurabaya.jawapos.com/ekonomi/776724733/kerugian-akibat-scam-digital-di-indonesia-tembus-rp7-triliun-ojk-ungkap-10-modus-utama
- [S4] OJK — IASC kembalikan Rp161 M. https://ojk.go.id/id/berita-dan-kegiatan/siaran-pers/Pages/IASC-Berhasil-Kembalikan-Rp161-Miliar-Dana-Masyarakat-Korban-Scam.aspx
- [S6] BPS — Statistik Penduduk Lanjut Usia 2025. https://www.bps.go.id/id/publication/2025/12/12/868d335b088dcddc3ddee052/statistik-penduduk-lanjut-usia-2025.html
- [S7] Dataloka (mengutip BPS) — lansia akses internet 2025. https://dataloka.id/humaniora/5794/persentase-penduduk-lansia-yang-mengakses-internet-2025-terus-meningkat-dalam-6-tahun-terakhir/

**Platform & Google**
- Android Authority — timeline sideloading changes. https://www.androidauthority.com/android-sideloading-changes-timeline-3679204/
- The Hacker News (Jun 2026) — Sept 30 deadline in four countries. https://thehackernews.com/2026/06/google-sets-sept-30-deadline-for.html
- Android Developers Blog (Jun 2026) — developer verification. https://android-developers.googleblog.com/2026/06/android-developer-verification.html
- Kaspersky — Android threats 2026 (kutipan bypass verifikasi). https://www.kaspersky.com/blog/growing-2026-android-threats-and-protection/55191/
- Kompas — Enhanced Fraud Protection Indonesia (Feb 2025). https://amp.kompas.com/tren/read/2025/02/19/130000665/google-rilis-fitur-enhanced-fraud-protection-di-indonesia-cegah-penipuan
- MITRE ATT&CK for Mobile. https://attack.mitre.org/matrices/mobile/

**Regulasi, bank, operator, asuransi, pembanding** — lihat daftar lengkap di [STRATEGY.md §10](STRATEGY.md) (POJK 12/2024, POJK 22/2023, Komdigi–operator, Siscamling, SATSPAM, Chubb×DBS, MSIG×Jenius, Kaspersky–BSSN MoU, ScamShield, Seraph Secure, Scammer Guardian).

**Internal (repo)**
- Guideline HackNusa 2026 (PDF di root repo) — track brief hlm. 4, kriteria juri FAQ #15, bahasa Inggris FAQ #7
- [PROJECT_REPORT.md](PROJECT_REPORT.md), [PRD.md](PRD.md), [ARCHITECTURE.md](ARCHITECTURE.md), [STRATEGY.md](STRATEGY.md), [finalscript.md](finalscript.md), [TODO.md](TODO.md)
