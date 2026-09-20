# RONDA — Naskah Final Video

**Track:** Human-Centric Security · **Target:** 2:57 · **Batas:** 3:00
**Tim:** Dhanes, Alek, Malik

- Arahan shot berbahasa Indonesia. **Kalimat di blok kutip `>` diucapkan dalam
  bahasa Inggris** — jangan diterjemahkan.
- Teks di layar ditulis `seperti ini`.
- **434 kata terucap** ≈ 2:48 pada 155 kata/menit, ditambah ±9 detik jeda visual
  yang ditahan = **±2:57**. Margin **3 detik** — nyaris tidak ada. Kalau take
  pertama melar sedikit saja, langsung pakai daftar potongan di bagian bawah.

## Istilah: victim & guardian

Naskah ini sudah dipindah dari framing spesifik ("ibu" / "anaknya") ke istilah
umum: **victim** (lansia yang HP-nya dilindungi) dan **guardian** (orang
terdekat yang mengambil keputusan). Dua aturan supaya konsisten:

1. **§1, §2, §3, §5, §6 tetap generik** — pakai `the victim` / `the guardian`,
   bukan `she`/`he`/`her`/`his`.
2. **§4 (demo) satu-satunya tempat yang boleh konkret.** Dibuka dengan satu
   kalimat jembatan (`For this demo, the victim is someone's mother...`), baru
   sesudah itu narasi boleh pakai "her"/"his" karena rujukannya sudah jelas.
   Ini juga jawaban langsung untuk permintaan: demo dicontohkan sebagai emak dan
   anaknya, tapi definisi umum di bagian lain tetap victim/guardian.

---

# NASKAH

## §1 · HOOK — Dhanes — 0:00–0:17
### ▸ Kriteria 1 — Problem Statement (1 dari 2)

**Shot** Cold open, tanpa logo. Rekaman layar penuh chat WhatsApp. Berkas masuk:
`Undangan Pernikahan.apk`. Kursor melayang di atasnya. Freeze.
Caption pojok: `Ilustrasi skenario`. Grade sedikit lebih redup dari bagian demo.

**Di layar** `64% of cyber incidents come from human error` · `— Kaspersky, 2023`
(sepertiga bawah, detik ke-11)

> This is a wedding invitation.
>
> It is also how Indonesian families lose their savings.
>
> The victims are usually elderly people who don't quite understand technology.
>
> They didn't click a phishing link. They installed an app — because someone
> they trusted asked them to.

---

## §2 · MASALAH — Dhanes — 0:17–0:35
### ▸ Kriteria 1 — Problem Statement (2 dari 2)

**Shot** Potong ke Dhanes, menghadap kamera, latar polos. Tahan di kalimat
terakhir sebelum potong.

> The usual answer is training: teach people to spot the scam.
>
> But you cannot train your way out of this. The scammer is on the phone with
> the victim *right now*.
>
> The decision sits with the person least able to make it, at the worst possible
> moment.

---

## §3 · SOLUSI — Malik — 0:35–1:07
### ▸ Kriteria 2 — Proposed Solution & Unique Selling Proposition

*Catatan produksi, tidak diucapkan: dua kalimat terakhir di sini adalah USP
utama RONDA — memindahkan keputusan, bukan melatih korban. Ini yang membedakan
dari peserta lain di track yang sama.*

**Shot** Title card: logo RONDA (wordmark), lalu potong ke Malik.

**Di layar** `RONDA — Pelindung Keluarga`
**Caption 4 detik**, muncul saat Malik menyebut nama track:
`Human-Centric Security — "UX design to make secure choices more intuitive and
accessible for everyone"`

> We chose the Human-Centric Security track, because this isn't a story about
> weak technology. The victim's phone was fine — the attack went through the
> person holding it.
>
> So we stopped trying to make the victim an expert. We move the decision to
> someone closer, who understands tech better: the guardian.
>
> This is RONDA — the victim's phone and the guardian's, paired. A dangerous app
> lands on it, RONDA covers it with a warning, and notifies the guardian.

---

## §4 · DEMONSTRASI — Dhanes — 1:07–2:04
### ▸ Kriteria 3 — Demonstration

**Shot 4a** Dua jendela emulator berdampingan, satu take menyambung.
Label menempel **sepanjang bagian ini**:
`Emulator 1 — HP Ibu (70 th)` dan `Emulator 2 — HP anaknya (penjaga)`.
Caption 2 detik: `Sudah dipasangkan — kode 6 huruf, dibacakan lewat telepon`.
Caption kedua: `"Ibu" = Mum`.

> For this demo, the victim is someone's mother. The guardian is her son.
>
> Two Android emulators, live. Left, her phone. Right, his.

**Shot 4b** Jalankan `scripts/ronda attack com.whatsapp`.
Caption kecil 2 detik: `Instalasi diatribusikan ke WhatsApp — adb -i`

