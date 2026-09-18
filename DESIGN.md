---
name: RONDA
description: Duolingo's structure re-tuned to a trusted guardian — one app, two hands, flat faces on solid drops
colors:
  # Light theme (LightColors in ui/theme/Color.kt). Each hue is a family of four:
  # fill, shadow, tint, border. Components take a Tone and receive the family.
  safe: "#1D7F4E"
  safe-shadow: "#155C39"
  safe-tint: "#F0FDF4"
  safe-border: "#6EE7B7"
  danger: "#DC2626"
  danger-shadow: "#991B1B"
  danger-tint: "#FFF1F1"
  danger-border: "#FCA5A5"
  trust: "#1E40AF"
  trust-shadow: "#1E3A8A"
  trust-tint: "#EFF6FF"
  trust-border: "#BFDBFE"
  warn: "#B45309"
  warn-shadow: "#92400E"
  warn-tint: "#FFFBEB"
  warn-border: "#FCD34D"
  text-primary: "#0F172A"
  text-secondary: "#64748B"
  text-muted: "#94A3B8"
  bg: "#FFFFFF"
  surface: "#F1F5F9"
  card: "#FFFFFF"
  border: "#E2E8F0"
  border-strong: "#CBD5E1"
  on-fill: "#FFFFFF"
  # Dark theme (DarkColors). Same families: fills brighten, tints darken.
  dark-safe: "#22C55E"
  dark-safe-shadow: "#16A34A"
  dark-safe-tint: "#052E16"
  dark-safe-border: "#166534"
  dark-danger: "#EF4444"
  dark-danger-shadow: "#B91C1C"
  dark-danger-tint: "#1C0505"
  dark-danger-border: "#7F1D1D"
  dark-trust: "#3B82F6"
  dark-trust-shadow: "#1D4ED8"
  dark-trust-tint: "#0F1C3D"
  dark-trust-border: "#1E3A8A"
  dark-warn: "#F59E0B"
  dark-warn-shadow: "#B45309"
  dark-warn-tint: "#2A1A05"
  dark-warn-border: "#92400E"
  dark-text-primary: "#F1F5F9"
  dark-text-secondary: "#B4C4D4"
  dark-text-muted: "#94A3B8"
  dark-bg: "#0A0F1E"
  dark-surface: "#111827"
  dark-card: "#1E293B"
  dark-border: "#334155"
  dark-border-strong: "#475569"
  # Warning overlay (res/values/colors.xml). Theme-independent; the rest of the
  # overlay reuses the light danger family, card, text-primary and text-secondary.
  overlay-bg: "#B91C1C"
  overlay-text-secondary: "#FECACA"
typography:
  # Guardian scale: Material roles mapped in ui/theme/Type.kt. Sizes in sp as
  # written in code; no font padding, glyphs centred in the line box.
  display:
    fontFamily: "Nunito"
    fontSize: "28sp"
    fontWeight: 900
    lineHeight: "34sp"
    letterSpacing: "-0.02em"
  display-sm:
    fontFamily: "Nunito"
    fontSize: "24sp"
    fontWeight: 900
    lineHeight: "30sp"
    letterSpacing: "-0.01em"
  headline:
    fontFamily: "Nunito"
    fontSize: "22sp"
    fontWeight: 900
    lineHeight: "28sp"
    letterSpacing: "-0.01em"
  headline-md:
    fontFamily: "Nunito"
    fontSize: "20sp"
    fontWeight: 800
    lineHeight: "26sp"
  title:
    fontFamily: "Nunito"
    fontSize: "19sp"
    fontWeight: 800
    lineHeight: "24sp"
  title-sm:
    fontFamily: "Nunito"
    fontSize: "16sp"
    fontWeight: 800
    lineHeight: "22sp"
  title-xs:
    fontFamily: "Nunito"
    fontSize: "14sp"
    fontWeight: 800
    lineHeight: "20sp"
  body:
    fontFamily: "Nunito"
    fontSize: "15sp"
    fontWeight: 700
    lineHeight: "22sp"
  body-sm:
    fontFamily: "Nunito"
    fontSize: "13sp"
    fontWeight: 700
    lineHeight: "18sp"
  caption:
    fontFamily: "Nunito"
    fontSize: "12sp"
    fontWeight: 700
    lineHeight: "16sp"
  button:
    fontFamily: "Nunito"
    fontSize: "16sp"
    fontWeight: 800
    lineHeight: "20sp"
    letterSpacing: "0.05em"
  label:
    fontFamily: "Nunito"
    fontSize: "10sp"
    fontWeight: 900
    lineHeight: "14sp"
    letterSpacing: "0.18em"
  # Large-print tier: every screen the protected person reads (components take large = true).
  large-print:
    fontFamily: "Nunito"
    fontSize: "20sp"
    fontWeight: 700
    lineHeight: "30sp"
  large-print-title:
    fontFamily: "Nunito"
    fontSize: "30sp"
    fontWeight: 900
    lineHeight: "36sp"
    letterSpacing: "-0.02em"
  large-print-label:
    fontFamily: "Nunito"
    fontSize: "18sp"
    fontWeight: 800
    lineHeight: "22sp"
    letterSpacing: "0.04em"
  # Figures
  pairing-code:
    fontFamily: "Nunito"
    fontSize: "40sp"
    fontWeight: 900
    lineHeight: "48sp"
    letterSpacing: "8sp"
    fontFeature: "tnum"
  score-numeral:
    fontFamily: "Nunito"
    fontSize: "34sp"
    fontWeight: 900
    lineHeight: "38sp"
    letterSpacing: "-0.02em"
    fontFeature: "tnum"
rounded:
  badge: "8dp"
  icon-box-sm: "12dp"
  button: "16dp"
  card: "20dp"
  icon-box-lg: "24dp"
  sheet: "28dp"
