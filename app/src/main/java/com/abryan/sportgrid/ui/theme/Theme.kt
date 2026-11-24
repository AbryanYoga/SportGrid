package com.abryan.sportgrid.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

private val DarkColorScheme = darkColorScheme(
    primary = VoltGreen,
    onPrimary = DeepCarbon,
    primaryContainer = CardElevatedDark,
    onPrimaryContainer = VoltGreen,
    secondary = TextSecondaryDark,
    onSecondary = DeepCarbon,
    secondaryContainer = ChipInactiveDark,
    onSecondaryContainer = TextPrimaryDark,
    tertiary = LiveRed,
    onTertiary = Color.White,
    background = DeepCarbon,
    onBackground = TextPrimaryDark,
    surface = CardDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = ChipInactiveDark,
    onSurfaceVariant = TextSecondaryDark,
    error = LiveRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = Color.White,
    primaryContainer = SurfaceVariantLight,
    onPrimaryContainer = PrimaryLight,
    secondary = TextSecondaryLight,
    onSecondary = Color.White,
    secondaryContainer = SurfaceVariantLight,
    onSecondaryContainer = TextPrimaryLight,
    tertiary = LiveRed,
    onTertiary = Color.White,
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondaryLight,
    error = LiveRed,
    onError = Color.White
)

@Composable
fun SportGridTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disabled to preserve consistent sports branding
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Preview(name = "SportGrid Theme - Dark", showBackground = true)
@Composable
private fun ThemePreviewDark() {
    SportGridTheme(darkTheme = true) {
        ThemeDemoCard()
    }
}

@Preview(name = "SportGrid Theme - Light", showBackground = true)
@Composable
private fun ThemePreviewLight() {
    SportGridTheme(darkTheme = false) {
        ThemeDemoCard()
    }
}

@Composable
private fun ThemeDemoCard() {
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.padding(16.dp)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SPORTGRID",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.error, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "LIVE",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onError
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "76 : 64",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Top Events")
                }
            }
        }
    }
}
