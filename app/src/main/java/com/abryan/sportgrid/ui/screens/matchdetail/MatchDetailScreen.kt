package com.abryan.sportgrid.ui.screens.matchdetail

import android.content.res.Configuration
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Stadium
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.abryan.sportgrid.SportGridApp
import com.abryan.sportgrid.domain.model.H2HMatch
import com.abryan.sportgrid.domain.model.H2HSummary
import com.abryan.sportgrid.domain.model.LineupPlayer
import com.abryan.sportgrid.domain.model.MatchDetailData
import com.abryan.sportgrid.domain.model.MatchEvent
import com.abryan.sportgrid.domain.model.MatchStatItem
import com.abryan.sportgrid.domain.model.TeamTactics
import com.abryan.sportgrid.ui.components.ErrorState
import com.abryan.sportgrid.ui.theme.VoltGreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchDetailScreen(
    matchId: Int,
    onBack: () -> Unit,
    onNavigateToCoach: (String) -> Unit = {},
    onNavigateToClub: (Int) -> Unit = {},
    onNavigateToPlayer: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val app = LocalContext.current.applicationContext as SportGridApp
    val coroutineScope = rememberCoroutineScope()
    var detailData by remember { mutableStateOf<MatchDetailData?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedTab by remember { mutableIntStateOf(0) }

    // Persistent Favorite Status
    val favoriteMatches by app.container.favoritesDataStore.favoriteMatches.collectAsState(initial = emptySet())
    val isFavorite = favoriteMatches.contains(matchId.toString())

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    LaunchedEffect(matchId) {
        isLoading = true
        detailData = app.container.matchRepository.getMatchDetail(matchId)
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "TAKTIK & STATISTIK LAGA",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            app.container.favoritesDataStore.toggleFavoriteMatch(matchId)
                        }
                    }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Favorit",
                            tint = if (isFavorite) VoltGreen else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            detailData == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    ErrorState(
                        message = "Data pertandingan tidak ditemukan.",
                        onRetry = onBack
                    )
                }
            }
            else -> {
                val data = detailData!!
                if (isLandscape) {
                    // LANDSCAPE DUAL COLUMN
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Left Column: Scoreboard Hero & Formation Overview
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 24.dp)
                        ) {
                            item {
                                MatchScoreboardCard(data = data)
                                Spacer(modifier = Modifier.height(12.dp))
                                TacticalComparisonCard(
                                    homeTeam = data.match.homeTeamName,
                                    awayTeam = data.match.awayTeamName,
                                    homeTactics = data.homeTactics,
                                    awayTactics = data.awayTactics
                                )
                            }
                        }

                        // Right Column: Tab Content
                        Column(
                            modifier = Modifier
                                .weight(1.2f)
                                .fillMaxSize()
                        ) {
                            DetailTabsHeader(
                                selectedTab = selectedTab,
                                onTabSelected = { selectedTab = it }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(bottom = 24.dp)
                            ) {
                                when (selectedTab) {
                                    0 -> {
                                        item {
                                            TacticalPitchView(
                                                homeTeam = data.match.homeTeamName,
                                                awayTeam = data.match.awayTeamName,
                                                homeTactics = data.homeTactics,
                                                awayTactics = data.awayTactics,
                                                onPlayerClick = { player ->
                                                    val playerId = mapPlayerNameToId(player.name)
                                                    onNavigateToPlayer(playerId)
                                                }
                                            )
                                            Spacer(modifier = Modifier.height(14.dp))
                                            StartingLineupCard(
                                                teamName = data.match.homeTeamName,
                                                tactics = data.homeTactics,
                                                isHome = true,
                                                onPlayerClick = { onNavigateToPlayer(mapPlayerNameToId(it.name)) }
                                            )
                                            Spacer(modifier = Modifier.height(10.dp))
                                            StartingLineupCard(
                                                teamName = data.match.awayTeamName,
                                                tactics = data.awayTactics,
                                                isHome = false,
                                                onPlayerClick = { onNavigateToPlayer(mapPlayerNameToId(it.name)) }
                                            )
                                        }
                                    }
                                    1 -> {
                                        item {
                                            MatchPressureMomentumCard(momentumPoints = data.momentumPoints)
                                            Spacer(modifier = Modifier.height(12.dp))
                                            StatisticsTabContent(statistics = data.statistics)
                                        }
                                    }
                                    2 -> {
                                        item {
                                            data.h2hSummary?.let { h2h ->
                                                HeadToHeadTabContent(
                                                    homeTeam = data.match.homeTeamName,
                                                    awayTeam = data.match.awayTeamName,
                                                    h2h = h2h
                                                )
                                            }
                                        }
                                    }
                                    3 -> {
                                        item {
                                            TimelineTabContent(events = data.events)
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // PORTRAIT SINGLE VERTICAL STREAM
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            MatchScoreboardCard(data = data)
                        }

                        item {
                            DetailTabsHeader(
                                selectedTab = selectedTab,
                                onTabSelected = { selectedTab = it }
                            )
                        }

                        when (selectedTab) {
                            0 -> {
                                item {
                                    TacticalComparisonCard(
                                        homeTeam = data.match.homeTeamName,
                                        awayTeam = data.match.awayTeamName,
                                        homeTactics = data.homeTactics,
                                        awayTactics = data.awayTactics
                                    )
                                }
                                item {
                                    TacticalPitchView(
                                        homeTeam = data.match.homeTeamName,
                                        awayTeam = data.match.awayTeamName,
                                        homeTactics = data.homeTactics,
                                        awayTactics = data.awayTactics,
                                        onPlayerClick = { player ->
                                            val playerId = mapPlayerNameToId(player.name)
                                            onNavigateToPlayer(playerId)
                                        }
                                    )
                                }
                                item {
                                    StartingLineupCard(
                                        teamName = data.match.homeTeamName,
                                        tactics = data.homeTactics,
                                        isHome = true,
                                        onPlayerClick = { onNavigateToPlayer(mapPlayerNameToId(it.name)) }
                                    )
                                }
                                item {
                                    StartingLineupCard(
                                        teamName = data.match.awayTeamName,
                                        tactics = data.awayTactics,
                                        isHome = false,
                                        onPlayerClick = { onNavigateToPlayer(mapPlayerNameToId(it.name)) }
                                    )
                                }
                            }
                            1 -> {
                                item {
                                    MatchPressureMomentumCard(momentumPoints = data.momentumPoints)
                                }
                                item {
                                    StatisticsTabContent(statistics = data.statistics)
                                }
                            }
                            2 -> {
                                item {
                                    data.h2hSummary?.let { h2h ->
                                        HeadToHeadTabContent(
                                            homeTeam = data.match.homeTeamName,
                                            awayTeam = data.match.awayTeamName,
                                            h2h = h2h
                                        )
                                    }
                                }
                            }
                            3 -> {
                                item {
                                    TimelineTabContent(events = data.events)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun mapPlayerNameToId(playerName: String): String {
    return when {
        playerName.contains("Mbappé", ignoreCase = true) -> "f_p1"
        playerName.contains("Bellingham", ignoreCase = true) -> "f_p2"
        playerName.contains("Haaland", ignoreCase = true) -> "f_p3"
        playerName.contains("Messi", ignoreCase = true) -> "f_p4"
        playerName.contains("Ronaldo", ignoreCase = true) -> "f_p5"
        playerName.contains("Yamal", ignoreCase = true) -> "f_p6"
        playerName.contains("Curry", ignoreCase = true) -> "b_p1"
        playerName.contains("James", ignoreCase = true) -> "b_p2"
        playerName.contains("Ohtani", ignoreCase = true) -> "bb_p1"
        else -> "f_p1"
    }
}

@Composable
private fun MatchScoreboardCard(data: MatchDetailData) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_scoreboard")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (data.match.isLive) VoltGreen.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // League and Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = data.match.leagueName,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                if (data.match.isLive) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(VoltGreen.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .scale(pulseScale)
                                    .clip(CircleShape)
                                    .background(VoltGreen)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = data.match.statusText.ifEmpty { "LIVE" },
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = VoltGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = data.match.statusText.ifEmpty { "SELESAI" },
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Scoreboard: Home - Score - Away
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Home Team
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        if (!data.match.homeTeamLogo.isNullOrEmpty()) {
                            AsyncImage(
                                model = data.match.homeTeamLogo,
                                contentDescription = data.match.homeTeamName,
                                modifier = Modifier.size(38.dp)
                            )
                        } else {
                            Text(
                                text = data.match.homeTeamName.take(1),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = data.match.homeTeamName,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Scores
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${data.match.homeScore ?: 0}",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 38.sp
                            ),
                            color = if (data.match.isLive) VoltGreen else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = " : ",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Light,
                                fontSize = 32.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${data.match.awayScore ?: 0}",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 38.sp
                            ),
                            color = if (data.match.isLive) VoltGreen else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "VS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 2.sp
                        )
                    )
                }

                // Away Team
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        if (!data.match.awayTeamLogo.isNullOrEmpty()) {
                            AsyncImage(
                                model = data.match.awayTeamLogo,
                                contentDescription = data.match.awayTeamName,
                                modifier = Modifier.size(38.dp)
                            )
                        } else {
                            Text(
                                text = data.match.awayTeamName.take(1),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = data.match.awayTeamName,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Stadium & Referee Footer
            if (!data.stadium.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.Stadium,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = data.stadium,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailTabsHeader(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    TabRow(
        selectedTabIndex = selectedTab,
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.primary,
        indicator = { tabPositions ->
            SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                color = VoltGreen
            )
        },
        divider = {}
    ) {
        listOf(
            "TAKTIK",
            "STATISTIK",
            "HEAD-TO-HEAD",
            "LINIMASA"
        ).forEachIndexed { index, title ->
            Tab(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                text = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 10.sp,
                            color = if (selectedTab == index) VoltGreen else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            )
        }
    }
}

@Composable
private fun TacticalComparisonCard(
    homeTeam: String,
    awayTeam: String,
    homeTactics: TeamTactics,
    awayTactics: TeamTactics
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "PENDEKATAN TAKTIK & FORMASI",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    color = VoltGreen
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Home Team Tactics
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = homeTeam,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(VoltGreen.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = homeTactics.formation,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = VoltGreen
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Pelatih: ${homeTactics.managerName}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Text(
                    text = homeTactics.tacticalStyle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            )

            // Away Team Tactics
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = awayTeam,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF2979FF).copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = awayTactics.formation,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2979FF)
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Pelatih: ${awayTactics.managerName}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF2979FF),
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Text(
                    text = awayTactics.tacticalStyle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun TacticalPitchView(
    homeTeam: String,
    awayTeam: String,
    homeTactics: TeamTactics,
    awayTactics: TeamTactics,
    onPlayerClick: (LineupPlayer) -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF132B16)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E5D36)),
        modifier = Modifier
            .fillMaxWidth()
            .height(340.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // Field markings background
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Goal Area (Away)
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(30.dp)
                        .align(Alignment.CenterHorizontally)
                        .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                )

                // Halfway Line
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color.White.copy(alpha = 0.3f))
                )

                // Bottom Goal Area (Home)
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(30.dp)
                        .align(Alignment.CenterHorizontally)
                        .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                )
            }

            // Center Circle
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .align(Alignment.Center)
                    .border(1.dp, Color.White.copy(alpha = 0.25f), CircleShape)
            )

            // INTERACTIVE PLAYER FORMATION NODES
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Half: Away Team Nodes (Blue Theme)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Away Attackers
                    PitchPlayerRow(players = awayTactics.starting11.takeLast(3), isHome = false, onPlayerClick = onPlayerClick)
                    // Away Midfielders
                    PitchPlayerRow(players = awayTactics.starting11.drop(4).take(3), isHome = false, onPlayerClick = onPlayerClick)
                    // Away Defenders
                    PitchPlayerRow(players = awayTactics.starting11.drop(1).take(3), isHome = false, onPlayerClick = onPlayerClick)
                }

                // Middle Pitch Label
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "SENTUH PEMAIN UNTUK BIODATA",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = VoltGreen,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                }

                // Bottom Half: Home Team Nodes (Volt Green Theme)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Home Defenders
                    PitchPlayerRow(players = homeTactics.starting11.drop(1).take(3), isHome = true, onPlayerClick = onPlayerClick)
                    // Home Midfielders
                    PitchPlayerRow(players = homeTactics.starting11.drop(4).take(3), isHome = true, onPlayerClick = onPlayerClick)
                    // Home Attackers
                    PitchPlayerRow(players = homeTactics.starting11.takeLast(3), isHome = true, onPlayerClick = onPlayerClick)
                }
            }
        }
    }
}

