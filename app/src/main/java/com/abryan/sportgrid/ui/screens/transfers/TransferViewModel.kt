package com.abryan.sportgrid.ui.screens.transfers

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.abryan.sportgrid.SportGridApp
import com.abryan.sportgrid.data.repository.ClubOption
import com.abryan.sportgrid.data.repository.TransferRepository
import com.abryan.sportgrid.domain.model.SportType
import com.abryan.sportgrid.domain.model.Transfer
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TransferUiState(
    val selectedSport: SportType = SportType.FOOTBALL,
    val selectedClub: ClubOption? = null,
    val clubs: List<ClubOption> = emptyList(),
    val transfers: List<Transfer> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class TransferViewModel(
    application: Application,
    private val transferRepo: TransferRepository
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(
        TransferUiState(
            clubs = transferRepo.defaultPopularClubs,
            selectedClub = transferRepo.defaultPopularClubs.firstOrNull()
        )
    )
    val uiState: StateFlow<TransferUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadTransfersForSelectedClub()
    }

    fun onSportChanged(sport: SportType) {
        _uiState.update { it.copy(selectedSport = sport) }
    }

    fun onClubSelected(club: ClubOption) {
        if (_uiState.value.selectedClub?.id == club.id) return
        _uiState.update { it.copy(selectedClub = club) }
        loadTransfersForSelectedClub()
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(400)
            if (query.isBlank()) {
                _uiState.update { it.copy(clubs = transferRepo.defaultPopularClubs) }
            } else {
                val result = transferRepo.searchTeams(query)
                if (result.isSuccess) {
                    _uiState.update { it.copy(clubs = result.getOrNull().orEmpty()) }
                }
            }
        }
    }

    fun refresh() {
        loadTransfersForSelectedClub(forceRefresh = true)
    }

    private fun loadTransfersForSelectedClub(forceRefresh: Boolean = false) {
        val club = _uiState.value.selectedClub ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = transferRepo.getTransfersByTeam(club.id, forceRefresh)
            _uiState.update { current ->
                if (result.isSuccess) {
                    current.copy(
                        transfers = result.getOrNull().orEmpty(),
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
                    return TransferViewModel(
                        application = application,
                        transferRepo = app.container.transferRepository
                    ) as T
                }
            }
    }
}
