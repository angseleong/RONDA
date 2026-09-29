package com.ronda.app.detection

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * The end of a scan of installed apps, told to whatever screen is open.
 *
 * In-process only: [DetectionService] and MainActivity share a process, and a
 * scan finishing while RONDA is closed has no screen to tell. Before this the
 * first-run scan screen simply waited three seconds and hoped.
 */
object ScanEvents {

    /** [found] is how many apps this scan newly flagged. */
    data class Finished(val found: Int)

    private val finished = MutableSharedFlow<Finished>(extraBufferCapacity = 4)
    val results: SharedFlow<Finished> = finished.asSharedFlow()

    fun finish(found: Int) {
        finished.tryEmit(Finished(found))
    }
}