@Composable
private fun PitchPlayerRow(
    players: List<LineupPlayer>,
    isHome: Boolean,
    onPlayerClick: (LineupPlayer) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        players.forEach { player ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onPlayerClick(player) }
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(if (isHome) VoltGreen else Color(0xFF2979FF))
                        .border(1.5.dp, Color.White, CircleShape)
                ) {
                    Text(
                        text = "${player.number}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            color = if (isHome) Color.Black else Color.White
                        )
                    )
                }
                Text(
                    text = player.name.split(" ").lastOrNull() ?: player.name,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun MatchPressureMomentumCard(momentumPoints: List<Float>) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.ShowChart,
                    contentDescription = null,
                    tint = VoltGreen,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "MOMENTUM TEKANAN SERANGAN (0' - 90')",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Bar chart representing attacking pressure swings
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                momentumPoints.forEach { point ->
                    val isHomeDominant = point >= 0
                    val barHeightFraction = (kotlin.math.abs(point)).coerceIn(0.15f, 1.0f)
                    Box(
                        modifier = Modifier
                            .width(10.dp)
                            .fillMaxHeight(barHeightFraction)
                            .clip(RoundedCornerShape(3.dp))
                            .background(if (isHomeDominant) VoltGreen else Color(0xFF2979FF))
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(VoltGreen))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Tekanan Tuan Rumah", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF2979FF)))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Tekanan Tim Tamu", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp))
                }
            }
        }
    }
}

