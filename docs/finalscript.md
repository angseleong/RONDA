# RONDA — Final Video Script

**Track:** Human-Centric Security · **Target:** 2:57 · **Limit:** 3:00
**Team:** Dhanes, Alek, Malik

- Shot directions are notes for the crew. **Lines in `>` quote blocks are spoken
  in English**, word for word.
- On-screen text is written `like this`. Text quoted from RONDA's own screens is
  in Indonesian, because that is the language the app is demoed in; an English
  gloss follows in brackets.
- **434 spoken words** ≈ 2:48 at 155 words/minute, plus about 9 seconds of held
  visual pauses = **about 2:57**. A **3-second** margin — almost none. If the
  first take runs even slightly long, go straight to the cut list at the bottom.

## Terms: victim & guardian

The script has moved from a specific framing ("mum" / "her son") to general
terms: **victim** (the elderly person whose phone is protected) and **guardian**
(the person close to them who makes the decision). Two rules keep it consistent:

1. **§1, §2, §3, §5, §6 stay generic** — use `the victim` / `the guardian`, not
   `she`/`he`/`her`/`his`.
2. **§4 (the demo) is the only place allowed to be concrete.** It opens with one
   bridging sentence (`For this demo, the victim is someone's mother...`), and
   only after that may the narration use "her"/"his", because the reference is
   now clear. This is also the direct answer to the request: the demo is shown
   as a mother and her son, while everywhere else the general definition stays
   victim/guardian.

---

# SCRIPT

## §1 · HOOK — Dhanes — 0:00–0:17
### ▸ Criterion 1 — Problem Statement (1 of 2)

**Shot** Cold open, no logo. Full-screen recording of a WhatsApp chat. A file
arrives: `Undangan Pernikahan.apk` [Wedding Invitation.apk]. The cursor hovers
over it. Freeze.
Corner caption: `Illustrated scenario`. Grade slightly darker than the demo.

**On screen** `64% of cyber incidents come from human error` · `— Kaspersky, 2023`
(lower third, at second 11)

> This is a wedding invitation.
>
> It is also how Indonesian families lose their savings.
>
> The victims are usually elderly people who don't quite understand technology.
>
> They didn't click a phishing link. They installed an app — because someone
> they trusted asked them to.

---

## §2 · THE PROBLEM — Dhanes — 0:17–0:35
### ▸ Criterion 1 — Problem Statement (2 of 2)

**Shot** Cut to Dhanes, facing the camera, plain background. Hold on the last
sentence before the cut.

> The usual answer is training: teach people to spot the scam.
>
> But you cannot train your way out of this. The scammer is on the phone with
> the victim *right now*.
>
> The decision sits with the person least able to make it, at the worst possible
> moment.

---

## §3 · THE SOLUTION — Malik — 0:35–1:07
### ▸ Criterion 2 — Proposed Solution & Unique Selling Proposition

*Production note, not spoken: the last two sentences here are RONDA's main USP —
moving the decision, not training the victim. This is what sets it apart from
the other entries in the same track.*

**Shot** Title card: the RONDA logo (wordmark), then cut to Malik.

**On screen** `RONDA — Family Protector`
**4-second caption**, appearing as Malik names the track:
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

## §4 · DEMONSTRATION — Dhanes — 1:07–2:04
### ▸ Criterion 3 — Demonstration

**Shot 4a** Two emulator windows side by side, one continuous take.
Labels stay on **for the whole section**:
`Emulator 1 — Mum's phone (70)` and `Emulator 2 — her son's phone (guardian)`.
2-second caption: `Already paired — a 6-letter code, read out over the phone`.
Second caption: `"Ibu" = Mum`.

> For this demo, the victim is someone's mother. The guardian is her son.
>
> Two Android emulators, live. Left, her phone. Right, his.

**Shot 4b** Run `scripts/ronda attack` (or `scripts/ronda scenario uc02-realtime`
for the whole setup in one go).
Small 2-second caption: `Install attributed to WhatsApp — adb -i`

