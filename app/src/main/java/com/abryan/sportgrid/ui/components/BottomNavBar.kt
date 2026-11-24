package com.abryan.sportgrid.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.abryan.sportgrid.ui.navigation.Screen

@Composable
fun SportGridBottomBar(
    currentRoute: String?,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(
                horizontal = if (isLandscape) 64.dp else 20.dp,
                vertical = if (isLandscape) 2.dp else 8.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            shape = RoundedCornerShape(if (isLandscape) 20.dp else 32.dp),
            shadowElevation = 8.dp,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = if (isLandscape) 12.dp else 6.dp,
                        vertical = if (isLandscape) 2.dp else 6.dp
                    ),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Screen.bottomNavItems.forEach { screen ->
                    val selected = currentRoute == screen.route
                    val title = stringResource(screen.titleRes)
                    val interactionSource = remember { MutableInteractionSource() }

                    // Area sentuh luas per item navigasi
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(if (isLandscape) 16.dp else 24.dp))
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) {
                                onNavigate(screen)
                            }
                            .padding(vertical = if (isLandscape) 1.dp else 4.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(if (isLandscape) 36.dp else 46.dp)
                                .clip(CircleShape)
                                .background(
                                    if (selected) MaterialTheme.colorScheme.primary else Color.Transparent
                                )
                        ) {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = title,
                                tint = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(if (isLandscape) 18.dp else 22.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