@Composable
private fun HeadToHeadTabContent(
    homeTeam: String,
    awayTeam: String,
    h2h: H2HSummary
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Win Probability Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.CompareArrows, contentDescription = null, tint = VoltGreen, modifier = Modifier.size(20.dp))
                    Text(
                        text = "PELUANG KEMENANGAN (WIN PROBABILITY)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Probability % labels
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("$homeTeam ${h2h.winProbabilityHome}%", style = MaterialTheme.typography.labelMedium.copy(color = VoltGreen, fontWeight = FontWeight.Bold))
                    Text("Seri ${h2h.winProbabilityDraw}%", style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                    Text("${h2h.winProbabilityAway}% $awayTeam", style = MaterialTheme.typography.labelMedium.copy(color = Color(0xFF2979FF), fontWeight = FontWeight.Bold))
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Triple Segmented Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                ) {
                    Box(modifier = Modifier.fillMaxHeight().weight(h2h.winProbabilityHome.toFloat()).background(VoltGreen))
                    Box(modifier = Modifier.fillMaxHeight().weight(h2h.winProbabilityDraw.toFloat()).background(Color.Gray.copy(alpha = 0.5f)))
                    Box(modifier = Modifier.fillMaxHeight().weight(h2h.winProbabilityAway.toFloat()).background(Color(0xFF2979FF)))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stats summary
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    H2HMetricItem(label = "Menang Home", value = "${h2h.homeWins}", color = VoltGreen)
                    H2HMetricItem(label = "Hasil Seri", value = "${h2h.draws}", color = MaterialTheme.colorScheme.onSurface)
                    H2HMetricItem(label = "Menang Away", value = "${h2h.awayWins}", color = Color(0xFF2979FF))
                }
            }
        }

        // Recent 5 Encounters
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "5 PERTEMUAN TERAKHIR",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp, color = VoltGreen)
                )

                Spacer(modifier = Modifier.height(12.dp))

                h2h.recentMatches.forEach { match ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${match.homeTeam} vs ${match.awayTeam}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${match.date} • ${match.competition}",
                                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${match.homeScore} - ${match.awayScore}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Black, color = VoltGreen)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun H2HMetricItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black, color = color))
        Text(text = label, style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp))
    }
}

