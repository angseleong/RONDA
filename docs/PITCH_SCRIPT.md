# RONDA — Final Pitch Script (7 minutes, live)

**Event:** HackNusa 2026 Final Pitching · Bandung · 3 October 2026
**Deck:** `RONDA_Pitch_Deck_v4.pdf` (slides 1–15 main, B1–B7 backup)
**Speakers:** Dhanes (opening, demo) · Alek (insight, solution) · Malik (tech, business, close)

## Time budget

| Block | Slides | Speaker | Target | Running |
|---|---|---|---|---|
| Hook & problem | 1–3 | Dhanes | 1:13 | 1:13 |
| Insight & solution | 4–7 | Alek | 1:40 | 2:53 |
| Demo & proof | 8–9 | Dhanes | 1:30 | 4:23 |
| How, why safe, why it scales | 10–14 | Malik | 2:10 | 6:33 |
| Close | 15 | Malik | 0:20 | **6:53** |

About 10 seconds of buffer for handoffs and clicks. **Do not spend it.** If you
pass 4:30 when the demo ends, use the cut list at the bottom.

Pace: 763 spoken words outside the demo video, about 140 words per minute — slower than you think. Stage nerves make
everyone fast. Lines in `>` blocks are spoken. *Italic* lines are stage notes.

---

## DHANES — Slides 1–3 (0:00–1:13)

### Slide 1 · Title — 0:08

*Walk to center. Smile. Wait one beat before the first word.*

> Hello everyone, greetings judges, we're Dhanes, Alek and Malik — and we built RONDA: a second pair of eyes on your parents' phone.

*Click.*

### Slide 2 · The story — 0:38

*Slow down here. This slide sells the whole pitch. Look at the judges, not the
screen.*

> Imagine this. Ibu Ratna, a retired teacher. Her son Dimas is in his final year in
> Jakarta. For years she has saved, rupiah by rupiah, for his tuition.
>
> On a random night. A message appears on her phone. It was a message from a former student inviting her to his wedding. 
>
> Spontaneously, she opens it.
>
> And the next day, she opens her bank app and it's empty completely.

*Pause two seconds. Let the silence do the work.*

> She did nothing wrong. She was being kind. And nobody was watching.

*Click.*

### Slide 3 · The problem — 0:30

> The problem is Bu Ratna is not the only victim. In Batang, one tap on an app took a
> congressman's savings from 1.3 billion rupiah down to only sa hundred thousand.
> If it can happen to her, it can happen to your loved ones.
>
> Nationwide: 134 billion rupiah in under a year. And less than two percent
> ever comes back.
>
> So the only window that matters is between the install and the first
> "Allow".


---

## ALEK — Slides 4–7 (1:13–2:53)

### Slide 4 · The product — 0:20
> Ronda is the answer.
>
> *Ronda* is the Indonesian night watch: neighbours taking turns staying awake,
> so everyone else can sleep.
>
> Our app does that for your parents' phone. It scores every new app, blocks the
> risky ones, and lets someone they trust decide.

*Click.*

### Slide 5 · Why warnings fail — 0:35

*Point at the four phone prompts, left to right.*

> She opens the file. Allows the source. Taps
> install. Allows SMS. Four warnings — she ignores every single one. Why?
>
> Because by then, she has trusted the caller for twenty minutes, and a pop-up
> can't compete with that. The scammer even warns her first: "a warning will
> appear, just tap continue." And antivirus is always one step behind, because
> the app is repackaged for every new campaign.
>
> Our track asks us to change behaviour. Our answer: don't train the victim.
> Change who decides.

*Click.*

### Slide 6 · How it works — 0:30

> There's only 5 steps. First, connect by pairing with a six-letter code. Second, Detect every new app
> and scored it from zero to a hundred offlinely. Third, Block anything that is risky and covered it every
> time it opens. Next is the guardian gets plain-language alert reasons. Last one, the guardian makes the decisions whether to remove
> it, or mark it safe.
>
> The parent makes zero security decisions. And alerts start at sixty — so when
> RONDA speaks, people listen.

*Click.*

### Slide 7 · Competition — 0:20

> "Doesn't Google already do this?" Play Protect can be switched off — and the
> scammer tells her to. Banks guard only their own app. Telcos can't read
> WhatsApp.
>
> At the moment of install, every defence asks the victim. Only RONDA asks a
> guardian.

*Hand off:* "Let's show you. Dhanes?"

---

## DHANES — Slides 8–9 (2:53–4:23)

### Slide 8 · Demo video — 1:10 (5 s intro + about 65 s video)

*Before the video starts:*

> and this is our demo.
> Two phones, live. Left is Mum's, right is her son's. It's a harmless test
> app — we never use real malware.

*Press play. The video has no audio. Speak **only** on each caption, then stay
quiet. Silence while the judges watch is good.*

