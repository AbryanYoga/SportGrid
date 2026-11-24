package com.abryan.sportgrid.ui.screens.playerdetail

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.abryan.sportgrid.SportGridApp
import com.abryan.sportgrid.domain.model.CareerItem
import com.abryan.sportgrid.domain.model.PersonProfile
import com.abryan.sportgrid.ui.components.ErrorState
import com.abryan.sportgrid.ui.theme.VoltGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerDetailScreen(
    playerId: String,
    onBack: () -> Unit,
    onNavigateToClub: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val app = LocalContext.current.applicationContext as SportGridApp
    var player by remember { mutableStateOf<PersonProfile?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    LaunchedEffect(playerId) {
        isLoading = true
        player = app.container.personRepository.getPersonById(playerId)
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "BIODATA & KARIER PEMAIN",
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
            player == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    ErrorState(
                        message = "Data pemain tidak ditemukan.",
                        onRetry = onBack
                    )
                }
            }
            else -> {
                val p = player!!
                if (isLandscape) {
                    // DUAL COLUMN LANDSCAPE LAYOUT
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Left Column: Header Card & Biodata Grid
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 24.dp)
                        ) {
                            item {
                                PlayerHeaderCard(player = p, onNavigateToClub = onNavigateToClub)
                                Spacer(modifier = Modifier.height(12.dp))
                                PlayerBioMetricsCard(player = p)
                            }
                        }

                        // Right Column: Career History, Achievements & Summary
                        LazyColumn(
                            modifier = Modifier
                                .weight(1.2f)
                                .fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 24.dp)
                        ) {
                            item {
                                PlayerAchievementsCard(achievements = p.achievements)
                                Spacer(modifier = Modifier.height(14.dp))
                                PlayerCareerTimelineCard(careerHistory = p.careerHistory)
                                Spacer(modifier = Modifier.height(14.dp))
                                PlayerBioSummaryCard(player = p)
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
                            PlayerHeaderCard(player = p, onNavigateToClub = onNavigateToClub)
                        }

                        item {
                            PlayerBioMetricsCard(player = p)
                        }

                        if (p.achievements.isNotEmpty()) {
                            item {
                                PlayerAchievementsCard(achievements = p.achievements)
                            }
                        }

                        if (p.careerHistory.isNotEmpty()) {
                            item {
                                PlayerCareerTimelineCard(careerHistory = p.careerHistory)
                            }
                        }

                        item {
                            PlayerBioSummaryCard(player = p)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayerHeaderCard(
    player: PersonProfile,
    onNavigateToClub: (Int) -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            VoltGreen.copy(alpha = 0.5f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Player Photo
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(3.dp, VoltGreen, CircleShape)
            ) {
                if (!player.photo.isNullOrEmpty()) {
                    AsyncImage(
                        model = player.photo,
                        contentDescription = player.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(56.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Player Name
            Text(
                text = player.name,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            // Sport & Role badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Text(
                    text = "ATLET PROFESIONAL",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = VoltGreen,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(text = "•", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    text = stringResource(player.sportType.displayNameRes),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Current Team Pill (Clickable)
            if (!player.teamName.isNullOrEmpty()) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.clickable {
                        val clubId = when {
                            player.teamName.contains("Real Madrid", ignoreCase = true) -> 541
                            player.teamName.contains("Barcelona", ignoreCase = true) -> 529
                            player.teamName.contains("Manchester City", ignoreCase = true) -> 50
                            player.teamName.contains("Arsenal", ignoreCase = true) -> 42
                            player.teamName.contains("Liverpool", ignoreCase = true) -> 40
                            player.teamName.contains("Warriors", ignoreCase = true) -> 1002
                            player.teamName.contains("Lakers", ignoreCase = true) -> 1001
                            else -> 541
                        }
                        onNavigateToClub(clubId)
                    }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        if (!player.teamLogo.isNullOrEmpty()) {
                            AsyncImage(
                                model = player.teamLogo,
                                contentDescription = player.teamName,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(
                            text = player.teamName,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        player.number?.let { num ->
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "#$num",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = VoltGreen
                                )
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayerBioMetricsCard(player: PersonProfile) {
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
                    Icons.Default.Badge,
                    contentDescription = null,
                    tint = VoltGreen,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "BIODATA FISIK & INFORMASI",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                player.nationality?.let {
                    MetricRow(label = "Kewarganegaraan", value = it)
                }
                player.age?.let {
                    MetricRow(label = "Usia", value = "$it Tahun")
                }
                player.position?.let {
                    MetricRow(label = "Posisi Utama", value = it)
                }
                player.height?.let {
                    MetricRow(label = "Tinggi Badan", value = it)
                }
                player.weight?.let {
                    MetricRow(label = "Berat Badan", value = it)
                }
                player.dominantFootHand?.let {
                    MetricRow(label = "Dominansi Tangan/Kaki", value = it)
                }
                player.marketValue?.let {
                    MetricRow(label = "Estimasi Nilai Pasar", value = it, isHighlight = true)
                }
            }
        }
    }
}

@Composable
private fun MetricRow(
    label: String,
    value: String,
    isHighlight: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = if (isHighlight) VoltGreen else MaterialTheme.colorScheme.onSurface,
                fontSize = 13.sp
            )
        )
    }
}

@Composable
private fun PlayerAchievementsCard(achievements: List<String>) {
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
                    Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = VoltGreen,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "GELAR JUARA & PENGHARGAAN",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            achievements.forEach { achievement ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(text = "🏆", fontSize = 14.sp, modifier = Modifier.padding(end = 8.dp))
                    Text(
                        text = achievement,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun PlayerCareerTimelineCard(careerHistory: List<CareerItem>) {
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
                    Icons.Default.Timeline,
                    contentDescription = null,
                    tint = VoltGreen,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "RIWAYAT KARIER KLUB",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            careerHistory.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(VoltGreen.copy(alpha = 0.15f))
                        ) {
                            Text(
                                text = item.teamName.take(1),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = VoltGreen
                                )
                            )
                        }

                        Column {
                            Text(
                                text = item.teamName,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            item.roleTitle?.let { role ->
                                Text(
                                    text = role,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }

                    val yearLabel = if (!item.startYear.isNullOrEmpty()) {
                        "${item.startYear} - ${item.endYear ?: "Sekarang"}"
                    } else item.endYear ?: ""

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = yearLabel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = VoltGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                if (index < careerHistory.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    )
                }
            }
        }
    }
}

@Composable
private fun PlayerBioSummaryCard(player: PersonProfile) {
    if (!player.careerSummary.isNullOrEmpty()) {
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
                        Icons.Default.SportsScore,
                        contentDescription = null,
                        tint = VoltGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "GAYA BERMAIN & BIOGRAFI",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = player.careerSummary,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp,
                        fontSize = 13.sp
                    )
                )
            }
        }
    }
}
