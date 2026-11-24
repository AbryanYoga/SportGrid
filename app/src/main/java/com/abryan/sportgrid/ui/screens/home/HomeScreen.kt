package com.abryan.sportgrid.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.abryan.sportgrid.R
import com.abryan.sportgrid.domain.model.League
import com.abryan.sportgrid.domain.model.Match
import com.abryan.sportgrid.domain.model.MatchStatus
import com.abryan.sportgrid.domain.model.SportType
import com.abryan.sportgrid.domain.model.Transfer
import com.abryan.sportgrid.ui.components.HeroLiveCard
import com.abryan.sportgrid.ui.components.LeagueCard
import com.abryan.sportgrid.ui.components.MatchCard
import com.abryan.sportgrid.ui.components.SportFilterChips
import com.abryan.sportgrid.ui.components.TransferCard
import com.abryan.sportgrid.ui.theme.SportGridTheme

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToLeagues: () -> Unit = {},
    onNavigateToTransfers: () -> Unit = {},
    onNavigateToMatchDetail: (Int) -> Unit = {}
) {
    var selectedSport by remember { mutableStateOf(SportType.FOOTBALL) }

    // Dummy preview data to verify responsive layout in Commit #10
    val sampleHeroMatch = remember {
        Match(
            id = 101,
            sportType = SportType.FOOTBALL,
            leagueName = "Premier League",
            leagueLogo = "https://media.api-sports.io/football/leagues/39.png",
            homeTeamName = "Arsenal",
            homeTeamLogo = "https://media.api-sports.io/football/teams/42.png",
            awayTeamName = "Chelsea",
            awayTeamLogo = "https://media.api-sports.io/football/teams/49.png",
            homeScore = 2,
            awayScore = 1,
            status = MatchStatus.LIVE,
            statusText = "68'",
            elapsedMinutes = 68,
            matchDate = "2026-11-24"
        )
    }

    val sampleLeagues = remember {
        listOf(
            League(39, "Premier League", "https://media.api-sports.io/football/leagues/39.png", "England", null, "League", SportType.FOOTBALL),
            League(140, "La Liga", "https://media.api-sports.io/football/leagues/140.png", "Spain", null, "League", SportType.FOOTBALL),
            League(12, "NBA", "https://media.api-sports.io/basketball/leagues/12.png", "USA", null, "League", SportType.BASKETBALL),
            League(135, "Serie A", "https://media.api-sports.io/football/leagues/135.png", "Italy", null, "League", SportType.FOOTBALL),
            League(2, "Champions League", "https://media.api-sports.io/football/leagues/2.png", "World", null, "Cup", SportType.FOOTBALL)
        )
    }

    val sampleTodayMatches = remember {
        listOf(
            Match(
                id = 201,
                sportType = SportType.BASKETBALL,
                leagueName = "NBA",
                leagueLogo = "https://media.api-sports.io/basketball/leagues/12.png",
                homeTeamName = "Thunder",
                homeTeamLogo = null,
                awayTeamName = "Pacers",
                awayTeamLogo = null,
                homeScore = 70,
                awayScore = 57,
                status = MatchStatus.LIVE,
                statusText = "3RD 6:02",
                elapsedMinutes = 30,
                matchDate = "2026-11-24"
            ),
            Match(
                id = 202,
                sportType = SportType.FOOTBALL,
                leagueName = "La Liga",
                leagueLogo = "https://media.api-sports.io/football/leagues/140.png",
                homeTeamName = "Real Madrid",
                homeTeamLogo = "https://media.api-sports.io/football/teams/541.png",
                awayTeamName = "Barcelona",
                awayTeamLogo = "https://media.api-sports.io/football/teams/529.png",
                homeScore = 3,
                awayScore = 2,
                status = MatchStatus.FINISHED,
                statusText = "FT",
                elapsedMinutes = 90,
                matchDate = "2026-11-24"
            ),
            Match(
                id = 203,
                sportType = SportType.FOOTBALL,
                leagueName = "Serie A",
                leagueLogo = null,
                homeTeamName = "Inter",
                homeTeamLogo = null,
                awayTeamName = "AC Milan",
                awayTeamLogo = null,
                homeScore = null,
                awayScore = null,
                status = MatchStatus.NOT_STARTED,
                statusText = "20:45",
                elapsedMinutes = null,
                matchDate = "2026-11-24"
            )
        )
    }

    val sampleTransfers = remember {
        listOf(
            Transfer(
                id = "1",
                playerName = "Kylian Mbappé",
                fromTeamName = "Paris Saint-Germain",
                fromTeamLogo = null,
                toTeamName = "Real Madrid",
                toTeamLogo = null,
                type = "Free Transfer",
                date = "2024-07-01"
            ),
            Transfer(
                id = "2",
                playerName = "Jude Bellingham",
                fromTeamName = "Borussia Dortmund",
                fromTeamLogo = null,
                toTeamName = "Real Madrid",
                toTeamLogo = null,
                type = "€103M",
                date = "2023-07-01"
            )
        )
    }

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Header: Top App Branding
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "SPORTGRID",
                            style = MaterialTheme.typography.displayMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = stringResource(R.string.app_tagline),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row {
                        IconButton(
                            onClick = onNavigateToLeagues,
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Sport Filter Chips
            item {
                SportFilterChips(
                    selectedSport = selectedSport,
                    onSportSelected = { selectedSport = it },
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Hero Live Match
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    HeroLiveCard(
                        match = sampleHeroMatch,
                        onClick = { onNavigateToMatchDetail(sampleHeroMatch.id) }
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Popular Leagues Section
            item {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.section_popular_leagues),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        TextButton(onClick = onNavigateToLeagues) {
                            Text(
                                text = stringResource(R.string.view_all),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(sampleLeagues) { league ->
                            LeagueCard(league = league)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Today's Matches Section
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.section_today_matches),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            items(sampleTodayMatches) { match ->
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    MatchCard(
                        match = match,
                        onClick = { onNavigateToMatchDetail(match.id) }
                    )
                }
            }

            // Latest Transfers (Football only)
            if (selectedSport.isFootball) {
                item {
                    Spacer(modifier = Modifier.height(18.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.section_latest_transfers),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        TextButton(onClick = onNavigateToTransfers) {
                            Text(
                                text = stringResource(R.string.view_all),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(sampleTransfers) { transfer ->
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)) {
                        TransferCard(transfer = transfer)
                    }
                }
            }
        }
    }
}

@Preview(name = "HomeScreen - Dark", showBackground = true)
@Composable
private fun HomeScreenDarkPreview() {
    SportGridTheme(darkTheme = true) {
        HomeScreen()
    }
}

@Preview(name = "HomeScreen - Light", showBackground = true)
@Composable
private fun HomeScreenLightPreview() {
    SportGridTheme(darkTheme = false) {
        HomeScreen()
    }
}
