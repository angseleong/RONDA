# RONDA — Partnership & Monetization Strategy

**Status:** Draft v1 · 18 September 2026 · Written for the RONDA team and the HackNusa 2026 judges
**What this document answers:** one question from the judges — *"After the hackathon, where does this go, and who pays?"*
**Related documents:** [PRD.md](PRD.md) (what is built), [ARCHITECTURE.md](ARCHITECTURE.md) (how), [TODO.md](TODO.md) (when)

Every figure in this document has a source in §10. Figures that are our own calculations from public data are marked **(estimate)**. No RONDA product metrics are quoted, because none exist yet — the POC has only run on emulators.

---

## 0. One-page summary

**Thesis.** Every existing player — Google, banks, mobile operators — warns the *victim*. The victim is the person who is on the phone with the scammer and already trusts them. RONDA is the only one that moves the decision to a *guardian*: a child, a nephew, a neighbour whom nobody is currently talking into anything. That is not a feature; it is a product category that does not yet exist in Indonesia.

**Why now.**
- Digital-scam losses reported to IASC: **Rp9.1 trillion** from 432,637 reports (Nov 2024 – Jan 2026). APK files sent over WhatsApp alone: **3,684 reports, Rp134 billion → about Rp36 million per victim (estimate)**.
- Indonesia is officially an *ageing population*: **11.97 % of residents are elderly (about 34 million people)**, 52 % of them already own a phone and 34 % use the internet — a figure that rose 7.7 points in a single year. The Deputy Minister of Communication and Digital Affairs has named the elderly as the main victims of AI-driven scams.
- Regulators are looking for tools, not just education: POJK 12/2024 requires every financial institution to have an anti-fraud strategy; Komdigi (Feb & Jul 2026) asked *all* mobile operators to deploy anti-scam features; Kaspersky and BSSN renewed their MoU (Apr 2026).

**An honest answer about Google.** Google already blocks some malicious APKs in Indonesia (Play Protect *enhanced fraud protection*, Feb 2025) and requires developer verification from 30 Sept 2026. We do not pretend otherwise. But: (1) the first wave covers app stores only — sideloading over WhatsApp or a browser is not covered until 2027; (2) a verified developer is not the same as a safe app; (3) legitimate remote-access apps (AnyDesk and similar) used in "share screen" scams pass every one of those filters; (4) every Google mechanism still asks the victim. RONDA does not compete with the door Google is reinforcing — RONDA changes *who opens the door*.

**Business model.** The victim never pays. The guardian almost never pays. The ones who pay are the institutions that carry the loss: **banks (B2B2C), mobile operators (bundling), personal cyber insurance (policy requirement)**. The free consumer app is the distribution engine, not the main source of revenue.

**Long-term asset.** A network of guardian–protected pairs who trust each other, plus aggregated detection signals from thousands of phones → a feed of new attack patterns for IASC and banks, faster than any single point of detection (the second USP in the pitch script).

---

## 0.5 Selling points for the pitch

Six points, in the order of the pitch (problem → solution → why us → why now → business → vision). Each point has the **claim** that is said out loud, the *evidence* shown on screen, and the section of this document that covers it in depth. Use what fits the time slot; points 2 and 3 must never be cut.

**1. One APK victim loses Rp36 million on average — and almost none of it comes back.**
*Evidence:* IASC — 3,684 reports of APKs over WhatsApp, Rp134 billion; the funds IASC managed to recover are under 2 % of total losses. → §1
*On screen:* `Rp36 million / victim` · `<2 % recovered`

**2. Everyone warns the victim. RONDA is the only one that tells someone else.**
*Evidence:* Google Play Protect, BRImo/Livin'/myBCA, Siscamling, SATSPAM, ScamShield — all of them ask the person who is on the phone with the scammer. Not a single product in Indonesia sends the decision to a guardian. → §2
*On screen:* a table of 5 players with one empty column: "tells someone else?"

**3. Google reinforces its door. RONDA changes who opens it.**
*Evidence:* Developer Verification in Indonesia starts 30 Sept 2026 — the first wave covers app stores only, WhatsApp and browsers not until 2027. Even after that, the "is someone pressuring you?" dialog is still asked of the person being pressured. AnyDesk (share-screen scams) passes every Google filter because it is legitimate. → §3
*On screen:* `30 Sep 2026: app stores only` · `2027: still asks the victim`

**4. Regulators have already asked for this tool — they just don't know its name yet.**
*Evidence:* POJK 12/2024 requires an anti-fraud strategy from every financial institution; the Deputy Minister (Jul 2026) asked *all* operators to deploy anti-scam features "in the form of an application or another system"; Kaspersky and BSSN renewed their MoU (Apr 2026); personal cyber insurance already sells for Rp60–150 thousand a year and covers malware and social engineering. → §5
*On screen:* four names: OJK · Komdigi · Kaspersky×BSSN · Chubb/MSIG

