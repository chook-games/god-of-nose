package com.godofnose.sneezeandthunder.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.godofnose.sneezeandthunder.game.GameState

/**
 * StatisticsScreen — Displays detailed player statistics and progress.
 *
 * Shows:
 * - Total sneezes, items destroyed, crits, frenzy activations
 * - Sneezes per zone (bar chart)
 * - Total play time
 * - Total coffee/tissues earned
 * - Bosses defeated
 * - Upgrades purchased
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    gameState: GameState,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📊 Statistics", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("← Back", color = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Overview Card ──
            StatCard(title = "📈 Overview") {
                StatRow("Total Sneezes", formatNumber(gameState.totalSneezes))
                StatRow("Items Destroyed", formatNumber(gameState.totalItemsDestroyed))
                StatRow("Critical Hits", formatNumber(gameState.totalCrits))
                StatRow("Frenzy Activations", "${gameState.frenzyActivations}")
                StatRow("Bosses Defeated", "${gameState.bossesDefeated}")
                StatRow("Upgrades Purchased", "${gameState.totalUpgradesBought}")
                StatRow("Stamina Exhaustions", "${gameState.staminaExhaustions}")
            }

            // ── Currency Card ──
            StatCard(title = "💰 Currencies") {
                StatRow("Current Coffee Drops", formatNumber(gameState.coffeeDrops))
                StatRow("Total Coffee Earned", formatNumber(gameState.totalCoffeeEarned))
                StatRow("Current Tissues", formatNumber(gameState.tissues))
                StatRow("Total Tissues Earned", formatNumber(gameState.totalTissuesEarned))
            }

            // ── Play Time Card ──
            StatCard(title = "⏱️ Play Time") {
                val hours = gameState.playTimeSeconds / 3600
                val minutes = (gameState.playTimeSeconds % 3600) / 60
                val seconds = gameState.playTimeSeconds % 60
                StatRow("Total Play Time", "${hours}h ${minutes}m ${seconds}s")
                StatRow("Current Zone", "Zone ${gameState.currentZone + 1}")
                StatRow("Sneeze Power", "%.1f".format(gameState.sneezePower))
                StatRow("Crit Chance", "%.1f%%".format(gameState.critChance * 100))
                StatRow("Max Stamina", "%.0f".format(gameState.maxStamina))
            }

            // ── Sneezes Per Zone Chart ──
            StatCard(title = "📊 Sneezes Per Zone") {
                SneezesPerZoneChart(gameState)
            }

            // ── Tutorial Status ──
            StatCard(title = "🎓 Tutorial") {
                StatRow(
                    "Tutorial Completed",
                    if (gameState.tutorialCompleted == 1) "✅ Yes" else "❌ No"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun SneezesPerZoneChart(gameState: GameState) {
    val zoneNames = listOf("Zone 1", "Zone 2", "Zone 3", "Zone 4", "Zone 5")
    val zoneColors = listOf(
        Color(0xFF4CAF50), // Green
        Color(0xFF2196F3), // Blue
        Color(0xFFFF9800), // Orange
        Color(0xFF9C27B0), // Purple
        Color(0xFFF44336)  // Red
    )

    val maxSneezes = gameState.sneezesPerZone.maxOrNull()?.toFloat()?.coerceAtLeast(1f) ?: 1f

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        gameState.sneezesPerZone.forEachIndexed { index, count ->
            val fraction = count.toFloat() / maxSneezes
            val animatedFraction by animateFloatAsState(
                targetValue = fraction,
                animationSpec = tween(durationMillis = 500),
                label = "bar_$index"
            )

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = zoneNames[index],
                        style = MaterialTheme.typography.bodySmall,
                        color = zoneColors[index]
                    )
                    Text(
                        text = formatNumber(count),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(animatedFraction)
                            .background(
                                color = zoneColors[index].copy(alpha = 0.7f),
                                shape = RoundedCornerShape(8.dp)
                            )
                    )
                }
            }
        }
    }
}

/** Format large numbers with commas. */
private fun formatNumber(value: Long): String {
    if (value < 1000) return "$value"
    if (value < 1_000_000) return "%.1fK".format(value / 1000.0)
    if (value < 1_000_000_000) return "%.2fM".format(value / 1_000_000.0)
    return "%.2fB".format(value / 1_000_000_000.0)
}
