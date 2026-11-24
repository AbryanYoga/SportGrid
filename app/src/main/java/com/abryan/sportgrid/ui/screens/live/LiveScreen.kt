package com.abryan.sportgrid.ui.screens.live

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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abryan.sportgrid.R
import com.abryan.sportgrid.domain.model.Match
import com.abryan.sportgrid.ui.components.EmptyState
import com.abryan.sportgrid.ui.components.MatchCard
import com.abryan.sportgrid.ui.components.ShimmerCard
import com.abryan.sportgrid.ui.components.SportFilterChips
import com.abryan.sportgrid.ui.theme.VoltGreen
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class CalendarDayItem(
    val dayName: String,
    val dayNumber: String,
    val offset: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveScreen(
    modifier: Modifier = Modifier,
    viewModel: LiveViewModel = viewModel(
        factory = LiveViewModel.provideFactory(LocalContext.current.applicationContext as Application)
    ),
    onMatchClick: (Int) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                        datePickerState.selectedDateMillis?.let { selectedMillis ->
                            val calSelected = Calendar.getInstance().apply { timeInMillis = selectedMillis }
                            val calToday = Calendar.getInstance().apply {
                                set(Calendar.HOUR_OF_DAY, 0)
                                set(Calendar.MINUTE, 0)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }
                            calSelected.set(Calendar.HOUR_OF_DAY, 0)
                            calSelected.set(Calendar.MINUTE, 0)
                            calSelected.set(Calendar.SECOND, 0)
                            calSelected.set(Calendar.MILLISECOND, 0)
                            val diffDays = ((calSelected.timeInMillis - calToday.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()
                            viewModel.onDateSelected(diffDays)
                        }
                    }
                ) {
                    Text("Terapkan", color = VoltGreen, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Batal")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // 7-day horizontal calendar strip (3 days past, today, 3 days future)
    val calendarDays = remember {
        val list = mutableListOf<CalendarDayItem>()
        val cal = Calendar.getInstance()
        val dayFormat = SimpleDateFormat("EEE", Locale.US)
        val numFormat = SimpleDateFormat("d", Locale.US)

        for (offset in -3..3) {
            val c = cal.clone() as Calendar
            c.add(Calendar.DAY_OF_YEAR, offset)
            list.add(
                CalendarDayItem(
                    dayName = dayFormat.format(c.time),
                    dayNumber = numFormat.format(c.time),
                    offset = offset
                )
            )
        }
        list
    }

    // Filter matches based on search query
    val displayedMatches = remember(state.liveMatches, state.todayMatches, state.searchQuery) {
        val source = if (state.liveMatches.isNotEmpty()) state.liveMatches else state.todayMatches
        if (state.searchQuery.isBlank()) source
        else source.filter {
            it.homeTeamName.contains(state.searchQuery, ignoreCase = true) ||
            it.awayTeamName.contains(state.searchQuery, ignoreCase = true) ||
            it.leagueName.contains(state.searchQuery, ignoreCase = true)
        }
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
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
            // Header: "SCORES" + Controls (from reference image)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SCORES",
                        style = MaterialTheme.typography.headlineMedium.copy(fontSize = 24.sp),
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(onClick = { viewModel.refresh() }) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Filter",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    placeholder = {
                        Text(
                            text = stringResource(R.string.search_leagues),
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

            // Date Row (Calendar Strip + Custom Date Picker)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .padding(start = 16.dp, end = 8.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .clickable { showDatePicker = true }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Pilih Tanggal Bebas",
                            tint = VoltGreen,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    LazyRow(
                        contentPadding = PaddingValues(end = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(calendarDays) { day ->
                            val isSelected = day.offset == state.selectedDateOffset

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(
                                        if (isSelected) VoltGreen else MaterialTheme.colorScheme.surface
                                    )
                                    .clickable { viewModel.onDateSelected(day.offset) }
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = day.dayName,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = day.dayNumber,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // Sport Chips Filter
            item {
                SportFilterChips(
                    selectedSport = state.selectedSport,
                    onSportSelected = { viewModel.onSportSelected(it) },
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Fallback Notice (if no live match right now)
            if (state.isFallback && state.liveMatches.isEmpty() && displayedMatches.isNotEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.no_live_matches_fallback),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Matches List
            if (state.isLoading) {
                item {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ShimmerCard(height = 130)
                        ShimmerCard(height = 130)
                        ShimmerCard(height = 130)
                    }
                }
            } else if (displayedMatches.isEmpty()) {
                item {
                    EmptyState(message = stringResource(R.string.state_empty))
                }
            } else {
                items(displayedMatches) { match ->
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                        MatchCard(
                            match = match,
                            onClick = { onMatchClick(match.id) }
                        )
                    }
                }
            }
            }
        }
    }
}
