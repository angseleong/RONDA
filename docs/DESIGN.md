# RONDA — Design System
> Duolingo structure, calibrated for a security app used by elderly users

**Theme:** light + dark  
**App:** RONDA — Real-time On-Device Detection Agent  
**Target users:** Protected = elderly/non-technical · Guardian = tech-literate adult  
**Platform:** Android, Jetpack Compose (Material 3 custom theme)

---

## 1. Design Philosophy

RONDA takes **Duolingo's structural DNA** (chunky tactile buttons, card-based layout, thick borders, bold rounded type) and **re-tunes its voice** from "playful classroom" to "trusted guardian". The result feels warm and approachable for orang tua, without looking like a game.

| Duolingo original | RONDA adjustment | Reason |
|---|---|---|
| Neon green `#58CC02` as primary CTA | Forest green `#1D7F4E` | Security/safety feel, less "toy-like" |
| Playful emoji and mascot illustrations | Custom thick-stroke SVG icons | Premium, consistent, accessible |
| Single role UI | Two role UIs (Protected / Guardian) | Different users, different needs |
| Always light mode | Light + Dark mode | App used at night (dark conditions) |

**Core rule:** The Protected screen (HP Orang Tua) must require zero decisions from the user. The Guardian screen can be information-dense.

---

## 2. Color System

### 2.1 Semantic Roles

| Token | Role |
|---|---|
| `color-safe` | Primary action, safe status, progress |
| `color-danger` | High-risk alert, destructive action |
| `color-trust` | Guardian UI primary, informational links |
| `color-surface` | Card and panel backgrounds |
| `color-bg` | Page canvas |
| `color-text-primary` | Headings and important body |
| `color-text-secondary` | Supporting / muted text |
| `color-border` | Default card and input borders |

---

### 2.2 Light Mode Palette

| Name | Hex | Token | Usage |
|---|---|---|---|
| **Forest Green** | `#1D7F4E` | `safe` | Primary CTA fill, status "Aman" badge |
| **Forest Green Shadow** | `#155C39` | `safe-shadow` | Tactile button shadow |
| **Safe Tint** | `#F0FDF4` | `safe-tint` | Card background on safe state |
| **Safe Border** | `#6EE7B7` | `safe-border` | Card border on safe state |
| **Danger Red** | `#DC2626` | `danger` | Alert fill, destructive button |
| **Danger Shadow** | `#991B1B` | `danger-shadow` | Tactile button shadow |
| **Danger Tint** | `#FFF1F1` | `danger-tint` | Alert card background |
| **Danger Border** | `#FCA5A5` | `danger-border` | Alert card border |
| **Trust Blue** | `#1E40AF` | `trust` | Guardian primary, links |
| **Trust Shadow** | `#1E3A8A` | `trust-shadow` | Guardian button shadow |
| **Trust Tint** | `#EFF6FF` | `trust-tint` | Guardian nav active bg |
| **Trust Border** | `#BFDBFE` | `trust-border` | Guardian nav active border |
| **Ink** | `#0F172A` | `text-primary` | Headings, critical labels |
| **Slate** | `#64748B` | `text-secondary` | Body and description text |
| **Muted** | `#94A3B8` | `text-muted` | Timestamps, captions, inactive nav |
| **Paper White** | `#FFFFFF` | `bg` | Page canvas, card surfaces |
| **Smoke** | `#F1F5F9` | `surface` | Tab switcher, inactive states |
| **Border Default** | `#E2E8F0` | `border` | Card, input, divider borders |
| **Border Strong** | `#CBD5E1` | `border-strong` | Phone frame, section separators |

---

### 2.3 Dark Mode Palette

> **Rule:** Dark mode uses a deep navy base (not pure black). Never use `#000000`. Use `#0A0F1E`.

