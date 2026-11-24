package com.abryan.sportgrid.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.abryan.sportgrid.ui.components.SportGridBottomBar
import com.abryan.sportgrid.ui.screens.clubdetail.ClubDetailScreen
import com.abryan.sportgrid.ui.screens.coachdetail.CoachDetailScreen
import com.abryan.sportgrid.ui.screens.home.HomeScreen
import com.abryan.sportgrid.ui.screens.leaguedetail.LeagueDetailScreen
import com.abryan.sportgrid.ui.screens.leagues.LeaguesScreen
import com.abryan.sportgrid.ui.screens.live.LiveScreen
import com.abryan.sportgrid.ui.screens.profiles.ProfilesScreen
import com.abryan.sportgrid.ui.screens.settings.SettingsScreen
import com.abryan.sportgrid.ui.screens.transfers.TransfersScreen

@Composable
fun SportGridAppShell(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isTopLevelDestination = Screen.bottomNavItems.any { it.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (isTopLevelDestination) {
                SportGridBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { screen ->
                        if (screen.route == Screen.Home.route) {
                            if (currentRoute != Screen.Home.route) {
                                val popped = navController.popBackStack(Screen.Home.route, inclusive = false)
                                if (!popped) {
                                    navController.navigate(Screen.Home.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            inclusive = false
                                        }
                                        launchSingleTop = true
                                    }
                                }
                            }
                        } else {
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    }
                )
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToLeagues = {
                        navController.navigate(Screen.Leagues.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToTransfers = {
                        navController.navigate(Screen.Transfers.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToProfiles = {
                        navController.navigate("profiles")
                    },
                    onNavigateToMatchDetail = { /* Handled in fixtures */ }
                )
            }
            composable(Screen.Live.route) {
                LiveScreen()
            }
            composable(Screen.Leagues.route) {
                LeaguesScreen(
                    onLeagueClick = { leagueId, sportSlug ->
                        navController.navigate("league_detail/$leagueId/$sportSlug")
                    }
                )
            }
            composable(Screen.Transfers.route) {
                TransfersScreen()
            }
            composable(Screen.Settings.route) {
                SettingsScreen()
            }
            composable("profiles") {
                ProfilesScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateToCoach = { coachId ->
                        navController.navigate("coach_detail/$coachId")
                    },
                    onNavigateToClub = { clubId ->
                        navController.navigate("club_detail/$clubId")
                    }
                )
            }
            composable(
                route = "coach_detail/{coachId}",
                arguments = listOf(
                    navArgument("coachId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val coachId = backStackEntry.arguments?.getString("coachId") ?: ""
                CoachDetailScreen(
                    coachId = coachId,
                    onBack = { navController.popBackStack() },
                    onNavigateToClub = { clubId ->
                        navController.navigate("club_detail/$clubId")
                    }
                )
            }
            composable(
                route = "club_detail/{clubId}",
                arguments = listOf(
                    navArgument("clubId") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val clubId = backStackEntry.arguments?.getInt("clubId") ?: 0
                ClubDetailScreen(
                    clubId = clubId,
                    onBack = { navController.popBackStack() },
                    onNavigateToCoach = { coachId ->
                        navController.navigate("coach_detail/$coachId")
                    }
                )
            }
            composable(
                route = "league_detail/{leagueId}/{sportSlug}",
                arguments = listOf(
                    navArgument("leagueId") { type = NavType.IntType },
                    navArgument("sportSlug") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val leagueId = backStackEntry.arguments?.getInt("leagueId") ?: 0
                val sportSlug = backStackEntry.arguments?.getString("sportSlug") ?: "football"
                LeagueDetailScreen(
                    leagueId = leagueId,
                    sportSlug = sportSlug,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