**5. The elderly never pay. The ones who pay are those who carry the Rp36 million today.**
*Evidence:* at Rp18 thousand per device per year, preventing one case per 2,000 devices already breaks even for a bank (estimate). Three payers: banks (B2B2C), operators (bundling — the Siscamling VAS mechanism already exists), insurers (policy requirement). Free forever for consumers at 1↔1. → §6
*On screen:* `Rp18k/year` vs `Rp36 million/case` · `1 : 2,000`

**6. Thousands of elderly people's phones = a distributed sensor for the next APK campaign.**
*Evidence:* every RONDA detection produces package metadata (hash, certificate, permissions, source) with no personal data — aggregated, that is a feed of new attack patterns for IASC and Kaspersky, hours before they reach a signature database. This is the answer to "partnering with institutions to catch zero-day scam patterns" in §5 of the script. → §4 Phase 4
*On screen:* a map of Indonesia with detection points lighting up

**30-second version (if there is only one slide):**
> An APK victim in Indonesia loses Rp36 million on average, and less than 2 % comes back. Every existing solution — Google, banks, operators — warns the victim, the person who is on the phone with the scammer. RONDA is the only one that sends the decision to a guardian. Google is reinforcing its door; RONDA changes who opens it. Regulators have already asked for this tool through POJK 12/2024 and Komdigi's mandate to operators. The elderly never pay — banks, operators and insurers, who carry the loss today, do. And every guarded phone becomes a sensor for the next scam campaign.

---

## 1. The problem in numbers

