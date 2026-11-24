package com.abryan.sportgrid.ui.screens.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.abryan.sportgrid.R
import com.abryan.sportgrid.SportGridApp
import com.abryan.sportgrid.data.repository.LeagueRepository
import com.abryan.sportgrid.data.repository.MatchRepository
import com.abryan.sportgrid.data.repository.TransferRepository
import com.abryan.sportgrid.domain.model.League
import com.abryan.sportgrid.domain.model.Match
import com.abryan.sportgrid.domain.model.MatchStatus
import com.abryan.sportgrid.domain.model.SportType
import com.abryan.sportgrid.domain.model.Transfer
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val selectedSport: SportType = SportType.FOOTBALL,
    val heroMatch: Match? = null,
    val popularLeagues: List<League> = emptyList(),
    val todayMatches: List<Match> = emptyList(),
    val latestTransfers: List<Transfer> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val errorMessageRes: Int? = null
)

class HomeViewModel(
    application: Application,
    private val leagueRepo: LeagueRepository,
    private val matchRepo: MatchRepository,
    private val transferRepo: TransferRepository
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData(forceRefresh = false)
    }

    fun onSportSelected(sport: SportType) {
        if (_uiState.value.selectedSport == sport) return
        _uiState.update { it.copy(selectedSport = sport) }
        loadDashboardData(forceRefresh = false)
    }

    fun refresh() {
        loadDashboardData(forceRefresh = true)
    }

    private fun loadDashboardData(forceRefresh: Boolean) {
        val sport = _uiState.value.selectedSport
        viewModelScope.launch {
            _uiState.update {
                if (forceRefresh) it.copy(isRefreshing = true)
                else it.copy(isLoading = true, errorMessage = null, errorMessageRes = null)
            }

            try {
                val liveDeferred = async { matchRepo.getLiveMatches(sport, forceRefresh) }
                val leaguesDeferred = async { leagueRepo.getPopularLeagues(sport, forceRefresh) }
                val todayDeferred = async { matchRepo.getTodayMatches(sport, forceRefresh) }
                val transferDeferred = async {
                    if (sport.isFootball) transferRepo.getRecentTransfers(forceRefresh)
                    else Result.success(emptyList())
                }

                val liveResult = liveDeferred.await()
                val leaguesResult = leaguesDeferred.await()
                val todayResult = todayDeferred.await()
                val transferResult = transferDeferred.await()

                val liveMatches = liveResult.getOrNull().orEmpty()
                val todayMatches = todayResult.getOrNull().orEmpty()
                val leagues = leaguesResult.getOrNull().orEmpty()
                val transfers = transferResult.getOrNull().orEmpty()

                // Hero match priority: first live match, otherwise first today match
                val hero = liveMatches.firstOrNull() ?: todayMatches.firstOrNull()

                _uiState.update { current ->
                    current.copy(
                        heroMatch = hero,
                        popularLeagues = leagues,
                        todayMatches = todayMatches,
                        latestTransfers = transfers,
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = null,
                        errorMessageRes = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { current ->
                    current.copy(
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = e.localizedMessage,
                        errorMessageRes = R.string.state_error_network
                    )
                }
            }
        }
    }

    companion object {
        fun provideFactory(application: Application): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val app = application as SportGridApp
                    return HomeViewModel(
                        application = application,
                        leagueRepo = app.container.leagueRepository,
                        matchRepo = app.container.matchRepository,
                        transferRepo = app.container.transferRepository
                    ) as T
                }
            }
    }
}
