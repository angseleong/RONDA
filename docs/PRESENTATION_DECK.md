# RONDA — Presentation Deck Material (Final Pitching)

**Status:** Draft v2 · 27 September 2026 · raw material for the team to filter before it goes into the PPT
**Event:** HackNusa 2026 Final Project Pitching — Telkom University, Bandung, 3 October 2026
**Final format:** `.pptx` / `.pdf`, 10–15 slides, **12 minutes in total (pitch + Q&A)**
**Sources:** the code in `app/` + `RondaTestSample/`, [PROJECT_REPORT.md](PROJECT_REPORT.md), [PRD.md](PRD.md), [ARCHITECTURE.md](ARCHITECTURE.md), [STRATEGY.md](STRATEGY.md), [USE_CASES.md](USE_CASES.md), [finalscript.md](finalscript.md), the HackNusa 2026 Guideline, and a fresh web check (26 Sep 2026)

---

## How to use this document

- **Language.** Guideline FAQ #7: *"The final pitching session and presentations during the onsite event will also be conducted in English."* All slide text, speaker notes and team notes are in English. Text quoted from RONDA's own screens stays in Indonesian — that is what the audience will see in the demo — with an English gloss in brackets.
- **Deliberately too much material.** Every slide has the same structure:
  - **Judging criteria** this slide goes after (from the 6 official criteria, see the table below)
  - **Headline** — the slide title
  - **On-screen** — the text shown on the slide. Take 3–5 points only.
  - **Visual** — suggested image/diagram/screenshot
  - **Speaker notes** — what is said
  - **Backup** — extra material if there is room, or for Q&A
  - **Evidence in the repo**