| Fact | Figure | Source |
|---|---|---|
| Digital-scam losses reported to IASC | Rp9.1 trillion · 432,637 reports (22 Nov 2024 – 11 Jan 2026) | [S1] |
| Reports to IASC (longer period) | 579,459 reports to May 2026; West Java highest at 119,750, Jakarta 84,845 | [S2] |
| APKs sent over WhatsApp/Telegram | 3,684 reports · Rp134 billion (Nov 2024 – 15 Oct 2025), among the top 10 methods | [S3] |
| **Average loss per APK report** | **about Rp36.4 million (estimate: 134 billion ÷ 3,684)** | our calculation from [S3] |
| "Impersonation over a fake phone call" | 47,269 reports (#2 most common) | [S2] |
| Funds recovered by IASC | Rp161 billion — under 2 % of losses | [S4] |
| Scam numbers reported to Komdigi | >30,000 numbers (Jan–Jul 2026) | [S5] |
| Elderly population | 11.97 % (about 34 million, **estimate** from a population of about 284 million) | [S6] |
| Elderly owning a phone / using the internet | 52.23 % / 34.13 % (2025), up from 26.42 % (2024) | [S7] |
| Internet penetration, ages 61–79 | 59.4 % | [S8] |

Three things these numbers say:

1. **Recovery does not work.** IASC is impressive, but under 2 % of funds come back. The value lies in *prevention before the transfer*, and that is exactly RONDA's window (install → before permissions are granted).
2. **APK reports are certainly under-reported.** Elderly victims often do not know how to report, are ashamed, or do not realise that the "invitation" was the cause. 3,684 reports is a floor, not a ceiling.
3. **The target population grows in two directions.** The number of elderly people is rising, and the share who are online is rising faster (7.7 points a year). Every elderly person who comes online is a potential victim who has never been trained.

---

## 2. The landscape: who has moved, and where the gap is

This table is deliberately honest. The Kaspersky judges will know all of this; it is better that we say it first.

| Player | What they do | Their limit (for the elderly + WhatsApp APK case) |
|---|---|---|
| **Google Play Protect — Enhanced Fraud Protection** (Indonesia, Feb 2025) | Automatically blocks sideloaded installs if the app asks for RECEIVE_SMS / READ_SMS / Notification Listener / Accessibility | Can be turned off; scammers have already put "if you see a warning, press continue" into their scripts. Tells nobody but the victim. Only four permissions. |
| **Android Developer Verification** (Indonesia, from 30 Sept 2026) | Apps on certified devices must come from verified developers; unverified apps go through an *advanced flow* with a 24-hour wait | Wave 1 covers **app stores only** (Play, Galaxy Store, GetApps, etc.); sideloading from WhatsApp or a browser only from 2027. Does not apply to uncertified devices. Verification = identity, not safety — Kaspersky itself expects attackers to find shortcuts. |
| **Android 17 Live Threat Detection** (2026) | On-device detection of SMS forwarding and overlay/accessibility abuse | Pixel first; the Rp1–2 million phones Indonesian elderly people use wait years for it, or never get it. |
| **Banks: BRImo, Livin', myBCA** | Block accessibility services, detect malware when the banking app opens | Protects **that banking app only**, **at transaction time**. The malware is already installed and the OTP has already leaked to the scammer before the banking app is opened. Once again: the victim is the one who is told. |
| **Operators: Siscamling (Telkomsel), SATSPAM (Indosat)** | Filter malicious calls/SMS/links in the network | Network-based → cannot see what is installed on the phone. WhatsApp is end-to-end encrypted, so APK files never pass through their filters. |
| **ScamShield (Singapore)** | App + hotline + account *kill switch*, run by the government | A good model, but still *self-service*: the victim has to decide to press the kill switch. |
| **Seraph Secure, Scammer Guardian (US)** | Paid services that alert family when an elderly person appears to be being scammed | Proof that **the guardian model sells** — but US-only, call-based, and absent from Indonesia. |

**The gap is consistent:** everyone builds smarter filters for the *victim*. Nobody builds a path to *someone else*. RONDA stands alone in that column, and not by accident — it has been a design decision since PRD §1.1.

---

## 3. RONDA's position after Google closes the front door

This is the part the judges are most likely to ask about: *"If Google already blocks sideloading, why RONDA?"*

**Four answers, from the most temporary to the most permanent:**

1. **The 2026–2027 window is still open.** Enforcement on 30 Sept 2026 only covers app stores. APKs sent over WhatsApp — the main scam vector — are untouched until the global expansion in 2027, and Google has not given a date for "all installation sources".
2. **Verified does not mean safe.** Verification binds an identity to an app; a syndicate that can run a call centre can buy an identity. Kaspersky's Malware Analyst Team Lead: *"Attackers will likely find ways to bypass verification."* RONDA reads what an app *can do* (its declared permissions), not who signed it.
3. **The next scam does not need a malicious APK.** "Share screen" scams use AnyDesk/TeamViewer — legitimate, verified, on the Play Store. They pass every Google filter. RONDA treats "a new remote-access app was installed on Mum's phone" as a signal, and the guardian judges the context.
4. **The most permanent: Google still asks the victim.** The "is someone pressuring you right now?" dialog in the *advanced flow* is asked of the person being pressured. The 24-hour wait is good, but the scammer can call again tomorrow. As long as the decision lives on the victim's phone, the victim can be talked round. RONDA moves that decision off the victim's phone. No Android update can do that, because Google does not know who your child is.

**Line for the pitch:** *Google reinforces its door. RONDA changes who opens it.*

The strategic consequence: **RONDA's asset is not an APK detector — RONDA's asset is the guardian–protected relationship, already paired and trusted.** The APK detector is the first signal to travel that path. The second, third and later signals (§4) are what keep RONDA relevant after 2027.

---

## 4. Product roadmap (the basis of the revenue roadmap)

| Phase | When | What | Why this first |
|---|---|---|---|
| **0 — POC** | done 21 Aug 2026 | Install-time detection, soft-block overlay, pairing, alerts to the guardian, remote uninstall request. Emulator. | Proves the core mechanism. |
| **1 — Trusted MVP** | Oct 2026 – Mar 2027 | Physical phones (Xiaomi/Oppo/Vivo, Android 11–14). Initial scan (Block 4B). Multiple protected phones per guardian (4C). QR deep link (4D). Play Store listing — **RONDA itself must be a verified developer** before 2027. *False-positive* testing on 100 legitimate apps. | Without the Play Store and verification, no bank or Komdigi can recommend RONDA. Without FP testing, guardians learn to ignore alerts. |
| **2 — Second and third signals** | Q2–Q3 2027 | (a) A new remote-access/screen-share app installed, from any source. (b) An accessibility service enabled for a sideloaded app. (c) A new device admin. (d) A change of default SMS app. All still through `PackageManager` + Settings, with no new permissions. | The answer to Google's 2027 verification: the scam vector shifts to legitimate apps being abused. |
| **3 — The guardian as *second approval*** | 2027–2028, via bank partners | The bank notifies the guardian (through RONDA) when an elderly person's account makes an out-of-pattern transfer; the guardian can "hold for 30 minutes". A kind of ScamShield *kill switch*, pressed by someone who is not being scammed. | A feature banks can sell to their priority customers, and one that makes RONDA part of a bank's anti-fraud strategy (POJK 12/2024) rather than just a third-party app. |
| **4 — Signal network** | 2028+ | Aggregated detection metadata (package hash, certificate, permissions, install source; **no personal data**) → a feed of attack patterns to IASC, banks, Kaspersky. Opt-in per guardian. | Thousands of elderly people's phones = a distributed sensor for new APK campaigns, hours before they reach a signature database. |

Four things that are **not** on the roadmap and never will be: iOS (architecturally impossible, PRD §7), Accessibility Service, SMS permissions, and hidden monitoring. These are product guardrails and, at the same time, the trust argument to regulators.

---

## 5. Partnerships: who, why they care, what we ask

Principle: every partner is approached with **a problem they have already acknowledged publicly**, not with our product. The order below is the order of priority.

### 5.1 Banks & e-wallets — the main commercial partners

**Why they care:** POJK 12/2024 requires an anti-fraud strategy and incident reporting to OJK; POJK 22/2023 demands system security and fair treatment of consumers. Banks are already moving on their own (BRImo's accessibility pop-up, Livin' blocking sideloaded apps) — so the budget and the internal mandate already exist. Losses among elderly customers are a reputational cost + a *dispute* cost + OJK pressure.

**What RONDA gives:** a layer *upstream* of their protection. The bank sees malware when the banking app opens; RONDA sees it when it is installed, before the OTP leaks. Plus Phase 3: the guardian as *second approval* — a feature no bank in Indonesia has yet.

**What we ask:** a 6-month pilot with 500–2,000 elderly customers whose children bank with the same bank (the bank already knows family relationships from KYC data and joint accounts). The bank distributes through its customer-service and branch channels; RONDA provides the app and an aggregate dashboard.

**Way in:** not a big bank's RFP (a 12–18-month cycle). Start with **digital banks / rural banks (BPR) / fintechs**, whose cycles are short and whose customers are already used to bundling (Jenius already bundles MSIG cyber insurance; blu by BCA Digital writes about *personal cyber insurance*). Big banks follow once there are numbers from a pilot.

**First candidates:** blu (BCA Digital), Jenius (SMBC), SeaBank, Bank Jago; e-wallets: DANA, GoPay (family ecosystem). BRI as the first big-bank target because it has the largest rural and elderly customer base and is already the most aggressive in BRImo.

### 5.2 OJK / IASC / Satgas PASTI — legitimacy & data

**Why they care:** IASC was built for *response* (freezing accounts), and the result is under 2 % of funds recovered. OJK needs a *prevention* story. IASC already collects reports by method, including "APK over WhatsApp" — RONDA is the sensor at the edge they do not yet have.

**What RONDA gives:** (a) aggregated detection statistics by region and method (Phase 4); (b) concrete case studies for OJK's education campaigns; (c) evidence that the "guardian" approach can be regulated — explicit consent, no SMS or Accessibility, minimal data.

**What we ask:** not money. **A letter of support / recognition as an initiative aligned with Satgas PASTI**, and a slot in IASC's communication channels (website, social media). That is what opens the bank door (§5.1) — banks move much faster once OJK has nodded.

**Way in:** OJK's West Java regional office (highest IASC reports: 119,750) — a natural pilot region. The RONDA team is based in Bandung (the HackNusa final is at Telkom University).

### 5.3 Komdigi — distribution & the operator mandate

**Why they care:** Komdigi runs a digital-literacy programme for the elderly (2026: 100 literacy guides, a target of 5,000 people) and has publicly (Feb & Jul 2026) asked *all* mobile operators to deploy anti-scam features, leaving them free to choose the form — "an application or another security system". That sentence opens exactly the space RONDA fills.

**What RONDA gives:** a concrete tool for the elderly literacy programme — a session ends with "pair your phone with your child's phone", not just "be careful". Literacy guides become backup guardians for elderly people whose children are not tech-savvy.

**What we ask:** (a) RONDA in the curriculum/toolkit for elderly digital-literacy guides; (b) a formal introduction to operators as an option for meeting Komdigi's request.

### 5.4 Mobile operators — the largest distribution channel

**Why they care:** Komdigi is pressing them, and their products (Siscamling, SATSPAM) only cover calls and SMS. The "what is installed on the phone" gap is unfilled. Telkomsel already sells Siscamling as a VAS — the *billing* and *bundling* mechanism exists.

**What RONDA gives:** an *on-device* layer that complements Siscamling/SATSPAM. The marketing story is clean: "Siscamling guards the network, RONDA guards the phone."

**What we ask:** bundling in family or elderly plans, with *revenue share* (§6, Model B). Pre-loading on operator-bundled phones solves our biggest problem: OEMs killing background services.

**Candidates:** Telkomsel (largest rural and elderly base, mature VAS), Indosat (most vocal on anti-scam, already partnered with Tanla for AI).

### 5.5 Personal cyber insurance — the neatest economic incentive

**Why they care:** the products already exist and are growing — Chubb×DBS Cyber Guard (Rp60–150 thousand a year, cover up to Rp50 million), MSIG×Jenius (from Rp70 thousand a year), BCAinsurance (from Rp8,750). These policies **cover social engineering and malware** — exactly the losses RONDA prevents. Every claim that does not happen is the insurer's margin.

**What RONDA gives:** a provable reduction in risk ("RONDA active & guardian paired" can be checked at *underwriting*), like telematics in car insurance.

**What we ask:** RONDA active as a **condition for a premium discount** or a policy requirement for insured people over 60. The insurer pays RONDA per active policy (§6, Model C).

### 5.6 Kaspersky — sponsor, threat intelligence, a door to BSSN

**Why they care:** Kaspersky recorded 14.9 million internet-borne attacks on Indonesian users in 2025, renewed its MoU with BSSN (Apr 2026) covering "public awareness initiatives", and offers the *Kaspersky Fraud Prevention* SDK to banks. RONDA is the family layer they do not have, and HackNusa is a relationship that is already open.

**What RONDA gives:** a channel into a segment that never buys antivirus (rural elderly people) through the person who might buy it (their child). Field detection signals (Phase 4).

**What we ask:** (a) access to a hash/certificate reputation feed to enrich scoring (optional; core detection stays offline); (b) an introduction to BSSN through their MoU; (c) "supported by Kaspersky" co-branding for the first pilot.

### 5.7 OEMs (Xiaomi, Oppo, Vivo, Samsung) — a technical problem turned opportunity

Three Chinese OEMs dominate the Rp1–3 million phones that elderly Indonesians use, and their *skins*, which kill background services, are risk R1 in the PRD. Pre-loading or an official *whitelist* solves it in one step. Not a priority in the first 12 months — it needs volume first — but noted because the operator path (§5.4) usually opens the OEM path.

### 5.8 Community: RT/RW, elderly health posts, Karang Taruna, the Ministry of Social Affairs

Not commercial partners, but the answer to a question that will certainly come up: **"What about elderly people without a tech-savvy child?"** The answer is in the product's name: *ronda* — the neighbourhood night watch. A Karang Taruna volunteer or a health-post cadre becomes the guardian for 5–10 elderly people in their neighbourhood (Phase 1's multiple protected phones per guardian makes this possible). Komdigi or the Ministry of Social Affairs could sponsor the programme. This is what fits RONDA to *Human-Centric Security* — security as a social practice, not an individual product.