> I install our test app on her phone. Harmless — one permission, nothing else.
> We never use real malware; we simulate delivery, not detection.

**Shot 4c** The score card appears on the guardian emulator. Slow zoom.
**Hold a full 3 seconds** — every line must be readable.

**On screen** (read from the real screen, not from this script)
`85 / 100 — PERINGATAN` [WARNING] · `PENCURIAN OTP` [OTP THEFT] ·
`BUKAN PLAY STORE` [NOT PLAY STORE] · `TANDA TANGAN TIDAK RESMI` [UNOFFICIAL SIGNATURE]

> RONDA scores it. Not a verdict — a number out of a hundred.
>
> Eighty-five. What it can do, where it came from, and who signed it.
>
> Above sixty, RONDA wakes the guardian. Below that, it logs it and stays quiet.
>
> Watch both phones.

**Shot 4d** Mum's emulator opens the app → the red overlay covers it.
The son's emulator shows the alert. **Hold a full 2 seconds — the most important
frame.** Emulators don't vibrate: sell it with a subtle zoom punch into the
guardian window. **Do not** add a vibration sound effect.

> Covered offline, in under a second. Her son has the reasons, not just the
> score.

**Shot 4e** On the guardian emulator, the buttons `Tandai berbahaya` [Mark
dangerous] → `Minta Ibu menghapus aplikasi ini` [Ask Mum to remove this app]
are pressed. Mum's emulator shows the confirmation screen.
Confirm → the app disappears → the overlay clears → the guardian's screen
updates to `Riwayat` [History].

> The guardian decides: uninstall. She confirms — Android requires it. No app
> can delete another silently.
>
> Gone. And he sees it.

---

## §5 · PATH TO MVP — Malik — 2:04–2:28
### ▸ Criterion 4 — Pathway

*Production note, not spoken: the last sentence here is the second USP —
detection signals from many family pairs can become a network that helps
institutions spot new attack patterns (zero-day) earlier than any single point
of detection could on its own.*

**Shot** Malik facing the camera, framed exactly as in §3. **No graphics.**
Three text captions appear one after another in the lower third, one per
sentence — white text on a translucent dark bar, no illustration needed:

1. `Stock Android · minSdk 30 · no root, no server`
2. `No accessibility service · no SMS permission`
3. `Next: real phones + institutional partners`

> It runs today on a stock Android image. No root, no server, no cloud model.
>
> One thing we will never add: RONDA uses no accessibility service and no SMS
> permission. That is the trojans' own toolkit.
>
> This ran on emulators — the logic is proven, not the endurance. Next: detection engine upgrade, and partnering with institutions to catch zero-day scam patterns
> before they spread.

---

## §6 · CLOSING — Alek — 2:28–2:57
### ▸ Criterion 5 — Wrap-Up

**Shot** Back to camera. On the third sentence, a slow push in to the two
emulator windows side by side. Cut to black, logo.

**On screen** `RONDA — Family Protector`

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

## If it runs long

The 3-second margin is almost nothing. If the first take goes past 3:00, cut in
this order:

1. `and partnering with institutions to catch zero-day scam patterns before they
   spread` (§5) → replace with `Next: real hardware.` — saves about 9 seconds,
   but this is the second USP, so first check whether it can survive as an
   on-screen caption before dropping it from the narration entirely
2. `She confirms — Android requires it. No app can delete another silently.`
   (§4e) — saves about 6 seconds; replace with the caption
   `Android requires the user's confirmation` so the admission of the limit is
   not lost completely
3. `The victim's phone was fine — the attack went through the person holding
   it.` (§3) — saves about 5 seconds

**Never cut:** the two sentences that introduce the victim in §1, the bridging
sentence `For this demo, the victim is someone's mother...` in §4a, the 3-second
hold on the score card, the 2-second hold when the overlay appears, and the line
`We are those guardians` in §3 — the one line that establishes why three male
students have standing to talk about elderly victims (see the cast section of
`VIDEO_SCRIPT.md`).
