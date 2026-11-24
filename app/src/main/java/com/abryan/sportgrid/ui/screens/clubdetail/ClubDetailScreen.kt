package com.abryan.sportgrid.ui.screens.clubdetail

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Stadium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.abryan.sportgrid.domain.model.ClubBio
import com.abryan.sportgrid.ui.components.ErrorState
import com.abryan.sportgrid.ui.theme.VoltGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubDetailScreen(
    clubId: Int,
    onBack: () -> Unit,
    onNavigateToCoach: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val app = LocalContext.current.applicationContext as SportGridApp
    val context = LocalContext.current
    var club by remember { mutableStateOf<ClubBio?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    LaunchedEffect(clubId) {
        isLoading = true
        club = app.container.clubRepository.getClubById(clubId)
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "BIO DATA RESMI KLUB",
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
            club == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    ErrorState(
                        message = "Data klub tidak ditemukan.",
                        onRetry = onBack
                    )
                }
            }
            else -> {
                val c = club!!
                if (isLandscape) {
                    // Landscape Dual Column Layout
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Left Column: Crest, President & Manager Leadership
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 24.dp)
                        ) {
                            item {
                                ClubHeaderCard(club = c)
                                Spacer(modifier = Modifier.height(14.dp))
                                ClubLeadershipCard(club = c, onNavigateToCoach = onNavigateToCoach)
                            }
                        }

                        // Right Column: Stadium, Trophies & Biography
                        LazyColumn(
                            modifier = Modifier
                                .weight(1.2f)
                                .fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 24.dp)
                        ) {
                            item {
                                StadiumCard(club = c)
                                Spacer(modifier = Modifier.height(14.dp))
                                TrophiesCard(club = c)
                                Spacer(modifier = Modifier.height(14.dp))
                                ClubHistoryCard(club = c)

                                if (!c.website.isNullOrEmpty()) {
                                    Spacer(modifier = Modifier.height(14.dp))
                                    OutlinedButton(
                                        onClick = {
                                            try {
                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(c.website))
                                                context.startActivity(intent)
                                            } catch (_: Exception) {}
                                        },
                                        shape = RoundedCornerShape(16.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Kunjungi Situs Resmi Klub", fontWeight = FontWeight.SemiBold)
                                    }
                                }
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
                            ClubHeaderCard(club = c)
                        }

                        item {
                            ClubLeadershipCard(club = c, onNavigateToCoach = onNavigateToCoach)
                        }

                        item {
                            StadiumCard(club = c)
                        }

                        item {
                            TrophiesCard(club = c)
                        }

                        item {
                            ClubHistoryCard(club = c)
                        }

                        if (!c.website.isNullOrEmpty()) {
                            item {
                                OutlinedButton(
                                    onClick = {
                                        try {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(c.website))
                                            context.startActivity(intent)
                                        } catch (_: Exception) {}
                                    },
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Kunjungi Situs Resmi Klub", fontWeight = FontWeight.SemiBold)
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
private fun ClubHeaderCard(club: ClubBio) {
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
            // Club Crest
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(2.dp, VoltGreen, CircleShape)
            ) {
                if (!club.logo.isNullOrEmpty()) {
                    AsyncImage(
                        model = club.logo,
                        contentDescription = club.name,
                        modifier = Modifier
                            .size(72.dp)
                            .padding(4.dp)
                    )
                } else {
                    Icon(
                        Icons.Default.Business,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = club.name,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            // Tags: Country, Founded Year
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 6.dp)
            ) {
                club.country?.let { country ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = country,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }

                club.foundedYear?.let { year ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(VoltGreen.copy(alpha = 0.2f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Est. $year",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = VoltGreen
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ClubLeadershipCard(
    club: ClubBio,
    onNavigateToCoach: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.People,
                    contentDescription = null,
                    tint = VoltGreen,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "DEWAN PIMPINAN & PELATIH",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // PRESIDENT / OWNER (Prominently featured as requested by user!)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = androidx.compose.foundation.BorderStroke(1.dp, VoltGreen.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(VoltGreen.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            Icons.Default.AccountBalance,
                            contentDescription = null,
                            tint = VoltGreen,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "PRESIDEN / PEMILIK KLUB",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = VoltGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                        Text(
                            text = club.president ?: "Dewan Direksi Resmi",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // HEAD COACH / MANAGER (Clickable to dedicated coach page!)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        // Navigasi ke halaman pelatih jika cocok
                        val coachId = when {
                            club.name.contains("Real Madrid", ignoreCase = true) -> "f_c1"
                            club.name.contains("Manchester City", ignoreCase = true) -> "f_c2"
                            club.name.contains("Arsenal", ignoreCase = true) -> "f_c3"
                            club.name.contains("Barcelona", ignoreCase = true) -> "f_c4"
                            club.name.contains("Warriors", ignoreCase = true) -> "b_c1"
                            else -> "f_c1"
                        }
                        onNavigateToCoach(coachId)
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2979FF).copy(alpha = 0.2f))
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = Color(0xFF2979FF),
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "PELATIH KEPALA (MANAGER)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF2979FF),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            )
                            Text(
                                text = club.manager ?: "Pelatih Kepala",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Buka Profil Pelatih",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun StadiumCard(club: ClubBio) {
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
                    Icons.Default.Stadium,
                    contentDescription = null,
                    tint = VoltGreen,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "STADION & MARKAS UTAMA",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Stadium Photo
            if (!club.stadiumImage.isNullOrEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    AsyncImage(
                        model = club.stadiumImage,
                        contentDescription = club.stadiumName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Text(
                text = club.stadiumName ?: "Stadion Utama",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                club.stadiumCity?.let { city ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = city,
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }

                club.stadiumCapacity?.let { cap ->
                    Text(
                        text = "Kapasitas: ${"%,d".format(cap)} Kursi",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = VoltGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun TrophiesCard(club: ClubBio) {
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
                    text = "LEMARI TROFI & KEJUARAAN",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            val summary = club.trophiesSummary ?: "Juara Kompetisi Domestik & Internasional"
            Text(
                text = summary,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 20.sp
                )
            )
        }
    }
}

@Composable
private fun ClubHistoryCard(club: ClubBio) {
    if (!club.description.isNullOrEmpty()) {
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
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "SEJARAH & PROFIL SINGKAT",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = club.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                )
            }
        }
    }
}
