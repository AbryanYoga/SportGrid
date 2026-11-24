package com.abryan.sportgrid.ui.screens.profiles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.abryan.sportgrid.data.repository.PersonRepository
import com.abryan.sportgrid.domain.model.PersonProfile
import com.abryan.sportgrid.domain.model.PersonRole
import com.abryan.sportgrid.domain.model.SportType
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfilesUiState(
    val selectedSport: SportType = SportType.FOOTBALL,
    val selectedRole: PersonRole = PersonRole.ALL,
    val searchQuery: String = "",
    val profiles: List<PersonProfile> = emptyList(),
    val selectedProfile: PersonProfile? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ProfilesViewModel(
    private val personRepository: PersonRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfilesUiState())
    val uiState: StateFlow<ProfilesUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadProfiles()
    }

    fun onSportSelected(sportType: SportType) {
        if (_uiState.value.selectedSport == sportType) return
        _uiState.update { it.copy(selectedSport = sportType) }
        loadProfiles()
    }

    fun onRoleSelected(role: PersonRole) {
        if (_uiState.value.selectedRole == role) return
        _uiState.update { it.copy(selectedRole = role) }
        loadProfiles()
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(400) // Debounce search
            loadProfiles()
        }
    }

    fun onProfileClicked(profile: PersonProfile) {
        _uiState.update { it.copy(selectedProfile = profile) }
    }

    fun onDismissDetail() {
        _uiState.update { it.copy(selectedProfile = null) }
    }

    fun refresh() {
        loadProfiles()
    }

    private fun loadProfiles() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = personRepository.getProfiles(
                sportType = state.selectedSport,
                role = state.selectedRole,
                query = state.searchQuery
            )
            result.onSuccess { list ->
                _uiState.update { it.copy(profiles = list, isLoading = false) }
            }.onFailure { err ->
                _uiState.update { it.copy(errorMessage = err.message, isLoading = false) }
            }
        }
    }

    companion object {
        fun provideFactory(repository: PersonRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ProfilesViewModel(repository) as T
                }
            }
    }
}
