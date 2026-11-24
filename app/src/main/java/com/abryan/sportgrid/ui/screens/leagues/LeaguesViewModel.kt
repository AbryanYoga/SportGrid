package com.abryan.sportgrid.ui.screens.leagues

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.abryan.sportgrid.SportGridApp
import com.abryan.sportgrid.data.repository.LeagueRepository
import com.abryan.sportgrid.domain.model.League
import com.abryan.sportgrid.domain.model.SportType
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LeaguesUiState(
    val selectedSport: SportType = SportType.FOOTBALL,
    val searchQuery: String = "",
    val leagues: List<League> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class LeaguesViewModel(
    application: Application,
    private val leagueRepo: LeagueRepository
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(LeaguesUiState())
    val uiState: StateFlow<LeaguesUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadLeagues()
    }

    fun onSportSelected(sport: SportType) {
        if (_uiState.value.selectedSport == sport) return
        _uiState.update { it.copy(selectedSport = sport, searchQuery = "") }
        loadLeagues()
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(400) // debounce search
            if (query.isBlank()) {
                loadLeagues()
            } else {
                searchLeagues(query)
            }
        }
    }

    fun refresh() {
        loadLeagues(forceRefresh = true)
    }

    private fun loadLeagues(forceRefresh: Boolean = false) {
        val sport = _uiState.value.selectedSport
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = leagueRepo.getPopularLeagues(sport, forceRefresh)
            _uiState.update { current ->
                if (result.isSuccess) {
                    current.copy(
                        leagues = result.getOrNull().orEmpty(),
                        isLoading = false,
                        errorMessage = null
                    )
                } else {
                    current.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.localizedMessage
                    )
                }
            }
        }
    }

    private fun searchLeagues(query: String) {
        val sport = _uiState.value.selectedSport
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = leagueRepo.searchLeagues(sport, query)
            _uiState.update { current ->
                if (result.isSuccess) {
                    current.copy(
                        leagues = result.getOrNull().orEmpty(),
                        isLoading = false,
                        errorMessage = null
                    )
                } else {
                    current.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.localizedMessage
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
                    return LeaguesViewModel(
                        application = application,
                        leagueRepo = app.container.leagueRepository
                    ) as T
                }
            }
    }
}