spacing:
  xs: "4dp"
  sm: "8dp"
  md: "12dp"
  lg: "16dp"
  xl: "20dp"
  2xl: "24dp"
  3xl: "32dp"
components:
  button-tactile:
    backgroundColor: "{colors.safe}"
    textColor: "{colors.on-fill}"
    typography: "{typography.button}"
    rounded: "{rounded.button}"
    padding: "14dp 20dp"
    height: "56dp"
  button-tactile-danger:
    backgroundColor: "{colors.danger}"
    textColor: "{colors.on-fill}"
  button-tactile-trust:
    backgroundColor: "{colors.trust}"
    textColor: "{colors.on-fill}"
  button-tactile-disabled:
    backgroundColor: "{colors.border-strong}"
    textColor: "{colors.on-fill}"
  button-tactile-large:
    typography: "{typography.large-print-label}"
    height: "60dp"
  button-secondary:
    backgroundColor: "{colors.card}"
    textColor: "{colors.trust}"
    typography: "{typography.title-sm}"
    rounded: "{rounded.button}"
    padding: "12dp 20dp"
    height: "52dp"
  button-secondary-danger:
    textColor: "{colors.danger}"
  button-secondary-large:
    typography: "{typography.large-print-label}"
    height: "56dp"
  text-action:
    textColor: "{colors.trust}"
    typography: "{typography.title-sm}"
    rounded: "{rounded.button}"
    padding: "12dp 16dp"
    height: "48dp"
  card:
    backgroundColor: "{colors.card}"
    textColor: "{colors.text-primary}"
    rounded: "{rounded.card}"
    padding: "20dp"
  card-row:
    padding: "16dp"
  card-safe:
    backgroundColor: "{colors.safe-tint}"
  card-danger:
    backgroundColor: "{colors.danger-tint}"
  card-warn:
    backgroundColor: "{colors.warn-tint}"
  card-trust:
    backgroundColor: "{colors.trust-tint}"
  icon-box-sm:
    rounded: "{rounded.icon-box-sm}"
    size: "32dp"
  icon-box-md:
    rounded: "{rounded.button}"
    size: "40dp"
  icon-box-lg:
    rounded: "{rounded.icon-box-lg}"
    size: "48dp"
  badge-danger:
    backgroundColor: "{colors.danger}"
    textColor: "{colors.on-fill}"
    typography: "{typography.label}"
    rounded: "{rounded.badge}"
    padding: "6dp 10dp"
  badge-warn:
    backgroundColor: "{colors.warn}"
    textColor: "{colors.on-fill}"
  badge-safe:
    backgroundColor: "{colors.safe}"
    textColor: "{colors.on-fill}"
  badge-quiet-safe:
    backgroundColor: "{colors.safe-tint}"
    textColor: "{colors.safe}"
  badge-quiet-danger:
    backgroundColor: "{colors.danger-tint}"
    textColor: "{colors.danger}"
  # Dark-theme badges only. A badge's 10sp label needs 4.5:1, which the vivid
  # dark fills do not give; buttons keep those fills (their 16sp+ labels are
  # large text). See The White-on-Fill Rule.
  badge-danger-dark:
    backgroundColor: "{colors.dark-danger-shadow}"
    textColor: "{colors.on-fill}"
  badge-warn-dark:
    backgroundColor: "{colors.dark-warn}"
    textColor: "{colors.dark-warn-tint}"
  input:
    backgroundColor: "{colors.card}"
    textColor: "{colors.text-primary}"
    typography: "{typography.title-sm}"
    rounded: "{rounded.button}"
    padding: "14dp 16dp"
    height: "56dp"
  input-large:
    typography: "{typography.large-print}"
    height: "60dp"
  chip:
    backgroundColor: "{colors.card}"
    textColor: "{colors.text-primary}"
    typography: "{typography.title-sm}"
    rounded: "{rounded.icon-box-sm}"
    padding: "12dp 18dp"
    height: "48dp"
  chip-selected:
    backgroundColor: "{colors.trust-tint}"
    textColor: "{colors.trust}"
  top-bar:
    padding: "10dp 20dp"
    height: "64dp"
  icon-action:
    backgroundColor: "{colors.surface}"
    textColor: "{colors.text-primary}"
    rounded: "{rounded.button}"
    size: "44dp"
  bottom-nav:
    backgroundColor: "{colors.card}"
    padding: "6dp 12dp"
    height: "64dp"
  nav-item:
    backgroundColor: "{colors.card}"
    textColor: "{colors.text-muted}"
    typography: "{typography.label}"
    rounded: "{rounded.icon-box-sm}"
    padding: "6dp 0"
    height: "52dp"
  nav-item-active:
    backgroundColor: "{colors.trust-tint}"
    textColor: "{colors.trust}"
  section-label:
    textColor: "{colors.text-secondary}"
    typography: "{typography.label}"
    padding: "20dp 20dp 10dp"
  sheet:
    backgroundColor: "{colors.card}"
    textColor: "{colors.text-primary}"
    rounded: "{rounded.sheet}"
    padding: "0 24dp 32dp"
  overlay:
    backgroundColor: "{colors.overlay-bg}"
    textColor: "{colors.on-fill}"
    padding: "56dp 24dp 48dp"
---

# Design System: RONDA

## Overview

This file records the RONDA visual system **as built** — the tokens in `app/src/main/java/com/ronda/app/ui/theme/`, the components in `ui/components/`, the stroke icons and the warning overlay under `app/src/main/res/`. The team-authored brief it implements is `docs/DESIGN.md`; that file is the source and stays as written. Where the build extends the brief (amber, the large-print tier, the overlay palette) or departs from it in a value, this document records the build and notes the departure in parentheses, so the two can be reconciled deliberately rather than drift apart.

