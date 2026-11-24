package com.abryan.sportgrid.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.abryan.sportgrid.ui.components.SportGridBottomBar
import com.abryan.sportgrid.ui.screens.home.HomeScreen
import com.abryan.sportgrid.ui.screens.leagues.LeaguesScreen
import com.abryan.sportgrid.ui.screens.live.LiveScreen
import com.abryan.sportgrid.ui.screens.settings.SettingsScreen
import com.abryan.sportgrid.ui.screens.transfers.TransfersScreen

@Composable
fun SportGridAppShell(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            SportGridBottomBar(
                currentRoute = currentRoute,
                onNavigate = { screen ->
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { HomeScreen() }
            composable(Screen.Live.route) { LiveScreen() }
            composable(Screen.Leagues.route) { LeaguesScreen() }
            composable(Screen.Transfers.route) { TransfersScreen() }
            composable(Screen.Settings.route) { SettingsScreen() }
        }
    }
}