---

## 6. Monetization

### Principles

1. **The protected person never pays, never sees a price, is never offered an upgrade.** The elderly person's screen is free of anything a scammer could exploit ("sir, pay first so you're safe").
2. **The ones who pay are the ones who carry the loss** — banks, insurers, operators. They already pay the cost of scams today; RONDA only turns it from a cost *after* into a cost *before*.
3. **The consumer app is free forever for 1 guardian ↔ 1 protected person.** It is the distribution engine and the source of trust; monetising it kills both.
4. **No ads, no selling of personal data, no hidden monitoring.** Beyond ethics, these are eligibility conditions for every partner in §5.

### The price anchor: what is one prevented case worth?

From IASC data: Rp134 billion ÷ 3,684 reports = **about Rp36 million per APK case (estimate)**. Compare personal cyber insurance at Rp60–150 thousand a year for Rp10–50 million of cover. The market has already priced "peace of mind" at around **Rp5–12 thousand a month per person**. On the B2B side RONDA must be cheaper than that, because RONDA reduces claims rather than paying them.

### Five models, in order of priority

| # | Model | Who pays | Form | When | Notes |
|---|---|---|---|---|---|
| **A** | **B2B2C licence for banks / e-wallets** | Bank | Per protected device per year, **Rp12–24 thousand** (estimate), with a minimum pilot commitment. Phase 3 (*second approval*) as a premium tier. | Pilot Q1 2027, commercial 2028 | The main revenue source. Break-even for the bank: at Rp18 thousand per device, 2,000 devices = Rp36 million a year → preventing **one** Rp36 million case per 2,000 devices already breaks even (estimate). |
| **B** | **Operator bundling** | Operator (and partly the subscriber through the plan) | 30–50 % VAS revenue share on a Rp3–5 thousand monthly fee in family/elderly plans, or a flat licence. | Q3 2027 | Largest volume, smallest margin. Solves the OEM problem (pre-loading). |
| **C** | **Cyber-insurance policy requirement** | Insurer | Rp5–10 thousand per active policy per year, or an insurer-funded premium discount. | 2027 (together with A, through digital banks that already bundle insurance) | The neatest incentive: the insurer profits from every claim that does not happen. |
| **D** | **"RONDA Family" consumer freemium** | Guardian | Free: 1↔1. Paid Rp15–25 thousand a month: up to 5 protected phones, 12-month history, backup guardian, Phase 2 signals. | After the Play Store, Q2 2027 | Deliberately small. Indonesian consumer ARPU is low; its main role is to validate *willingness to pay* and bring early revenue before the first B2B contract. |
| **E** | **Threat-intelligence feed** | IASC, banks, security vendors | Subscription to aggregated data (hash, certificate, permissions, source, region; no identities). Opt-in per guardian. | 2028+ | Only valuable at tens of thousands of devices. Bounded by the Personal Data Protection Law and PRD §5.4: package metadata only. |

