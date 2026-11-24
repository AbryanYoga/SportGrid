package com.abryan.sportgrid.ui.screens.leaguedetail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.abryan.sportgrid.SportGridApp
import com.abryan.sportgrid.data.repository.LeagueRepository
import com.abryan.sportgrid.data.repository.MatchRepository
import com.abryan.sportgrid.domain.model.Match
import com.abryan.sportgrid.domain.model.SportType
import com.abryan.sportgrid.domain.model.Standing
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LeagueDetailUiState(
    val leagueId: Int = 0,
    val sportType: SportType = SportType.FOOTBALL,
    val leagueName: String = "",
    val selectedTab: Int = 0, // 0 = Fixtures, 1 = Standings
    val matches: List<Match> = emptyList(),
    val standings: List<Standing> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class LeagueDetailViewModel(
    application: Application,
    private val leagueId: Int,
    private val sportType: SportType,
    private val leagueRepo: LeagueRepository,
    private val matchRepo: MatchRepository
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(
        LeagueDetailUiState(leagueId = leagueId, sportType = sportType)
    )
    val uiState: StateFlow<LeagueDetailUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun onTabSelected(tab: Int) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun refresh() {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val matchesDeferred = async { matchRepo.getLeagueMatches(sportType, leagueId) }
            val standingsDeferred = async { leagueRepo.getStandings(sportType, leagueId) }

            val matchesResult = matchesDeferred.await()
            val standingsResult = standingsDeferred.await()

            val matches = matchesResult.getOrNull().orEmpty()
            val standings = standingsResult.getOrNull().orEmpty()

            _uiState.update { current ->
                current.copy(
                    matches = matches,
                    standings = standings,
                    leagueName = matches.firstOrNull()?.leagueName ?: "League Details",
                    isLoading = false,
                    errorMessage = null
                )
            }
        }
    }

    companion object {
        fun provideFactory(
            application: Application,
            leagueId: Int,
            sportSlug: String
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val app = application as SportGridApp
                    val sportType = SportType.values().firstOrNull { it.slug == sportSlug } ?: SportType.FOOTBALL
                    return LeagueDetailViewModel(
                        application = application,
                        leagueId = leagueId,
                        sportType = sportType,
                        leagueRepo = app.container.leagueRepository,
                        matchRepo = app.container.matchRepository
                    ) as T
                }
            }
    }
}
