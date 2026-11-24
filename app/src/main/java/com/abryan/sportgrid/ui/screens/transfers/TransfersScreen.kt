package com.abryan.sportgrid.ui.screens.transfers

import android.app.Application
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.abryan.sportgrid.R
import com.abryan.sportgrid.domain.model.SportType
import com.abryan.sportgrid.ui.components.EmptyState
import com.abryan.sportgrid.ui.components.ErrorState
import com.abryan.sportgrid.ui.components.ShimmerCard
import com.abryan.sportgrid.ui.components.SportFilterChips
import com.abryan.sportgrid.ui.components.TransferCard
import com.abryan.sportgrid.ui.theme.VoltGreen

@Composable
fun TransfersScreen(
    modifier: Modifier = Modifier,
    viewModel: TransferViewModel = viewModel(
        factory = TransferViewModel.provideFactory(LocalContext.current.applicationContext as Application)
    )
) {
    val state by viewModel.uiState.collectAsState()

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.transfer_market).uppercase(),
                        style = MaterialTheme.typography.headlineMedium.copy(fontSize = 22.sp),
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Sport Filter
            item {
                SportFilterChips(
                    selectedSport = state.selectedSport,
                    onSportSelected = { viewModel.onSportChanged(it) },
                    modifier = Modifier.padding(bottom = 14.dp)
                )
            }

            // Check if Football is selected
            if (!state.selectedSport.isFootball) {
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = stringResource(R.string.transfers_football_only),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                // Search Club Input
                item {
                    OutlinedTextField(
                        value = state.searchQuery,
                        onValueChange = { viewModel.onSearchQueryChanged(it) },
                        placeholder = {
                            Text(
                                text = stringResource(R.string.transfer_search_team),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Club Selector Chips
                item {
                    Text(
                        text = "SELECT CLUB",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        items(state.clubs) { club ->
                            val isSelected = state.selectedClub?.id == club.id
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(
                                        if (isSelected) VoltGreen else MaterialTheme.colorScheme.surface
                                    )
                                    .clickable { viewModel.onClubSelected(club) }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                AsyncImage(
                                    model = club.logo,
                                    contentDescription = club.name,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = club.name,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Transfer Items
                if (state.isLoading) {
                    item {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            ShimmerCard(height = 100)
                            ShimmerCard(height = 100)
                            ShimmerCard(height = 100)
                        }
                    }
                } else if (state.errorMessage != null && state.transfers.isEmpty()) {
                    item {
                        ErrorState(
                            message = state.errorMessage ?: stringResource(R.string.state_error_generic),
                            onRetry = { viewModel.refresh() }
                        )
                    }
                } else if (state.transfers.isEmpty()) {
                    item {
                        EmptyState(message = stringResource(R.string.state_empty))
                    }
                } else {
                    items(state.transfers) { transfer ->
                        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                            TransferCard(transfer = transfer)
                        }
                    }
                }
            }
        }
    }
}