**Non-commercial funding (not a business model, but a bridge for the first 12 months):** Komdigi and BSSN programme grants/incentives, bank CSR funds (elderly programmes), competition prizes. Used for: physical phones, developer verification and listing fees, the first pilot.

### Staged projection (targets, not forecasts)

| Stage | Protected devices | Revenue source | Goal of the stage |
|---|---|---|---|
| Oct 2026 – Mar 2027 | 0 → 1,000 (community, team families, literacy programme) | Rp0; grants/CSR | Real *false-positive* figures, guardian *time-to-decision*, 5 case studies |
| Apr – Dec 2027 | 1,000 → 20,000 (pilot with 1–2 digital banks + Komdigi programme) | Model A pilot + D | First B2B contract, evidence of fewer incidents in the pilot cohort |
| 2028 | 20,000 → 200,000 (1 big bank + 1 operator) | A + B + C | Phase 3 *second approval* live; ARR Rp2–4 billion (estimate at Rp12–24 thousand per device) |
| 2029+ | >1 million | A + B + C + E | The signal network (Phase 4) becomes a product of its own |

### Metrics we hold ourselves to (and partners will ask for)

- **Alert precision** (target >95 %): guardians must never learn to ignore alerts.
- **Time from detection → guardian decision** (target median <10 minutes): measures whether the guardian path really is faster than the scammer.
- **Installs still active after 30 days** (target >80 %): measures whether OEMs are killing us and whether elderly people are removing us.
- **Estimated loss prevented** = DARURAT (emergency) alerts that end in an *uninstall* × Rp36 million. This is the number taken to banks.

