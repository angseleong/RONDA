package com.ronda.app.ui.protectedrole

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ronda.app.R
import com.ronda.app.ui.components.CardDivider
import com.ronda.app.ui.components.IconActionButton
import com.ronda.app.ui.components.IconBox
import com.ronda.app.ui.components.RondaCard
import com.ronda.app.ui.components.RondaIcons
import com.ronda.app.ui.components.RondaTopBar
import com.ronda.app.ui.components.SecondaryButton
import com.ronda.app.ui.components.TactileButton
import com.ronda.app.ui.components.Wordmark
import com.ronda.app.ui.components.screenInsets
import com.ronda.app.ui.setup.SetupStatus
import com.ronda.app.ui.theme.LargePrint
import com.ronda.app.ui.theme.LargePrintTitle
import com.ronda.app.ui.theme.PairingCode
import com.ronda.app.ui.theme.RondaRadius
import com.ronda.app.ui.theme.RondaTheme
import com.ronda.app.ui.theme.Tone

/**
 * The protected person's home (DESIGN.md §8.1): one status card, zero
 * decisions. Green when everything is on; amber with a single "continue setup"
 * button when it is not. The second card is the privacy promise — the two
 * things this person needs to know, and nothing they need to do.
 *
 * The profile box opens a sheet that says who is guarding the phone and shows
 * the pairing code. Information only: no unpairing, no destructive action,
 * ever, on this screen.
 */
@Composable
fun ProtectedHomeScreen(
    status: SetupStatus,
    pairingCode: String,
    /** Who is guarding, from the pairing record; blank until known. */
    guardianName: String,
    onContinueSetup: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = RondaTheme.colors
    var profileOpen by rememberSaveable { mutableStateOf(false) }
    val complete = status.isFullyProtected

    Column(
        modifier
            .fillMaxSize()
            .screenInsets()
    ) {
        RondaTopBar(
            leading = { Wordmark() },
            trailing = {
                IconActionButton(
                    icon = RondaIcons.person,
                    contentDescription = stringResource(R.string.home_protected_profile),
                    onClick = { profileOpen = true }
                )
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            RondaCard(tone = if (complete) Tone.SAFE else Tone.WARN) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconBox(
                        icon = if (complete) RondaIcons.shieldCheck else RondaIcons.shieldAlert,
                        tone = if (complete) Tone.SAFE else Tone.WARN,
                        size = 80.dp,
                        filled = complete
                    )
                    Spacer(Modifier.height(20.dp))
                    Text(
                        text = stringResource(
                            if (complete) R.string.home_protected_title_active
                            else R.string.home_protected_title_incomplete
                        ),
                        style = LargePrintTitle,
                        color = colors.textPrimary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = if (complete) {
                            stringResource(R.string.home_protected_body_active)
                        } else {
                            stringResource(
                                R.string.home_protected_body_incomplete,
                                status.grantedCount,
                                SetupStatus.TOTAL
                            )
                        },
                        style = LargePrint,
                        color = colors.textPrimary,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(Modifier.height(20.dp))
                if (complete) {
                    // Whole sentences at 20sp, not label/value rows: a 10sp
                    // upper-case label is a guardian-screen device and has no
                    // place on a screen an older person reads.
                    CardDivider(tone = Tone.SAFE)
                    Spacer(Modifier.height(16.dp))
                    StatusLine(
                        icon = RondaIcons.eye,
                        text = stringResource(R.string.home_protected_watch_active)
                    )
                    Spacer(Modifier.height(12.dp))
                    StatusLine(
                        icon = RondaIcons.heartHandshake,
                        text = guardedBy(guardianName)
                    )
                } else {
                    TactileButton(
                        text = stringResource(R.string.home_protected_continue_setup),
                        onClick = onContinueSetup,
                        icon = RondaIcons.arrowRight,
                        large = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            RondaCard {
                Row(verticalAlignment = Alignment.Top) {
                    IconBox(icon = RondaIcons.lock, tone = Tone.NEUTRAL, size = 44.dp)
                    Spacer(Modifier.width(14.dp))
                    Text(
                        text = stringResource(R.string.setup_privacy_note),
                        style = LargePrint,
                        color = colors.textPrimary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }

    if (profileOpen) {
        ProfileSheet(
            pairingCode = pairingCode,
            guardianName = guardianName,
            onDismiss = { profileOpen = false }
        )
    }
}

/** "Dijaga oleh Rina", or the family in general until the name is known. */
@Composable
private fun guardedBy(guardianName: String): String =
    if (guardianName.isBlank()) stringResource(R.string.home_protected_guarded_by_unknown)
    else stringResource(R.string.home_protected_guarded_by, guardianName)

@Composable
private fun StatusLine(icon: Int, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconBox(icon = icon, tone = Tone.SAFE, size = 40.dp)
        Spacer(Modifier.width(12.dp))
        Text(
            text = text,
            style = LargePrint,
            color = RondaTheme.colors.textPrimary,
            modifier = Modifier.weight(1f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileSheet(pairingCode: String, guardianName: String, onDismiss: () -> Unit) {
    val colors = RondaTheme.colors
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        // Opens at its full height and scrolls: a half-open sheet would cut the
        // close button under the gesture bar for the person least able to
        // guess that a sheet can be dragged.
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = colors.card,
        contentColor = colors.textPrimary,
        shape = RoundedCornerShape(topStart = RondaRadius.sheet, topEnd = RondaRadius.sheet)
    ) {
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(start = 24.dp, end = 24.dp, bottom = 32.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconBox(icon = RondaIcons.heartHandshake, tone = Tone.SAFE, size = 48.dp)
                Spacer(Modifier.width(14.dp))
                Text(
                    text = stringResource(R.string.home_protected_profile),
                    style = MaterialTheme.typography.displaySmall,
                    color = colors.textPrimary
                )
            }
            Spacer(Modifier.height(14.dp))
            Text(
                text = guardedBy(guardianName),
                style = LargePrint,
                color = colors.textPrimary
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.home_protected_profile_body),
                style = LargePrint,
                color = colors.textSecondary
            )
            Spacer(Modifier.height(20.dp))
            Text(
                text = stringResource(R.string.home_protected_pairing_code),
                style = LargePrint,
                color = colors.textSecondary
            )
            Spacer(Modifier.height(4.dp))
            SelectionContainer {
                Text(text = pairingCode, style = PairingCode, color = colors.textPrimary)
            }
            Spacer(Modifier.height(24.dp))
            SecondaryButton(
                text = stringResource(R.string.action_close),
                onClick = onDismiss,
                large = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