**Creative North Star: "The Trusted Guardian"** *(inferred from docs/DESIGN.md §1, which takes "Duolingo's structural DNA" and re-tunes it "from playful classroom to trusted guardian")*

RONDA borrows the structure of a learning app — chunky buttons that physically press, cards with thick borders, heavy rounded type — and drains the classroom out of it. Nothing is neon, nothing is a mascot, nothing is a gradient. The green is a forest green, the type is Nunito at bold weights and heavier, the icons are a single 2.2dp round stroke, and every surface is flat: depth is a solid drop in the border colour, never a blur. The result is meant to feel warm enough for an elderly parent and serious enough for a security tool — the two things a scam-defence app has to be at once.

The system is built for two hands *(direction contract)*. The guardian's screens are a dense, blue-chromed control room: a status hero, lists of flagged apps with badges and consequence sentences, a bottom bar with an active trust-tint pill. The protected person's screens are one green card that asks nothing: 30sp title, 20sp body, a filled shield, two status rows, and a privacy card below — never a destructive button, never a technical string. The same components serve both; they switch tiers with a single `large = true` flag rather than becoming different components.

Colour is spent, not decorated. Green means guarded, red means flagged, blue means the guardian's own chrome, and amber (the build's one addition to the brief) means "not yet" — a permission still off, a warning-band score. Quiet rows read quiet so loud rows stay credible. The build refuses the antivirus dashboard (threat meters, red everywhere, "scan now") and the wellness-app softness that would make a security tool look like a habit tracker; both rejections are confirmed in the direction contract.

**Key Characteristics:**
- Flat faces on solid drops: a 2.5dp border and a 4dp drop under every card, a 5dp drop under every primary button; no blur shadows, no tonal elevation, no gradients, no glass.
- Tactile press as the signature motion: the face travels 5dp (cards 4dp) onto its drop in 90ms and springs back.
- One typeface at heavy weights: Nunito 700 is the *body* weight; 800 for titles and buttons; 900 for headlines, badges and numerals.
- Semantic colour families of four (fill / shadow / tint / border) — components take a `Tone`, never a colour.
- Two type tiers: a Material-mapped guardian scale (15sp body) and a LargePrint tier for the protected person (20sp body, 30sp title, 18sp button labels).
- Custom stroke icons only — 24dp grid, 2.2dp round stroke, tinted at use; no emoji anywhere.
- Light and dark, where dark is deep navy (`#0A0F1E`), never black; semantic colours keep their meaning in both.
- Nothing square: minimum 8dp radius on any container.

## Colors

A small set of saturated semantic hues on paper white or deep navy, each hue carried as a family of four (fill, shadow, tint, border) so a button, a badge and a state card all speak the same colour at different volumes.

