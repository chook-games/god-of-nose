package com.godofnose.sneezeandthunder.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.godofnose.sneezeandthunder.game.GameState
import kotlinx.coroutines.launch

/**
 * TutorialScreen — Interactive tutorial for new players.
 *
 * Shows a series of pages explaining game mechanics:
 * 1. Welcome & Story
 * 2. How to Play (tap to sneeze)
 * 3. Stamina & Recharge
 * 4. Items & Rewards
 * 5. Upgrades & Shop
 * 6. Frenzy Mode (Pollen Cloud)
 * 7. Boss Battles
 * 8. Achievements & Cosmetics
 * 9. Offline Progress
 * 10. You're Ready!
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorialScreen(
    gameState: GameState,
    onComplete: () -> Unit
) {
    val pages = remember { TutorialData.pages }
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🎓 Tutorial", fontWeight = FontWeight.Bold) },
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
        ) {
            // ── Pager ──
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { pageIndex ->
                TutorialPage(pages[pageIndex])
            }

            // ── Bottom Controls ──
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Page indicator dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    pages.indices.forEach { index ->
                        Box(
                            modifier = Modifier
                                .size(if (pagerState.currentPage == index) 10.dp else 8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (pagerState.currentPage == index)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.outlineVariant
                                )
                        )
                    }
                }

                // Navigation buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Skip button (only on first page)
                    if (pagerState.currentPage == 0) {
                        TextButton(onClick = {
                            gameState.completeTutorial()
                            onComplete()
                        }) {
                            Text("Skip")
                        }
                    } else {
                        TextButton(onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        }) {
                            Text("← Previous")
                        }
                    }

                    // Next / Finish button
                    if (pagerState.currentPage < pages.size - 1) {
                        Button(onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }) {
                            Text("Next →")
                        }
                    } else {
                        Button(
                            onClick = {
                                gameState.completeTutorial()
                                onComplete()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("✅ Let's Go!")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TutorialPage(page: TutorialPageData) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Emoji icon
        Text(
            text = page.emoji,
            fontSize = 64.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Title
        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Description
        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 24.sp
        )

        // Tips
        if (page.tips.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "💡 Tips",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    page.tips.forEach { tip ->
                        Text(
                            text = "• $tip",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

private data class TutorialPageData(
    val emoji: String,
    val title: String,
    val description: String,
    val tips: List<String> = emptyList()
)

private object TutorialData {
    val pages = listOf(
        TutorialPageData(
            emoji = "👃⚡",
            title = "Welcome, God of Nose!",
            description = "You are Babis, the God of Nose, Sneeze, and Thunder! Your divine sneezes can destroy anything. Tap items to sneeze on them and collect rewards!",
            tips = listOf(
                "Tap the 👃 SNEEZE! button to attack",
                "Destroy items to earn Coffee Drops",
                "Defeat bosses to earn Tissues"
            )
        ),
        TutorialPageData(
            emoji = "👆",
            title = "How to Play",
            description = "Tap the big 👃 SNEEZE! button at the bottom of the game screen. Each tap makes Babis sneeze, dealing damage to the current item on the right side of the screen.",
            tips = listOf(
                "Watch the item's HP bar go down",
                "When HP reaches 0, you get rewards!",
                "A new item appears automatically"
            )
        ),
        TutorialPageData(
            emoji = "⚡",
            title = "Stamina & Recharge",
            description = "Each sneeze costs stamina. When your stamina runs out, Babis needs to recharge before you can sneeze again. Watch the stamina bar at the top!",
            tips = listOf(
                "Stamina recharges automatically over time",
                "Upgrade Max Stamina to sneeze more",
                "Frenzy mode gives unlimited stamina!"
            )
        ),
        TutorialPageData(
            emoji = "🎯",
            title = "Items & Rewards",
            description = "Each zone has different items to destroy. Tougher items give more Coffee Drops. Progress through 5 zones, each harder than the last!",
            tips = listOf(
                "Zone 1: Easy items (low HP)",
                "Zone 5: Epic items (high HP, big rewards)",
                "Boss items appear every 10 items"
            )
        ),
        TutorialPageData(
            emoji = "🛒",
            title = "Upgrades & Shop",
            description = "Spend Coffee Drops on upgrades to become stronger! Visit the Upgrade Shop to increase Sneeze Power, Max Stamina, and Crit Chance.",
            tips = listOf(
                "Sneeze Power: Deal more damage per sneeze",
                "Max Stamina: Sneeze more before recharging",
                "Crit Chance: Land double-damage hits!"
            )
        ),
        TutorialPageData(
            emoji = "🌿",
            title = "Frenzy Mode (Pollen Cloud)",
            description = "Sometimes a Pollen Cloud appears! Tap it to activate Frenzy Mode. During Frenzy, you sneeze infinitely with no stamina cost for 15 seconds!",
            tips = listOf(
                "Pollen Clouds appear randomly every ~20 seconds",
                "Frenzy is great for tough items and bosses",
                "Watch for the green cloud icon!"
            )
        ),
        TutorialPageData(
            emoji = "👹",
            title = "Boss Battles",
            description = "Every 10 items, a Boss appears! Bosses have multiple phases with special attacks: Stamina Drain, Damage Reflect, Self-Heal, Enrage, and Shield.",
            tips = listOf(
                "Bosses have 3 phases that get harder",
                "Use Frenzy mode during boss fights",
                "Defeat bosses for Tissues (premium currency)"
            )
        ),
        TutorialPageData(
            emoji = "🏆",
            title = "Achievements & Cosmetics",
            description = "Complete achievements to earn bonus rewards! Visit the Achievements screen to track your progress. Spend Tissues on cool cosmetics in the Shop!",
            tips = listOf(
                "31 achievements to unlock",
                "Cosmetics include hats, glasses, and sneeze colors",
                "Check achievements often for free rewards"
            )
        ),
        TutorialPageData(
            emoji = "💤",
            title = "Offline Progress",
            description = "Even when you're away, Babis keeps sneezing! When you return, you'll find Coffee Drops and Tissues waiting for you. Stamina is fully restored too!",
            tips = listOf(
                "Offline earnings cap at 8 hours",
                "Better upgrades = more offline earnings",
                "Always come back to collect!"
            )
        ),
        TutorialPageData(
            emoji = "🎉",
            title = "You're Ready!",
            description = "You now know everything you need to become the ultimate God of Nose! Go forth and sneeze with divine power!",
            tips = listOf(
                "Check HOWTOPLAY.md for more details",
                "Have fun and happy sneezing!",
                "👃⚡💪"
            )
        )
    )
}
