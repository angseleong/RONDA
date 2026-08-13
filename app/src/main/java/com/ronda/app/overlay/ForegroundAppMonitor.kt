package com.ronda.app.overlay

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context

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

    fun currentForegroundPackage(): String? {
        val now = System.currentTimeMillis()
        // Re-scan a rolling window rather than only since the last poll: usage
        // events can be reported with a delay, and re-reading them is harmless.
        val events = usageStatsManager.queryEvents(now - QUERY_WINDOW_MS, now)
        val event = UsageEvents.Event()

        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                lastKnownPackage = event.packageName
            }
        }

        return lastKnownPackage
    }

    private companion object {
        const val QUERY_WINDOW_MS = 10_000L
    }
}
