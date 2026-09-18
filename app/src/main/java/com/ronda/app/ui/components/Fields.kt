package com.ronda.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.ronda.app.ui.theme.LargePrint
import com.ronda.app.ui.theme.RondaDepth
import com.ronda.app.ui.theme.RondaRadius
import com.ronda.app.ui.theme.RondaTheme

/**
 * A text field in the system's vocabulary: a bordered box, trust-blue while it
 * has focus, the caret in the same blue. Material's field would bring its own
 * floating label and indicator line, which belong to no design system here.
 */
@Composable
fun RondaTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    large: Boolean = false
) {
    val colors = RondaTheme.colors
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val shape = RoundedCornerShape(RondaRadius.button)
    val style = if (large) LargePrint else MaterialTheme.typography.titleMedium

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = singleLine,
        interactionSource = interaction,
        textStyle = style.copy(color = colors.textPrimary),
        cursorBrush = SolidColor(colors.trust),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        modifier = modifier.fillMaxWidth(),
        decorationBox = { inner ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = if (large) 60.dp else 56.dp)
                    .background(colors.card, shape)
                    .border(
                        RondaDepth.border,
                        if (focused) colors.trust else colors.borderStrong,
                        shape
                    )
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (value.isEmpty()) {
                    Text(text = placeholder, style = style, color = colors.textMuted)
                }
                inner()
            }
        }
    )
}

/** One nickname among several: a bordered pill that turns trust-blue when chosen. */
@Composable
fun Chip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    large: Boolean = false
) {
    val colors = RondaTheme.colors
    val shape = RoundedCornerShape(RondaRadius.iconBoxSmall)
    Box(
        modifier = modifier
            .heightIn(min = 48.dp)
            .background(if (selected) colors.trustTint else colors.card, shape)
            .border(RondaDepth.border, if (selected) colors.trust else colors.border, shape)
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = if (large) LargePrint else MaterialTheme.typography.titleMedium,
            color = if (selected) colors.trust else colors.textPrimary
        )
    }
}