> I install our test app on her phone. Harmless — one permission, nothing else.
> We never use real malware; we simulate delivery, not detection.

**Shot 4c** Kartu skor muncul di emulator penjaga. Zoom pelan.
**Tahan 3 detik penuh** — tiap baris harus terbaca.

**Di layar** (baca dari layar sungguhan, jangan dari naskah ini)
`85 / 100 — PERINGATAN` · `PENCURIAN OTP` · `BUKAN PLAY STORE`
· `TANDA TANGAN TIDAK RESMI`

> RONDA scores it. Not a verdict — a number out of a hundred.
>
> Eighty-five. What it can do, where it came from, and who signed it.
>
> Above sixty, RONDA wakes the guardian. Below that, it logs it and stays quiet.
>
> Watch both phones.

**Shot 4d** Emulator ibu membuka aplikasinya → overlay merah menutupi.
Emulator anaknya menampilkan alert. **Tahan 2 detik penuh — frame terpenting.**
Emulator tidak bergetar: tegaskan dengan hentakan zoom halus ke jendela penjaga.
**Jangan** menambahkan efek suara getaran.

> Covered offline, in under a second. Her son has the reasons, not just the
> score.

**Shot 4e** Tombol `Tandai berbahaya` → `Minta Ibu menghapus aplikasi ini`
ditekan di emulator penjaga. Emulator ibu menampilkan layar konfirmasi.
Konfirmasi → aplikasi hilang → overlay bersih → layar penjaga ter-update ke
`Riwayat`.

> The guardian decides: uninstall. She confirms — Android requires it. No app
> can delete another silently.
>
> Gone. And he sees it.

---

## §5 · JALAN KE MVP — Malik — 2:04–2:28
### ▸ Kriteria 4 — Pathway

*Catatan produksi, tidak diucapkan: kalimat terakhir di sini adalah USP kedua —
sinyal deteksi dari banyak pasangan keluarga bisa jadi jaringan yang berguna
untuk institusi mendeteksi pola serangan baru (zero-day) lebih awal daripada
satu titik deteksi sendirian bisa lakukan.*

**Shot** Malik menghadap kamera, framing sama persis seperti §3. **Tanpa
grafis.** Tiga caption teks muncul bergantian di sepertiga bawah, satu per
kalimat — teks putih di atas bar gelap transparan, tidak perlu ilustrasi apa
pun:

1. `Stock Android · minSdk 30 · tanpa root, tanpa server`
2. `Tanpa accessibility service · tanpa izin SMS`
3. `Berikutnya: HP fisik + kerja sama institusi`

> It runs today on a stock Android image. No root, no server, no cloud model.
>
> One thing we will never add: RONDA uses no accessibility service and no SMS
> permission. That is the trojans' own toolkit.
>
> This ran on emulators — the logic is proven, not the endurance. Next: detection engine upgrade, and partnering with institutions to catch zero-day scam patterns
> before they spread.

---

## §6 · PENUTUP — Alek — 2:28–2:57
### ▸ Kriteria 5 — Wrap-Up

**Shot** Kembali ke kamera. Pada kalimat ketiga, dorongan pelan ke dua jendela
emulator berdampingan. Potong ke hitam, logo.

**Di layar** `RONDA — Pelindung Keluarga`

> Sixty-four percent of incidents come from human error — because the decision
> lands on the person least able to make it.
>
> RONDA doesn't train the victim out of that. It scores the app, covers it, and
> hands the decision to the guardian.
>
> You just watched it happen. Not a better warning — a second pair of eyes.
>
> That's *ronda*: neighbours keeping watch so everyone else can sleep. Now it
> works on your parents' phone.

---

## Kalau kepanjangan

Margin 3 detik itu nyaris tidak ada. Kalau take pertama lewat 3:00, potong
berurutan:

1. `and partnering with institutions to catch zero-day scam patterns before they
   spread` (§5) → ganti jadi `Next: real hardware.` — hemat ~9 detik, tapi ini
   USP kedua, jadi cek dulu apakah masih bisa masuk lewat caption di layar
   sebelum dibuang total dari narasi
2. `She confirms — Android requires it. No app can delete another silently.`
   (§4e) — hemat ~6 detik, ganti caption
   `Android mewajibkan konfirmasi pengguna` supaya pengakuan batasannya tidak
   hilang sepenuhnya
3. `The victim's phone was fine — the attack went through the person holding
   it.` (§3) — hemat ~5 detik

**Jangan pernah dipotong:** dua kalimat pengenal victim di §1, kalimat jembatan
`For this demo, the victim is someone's mother...` di §4a, tahan 3 detik di
kartu skor, tahan 2 detik saat overlay muncul, dan kalimat `We are those
guardians` di §3 — itu satu-satunya baris yang mengunci kenapa tiga mahasiswa
cowok pantas bicara soal victim lansia (lihat `VIDEO_SCRIPT.md` bagian pemeran).
