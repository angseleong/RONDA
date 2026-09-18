---
version: 1
slug: "app-src-main-java-com-ronda-app-mainactivity-kt"
primary_target: "app/src/main/java/com/ronda/app/MainActivity.kt"
related_targets: ["app/src/main/java/com/ronda/app/ui/guardian/GuardianHomeScreen.kt","app/src/main/java/com/ronda/app/ui/protectedrole/ProtectedHomeScreen.kt"]
---

# Surface: RONDA app shell (both role homes)

Scope: Operate. Native Android, phone only. Two surfaces under one Activity: the guardian's home (bottom nav Peringatan / Riwayat / Setelan, alert detail) and the protected person's home (single status card, permission wizard, uninstall prompt). Audience: guardian = tech-literate adult child; protected = elderly parent. Task: guardian triages a flagged app and taps one of two actions; protected person reads that the phone is guarded and does nothing.

## Direction contract

THESIS: One app, two hands. The guardian's screen is a dense, blue-chromed control room; the protected person's screen is one green card that asks nothing. It refuses the antivirus dashboard (threat meters, red everywhere, "scan now") and the wellness-app softness that would make a security tool look like a habit tracker.

OWN-WORLD: docs/DESIGN.md, verbatim. Nunito 700–900 everywhere; paper-white or deep-navy ground (never black); 20 dp cards with a 2.5 dp border and a 4 dp solid drop in the border colour; tactile buttons with 5 dp press travel and a solid shadow that collapses; forest green = safe/primary, danger red spent only on flagged apps and the uninstall action, trust blue = guardian chrome and active nav; amber (extension) = "not yet" and PERINGATAN; 2.2 dp round-stroke icons; flat, no gradients, no emoji.

STORY: Guardian: "Ibu's phone is fine" or "here is exactly what is wrong, in sentences, and the one tap that fixes it". Protected: "This phone is guarded by someone you trust. You need to do nothing."

FIRST VIEWPORT: Guardian home — top bar: avatar box, "HP Ibu", connection pill; hero status card full width (green "Semua aman" with shield-check, or red "N aplikasi perlu diperiksa"); alert cards with DARURAT/PERINGATAN badge, app name, first consequence sentence, time; bottom nav with an active trust-tint pill. Protected home — RONDA wordmark; one SafeCard: 64 dp shield-check box, "HP ini sedang dilindungi" at 30 sp, guardian line, two status rows; a quiet privacy card; nothing else on screen.

FORM: Duolingo-structure card app, brief-pinned by docs/DESIGN.md (team-authored). Seed: none — the direction roll was skipped because the world and every surface are pinned by the brief; ambition lives in the tactile press (5 dp travel, 90 ms, with a haptic tick on release) as the signature interaction and the status-card crossfade when a permission lands.

FINISH: unreviewed and undocumented is unfinished; this build ends with the finish review, the verdict, DESIGN.md, and every shipping raster carrying its provenance.

## Finish review (round 1: fix → applied)
- Guardian's name is collected at pairing, carried in the pairing record, and shown on the protected home as "Dijaga oleh <nama>".
- Red is spent only on flagged apps and the uninstall; disconnect is an outline action with a trust confirm.
- Band labels are the product names (Peringatan / Darurat; Warning / Danger).
- Protected surfaces carry no text under 20 sp; the 10 sp label is a guardian-only device.
- Overlay: sentence-case title, window extends under the status bar.
- Dark-theme badges: brown ink on amber, danger badge on the shadow shade.

## Unresolved
- Disconnect on the protected phone (USERFLOW §6) is not offered: DESIGN.md §8.1 forbids pairing changes there.
- Dark-mode primary button keeps white-on-#22C55E per DESIGN.md §9.5 (2.3:1); flagged for the team.
