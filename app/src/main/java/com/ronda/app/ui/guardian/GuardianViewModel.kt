package com.ronda.app.ui.guardian

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ronda.app.core.RiskEvaluator
import com.ronda.app.core.Verdict
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

class GuardianViewModel(private val repo: GuardianRepository) : ViewModel() {

    private val _state = MutableStateFlow(GuardianUiState(protectedName = repo.protectedName))
    val state: StateFlow<GuardianUiState> = _state.asStateFlow()

    private var undoJob: kotlinx.coroutines.Job? = null

    init {
        viewModelScope.launch {
            repo.observeVerdicts().collect { verdicts ->
                // Two questions, in order. First: has the guardian ruled on it?
                // If so it is history and leaves both tabs. Only among the ones
                // still open does the score decide which tab it belongs to —
                // that is what keeps "Terpantau" meaning "quiet", not "done".
                val (decided, open) = verdicts.partition { it.state.decided }
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

    fun find(packageName: String?): Verdict? {
        if (packageName == null) return null
        val s = _state.value
        return (s.needsReview + s.monitored + s.history)
            .firstOrNull { it.packageName == packageName }
    }

    fun markUnsafe(packageName: String) {
        viewModelScope.launch { repo.decide(packageName, safe = false) }
    }

    /**
     * Marking safe removes protection, so it is the only decision that is
     * reversible — the guardian gets ten seconds to take it back.
     */
    fun markSafe(packageName: String) {
        viewModelScope.launch { repo.decide(packageName, safe = true) }
        undoJob?.cancel()
        _state.value = _state.value.copy(undoable = packageName)
        undoJob = viewModelScope.launch {
            delay(UNDO_WINDOW_MS)
            if (_state.value.undoable == packageName) {
                _state.value = _state.value.copy(undoable = null)
            }
        }
    }

    fun undoMarkSafe(packageName: String) {
        undoJob?.cancel()
        _state.value = _state.value.copy(undoable = null)
        viewModelScope.launch { repo.decide(packageName, safe = false) }
    }

    fun requestUninstall(packageName: String) {
        viewModelScope.launch { repo.requestUninstall(packageName) }
    }

    private companion object {
        const val UNDO_WINDOW_MS = 10_000L
    }
}