- **The numbers rule.** Every figure here has a source (web or code). **Do not add field metrics** (accuracy, latency on physical phones, user counts) — they do not exist yet, and PROJECT_REPORT already promises not to claim them. See [Appendix D — What must NOT be claimed](#appendix-d--what-must-not-be-claimed).

### Six judging criteria → slides

From the HackNusa 2026 Guideline, FAQ #15: *"Projects will be evaluated based on six criteria."*

| # | Official criterion | Main slide | Supporting slides |
|---|---|---|---|
| 1 | Accordance with the track | 2 | 3, 10 |
| 2 | Unique selling proposition (USP) | 4 | 3, 11 |
| 3 | Technical feasibility | 7 | 5, 6, 9 |
| 4 | Proof of concept (PoC) | 5, 6 | 7 |
| 5 | Level of security and patentability | 8 | 7 |
| 6 | Scalability and deployment readiness | 9 | 10, 11 |

### Time budget (proposed)

12 minutes covers the pitch **and** Q&A. Proposal: **about 7 minutes of pitch, about 5 minutes of Q&A.**

| Slide | Content | Speaker (proposed, following finalscript) | Duration |
|---|---|---|---|
| 1 | Title | Dhanes | 0:15 |
| 2 | Problem & track | Dhanes | 1:00 |
| 3 | Solution overview | Malik | 0:40 |
| 4 | USP | Malik | 0:50 |
| 5–6 | PoC / live demo | Dhanes | 2:00 |
| 7 | Architecture & stack | Alek | 0:40 |
| 8 | Security framework | Alek | 0:40 |
| 9 | Scalability & deployment | Malik | 0:30 |
| 10–11 | Impact & conclusion | Alek | 0:25 |
| 12 | Q&A | everyone | about 5:00 |

> **Team note:** if the demo is live, slides 5–6 can be the backdrop shown before and after it. Prepare a **backup demo video** (PRD R3) in case the emulators or phones misbehave on stage.

---

# SLIDE 1 — Title, Team Name, Tagline

**Judging criteria:** — (first impression)

**Headline:** RONDA

**On-screen:**
- **RONDA** — Real-time On-Device Detection Agent
- Tagline (pick one):
  - *"Not a better warning. A second pair of eyes."* ← **recommended** (also the closing line of the video)
  - *"Everyone else warns the victim. RONDA tells somebody else."*
  - *"Family Protector — family-guarded protection against APK scams"*
- Team: **Dhanes · Axeleon (Alek) · Malik**
- Track: **Human-Centric Security**
- HackNusa 2026 — Telkom University × Kaspersky

**Visual:**
- RONDA logo/wordmark (`app/src/main/res/drawable-nodpi/ronda_wordmark.png`, `ronda_mark.png`) — or record the app's animated splash (the mark pops in and slides left as the name appears)
- Brand colours: forest green `#1D7F4E` (safe), danger red `#DC2626`, trust blue `#1E40AF`; the Nunito typeface (see `docs/DESIGN.md`)
- Optional: two phone silhouettes side by side (the parent's phone ↔ the guardian's phone) joined by one line

**Speaker notes:**
> Good afternoon. We are RONDA — Dhanes, Alek and Malik. In Indonesia, *ronda* is the practice of neighbours taking turns keeping watch at night so everyone else can sleep. We built that idea into an Android app: when a scam app lands on your parent's phone, someone who is not being manipulated gets to decide what happens next.

**Backup:**
- The name: *ronda* = the neighbourhood night watch — security as a shared social responsibility, not an individual technical skill. A direct bridge to "Human-Centric".
- Repo: https://github.com/angseleong/RONDA

---

# SLIDE 2 — Problem Statement & Track Alignment

**Judging criteria:** #1 Accordance with the track

**Headline (pick one):**
- *"The attack doesn't break the phone. It goes through the person holding it."*
- *"Indonesia's APK scam: no exploit, just trust."*

**On-screen (short version):**
- A WhatsApp file called `Undangan Pernikahan.apk` [Wedding Invitation.apk] → installed → SMS permission granted → banking OTP forwarded → account drained
- **Rp134 billion** lost to APK-via-WhatsApp scams · **3,684 reports** · **≈ Rp36 million per victim** (OJK/IASC)
- **< 2%** of scam losses ever recovered (IASC)
- **64%** of cyber incidents come from human error (Kaspersky, 2023)
- The warning goes to **the one person the scammer is already talking to**

**Visual:**
- Left: a WhatsApp chat mockup containing `Undangan Pernikahan.apk` (small caption "Illustrative scenario")
- Right: the 5-step attack chain as arrows:
  `Contact on WhatsApp → APK sent → Victim installs → Grants SMS → OTP stolen → Account drained`
- Bottom: three big numbers `Rp36M / victim` · `<2% recovered` · `64% human error`

**Speaker notes:**
> Here is a scam that is routine in Indonesia. A stranger on WhatsApp poses as a courier, a bank officer, or a wedding guest. They send a file — a wedding invitation, a delivery receipt. They stay on the phone and walk the victim, usually an elderly parent, through installing it and granting SMS permission. The app forwards the bank's OTP codes. The account is drained.
>
> No vulnerability is exploited. The victim installs it. The victim grants the permission. From the phone's point of view, nothing abnormal happened.
>
> Indonesia's Anti-Scam Centre recorded 134 billion rupiah lost to this one method — about 36 million per report — and less than two percent of scam losses ever come back.
>
> The industry's answer is a warning on the victim's screen. But the scammer's script already says: "a warning will appear, that's normal, just tap continue." The decision lands on the person least able to make it, at the worst possible moment.

**Track alignment (could be half a slide of its own, or slide 2b):**

- Track brief (Guideline p. 4): *"Technology alone is not sufficient… 64% of cyber incidents … were caused by human error."* Challenge: *"design a system that changes or influences user behavior to reduce and manage cybersecurity risks."* The areas listed include *"UX solutions that make secure behavior easier and more intuitive."*
- **RONDA's reading of the brief:** most entries try to make the victim a better decision-maker (training, gamification, clearer warnings). That approach has a ceiling this attack sits above — you cannot design a warning good enough to beat a human coaching the victim past it *in real time*.
- **So RONDA changes the behaviour of the *system*, not the victim:** it moves the decision to a second human — a guardian — who is not under the scammer's influence.
- Human-centric on both sides:
  - Protected phone: zero security decisions, ≥20 sp text, one action per screen, plain Indonesian
  - Guardian phone: evidence in consequence-language, not jargon ("can read the bank's OTP codes", not "declares READ_SMS")

**Alternative on-screen text for the track part:**
> Track challenge: *"change or influence user behavior to reduce cybersecurity risk"*
> RONDA's answer: **don't retrain the victim — change who decides.**

**Backup (extra figures):**

| Fact | Figure | Source |
|---|---|---|
| Digital-scam losses reported to IASC | **Rp9.1 trillion**, 432,637 reports (22 Nov 2024 – Jan 2026) | OJK/IASC via TIMES Indonesia [S1] |
| APKs over WhatsApp/Telegram | **3,684 reports, Rp134 billion** (Nov 2024 – 15 Oct 2025), a top-10 method | OJK via Radar Surabaya/Jawa Pos [S3] |
| Average loss per APK report | **≈ Rp36.4 million** (our calculation: 134 billion ÷ 3,684) | derived |
| Funds recovered by IASC | **Rp161 billion — < 2%** of losses | OJK press release [S4] |
| Funds blocked by IASC | Rp436.88 billion (to Jan 2026) | OJK/IASC [S1] |
| IASC reports to May 2026 | 579,459; West Java highest (119,750) | Kompas [S2] |
| Elderly population (60+) | **11.97% (≈ 34 million)** — an ageing population | BPS 2025 [S6] |
| Elderly with a phone / online | 52.23% / **34.13%**, up 7.7 points in a year | BPS via Dataloka [S7] |

**Three conclusions from the numbers (could be voice-over):**
1. **Recovery doesn't work** — all the value is in the minutes *before* the transfer, i.e. between install and permission grant. That is exactly RONDA's window.
2. **Reported numbers are a floor** — elderly victims often don't report, are ashamed, or never connect the "invitation" to the missing money.
3. **The exposed population grows on two axes** — more elderly people, and a faster-growing share of them online.

**Why warnings fail (3 reasons, from PROJECT_REPORT §1.2):**
1. The warning arrives after 20 minutes of trust-building with the "officer".
2. Scammers pre-script it: *"a warning will pop up, just tap continue."*
3. Signature detection lags — the APK is repackaged for every campaign.

---

# SLIDE 3 — Proposed Solution Overview

**Judging criteria:** #1 Track, #2 USP (introduction)

**Headline:** *"One app, two phones, one decision moved to the right person."*

**On-screen:**
- **One APK, two roles** chosen during setup:
  - **Protected phone** (the parent) — detects and blocks. Never asked to make a security judgement.
  - **Guardian phone** (the adult child / relative) — receives the alert, sees the reasons, decides.
- **5 steps:** DETECT → BLOCK → ALERT → DECIDE → ACT
- Detection reads **declared** permissions at install time — **before the victim grants anything**

**Visual — the 5-step flow (the core of the slide):**

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

**Backup:**
- Pairing: the guardian shows a **6-character code + QR** (`ronda://pair/CODE`) with a live 10-minute countdown; the parent types the code or scans the QR. Designed for the realistic case — **a phone call to a parent in another city**. The code alphabet excludes O/0, I/1, S/5, B/8 so it survives being read aloud.
- Right after pairing, an **initial scan** checks every app already installed, so an APK that arrived before RONDA does not slip through.
- One guardian can watch **several protected phones** (e.g. both parents, or a volunteer guarding several neighbours).
- UI language: Indonesian (default) and English, chosen during onboarding.
- Soft-block ≠ lock: "the overlay is the alarm, the guardian is the defence".

---

# SLIDE 4 — Unique Selling Proposition (USP)

**Judging criteria:** #2 USP

**Headline:** *"Everyone else warns the victim. RONDA is the only one that tells somebody else."*

**On-screen:**
- **USP 1 — Guardian-mediated intervention.** The decision leaves the handset the scammer is coaching.
- **USP 2 — Scores, not verdicts.** 0–100 with named reasons, so quiet apps stay quiet and loud ones stay credible.
- **USP 3 — Consequence language.** "This app can read your bank's OTP codes" — not "declares READ_SMS".
- **USP 4 — Minimal privilege.** No SMS permission, no Accessibility Service — RONDA never uses the trojans' own toolkit.

**Visual — a competitor table with one key column, "Who decides?":**

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

**Positioning against Google 2026–2027 (prepare this — the sharpest question the judges will ask):**

Backup headline: ***"Google is strengthening the door. RONDA changes who opens it."***

1. **The window is open now.** The 30 Sep 2026 enforcement in Indonesia (with Brazil, Singapore, Thailand) first covers apps distributed through app stores (Play, Galaxy Store, GetApps, OPPO, vivo, etc.). The global expansion to all apps on certified devices is **2027**. *(re-confirmed 26 Sep 2026 — Android Authority, The Hacker News, Android Developers Blog)*
2. **Verified ≠ safe.** Verification binds identity, not behaviour. Kaspersky itself: *"attackers will likely find ways to bypass verification."* RONDA reads what an app **can do**, not who signed it.
3. **The next scam needs no malicious APK.** "Share screen" scams use AnyDesk/TeamViewer — legitimate, verified, on the Play Store. RONDA can treat "a remote-access app just appeared on Mum's phone" as a signal for a human to judge (roadmap Phase 2).
4. **Google will always ask the victim.** Even the advanced flow asks *"is someone pressuring you?"* — of the person being pressured. Google doesn't know who your son is. RONDA does.

**Backup:**
- Patent/IP (criterion #5) — what is genuinely new is not the permission scanner (plenty of prior art) but the **guardian-mediated intervention architecture**: a local verdict → sent to a pre-paired second human's device → local block while waiting → the decision executed back on the origin device. Details on slide 8.

---

# SLIDE 5 — Proof of Concept (1/2): Detection, Scoring & Guardian Alert

**Judging criteria:** #4 PoC, #3 Technical feasibility

**Headline:** *"Working end to end: install on one phone, the other phone alerts."*

**On-screen:**
- **Risk scoring engine** — 23 signals (16 capability + 7 trust), 5 intent combinations, each capability mapped to **MITRE ATT&CK for Mobile**
- **Plain-language explanations** — 23 curated Indonesian sentences + 5 combination sentences
- **Real-time guardian alert** — score, band and reasons on the guardian's phone, with a sound that tells an emergency from a warning
- Calibrated on real cases (below) — **WhatsApp from the Play Store stays quiet; a sideloaded SMS reader alerts**

**Visual — a table of real results (not a mockup; numbers from unit tests + fixture + decoys):**

| App (scenario) | Signals | Score | Band | Guardian? |
|---|---|---|---|---|
| Real WhatsApp, Play Store | SMS, contacts, camera, mic, location… + Play | **45** | RENDAH [low] | Silent |
| Sideloaded game, internet only | Internet + sideload + self-signed | **14** | AMAN [safe] | Silent |
| Sideloaded courier app | Contacts, location, phone state, internet | **55** | RENDAH | Silent |
| "Senter" flashlight that reads SMS | SMS + internet + sideload + self-signed | **85** | PERINGATAN [warning] | **Alert** |
| Banking trojan pattern | Accessibility + SMS + overlay + installer + notif… | **100** | DARURAT [emergency] | **Alert** |

Bands: **AMAN** 0–29 · **RENDAH** 30–59 · **PERINGATAN** 60–89 · **DARURAT** 90–100 · guardian threshold **60**

**Screenshots to capture (shot list):**
1. The score card on the guardian's `AlertDetailScreen`: `85 / 100 — PERINGATAN`, with the eyebrows `PENCURIAN OTP` [OTP THEFT] · `BUKAN PLAY STORE` [NOT PLAY STORE] · `TANDA TANGAN TIDAK RESMI` [UNOFFICIAL SIGNATURE]
2. The high-priority notification on the guardian's phone, with the RONDA mark in the status bar: "BAHAYA: aplikasi mencurigakan dipasang" [DANGER: suspicious app installed]
3. `GuardianHomeScreen`, Alerts tab — the scored list (run `scripts/ronda fixture` → the `DEMO01` demo pairing, a 5-app fixture: Senter Super Terang 85, Info BCA Mobile 100, Cek Resi Kilat 83, WhatsApp 45, Sudoku Offline 14)
4. `ExplanationStack` — a consequence sentence ("Aplikasi ini bisa membaca SMS Ibu dan mengirimnya ke internet. Ini pola yang dipakai untuk mencuri kode OTP bank." [This app can read Mum's SMS and send it to the internet. This is the pattern used to steal bank OTP codes.])

**Speaker notes:**
> This is the scoring engine at work. RONDA reads 23 signals through Android's PackageManager — what the app can do, like reading SMS or controlling the screen, and where it came from — Play Store, sideloaded, self-signed. Combinations carry intent: reading SMS *plus* internet access is the complete OTP-theft path, so it scores higher than either alone.
>
> Look at the first row. Real WhatsApp from the Play Store requests SMS, contacts, camera, microphone — and scores 45. Silent. A sideloaded flashlight that reads SMS scores 85, and the guardian is woken up. That difference is the whole point: a system that alerts on everything is worse than no system, because guardians learn to swipe it away.
>
> And the guardian doesn't get "declares READ_SMS". They get: "This app can read your mother's SMS and send it to the internet. This is the pattern used to steal bank OTP codes."

**Backup — engine technical detail (could move to slide 7 or an appendix):**

```
impact = heaviest signal + 0.4 × Σ(other signals) + combo bonuses   → cap 100
trust  = product of trust multipliers                               → clamp 0.45 … 1.6
score  = min(100, round(impact × trust))
```

- The structure is adapted from **CVSS** (an impact term × a provenance term) → auditable, explainable, not a black-box model.
- The **0.4 diminishing-returns** factor is a load-bearing design choice: naive addition would push a legitimate app with many permissions straight to 100.
- Every verdict emits a **vector string** for logs and audit; a real example from the tests:
  `RONDA:1.0/SMS_READ:40/INTERNET:10/CMB:15/SRC:SIDELOAD/CERT:SELF=85`

**Signals & weights (from `core/Signal.kt`):**

| Impact signal | Weight | MITRE ATT&CK Mobile |
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

**Evidence in the repo:** `app/src/main/java/com/ronda/app/core/RiskEvaluator.kt`, `core/Signal.kt`, `detect/SignalExtractor.kt`, `app/src/test/.../RiskEvaluatorTest.kt`, `ui/guardian/FakeGuardianRepository.kt`, `res/values-in/strings_capabilities.xml`

---

# SLIDE 6 — Proof of Concept (2/2): Block, Decide, Act

**Judging criteria:** #4 PoC

**Headline:** *"Blocked offline in under a second. Removed on the guardian's word."*

**On-screen:**
- **Offline soft-block** — a full-screen RONDA overlay covers the flagged app every time it opens; no "continue anyway" button; works with the network off, and comes back by itself after a restart
- **Guardian decision** — *Request uninstall* or *Mark safe* (confirmed, and undoable for 10 s — undo re-covers the app on the parent's phone)
- **Remote uninstall request** — the parent's phone explains who asked and why, then opens Android's uninstall dialog; the guardian is told only when the OS confirms removal
- **Pairing** — 6-character code or QR deep link, no camera permission
- **6 harmless decoy APKs** — we never use real malware

**Visual — screenshots side by side (left the parent's phone, right the guardian's phone):**
1. RONDA's red overlay on the parent's phone: *"Jangan buka aplikasi ini"* [Don't open this app] · *"Penjaga Anda sudah diberi tahu"* [Your guardian has been told] · *"Tekan tombol Home untuk keluar"* [Press Home to leave]
2. `AlertDetailScreen` → the `Minta Ibu menghapus aplikasi ini` [Ask Mum to remove this app] button
3. `UninstallPromptScreen` on the parent's phone (large text, one decision)
4. Android's system uninstall dialog
5. The guardian's phone: the `Riwayat` [History] tab — "Aplikasi berbahaya sudah dihapus" [The dangerous app has been removed]
6. Pairing: `GuardianPairingScreen` (big code + QR + countdown) and `ProtectedPairingScreen`
7. Onboarding: splash → language → intro → role; the 4-step permission wizard on the parent's phone

**Decoy APK table (`RondaTestSample/`, 6 flavors, each empty and doing nothing):**

| Flavor | Shown as | Declares | Score | Result |
|---|---|---|---|---|
| `sms` | Undangan Pernikahan [Wedding Invitation] | READ_SMS + INTERNET | **85** | PERINGATAN → alert |
| `accessibility` | Update Sistem [System Update] | Accessibility service + overlay | **100** | DARURAT → alert |
| `notification` | Cek Resi Kilat [Quick Parcel Tracking] | Notification listener + internet | **70** | PERINGATAN → alert |
| `deviceadmin` | Layanan Keamanan [Security Service] | Device admin + hidden icon | **88** | PERINGATAN → alert |
| `dropper` | Info Paket [Package Info] | Install packages + internet | **63** | PERINGATAN → alert |
| `overlay` | Senter Super [Super Flashlight] | Overlay only | **43** | RENDAH → **deliberately silent** |

*(Scores computed from the weights in `Signal.kt`, assuming sideload + self-signed. Confirm on the emulator before putting them on the slide.)*

**Speaker notes (if the demo is live):**
> Two phones, live. Left is the parent's, right is the guardian's. They're already paired — a six-letter code, read aloud over a phone call.
>
> I install our test app on the parent's phone. It's harmless — it declares SMS access and does nothing. We never use real malware; we simulate the delivery, not the detection.
>
> Watch both phones. The parent opens the app — covered, offline, in under a second. The guardian has the score and the reasons.
>
> The guardian taps "ask Mum to remove this app." Her phone explains who asked and why, and opens Android's uninstall dialog — Android requires the person holding the phone to confirm; no app can delete another silently, and we say so openly. Confirmed. Gone. And the guardian sees it — only after the OS confirms, never just because a command was sent.

**Demo plan (from `scripts/ronda` — every step is one command):**
- `scripts/ronda scenario uc01-pair` → both emulators from zero to paired, initial scan done
- `scripts/ronda scenario uc02-realtime` → the bait is sideloaded, the guardian is alerted
- `scripts/ronda victim` → open the bait → the overlay covers it
- `scripts/ronda scenario uc04-uninstall` → guided uninstall request, with a pause for each tap
- The counter-rule proof: `scripts/ronda attack sms playstore` → as if from the Play Store → the score drops, **no alert**
- `scripts/ronda scenario uc03-reboot` → the phone restarts, and the block comes back without opening RONDA
- `scripts/ronda fixture` → the `DEMO01` offline 5-app fixture mode, as a fallback if the venue network is poor
- `scripts/ronda scenario` lists every scenario in `docs/USE_CASES.md`

**Verification evidence (from TODO.md, 14 August log, two Pixel 6 / API 33 emulators):**
- Pairing `QTDEZ3` → status `active` in RTDB
- Test APK installed → risk HIGH → alert written to `alerts/QTDEZ3/` → guardian notification `importance=4, category=alarm`
- The overlay covered the test app; *Mark safe* → the overlay disappeared **while the app was still on screen**, with no action on the parent's phone
- Uninstall → system dialog → package gone → the guardian received "removed"
- **Real bugs found by running it, not by building it** (good for credibility): `ACTION_DELETE` silently refused without `REQUEST_DELETE_PACKAGES`; the uninstall prompt did not appear while RONDA was open; `DetectionService` did not pick up the pairing on a phone set up from zero. All fixed and re-verified. A September round of testing against `USE_CASES.md` found and fixed four more (see TODO.md Block 6) — for example, undoing "mark safe" did not re-cover the app on the parent's phone.

**Repo numbers (recounted 27 Sep 2026 — recount again before the final):**
- **63** Kotlin files, **≈ 11,050** lines in `app/src/main`
- **30** JVM unit tests: 16 scoring engine (including a 5-case calibration table), 8 pairing deep-link parser, 5 explanation coverage, 1 template
- **54** commits, 3 contributors

**Backup — details that may be asked about:**
- The overlay absorbs touches and the Back key; the only way out is Home. Branded as RONDA, never imitating a system dialog.
- Offline: detection + overlay need no network. An alert written offline is queued (`setPersistenceEnabled(true)`) and sent when back online. *"Telling the victim to turn off mobile data doesn't suppress the alert — it only delays it."*
- An update to an already-flagged app stays blocked and files no second alert. An app once marked safe and then uninstalled leaves the allowlist; reinstalling it = a new question, and the guardian's history keeps the earlier ruling.
- Restart: the list of dangerous apps is stored locally, and `StartupReceiver` restarts detection and the overlay on boot — nobody has to open RONDA.
- Transparency: a permanent notification on the parent's phone that RONDA is active, and an info sheet "Dijaga oleh [name]" [Guarded by [name]]. Ending the pairing needs a confirmation on the phone itself.

---

# SLIDE 7 — Technical Architecture & Tech Stack

**Judging criteria:** #3 Technical feasibility

**Headline:** *"Stock Android. No root, no server, no cloud model."*

**On-screen:**
- Detection surface = **`PackageManager` only** (`getInstallSourceInfo()` API 30+, declared permissions, services, receivers, signing certificate)
- Scoring = **pure Kotlin, zero Android imports** → unit-tested on the JVM, deterministic, offline
- Transport = **Firebase Realtime Database**, one mechanism both ways (alerts ↑, commands ↓)
- Cost today = **Rp0** (Firebase Spark free tier)

**Visual — architecture diagram (redraw as a clean diagram):**

```
┌──────────────── PROTECTED PHONE ─────────────────┐
│  DetectionService (foreground; restarted on boot) │
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
│  Uninstall / Mark safe / Undo → CommandRepository ┘
└───────────────────────────────────────────────────┘
```

**Tech stack (table/icons):**

| Layer | Choice |
|---|---|
| Platform | Android native, **Kotlin**, minSdk **30**, target/compile SDK 37 |
| UI | **Jetpack Compose, Material 3**, custom design system (Nunito, light + dark) |
| Async | Kotlin **Coroutines + Flow** |
| Backend | **Firebase Realtime Database** (disk persistence on) — no server of our own |
| QR | ZXing core (generation only — **no camera permission**) |
| Blocking | `SYSTEM_ALERT_WINDOW` + `PACKAGE_USAGE_STATS` (`UsageStatsManager`, ±700 ms polling while an app is flagged) |
| Tooling | Android Studio, Gradle (AGP 9.3), GitHub, `scripts/ronda` two-emulator demo driver with one scenario per use case |
| Testing | JUnit (30 JVM tests), a 6-flavor decoy APK project |
| Languages | Indonesian + English (per-app locale) |

**Speaker notes:**
> Architecturally, RONDA is deliberately small. On the parent's phone, a foreground service listens for new installs. A signal extractor reads the app through PackageManager — and only PackageManager. A pure-Kotlin evaluator with no Android dependencies turns that into a score, which is why we can pin it with unit tests on a laptop.
>
> Alerts and commands flow through Firebase Realtime Database. We originally designed for Firebase Cloud Messaging, but Google retired the legacy server key in 2024 — device-to-device push now needs a paid backend. So the guardian holds an open listener instead: no server, no billing, sub-second delivery in testing. The trade-off, stated openly: if an OEM kills that service, alerts wait until RONDA runs again — on boot or when opened. FCM is the upgrade once there's a backend.

**Backup — technical decisions and why:**

| Decision | Why |
|---|---|
| RTDB listener, not FCM | The FCM legacy key was turned off in June 2024; HTTP v1 needs a backend (Cloud Functions, Blaze plan). **Gain:** no backend, no billing, sub-second, one mechanism both ways. **Cost:** depends on `GuardianAlertService` staying alive. |
| Data nested per pairing | The guardian subscribes to one node and receives only its own family's data. No `orderByChild`, no `.indexOn`, no cross-family path. |
| `timestamp` = server clock | A phone's clock can be manipulated; `ServerValue.TIMESTAMP` cannot. |
| `executedAt` = "delivered", not "done" | A removal is only confirmed by the `ACTION_PACKAGE_REMOVED` broadcast. The guardian is never told "removed" just because a command was received. |
| `packageName` duplicated into the command | Acting on the wrong package cannot be undone. |
| `InstallReceiver` registered at runtime | A manifest receiver for `PACKAGE_ADDED` has not been possible since API 26. A separate manifest receiver (`StartupReceiver`) restarts that service after boot. |
| Single Activity, no NavGraph | The screen is a function of role + pairing state; one source of truth. |
| No iOS | iOS cannot enumerate apps, detect installs, read other apps' permissions, or trigger an uninstall. **An architectural fact, not a preference.** The scam is Android-specific too. |

**Data model (RTDB):**

| Node | Key fields |
|---|---|
| `pairings/{code}` | `guardianDeviceId`, `protectedDeviceId`, `status` (pending→active→revoked), `createdAt`, `expiresAt` (+10 min) |
| `alerts/{pairingId}/{alertId}` | `packageName`, `appLabel`, `score`, `signals`, `status`, `removedAt`, `timestamp` (server) |
| `commands/{pairingId}/{commandId}` | `alertId`, `action` (`uninstall` / `mark_safe` / `revoke_safe` / `scan` / `disconnect`), `packageName`, `createdAt`, `executedAt` |

---

# SLIDE 8 — Security Framework

**Judging criteria:** #5 Level of security **and patentability**

**Headline:** *"A security app for vulnerable users must be held to a higher standard than the malware it detects."*

**On-screen — 5 hard rules (a violation = disqualification, at the level of the project's rules):**
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
| `REQUEST_DELETE_PACKAGES` | *Request* an uninstall (the user confirms) | `READ_CONTACTS`, location, mic |
| `INTERNET`, notifications, foreground service, battery exemption, boot completed | Alerts & staying alive | Device Owner / root |

*(From `app/src/main/AndroidManifest.xml` — can be shown directly.)*

**Speaker notes:**
> RONDA asks for the same trust the attacker is asking for, so we hold it to a stricter standard. Five rules in our project are treated as disqualifying, not as preferences. RONDA never requests SMS permission — it detects apps that do. It never uses an accessibility service — that's the banking trojans' main tool, and using it would make RONDA look exactly like what it's hunting.
>
> That has a cost we accept deliberately: accessibility would give a much stronger block. We take the weaker block in exchange for a far smaller privilege footprint. Our entire detection surface is PackageManager — it reveals no screen content, no keystrokes, no app data.
>
> And the thing a judge should ask about any family app: is this stalkerware? No. Pairing needs a code confirmed on the parent's own phone, a permanent notification shows monitoring is on and who the guardian is, and there is no covert mode — not deferred, permanently out of scope.

**Threat model (could be slide 8b or Q&A backup):**

| Threat | Mitigation |
|---|---|
| RONDA used as stalkerware | Pairing needs a code typed or tapped on the parent's phone; a permanent indicator; no hidden mode. Either side can end the pairing, and the other side is always told |
| The scammer tells the victim to disconnect, remove RONDA or revoke the overlay permission | A disconnect is announced to the guardian at once, and the parent's phone keeps covering what it flagged and keeps detecting locally. Permissions are rechecked on every launch; losing a capability is reported to the guardian (roadmap: heartbeat). Phase 3 (the bank holding the transfer) does not depend on the victim's phone |
| The overlay imitates a system dialog | Forbidden. The overlay is branded RONDA — imitating system UI is a trojan technique |
| The overlay used to block arbitrary apps | It may only cover packages the engine has flagged |
| A malicious deep link pairs the phone silently | `ronda://pair` **only fills the field**; a claim always needs an explicit tap on the parent's phone |
| Pairing codes brute-forced / reused | Single use, 10-minute expiry, enforced by database rules (not the client) |
| False positives train the guardian to ignore alerts | Two-axis scoring + diminishing returns, a "quiet" band, the guardian's allowlist, a calibration test that fails the build if a safe case drifts upward |

**Weaknesses we disclose ourselves (important for credibility in front of the Kaspersky judges):**
> The current Firebase rules are **PoC-grade and must not ship**: there is no authentication binding a node to a device, so anyone who guessed a pairing code could read that family's alerts. Fix: Firebase Anonymous Auth, store device IDs as `auth.uid`, rules `auth.uid === data.child('guardianDeviceId').val()`. The first post-hackathon task.
>
> *"A security product that overstates its own posture has already failed its users."*

**Compliance:**
- The full Android permission model; no root, no privilege escalation
- Google Play Device & Network Abuse + Stalkerware policies → met through visible consent
- **Personal Data Protection Law** (UU 27/2022) → data minimisation by design + explicit, revocable consent
- **Android Developer Verification** → RONDA itself will register as a verified developer (90-day plan)

**Patentability / IP (criterion #5 names "patentability" — do not skip it):**
- **What is new:** not the permission scanner (broad prior art), but the **guardian-mediated intervention architecture** — a local security verdict sent to a pre-paired second human's device, the origin device blocking locally while it waits for that human's decision, and the decision executed back on the origin device. Plus the two-axis scoring model with combination bonuses that encode the attacker's *intent*, and the consequence-language explanation layer.
- **Realistic in Indonesia:** Patent Law No. 13/2016 excludes computer programs *as such*, but an implementation with a concrete technical effect can qualify. The strongest claim is framed as a **technical system** (a device-to-device verdict-and-command protocol with local enforcement pending remote authorisation), not a business method.
- **A pragmatic order:** (1) register copyright of the code with DJKI · (2) the "RONDA" trademark + wordmark · (3) defensive publication (report + public repo) so nobody else can patent the mechanism · (4) patent advice on the device-to-device protocol once there is a commercial partner · (5) trade secret for the tuned weights and calibration tables.
- **An asset that keeps growing:** the network of paired, trusted guardian–protected relationships, plus aggregated detection signals.
- *Not legal advice; a patent consultant is needed before filing.*

---

# SLIDE 9 — Scalability & Deployment Readiness

**Judging criteria:** #6 Scalability & deployment readiness

**Headline:** *"Near-serverless today. A clear path to 200,000 families."*

**On-screen:**
- Detection & blocking run **on-device** → cost does not grow with scans
- An alert = **a few hundred bytes**, written only when a risky app appears
- The scaling driver = concurrent guardian connections, not data volume → **FCM + Anonymous Auth** at the pilot stage
- Deployment blockers identified, each with a plan

**Visual — infrastructure stages:**

| Stage | Devices | Infrastructure | Monthly cost |
|---|---|---|---|
| PoC (today) | < 10 | RTDB Spark (free) | **Rp0** |
| Pilot | 1,000–20,000 | RTDB Blaze + Anonymous Auth + FCM via Cloud Functions | Low, usage-based |
| Production | 200,000+ | Firestore or sharded RTDB, region `asia-southeast2` (Jakarta), monitoring | Scales with active pairings |

**Second visual — roadmap timeline:**

| Phase | When | Scope |
|---|---|---|
| **0 — PoC** | Done | Detection, soft-block, pairing, alerting, guardian response, initial scan, several protected phones per guardian, QR deep link, decoy APKs |
| **1 — Trustworthy MVP** | Oct 2026 – Mar 2027 | Physical low-end devices (Xiaomi/Oppo/Vivo, Android 11–14); a 100-app false-positive study; Play Store listing; developer verification |
| **2 — More signals** | Q2–Q3 2027 | A remote-access / screen-share app installed; accessibility enabled for a sideloaded app; a new device admin; default SMS app changed — **no new permissions** |
| **3 — Guardian as second approval** | 2027–2028 | With a bank partner: the guardian is notified of out-of-pattern transfers on an elderly account and can hold one for 30 minutes |
| **4 — Signal network** | 2028+ | Opt-in, anonymised detection metadata (hashes, certs, permissions, source — no personal data) as early warning for IASC, banks, Kaspersky |

**Speaker notes:**
> RONDA is close to serverless by design. All detection and blocking happens on the phone, so adding a family adds almost no backend load — an alert is a few hundred bytes, written only when something risky appears. What scales is the number of guardians holding a listener open, which is exactly why the pilot stage moves delivery to FCM and locks the database with anonymous auth.
>
> We know our deployment blockers: Play Store review of our two special permissions, developer verification, and OEMs that kill background services. Each has a plan — and the fallback for all three is distribution through a bank or telco partner.

**Performance — target vs status (honest):**

| Metric | Target (PRD) | Status today |
|---|---|---|
| Install → verdict | < 2 s on a 2 GB phone | In-memory arithmetic over about 23 signals; no network, no I/O beyond `PackageManager` |
| Detection → guardian notified | < 10 s | Sub-second on the emulator; **not yet measured on a physical phone over mobile data** |
| False positives on legitimate apps | 0 | Guarded by the calibration suite; **the 100-app study is the first post-hackathon task** |
| Battery | Negligible | Broadcast-based detection, not polling; `UsageStatsManager` is polled only while an app is flagged |

**Deployment blockers & plans:**

| Blocker | Plan |
|---|---|
| Play Store review of `SYSTEM_ALERT_WINDOW` + `PACKAGE_USAGE_STATS` | Core-functionality justification + demo video + stalkerware-policy compliance. Fallback: distribution through an operator/bank |
| Developer verification (ID from 30 Sep 2026; global 2027) | Register immediately — also a prerequisite for talking to banks |
| OEMs kill services (Xiaomi/Oppo/Vivo) | Battery exemption during setup (**already built**, step 4 of the wizard); protection restored on boot (**already built**); a heartbeat "Mum's phone last seen 3 days ago"; long term, pre-loading through operators |
| Stalkerware perception | Answered proactively (slide 8) |
| Firebase as a single point of failure | Detection + blocking are already offline; the transport is abstracted so it can move onto a partner's infrastructure (banks and telcos usually require this) |

**Business model (backup — or make it slide 9b if there is time):**

> **The elderly user never pays.** Anything monetisable on that screen is something a scammer can exploit (*"Pak, bayar dulu biar aman"* — "Sir, pay first so you're safe"). The 1-guardian ↔ 1-parent app stays free forever — it is the distribution engine.

| Model | Payer | Structure | Why they'd pay |
|---|---|---|---|
| **B2B2C licence** (primary) | Banks / e-wallets | Per protected device per year, est. Rp12–24k | **POJK 12/2024** requires every financial institution to run an anti-fraud strategy |
| **Operator bundling** | Telcos | Revenue share in family/elderly plans | Komdigi (Feb & Jul 2026) asked **all** operators to ship anti-scam features "as an application or other system"; Siscamling proves VAS billing rails exist |
| **Cyber-insurance requirement** | Insurers | Per active policy or premium discount | Personal cyber policies already sell (±Rp60–150k/year) and cover social engineering & malware — every prevented claim is margin |
| Freemium "RONDA Family" | Guardian | Rp15–25k/month for up to 5 parents | Validates willingness to pay |
| Threat-intel feed | IASC, banks, vendors | Aggregated, anonymised | Only valuable at tens of thousands of devices |

**Unit economics (one strong sentence):** at an estimated **Rp18k per device per year**, a bank breaks even by preventing **one Rp36-million case per 2,000 protected devices**. *(estimate, from STRATEGY.md §6)*

---

# SLIDE 10 — Impact

**Judging criteria:** #1 Track (social impact), #6

**Headline:** *"Every protected phone is one less family that loses Rp36 million."*

**On-screen:**
- **Who it protects:** ≈ 34 million Indonesians aged 60+, a fast-growing share of them newly online
- **Where the value is:** before the transfer — the < 2% recovery rate means prevention is the only lever
- **Community model:** no tech-savvy child? RT/RW volunteers, Karang Taruna, health-post cadres guard several neighbours — *ronda*, literally
- **Network effect:** thousands of guarded phones become a distributed early-warning sensor for new APK campaigns

**Visual:**
- Family icons: 1 guardian ↔ 1 parent → 1 neighbourhood volunteer ↔ 5–10 elderly people → a map of Indonesia with detection points (Phase 4)
- Or "before / after" — *Before: the victim decides alone, on the phone with the scammer* vs *After: the guardian decides, out of the scammer's reach*

**Speaker notes:**
> The impact we're after is simple: move the decision before the money moves. Recovery doesn't work — under two percent comes back — so every protected phone is measured in losses that never happen.
>
> The obvious question: what about an elderly person without a tech-savvy child? The answer is in the name. Neighbourhood volunteers — RT/RW, Karang Taruna, health-post cadres — can each guard several neighbours. Security as a shared social practice, not an individual skill.
>
> And at scale, every guarded phone becomes a sensor. Anonymised detections — hashes, certificates, permissions, no personal data — can warn IASC, banks and Kaspersky about a new campaign hours before it reaches a signature database.

**Metrics we will measure (not claims):**
- Alert precision (target > 95%) — guardians must never learn to ignore alerts
- Time from detection → guardian decision (target median < 10 minutes)
- 30-day active retention (target > 80%) — are OEMs killing us / are the elderly removing us
- Estimated loss prevented = DARURAT alerts that end in an uninstall × Rp36 million

**Partners (who, and why they care) — backup:**
- **Banks/e-wallets** (POJK 12/2024) — start with digital banks (blu, Jenius, SeaBank, Jago), BRI as the first big-bank target
- **OJK / IASC / Satgas PASTI** — legitimacy & a letter of support; OJK's West Java office (highest IASC reports) as the pilot region
- **Komdigi** — the elderly digital-literacy programme; the anti-scam mandate to operators
- **Operators** — Telkomsel (Siscamling), Indosat (SATSPAM): *"Siscamling guards the network, RONDA guards the phone."*
- **Cyber insurance** — Chubb×DBS, MSIG×Jenius, BCA Insurance
- **Kaspersky** — MoU with BSSN renewed April 2026 (covering public-awareness initiatives); hash reputation access; an introduction to BSSN

---

# SLIDE 11 — Conclusion & Next 90 Days

**Judging criteria:** a recap of all (#2 USP repeated)

**Headline:** *"Not a better warning. A second pair of eyes."*

**On-screen — a 3-line recap:**
- **Scores** the app on-device, in seconds, offline
- **Covers** it before the victim can use it
- **Hands the decision** to someone the scammer cannot reach

**On-screen — the next 90 days (optional, same slide):**
1. Register as a verified Android developer; test on 2–3 low-end physical phones
2. Run and publish a **100-app false-positive study**
3. Harden the backend (Anonymous Auth + locked rules); target a Play Store listing in Dec 2026
4. **Community pilot, 50–100 elderly users** in Bandung through elderly health posts
5. Three conversations: **OJK West Java**, one digital bank or insurtech, **Kaspersky**

**Success at day 90:** one institutional letter of support · one paid pilot scheduled · real precision numbers we can quote

**Speaker notes:**
> Sixty-four percent of incidents come from human error — because the decision lands on the person least able to make it. RONDA doesn't try to train that person out of it. It scores the app, covers it, and hands the decision to someone the scammer can't reach.
>
> You've seen it working on two phones. In the next ninety days we take it to real hardware, publish our false-positive numbers, and run a community pilot here in Bandung.
>
> That's *ronda*: neighbours keeping watch so everyone else can sleep. Now it works on your parents' phone.

**"The ask" (if closing with a request):**
- Introductions: OJK / IASC, a digital bank, Kaspersky's anti-fraud team
- Mentorship on Play Store policy for `SYSTEM_ALERT_WINDOW` + `PACKAGE_USAGE_STATS`

---

# SLIDE 12 — Q&A

**On-screen:**
- **Thank you — Questions?**
- RONDA · Dhanes · Alek · Malik
- github.com/angseleong/RONDA
- (optional) a QR code to the repo / demo video

**Team note:** keep this slide simple. Put the **backup slides** (Appendix B) behind it to jump to when answering specific questions.

---

# APPENDIX A — Judges' Question Bank (Q&A prep)

Answers in English, short (about 20–30 seconds). The bold line = the question most likely to come up.

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

**6. "What if the scammer tells the victim to disconnect, uninstall RONDA or revoke the permission?"**
> A disconnect is announced to the guardian immediately, and it doesn't lift the protection: the parent's phone keeps covering what it flagged and keeps detecting new installs. Permissions are re-verified on every launch, and losing capability is itself something to report to the guardian — our roadmap adds a heartbeat so the guardian sees "Mum's phone last seen 3 days ago". Longer term, Phase 3 lets a bank hold a suspicious transfer for the guardian — which doesn't depend on the victim's phone at all.

**7. "What about elderly people without a tech-savvy child?"**
> That's the name: ronda. RT/RW volunteers, Karang Taruna or health-post cadres can guard several neighbours — one guardian phone can already watch several protected phones.

**8. "Why Android only?"**
> The scam is Android-specific, and so is the defence. iOS doesn't let an app see other installed apps, detect installs, read their permissions, or trigger uninstalls.

**9. "Why Firebase RTDB instead of push notifications?"**
> Google retired the FCM legacy server key in June 2024; device-to-device push now needs a paid backend. RTDB gave us no server, no billing and sub-second delivery. The trade-off is the always-on listener; FCM is the upgrade at the pilot stage.

**10. "How secure is your own backend?"**
> Honestly: PoC-grade. There's no auth binding a node to a device yet, so a guessed pairing code could read that family's alerts. The fix is Firebase Anonymous Auth with rules checking `auth.uid` — it's our first post-hackathon task. We'd rather tell you than have you find it.

**11. "Is this tested on real phones?"**
> Development and verification ran on Android emulators — Pixel 6, API 33. The logic is proven; endurance under OEM battery management on Xiaomi/Oppo/Vivo is not yet. Physical low-end devices are step one of our 90-day plan. *(Update this answer if the team manages a physical-phone test before 3 October.)*

**12. "Who pays?"**
> Not the elderly user — ever. Banks, telcos and insurers, who already absorb the loss today. POJK 12/2024 obliges banks to run anti-fraud strategies; Komdigi has asked every operator to ship anti-scam features. At about Rp18k per device per year, preventing one case per 2,000 devices breaks even for a bank.

**13. "How is the score calculated? Isn't it arbitrary?"**
> It's adapted from CVSS: the heaviest capability, plus 40% of the rest, plus bonuses for combinations that express intent — then multiplied by provenance. Every capability maps to a MITRE ATT&CK for Mobile technique, and every verdict emits an audit string. The weights are calibrated against known cases in unit tests.

**14. "What's patentable here?"**
> Not the permission scanner — there's plenty of prior art. The novel part is the system: a device-local verdict routed to a pre-paired human device, local enforcement while awaiting that human's decision, and the decision executed back on the origin device. Near term we'd register copyright and the RONDA trademark, and publish defensively.

**15. "Couldn't malware avoid declaring SMS permission?"**
> To read SMS it must declare it — Android enforces that. Malware that switches to reading notifications instead is covered by the notification-listener signal and its combo. And the engine is signal-based, so new signals — remote-access apps, device-admin changes — slot in without new permissions.

**16. "Why not use AI / machine learning?"**
> Explainability. A guardian needs to know *why* — "can read your bank's OTP codes" — not a confidence value. Our model is small, deterministic, offline, auditable, and runs on a Rp1-million phone. ML can be layered on later, e.g. over the aggregated signal network.

**17. "What happens when the parent's phone is offline?"**
> Detection and blocking never touch the network. The alert is queued on disk and delivered on reconnect. Turning off mobile data delays the alert; it doesn't suppress it.

**18. "What if the phone is restarted?"**
> Protection comes back on boot by itself — detection, and the overlay over anything already flagged — without anyone opening RONDA.

---

# APPENDIX B — Backup Slides (after the Q&A slide)

| # | Backup title | Content | Used when asked |
|---|---|---|---|
| B1 | Scoring formula & worked example | The formula + the "Senter" case: 40 + 0.4×10 + 15 = 59 × (1.25×1.15) = 84.8 → **85** | Q13 |
| B2 | Signal catalogue + MITRE mapping | Table of 16 impact + 7 trust + 5 combos (slide 5 backup) | Q13, Q15 |
| B3 | Calibration & decoy results | Table of 5 calibration cases + 6 decoys | Q5 |
| B4 | Permission footprint | The "asks for vs refuses" table (slide 8) | Q2 |
| B5 | Threat model | The 7-threat table (slide 8) | Q2, Q6, Q10 |
| B6 | Data model & RTDB decision | The 3-node schema + why RTDB vs FCM | Q9, Q10 |
| B7 | Honest limitations | The Appendix D table | Q3, Q4, Q11 |
| B8 | Business model & unit economics | The 5-model table + 1:2,000 | Q12 |
| B9 | Competitive landscape (full) | The 7-player table + "limit" column | Q1 |

**Worked example B1 (ready to paste):**
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

# APPENDIX C — Fact check: where every number comes from

| Number on a slide | Origin | Status |
|---|---|---|
| 64% human error | Kaspersky 2023, cited in the HackNusa Guideline p. 4 | ✅ |
| Rp9.1 T, 432,637 reports | OJK/IASC (22 Nov 2024 – Jan 2026) [S1] | ✅ rechecked 26 Sep 2026 |
| Rp134 billion, 3,684 APK reports | OJK/IASC (Nov 2024 – 15 Oct 2025) [S3] | ✅ rechecked 26 Sep 2026 |
| ≈ Rp36.4 million / report | Our calculation, 134 billion ÷ 3,684 | ⚠️ mark "est." on the slide |
| < 2% recovered (Rp161 billion) | OJK press release [S4] | ✅ |
| 11.97% elderly, ≈ 34 million | BPS 2025 [S6]; "34 million" = an estimate from about 284 million residents | ⚠️ "≈" |
| 34.13% of elderly online, +7.7 points | BPS via Dataloka [S7] | ✅ |
| 30 Sep 2026 verification, app stores first, global 2027 | Android Authority, The Hacker News, Android Developers Blog | ✅ rechecked 26 Sep 2026 |
| 23 signals, 5 combos, weights | `core/Signal.kt` | ✅ code |
| Scores 45 / 14 / 55 / 85 / 100 | `RiskEvaluatorTest.kt` | ✅ test |
| Fixture 85 / 100 / 83 / 45 / 14 | `FakeGuardianRepository.kt` (computed through the real RiskEvaluator) | ✅ code |
| Decoys 85 / 100 / 70 / 88 / 63 / 43 | Flavor manifests + weights | ⚠️ check on the emulator |
| 63 files, ≈ 11,050 lines, 30 tests, 54 commits | Counted 27 Sep 2026 | ✅ (recount before the final) |
| Rp18k / device / year, 1 : 2,000 | STRATEGY.md §6 | ⚠️ estimate, say "est." |
| ±700 ms polling | `ForegroundAppMonitor.POLL_ESTIMATE_MS` | ✅ code (log estimate) |

**Small corrections to PROJECT_REPORT.md** (so the slides don't inherit them):
- The report says *"a 16-case calibration table"* — correct: **16 scoring-engine tests, 5 of which are the calibration table**, the rest edge cases & plumbing.
- The report says **41 commits** — now **54**.
- One scoring test, `RiskEvaluatorTest.reasonsAreOrderedBySeverity`, currently fails on `main`. **Do not say "all tests pass"** until it is fixed.

---

# APPENDIX D — What must NOT be claimed

| Do not claim | What is actually true |
|---|---|
| Notifications via FCM | Replaced by an RTDB listener. Do not say "FCM push". |
| A "Continue anyway" / override button on the overlay | The overlay has **no** continue button. The `overrodeAt` data exists only in the demo fixture. |
| Hard-block / the app is truly frozen | Needs Device Owner — roadmap. |
| Silent uninstall | Impossible on Android without Device Owner. |
| Tested on physical phones | Not yet (as of 27 Sep) — Pixel 6 / API 33 emulators. Update if it happens. |
| Accuracy %, latency on physical phones, user numbers, testimonials | **There is no data.** Do not make it up. |
| A secure / production-ready backend | The RTDB rules are still PoC-grade — admit it on slide 8. |
| "Patented" / "patent pending" | Nothing has been filed. Say "patentability potential". |
| "All tests pass" | One pre-existing scoring test fails (Appendix C). |

Now built, and safe to show as features: the initial scan of already-installed apps, one guardian watching several protected phones, the QR deep link, protection restored on boot, and undo of "mark safe" re-covering the app.

---

# APPENDIX E — Sources

Titles of Indonesian-language sources are kept in the original.

**Scale of the problem**
- [S1] TIMES Indonesia — IASC data, Rp9.1 T. https://jogja.times.co.id/news/kriminal/zHU1rbTfC/penipuan-whatsapp-meningkat-kerugian-capai-rp91-triliun-pakar-ugm-ingatkan-bahaya-file-apk
- [S2] Kompas (2 Jul 2026) — 579,459 IASC reports, the elderly as scam victims. https://nasional.kompas.com/read/2026/07/02/20330831/komdigi-sebut-banyak-lansia-jadi-korban-scam-ai-yang-tiru-suara-pejabat
- [S3] Radar Surabaya / Jawa Pos — APKs over WhatsApp, 3,684 reports, Rp134 billion. https://radarsurabaya.jawapos.com/ekonomi/776724733/kerugian-akibat-scam-digital-di-indonesia-tembus-rp7-triliun-ojk-ungkap-10-modus-utama
- [S4] OJK — IASC recovers Rp161 billion. https://ojk.go.id/id/berita-dan-kegiatan/siaran-pers/Pages/IASC-Berhasil-Kembalikan-Rp161-Miliar-Dana-Masyarakat-Korban-Scam.aspx
- [S6] BPS — Statistik Penduduk Lanjut Usia 2025 (elderly population statistics). https://www.bps.go.id/id/publication/2025/12/12/868d335b088dcddc3ddee052/statistik-penduduk-lanjut-usia-2025.html
- [S7] Dataloka (citing BPS) — elderly internet access 2025. https://dataloka.id/humaniora/5794/persentase-penduduk-lansia-yang-mengakses-internet-2025-terus-meningkat-dalam-6-tahun-terakhir/

**Platform & Google**
- Android Authority — timeline of the sideloading changes. https://www.androidauthority.com/android-sideloading-changes-timeline-3679204/
- The Hacker News (Jun 2026) — the Sept 30 deadline in four countries. https://thehackernews.com/2026/06/google-sets-sept-30-deadline-for.html
- Android Developers Blog (Jun 2026) — developer verification. https://android-developers.googleblog.com/2026/06/android-developer-verification.html
- Kaspersky — Android threats 2026 (the quote on bypassing verification). https://www.kaspersky.com/blog/growing-2026-android-threats-and-protection/55191/
- Kompas — Enhanced Fraud Protection in Indonesia (Feb 2025). https://amp.kompas.com/tren/read/2025/02/19/130000665/google-rilis-fitur-enhanced-fraud-protection-di-indonesia-cegah-penipuan
- MITRE ATT&CK for Mobile. https://attack.mitre.org/matrices/mobile/

**Regulation, banks, operators, insurance, comparisons** — see the full list in [STRATEGY.md §10](STRATEGY.md) (POJK 12/2024, POJK 22/2023, Komdigi–operators, Siscamling, SATSPAM, Chubb×DBS, MSIG×Jenius, the Kaspersky–BSSN MoU, ScamShield, Seraph Secure, Scammer Guardian).

**Internal (repo)**
- HackNusa 2026 Guideline (PDF in the repo root) — track brief p. 4, judging criteria FAQ #15, English FAQ #7
- [PROJECT_REPORT.md](PROJECT_REPORT.md), [PRD.md](PRD.md), [ARCHITECTURE.md](ARCHITECTURE.md), [STRATEGY.md](STRATEGY.md), [USE_CASES.md](USE_CASES.md), [finalscript.md](finalscript.md), [TODO.md](TODO.md)