---

## 7. Strategic risks

| Risk | Impact | Mitigation |
|---|---|---|
| **Google extends verification to all sources (2027)** and most APKs-over-WhatsApp die | RONDA's first signal weakens | Phase 2 is built *before* that happens. The message from day one: RONDA = the guardian path, not an APK detector. |
| **Play Store policy** rejects `PACKAGE_USAGE_STATS` / `SYSTEM_ALERT_WINDOW` | Cannot list | *Core functionality* justification + demo video; RONDA already complies with the anti-stalkerware rules (visible consent). Fallback: distribution through operators/banks (official sideloading from a verified developer — ironic, but legitimate). |
| **Stalkerware perception** from media, judges or regulators | Reputation, access to banks & OJK | PRD §5.5–5.6: in-person pairing, a permanent indicator on the protected phone, no content reading. Either side can end a pairing, and the other side is told immediately — but a remote disconnect never removes protection from the protected phone: flagged apps stay covered and detection keeps running. Document it and say it first. |
| **Scammers adapt**: they walk the victim through removing RONDA or revoking the overlay permission | The soft-block fails | Revoking RONDA's permissions or uninstalling it = an alert to the guardian (already in PRD R6a). Phase 3 (the bank holding the transfer) does not depend on the victim's phone at all. |
| **12–18-month bank procurement cycles** | Running out of runway before a contract | Start with digital banks, fintechs and insurers (§5.1, §5.5); bridge funding from grants/CSR; freemium for small cash flow. |
| **OEMs kill background services** | Protection silently dies | Battery-optimisation exemption during setup (already built), restoring protection on boot without opening the app (already built), a *heartbeat* to the guardian ("Mum's phone was last seen 3 days ago"), and in the long run pre-loading through operators. |
| **Personal Data Protection Law** | Sanctions, loss of partners | Minimal data by design (package metadata only), explicit consent, Phase 4 aggregate and opt-in only. An audit by the first bank partner as part of the pilot. |
| **Firebase dependency** | A single point of failure for alerts | Detection + overlay are already offline. Plan: a transport abstraction so it can move onto a partner's infrastructure (banks and operators usually require this). |

---

## 8. The 90 days after HackNusa (Oct – Dec 2026)

This order is chosen so that each step unlocks the next.

