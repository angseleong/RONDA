package com.ronda.app.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.ronda.app.core.RiskLevel

/**
 * The palette from docs/DESIGN.md §2, as semantic roles.
 *
 * Every colour comes in a family of four — fill, shadow, tint, border — because
 * that is how the system spends it: a tactile button is fill over shadow, a
 * state card is tint inside border. Nothing here is an accent picked per
 * screen; a screen asks for a [Tone] and gets the family.
 *
 * Amber ([warn]) is the one extension to the brief. DESIGN.md has no colour for
 * "not yet" — a permission still off, a PERINGATAN score — and spending red on
 * those would make the one red that matters (a flagged app) ordinary.
 */
@Immutable
data class RondaColors(
    val safe: Color,
    val safeShadow: Color,
    val safeTint: Color,
    val safeBorder: Color,

    val danger: Color,
    val dangerShadow: Color,
    val dangerTint: Color,
    val dangerBorder: Color,

    val trust: Color,
    val trustShadow: Color,
    val trustTint: Color,
    val trustBorder: Color,

    val warn: Color,
    val warnShadow: Color,
    val warnTint: Color,
    val warnBorder: Color,

    val textPrimary: Color,
    val textSecondary: Color,
    /** Inactive nav and badge labels only — too light for a sentence. */
    val textMuted: Color,

    val bg: Color,
    val surface: Color,
    val card: Color,
    val border: Color,
    val borderStrong: Color,

    val isDark: Boolean
) {
    /** Text and icons on any filled semantic colour. White in both themes (DESIGN.md §9.5). */
    val onFill: Color get() = Color.White

    /**
     * Ink on a filled [tone]. White everywhere the brief pins it; the one
     * exception is amber in the dark theme, which the brief does not define and
     * on which white measures 2.2:1 — there the ink is the family's own deep
     * brown, which reads at 10:1.
     */
    fun onFill(tone: Tone): Color = if (tone == Tone.WARN && isDark) warnTint else onFill

    /**
     * Fill for a 10sp badge. Buttons keep the brief's vivid dark-theme fills
     * (their 16sp+ labels clear the large-text bar), but a badge's small type
     * needs 4.5:1, so in the dark theme a red badge sits on the family's shadow
     * shade (#B91C1C, 6.4:1 with white) instead of the vivid fill (3.8:1).
     */
    fun badgeFill(tone: Tone): Color = if (tone == Tone.DANGER && isDark) dangerShadow else fill(tone)

    fun fill(tone: Tone): Color = when (tone) {
        Tone.SAFE -> safe
        Tone.DANGER -> danger
        Tone.TRUST -> trust
        Tone.WARN -> warn
        Tone.NEUTRAL -> textSecondary
    }

    fun shadow(tone: Tone): Color = when (tone) {
        Tone.SAFE -> safeShadow
        Tone.DANGER -> dangerShadow
        Tone.TRUST -> trustShadow
        Tone.WARN -> warnShadow
        Tone.NEUTRAL -> borderStrong
    }

    fun tint(tone: Tone): Color = when (tone) {
        Tone.SAFE -> safeTint
        Tone.DANGER -> dangerTint
        Tone.TRUST -> trustTint
        Tone.WARN -> warnTint
        Tone.NEUTRAL -> surface
    }

    fun border(tone: Tone): Color = when (tone) {
        Tone.SAFE -> safeBorder
        Tone.DANGER -> dangerBorder
        Tone.TRUST -> trustBorder
        Tone.WARN -> warnBorder
        Tone.NEUTRAL -> border
    }
}

/** A semantic hue family. Components take a tone, never a colour. */
enum class Tone { SAFE, DANGER, TRUST, WARN, NEUTRAL }

/** The band's tone: green for cleared, amber for a warning, red for an emergency. */
fun RiskLevel.tone(): Tone = when (this) {
    RiskLevel.AMAN -> Tone.SAFE
    RiskLevel.RENDAH -> Tone.NEUTRAL
    RiskLevel.PERINGATAN -> Tone.WARN
    RiskLevel.DARURAT -> Tone.DANGER
}

val LightColors = RondaColors(
    safe = Color(0xFF1D7F4E),
    safeShadow = Color(0xFF155C39),
    safeTint = Color(0xFFF0FDF4),
    safeBorder = Color(0xFF6EE7B7),

    danger = Color(0xFFDC2626),
    dangerShadow = Color(0xFF991B1B),
    dangerTint = Color(0xFFFFF1F1),
    dangerBorder = Color(0xFFFCA5A5),

    trust = Color(0xFF1E40AF),
    trustShadow = Color(0xFF1E3A8A),
    trustTint = Color(0xFFEFF6FF),
    trustBorder = Color(0xFFBFDBFE),

    warn = Color(0xFFB45309),
    warnShadow = Color(0xFF92400E),
    warnTint = Color(0xFFFFFBEB),
    warnBorder = Color(0xFFFCD34D),

    textPrimary = Color(0xFF0F172A),
    textSecondary = Color(0xFF64748B),
    textMuted = Color(0xFF94A3B8),

    bg = Color(0xFFFFFFFF),
    surface = Color(0xFFF1F5F9),
    card = Color(0xFFFFFFFF),
    border = Color(0xFFE2E8F0),
    borderStrong = Color(0xFFCBD5E1),

    isDark = false
)

/** Deep navy, never black (DESIGN.md §9.1). Fills brighten; tints darken. */
val DarkColors = RondaColors(
    safe = Color(0xFF22C55E),
    safeShadow = Color(0xFF16A34A),
    safeTint = Color(0xFF052E16),
    safeBorder = Color(0xFF166534),

    danger = Color(0xFFEF4444),
    dangerShadow = Color(0xFFB91C1C),
    dangerTint = Color(0xFF1C0505),
    dangerBorder = Color(0xFF7F1D1D),

    trust = Color(0xFF3B82F6),
    trustShadow = Color(0xFF1D4ED8),
    trustTint = Color(0xFF0F1C3D),
    trustBorder = Color(0xFF1E3A8A),

    warn = Color(0xFFF59E0B),
    warnShadow = Color(0xFFB45309),
    warnTint = Color(0xFF2A1A05),
    warnBorder = Color(0xFF92400E),

    textPrimary = Color(0xFFF1F5F9),
    textSecondary = Color(0xFFB4C4D4),
    textMuted = Color(0xFF94A3B8),

    bg = Color(0xFF0A0F1E),
    surface = Color(0xFF111827),
    card = Color(0xFF1E293B),
    border = Color(0xFF334155),
    borderStrong = Color(0xFF475569),

    isDark = true
)

val LocalRondaColors = staticCompositionLocalOf { LightColors }
