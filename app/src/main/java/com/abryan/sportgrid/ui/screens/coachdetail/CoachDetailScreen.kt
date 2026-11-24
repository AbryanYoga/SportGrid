package com.abryan.sportgrid.ui.screens.coachdetail

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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Color
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
fun CoachDetailScreen(
    coachId: String,
    onBack: () -> Unit,
    onNavigateToClub: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val app = LocalContext.current.applicationContext as SportGridApp
    var coach by remember { mutableStateOf<PersonProfile?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    LaunchedEffect(coachId) {
        isLoading = true
        coach = app.container.personRepository.getCoachById(coachId)
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "PROFIL PELATIH KEPALA",
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
            coach == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    ErrorState(
                        message = "Data profil pelatih tidak ditemukan.",
                        onRetry = onBack
                    )
                }
            }
            else -> {
                val c = coach!!
                if (isLandscape) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Left Column (Landscape): Profile & Philosophy
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 24.dp)
                        ) {
                            item {
                                CoachHeaderCard(coach = c, onNavigateToClub = onNavigateToClub)
                                Spacer(modifier = Modifier.height(12.dp))
                                CoachPhilosophyCard(coach = c)
                            }
                        }

                        // Right Column (Landscape): Career Timeline & Achievements
                        LazyColumn(
                            modifier = Modifier
                                .weight(1.2f)
                                .fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 24.dp)
                        ) {
                            item {
                                CoachAchievementsCard(achievements = c.achievements)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "RIWAYAT MANAJERIAL & KLUB",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            items(c.careerHistory) { item ->
                                CareerHistoryItemRow(item = item)
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                } else {
                    // Portrait Layout: Single vertical stream
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            CoachHeaderCard(coach = c, onNavigateToClub = onNavigateToClub)
                        }

                        item {
                            CoachPhilosophyCard(coach = c)
                        }

                        if (c.achievements.isNotEmpty()) {
                            item {
                                CoachAchievementsCard(achievements = c.achievements)
                            }
                        }

                        if (c.careerHistory.isNotEmpty()) {
                            item {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Timeline,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "RIWAYAT MANAJERIAL & KLUB",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.5.sp
                                        )
                                    )
                                }
                            }

                            items(c.careerHistory) { item ->
                                CareerHistoryItemRow(item = item)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CoachHeaderCard(
    coach: PersonProfile,
    onNavigateToClub: (Int) -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Coach Portrait
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(3.dp, VoltGreen, CircleShape)
            ) {
                if (!coach.photo.isNullOrEmpty()) {
                    AsyncImage(
                        model = coach.photo,
                        contentDescription = coach.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(54.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Name
            Text(
                text = coach.name,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            // Role and Sport
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Text(
                    text = "PELATIH KEPALA",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "•",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(coach.sportType.displayNameRes),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Current Team Pill (Clickable)
            if (!coach.teamName.isNullOrEmpty()) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.clickable {
                        // Jika ada club terkait, navigasi
                    }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        if (!coach.teamLogo.isNullOrEmpty()) {
                            AsyncImage(
                                model = coach.teamLogo,
                                contentDescription = coach.teamName,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(
                            text = coach.teamName,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
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

            Spacer(modifier = Modifier.height(14.dp))

            // Info Metrics (Usia, Kewarganegaraan)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                coach.age?.let { age ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$age Tahun",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Usia",
                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }
                coach.nationality?.let { nat ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = nat,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Kewarganegaraan",
                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CoachPhilosophyCard(coach: PersonProfile) {
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = VoltGreen,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "FILOSOFI & TAKTIK",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            val style = coach.coachingStyle ?: "Mengutamakan disiplin taktik, adaptabilitas formasi, dan penguasaan bola terorganisir."
            Text(
                text = style,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            )

            if (!coach.careerSummary.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = coach.careerSummary,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun CoachAchievementsCard(achievements: List<String>) {
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
                    text = "GELAR JUARA & PRESTASI",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            achievements.forEach { achievement ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "🏆",
                        fontSize = 14.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )
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
private fun CareerHistoryItemRow(item: CareerItem) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    if (!item.teamLogo.isNullOrEmpty()) {
                        AsyncImage(
                            model = item.teamLogo,
                            contentDescription = item.teamName,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = item.teamName.take(1),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Column {
                    Text(
                        text = item.teamName,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
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

            // Years Badge
            val yearsText = if (!item.startYear.isNullOrEmpty()) {
                "${item.startYear} - ${item.endYear ?: "Sekarang"}"
            } else {
                item.endYear ?: ""
            }

            if (yearsText.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = yearsText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
}