1. **Weeks 1–2 — Developer verification & physical phones.** Register as a verified developer (required before 2027; also proof of compliance when talking to banks). Buy 2–3 second-hand Xiaomi/Oppo phones in the Rp1–2 million class.
2. **Weeks 2–6 — *False-positive* testing on 100 apps** (50 from the Play Store, 50 legitimate sideloads: old banking apps, government apps, games). Publish the results in the repo. This is the first document banks will ask for.
3. **Weeks 3–8 — Blocks 4B/4C/4D** (initial scan, multiple protected phones, QR deep link) → an MVP volunteers can use.
4. **Weeks 6–10 — A community pilot with 50–100 elderly people** through elderly health posts / neighbourhoods in Bandung, with student volunteers as backup guardians. Collect: precision, decision time, 30-day retention, and **stories**.
5. **Weeks 8–12 — Three conversations:** (a) OJK West Java / IASC, bringing pilot data; (b) one digital bank or insurtech (Jenius/blu/MSIG) for a paid pilot in 2027; (c) Kaspersky, following up HackNusa for an introduction to BSSN.
6. **Throughout — Play Store listing**, targeted for December 2026.

The definition of "success" on day 90: one letter of institutional support, one scheduled paid pilot, and a real precision figure that can be quoted.

---

## 9. Ready answers to the judges' questions

- *"Google already does this."* → §3. Google reinforces its door; RONDA changes who opens it. The 2026 verification wave does not touch WhatsApp yet; even in 2027 it still asks the victim.
- *"Who pays?"* → §6. Banks, operators, insurers — the parties already carrying Rp36 million per case today. The elderly never pay.
- *"This is stalkerware."* → PRD §5. In-person consent, a permanent indicator, no SMS/Accessibility/content. Every one of those design decisions is an *entry requirement* for every partner in §5.
- *"What about elderly people whose children aren't tech-savvy?"* → §5.8. Ronda, literally: neighbourhood and health-post volunteers as backup guardians.
- *"Why Android only?"* → PRD §7. The scam is on Android, and the defence is only possible on Android.
- *"What happens if RONDA is a big success?"* → §4 Phase 4. Thousands of elderly people's phones become a distributed sensor for new APK campaigns — that is a partnership with IASC and Kaspersky, not just an app.

---

## 10. Sources

Titles of Indonesian-language sources are kept in the original.

**Scale of the problem**
- [S1] TIMES Indonesia, "Penipuan WhatsApp Meningkat, Kerugian Capai Rp9,1 Triliun" — IASC data 22 Nov 2024 – 11 Jan 2026. https://jogja.times.co.id/news/kriminal/zHU1rbTfC/penipuan-whatsapp-meningkat-kerugian-capai-rp91-triliun-pakar-ugm-ingatkan-bahaya-file-apk
- [S2] Kompas, "Komdigi Sebut Banyak Lansia Jadi Korban Scam AI" (2 Jul 2026) — 579,459 IASC reports, top-5 methods, the Deputy Minister's statement on operators. https://nasional.kompas.com/read/2026/07/02/20330831/komdigi-sebut-banyak-lansia-jadi-korban-scam-ai-yang-tiru-suara-pejabat
- [S3] Radar Surabaya / Jawa Pos, "Kerugian Akibat Scam Digital Tembus Rp7 Triliun, OJK Ungkap 10 Modus Utama" — APKs over WhatsApp, 3,684 reports, Rp134 billion (Nov 2024 – 15 Oct 2025). https://radarsurabaya.jawapos.com/ekonomi/776724733/kerugian-akibat-scam-digital-di-indonesia-tembus-rp7-triliun-ojk-ungkap-10-modus-utama
- [S4] OJK, press release "IASC Berhasil Kembalikan Rp161 Miliar Dana Masyarakat Korban Scam". https://ojk.go.id/id/berita-dan-kegiatan/siaran-pers/Pages/IASC-Berhasil-Kembalikan-Rp161-Miliar-Dana-Masyarakat-Korban-Scam.aspx
- [S5] Kompas, "Komdigi Terima Laporan 30.000 Nomor Scamming Sepanjang Januari–Juli 2026". https://nasional.kompas.com/read/2026/07/23/17250811/komdigi-terima-laporan-30000-nomor-scamming-sepanjang-januari-juli-2026
- [S6] BPS (Statistics Indonesia), Statistik Penduduk Lanjut Usia 2025 (11.97 %). https://www.bps.go.id/id/publication/2025/12/12/868d335b088dcddc3ddee052/statistik-penduduk-lanjut-usia-2025.html
- [S7] Dataloka (citing BPS), "Persentase Penduduk Lansia yang Mengakses Internet 2025". https://dataloka.id/humaniora/5794/persentase-penduduk-lansia-yang-mengakses-internet-2025-terus-meningkat-dalam-6-tahun-terakhir/
- [S8] GoodStats (citing APJII 2025), internet penetration by generation. https://data.goodstats.id/statistic/penetrasi-internet-indonesia-menurut-generasi-2025-milenial-dan-gen-z-terdepan-jw8fn