### Primary
- **Forest Green** (#1D7F4E, family `safe-shadow` / `safe-tint` / `safe-border`): the primary action and the "guarded" state. Fills every progress or confirmation button, the RONDA wordmark box, the filled shield on a protected home that is fully set up, the "Semua aman" hero, the AMAN badge and the "terhubung" connection pill. In dark mode the fill brightens to **Vivid Green** (#22C55E) and the tint drops to a near-black green (#052E16).
- Mapped to Material `primary` / `primaryContainer`, so any Material component that reaches for the primary role lands in this family.

### Secondary
- **Trust Blue** (#1E40AF, family `trust-shadow` / `trust-tint` / `trust-border`): the guardian's chrome and anything "chosen". Active bottom-nav pill, selected chips and choice cards, the radio dot, the focused text-field border and caret, the avatar box on the guardian's top bar, the text of secondary buttons and text actions ("Tandai aman", "Nanti saja"), and the confirm button of sheets that keep protection. Dark: **Trust Blue Dark** (#3B82F6) on a navy tint. Mapped to Material `secondary`.

### Tertiary
- **Danger Red** (#DC2626, family `danger-shadow` / `danger-tint` / `danger-border`): spent on exactly two things — an app that has been flagged (DARURAT badge, red state card, red hero, "Bukan dari Play Store" value, red explanation labels) and the uninstall action ("Hapus aplikasi ini" tactile button, on both phones). The bell's unread count is the one other red mark. Unpairing is deliberately *not* red: it is reversible with a new code, so it is an ordinary outline action behind a confirmation. Dark: #EF4444. Mapped to Material `error`.
- **Amber** (#B45309, family `warn-shadow` / `warn-tint` / `warn-border`) *(name inferred; the family is the build's extension to the brief, which has no "not yet" colour)*: the PERINGATAN/WASPADA band, a permission still off, the offline card, the "continue setup" state of the protected home, and the override notice ("Ibu tetap membuka aplikasi ini…"). Light amber is a burnt, brownish orange so white text on it still reads (5.0:1); dark amber (#F59E0B) is the bright Tailwind amber. Mapped to Material `tertiary`.

### Neutral
- **Ink** (#0F172A): headings, titles, card values, body on tinted cards, the scrim. Dark: **Ghost White** (#F1F5F9).
- **Slate** (#64748B): descriptions, meta lines ("85/100 · 12 menit lalu"), section labels, info-row labels, sheet bodies. Also the NEUTRAL tone's fill (icons in a neutral icon box). Dark: **Slate Light** (#B4C4D4).
- **Muted** (#94A3B8): inactive nav items, disabled secondary-button text and field placeholders only — at 2.6:1 on white it is too light for a sentence, which is why the info-row label uses Slate (the brief's §6.4 said Muted; the build chose Slate). Same value in both themes.
- **Paper White** (#FFFFFF) is the page and, as `card`, every card face; **Smoke** (#F1F5F9) is the NEUTRAL tint (icon-action boxes, neutral icon boxes, skeleton bars). Dark: **Deep Navy** page (#0A0F1E), **Surface Dark** (#111827), **Card Dark** (#1E293B).
- **Border** (#E2E8F0) is every neutral edge and every neutral drop: cards, chips, fields at rest use it; **Border Strong** (#CBD5E1) is the unfocused field border, the unselected radio ring, the NEUTRAL shadow and the disabled button fill. Dark: #334155 / #475569 (the dark strong border is not in the brief's table).
- **On-fill** (#FFFFFF): white text and icons on any filled semantic colour, in both themes.

### Warning overlay
The overlay (`res/layout/overlay_warning.xml`) is theme-independent — an alarm looks the same at noon and at midnight. It is drenched in a deep red ground (#B91C1C, the value the dark theme uses as its danger shadow) with white primary text and a rose secondary (#FECACA, 4.5:1 on the ground); the card on it is the light system card — white face, `danger-border` edge and drop, `danger-tint` icon box, `danger` glyph and title, Ink and Slate text. Neither the ground nor the rose appear in the brief; they exist only here.

### Named Rules
**The Family-of-Four Rule.** Every semantic hue ships as fill, shadow, tint and border, and a component asks for a `Tone` (SAFE, DANGER, TRUST, WARN, NEUTRAL), never a colour. A tactile button is fill over shadow; a state card is tint inside border; a quiet badge is tint, border and fill-coloured text. If a new colour cannot be expressed as this family it does not belong in the system.

**The One Red Rule.** Red is spent only on a flagged app and on the uninstall action. "Not yet" states — a permission still off, a PERINGATAN score, a lost connection — take amber, so the one red that matters stays alarming. Below the guardian threshold (score < 60) a row is a plain card with a quiet badge and no consequence sentence: quiet must read quiet.

**The Never-Black Rule.** Dark mode sits on deep navy (`#0A0F1E`), cards on `#1E293B`; `#000000` is never used as a ground. Fills brighten and tints darken so meaning survives the switch; dynamic (wallpaper-derived) colour is off because it would repaint green and red.

**The White-on-Fill Rule.** Text and icons on a filled semantic colour are white — in both themes on a *button*, whose label is 16sp or larger and clears the large-text bar. Known trade-off carried from the brief (§9.5), not a target: on the dark fills white measures 2.3:1 on green (`#22C55E`) and 3.7:1 on blue; the light fills all clear 4.8:1. Flagged for the team in the direction contract.

A **badge** is the exception, because its label is 10sp and owes 4.5:1. Two functions in `Color.kt` carry it, and components ask them rather than reaching for a colour: `onFill(tone)` returns the family's deep-brown `warn-tint` as ink on an amber fill in the dark theme (≈10:1) and white everywhere else; `badgeFill(tone)` puts a dark-theme danger badge on `danger-shadow` (`#B91C1C`, 6.4:1 with white) while the button keeps the vivid `#EF4444`. Light-theme badges are unchanged.

## Typography

**Display Font:** Nunito (variable TTF bundled at `res/font/nunito.ttf`; weights 700, 800 and 900 instantiated through `FontVariation`)
**Body Font:** Nunito (same file)
**Label/Mono Font:** none distinct — figures use Nunito with tabular numerals (`tnum`)

**Character:** One rounded humanist face, used only at bold weights and heavier. The rounded terminals at 700–900 are what let the type read as friendly instead of loud, and hold up at 20sp on an older person's phone. Font padding is off and glyphs are centred in their line box, so buttons and badges are sized from the numbers below rather than from platform slack. The font ships in the APK (offline demo); there is no runtime fallback stack.

### Hierarchy

Guardian screens use the Material-mapped scale (`Typography` in `Type.kt`); the roles below name the slots the build actually reaches for.

- **Display** (900, 28sp/34sp, −0.02em; `displayMedium`): screen greetings and onboarding titles. **Display-sm** (900, 24sp/30sp, −0.01em; `displaySmall`): titles of large-tier sheets.
- **Headline** (900, 22sp/28sp, −0.01em; `headlineLarge`): the hero card title ("3 aplikasi perlu diperiksa") and the app name in the alert detail header. **Headline-md** (800, 20sp/26sp; `headlineMedium`): the wordmark (with 0.06em tracking), sheet titles, large-tier choice-card titles, and every top-bar title — the same bar heads protected screens, where nothing may fall under 20sp. **Headline-sm** (800, 19sp/24sp; `headlineSmall`): empty-state titles — identical in value to Title, so a Material component reaching for either lands in the same place.
- **Title** (800, 19sp/24sp; `titleLarge`): app names in list rows, choice-card titles. **Title-sm** (800, 16sp/22sp; `titleMedium`): the workhorse — secondary-button and text-action labels, field text, chips, option rows. **Title-xs** (800, 14sp/20sp; `titleSmall`): info-row values.
- **Body** (700, 15sp/22sp; `bodyLarge`): card body, explanation sentences, sheet bodies. **Body-sm** (700, 13sp/18sp; `bodyMedium`): the first consequence sentence in a list row (two lines max), option-row subtitles, meta lines (as **Tabular**, `bodyMedium` + `tnum`). **Caption** (700, 12sp/16sp; `bodySmall`): rare; hints.
- **Button** (800, 16sp/20sp, +0.05em, UPPERCASE; `labelLarge`): the tactile button's label. **Label** (900, 10sp/14sp, +0.18em, UPPERCASE; `labelSmall`): badges, nav labels, section labels, info-row labels, explanation labels, "SKOR RISIKO". Guardian screens only — see The Large-Print Floor Rule.
- Defined but unused by any screen: `displayLarge` (32sp) and `labelMedium` (12sp/800/0.02em).

Protected-person screens (home, pairing, setup wizard, uninstall prompt, onboarding) use the **LargePrint** tier, and shared components switch to it with `large = true`:

- **LargePrint title** (900, 30sp/36sp, −0.02em): the one sentence on the status card ("HP ini sedang dilindungi").
- **LargePrint** (700, 20sp/30sp): every sentence the protected person reads — 1.5 line height so long Indonesian sentences do not stack into a wall. The PRD floor for these screens is 20sp (the brief's §8.1 said 15sp body / 27sp display; the build follows the PRD).
- **LargePrint label** (800, 18sp/22sp, +0.04em): buttons on those screens — larger than the guardian's 16sp, same voice.

Figures:
- **Pairing code** (900, 40sp/48sp, 8sp tracking, `tnum`): the six-character code on both phones — wide enough to read aloud one character at a time, tabular so it never jitters.
- **Score numeral** (900, 34sp/38sp, −0.02em, `tnum`): the risk score in the alert header, in the band's fill colour, followed by "/100" in Slate.

The overlay, in View form, sets the same voice by hand: brand 28sp/900 with 0.06 tracking, title 26sp/900 in red, app label 24sp/900, body 20sp/700 at 1.3 line spacing, reason label 11sp/900 upper-case at 0.18 tracking, reason 18sp/700, guardian line 20sp/800, exit hint 17sp/700.

### Named Rules
**The Bold-Is-Body Rule.** 700 is the lightest weight in the system. Nothing is set regular or medium; hierarchy comes from size, weight steps of 700 → 800 → 900, and colour (Ink vs Slate), not from thin type.

**The Large-Print Floor Rule.** No text on a screen the protected person reads falls below 20sp — including field labels, step counters and the warning overlay. The 10sp `label` step, and the label/value info row built on it, are guardian-only devices; a protected screen states the same fact as a whole sentence at 20sp ("Pengawasan aktif", "Dijaga oleh Rina") rather than as a label over a value.

**The Upper-Case-Is-Label Rule.** Upper case with +0.18em tracking belongs to labels, badges, nav and section headings only (the tactile button is upper case at +0.05em). Everything else is sentence case. Components apply `.uppercase()` at render time; strings are authored in sentence case so translations stay clean.

**The Large-Print Rule.** Any screen the protected person reads is set in the LargePrint tier — 20sp body minimum, 30sp title, 18sp button labels — by passing `large = true` to the shared components, never by building a second component.

**The Tabular Rule.** Anything numeric that sits in a column or is compared — scores, times, the pairing code — is set with `tnum` so 96 and 100 share a column and a ticking time does not jitter.

## Layout

Phone only, portrait, one column. Every screen pads its own insets (`screenInsets()` = safe-drawing insets; lists pay the status bar once at the top and the bottom bar pays the navigation bar), so scrolled content never runs under the clock.

- **Gutters:** guardian screens use a 20dp horizontal gutter (top bar, hero, list rows, section labels); protected-person and onboarding screens use 24dp (the brief's single 24dp value; the build split it by role).
- **Card rhythm:** cards in a list sit 12dp apart (6dp vertical padding on each row); the first card sits 8dp below the top bar; lists end with 24dp of bottom padding. Section labels get 20dp above and 10dp below.
- **Card interiors:** 20dp padding on hero and status cards, 16dp on list rows and explanation cards. Inside a card: icon box, then a 12–16dp gap (12dp beside a 32dp box, 14dp beside 40–44dp, 16dp beside 48–60dp), then text; 4–6dp between a title and its body, 8dp before a meta line.
- **Chrome:** top bar 64dp minimum (10dp vertical, 20dp horizontal padding; leading slot + 12dp gap + title + trailing slot); bottom nav 64dp minimum plus the navigation-bar inset, 12dp horizontal padding, three items spaced evenly.
- **Sheets:** 24dp side padding, 32dp bottom padding; title, 10dp, body, 24dp, primary button, 12dp, secondary button.
- **Protected home:** exactly two cards — the status card and the privacy card — 16dp apart, on 24dp gutters with 12dp vertical padding; nothing else on screen.
- **Spacing scale:** the brief's 4dp scale (4 / 8 / 12 / 16 / 20 / 24 / 32) is the backbone; the build also uses 6, 10, 14 and 28dp for intra-component gaps, so in practice the grid is 2dp with 4dp anchors.
- **Touch targets:** 56dp primary button (60dp large), 52dp secondary (56dp large), 56dp option rows, 52dp nav items, 48dp text actions and chips, 44dp icon-action buttons.
- **Density by role:** guardian medium-high (hero + two lists + nav on one screen, every row two-line title-safe at large font scales); protected minimal (one decision at most, usually none).

## Elevation & Depth

No blur shadows and no tonal elevation anywhere. Depth is a **solid drop**: a second rounded rectangle in the edge colour drawn under the face and offset downward — 4dp under a card, 5dp under a button — with the face carrying a 2.5dp border in that same colour. The drop reads as thickness, not as light, which is what keeps surfaces flat and the world un-glassy. The top bar has no elevation and sits directly on the page colour; the bottom nav is separated from content by a 2.5dp rule in the border colour, not a shadow. Modal sheets use Material's scrim (Ink at Material's default alpha) over the page. Dark mode uses the same drops in the dark family colours.

### Shadow Vocabulary
- **Card drop** (`box-shadow: 0 4px 0 <border colour>` equivalent; `RondaDepth.card = 4dp`): under every card, in the card's edge colour — `border` for a neutral card, the tone's border for a state card, `dangerBorder` under the overlay card.
- **Button drop** (`box-shadow: 0 5px 0 <shadow colour>` equivalent; `RondaDepth.button = 5dp`): under tactile buttons in the tone's *shadow* colour (a darker fill), under secondary buttons in the edge colour (`border`, or `dangerBorder` for the red variant).
- **No other shadow exists.** Icon boxes, chips, fields, badges and nav pills are border-only.

### Named Rules
**The Solid-Drop Rule.** If something needs to look raised, it gets a solid offset drop in its own edge colour and a matching border. Never a blurred shadow, never Material elevation, never a gradient edge.

**The Press-Travels Rule.** Anything pressable travels onto its drop: buttons 5dp, tappable cards 4dp, in 90ms with fast-out-slow-in easing, no ripple (`indication = null`). A disabled button keeps its shape and drop and loses only its colour, so layouts never jump when a field becomes valid.

## Shapes

Nothing is square. Corners are large and consistent, and the radius follows the role of the box rather than being chosen per screen: badges and quiet pills 8dp; small icon boxes (32dp), chips and the nav pill 12dp; buttons, fields, icon-action buttons and medium icon boxes (40–44dp) 16dp; cards 20dp; large icon boxes (48dp and up: hero shields, empty states) 24dp; bottom sheets 28dp on the top corners only. Material's shape roles are mapped to the same values (small 8, medium 16, large 20, extraLarge 28).

Borders are thick and part of the form: 2.5dp on cards, secondary buttons, fields, chips and the nav's top rule; 2dp on icon boxes, icon-action buttons and the active nav pill; 1.5dp on quiet badges and card dividers. Fully round shapes are reserved for the few things that are genuinely dots: the 24dp radio ring with its 12dp dot, the 18dp unread count on the bell (with a 2dp ring in the card colour so it reads over the icon), and the 10dp-tall progress segments (5dp radius, i.e. a pill).

Icons share the geometry: a 24dp grid, outline only, 2.2dp stroke, round caps and round joins, drawn once as vector drawables so Compose, the overlay layout and notification icons pull the same file. Default icon size is 22dp (nav, buttons, icon-action), 20dp in secondary buttons, 16dp inline in a meta line, 12dp inside a badge; an icon inside an icon box is half the box, never below 16dp. (The brief allows a 2.5dp stroke for CTA icons; the build draws every icon at 2.2dp.)

### Named Rules
**The Nothing-Square Rule.** Minimum radius on any container is 8dp; there is no 0dp corner in the system, and no "sharp" variant of anything.

**The Radius-Follows-Size Rule.** An icon box picks its own radius from its size (≥48dp → 24dp, ≥40dp → 16dp, smaller → 12dp). Do not pass a radius; pass a size.

## Components

The shared vocabulary lives in `ui/components/` (`Tactile.kt`, `Cards.kt`, `Chrome.kt`, `Fields.kt`, `RondaIcons.kt`). Every component takes a `Tone` and a `large` flag; none takes a raw colour.

### Buttons
Tactile and confident: a face resting on a solid shadow, pressed down under the thumb.
- **Shape:** generously rounded (16dp), full width, centred label with an optional leading icon (22dp, 10dp gap).
- **Primary — TactileButton:** tone fill over the tone's shadow colour (default SAFE; DANGER only for uninstall; TRUST for guardian confirmations); white upper-case label (`button`, 16sp/800/+0.05em); 14dp vertical and 20dp horizontal padding inside a 56dp minimum height (the brief wrote 16dp vertical; the minimum height carries the size). Large tier: 60dp minimum, `large-print-label` (18sp). The press is felt as well as seen: one `HapticFeedbackType.Confirm` tick fires on release.
- **Press:** the face travels 5dp onto the drop in 90ms and returns; no ripple. **Loading:** a 20dp white spinner with a 2.5dp stroke replaces the icon and the button stops responding. **Disabled:** fill becomes Border Strong and the drop becomes Border — shape and height unchanged.
- **Secondary — SecondaryButton:** card-coloured face, 2.5dp border and 5dp drop both in Border (Danger Border for the red variant, which is reserved for a destructive action and is therefore unused in the shipped screens), label in the tone's fill colour (`title-sm`, 16sp/800, sentence case — the brief wrote 14sp), 12dp vertical / 20dp horizontal padding, 52dp minimum (56dp large, 18sp label). Same press travel. It carries its tone in the text only, so it never competes with the one filled button on the screen. Disabled: Muted text.
- **Tertiary — TextAction:** the quiet third option ("Nanti saja", "Lewati"): trust-blue `title-sm` text on nothing, 48dp minimum touch height, 12dp/16dp padding, 16dp clip for the press highlight.

### Chips
- **Style:** a bordered pill of `title-sm` text — card face, 2.5dp Border, 12dp radius, 48dp minimum height, 12dp vertical / 18dp horizontal padding. Radio semantics (one nickname among several).
- **State:** selected turns the face Trust Tint, the border Trust and the text Trust; unselected is Ink on card. No shadow.

### Cards / Containers
- **Corner Style:** 20dp.
- **Background:** card colour for a neutral card; the tone's tint for a state card (Safe Tint for "guarded", Danger Tint for a flagged app, Warn Tint for "not yet" and offline, Trust Tint for a selected choice).
- **Shadow Strategy:** the 4dp solid card drop in the edge colour (see Elevation & Depth).
- **Border:** 2.5dp in Border, or the tone's border colour.
- **Internal Padding:** 20dp by default; 16dp for list rows and explanation cards.
- **Behaviour:** the face and edge crossfade between tones in 260ms — this is how the protected person sees a permission land without reading anything. With an `onClick` the card presses like a button (4dp travel); a row in a list is the same object as a button, only wider.
- **IconBox:** the pictogram container used everywhere (32dp beside an info row, 40–44dp on rows and chrome, 48dp on choice cards, 60–80dp as a hero). Tinted by default (tone tint face, 2dp tone border, tone-coloured glyph); `filled` for the wordmark, the check on a selected choice card and the hero of a fully-safe state (tone fill face and border, white glyph). Colours crossfade in 260ms.
- **CardDivider:** a 1.5dp hairline in Border or the tone's border (the brief wrote 1dp).

### Status Badge
- **Filled:** `badgeFill(tone)` face, `onFill(tone)` upper-case `label` text (10sp/900/+0.18em), 8dp radius, 6dp vertical / 10dp horizontal padding (the brief wrote 12dp), optional 12dp leading icon with a 6dp gap. Used for the loud bands (DARURAT in red, PERINGATAN in amber) and "sudah dihapus". In the dark theme the amber badge takes brown ink and the red badge sits on `danger-shadow`; see The White-on-Fill Rule.
- **Quiet** (`filled = false`, the build's addition): tone tint face, 1.5dp tone border, tone-coloured text. Used for information rather than alarm — the RENDAH band, "terhubung", a decided row's outcome.

### Info Row and Section Label
- **InfoRow:** a 32dp icon box, 12dp gap, then an upper-case `label` in Slate over a `title-xs` value in Ink (3dp apart). The value takes a tone colour only when it *is* the finding ("Bukan dari Play Store" in red). Guardian screens only; the protected home says the same thing as a **StatusLine** — a 40dp icon box, 12dp gap, one `large-print` sentence.
- **SectionLabel:** upper-case `label` in Slate, 20dp above and 10dp below, heading each list ("PERLU DIPERIKSA", "KENAPA DITANDAI").

### Inputs / Fields
- **Style — RondaTextField:** a bordered box in the button's shape (16dp), card face, 2.5dp Border Strong edge, 56dp minimum (60dp large), 14dp vertical / 16dp horizontal padding, `title-sm` Ink text (LargePrint on protected screens), Muted placeholder. Built on `BasicTextField`; Material's floating label and indicator line are deliberately absent.
- **Focus:** the border and the caret turn Trust Blue. No glow, no shadow.
- **Error / Disabled:** no distinct styling exists yet; validity is expressed by the button below enabling.

### Navigation
- **Top bar — RondaTopBar:** 64dp minimum, no elevation, on the page colour; leading slot (a 44dp icon box or icon-action button, or the wordmark), `headline-md` title in Ink, trailing slot (a quiet badge or icon-action button). **IconActionButton** is a 44dp tinted box (Smoke face, 2dp Border, 16dp radius, 22dp Ink glyph; a tone gives it the tone's tint/border/glyph) for back, profile and close.
- **Bottom bar — GuardianBottomNav:** card-coloured, a 2.5dp Border rule on top, 64dp minimum plus the navigation-bar inset, three `NavItem`s spaced evenly. **Active:** a pill (12dp radius) in Trust Tint with a 2dp Trust Border, 22dp glyph and upper-case `label` in Trust. **Inactive:** same pill with an invisible card-coloured border, glyph and label in Muted. Colours animate in 160ms. **Unread count:** an 18dp Danger circle with a 2dp card-coloured ring at the bell's top-right, `label` digits in white, capped at "9+". Tabs crossfade (fade in 200ms after a 40ms delay, fade out 80ms).
- **Wordmark:** a 32dp filled Safe icon box with the shield-check, 10dp gap, "RONDA" in `headline-md` at 0.06em in Ink.

### Sheets and Choices
- **ConfirmSheet:** Material `ModalBottomSheet` with 28dp top corners and the card colour; used for the actions that *reduce* safety (mark safe, unpair, rename) — friction belongs there and never on the actions that keep protection. Title `headline-md` (Display-sm when large), body in Slate, a tactile confirm (TRUST by default) and a secondary cancel; the sheet slides away before the caller removes it.
- **OptionRow:** one choice among a few (theme, language): a 40dp icon box that goes Trust when selected, `title-sm` title (LargePrint when large), optional `body-sm` subtitle, and a drawn **RadioDot** (24dp, 2.5dp ring in Trust or Border Strong, 12dp Trust dot; 160ms). Never a bare Material radio.
- **ChoiceCard:** a pressable card that takes the Trust tint when chosen and shows a 28dp filled Trust check; 48dp icon box, `title` (headline-md when large), body in Slate. The answer is committed by the button below, not by the tap.
- **Bottom sheets** open at their content height (`skipPartiallyExpanded`) and scroll when tall, so the close button never hides under the gesture bar.
- **SegmentedProgress:** the Duolingo path, flattened — equal 10dp-tall segments with 6dp gaps that fill Safe (320ms) as each permission lands.
- **SkeletonCard / EmptyState:** a neutral card with a 44dp box and three Smoke bars (8dp radius, the system's floor) pulsing between 45% and 100% alpha (900ms, reversing) while the first alerts arrive; an empty list shows a 64dp icon box, a `headline-sm` title and a Slate body, centred, saying what the emptiness means.

### Signature: Alert Row and Hero
- **AlertRow** (`ui/guardian/components/AlertRow.kt`): one app in the list. An open DARURAT row is a red state card, an open PERINGATAN row an amber one; a RENDAH row and any decided row are plain cards with a quiet badge. Layout: 44dp icon box (the band's pictogram: triangle for DARURAT, shield-alert for PERINGATAN, app box for RENDAH, check when removed) + 14dp, then the app name in `title` at full width (two lines, ellipsised) with the badge on its own line 6dp beneath it — a badge beside the name forces long names to wrap, and history's outcome badges are longer still — then the first consequence sentence in `body-sm` Slate, cut at its first full stop so it is always one complete thought (only at or above the guardian threshold of 60), a tabular meta line ("85/100 · 12 menit lalu · Menunggu keputusan Anda"), and, when the protected person reopened the app, an amber override line with a 16dp eye glyph — the mechanism made visible, so it gets the accent.
- **HeroCard:** the first thing the guardian sees — a state card in Safe ("Semua aman", filled shield-check) or Danger ("N aplikasi perlu diperiksa", filled triangle) with a 60dp icon box, `headline` title and Slate body; neutral with an outline shield while loading.
- **Alert detail header:** the same state card with a 60dp icon box, the badge above the app name (`headline`), the package name in `body-sm` Slate (the guardian may see technical strings; the protected person never does), the score numeral in the band's colour beside "/100" and an upper-case "SKOR RISIKO" label (the three read to TalkBack as one label, "Risiko 100 dari 100, Darurat"), a tone divider, and info rows for install source (value in red when not from the Play Store) and install time. Below it, explanation cards (16dp padding, 40dp icon box, upper-case label in the signal's tone, `body` in Ink) and finally the one filled button: "Hapus aplikasi ini" in Danger with a trash glyph, its honest caption ("Ibu akan diminta menekan 'Hapus' di HP-nya…") in Slate, and "Tandai aman" as a secondary button.

### Signature: Protected Status Card
One card, zero decisions. An 80dp icon box (filled Safe shield-check when every permission is on; tinted Warn shield-alert when not), 20dp, a centred `large-print-title`, 10dp, a centred `large-print` body in Ink, 20dp, then either a Safe divider and two Safe info rows ("PENGAWASAN — Aktif", "PENJAGA — Terhubung", 14dp apart) or a single large tactile button ("Lanjutkan pengaturan"). The card crossfades from amber to green when the last permission lands. Beneath it a neutral card with a 44dp lock box states the privacy promise in `large-print`. The profile sheet above it is information only — who is guarding, and the pairing code in `pairing-code` — and closes with a large secondary button.

### Signature: The Warning Overlay
The block in View form (`overlay_warning.xml`, `bg_overlay_card.xml`, `bg_overlay_icon.xml`), drawn over a flagged app through `SYSTEM_ALERT_WINDOW`. A scrollable red ground (`overlay-bg`) with 24dp gutters, 56dp top and 48dp bottom padding; the wordmark in white (40dp shield-check, "RONDA" 28sp/900) with the "Pelindung Keluarga" tagline in rose beneath it, so the person knows it is RONDA and not the phone; then the system card — white face, 20dp radius, 2.5dp Danger Border and a 4dp drop in the same colour, 24dp padding — holding a 72dp danger-tinted icon box (24dp radius, 2dp border, red triangle), the title "Jangan buka aplikasi ini" in red 28sp/900 (sentence case, `breakStrategy="balanced"` so it never leaves a one-word orphan), the app label 24sp/900 in Ink, the consequence in 20sp/700, a 1.5dp Danger Border divider, and the reason under a 20sp/800 Slate label. Below the card, in white 20sp/800, that the guardian has been told, and in rose 20sp the only exit: the Home button. Nothing on the overlay falls below 20sp, and the window sets `fitInsetsTypes = 0` with `LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS` so the red field runs edge to edge under the status bar — one surface, not a panel under a system band. There is no "continue anyway" button, and nothing on it may resemble a system dialog.

## Do's and Don'ts

### Do:
- **Do** give every action a `Tone` and let the family follow: SAFE for progress and confirmation, DANGER only for uninstall, TRUST for guardian confirmations and anything "chosen", WARN for "not yet", NEUTRAL for information.
- **Do** put one filled tactile button on a screen and make every other action a secondary button or a text action; the filled one is the decision.
- **Do** build every raised surface as a face with a 2.5dp border on a solid 4dp (card) or 5dp (button) drop in the edge colour, and let it travel onto the drop when pressed.
- **Do** set protected-person screens in the LargePrint tier by passing `large = true` — 20sp body, 30sp title, 18sp button labels — and keep them to one status card, one privacy card, and at most one action that keeps protection.
- **Do** keep quiet rows quiet: below the guardian threshold a row is a plain card with a quiet badge and no consequence sentence.
- **Do** use `RondaIcons` only — 24dp grid, 2.2dp round stroke, tinted from context (white on a fill, the tone's colour on a tint, Ink on Smoke) — at 22dp by default, inside an icon box whose radius follows its size.
- **Do** upper-case labels, badges, nav and button text in the component (`.uppercase()`), and author strings in sentence case in `values/` (English) and `values-in/` (Indonesian) so translations and TalkBack stay clean.
- **Do** put friction (a `ConfirmSheet`) on actions that reduce safety — mark safe, unpair — and none on actions that keep it.
- **Do** keep every word on a protected-person screen at 20sp or larger, the warning overlay included, and state facts there as whole sentences rather than as a 10sp label over a value.
- **Do** set figures that get compared or tick — scores, times, the pairing code — in tabular numerals.
- **Do** brand the overlay as RONDA (wordmark, tagline, the system card) and keep it theme-independent and exit-less.

### Don't:
- **Don't** use Duolingo's neon green (`#58CC02`) or any colour outside the semantic families; a colour that cannot be expressed as fill/shadow/tint/border does not belong.
- **Don't** spend red on anything but a flagged app and the uninstall action; "not yet" is amber.
- **Don't** add gradients, blur, glass, Material elevation or blurred shadows — depth is a solid drop, surfaces are flat.
- **Don't** use emoji, glyph fonts or Material's default icons; every pictogram is a `RondaIcons` stroke drawable.
- **Don't** set any container below an 8dp radius, or any text below weight 700.
- **Don't** put white on a dark-theme *badge* fill: ask `badgeFill(tone)` and `onFill(tone)`, which give the amber badge brown ink and the danger badge the deeper `#B91C1C`. Buttons keep the brief's vivid fills.
- **Don't** use Muted (`#94A3B8`) for a sentence or a label the reader must read — it is for inactive nav items and placeholders only.
- **Don't** use `#000000` as a ground, and don't enable dynamic colour; dark mode is deep navy and the semantic hues keep their meaning.
- **Don't** reach for Material's `TextField`, `RadioButton`, `Button` or `Card` — the system's own field, radio dot, tactile button and card exist so nothing on screen arrives in Roboto or baseline purple.
- **Don't** show a protected person a technical string (package name, permission name), a destructive button, a pairing change, or more than two cards on one screen.
- **Don't** let the overlay mimic a system dialog or offer a "continue anyway" — the only exits are the Home button and the guardian marking the app safe.
