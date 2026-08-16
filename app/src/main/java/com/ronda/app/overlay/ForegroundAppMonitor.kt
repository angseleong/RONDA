package com.ronda.app.overlay

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.util.Log

/**
 * Answers one question: which package is on screen right now?
 *
 * Uses PACKAGE_USAGE_STATS, which exposes the foreground package name and
 * nothing else — no screen content, no input, no app data. This is the whole
 * reason RONDA can block without an AccessibilityService.
 */
class ForegroundAppMonitor(context: Context) {

    private val usageStatsManager =
        context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

    /**
     * The last package that came to the foreground, or null if nothing is known yet.
     *
     * Usage events are only reported when the foreground app *changes*, so the
     * last seen value is cached and returned while nothing moves.
     */
    private var lastKnownPackage: String? = null

    /** Timestamp of the newest resume already consumed, so it is not re-counted. */
    private var lastEventAt = 0L

    /** Polls since the last resume event, for the stalled-log warning below. */
    private var quietPolls = 0

    fun currentForegroundPackage(): String? {
        val now = System.currentTimeMillis()
        // Re-scan a rolling window rather than only since the last poll: usage
        // events can be reported with a delay, and re-reading them is harmless.
        val events = usageStatsManager.queryEvents(now - QUERY_WINDOW_MS, now)
        val event = UsageEvents.Event()
        var sawEvent = false

        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            sawEvent = true
            if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED &&
                event.timeStamp > lastEventAt
            ) {
                lastEventAt = event.timeStamp
                if (event.packageName != lastKnownPackage) {
                    Log.d(TAG, "Foreground: ${event.packageName}")
                }
                lastKnownPackage = event.packageName
            }
        }

        warnIfEventLogStalled(sawEvent)
        return lastKnownPackage
    }

    /**
     * A silent usage-event log looks exactly like "nothing is happening", which
     * is why this failure is so easy to lose an afternoon to: the block simply
     * never fires, with every permission granted and the package correctly
     * flagged. Emulators stop recording events after the screen goes off and do
     * not resume — queryEvents then returns an empty window forever. Rebooting
     * the emulator restores it.
     */
    private fun warnIfEventLogStalled(sawEvent: Boolean) {
        if (sawEvent) {
            quietPolls = 0
            return
        }
        quietPolls++
        if (quietPolls % STALL_WARNING_POLLS == 0) {
            Log.w(TAG, "No usage events for ${quietPolls * POLL_ESTIMATE_MS / 1000}s — the " +
                "usage-stats log looks stalled; on an emulator, reboot it")
        }
    }

    private companion object {
        const val TAG = "ForegroundAppMonitor"
        const val QUERY_WINDOW_MS = 10_000L

        /** OverlayService polls roughly this often; used only for the log text. */
        const val POLL_ESTIMATE_MS = 700L
        const val STALL_WARNING_POLLS = 60
    }
}
