package com.ronda.app.ui.guardian.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ronda.app.R
import com.ronda.app.core.Signal
import com.ronda.app.core.Signals
import com.ronda.app.core.Verdict
import com.ronda.app.core.severity
import com.ronda.app.ui.components.IconBox
import com.ronda.app.ui.components.RondaCard
import com.ronda.app.ui.components.RondaIcons
import com.ronda.app.ui.theme.RondaTheme
import com.ronda.app.ui.theme.Tone

/**
 * The core content of the decision screen: one card per active signal, most
 * severe first, each a pictogram, a short label and one complete sentence.
 *
 * Sentences are never truncated and there is no "show more". They are the whole
 * reason the guardian can act on a number.
 */
@Composable
fun ExplanationStack(
    verdict: Verdict,
    protectedName: String,
    modifier: Modifier = Modifier
) {
    val colors = RondaTheme.colors
    Column(modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        explanationKeys(verdict).forEach { key ->
            val res = CATALOG[key] ?: return@forEach
            val tone = toneOf(key)
            RondaCard(contentPadding = PaddingValues(16.dp), modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.Top) {
                    IconBox(icon = RondaIcons.forSignal(key), tone = tone, size = 40.dp)
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = stringResource(res.first).uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.fill(tone)
                        )
                        Spacer(Modifier.height(5.dp))
                        Text(
                            text = stringResource(res.second, protectedName),
                            style = MaterialTheme.typography.bodyLarge,
                            color = colors.textPrimary
                        )
                    }
                }
            }
        }
    }
}

/**
 * Red for a capability that can empty an account on its own (a combo, or an
 * impact weight of 30+), amber for the rest and for provenance, green for the
 * one reassuring signal.
 */
private fun toneOf(key: String): Tone {
    if ('+' in key) return Tone.DANGER
    if (key == "SRC_PLAY" || key == "SRC_KNOWN_STORE") return Tone.SAFE
    val signal = Signals[key] ?: return Tone.WARN
    return if (signal.weight >= 30.0) Tone.DANGER else Tone.WARN
}

/**
 * Ordered keys to explain: combos first, then whatever signals they did not
 * already account for, most severe first.
 *
 * A combo *replaces* its member signals rather than sitting alongside them. Two
 * capabilities together describe intent, and repeating "can read SMS" under a
 * card that already says "reads your SMS and sends it to the internet" makes the
 * stack longer without making it clearer.
 *
 * Pure and Android-free so the replacement rule can be tested directly.
 */
fun explanationKeys(verdict: Verdict): List<String> {
    val combos = Signals.COMBOS.filter { it.label in verdict.combos }
    val covered = combos.flatMapTo(mutableSetOf()) { it.keys }
    val rest = verdict.signals
        .filter { it.key !in covered }
        .sortedWith(compareByDescending<Signal> { it.severity }.thenBy { it.key })
        .map { it.key }
    return combos.map { it.label } + rest
}

/** The sentence for a key, or null if it has none (e.g. SRC_PLAY). */
internal fun sentenceRes(key: String): Int? = CATALOG[key]?.second

/** Signal or combo key -> (label, sentence). Keys with no entry are skipped. */
private val CATALOG: Map<String, Pair<Int, Int>> = mapOf(
    "ACCESSIBILITY" to (R.string.cap_accessibility_eyebrow to R.string.cap_accessibility_body),
    "DEVICE_ADMIN" to (R.string.cap_device_admin_eyebrow to R.string.cap_device_admin_body),
    "SMS_READ" to (R.string.cap_sms_read_eyebrow to R.string.cap_sms_read_body),
    "INSTALL_PKG" to (R.string.cap_install_pkg_eyebrow to R.string.cap_install_pkg_body),
    "OVERLAY" to (R.string.cap_overlay_eyebrow to R.string.cap_overlay_body),
    "NOTIF_LISTENER" to (R.string.cap_notif_listener_eyebrow to R.string.cap_notif_listener_body),
    "AUDIO" to (R.string.cap_audio_eyebrow to R.string.cap_audio_body),
    "CALL" to (R.string.cap_call_eyebrow to R.string.cap_call_body),
    "CONTACTS" to (R.string.cap_contacts_eyebrow to R.string.cap_contacts_body),
    "CAMERA" to (R.string.cap_camera_eyebrow to R.string.cap_camera_body),
    "LOCATION" to (R.string.cap_location_eyebrow to R.string.cap_location_body),
    "PHONE_STATE" to (R.string.cap_phone_state_eyebrow to R.string.cap_phone_state_body),
    "QUERY_PKGS" to (R.string.cap_query_pkgs_eyebrow to R.string.cap_query_pkgs_body),
    "BOOT" to (R.string.cap_boot_eyebrow to R.string.cap_boot_body),
    "INTERNET" to (R.string.cap_internet_eyebrow to R.string.cap_internet_body),
    "FG_SERVICE" to (R.string.cap_fg_service_eyebrow to R.string.cap_fg_service_body),

    "SRC_SIDELOAD" to (R.string.cap_src_sideload_eyebrow to R.string.cap_src_sideload_body),
    "NO_LAUNCHER" to (R.string.cap_no_launcher_eyebrow to R.string.cap_no_launcher_body),
    "NAME_MIMIC" to (R.string.cap_name_mimic_eyebrow to R.string.cap_name_mimic_body),
    "CERT_SELF_SIGNED" to
        (R.string.cap_cert_self_signed_eyebrow to R.string.cap_cert_self_signed_body),
    "LEGACY_SDK" to (R.string.cap_legacy_sdk_eyebrow to R.string.cap_legacy_sdk_body),

    "SMS_READ+INTERNET" to
        (R.string.combo_sms_read_internet_eyebrow to R.string.combo_sms_read_internet_body),
    "ACCESSIBILITY+OVERLAY" to
        (R.string.combo_accessibility_overlay_eyebrow to R.string.combo_accessibility_overlay_body),
    "NOTIF_LISTENER+INTERNET" to
        (R.string.combo_notif_listener_internet_eyebrow to
            R.string.combo_notif_listener_internet_body),
    "INSTALL_PKG+SRC_SIDELOAD" to
        (R.string.combo_install_pkg_src_sideload_eyebrow to
            R.string.combo_install_pkg_src_sideload_body),
    "DEVICE_ADMIN+NO_LAUNCHER" to
        (R.string.combo_device_admin_no_launcher_eyebrow to
            R.string.combo_device_admin_no_launcher_body)
)
