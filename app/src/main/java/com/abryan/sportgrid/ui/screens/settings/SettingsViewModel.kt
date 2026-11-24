package com.abryan.sportgrid.ui.screens.settings

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.abryan.sportgrid.SportGridApp
import com.abryan.sportgrid.data.local.AppThemeMode
import com.abryan.sportgrid.data.local.SettingsDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    application: Application,
    private val settingsDataStore: SettingsDataStore
) : AndroidViewModel(application) {

    val themeMode: StateFlow<AppThemeMode> = settingsDataStore.themeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppThemeMode.SYSTEM)

    val language: StateFlow<String> = settingsDataStore.language
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "en")

    fun setThemeMode(mode: AppThemeMode) {
        viewModelScope.launch {
            settingsDataStore.setThemeMode(mode)
        }
    }

    fun setLanguage(langCode: String) {
        viewModelScope.launch {
            settingsDataStore.setLanguage(langCode)
            AppCompatDelegate.setApplicationLocales(
                LocaleListCompat.forLanguageTags(langCode)
            )
        }
    }

    companion object {
        fun provideFactory(application: Application): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val dataStore = SettingsDataStore(application)
                    return SettingsViewModel(application, dataStore) as T
                }
            }
    }
}
