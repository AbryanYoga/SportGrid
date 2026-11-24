package com.abryan.sportgrid

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.abryan.sportgrid.data.local.AppThemeMode
import com.abryan.sportgrid.data.local.SettingsDataStore
import com.abryan.sportgrid.ui.navigation.SportGridAppShell
import com.abryan.sportgrid.ui.theme.SportGridTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val settingsDataStore = SettingsDataStore(this)

        setContent {
            val themeMode by settingsDataStore.themeMode.collectAsState(initial = AppThemeMode.SYSTEM)
            val isDark = when (themeMode) {
                AppThemeMode.LIGHT -> false
                AppThemeMode.DARK -> true
                AppThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            SportGridTheme(darkTheme = isDark) {
                SportGridAppShell()
            }
        }
    }
}