@Composable
private fun StartingLineupCard(
    teamName: String,
    tactics: TeamTactics,
    isHome: Boolean,
    onPlayerClick: (LineupPlayer) -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LINEUP: $teamName",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isHome) VoltGreen else Color(0xFF2979FF)
                    )
                )
                Text(
                    text = tactics.formation,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            tactics.starting11.forEach { player ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPlayerClick(player) }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "#${player.number}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isHome) VoltGreen else Color(0xFF2979FF)
                            ),
                            modifier = Modifier.width(32.dp)
                        )
                        Text(
                            text = player.name,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            fontSize = 13.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = player.position,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatisticsTabContent(statistics: List<MatchStatItem>) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.Assessment,
                    contentDescription = null,
                    tint = VoltGreen,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "STATISTIK KOMPARASI PERTANDINGAN",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            statistics.forEach { item ->
                StatComparisonBar(item = item)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun StatComparisonBar(item: MatchStatItem) {
    val total = (item.homeNumeric + item.awayNumeric).coerceAtLeast(1f)
    val homeRatio = item.homeNumeric / total

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = item.homeValue,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = VoltGreen)
            )
            Text(
                text = item.label,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            )
            Text(
                text = item.awayValue,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF2979FF))
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Visual dual-colored bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(homeRatio.coerceAtLeast(0.05f))
                    .background(VoltGreen)
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight((1f - homeRatio).coerceAtLeast(0.05f))
                    .background(Color(0xFF2979FF))
            )
        }
    }
}

@Composable
private fun TimelineTabContent(events: List<MatchEvent>) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.History,
                    contentDescription = null,
                    tint = VoltGreen,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "LINIMASA & PERISTIWA LAGA",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            events.forEachIndexed { index, event ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Minute
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(
                                if (event.isHomeTeam) VoltGreen.copy(alpha = 0.2f) else Color(0xFF2979FF).copy(alpha = 0.2f)
                            )
                    ) {
                        Text(
                            text = "${event.minute}'",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (event.isHomeTeam) VoltGreen else Color(0xFF2979FF)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Event icon
                    val iconText = when (event.type) {
                        "GOAL" -> "⚽"
                        "CARD" -> "🟨"
                        "SUB" -> "🔁"
                        else -> "⚡"
                    }
                    Text(text = iconText, fontSize = 16.sp)

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = event.player,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        event.detail?.let { detail ->
                            Text(
                                text = detail,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                if (index < events.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                }
            }
        }
    }
}
