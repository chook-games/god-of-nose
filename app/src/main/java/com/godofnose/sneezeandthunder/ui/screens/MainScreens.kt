package com.godofnose.sneezeandthunder.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.godofnose.sneezeandthunder.engine.GameSaveData
import com.godofnose.sneezeandthunder.engine.SaveManager
import com.godofnose.sneezeandthunder.engine.SoundEngine
import com.godofnose.sneezeandthunder.game.*
import com.godofnose.sneezeandthunder.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

// ─────────────────────────────────────────────────────────────────────────────
// MainMenuScreen
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Main Menu — the first screen players see.
 *
 * Shows:
 * - Animated Stickman Babis
 * - Game title
 * - Navigation buttons
 * - Daily reward popup (if available)
 * - Offline earnings popup (if any)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenuScreen(
    gameState: GameState,
    onPlayClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onAchievementsClick: () -> Unit,
    onShopClick: () -> Unit,
    onStatisticsClick: () -> Unit,
    onTutorialClick: () -> Unit
) {
    var showDailyReward by remember { mutableStateOf(false) }
    var showOfflineEarnings by remember { mutableStateOf(false) }
    var claimedRewardDescription by remember { mutableStateOf("") }
    var claimError by remember { mutableStateOf(false) }

    // Check for daily reward on first composition
    LaunchedEffect(Unit) {
        if (gameState.getAvailableDailyReward() >= 0) {
            showDailyReward = true
        }
    }

    // Check for offline earnings
    LaunchedEffect(Unit) {
        if (gameState.offlineSeconds > 0L) {
            showOfflineEarnings = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A0E27),
                        Color(0xFF1A1A2E),
                        Color(0xFF0D1117)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // ── Top: Title ───────────────────────────────────────────────
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(
                    text = "👃",
                    fontSize = 64.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "God of Nose",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = Gold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Sneeze & Thunder ⚡",
                    style = MaterialTheme.typography.titleLarge,
                    color = LightningYellow.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Prestige badge
                if (gameState.prestigeLevel > 0) {
                    Text(
                        text = "🌟 Prestige Lv.${gameState.prestigeLevel} (${gameState.getPrestigeBonusDescription()})",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF81C784),
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Tap your nose to greatness!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            // ── Center: Stickman Babis ───────────────────────────────────
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .padding(8.dp)
            ) {
                StickmanBabis(
                    modifier = Modifier.fillMaxSize(),
                    hatId = gameState.equippedHat,
                    glassesId = gameState.equippedGlasses
                )
            }

            // ── Bottom: Navigation Buttons ──────────────────────────────
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Play button — prominent
                Button(
                    onClick = onPlayClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Gold,
                        contentColor = DarkText
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                ) {
                    Text(
                        text = "⚡ SNEEZE!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp
                    )
                }

                // Row 2: Shop, Achievements
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onShopClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Gold
                        )
                    ) {
                        Text("🛒 Shop", fontWeight = FontWeight.SemiBold)
                    }

                    OutlinedButton(
                        onClick = onAchievementsClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = StormPurpleLight
                        )
                    ) {
                        Text("🏆 Achievements", fontWeight = FontWeight.SemiBold)
                    }
                }

                // Row 3: Settings, Statistics, Tutorial
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onSettingsClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("⚙️", fontSize = 14.sp)
                    }

                    OutlinedButton(
                        onClick = onStatisticsClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("📊", fontSize = 14.sp)
                    }

                    OutlinedButton(
                        onClick = onTutorialClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("🎓", fontSize = 14.sp)
                    }
                }
            }
        }

        // ── Daily Reward Popup ──────────────────────────────────────────
        if (showDailyReward) {
            AlertDialog(
                onDismissRequest = { showDailyReward = false },
                title = {
                    Text(
                        text = "🎁 Daily Reward!",
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (gameState.dailyStreak > 1) {
                            Text(
                                text = "🔥 ${gameState.dailyStreak}-day streak!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFFFF9800),
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        val rewardIndex = gameState.dailyRewardIndex
                        val reward = if (rewardIndex in GameState.DAILY_REWARDS.indices)
                            GameState.DAILY_REWARDS[rewardIndex]
                        else null

                        if (reward != null) {
                            Text(
                                text = "Day ${rewardIndex + 1}:",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = reward.description,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Gold
                            )
                        }

                        if (claimError) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Already claimed today!",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        if (claimedRewardDescription.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "✅ Claimed!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF4CAF50),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val result = gameState.claimDailyReward()
                            if (result != null) {
                                claimedRewardDescription = result.description
                                claimError = false
                            } else {
                                claimError = true
                            }
                        },
                        enabled = claimedRewardDescription.isEmpty()
                    ) {
                        Text("Claim")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDailyReward = false }) {
                        Text("Later")
                    }
                }
            )
        }

        // ── Offline Earnings Popup ──────────────────────────────────────
        if (showOfflineEarnings) {
            AlertDialog(
                onDismissRequest = {
                    showOfflineEarnings = false
                    gameState.clearOfflineEarnings()
                },
                title = {
                    Text(
                        text = "💤 Welcome Back!",
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "You were away for:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )

                        val seconds = gameState.offlineSeconds
                        val hours = seconds / 3600
                        val minutes = (seconds % 3600) / 60
                        val secs = seconds % 60
                        Text(
                            text = "${hours}h ${minutes}m ${secs}s",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Gold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        if (gameState.offlineCoffeeEarned > 0L) {
                            Text(
                                text = "☕ +${gameState.offlineCoffeeEarned} Coffee Drops",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color(0xFF4CAF50)
                            )
                        }
                        if (gameState.offlineTissuesEarned > 0L) {
                            Text(
                                text = "🧻 +${gameState.offlineTissuesEarned} Tissues",
                                style = MaterialTheme.typography.bodyLarge,
                                color = StormPurpleLight
                            )
                        }
                        Text(
                            text = "⚡ Stamina fully restored!",
                            style = MaterialTheme.typography.bodySmall,
                            color = StaminaBlue
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        showOfflineEarnings = false
                        gameState.clearOfflineEarnings()
                    }) {
                        Text("Great!")
                    }
                }
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// GameScreen
// ─────────────────────────────────────────────────────────────────────────────

/**
 * The main game screen where the player sneezes on items.
 *
 * Features:
 * - 2D room environment with perspective floor, window, and dynamic lighting
 * - Animated Stickman Babis (left side) and target item (right side) on the floor
 * - Sneeze projectile animation traveling from stickman to target
 * - Clicker HUD: header bars, stamina/HP, big sneeze button, item nav
 * - Notch-safe padding using WindowInsets
 * - Frenzy mode, boss battles, pollen clouds, achievement popups
 * - Upgrade panel overlay
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    gameState: GameState,
    soundEngine: SoundEngine? = null,
    onBackToMenu: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var showUpgradePanel by remember { mutableStateOf(false) }
    var sneezeAnim by remember { mutableFloatStateOf(0f) }
    var damageText by remember { mutableStateOf("") }
    var damageTextAlpha by remember { mutableStateOf(0f) }
    var bossMessage by remember { mutableStateOf("") }
    var bossMessageTimer by remember { mutableStateOf(0) }
    var achievementPopup by remember { mutableStateOf("") }
    var achievementPopupVisible by remember { mutableStateOf(false) }
    var pollenCloudVisible by remember { mutableStateOf(false) }
    var pollenCloudTimer by remember { mutableLongStateOf(0L) }

    // Notch-safe padding via WindowInsets
    val insets = WindowInsets.systemBars
    val topInset = with(LocalDensity.current) { insets.getTop(this).toFloat() }
    val bottomInset = with(LocalDensity.current) { insets.getBottom(this).toFloat() }

    // Zone background colors — used to tint the room
    val zoneBackgrounds = listOf(
        ZoneHome,
        ZoneCoffee,
        ZoneMarket,
        ZoneOffice,
        ZoneTaverna
    )

    val currentZoneColors = zoneBackgrounds.getOrNull(gameState.currentZone) ?: ZoneHome
    val bgColor by animateColorAsState(
        targetValue = currentZoneColors[0],
        animationSpec = tween(durationMillis = 800),
        label = "bgColor"
    )

    // ── Game Loop (every 100ms) ─────────────────────────────────────────
    LaunchedEffect(Unit) {
        while (true) {
            delay(100L)

            // Tick frenzy timer
            gameState.tickFrenzy(100L)

            // Tick stamina recharge
            if (gameState.isRecharging == 1 || !gameState.isFrenzyActive) {
                val rechargeAmount = GameState.STAMINA_RECHARGE_RATE * 0.1f // per 100ms
                gameState.rechargeStamina(rechargeAmount)
            }

            // Tick boss attack
            val currentItem = ItemDatabase.getItem(gameState.currentZone, gameState.currentItem)
            if (currentItem.isBoss && gameState.bossCurrentPhase >= 0) {
                val msg = gameState.tickBossAttack(100L, currentItem.phases)
                if (msg != null) {
                    bossMessage = msg
                    bossMessageTimer = 30 // show for ~3 seconds
                    soundEngine?.playBossAttack()
                }

                val phaseMsg = gameState.checkBossPhaseTransition(currentItem.phases)
                if (phaseMsg != null) {
                    bossMessage = phaseMsg
                    bossMessageTimer = 30
                    soundEngine?.playBossPhase()
                }
            }

            // Decrease boss message timer
            if (bossMessageTimer > 0) {
                bossMessageTimer--
                if (bossMessageTimer <= 0) {
                    bossMessage = ""
                }
            }

            // Pollen cloud spawn timer
            if (!gameState.isFrenzyActive && !pollenCloudVisible) {
                pollenCloudTimer += 100L
                if (pollenCloudTimer >= GameState.POLLEN_CLOUD_INTERVAL_MS) {
                    pollenCloudVisible = true
                    pollenCloudTimer = 0L
                }
            }

            // Auto-sneeze logic
            if (gameState.autoSneezeInterval > 0L && gameState.itemHp > 0f) {
                // Auto-sneeze fires every autoSneezeInterval milliseconds
                // We handle this in a separate timer below
            }
        }
    }

    // ── Auto-Sneeze Timer ───────────────────────────────────────────────
    LaunchedEffect(gameState.autoSneezeInterval) {
        while (gameState.autoSneezeInterval > 0L) {
            delay(gameState.autoSneezeInterval)
            if (gameState.itemHp > 0f && gameState.isItemDestroyed == 0) {
                val result = gameState.sneeze()
                when (result) {
                    SneezeResult.HIT -> {
                        soundEngine?.playRandomSneeze()
                        sneezeAnim = 1f
                    }
                    SneezeResult.CRIT -> {
                        soundEngine?.playCrit()
                        damageText = "CRIT! 💥"
                        damageTextAlpha = 1f
                        sneezeAnim = 1f
                    }
                    SneezeResult.DESTROYED -> {
                        soundEngine?.playDestroy()
                        handleItemDestroyed(gameState)
                    }
                    SneezeResult.NO_STAMINA -> {
                        soundEngine?.playNoStamina()
                    }
                }
            }
        }
    }

    // ── Damage text fade ────────────────────────────────────────────────
    LaunchedEffect(damageTextAlpha) {
        if (damageTextAlpha > 0f) {
            delay(600L)
            damageTextAlpha = 0f
        }
    }

    // ── Sneeze animation reset ─────────────────────────────────────────
    LaunchedEffect(sneezeAnim) {
        if (sneezeAnim > 0f) {
            delay(200L)
            sneezeAnim = 0f
        }
    }

    // ── Achievement check on destroy ────────────────────────────────────
    LaunchedEffect(gameState.totalItemsDestroyed) {
        val newly = gameState.checkAchievements()
        if (newly.isNotEmpty()) {
            val names = newly.mapNotNull { AchievementDatabase.getById(it)?.title }
            achievementPopup = names.joinToString("\n") { "🏆 $it" }
            achievementPopupVisible = true
            soundEngine?.playAchievement()
            delay(3000L)
            achievementPopupVisible = false
        }
    }

    // ── Root Layout: full-screen Box ─────────────────────────────────────
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0E27))
    ) {
        // ── 1. Canvas Background: 2D Room Environment ──────────────────────
        val item = ItemDatabase.getItem(gameState.currentZone, gameState.currentItem)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Room dimensions
            val floorY = h * 0.62f // horizon: back wall meets floor
            val roomColor = bgColor
            val wallDark = roomColor.copy(alpha = 0.85f)
            val wallLight = roomColor.copy(alpha = 0.95f)

            // ── Back Wall ──
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(wallDark, wallLight),
                    startY = 0f,
                    endY = floorY
                ),
                topLeft = Offset(0f, 0f),
                size = Size(w, floorY)
            )

            // ── Floor (perspective trapezoid) ──
            val floorPath = Path().apply {
                moveTo(w * 0.05f, floorY)       // top-left (narrow)
                lineTo(w * 0.95f, floorY)       // top-right (narrow)
                lineTo(w, h)                     // bottom-right (wide)
                lineTo(0f, h)                    // bottom-left (wide)
                close()
            }
            drawPath(
                path = floorPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        roomColor.copy(alpha = 0.7f),
                        roomColor.copy(alpha = 1.0f)
                    ),
                    startY = floorY,
                    endY = h
                )
            )

            // ── Wooden plank lines on floor ──
            val plankAlpha = 0.15f
            val plankColor = Color.White.copy(alpha = plankAlpha)
            val plankCount = 7
            for (i in 1..plankCount) {
                val t = i.toFloat() / (plankCount + 1)
                val y = floorY + (h - floorY) * t
                val leftX = w * 0.05f * (1f - t) // perspective narrowing
                val rightX = w - leftX
                drawLine(
                    color = plankColor,
                    start = Offset(leftX, y),
                    end = Offset(rightX, y),
                    strokeWidth = 1.5f
                )
            }

            // ── Window on Back Wall ──
            val winW = w * 0.28f
            val winH = floorY * 0.4f
            val winX = w * 0.5f - winW * 0.5f
            val winY = floorY * 0.22f

            // Window sky gradient
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF4A90D9),
                        Color(0xFF87CEEB),
                        Color(0xFFB0E0F0)
                    )
                ),
                topLeft = Offset(winX, winY),
                size = Size(winW, winH)
            )

            // Window frame
            drawRect(
                color = Color(0xFF5C4033).copy(alpha = 0.8f),
                topLeft = Offset(winX - 4f, winY - 4f),
                size = Size(winW + 8f, winH + 8f),
                style = Stroke(width = 6f)
            )
            // Window cross
            drawLine(
                color = Color(0xFF5C4033).copy(alpha = 0.7f),
                start = Offset(winX + winW * 0.5f, winY),
                end = Offset(winX + winW * 0.5f, winY + winH),
                strokeWidth = 3f
            )
            drawLine(
                color = Color(0xFF5C4033).copy(alpha = 0.7f),
                start = Offset(winX, winY + winH * 0.5f),
                end = Offset(winX + winW, winY + winH * 0.5f),
                strokeWidth = 3f
            )

            // ── Tree silhouette outside window ──
            val treeTrunkColor = Color(0xFF2D1B0E).copy(alpha = 0.7f)
            val treeLeafColor = Color(0xFF1A3A1A).copy(alpha = 0.6f)
            val treeX = winX + winW * 0.7f
            val treeBaseY = winY + winH
            val trunkH = winH * 0.5f

            // Trunk
            drawRect(
                color = treeTrunkColor,
                topLeft = Offset(treeX - 3f, treeBaseY - trunkH),
                size = Size(6f, trunkH)
            )

            // Foliage (simple circles)
            val leafCx = treeX
            val leafCy = treeBaseY - trunkH - winH * 0.15f
            drawCircle(color = treeLeafColor, radius = winH * 0.18f, center = Offset(leafCx, leafCy))
            drawCircle(color = treeLeafColor, radius = winH * 0.13f, center = Offset(leafCx - winH * 0.12f, leafCy + winH * 0.05f))
            drawCircle(color = treeLeafColor, radius = winH * 0.13f, center = Offset(leafCx + winH * 0.12f, leafCy + winH * 0.05f))
            drawCircle(color = treeLeafColor, radius = winH * 0.1f, center = Offset(leafCx - winH * 0.06f, leafCy - winH * 0.1f))
            drawCircle(color = treeLeafColor, radius = winH * 0.1f, center = Offset(leafCx + winH * 0.06f, leafCy - winH * 0.1f))

            // ── Drop Shadows (on the floor) ──
            val shadowColor = Color.Black.copy(alpha = 0.25f)
            // Stickman shadow (left side)
            val stickmanX = w * 0.18f
            val shadowCY = floorY + (h - floorY) * 0.08f
            drawOval(
                color = shadowColor,
                topLeft = Offset(stickmanX - w * 0.08f, shadowCY - h * 0.015f),
                size = Size(w * 0.16f, h * 0.03f)
            )
            // Target shadow (right side)
            val targetX = w * 0.82f
            drawOval(
                color = shadowColor,
                topLeft = Offset(targetX - w * 0.08f, shadowCY - h * 0.015f),
                size = Size(w * 0.16f, h * 0.03f)
            )

            // ── Sneeze Projectile Animation ──
            if (sneezeAnim > 0f) {
                val sneezeColor = getEquippedSneezeColor(gameState)
                // Nose position (relative to stickman box on canvas)
                val nosePos = Offset(stickmanX + 20f, floorY - (h * 0.08f) - 30f)
                // Target center position
                val targetPos = Offset(targetX, floorY - (h * 0.08f))
                // Interpolated projectile position
                val projX = nosePos.x + (targetPos.x - nosePos.x) * sneezeAnim
                val projY = nosePos.y + (targetPos.y - nosePos.y) * sneezeAnim

                // Wind/shockwave trail — several circles of decreasing opacity
                val trailSteps = 5
                for (i in 0 until trailSteps) {
                    val t = (sneezeAnim - i * 0.03f).coerceIn(0f, sneezeAnim)
                    val tx = nosePos.x + (targetPos.x - nosePos.x) * t
                    val ty = nosePos.y + (targetPos.y - nosePos.y) * t
                    val alpha = (1f - i * 0.2f) * sneezeAnim * 0.6f
                    val radius = (12f - i * 2f).coerceAtLeast(2f)
                    drawCircle(
                        color = sneezeColor.copy(alpha = alpha.coerceIn(0f, 1f)),
                        radius = radius,
                        center = Offset(tx, ty)
                    )
                }
                // Main projectile core
                drawCircle(
                    color = Color.White.copy(alpha = sneezeAnim * 0.9f),
                    radius = 14f,
                    center = Offset(projX, projY)
                )
                drawCircle(
                    color = sneezeColor.copy(alpha = sneezeAnim * 0.6f),
                    radius = 20f,
                    center = Offset(projX, projY)
                )
            }
        }

        // ── 2. UI Overlay (with notch-safe padding) ───────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = (topInset + 4f).dp, bottom = (bottomInset + 4f).dp)
        ) {
            // ── Top Header Row: [← Menu] [Zone/Target Name] [☕] [🧻] ────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(bgColor.copy(alpha = 0.85f))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Menu button
                TextButton(
                    onClick = onBackToMenu,
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Text("← Menu", color = Gold, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }

                // Zone / Target name
                val itemName = item.name
                Text(
                    text = "Zone ${gameState.currentZone + 1} · $itemName",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                    maxLines = 1
                )

                // Currencies
                Text(
                    text = "☕ ${formatNumber(gameState.coffeeDrops)}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.padding(end = 6.dp)
                )
                Text(
                    text = "🧻 ${formatNumber(gameState.tissues)}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = StormPurpleLight,
                    modifier = Modifier.padding(end = 4.dp)
                )
            }

            // ── Stamina Bar (full width, beneath header) ──
            Box(modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)) {
                StaminaBar(
                    currentStamina = gameState.currentStamina,
                    maxStamina = gameState.maxStamina,
                    isRecharging = gameState.isRecharging == 1,
                    isFrenzyActive = gameState.isFrenzyActive
                )
            }

            // ── HP Bar (beneath stamina) ──
            Box(modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)) {
                ItemHpBar(
                    currentHp = gameState.itemHp,
                    maxHp = gameState.itemMaxHp,
                    isBoss = item.isBoss
                )
            }

            // ── Boss Phase Indicator ──
            if (item.isBoss && gameState.bossCurrentPhase >= 0) {
                Text(
                    text = "👑 Phase ${gameState.bossCurrentPhase + 1}/${item.phases.size}",
                    style = MaterialTheme.typography.labelSmall,
                    color = BossPurple,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 1.dp)
                )
            }

            // ── Boss Message ──
            if (bossMessage.isNotEmpty()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = BossRed.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = bossMessage,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFFF5252),
                        modifier = Modifier.padding(8.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // ── Frenzy Mode Indicator / Pollen Cloud ──
            if (gameState.isFrenzyActive) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = SneezeGreen.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "🌿 FRENZY MODE! ${gameState.frenzyTimeRemaining / 1000}s remaining",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = SneezeGreen,
                        modifier = Modifier.padding(8.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            if (pollenCloudVisible && !gameState.isFrenzyActive) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                        .clickable {
                            gameState.activateFrenzy(GameState.FRENZY_DURATION_MS)
                            pollenCloudVisible = false
                            pollenCloudTimer = 0L
                            soundEngine?.playFrenzy()
                        },
                    shape = RoundedCornerShape(12.dp),
                    color = SneezeGreen.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "🌿 Pollen Cloud! Tap to activate Frenzy!",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = SneezeGreen,
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // ── Main Game Area (center) ─────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                // Left: Stickman Babis
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 24.dp)
                        .size(130.dp)
                ) {
                    StickmanBabis(
                        modifier = Modifier.fillMaxSize(),
                        hatId = gameState.equippedHat,
                        glassesId = gameState.equippedGlasses,
                        sneezeAnim = sneezeAnim,
                        sneezeColor = getEquippedSneezeColor(gameState)
                    )

                    // Damage text overlay
                    if (damageTextAlpha > 0f) {
                        Text(
                            text = damageText,
                            color = CritGold,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .offset(y = (-10).dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Right: Target Item
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 24.dp)
                        .width(150.dp)
                ) {
                    // Large item emoji
                    Text(
                        text = item.emoji,
                        fontSize = 64.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Item name
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // ── Bottom Control Area ─────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(bgColor.copy(alpha = 0.85f))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // SNEEZE Button (full width)
                Button(
                    onClick = {
                        val result = gameState.sneeze()
                        when (result) {
                            SneezeResult.HIT -> {
                                soundEngine?.playRandomSneeze()
                                sneezeAnim = 1f
                            }
                            SneezeResult.CRIT -> {
                                soundEngine?.playCrit()
                                damageText = "CRIT! 💥"
                                damageTextAlpha = 1f
                                sneezeAnim = 1f
                            }
                            SneezeResult.DESTROYED -> {
                                soundEngine?.playDestroy()
                                handleItemDestroyed(gameState)
                                sneezeAnim = 1f
                            }
                            SneezeResult.NO_STAMINA -> {
                                soundEngine?.playNoStamina()
                                gameState.startRecharging()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (gameState.isFrenzyActive) SneezeGreen else Gold,
                        contentColor = DarkText
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                ) {
                    Text(
                        text = if (gameState.isFrenzyActive) "🌿 FRENZY!" else "👃 SNEEZE!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }

                // Item Navigation: [< prev item] [1/4] [next item >]
                val zoneItems = ItemDatabase.getZoneItems(gameState.currentZone)
                val totalItems = zoneItems.size
                val currentIndex = gameState.currentItem + 1
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            if (gameState.currentItem > 0) {
                                val prevItem = zoneItems[gameState.currentItem - 1]
                                gameState.setNewItem(gameState.currentItem - 1, prevItem.maxHp)
                                if (prevItem.isBoss) {
                                    gameState.initBossFight(prevItem.phases)
                                }
                            }
                        },
                        enabled = gameState.currentItem > 0
                    ) {
                        Text("< prev item", color = Gold.copy(alpha = 0.8f), fontSize = 12.sp)
                    }

                    Text(
                        text = "$currentIndex / $totalItems",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    TextButton(
                        onClick = {
                            if (gameState.currentItem < zoneItems.size - 1) {
                                val nextItem = zoneItems[gameState.currentItem + 1]
                                gameState.setNewItem(gameState.currentItem + 1, nextItem.maxHp)
                                if (nextItem.isBoss) {
                                    gameState.initBossFight(nextItem.phases)
                                }
                            }
                        },
                        enabled = gameState.currentItem < zoneItems.size - 1
                    ) {
                        Text("next item >", color = Gold.copy(alpha = 0.8f), fontSize = 12.sp)
                    }
                }
            }
        }

        // ── 3. Right Side Vertical Action Menu ─────────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 4.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Quick Upgrade
            FloatingActionButton(
                onClick = { showUpgradePanel = true },
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(12.dp),
                containerColor = GoldDark.copy(alpha = 0.85f),
                contentColor = DarkText,
                elevation = FloatingActionButtonDefaults.elevation(4.dp)
            ) {
                Text("⚡", fontSize = 18.sp)
            }

            // Achievements
            FloatingActionButton(
                onClick = { /* navigates to achievements — handled by parent screen */ },
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(12.dp),
                containerColor = StormPurple.copy(alpha = 0.85f),
                contentColor = White,
                elevation = FloatingActionButtonDefaults.elevation(4.dp)
            ) {
                Text("🏆", fontSize = 16.sp)
            }

            // Settings
            FloatingActionButton(
                onClick = { /* navigates to settings — handled by parent screen */ },
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(12.dp),
                containerColor = DeepBlueLight.copy(alpha = 0.85f),
                contentColor = White,
                elevation = FloatingActionButtonDefaults.elevation(4.dp)
            ) {
                Text("⚙️", fontSize = 16.sp)
            }

            // Shop
            FloatingActionButton(
                onClick = { /* navigates to shop — handled by parent screen */ },
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(12.dp),
                containerColor = Color(0xFF4CAF50).copy(alpha = 0.85f),
                contentColor = White,
                elevation = FloatingActionButtonDefaults.elevation(4.dp)
            ) {
                Text("🛒", fontSize = 16.sp)
            }
        }

        // ── 4. Achievement Popup ──────────────────────────────────────────
        if (achievementPopupVisible) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = (topInset + 60f).dp)
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = GoldDark.copy(alpha = 0.9f),
                    shadowElevation = 8.dp
                ) {
                    Text(
                        text = achievementPopup,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = White,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // ── 5. Upgrade Panel Overlay ──────────────────────────────────────
        UpgradePanel(
            gameState = gameState,
            visible = showUpgradePanel,
            onDismiss = { showUpgradePanel = false }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SettingsScreen
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Settings screen — sound controls, volume, reset progress.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    gameState: GameState,
    saveManager: SaveManager? = null,
    onBackToMenu: () -> Unit
) {
    var showResetConfirm by remember { mutableStateOf(false) }
    var savedMessage by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("⚙️ Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    TextButton(onClick = onBackToMenu) {
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
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Sound Section ───────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🔊 Sound",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Gold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Sound FX",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Slider(
                        value = gameState.soundVolume,
                        onValueChange = { gameState.soundVolume = it },
                        valueRange = 0f..1f,
                        colors = SliderDefaults.colors(
                            thumbColor = Gold,
                            activeTrackColor = Gold,
                            inactiveTrackColor = GoldDark.copy(alpha = 0.3f)
                        )
                    )
                    Text(
                        text = "${(gameState.soundVolume * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Music",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Slider(
                        value = gameState.soundVolume,
                        onValueChange = { gameState.soundVolume = it },
                        valueRange = 0f..1f,
                        colors = SliderDefaults.colors(
                            thumbColor = StormPurpleLight,
                            activeTrackColor = StormPurpleLight,
                            inactiveTrackColor = StormPurpleLight.copy(alpha = 0.3f)
                        )
                    )
                    Text(
                        text = "${(gameState.soundVolume * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            // ── Game Section ───────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🎮 Game",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Gold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Sound Effects toggle (placeholder)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Sound Effects",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Switch(
                            checked = gameState.soundEnabled == 1,
                            onCheckedChange = { gameState.soundEnabled = if (it) 1 else 0 },
                            colors = SwitchDefaults.colors(
                                checkedTrackColor = Gold,
                                checkedThumbColor = GoldLight,
                                uncheckedTrackColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                            )
                        )
                    }
                }
            }

            // ── Save/Load Section ──────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "💾 Save / Load",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Gold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (savedMessage.isNotEmpty()) {
                        Text(
                            text = savedMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                saveManager?.let {
                                    it.save(gameState)
                                    savedMessage = "✅ Game saved!"
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("💾 Save")
                        }

                        OutlinedButton(
                            onClick = {
                                saveManager?.let {
                                    val loaded = it.load(gameState)
                                    if (loaded) {
                                        savedMessage = "✅ Game loaded!"
                                    } else {
                                        savedMessage = "❌ No save found"
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("📂 Load")
                        }
                    }
                }
            }

            // ── Danger Zone ────────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2D1111)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "⚠️ Danger Zone",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = HealthRed
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "This will erase ALL progress. Cannot be undone.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (!showResetConfirm) {
                        Button(
                            onClick = { showResetConfirm = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = HealthRed.copy(alpha = 0.8f),
                                contentColor = White
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("🗑️ Reset Progress", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Text(
                            text = "Are you sure? This cannot be undone!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = HealthRed,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    gameState.restoreFromSave(GameSaveData())
                                    saveManager?.deleteSave()
                                    showResetConfirm = false
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = HealthRed,
                                    contentColor = White
                                ),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Yes, Reset", fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = { showResetConfirm = false },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Cancel")
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Version info
            Text(
                text = "v1.0.0 · God of Nose: Sneeze & Thunder",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// AchievementsScreen
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Achievement gallery — shows unlocked and locked achievements.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(
    gameState: GameState,
    onBackToMenu: () -> Unit
) {
    val achievements = remember { AchievementDatabase.getAll() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "🏆 Achievements",
                        fontWeight = FontWeight.Bold,
                        color = Gold
                    )
                },
                navigationIcon = {
                    TextButton(onClick = onBackToMenu) {
                        Text("← Back", color = Gold)
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
                .padding(12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Stats summary
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${Integer.bitCount(gameState.unlockedAchievements)}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Gold
                        )
                        Text(
                            text = "Unlocked",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${achievements.size}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Total",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            // Achievement list
            for (achievement in achievements) {
                val unlocked = gameState.isAchievementUnlocked(achievement.id)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (unlocked)
                            GoldDark.copy(alpha = 0.15f)
                        else
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (unlocked) achievement.icon else "🔒",
                            fontSize = 28.sp,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = achievement.title,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (unlocked) FontWeight.Bold else FontWeight.Normal,
                                color = if (unlocked) Gold else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                            Text(
                                text = if (unlocked) achievement.description else "???",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (unlocked) 0.7f else 0.3f)
                            )
                            val rewardParts = mutableListOf<String>()
                            if (achievement.coffeeReward > 0L) rewardParts.add("☕ ${achievement.coffeeReward}")
                            if (achievement.tissueReward > 0L) rewardParts.add("🧻 ${achievement.tissueReward}")
                            if (rewardParts.isNotEmpty()) {
                                Text(
                                    text = "🎁 " + rewardParts.joinToString(" + "),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF4CAF50),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                        if (unlocked) {
                            Text(
                                text = "✅",
                                fontSize = 20.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// CosmeticsShopScreen
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Cosmetics shop — hats, glasses, sneeze colors.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CosmeticsShopScreen(
    gameState: GameState,
    onBackToMenu: () -> Unit
) {
    val hats = remember { CosmeticsDatabase.getBySlot(com.godofnose.sneezeandthunder.game.CosmeticSlot.HAT) }
    val glasses = remember { CosmeticsDatabase.getBySlot(com.godofnose.sneezeandthunder.game.CosmeticSlot.GLASSES) }
    val sneezeColors = remember { CosmeticsDatabase.getBySlot(com.godofnose.sneezeandthunder.game.CosmeticSlot.SNEEZE_COLOR) }
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("🎩 Hats", "👓 Glasses", "✨ Colors")
    var feedbackMessage by remember { mutableStateOf("") }

    fun purchase(item: com.godofnose.sneezeandthunder.game.Cosmetic) {
        if (!gameState.hasCosmetic(item.id)) {
            val success = gameState.buyCosmetic(item.id, item.costInTissues)
            if (success) {
                feedbackMessage = "✅ Purchased!"
            } else {
                feedbackMessage = "❌ Purchase failed"
            }
        } else {
            // Already owned — equip (need slot from context)
            feedbackMessage = "✅ Already owned!"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🛒 Cosmetics Shop", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    TextButton(onClick = onBackToMenu) {
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
                .padding(12.dp)
        ) {
            // Feedback message
            if (feedbackMessage.isNotEmpty()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = if (feedbackMessage.startsWith("✅"))
                        Color(0xFF1B3D1B) else Color(0xFF3D1B1B)
                ) {
                    Text(
                        text = feedbackMessage,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (feedbackMessage.startsWith("✅")) Color(0xFF4CAF50) else HealthRed
                    )
                }
            }

            // Currency display
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "☕ ${formatNumber(gameState.coffeeDrops)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.padding(end = 12.dp)
                )
                Text(
                    text = "🧻 ${formatNumber(gameState.tissues)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = StormPurpleLight
                )
            }

            // Tab buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                tabs.forEachIndexed { index, title ->
                    FilterChip(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        label = { Text(title, fontSize = 13.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GoldDark,
                            selectedLabelColor = White
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Items
            val items = when (selectedTab) {
                0 -> hats
                1 -> glasses
                2 -> sneezeColors
                else -> hats
            }

            if (items.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No items available yet!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (cosmetic in items) {
                        val owned = gameState.hasCosmetic(cosmetic.id)
                        val equipped = when {
                            selectedTab == 0 && cosmetic.id == gameState.equippedHat -> true
                            selectedTab == 1 && cosmetic.id == gameState.equippedGlasses -> true
                            selectedTab == 2 && cosmetic.id == gameState.equippedSneezeColor -> true
                            else -> false
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (equipped)
                                    GoldDark.copy(alpha = 0.25f)
                                else
                                    MaterialTheme.colorScheme.surfaceVariant
                            ),
                            onClick = { purchase(cosmetic) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = cosmetic.emoji,
                                    fontSize = 28.sp,
                                    modifier = Modifier.padding(end = 12.dp)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = cosmetic.name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (cosmetic.name.isNotEmpty()) {
                                        Text(
                                            text = cosmetic.name,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                        )
                                    }
                                }
                                if (equipped) {
                                    Text(
                                        text = "✅ Equipped",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Gold,
                                        fontWeight = FontWeight.Bold
                                    )
                                } else if (owned) {
                                    Text(
                                        text = "Equip",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Gold,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                } else {
                                    Text(
                                        text = "☕ ${formatNumber(cosmetic.costInTissues)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF4CAF50)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// StickmanBabis — Canvas-drawn stickman
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Canvas-drawn Stickman Babis with cosmetic items and sneeze effect.
 *
 * @param modifier Modifier for size/positioning
 * @param hatId   Cosmetic hat ID (0 = none)
 * @param glassesId Cosmetic glasses ID (0 = none)
 * @param sneezeAnim 0f → 1f sneeze animation intensity
 * @param sneezeColor Color of the sneeze effect
 */
@Composable
fun StickmanBabis(
    modifier: Modifier = Modifier,
    hatId: Int = 0,
    glassesId: Int = 0,
    sneezeAnim: Float = 0f,
    sneezeColor: Color = Color(0xFFFFD700)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // ── Head (circle) ──
        val headRadius = w * 0.18f
        val headCenter = Offset(size.width / 2f, h * 0.25f)
        drawCircle(
            color = Color(0xFFFFE0BD),
            radius = headRadius,
            center = headCenter
        )

        // ── Eyes ──
        val eyeOffsetX = headRadius * 0.35f
        val eyeOffsetY = headRadius * 0.15f
        val eyeRadius = headRadius * 0.08f
        // Left eye
        drawCircle(Color.Black, eyeRadius, Offset(headCenter.x - eyeOffsetX, headCenter.y - eyeOffsetY))
        // Right eye
        drawCircle(Color.Black, eyeRadius, Offset(headCenter.x + eyeOffsetX, headCenter.y - eyeOffsetY))

        // ── Nose ──
        val noseEnd = Offset(headCenter.x, headCenter.y + headRadius * 0.2f)
        drawLine(
            color = Color(0xFFFFCC99),
            start = Offset(headCenter.x, headCenter.y + headRadius * 0.1f),
            end = noseEnd,
            strokeWidth = headRadius * 0.08f
        )

        // ── Mouth ──
        drawArc(
            color = Color(0xFF8B4513),
            startAngle = 0f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(headCenter.x - headRadius * 0.25f, headCenter.y + headRadius * 0.25f),
            size = Size(headRadius * 0.5f, headRadius * 0.15f),
            style = Stroke(width = headRadius * 0.05f)
        )

        // ── Body (line) ──
        val bodyTop = headCenter.y + headRadius * 0.8f
        val bodyBottom = h * 0.6f
        drawLine(
            color = Color(0xFFFFE0BD),
            start = Offset(headCenter.x, bodyTop),
            end = Offset(headCenter.x, bodyBottom),
            strokeWidth = w * 0.05f
        )

        // ── Arms ──
        val armY = bodyTop + (bodyBottom - bodyTop) * 0.3f
        // Left arm
        drawLine(
            color = Color(0xFFFFE0BD),
            start = Offset(headCenter.x - w * 0.05f, armY),
            end = Offset(headCenter.x - w * 0.2f, armY + w * 0.08f),
            strokeWidth = w * 0.04f
        )
        // Right arm
        drawLine(
            color = Color(0xFFFFE0BD),
            start = Offset(headCenter.x + w * 0.05f, armY),
            end = Offset(headCenter.x + w * 0.2f, armY + w * 0.08f),
            strokeWidth = w * 0.04f
        )

        // ── Legs ──
        drawLine(
            color = Color(0xFFFFE0BD),
            start = Offset(headCenter.x, bodyBottom),
            end = Offset(headCenter.x - w * 0.1f, h * 0.85f),
            strokeWidth = w * 0.04f
        )
        drawLine(
            color = Color(0xFFFFE0BD),
            start = Offset(headCenter.x, bodyBottom),
            end = Offset(headCenter.x + w * 0.1f, h * 0.85f),
            strokeWidth = w * 0.04f
        )

        // ── Hat (if equipped) ──
        if (hatId > 0) {
            drawHat(headCenter, headRadius, hatId)
        }

        // ── Glasses (if equipped) ──
        if (glassesId > 0) {
            drawGlasses(headCenter, headRadius, glassesId)
        }

        // ── Sneeze effect ──
        if (sneezeAnim > 0f) {
            drawSneezeEffect(
                center = noseEnd,
                radius = headRadius * 0.5f,
                color = sneezeColor,
                intensity = sneezeAnim
            )
        }
    }
}

/**
 * Draw a hat on Babis's head.
 */
private fun DrawScope.drawHat(center: Offset, radius: Float, hatId: Int) {
    when (hatId) {
        1 -> { // Party Hat
            val hatTop = Offset(center.x, center.y - radius * 1.4f)
            val hatBaseLeft = Offset(center.x - radius * 0.6f, center.y - radius * 0.7f)
            val hatBaseRight = Offset(center.x + radius * 0.6f, center.y - radius * 0.7f)
            drawLine(Color(0xFFFF4081), hatTop, hatBaseLeft, radius * 0.12f)
            drawLine(Color(0xFFFF4081), hatTop, hatBaseRight, radius * 0.12f)
            drawCircle(Color(0xFFFFEB3B), radius * 0.12f, hatTop)
        }
        2 -> { // Crown
            val crownY = center.y - radius * 1.1f
            // Simple triangle crown
            val p1 = Offset(center.x - radius * 0.5f, center.y - radius * 0.75f)
            val p2 = Offset(center.x, crownY)
            val p3 = Offset(center.x + radius * 0.5f, center.y - radius * 0.75f)
            drawLine(Color(0xFFFFD700), p1, p2, radius * 0.1f)
            drawLine(Color(0xFFFFD700), p2, p3, radius * 0.1f)
            drawLine(Color(0xFFFFD700), p1, Offset(p3.x, p1.y), radius * 0.1f)
            drawCircle(Color(0xFFFF1744), radius * 0.08f, p2)
        }
        3 -> { // Top Hat
            val hatTop = Offset(center.x, center.y - radius * 1.6f)
            val hatBottom = center.y - radius * 0.7f
            // Brim
            drawLine(Color(0xFF212121), Offset(center.x - radius * 0.7f, hatBottom), Offset(center.x + radius * 0.7f, hatBottom), radius * 0.12f)
            // Top part
            drawLine(Color(0xFF212121), Offset(center.x - radius * 0.35f, hatBottom), Offset(center.x - radius * 0.35f, hatTop.y + radius * 0.2f), radius * 0.12f)
            drawLine(Color(0xFF212121), Offset(center.x + radius * 0.35f, hatBottom), Offset(center.x + radius * 0.35f, hatTop.y + radius * 0.2f), radius * 0.12f)
            drawLine(Color(0xFF212121), Offset(center.x - radius * 0.35f, hatTop.y + radius * 0.2f), Offset(center.x + radius * 0.35f, hatTop.y + radius * 0.2f), radius * 0.12f)
        }
        4 -> { // Baseball Cap
            val capY = center.y - radius * 0.85f
            // Cap dome
            drawArc(
                color = Color(0xFF1565C0),
                startAngle = 0f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(center.x - radius * 0.5f, capY - radius * 0.3f),
                size = Size(radius, radius * 0.3f),
                style = Stroke(width = radius * 0.15f)
            )
            // Brim
            drawLine(Color(0xFF1565C0), Offset(center.x - radius * 0.5f, capY), Offset(center.x + radius * 0.6f, capY + radius * 0.1f), radius * 0.1f)
        }
    }
}

/**
 * Draw glasses on Babis.
 */
private fun DrawScope.drawGlasses(center: Offset, radius: Float, glassesId: Int) {
    val eyeY = center.y - radius * 0.1f
    val leftEye = Offset(center.x - radius * 0.3f, eyeY)
    val rightEye = Offset(center.x + radius * 0.3f, eyeY)
    val glassSize = radius * 0.3f

    when (glassesId) {
        5 -> { // Sunglasses
            drawRect(Color(0xFF212121), topLeft = Offset(leftEye.x - glassSize * 0.5f, leftEye.y - glassSize * 0.3f), size = Size(glassSize, glassSize * 0.6f))
            drawRect(Color(0xFF212121), topLeft = Offset(rightEye.x - glassSize * 0.5f, rightEye.y - glassSize * 0.3f), size = Size(glassSize, glassSize * 0.6f))
            drawLine(Color(0xFF212121), Offset(leftEye.x + glassSize * 0.5f, eyeY), Offset(rightEye.x - glassSize * 0.5f, eyeY), radius * 0.04f)
        }
        6 -> { // Nerd Glasses
            drawCircle(Color(0xFF424242), radius * 0.22f, leftEye, style = Stroke(radius * 0.06f))
            drawCircle(Color(0xFF424242), radius * 0.22f, rightEye, style = Stroke(radius * 0.06f))
            drawLine(Color(0xFF424242), Offset(leftEye.x + radius * 0.2f, eyeY), Offset(rightEye.x - radius * 0.2f, eyeY), radius * 0.04f)
        }
        7 -> { // Star Glasses
            drawCircle(Color(0xFFFFD700), radius * 0.22f, leftEye, style = Stroke(radius * 0.06f))
            drawCircle(Color(0xFFFFD700), radius * 0.22f, rightEye, style = Stroke(radius * 0.06f))
            drawLine(Color(0xFFFFD700), Offset(leftEye.x + radius * 0.2f, eyeY), Offset(rightEye.x - radius * 0.2f, eyeY), radius * 0.04f)
            // Stars
            drawCircle(Color(0xFFFFD700), radius * 0.05f, Offset(leftEye.x - radius * 0.1f, leftEye.y - radius * 0.15f))
            drawCircle(Color(0xFFFFD700), radius * 0.05f, Offset(rightEye.x + radius * 0.1f, rightEye.y - radius * 0.15f))
        }
    }
}

/**
 * Draw sneeze particles coming from Babis's nose.
 */
private fun DrawScope.drawSneezeEffect(
    center: Offset,
    radius: Float,
    color: Color,
    intensity: Float
) {
    val particleCount = (intensity * 8).toInt().coerceAtLeast(3)
    for (i in 0 until particleCount) {
        val angle = Math.toRadians((i * 45 + 10).toDouble()).toFloat()
        val distance = radius * (0.3f + intensity * 0.5f)
        val px = center.x + kotlin.math.cos(angle) * distance
        val py = center.y - kotlin.math.sin(angle) * distance
        val particleRadius = radius * 0.1f * (1f - intensity * 0.3f)
        drawCircle(
            color = color.copy(alpha = intensity * 0.8f),
            radius = particleRadius.coerceAtLeast(1f),
            center = Offset(px, py)
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Shared Components: Utility Composables
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Stamina bar with color transitions based on state.
 */
@Composable
private fun StaminaBar(
    currentStamina: Float,
    maxStamina: Float,
    isRecharging: Boolean,
    isFrenzyActive: Boolean
) {
    val fraction = if (maxStamina > 0f) (currentStamina / maxStamina).coerceIn(0f, 1f) else 0f

    val barColor by animateColorAsState(
        targetValue = when {
            isFrenzyActive -> SneezeGreen
            isRecharging -> Color(0xFFFF9800)
            fraction < 0.25f -> HealthRed
            fraction < 0.5f -> HealthYellow
            else -> StaminaBlue
        },
        animationSpec = tween(durationMillis = 300),
        label = "staminaColor"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (isFrenzyActive) "🌿 FRENZY" else "⚡ Stamina",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = barColor
            )
            Text(
                text = if (isFrenzyActive) "∞" else "${currentStamina.toInt()} / ${maxStamina.toInt()}",
                style = MaterialTheme.typography.labelSmall,
                color = barColor
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .background(
                    color = Color.Black.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(6.dp)
                )
        ) {
            val animFraction by animateFloatAsState(
                targetValue = fraction,
                animationSpec = tween(durationMillis = 200),
                label = "staminaFraction"
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animFraction)
                    .background(
                        color = barColor,
                        shape = RoundedCornerShape(6.dp)
                    )
            )
        }
    }
}

/**
 * Item HP bar — shows remaining health of the current target.
 */
@Composable
private fun ItemHpBar(
    currentHp: Float,
    maxHp: Float,
    isBoss: Boolean
) {
    val fraction = if (maxHp > 0f) (currentHp / maxHp).coerceIn(0f, 1f) else 0f

    val barColor by animateColorAsState(
        targetValue = when {
            fraction < 0.25f -> HealthRed
            fraction < 0.5f -> HealthYellow
            else -> if (isBoss) BossPurple else HealthGreen
        },
        animationSpec = tween(durationMillis = 300),
        label = "hpColor"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .background(
                    color = Color.Black.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            val animFraction by animateFloatAsState(
                targetValue = fraction,
                animationSpec = tween(durationMillis = 200),
                label = "hpFraction"
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animFraction)
                    .background(
                        color = barColor,
                        shape = RoundedCornerShape(8.dp)
                    )
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "${currentHp.toInt()} / ${maxHp.toInt()}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Helper Functions
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Handle item destroyed: collect reward + set up next item.
 */
private fun handleItemDestroyed(gameState: GameState) {
    val item = ItemDatabase.getItem(gameState.currentZone, gameState.currentItem)

    // Collect reward
    gameState.collectReward(item.coffeeReward, item.tissueReward)

    // If boss, mark as defeated
    if (item.isBoss) {
        gameState.addBossDefeated()
        gameState.endBossFight()
    }

    // Advance to next item
    val zoneItems = ItemDatabase.getZoneItems(gameState.currentZone)
    val nextIndex = gameState.currentItem + 1

    if (nextIndex >= zoneItems.size) {
        // Zone complete — advance to next zone
        gameState.advanceZone()
        val newZoneItems = ItemDatabase.getZoneItems(gameState.currentZone)
        val nextItem = newZoneItems[0]
        gameState.setNewItem(0, nextItem.maxHp)
    } else {
        val nextItem = zoneItems[nextIndex]
        gameState.setNewItem(nextIndex, nextItem.maxHp)
    }

    // If next item is a boss, initialize boss fight
    val newItem = ItemDatabase.getItem(gameState.currentZone, gameState.currentItem)
    if (newItem.isBoss) {
        gameState.initBossFight(newItem.phases)
    }
}

/**
 * Get the equipped sneeze color from cosmetics.
 */
private fun getEquippedSneezeColor(gameState: GameState): Color {
    if (gameState.equippedSneezeColor > 0) {
        val cosmetic = CosmeticsDatabase.getById(gameState.equippedSneezeColor)
        return cosmetic?.sneezeColor ?: Color(0xFFFFD700)
    }
    return Color(0xFFFFD700) // Default gold
}

/**
 * Format large numbers with commas and abbreviations.
 */
private fun formatNumber(value: Long): String {
    if (value < 1000) return "$value"
    if (value < 1_000_000) return "%.1fK".format(value / 1000.0)
    if (value < 1_000_000_000) return "%.2fM".format(value / 1_000_000.0)
    return "%.2fB".format(value / 1_000_000_000.0)
}