**Platform landscape**
- Google Security Blog, "Piloting new ways of protecting Android users from financial fraud" (Feb 2024). https://security.googleblog.com/2024/02/piloting-new-ways-to-protect-Android-users-from%20financial-fraud.html
- Kompas, "Google Rilis Fitur Enhanced Fraud Protection di Indonesia" (19 Feb 2025). https://amp.kompas.com/tren/read/2025/02/19/130000665/google-rilis-fitur-enhanced-fraud-protection-di-indonesia-cegah-penipuan
- Google, "Learn about Android developer verification" — device and country coverage. https://support.google.com/android/answer/17065026
- Android Authority, "Google details when Android's new sideloading changes will start affecting users" — the 30 Sept 2026 wave covers app stores only. https://www.androidauthority.com/android-sideloading-changes-timeline-3679204/
- Android Authority, "Android's new sideloading rules … 24-hour lock". https://www.androidauthority.com/google-android-sideloading-unverified-apps-new-rules-3650343/
- The Hacker News, "Google Sets Sept. 30 Deadline for Android Developer Verification in Four Countries" (Jun 2026). https://thehackernews.com/2026/06/google-sets-sept-30-deadline-for.html
- Kaspersky, "Fake apps, NFC skimming attacks, and other Android issues in 2026" — the quote on bypassing verification. https://www.kaspersky.com/blog/growing-2026-android-threats-and-protection/55191/
- Google, "What's new in Android security & privacy 2026" — Live Threat Detection, verified financial calls. https://blog.google/security/whats-new-in-android-security-privacy-2026/

**Banks, operators, insurance**
- detik, "BRImo Terapkan Proteksi Berlapis dan Fitur Anti-Malware Terbaru". https://inet.detik.com/security/d-7715974/brimo-terapkan-proteksi-berlapis-dan-fitur-anti-malware-terbaru
- Kontan, "Cara Kelola Fitur Accessibility biar BRImo Aman" — Livin'/myBCA practice. https://momsmoney.kontan.co.id/news/wajib-tahu-ini-cara-kelola-fitur-accessibility-biar-brimo-aman-digunakan-di-2025
- Bisnis, "Komdigi Minta Semua Operator Seluler Punya Fitur Antispam dan Antiscam" (6 Feb 2026). https://teknologi.bisnis.com/read/20260206/101/1950761/komdigi-minta-semua-operator-seluler-punya-fitur-antispam-dan-antiscam
- Telkomsel, Siscamling VAS page. https://www.telkomsel.com/vas/siscamling
- Selular, "Indosat Klaim Blokir 2 Miliar Spam dan Scam dalam Enam Bulan" (Feb 2026). https://selular.id/2026/02/indosat-klaim-blokir-2-miliar-spam-dan-scam-dalam-enam-bulan/
- CNBC Indonesia, "Rekening Dibobol Hacker, Uang Hilang Bisa Diganti Asuransi?" (Aug 2026) — Chubb×DBS and MSIG×Jenius premiums. https://www.cnbcindonesia.com/mymoney/20260813122142-72-758911/rekening-dibobol-hacker-uang-hilang-bisa-diganti-asuransi
- BCAinsurance, Personal Cyber Insurance. https://www.bcainsurance.co.id/product/detail/asuransi-siber-pribadi-personal-cyber-insurance

**Regulation**
- POJK 12/2024, Penerapan Strategi Anti Fraud bagi Lembaga Jasa Keuangan (anti-fraud strategy for financial institutions). https://ojk.go.id/id/regulasi/Pages/Penerapan-Strategi-Anti-Fraud-Bagi-Lembaga-Jasa-Keuangan.aspx
- POJK 22/2023, Pelindungan Konsumen dan Masyarakat di Sektor Jasa Keuangan (consumer protection in the financial sector). https://ojk.go.id/id/regulasi/Pages/Pelindungan-Konsumen-dan-Masyarakat-di-Sektor-Jasa-Keuangan.aspx
- OJK, Satgas PASTI press release (Nov 2025). https://www.ojk.go.id/id/berita-dan-kegiatan/info-terkini/Documents/Pages/Satgas-PASTI-Imbau-Masyarakat-Waspadai-Penipuan-Menggunakan-AI/SP-08%20Satgas%20PASTI%20November%202025.pdf

**Kaspersky / BSSN / international comparisons**
- Pasardana, "Kaspersky dan BSSN Perbarui MoU" (13 Apr 2026). https://pasardana.id/news/2026/4/13/kaspersky-dan-bssn-perbarui-mou-untuk-memperkuat-ketahanan-siber-indonesia/
- Kaspersky Fraud Prevention. https://www.kaspersky.com/enterprise-security/fraud-prevention
- ScamShield (Singapore), Kill Switch. https://www.scamshield.gov.sg/kill-switch/
- Seraph Secure (US). https://www.seraphsecure.com/ · Scammer Guardian (US). https://www.scammerguardian.com/about/
