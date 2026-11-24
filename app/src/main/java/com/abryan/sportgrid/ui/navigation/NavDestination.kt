package com.abryan.sportgrid.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.ui.graphics.vector.ImageVector
import com.abryan.sportgrid.R

sealed class Screen(
    val route: String,
    @StringRes val titleRes: Int,
    val icon: ImageVector
) {
    object Home : Screen("home", R.string.nav_home, Icons.Default.Home)
    object Live : Screen("live", R.string.nav_live, Icons.Default.EmojiEvents)
    object Leagues : Screen("leagues", R.string.nav_leagues, Icons.Default.Language)
    object Transfers : Screen("transfers", R.string.nav_transfers, Icons.Default.SwapHoriz)
    object Settings : Screen("settings", R.string.nav_settings, Icons.Default.Settings)

    companion object {
        val bottomNavItems = listOf(Home, Live, Leagues, Transfers, Settings)
    }
}
