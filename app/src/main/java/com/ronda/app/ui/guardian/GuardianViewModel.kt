package com.ronda.app.ui.guardian

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ronda.app.core.RiskEvaluator
import com.ronda.app.core.Verdict
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

data class GuardianUiState(
    val needsReview: List<Verdict> = emptyList(),
    val monitored: List<Verdict> = emptyList(),
    /** Already ruled on. Shown under both tabs, never inside either list. */
    val history: List<Verdict> = emptyList(),
    val connected: Boolean = true,
    /** False until the first list arrives, so an empty screen can be told from a loading one. */
    val loaded: Boolean = false,
    val protectedName: String = "Ibu",
    /** Package whose "mark safe" can still be taken back, or null. */
    val undoable: String? = null
)

/** Something that arrived from the Rondee's side, worth a toast. */
sealed interface GuardianEvent {
    val verdict: Verdict

    /** A new app above the threshold: needs the guardian's decision. */
    data class NeedsReview(override val verdict: Verdict) : GuardianEvent

    /** A new app below it: listed quietly under Terpantau. */
    data class Monitored(override val verdict: Verdict) : GuardianEvent

    /** The Rondee's phone confirmed the app is gone. */
    data class Removed(override val verdict: Verdict) : GuardianEvent
}

class GuardianViewModel(private val repo: GuardianRepository) : ViewModel() {

    private val _state = MutableStateFlow(GuardianUiState(protectedName = repo.protectedName))
    val state: StateFlow<GuardianUiState> = _state.asStateFlow()

    private val _events = Channel<GuardianEvent>(Channel.BUFFERED)
    /** One-shot: each event is delivered to one collector, once. */
    val events: Flow<GuardianEvent> = _events.receiveAsFlow()

    private var undoJob: kotlinx.coroutines.Job? = null

    /** Null until the first list arrives: what was already there is not news. */
    private var knownAlerts: Set<String>? = null
    private var knownRemoved: Set<String> = emptySet()

    init {
        viewModelScope.launch {
            repo.observeVerdicts().collect { verdicts ->
                announce(verdicts)
                // An app can have several alerts, one per install. Its newest
                // one is its current state; newest first, so distinctBy keeps it.
                val current = verdicts.distinctBy { "${it.pairingId}:${it.packageName}" }

                // Two questions, in order. First: has the guardian ruled on it?
                // If so it is history and leaves both tabs. History takes every
                // ruling, not just the current one, so a reinstall opens a fresh
                // card without erasing what was decided about the last install.
                // Only among the ones still open does the score decide which tab
                // it belongs to — that is what keeps "Terpantau" meaning "quiet".
                val decided = verdicts.filter { it.state.decided }
                val open = current.filterNot { it.state.decided }
                val (review, monitored) = open.partition {
                    it.score >= RiskEvaluator.GUARDIAN_THRESHOLD
                }
                _state.value = _state.value.copy(
                    needsReview = review.sortedByDescending { it.detectedAt },
                    monitored = monitored.sortedByDescending { it.detectedAt },
                    history = decided.sortedByDescending { it.detectedAt },
                    loaded = true
                )
            }
        }
        viewModelScope.launch {
            repo.observeConnected().collect { _state.value = _state.value.copy(connected = it) }
        }
    }

    /**
     * A history row names its own alert, since one app can have several. A
     * notification only knows the package, and gets the open card if there is
     * one, else the newest ruling.
     */
    private fun announce(verdicts: List<Verdict>) {
        val keyOf = { v: Verdict -> v.alertId.ifEmpty { "${v.pairingId}:${v.packageName}" } }
        val ids = verdicts.map(keyOf).toSet()
        val removed = verdicts.filter { it.removed }.map(keyOf).toSet()
        val known = knownAlerts
        knownAlerts = ids
        val newlyRemoved = removed - knownRemoved
        knownRemoved = removed
        if (known == null) return

        verdicts.filter { keyOf(it) !in known && !it.state.decided }.forEach {
            _events.trySend(
                if (it.score >= RiskEvaluator.GUARDIAN_THRESHOLD) GuardianEvent.NeedsReview(it)
                else GuardianEvent.Monitored(it)
            )
        }
        verdicts.filter { keyOf(it) in newlyRemoved }.forEach {
            _events.trySend(GuardianEvent.Removed(it))
        }
    }

    fun find(pairingId: String?, packageName: String?, alertId: String? = null): Verdict? {
        if (packageName == null || pairingId == null) return null
        val s = _state.value
        val all = s.needsReview + s.monitored + s.history
        if (!alertId.isNullOrEmpty()) all.firstOrNull { it.alertId == alertId }?.let { return it }
        return all.firstOrNull { it.packageName == packageName && it.pairingId == pairingId }
    }

    fun markUnsafe(pairingId: String, packageName: String) {
        viewModelScope.launch { repo.decide(pairingId, packageName, safe = false) }
    }

    /**
     * Marking safe removes protection, so it is the only decision that is
     * reversible — the guardian gets ten seconds to take it back.
     */
    fun markSafe(pairingId: String, packageName: String) {
        viewModelScope.launch { repo.decide(pairingId, packageName, safe = true) }
        undoJob?.cancel()
        _state.value = _state.value.copy(undoable = packageName)
        undoJob = viewModelScope.launch {
            delay(UNDO_WINDOW_MS)
            if (_state.value.undoable == packageName) {
                _state.value = _state.value.copy(undoable = null)
            }
        }
    }

    fun undoMarkSafe(pairingId: String, packageName: String) {
        undoJob?.cancel()
        _state.value = _state.value.copy(undoable = null)
        viewModelScope.launch { repo.revokeSafe(pairingId, packageName) }
    }

    fun requestUninstall(pairingId: String, packageName: String) {
        viewModelScope.launch { repo.requestUninstall(pairingId, packageName) }
    }

    fun requestScan(pairingId: String) {
        viewModelScope.launch { repo.requestScan(pairingId) }
    }

    private companion object {
        const val UNDO_WINDOW_MS = 10_000L
    }
}
