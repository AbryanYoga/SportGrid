package com.abryan.sportgrid.ui.screens.profiles

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.abryan.sportgrid.SportGridApp
import com.abryan.sportgrid.domain.model.ClubBio
import com.abryan.sportgrid.domain.model.PersonProfile
import com.abryan.sportgrid.domain.model.PersonRole
import com.abryan.sportgrid.ui.components.EmptyState
import com.abryan.sportgrid.ui.components.ErrorState
import com.abryan.sportgrid.ui.components.SportFilterChips
import com.abryan.sportgrid.ui.theme.VoltGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilesScreen(
    onBack: () -> Unit,
    onNavigateToCoach: (String) -> Unit = {},
    onNavigateToClub: (Int) -> Unit = {},
    onNavigateToPlayer: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val app = LocalContext.current.applicationContext as SportGridApp
    val viewModel: ProfilesViewModel = viewModel(
        factory = ProfilesViewModel.provideFactory(
            app.container.personRepository,
            app.container.clubRepository
        )
    )
    val state by viewModel.uiState.collectAsState()

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "PROFIL BINTANG, PELATIH & KLUB",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Input
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = viewModel::onSearchQueryChanged,
                placeholder = { Text("Cari nama pemain, pelatih, atau klub...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (state.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Hapus")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = if (isLandscape) 2.dp else 6.dp)
            )

            // Sport Filter Chips
            SportFilterChips(
                selectedSport = state.selectedSport,
                onSportSelected = viewModel::onSportSelected,
                modifier = Modifier.padding(vertical = if (isLandscape) 2.dp else 4.dp)
            )

            // Role Segmented Chips: Semua, Pemain, Pelatih, Klub
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = if (isLandscape) 2.dp else 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    PersonRole.ALL to "Semua",
                    PersonRole.PLAYER to "Pemain",
                    PersonRole.COACH to "Pelatih",
                    PersonRole.CLUB to "Klub"
                ).forEach { (role, label) ->
                    val isSelected = state.selectedRole == role
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.onRoleSelected(role) },
                        label = { Text(label, style = MaterialTheme.typography.labelMedium) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Main Content Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when {
                    state.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    }
                    state.errorMessage != null -> ErrorState(
                        message = state.errorMessage!!,
                        onRetry = viewModel::refresh
                    )
                    state.profiles.isEmpty() && state.clubs.isEmpty() -> EmptyState(
                        message = if (state.searchQuery.isNotEmpty())
                            "Tidak ditemukan hasil untuk '${state.searchQuery}'"
                        else
                            "Belum ada data untuk filter ini."
                    )
                    else -> {
                        val minCardSize = if (isLandscape) 140.dp else 160.dp
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = minCardSize),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            // Section: Clubs (if any)
                            if (state.clubs.isNotEmpty()) {
                                if (state.selectedRole == PersonRole.ALL && state.profiles.isNotEmpty()) {
                                    item(span = { GridItemSpan(maxLineSpan) }) {
                                        Text(
                                            text = "KLUB & ORGANISASI",
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = 1.sp,
                                                color = VoltGreen
                                            ),
                                            modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                                        )
                                    }
                                }
                                items(state.clubs, key = { "club_${it.id}" }) { club ->
                                    ClubGridCard(
                                        club = club,
                                        onClick = { onNavigateToClub(club.id) }
                                    )
                                }
                            }

                            // Section: Profiles (Players & Coaches)
                            if (state.profiles.isNotEmpty()) {
                                if (state.selectedRole == PersonRole.ALL && state.clubs.isNotEmpty()) {
                                    item(span = { GridItemSpan(maxLineSpan) }) {
                                        Text(
                                            text = "PEMAIN & PELATIH",
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = 1.sp,
                                                color = MaterialTheme.colorScheme.primary
                                            ),
                                            modifier = Modifier.padding(top = 10.dp, bottom = 2.dp)
                                        )
                                    }
                                }
                                items(state.profiles, key = { it.id }) { profile ->
                                    ProfileGridCard(
                                        profile = profile,
                                        onClick = {
                                            if (profile.role == PersonRole.COACH) {
                                                // Pelatih navigasi ke PAGE BARU
                                                onNavigateToCoach(profile.id)
                                            } else {
                                                // Pemain navigasi ke PAGE BARU
                                                onNavigateToPlayer(profile.id)
                                            }
                                        }
                                    )
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
fun ProfileGridCard(
    profile: PersonProfile,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Role Badge
            val isCoach = profile.role == PersonRole.COACH
            val badgeColor = if (isCoach) Color(0xFF2979FF) else MaterialTheme.colorScheme.primary
            val badgeTextColor = if (isCoach) Color.White else MaterialTheme.colorScheme.onPrimary

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(badgeColor.copy(alpha = 0.9f))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
                    .align(Alignment.End)
            ) {
                Text(
                    text = if (isCoach) "PELATIH" else "PEMAIN",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeTextColor
                    )
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Photo Avatar
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .border(2.dp, badgeColor, CircleShape)
            ) {
                if (!profile.photo.isNullOrEmpty()) {
                    AsyncImage(
                        model = profile.photo,
                        contentDescription = profile.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Name
            Text(
                text = profile.name,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            // Team or Nationality
            val subtitle = profile.teamName ?: profile.nationality ?: stringResource(profile.sportType.displayNameRes)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            // Position & Number tag
            if (!profile.position.isNullOrEmpty() || profile.number != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    profile.number?.let { num ->
                        Text(
                            text = "#$num",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    profile.position?.let { pos ->
                        Text(
                            text = pos.take(14),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp
                            ),
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ClubGridCard(
    club: ClubBio,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            VoltGreen.copy(alpha = 0.5f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Club Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(VoltGreen.copy(alpha = 0.2f))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
                    .align(Alignment.End)
            ) {
                Text(
                    text = "KLUB",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = VoltGreen
                    )
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Club Crest
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .border(2.dp, VoltGreen, CircleShape)
            ) {
                if (!club.logo.isNullOrEmpty()) {
                    AsyncImage(
                        model = club.logo,
                        contentDescription = club.name,
                        modifier = Modifier
                            .size(50.dp)
                            .padding(4.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Business,
                        contentDescription = null,
                        tint = VoltGreen,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Club Name
            Text(
                text = club.name,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            // President Highlight
            club.president?.let { pres ->
                Text(
                    text = "Presiden: $pres",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = VoltGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }

            // Stadium
            club.stadiumName?.let { stadium ->
                Text(
                    text = stadium,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
