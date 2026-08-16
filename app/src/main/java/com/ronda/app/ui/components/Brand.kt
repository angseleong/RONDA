package com.ronda.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ronda.app.R

/**
 * The two brand marks, in one place.
 *
 * Both are bitmaps rather than vectors because that is how the identity was
 * delivered. They are sized by height and left to keep their own aspect ratio —
 * a wordmark stretched to fit a box is the fastest way to make a product look
 * unfinished.
 *
 * Both carry the same content description. To a screen reader they are the same
 * thing, said once; the difference between them is purely how much room the
 * layout has.
 */

/** Full lockup: mark plus "RONDA". Used in the header and on the splash. */
@Composable
fun RondaWordmark(modifier: Modifier = Modifier, height: Dp = 28.dp) {
    Image(
        painter = painterResource(R.drawable.ronda_wordmark),
        contentDescription = stringResource(R.string.watch_title),
        contentScale = ContentScale.Fit,
        modifier = modifier.height(height)
    )
}

/** Mark alone. Used where the app's name is already stated in nearby text. */
@Composable
fun RondaMark(modifier: Modifier = Modifier, size: Dp = 72.dp) {
    Image(
        painter = painterResource(R.drawable.ronda_mark),
        contentDescription = stringResource(R.string.watch_title),
        contentScale = ContentScale.Fit,
        modifier = modifier.size(size)
    )
}