| Name | Hex | Token | Usage |
|---|---|---|---|
| **Deep Navy** | `#0A0F1E` | `bg` | Page canvas |
| **Surface Dark** | `#111827` | `surface` | Tab switcher, pill backgrounds |
| **Card Dark** | `#1E293B` | `card` | Card and panel background |
| **Card Border Dark** | `#334155` | `border` | Card and input borders |
| **Vivid Green** | `#22C55E` | `safe` | Primary CTA — brighter on dark bg |
| **Vivid Green Shadow** | `#16A34A` | `safe-shadow` | Tactile shadow |
| **Safe Tint Dark** | `#052E16` | `safe-tint` | Safe card dark background |
| **Safe Border Dark** | `#166534` | `safe-border` | Safe card border |
| **Danger Red Dark** | `#EF4444` | `danger` | Alert fill |
| **Danger Shadow Dark** | `#B91C1C` | `danger-shadow` | Tactile shadow |
| **Danger Tint Dark** | `#1C0505` | `danger-tint` | Alert card dark background |
| **Danger Border Dark** | `#7F1D1D` | `danger-border` | Alert card border |
| **Trust Blue Dark** | `#3B82F6` | `trust` | Guardian primary |
| **Trust Shadow Dark** | `#1D4ED8` | `trust-shadow` | Button shadow |
| **Trust Tint Dark** | `#0F1C3D` | `trust-tint` | Active nav background |
| **Trust Border Dark** | `#1E3A8A` | `trust-border` | Active nav border |
| **Ghost White** | `#F1F5F9` | `text-primary` | Headings on dark |
| **Slate Light** | `#B4C4D4` | `text-secondary` | Body and descriptions on dark |
| **Muted Dark** | `#94A3B8` | `text-muted` | Captions, uppercase labels, inactive nav — must be readable on card surface (#1E293B) |
| **Sec Button Dark** | `#1E293B` | `btn-secondary-bg` | Secondary button background |
| **Sec Button Border Dark** | `#334155` | `btn-secondary-border` | Secondary button border |

---

## 3. Typography

**Primary font:** `Nunito` (Google Fonts) — rounded, friendly, highly readable at all weights.  
**Fallback:** `-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif`

> **Android:** Add `Nunito` via downloadable font in `res/font/` or Google Fonts provider in the Compose theme.

### Type Scale

| Role | Size (sp) | Weight | Letter Spacing | Usage |
|---|---|---|---|---|
| `display` | 28sp | 900 Black | -0.02em | Screen greeting title |
| `heading` | 22sp | 900 Black | -0.01em | Screen section headings |
| `subheading` | 19sp | 800 ExtraBold | normal | Card titles |
| `body` | 15sp | 700 Bold | normal | Card body, info rows |
| `body-sm` | 13sp | 700 Bold | normal | Supporting descriptions |
| `label` | 10sp | 900 Black | +0.18em (UPPERCASE) | Badges, nav labels, section labels |
| `caption` | 12sp | 700 Bold | normal | Timestamps, hints |

**Rules:**
- Nav labels and badges: always **UPPERCASE + tracking 0.18em**
- All other text: sentence case
- Minimum body text: **15sp** (elderly user requirement)

---

## 4. Spacing & Layout

**Base unit:** 4dp

| Token | Value | Usage |
|---|---|---|
| `spacing-xs` | 4dp | Tight inline gaps |
| `spacing-sm` | 8dp | Gap between inline elements |
| `spacing-md` | 12dp | Gap between cards |
| `spacing-lg` | 16dp | Card internal padding |
| `spacing-xl` | 20dp | Primary card padding |
| `spacing-2xl` | 24dp | Screen horizontal padding |
| `spacing-3xl` | 32dp | Section vertical gap |

---

## 5. Border Radius

| Element | Radius |
|---|---|
| Primary button | 16dp |
| Secondary button | 16dp |
| Badge / pill | 8dp |
| Card (default) | 20dp |
| Icon container 48–56dp | 24dp |
| Icon container 40–44dp | 16dp |
| Info row icon box | 10–12dp |
| Bottom sheet | 28dp top-only |

---

## 6. Component System

### 6.1 Tactile Button (Primary)

The signature of this system: every primary action has a **3D press effect** via a bottom shadow.

```
Border radius:  16dp
Padding:        16dp vertical · 20dp horizontal
Font:           Nunito 800, 16sp, UPPERCASE, tracking 0.05em
Shadow:         bottom 5dp solid [color-shadow]
Press:          translateY(5dp) + shadow collapses to 0
Min height:     56dp
```

| Variant | Light bg | Light shadow | Dark bg | Dark shadow |
|---|---|---|---|---|
| `SafeButton` | `#1D7F4E` | `#155C39` | `#22C55E` | `#16A34A` |
| `DangerButton` | `#DC2626` | `#991B1B` | `#EF4444` | `#B91C1C` |
| `TrustButton` | `#1E40AF` | `#1E3A8A` | `#3B82F6` | `#1D4ED8` |

---

### 6.2 Secondary / Outline Button

```
Background:    --btn-sec-bg (white / dark card)
Border:        2.5dp solid --btn-sec-border
Border radius: 16dp
Padding:       14dp vertical · 20dp horizontal
Font:          Nunito 800, 14sp
Text:          --color-trust
Shadow:        0 5dp 0 --btn-sec-border
Min height:    52dp
```

---

### 6.3 Cards

```
Standard:  bg=--card  border=2.5dp --border  shadow=0 4dp 0 --border  radius=20dp
SafeCard:  bg=--safe-tint  border=2.5dp --safe-border  shadow=0 4dp 0 --safe-border
AlertCard: bg=--danger-tint  border=2.5dp --danger-border  shadow=0 4dp 0 --danger-border
```

---

### 6.4 Info Row (inside card)

```
Layout:         Row, space-between, items-start
Icon box:       32×32dp, radius 10–12dp, semantic tint bg + border
Label:          10sp, UPPERCASE, tracking 0.18em, --text-muted
Value:          14sp, Nunito 800, --text-primary (or --danger for flagged)
Divider:        1dp --danger-border (inside alert card)
```

---

### 6.5 Status Badge

```
Padding:        6dp vertical · 12dp horizontal
Radius:         8dp
Font:           10sp, Nunito 900, UPPERCASE, tracking 0.18em
Icon:           12×12dp inline, stroke white 2dp
Background:     filled semantic color
Text:           white
```

---

### 6.6 Bottom Navigation

```
Height:         64dp
Top border:     2.5dp --border
Active tab:     container pill — bg=--trust-tint, border=--trust-border, text=--trust
Inactive tab:   text=--text-muted, no container
Icon size:      22×22dp stroke 2.2dp
Label:          10sp, Nunito 900, UPPERCASE, tracking 0.12–0.18em
```

---

## 7. Icon System

**Style:** Custom SVG, outline-only  
**Stroke width:** 2.2dp standard · 2.5dp for CTA icons  
**Linecap / Linejoin:** round / round  
**Fill:** none  
**Color:** inherited from context — white on colored bg, semantic color on neutral bg

> **Rule:** No emoji anywhere in the UI. All icons are custom SVG.

### Minimum Icon Set

| Icon | Screen | Usage |
|---|---|---|
| `shield-check` | Protected | Hero status icon |
| `shield` | Both | Nav, app wordmark |
| `triangle-warning` | Guardian | Alert state |
| `person` | Both | Avatar placeholder |
| `circle-check` | Protected | Safe badge, confirm |
| `circle-info` | Guardian | Install source row |
| `document-list` | Guardian | Permissions row |
| `app-box` | Guardian | App name row |
| `clock` | Protected | Last-checked time |
| `trash` | Guardian | Uninstall CTA |
| `bell` | Guardian | Alert nav tab |
| `history` | Guardian | Riwayat nav tab |
| `gear` | Both | Settings nav tab |
| `phone` | Both | Screen tab toggle |
| `signal-bars` | Both | Status bar |
| `battery` | Both | Status bar |
| `sun` | Both | Light mode toggle |
| `moon` | Both | Dark mode toggle |

---

## 8. Role-Based UI Rules

### 8.1 Protected (HP Orang Tua)

| Rule | Value |
|---|---|
| Max decisions per screen | **0** — never ask user to choose |
| Min font size | 15sp body · 27sp display |
| Primary content | One large status card only |
| Actionable buttons | None that trigger irreversible changes |
| Dark mode | Supported |
| Copy | Plain Indonesian, zero technical jargon |

**Forbidden on Protected screens:**
- Buttons for uninstall, pairing changes, or any destructive action
- Technical strings (`com.example.apk`, permission names)
- More than 2 cards on screen
- Alerts requiring user decision

**Copy examples:**
- ✅ "HP ini sedang dilindungi" · ❌ "Device monitoring active"
- ✅ "Anak Anda menjaga HP ini" · ❌ "Guardian paired successfully"

---

### 8.2 Guardian (HP Penjaga)

| Rule | Value |
|---|---|
| Info density | Medium-high |
| Primary CTA | `DangerButton` "Hapus Aplikasi Ini" |
| Secondary CTA | Outline button "Tandai Aman" |
| Alert detail | App name · install source · flagged permissions |
| Nav | Full bottom nav (Alert, Riwayat, Setelan) |
| Dark mode | Supported |

---

## 9. Dark Mode Rules

1. Never use `#000000` as background — use `#0A0F1E` (Deep Navy)
2. Boost primary green `#1D7F4E` → `#22C55E` in dark mode for contrast
3. Card surfaces use `#1E293B` — not transparent
4. Tactile button shadows use darker shades (see color table §2.3)
5. White text on colored buttons: stays white in both modes
6. SVG icon stroke on dark surfaces: `#F1F5F9` · on colored bg: `white`
7. Status bar elements always muted: `#94A3B8` light · `#475569` dark

---

## 10. Do's and Don'ts

### Do
- Use `SafeButton` for all primary progress and confirmation actions
- Use `DangerButton` **only** for irreversible destructive actions (uninstall)
- Use `TrustButton` for Guardian-specific confirmations
- Keep Protected screen to a single status card and zero decision buttons
- Use UPPERCASE + wide tracking **only** on labels, badges, and nav
- Minimum touch targets: **56dp** primary · **44dp** secondary
- All icons: custom SVG only

### Don't
- Don't use `#58CC02` Duolingo neon green — looks like a quiz app
- Don't add gradients, blur, or glass effects — flat surfaces only
- Don't use icons below 20×20dp without added padding
- Don't put more than one primary CTA per screen
- Don't show raw technical strings to Protected users
- Don't use `0dp` border radius — minimum 8dp everywhere
- Don't use emoji in place of icons
