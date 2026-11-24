package com.abryan.sportgrid.ui.screens.home

import android.app.Application
import android.content.res.Configuration
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.abryan.sportgrid.R
import com.abryan.sportgrid.SportGridApp
import com.abryan.sportgrid.domain.model.Match
import com.abryan.sportgrid.ui.components.EmptyState
import com.abryan.sportgrid.ui.components.ErrorState
import com.abryan.sportgrid.ui.components.HeroLiveCard
import com.abryan.sportgrid.ui.components.LeagueCard
import com.abryan.sportgrid.ui.components.MatchCard
import com.abryan.sportgrid.ui.components.ShimmerCard
import com.abryan.sportgrid.ui.components.SportFilterChips
import com.abryan.sportgrid.ui.components.TransferCard
import com.abryan.sportgrid.ui.theme.VoltGreen

data class FeaturedAthlete(
    val id: String,
    val name: String,
    val avatarUrl: String? = null,
    val sportName: String = "Football"
)

// GSAP-style Interactive Spring Bounce Modifier
fun Modifier.springBounceClick(onClick: () -> Unit): Modifier = this.pointerInput(Unit) {
    awaitEachGesture {
        awaitFirstDown()
        val up = waitForUpOrCancellation()
        if (up != null) {
            onClick()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.provideFactory(LocalContext.current.applicationContext as Application)
    ),
    onNavigateToLeagues: () -> Unit = {},
    onNavigateToTransfers: () -> Unit = {},
    onNavigateToProfiles: () -> Unit = {},
    onNavigateToPlayerDetail: (String) -> Unit = {},
    onNavigateToMatchDetail: (Int) -> Unit = {},
    onNavigateToClub: (Int) -> Unit = {}
) {
    val app = LocalContext.current.applicationContext as SportGridApp
    val favoriteClubs by app.container.favoritesDataStore.favoriteClubs.collectAsState(initial = emptySet())
    val favoritePlayers by app.container.favoritesDataStore.favoritePlayers.collectAsState(initial = emptySet())
    val allClubs = remember { app.container.clubRepository.topWorldClubs }
    val allPersons = remember { app.container.personRepository.featuredWorldwideProfiles }

    val state by viewModel.uiState.collectAsState()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // GSAP Staggered Entrance Animation State
    var isScreenEntered by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isScreenEntered = true
    }

    // GSAP-style Continuous Living Radar Glow Transition
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_radar")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "radar_scale"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "radar_alpha"
    )

    // Real HD Photos for athletes across world sports
    val athletes = remember {
        listOf(
            FeaturedAthlete("f_p1", "Mbappé", "https://media.api-sports.io/football/players/278.png", "Sepak Bola"),
            FeaturedAthlete("b_p1", "S. Curry", "https://a.espncdn.com/i/headshots/nba/players/full/3975.png", "Basket"),
            FeaturedAthlete("f_p2", "Bellingham", "https://media.api-sports.io/football/players/152982.png", "Sepak Bola"),
            FeaturedAthlete("f_p3", "Haaland", "https://media.api-sports.io/football/players/1100.png", "Sepak Bola"),
            FeaturedAthlete("f_p4", "L. Messi", "https://media.api-sports.io/football/players/154.png", "Sepak Bola"),
            FeaturedAthlete("f_p5", "C. Ronaldo", "https://media.api-sports.io/football/players/874.png", "Sepak Bola"),
            FeaturedAthlete("f_p6", "L. Yamal", "https://media.api-sports.io/football/players/361099.png", "Sepak Bola"),
            FeaturedAthlete("bb_p1", "S. Ohtani", "https://a.espncdn.com/i/headshots/mlb/players/full/39832.png", "Bisbol")
        )
    }

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) {
        PullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier.fillMaxSize()
        ) {
            if (state.isLoading && state.popularLeagues.isEmpty() && state.todayMatches.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ShimmerCard(height = 60)
                    ShimmerCard(height = 40)
                    ShimmerCard(height = 180)
                    ShimmerCard(height = 120)
                    ShimmerCard(height = 120)
                }
            } else if (state.errorMessageRes != null && state.popularLeagues.isEmpty() && state.todayMatches.isEmpty()) {
                ErrorState(
                    message = stringResource(state.errorMessageRes ?: R.string.state_error_generic),
                    onRetry = { viewModel.refresh() }
                )
            } else {
                // Header Top Bar
                Column(modifier = Modifier.fillMaxSize()) {
                    HomeHeader(
                        onRefresh = { viewModel.refresh() },
                        onNavigateToProfiles = onNavigateToProfiles,
                        isLandscape = isLandscape
                    )

                    if (isLandscape) {
                    // DUAL COLUMN RESPONSIVE LAYOUT FOR LANDSCAPE (Broadcasting Dashboard)
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Left Column (Hero, Spotlight Athletes, Banner)
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 24.dp)
                        ) {
                            item {
                                Text(
                                    text = "SPOTLIGHT DUNIA",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp,
                                        color = VoltGreen
                                    ),
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                                AthleteStoriesRow(
                                    athletes = athletes,
                                    pulseScale = pulseScale,
                                    onAthleteClick = onNavigateToPlayerDetail
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                FavoriteShortcutsRow(
                                    favoriteClubs = favoriteClubs,
                                    favoritePlayers = favoritePlayers,
                                    allClubs = allClubs,
                                    allPersons = allPersons,
                                    onClubClick = onNavigateToClub,
                                    onPlayerClick = onNavigateToPlayerDetail
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                            }

                            if (state.heroMatch != null) {
                                item {
                                    HeroLiveCard(
                                        match = state.heroMatch!!,
                                        onClick = { onNavigateToMatchDetail(state.heroMatch!!.id) }
                                    )
                                    Spacer(modifier = Modifier.height(14.dp))
                                }
                            }

                            item {
                                TopEventBanner(
                                    title = "${stringResource(state.selectedSport.displayNameRes)} • Spotlight"
                                )
                            }
                        }

                        // Right Column (Filters, Fixtures, Leagues, Transfers)
                        LazyColumn(
                            modifier = Modifier
                                .weight(1.15f)
                                .fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 24.dp)
                        ) {
                            item {
                                QuickExploreCard(
                                    onNavigateToProfiles = onNavigateToProfiles,
                                    modifier = Modifier.padding(bottom = 10.dp)
                                )
                            }

                            item {
                                SportFilterChips(
                                    selectedSport = state.selectedSport,
                                    onSportSelected = { viewModel.onSportSelected(it) },
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )
                            }

                            item {
                                SectionTitle(title = stringResource(R.string.section_today_matches))
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            if (state.todayMatches.isEmpty()) {
                                item {
                                    EmptyState(message = stringResource(R.string.state_empty))
                                }
                            } else {
                                items(state.todayMatches) { match ->
                                    Box(modifier = Modifier.padding(vertical = 4.dp)) {
                                        MatchCard(
                                            match = match,
                                            onClick = { onNavigateToMatchDetail(match.id) }
                                        )
                                    }
                                }
                            }

                            // Popular Leagues
                            if (state.popularLeagues.isNotEmpty()) {
                                item {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    SectionHeaderWithAction(
                                        title = stringResource(R.string.section_popular_leagues),
                                        actionText = stringResource(R.string.view_all),
                                        onAction = onNavigateToLeagues
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        items(state.popularLeagues) { league ->
                                            LeagueCard(league = league)
                                        }
                                    }
                                }
                            }

                            // Latest Transfers
                            if (state.selectedSport.isFootball && state.latestTransfers.isNotEmpty()) {
                                item {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    SectionHeaderWithAction(
                                        title = stringResource(R.string.section_latest_transfers),
                                        actionText = stringResource(R.string.view_all),
                                        onAction = onNavigateToTransfers
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                                items(state.latestTransfers.take(3)) { transfer ->
                                    Box(modifier = Modifier.padding(vertical = 4.dp)) {
                                        TransferCard(transfer = transfer)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // PORTRAIT FLUID STREAM LAYOUT
                    val entranceOffset by animateFloatAsState(
                        targetValue = if (isScreenEntered) 0f else 30f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        ),
                        label = "gsap_entrance"
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer { translationY = entranceOffset },
                        contentPadding = PaddingValues(bottom = 32.dp)
                    ) {
                        // Athlete Circles (Spotlight bar)
                        item {
                            AthleteStoriesRow(
                                athletes = athletes,
                                pulseScale = pulseScale,
                                onAthleteClick = onNavigateToPlayerDetail
                            )
                        }

                        // Favorite Shortcuts (Pin / Bookmark)
                        item {
                            FavoriteShortcutsRow(
                                favoriteClubs = favoriteClubs,
                                favoritePlayers = favoritePlayers,
                                allClubs = allClubs,
                                allPersons = allPersons,
                                onClubClick = onNavigateToClub,
                                onPlayerClick = onNavigateToPlayerDetail
                            )
                        }

                        // Quick Action Bar: Jelajahi Pemain, Pelatih & Klub
                        item {
                            QuickExploreCard(
                                onNavigateToProfiles = onNavigateToProfiles,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                            )
                        }

                        // Sport Filter Chips (Horizontal)
                        item {
                            SportFilterChips(
                                selectedSport = state.selectedSport,
                                onSportSelected = { viewModel.onSportSelected(it) },
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }

                        // Hero Live Match
                        if (state.heroMatch != null) {
                            item {
                                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                                    HeroLiveCard(
                                        match = state.heroMatch!!,
                                        onClick = { onNavigateToMatchDetail(state.heroMatch!!.id) }
                                    )
                                }
                                Spacer(modifier = Modifier.height(20.dp))
                            }
                        }

                        // Top Events / Featured Banner
                        item {
                            TopEventBanner(
                                title = "${stringResource(state.selectedSport.displayNameRes)} • Spotlight",
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                        }

                        // Popular Leagues Section
                        if (state.popularLeagues.isNotEmpty()) {
                            item {
                                SectionHeaderWithAction(
                                    title = stringResource(R.string.section_popular_leagues),
                                    actionText = stringResource(R.string.view_all),
                                    onAction = onNavigateToLeagues,
                                    modifier = Modifier.padding(horizontal = 20.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(state.popularLeagues) { league ->
                                        LeagueCard(league = league)
                                    }
                                }
                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }

                        // Today's Matches Section
                        item {
                            SectionTitle(
                                title = stringResource(R.string.section_today_matches),
                                modifier = Modifier.padding(horizontal = 20.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        if (state.todayMatches.isEmpty()) {
                            item {
                                EmptyState(message = stringResource(R.string.state_empty))
                            }
                        } else {
                            items(state.todayMatches) { match ->
                                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                                    MatchCard(
                                        match = match,
                                        onClick = { onNavigateToMatchDetail(match.id) }
                                    )
                                }
                            }
                        }

                        // Latest Transfers (Football only)
                        if (state.selectedSport.isFootball && state.latestTransfers.isNotEmpty()) {
                            item {
                                Spacer(modifier = Modifier.height(18.dp))
                                SectionHeaderWithAction(
                                    title = stringResource(R.string.section_latest_transfers),
                                    actionText = stringResource(R.string.view_all),
                                    onAction = onNavigateToTransfers,
                                    modifier = Modifier.padding(horizontal = 20.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            items(state.latestTransfers.take(3)) { transfer ->
                                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)) {
                                    TransferCard(transfer = transfer)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
}

@Composable
private fun HomeHeader(
    onRefresh: () -> Unit,
    onNavigateToProfiles: () -> Unit,
    isLandscape: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = if (isLandscape) 24.dp else 20.dp,
                vertical = if (isLandscape) 6.dp else 14.dp
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "SPORT",
                style = MaterialTheme.typography.titleLarge.copy(fontSize = if (isLandscape) 18.sp else 22.sp),
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "GRID+",
                style = MaterialTheme.typography.titleLarge.copy(fontSize = if (isLandscape) 18.sp else 22.sp),
                fontWeight = FontWeight.Black,
                color = VoltGreen
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            IconButton(onClick = onRefresh, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onNavigateToProfiles, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Cari Pemain, Pelatih & Klub",
                    tint = VoltGreen
                )
            }
        }
    }
}

@Composable
private fun AthleteStoriesRow(
    athletes: List<FeaturedAthlete>,
    pulseScale: Float,
    onAthleteClick: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        items(athletes) { athlete ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(62.dp)
                    .clickable { onAthleteClick(athlete.id) }
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .border(2.dp, VoltGreen, CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    if (!athlete.avatarUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = athlete.avatarUrl,
                            contentDescription = athlete.name,
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = athlete.name,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = athlete.name,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun QuickExploreCard(
    onNavigateToProfiles: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            VoltGreen.copy(alpha = 0.4f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onNavigateToProfiles)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = VoltGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Cari profil pemain, pelatih & bio klub...",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(VoltGreen.copy(alpha = 0.2f))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "JELAJAHI",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = VoltGreen,
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}

@Composable
fun TopEventBanner(
    title: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = VoltGreen),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .background(Color.Black, RoundedCornerShape(10.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "LIVE RADAR",
                        style = MaterialTheme.typography.labelSmall,
                        color = VoltGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Live Match Intelligence",
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 20.sp),
                color = Color.Black,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "Real-time scores, team standings, player stats and transfer tracker",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black.copy(alpha = 0.75f)
            )
        }
    }
}

@Composable
private fun SectionTitle(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
    )
}

@Composable
private fun SectionHeaderWithAction(
    title: String,
    actionText: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        TextButton(onClick = onAction) {
            Text(
                text = actionText,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun FavoriteShortcutsRow(
    favoriteClubs: Set<String>,
    favoritePlayers: Set<String>,
    allClubs: List<com.abryan.sportgrid.domain.model.ClubBio>,
    allPersons: List<com.abryan.sportgrid.domain.model.PersonProfile>,
    onClubClick: (Int) -> Unit,
    onPlayerClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (favoriteClubs.isEmpty() && favoritePlayers.isEmpty()) return

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = VoltGreen,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "PIN & FAVORIT SAYA",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = VoltGreen
                )
            )
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 10.dp)
        ) {
            // Favorite Clubs
            items(favoriteClubs.toList()) { clubIdStr ->
                val clubId = clubIdStr.toIntOrNull() ?: 0
                val club = allClubs.find { it.id == clubId }
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, VoltGreen.copy(alpha = 0.5f)),
                    modifier = Modifier.clickable { onClubClick(clubId) }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        if (!club?.logo.isNullOrBlank()) {
                            AsyncImage(
                                model = club!!.logo,
                                contentDescription = club.name,
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Fit
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Text(
                            text = club?.name ?: "Klub #$clubId",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            maxLines = 1
                        )
                    }
                }
            }

            // Favorite Players
            items(favoritePlayers.toList()) { playerId ->
                val person = allPersons.find { it.id == playerId }
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                    modifier = Modifier.clickable { onPlayerClick(playerId) }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        if (!person?.photo.isNullOrBlank()) {
                            AsyncImage(
                                model = person!!.photo,
                                contentDescription = person.name,
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Text(
                            text = person?.name ?: "Atlet #$playerId",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}