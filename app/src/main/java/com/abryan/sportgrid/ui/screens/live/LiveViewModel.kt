package com.abryan.sportgrid.ui.screens.live

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.abryan.sportgrid.SportGridApp
import com.abryan.sportgrid.data.repository.MatchRepository
import com.abryan.sportgrid.domain.model.Match
import com.abryan.sportgrid.domain.model.SportType
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class LiveUiState(
    val selectedSport: SportType = SportType.BASKETBALL,
    val selectedDateOffset: Int = 0, // 0 = today, -1 = yesterday, +1 = tomorrow
    val searchQuery: String = "",
    val liveMatches: List<Match> = emptyList(),
    val todayMatches: List<Match> = emptyList(),
    val isLoading: Boolean = false,
    val isAutoRefreshing: Boolean = false,
    val isFallback: Boolean = false,
    val errorMessage: String? = null
)

class LiveViewModel(
    application: Application,
    private val matchRepo: MatchRepository
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(LiveUiState())
    val uiState: StateFlow<LiveUiState> = _uiState.asStateFlow()

    private var autoRefreshJob: Job? = null

    init {
        loadScores(forceRefresh = false)
        startAutoRefresh()
    }

    fun onSportSelected(sport: SportType) {
        if (_uiState.value.selectedSport == sport) return
        _uiState.update { it.copy(selectedSport = sport) }
        loadScores(forceRefresh = false)
    }

    fun onDateSelected(offset: Int) {
        _uiState.update { it.copy(selectedDateOffset = offset) }
        loadScores(forceRefresh = false)
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun refresh() {
        loadScores(forceRefresh = true)
    }

    private fun loadScores(forceRefresh: Boolean = false) {
        val sport = _uiState.value.selectedSport
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = !it.isAutoRefreshing, errorMessage = null) }

            val liveResult = matchRepo.getLiveMatches(sport, forceRefresh)
            val todayResult = matchRepo.getTodayMatches(sport, forceRefresh)

            val live = liveResult.getOrNull().orEmpty()
            val today = todayResult.getOrNull().orEmpty()

            _uiState.update { current ->
                current.copy(
                    liveMatches = live,
                    todayMatches = today,
                    isFallback = live.isEmpty(),
                    isLoading = false,
                    isAutoRefreshing = false,
                    errorMessage = null
                )
            }
        }
    }

    private fun startAutoRefresh() {
        autoRefreshJob?.cancel()
        autoRefreshJob = viewModelScope.launch {
            while (isActive) {
                delay(60_000L) // 60 seconds interval to respect quota
                _uiState.update { it.copy(isAutoRefreshing = true) }
                loadScores(forceRefresh = true)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        autoRefreshJob?.cancel()
    }

    companion object {
        fun provideFactory(application: Application): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val app = application as SportGridApp
                    return LiveViewModel(
                        application = application,
                        matchRepo = app.container.matchRepository
                    ) as T
                }
            }
    }
}