| Caption on screen | Say |
|---|---|
| Installed | "She installs the wedding invitation." |
| Guardian alerted | "Within a second, her son's phone lights up." |
| Blocked offline | "She opens it — covered. Even with data off." |
| Score 85 + reasons | "And he sees why. Eighty-five out of a hundred: reads SMS, not from the Play Store, unofficial signature. That's OTP theft." |
| Guardian asks | "One tap: ask Mum to remove it." |
| Removed | "She confirms — Android requires that. No app can delete another one silently." |
| Confirmed | "Gone. And he knows it's gone." |

*Let the last frame hold for one second. Click.*

### Slide 9 · Beyond the demo — 0:20

*Point at the six feature tiles under "Beyond the demo".*

> And it's not a mockup — it's running today. One guardian can watch several
> parents. It can also scans the apps already on the phone, not just new ones. It
> catches more than SMS theft: screen control, fake screens, hidden apps.
> Decisions are reversible, and either side can disconnect.

*Hand off:* "Malik, how does it decide?"

---

## ALEK & MALIK — Slides 10–15 (4:18–6:38)

## ALEK

### Slide 10 · The scoring model — 0:35

*Don't read the formula. Point at the score row: Play Store 27 → sideloaded 85.
The math lives in backup B1 for Q&A.*

> RONDA asks two questions — the same ones you'd ask a stranger at the door.
>
> What can you do? This app can read SMS — so it can read her bank's OTP.
>
> And where did you come from? Not the Play Store. A WhatsApp file, from no
> known developer.
>
> Same app from the Play Store: quiet. From a stranger: eighty-five, and her son
> is alerted.
>
> And these aren't our guesses. Every ability maps to MITRE ATT&CK — the
> industry's map of real attacks.

*Click.*

### Slide 11 · Architecture — 0:15

> Everything that decides runs on the parent's phone. Firebase only carries the
> alert out and the decision back. No root, no backend code. Turning off data
> can't switch RONDA off.

*Click.*

## MALIK

### Slide 12 · Security & privacy — 0:20

> And we ask for less than the malware we catch. No SMS permission, no
> accessibility service, no hidden mode — the parent always sees who's
> guarding.

*Click.*

### Slide 13 · Patentability — 0:15

> A permission scanner isn't patentable. Our protocol is: phone A blocks, a
> paired human on phone B decides, and the decision runs back on A. We don't patent a better scanner. We patent who gets to
> decide.

*Click.*

### Slide 14 · Scalability & business — 0:35

> It scales on the phone, not the server — today, infrastructure costs zero.
>
> And the elderly never pay. Banks, telcos and insurers do; they already carry
> the loss. At eighteen thousand rupiah per phone per year, against thirty-six
> million lost per case, a bank breaks even by stopping one scam in two
> thousand phones.
>
> Next: a community pilot right here in Bandung. We're looking for OJK and
> IASC, a digital bank, and Kaspersky.

*Click.*

### Slide 15 · Close — 0:20

*Slow. Look at the judges, not the screen.*

> Back to that random night, nine forty-seven. Same scammer. Same file.
>
> This time, RONDA covers it — and her son calls her first. The savings stay
> put.
>
> Not a better warning. A second pair of eyes.
>
> We're RONDA. Thank you.

*All three stand together. Stop. Do not add anything.*

---

## If you are running long

Cut in this order. Each line is safe to drop.

1. Slide 13 — say only: "We don't patent a better scanner. We patent who gets
   to decide." (saves about 10 s)
2. Slide 11 — say only: "Detection and blocking never need the network."
   (saves about 8 s)
3. Slide 9 — say only: "Not a mockup. One guardian can watch several parents,
   and it catches more than SMS theft." (saves about 8 s)
4. Slide 5 — drop the "Because by then…" paragraph (saves about
   15 s)

**Never cut:** the slide 2 story (especially "She was being kind"), "Don't train the victim. Change who decides.",
the demo, the break-even line on slide 14, and the closing line.

## Q&A — where to point

| Likely question | Answer in one line | Slide |
|---|---|---|
| "How is the score calculated?" | Heaviest ability + 0.4 × the rest + combo bonus, times source multipliers. | B1, B3 |
| "False positives?" | Alerts start at 60; a busy Play Store app stays below 30. A 100-app study is next. | 10, B1 |
| "What if the scammer says uninstall RONDA?" | Permissions are re-checked every launch; loss is reported to the guardian. | 12 |
| "Isn't this stalkerware?" | Pairing needs consent on the parent's phone, plus a permanent "who's guarding" indicator. | 12 |
| "Why not just Play Protect?" | It asks the victim, and it can be switched off. | 7, B5 |
| "Can it delete the app itself?" | No. Android forbids silent uninstall; the parent confirms. | 11 |
| "Did you test real malware?" | No. Six harmless test apps, each declaring one permission group. | B4 |
| "Who pays?" | Banks, telcos, insurers — about Rp18k per device per year. | 14, B6 |
| "Patent filed?" | Not yet. Prior-art search first; do not say "patent pending". | 13, B7 |
