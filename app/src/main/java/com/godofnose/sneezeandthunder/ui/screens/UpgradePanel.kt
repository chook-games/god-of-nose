package com.godofnose.sneezeandthunder.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.godofnose.sneezeandthunder.game.CosmeticSlot
import com.godofnose.sneezeandthunder.game.CosmeticsDatabase
import com.godofnose.sneezeandthunder.game.GameState

/**
 * Upgrade & Cosmetics overlay panel.
 * Shown on top of the GameScreen when the player taps the upgrade button.
 */
@Composable
fun UpgradePanel(
    gameState: GameState,
    visible: Boolean,
    onDismiss: () -> Unit
) {
    var showPrestigeConfirm by remember { mutableStateOf(false) }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        // Semi-transparent backdrop
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center
        ) {
            // Panel card — stop click propagation
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(vertical = 32.dp)
                    .clickable(enabled = false) { /* consume click */ },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // ── Title ──────────────────────────────────────────────────
                    Text(
                        text = "⚡ UPGRADES",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "☕ ${gameState.coffeeDrops} available",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // ── Sneeze Power Upgrade ───────────────────────────────────
                    UpgradeRow(
                        emoji = "💪",
                        label = "Sneeze Power",
                        value = "${gameState.sneezePower.toInt()} / tap",
                        level = gameState.sneezePowerLevel,
                        cost = gameState.getSneezePowerCost(),
                        canAfford = gameState.coffeeDrops >= gameState.getSneezePowerCost(),
                        onUpgrade = { gameState.upgradeSneezePower() }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // ── Max Stamina Upgrade ────────────────────────────────────
                    UpgradeRow(
                        emoji = "⚡",
                        label = "Max Stamina",
                        value = "${gameState.maxStamina.toInt()}",
                        level = gameState.maxStaminaLevel,
                        cost = gameState.getMaxStaminaCost(),
                        canAfford = gameState.coffeeDrops >= gameState.getMaxStaminaCost(),
                        onUpgrade = { gameState.upgradeMaxStamina() }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // ── Crit Chance Upgrade ────────────────────────────────────
                    UpgradeRow(
                        emoji = "🎯",
                        label = "Crit Chance",
                        value = "${(gameState.critChance * 100).toInt()}%",
                        level = gameState.critChanceLevel,
                        cost = gameState.getCritChanceCost(),
                        canAfford = gameState.coffeeDrops >= gameState.getCritChanceCost(),
                        onUpgrade = { gameState.upgradeCritChance() }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // ── Auto-Sneeze Upgrade ───────────────────────────────────
                    UpgradeRow(
                        emoji = "🤖",
                        label = "Auto-Sneeze",
                        value = if (gameState.autoSneezeLevel > 0)
                            "Every ${gameState.autoSneezeInterval / 1000}s"
                        else "Off",
                        level = gameState.autoSneezeLevel,
                        cost = gameState.getAutoSneezeCost(),
                        canAfford = gameState.coffeeDrops >= gameState.getAutoSneezeCost(),
                        onUpgrade = { gameState.upgradeAutoSneeze() }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // ── Divider ────────────────────────────────────────────────
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(1.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    ) {}

                    Spacer(modifier = Modifier.height(16.dp))

                    // ── Cosmetics Section ──────────────────────────────────────
                    Text(
                        text = "🎩 COSMETICS",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary
                    )

                    Text(
                        text = "🧻 ${gameState.tissues} tissues available",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Hats
                    CosmeticSection(
                        title = "Hats",
                        gameState = gameState,
                        slot = CosmeticSlot.HAT
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Glasses
                    CosmeticSection(
                        title = "Glasses",
                        gameState = gameState,
                        slot = CosmeticSlot.GLASSES
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Sneeze Colors
                    CosmeticSection(
                        title = "Sneeze Colors",
                        gameState = gameState,
                        slot = CosmeticSlot.SNEEZE_COLOR
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // ── Prestige Section ───────────────────────────────────────
                    if (gameState.canPrestige()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF1B5E20).copy(alpha = 0.3f)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "🌟 PRESTIGE",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF81C784)
                                )
                                Text(
                                    text = "Reset progress for permanent bonus!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFA5D6A7).copy(alpha = 0.7f)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Current: ${gameState.getPrestigeBonusDescription()}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFC8E6C9).copy(alpha = 0.6f)
                                )
                                Text(
                                    text = "Next: ×${String.format("%.2f", gameState.prestigeMultiplier + 0.25f)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFC8E6C9).copy(alpha = 0.6f)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { showPrestigeConfirm = true },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF2E7D32)
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("🌟 PRESTIGE NOW", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ── Close button ───────────────────────────────────────────
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("Close", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // ── Prestige confirmation dialog ──────────────────────────────────────────
    if (showPrestigeConfirm) {
        AlertDialog(
            onDismissRequest = { showPrestigeConfirm = false },
            title = {
                Text("🌟 Prestige?", fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(
                        "Reset ALL progress for a permanent ×0.25 damage bonus?",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "You keep: achievements, cosmetics, prestige level.\n" +
                        "You lose: coffee, tissues, upgrades, zone progress, stats.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFFF9800)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Next bonus: ×${String.format("%.2f", gameState.prestigeMultiplier + 0.25f)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        gameState.performPrestige()
                        showPrestigeConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text("PRESTIGE!", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPrestigeConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// ── Single upgrade row ────────────────────────────────────────────────────────

@Composable
private fun UpgradeRow(
    emoji: String,
    label: String,
    value: String,
    level: Int,
    cost: Long,
    canAfford: Boolean,
    onUpgrade: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Emoji
            Text(text = emoji, fontSize = 28.sp)
            Spacer(modifier = Modifier.width(12.dp))

            // Label + value
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "$value • Lv.$level",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            // Cost & button
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "☕ $cost",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (canAfford) Color(0xFF4CAF50) else Color(0xFFF44336)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = onUpgrade,
                    enabled = canAfford,
                    modifier = Modifier.height(32.dp),
                    contentPadding = ButtonDefaults.TextButtonContentPadding,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    )
                ) {
                    Text("+", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ── Cosmetic section ──────────────────────────────────────────────────────────

@Composable
private fun CosmeticSection(
    title: String,
    gameState: GameState,
    slot: CosmeticSlot
) {
    val cosmetics = CosmeticsDatabase.getBySlot(slot)

    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(4.dp))

    cosmetics.forEach { cosmetic ->
        val owned = gameState.hasCosmetic(cosmetic.id)
        val equipped = when (slot) {
            CosmeticSlot.HAT -> gameState.equippedHat == cosmetic.id
            CosmeticSlot.GLASSES -> gameState.equippedGlasses == cosmetic.id
            CosmeticSlot.SNEEZE_COLOR -> gameState.equippedSneezeColor == cosmetic.id
        }
        val canBuy = gameState.tissues >= cosmetic.costInTissues

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            shape = RoundedCornerShape(8.dp),
            color = if (equipped) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = cosmetic.emoji, fontSize = 22.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = cosmetic.name,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f)
                )

                if (owned) {
                    if (equipped) {
                        Text(
                            text = "✅",
                            fontSize = 18.sp
                        )
                    } else {
                        OutlinedButton(
                            onClick = { gameState.equipCosmetic(cosmetic.id, slot) },
                            modifier = Modifier.height(28.dp),
                            contentPadding = ButtonDefaults.TextButtonContentPadding
                        ) {
                            Text("Equip", fontSize = 11.sp)
                        }
                    }
                } else {
                    Button(
                        onClick = { gameState.buyCosmetic(cosmetic.id, cosmetic.costInTissues) },
                        enabled = canBuy,
                        modifier = Modifier.height(28.dp),
                        contentPadding = ButtonDefaults.TextButtonContentPadding,
                        colors = ButtonDefaults.buttonColors(
                            disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                        )
                    ) {
                        Text("🧻${cosmetic.costInTissues}", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
