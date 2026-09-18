package com.ronda.app.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ronda.app.R
import com.ronda.app.core.RiskLevel

/**
 * The icon set (DESIGN.md §7): outline only, 2.2dp round stroke, drawn once as
 * vector drawables under res/drawable so the overlay layout, notification small
 * icons and Compose all pull the same file. No emoji anywhere.
 */
object RondaIcons {
    val shield = R.drawable.ic_shield
    val shieldCheck = R.drawable.ic_shield_check
    val shieldAlert = R.drawable.ic_shield_alert
    val triangleWarning = R.drawable.ic_triangle_warning
    val person = R.drawable.ic_person
    val circleCheck = R.drawable.ic_circle_check
    val circleInfo = R.drawable.ic_circle_info
    val documentList = R.drawable.ic_document_list
    val appBox = R.drawable.ic_app_box
    val clock = R.drawable.ic_clock
    val trash = R.drawable.ic_trash
    val bell = R.drawable.ic_bell
    val history = R.drawable.ic_history
    val gear = R.drawable.ic_gear
    val smartphone = R.drawable.ic_smartphone
    val phone = R.drawable.ic_phone
    val sun = R.drawable.ic_sun
    val moon = R.drawable.ic_moon
    val arrowLeft = R.drawable.ic_arrow_left
    val arrowRight = R.drawable.ic_arrow_right
    val chevronDown = R.drawable.ic_chevron_down
    val chevronRight = R.drawable.ic_chevron_right
    val globe = R.drawable.ic_globe
    val link = R.drawable.ic_link
    val wifiOff = R.drawable.ic_wifi_off
    val lock = R.drawable.ic_lock
    val eye = R.drawable.ic_eye
    val eyeOff = R.drawable.ic_eye_off
    val close = R.drawable.ic_x
    val check = R.drawable.ic_check
    val undo = R.drawable.ic_undo
    val message = R.drawable.ic_message
    val pointer = R.drawable.ic_pointer
    val key = R.drawable.ic_key
    val download = R.drawable.ic_download
    val layers = R.drawable.ic_layers
    val mic = R.drawable.ic_mic
    val users = R.drawable.ic_users
    val camera = R.drawable.ic_camera
    val mapPin = R.drawable.ic_map_pin
    val grid = R.drawable.ic_grid
    val power = R.drawable.ic_power
    val activity = R.drawable.ic_activity
    val send = R.drawable.ic_send
    val copy = R.drawable.ic_copy
    val fileWarning = R.drawable.ic_file_warning
    val battery = R.drawable.ic_battery
    val pen = R.drawable.ic_pen
    val logOut = R.drawable.ic_log_out
    val languages = R.drawable.ic_languages
    val qrCode = R.drawable.ic_qr_code
    val heartHandshake = R.drawable.ic_heart_handshake

    /** The pictogram for a signal or combo key in the explanation catalogue. */
    @DrawableRes
    fun forSignal(signal: String): Int = when (signal) {
        "ACCESSIBILITY", "ACCESSIBILITY+OVERLAY" -> pointer
        "DEVICE_ADMIN", "DEVICE_ADMIN+NO_LAUNCHER" -> key
        "SMS_READ", "SMS_READ+INTERNET" -> message
        "INSTALL_PKG", "INSTALL_PKG+SRC_SIDELOAD" -> download
        "OVERLAY" -> layers
        "NOTIF_LISTENER", "NOTIF_LISTENER+INTERNET" -> bell
        "AUDIO" -> mic
        "CALL" -> phone
        "CONTACTS" -> users
        "CAMERA" -> camera
        "LOCATION" -> mapPin
        "PHONE_STATE" -> smartphone
        "QUERY_PKGS" -> grid
        "BOOT" -> power
        "INTERNET" -> globe
        "FG_SERVICE" -> activity
        "SRC_SIDELOAD" -> send
        "SRC_PLAY", "SRC_KNOWN_STORE" -> circleCheck
        "NO_LAUNCHER" -> eyeOff
        "NAME_MIMIC" -> copy
        "CERT_SELF_SIGNED" -> fileWarning
        "LEGACY_SDK" -> history
        else -> circleInfo
    }

    /** The pictogram that goes with a band. */
    @DrawableRes
    fun forLevel(level: RiskLevel): Int = when (level) {
        RiskLevel.DARURAT -> triangleWarning
        RiskLevel.PERINGATAN -> shieldAlert
        RiskLevel.RENDAH -> appBox
        RiskLevel.AMAN -> circleCheck
    }
}

@Composable
fun RondaIcon(
    @DrawableRes id: Int,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    size: Dp = 22.dp
) {
    Icon(
        painter = painterResource(id),
        contentDescription = contentDescription,
        tint = tint,
        modifier = modifier.size(size)
    )
}
