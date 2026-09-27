package com.ronda.app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.net.Uri
import androidx.annotation.RawRes

/**
 * What every RONDA notification shares: the RONDA mark in the status bar, and
 * a sound that says what kind of news it is before the screen is even on.
 *
 * A channel's sound is fixed the first time the channel is created — Android
 * ignores later changes, so the user's own choice in Settings is never
 * overwritten. Giving a channel a new sound therefore means a new channel id;
 * the ids it replaces are listed in [RETIRED] and deleted at startup.
 */
object RondaNotifications {

    /** Silhouette of the RONDA mark; the status bar draws alpha only. */
    val SMALL_ICON = R.drawable.ic_ronda_mark

    // Tints the mark in the expanded shade, matching the app's tones.
    const val COLOR_DANGER = 0xFFDC2626.toInt()
    const val COLOR_WARN = 0xFFB45309.toInt()
    const val COLOR_SAFE = 0xFF1D7F4E.toInt()
    const val COLOR_TRUST = 0xFF1E40AF.toInt()

    enum class Sound(@RawRes val res: Int) {
        /** A threat that needs someone now: fast, high, repeated. */
        DANGER(R.raw.ronda_danger),

        /** Worth a look, not an emergency: two falling mallet notes. */
        WARN(R.raw.ronda_warn),

        /** Something was dealt with: a rising bell. */
        RESOLVED(R.raw.ronda_resolved),

        /** A request or a note: a soft two-note chime. */
        INFO(R.raw.ronda_info)
    }

    /**
     * Create (or refresh the name of) a channel. [sound] null leaves the
     * system default — only the silent, low-importance ongoing channels do.
     */
    fun ensureChannel(
        context: Context,
        id: String,
        name: String,
        importance: Int,
        sound: Sound?,
        description: String? = null,
        configure: NotificationChannel.() -> Unit = {}
    ): String {
        val channel = NotificationChannel(id, name, importance).apply {
            description?.let { this.description = it }
            sound?.let {
                setSound(
                    Uri.parse("android.resource://${context.packageName}/${it.res}"),
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
            }
            configure()
        }
        manager(context).createNotificationChannel(channel)
        return id
    }

    /** Called once per process start. Deleting a channel that is gone is a no-op. */
    fun retireOldChannels(context: Context) {
        val manager = manager(context)
        RETIRED.forEach(manager::deleteNotificationChannel)
    }

    private fun manager(context: Context) =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // Current ids. The _v2 suffix is the sound change described above.
    const val CHANNEL_DETECTION = "ronda_detection_v2"
    const val CHANNEL_GUARDIAN_ALERT = "ronda_guardian_alert_v2"
    const val CHANNEL_GUARDIAN_WARN = "ronda_guardian_warn_v2"
    const val CHANNEL_GUARDIAN_OUTCOME = "ronda_guardian_outcome_v2"
    const val CHANNEL_RONDEE_DISCONNECTED = "ronda_rondee_disconnected_v2"
    const val CHANNEL_DISCONNECTED = "ronda_disconnect_v2"
    const val CHANNEL_GUARDIAN_REQUEST = "ronda_guardian_request_v2"
    const val CHANNEL_GUARDIAN_DECISION = "ronda_guardian_decision_v2"

    private val RETIRED = listOf(
        "ronda_alert_channel",
        "ronda_guardian_alert_channel",
        "ronda_guardian_warn_channel",
        "ronda_guardian_outcome_channel",
        "ronda_rondee_disconnected_channel",
        "ronda_disconnect_channel",
        "ronda_guardian_request_channel"
    )
}
