package com.ronda.app.pairing

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.security.SecureRandom

/**
 * Generates the pairing code and renders it as a QR image.
 *
 * There is no scanner here on purpose. The QR is the fast path when the two
 * phones are together; the printed code underneath it is the path that works
 * over a phone call, on an emulator, and for a guardian who lives in another
 * city. Pairing must never depend on a camera.
 */
object QrCodeUtils {

    /**
     * Alphabet without characters that get misread when the code is dictated
     * aloud or typed by an older user: no O/0, no I/1, no S/5, no B/8.
     */
    private const val ALPHABET = "ACDEFGHJKLMNPQRTUVWXYZ2346789"

    private const val CODE_LENGTH = 6
    private const val URI_PREFIX = "ronda://pair/"

    private val random = SecureRandom()

    /** A fresh single-use pairing code, e.g. "4F2K9A". */
    fun newPairingCode(): String = buildString {
        repeat(CODE_LENGTH) { append(ALPHABET[random.nextInt(ALPHABET.length)]) }
    }

    /**
     * Normalises whatever the user typed into a comparable code.
     * Accepts a full `ronda://pair/XXXXXX` URI too, so a generic QR scanner app
     * can be used to read the code and paste it in.
     */
    fun normalizeCode(input: String): String =
        input.trim()
            .removePrefix(URI_PREFIX)
            .filter { !it.isWhitespace() && it != '-' }
            .uppercase()

    fun isValidCode(input: String): Boolean {
        val code = normalizeCode(input)
        return code.length == CODE_LENGTH && code.all { it in ALPHABET }
    }

    /**
     * @param sizePx side length in pixels. Keep it generous — a small QR on a
     *   dim phone screen is the usual reason pairing fails in the field.
     */
    fun qrBitmap(code: String, sizePx: Int = 640): ImageBitmap {
        val hints = mapOf(
            // High correction: the QR is often photographed off-angle or from a
            // screen with glare.
            EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.H,
            EncodeHintType.MARGIN to 1
        )
        val matrix = QRCodeWriter().encode(
            URI_PREFIX + code,
            BarcodeFormat.QR_CODE,
            sizePx,
            sizePx,
            hints
        )

        val pixels = IntArray(sizePx * sizePx)
        for (y in 0 until sizePx) {
            val offset = y * sizePx
            for (x in 0 until sizePx) {
                pixels[offset + x] = if (matrix[x, y]) Color.BLACK else Color.WHITE
            }
        }

        return Bitmap.createBitmap(pixels, sizePx, sizePx, Bitmap.Config.ARGB_8888)
            .asImageBitmap()
    }
}
