package com.godofnose.sneezeandthunder.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import com.godofnose.sneezeandthunder.engine.GameSaveData

/**
 * Core game state for "God of Nose: Sneeze and Thunder".
 *
 * Manages the Stickman (Babis), his stamina, the current target item,
 * currencies, upgrades, frenzy mode, and cosmetics.
 */
class GameState {

    // ── Currencies ────────────────────────────────────────────────────────────

    /** Coffee Drops — common currency earned by destroying items. */
    var coffeeDrops by mutableLongStateOf(0L)
        private set

    /** Tissues — rare/premium currency earned from Bosses. */
    var tissues by mutableLongStateOf(0L)
        private set

    // ── Stamina ───────────────────────────────────────────────────────────────

    var currentStamina by mutableFloatStateOf(MAX_STAMINA)
        private set
    var maxStamina by mutableFloatStateOf(MAX_STAMINA)
        private set

    /** 0=ready, 1=recharging */
    var isRecharging by mutableIntStateOf(0)
        private set

    // ── Current Target Item ───────────────────────────────────────────────────

    var currentItem by mutableIntStateOf(0)
        private set
    var itemHp by mutableFloatStateOf(0f)
        private set
    var itemMaxHp by mutableFloatStateOf(0f)
        private set
    var isItemDestroyed by mutableIntStateOf(0) // 0=alive, 1=destroyed
        private set

    // ── Player Stats ──────────────────────────────────────────────────────────

    var sneezePower by mutableFloatStateOf(1f)
        private set
    var critChance by mutableFloatStateOf(0.05f)
        private set
    var totalSneezes by mutableLongStateOf(0L)
        private set
    var currentZone by mutableIntStateOf(0)
        private set

    // ── Upgrade Levels ────────────────────────────────────────────────────────

    var sneezePowerLevel by mutableIntStateOf(0)
        private set
    var maxStaminaLevel by mutableIntStateOf(0)
        private set
    var critChanceLevel by mutableIntStateOf(0)
        private set

    // ── Frenzy Mode (Pollen Cloud) ────────────────────────────────────────────

    /** Remaining frenzy time in milliseconds. 0 = not in frenzy. */
    var frenzyTimeRemaining by mutableLongStateOf(0L)
        private set

    /** Whether frenzy is currently active. */
    val isFrenzyActive: Boolean get() = frenzyTimeRemaining > 0L

    // ── Cosmetics ─────────────────────────────────────────────────────────────

    /** Bitmask of owned cosmetic IDs. */
    var ownedCosmetics by mutableIntStateOf(0)
        private set

    /** Currently equipped cosmetic ID (0 = none). */
    var equippedHat by mutableIntStateOf(0)
        private set
    var equippedGlasses by mutableIntStateOf(0)
        private set
    var equippedSneezeColor by mutableIntStateOf(0)
        private set

    // ── Settings ──────────────────────────────────────────────────────────────

    /** Whether sound effects are enabled. 1=enabled, 0=disabled. */
    var soundEnabled by mutableIntStateOf(1)

    /** Sound volume (0.0 – 1.0). */
    var soundVolume by mutableFloatStateOf(1f)

    // ── Achievement Tracking ──────────────────────────────────────────────────

    /** Bitmask of unlocked achievement IDs. */
    var unlockedAchievements by mutableIntStateOf(0)
        private set

    /** Total items destroyed across all zones. */
    var totalItemsDestroyed by mutableLongStateOf(0L)
        private set

    /** Total critical hits landed. */
    var totalCrits by mutableLongStateOf(0L)
        private set

    /** Total frenzy activations. */
    var frenzyActivations by mutableIntStateOf(0)
        private set

    /** Total upgrades purchased. */
    var totalUpgradesBought by mutableIntStateOf(0)
        private set

    /** Total times stamina was exhausted. */
    var staminaExhaustions by mutableIntStateOf(0)
        private set

    /** Total bosses defeated. */
    var bossesDefeated by mutableIntStateOf(0)
        private set

    /** Total Coffee Drops earned across all time (for statistics). */
    var totalCoffeeEarned by mutableLongStateOf(0L)
        private set

    /** Total Tissues earned across all time (for statistics). */
    var totalTissuesEarned by mutableLongStateOf(0L)
        private set

    /** Total play time in seconds (tracked while app is open). */
    var playTimeSeconds by mutableLongStateOf(0L)
        private set

    /** Whether the tutorial has been completed. */
    var tutorialCompleted by mutableIntStateOf(0)
        private set

    // ── Prestige / New Game+ ───────────────────────────────────────────────────

    /** Number of times the player has prestiged. */
    var prestigeLevel by mutableIntStateOf(0)
        private set

    /** Permanent damage multiplier from prestige (e.g. 1.0 + 0.25 * prestigeLevel). */
    var prestigeMultiplier by mutableFloatStateOf(1f)
        private set

    // ── Daily Rewards ──────────────────────────────────────────────────────────

    /** Day-of-year of the last daily reward claim (0 = never). */
    var lastRewardDay by mutableIntStateOf(0)
        private set

    /** Current daily reward streak. */
    var dailyStreak by mutableIntStateOf(0)
        private set

    /** Index of the next reward in the daily reward cycle (0-based, 7-day cycle). */
    var dailyRewardIndex by mutableIntStateOf(0)
        private set

    // ── Auto-Sneeze ────────────────────────────────────────────────────────────

    /** Auto-sneeze upgrade level (0 = disabled). */
    var autoSneezeLevel by mutableIntStateOf(0)
        private set

    /** Interval in ms between auto-sneezes (decreases with level). */
    var autoSneezeInterval by mutableLongStateOf(0L)
        private set

    /** Sneezes per zone (index 0-4). */
    val sneezesPerZone: MutableList<Long> = mutableListOf(0L, 0L, 0L, 0L, 0L)

    // ── Offline Progress ───────────────────────────────────────────────────────

    /** Timestamp (System.currentTimeMillis()) of the last save. */
    var lastSaveTimestamp by mutableLongStateOf(System.currentTimeMillis())
        private set

    /** Offline earnings to display when returning to the game. */
    var offlineCoffeeEarned by mutableLongStateOf(0L)
        private set
    var offlineTissuesEarned by mutableLongStateOf(0L)
        private set
    var offlineSeconds by mutableLongStateOf(0L)
        private set

    // ── Boss Battle ───────────────────────────────────────────────────────────

    /** Current boss phase index (0 = first phase). -1 if not in a boss fight. */
    var bossCurrentPhase by mutableIntStateOf(-1)
        private set

    /** Time until next boss attack (ms). */
    var bossAttackTimer by mutableLongStateOf(0L)
        private set

    /** Current boss attack description to display. */
    var bossAttackDescription by mutableIntStateOf(0)
        private set

    /** Whether the boss is currently shielded (reduces damage). */
    var isBossShielded by mutableIntStateOf(0)
        private set

    /** Shield strength multiplier (0.0–1.0, lower = more reduction). */
    var bossShieldStrength by mutableFloatStateOf(1f)
        private set

    /** Whether the boss is currently reflecting damage. */
    var isBossReflecting by mutableIntStateOf(0)
        private set

    /** Boss heal amount per tick. */
    var bossHealAmount by mutableFloatStateOf(0f)
        private set

    /** Boss stamina drain per attack. */
    var bossStaminaDrain by mutableFloatStateOf(0f)
        private set

    /** List of achievement IDs unlocked this session (for popup notifications). */
    val newlyUnlocked: MutableList<Int> = mutableListOf()

    // ── Actions ───────────────────────────────────────────────────────────────

    /** Perform one sneeze attack on the current item. */
    fun sneeze(): SneezeResult {
        if (isRecharging == 1 && !isFrenzyActive) {
            return SneezeResult.NO_STAMINA
        }

        // In frenzy mode, no stamina cost
        if (!isFrenzyActive) {
            if (currentStamina < STAMINA_COST_PER_SNEEZE) {
                staminaExhaustions++
                return SneezeResult.NO_STAMINA
            }
            currentStamina -= STAMINA_COST_PER_SNEEZE
        }

        totalSneezes++
        // Track sneezes per zone
        if (currentZone in 0..4) {
            sneezesPerZone[currentZone] = sneezesPerZone[currentZone] + 1L
        }

        val isCrit = Math.random() < critChance
        val rawDamage = if (isCrit) sneezePower * 2f else sneezePower
        // Apply prestige multiplier
        val prestigeDamage = rawDamage * prestigeMultiplier
        val damage = calculateBossDamage(prestigeDamage)

        itemHp = (itemHp - damage).coerceAtLeast(0f)

        if (isCrit) {
            totalCrits++
        }

        if (itemHp <= 0f) {
            isItemDestroyed = 1
            totalItemsDestroyed++
            return SneezeResult.DESTROYED
        }

        return if (isCrit) SneezeResult.CRIT else SneezeResult.HIT
    }

    /** Collect rewards from a destroyed item. */
    fun collectReward(rewardCoffee: Long, rewardTissues: Long = 0L) {
        coffeeDrops += rewardCoffee
        tissues += rewardTissues
        totalCoffeeEarned += rewardCoffee
        totalTissuesEarned += rewardTissues
        isItemDestroyed = 0
    }

    /** Start a new item with given max HP. */
    fun setNewItem(itemIndex: Int, maxHp: Float) {
        currentItem = itemIndex
        itemMaxHp = maxHp
        itemHp = maxHp
        isItemDestroyed = 0
    }

    /** Recharge stamina by [amount] per tick. */
    fun rechargeStamina(amount: Float) {
        if (currentStamina < maxStamina) {
            currentStamina = (currentStamina + amount).coerceAtMost(maxStamina)
            if (currentStamina >= maxStamina) {
                isRecharging = 0
            }
        }
    }

    fun startRecharging() {
        isRecharging = 1
    }

    // ── Frenzy Mode ───────────────────────────────────────────────────────────

    /** Activate frenzy mode for [durationMs] milliseconds. */
    fun activateFrenzy(durationMs: Long) {
        frenzyTimeRemaining = durationMs
        frenzyActivations++
    }

    /** Tick down frenzy timer. Call every frame/100ms. */
    fun tickFrenzy(deltaMs: Long) {
        if (frenzyTimeRemaining > 0L) {
            frenzyTimeRemaining = (frenzyTimeRemaining - deltaMs).coerceAtLeast(0L)
        }
    }

    // ── Upgrades ──────────────────────────────────────────────────────────────

    fun getSneezePowerCost(): Long = (50L * Math.pow(1.5, sneezePowerLevel.toDouble())).toLong()
    fun getMaxStaminaCost(): Long = (75L * Math.pow(1.5, maxStaminaLevel.toDouble())).toLong()
    fun getCritChanceCost(): Long = (100L * Math.pow(1.5, critChanceLevel.toDouble())).toLong()

    fun upgradeSneezePower(): Boolean {
        val cost = getSneezePowerCost()
        if (coffeeDrops < cost) return false
        coffeeDrops -= cost
        sneezePower += 0.5f
        sneezePowerLevel++
        totalUpgradesBought++
        return true
    }

    fun upgradeMaxStamina(): Boolean {
        val cost = getMaxStaminaCost()
        if (coffeeDrops < cost) return false
        coffeeDrops -= cost
        maxStamina += 10f
        currentStamina = maxStamina
        maxStaminaLevel++
        totalUpgradesBought++
        return true
    }

    fun upgradeCritChance(): Boolean {
        val cost = getCritChanceCost()
        if (coffeeDrops < cost) return false
        coffeeDrops -= cost
        critChance = (critChance + 0.03f).coerceAtMost(0.5f)
        critChanceLevel++
        totalUpgradesBought++
        return true
    }

    // ── Cosmetics ─────────────────────────────────────────────────────────────

    /** Check if a cosmetic is owned. */
    fun hasCosmetic(id: Int): Boolean = (ownedCosmetics and (1 shl id)) != 0

    /** Buy a cosmetic. Returns true if successful. */
    fun buyCosmetic(id: Int, cost: Long): Boolean {
        if (hasCosmetic(id)) return false
        if (tissues < cost) return false
        tissues -= cost
        ownedCosmetics = ownedCosmetics or (1 shl id)
        return true
    }

    /** Equip a cosmetic by slot type. */
    fun equipCosmetic(id: Int, slot: CosmeticSlot) {
        when (slot) {
            CosmeticSlot.HAT -> equippedHat = id
            CosmeticSlot.GLASSES -> equippedGlasses = id
            CosmeticSlot.SNEEZE_COLOR -> equippedSneezeColor = id
        }
    }

    // ── Zone Progression ──────────────────────────────────────────────────────

    fun advanceZone() {
        currentZone++
    }

    /** Increment play time by [seconds]. */
    fun addPlayTime(seconds: Long) {
        playTimeSeconds += seconds
    }

    /** Mark tutorial as completed. */
    fun completeTutorial() {
        tutorialCompleted = 1
    }

    /** Increment bosses defeated counter. */
    fun addBossDefeated() {
        bossesDefeated++
    }

    // ── Prestige / New Game+ ───────────────────────────────────────────────────

    /** Check if the player can prestige (must have finished all 5 zones). */
    fun canPrestige(): Boolean = currentZone >= 5

    /**
     * Perform a prestige reset, granting permanent bonuses.
     * Resets most progress but keeps achievements, cosmetics, and prestige level.
     */
    fun performPrestige(): Boolean {
        if (!canPrestige()) return false

        prestigeLevel++
        prestigeMultiplier = 1f + prestigeLevel * 0.25f

        // Reset everything except achievements, cosmetics, tutorial, prestige
        coffeeDrops = 0L
        tissues = 0L
        totalCoffeeEarned = 0L
        totalTissuesEarned = 0L

        sneezePower = 1f
        maxStamina = MAX_STAMINA
        critChance = 0.05f
        sneezePowerLevel = 0
        maxStaminaLevel = 0
        critChanceLevel = 0
        totalUpgradesBought = 0
        autoSneezeLevel = 0
        autoSneezeInterval = 0L

        currentZone = 0
        currentItem = 0
        itemHp = 0f
        itemMaxHp = 0f
        isItemDestroyed = 0
        currentStamina = MAX_STAMINA
        isRecharging = 0

        totalSneezes = 0L
        totalItemsDestroyed = 0L
        totalCrits = 0L
        frenzyActivations = 0
        staminaExhaustions = 0
        bossesDefeated = 0
        sneezesPerZone[0] = 0L
        sneezesPerZone[1] = 0L
        sneezesPerZone[2] = 0L
        sneezesPerZone[3] = 0L
        sneezesPerZone[4] = 0L

        endBossFight()
        return true
    }

    /** Get the prestige damage bonus description. */
    fun getPrestigeBonusDescription(): String =
        "×${"%.2f".format(prestigeMultiplier)} (Lv.${prestigeLevel})"

    // ── Daily Rewards ──────────────────────────────────────────────────────────

    /**
     * Check if a daily reward is available (new day since last claim).
     * Returns reward index (0-6) if available, -1 if already claimed.
     */
    fun getAvailableDailyReward(): Int {
        val today = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR)
        return if (today != lastRewardDay) dailyRewardIndex else -1
    }

    /** Claim today's daily reward. Returns the reward, or null if unavailable. */
    fun claimDailyReward(): DailyReward? {
        val today = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR)
        if (today == lastRewardDay) return null
        val reward = DAILY_REWARDS[dailyRewardIndex]

        // Streak logic
        val dayDiff = (today - lastRewardDay + 365) % 365
        dailyStreak = if (dayDiff == 1 || lastRewardDay == 0) dailyStreak + 1 else 1

        coffeeDrops += reward.coffeeAmount
        if (reward.tissueAmount > 0L) tissues += reward.tissueAmount
        totalCoffeeEarned += reward.coffeeAmount
        totalTissuesEarned += reward.tissueAmount

        lastRewardDay = today
        dailyRewardIndex = (dailyRewardIndex + 1) % DAILY_REWARDS.size
        return reward
    }

    // ── Auto-Sneeze Upgrade ────────────────────────────────────────────────────

    fun getAutoSneezeCost(): Long =
        (200L * Math.pow(1.6, autoSneezeLevel.toDouble())).toLong()

    /** Upgrade auto-sneeze. Returns true if successful. */
    fun upgradeAutoSneeze(): Boolean {
        val cost = getAutoSneezeCost()
        if (coffeeDrops < cost) return false
        coffeeDrops -= cost
        autoSneezeLevel++
        // Interval: level 1 = 3000ms, level 2 = 2000ms, level 3 = 1200ms, level 4+ = 800ms
        autoSneezeInterval = when (autoSneezeLevel) {
            0 -> 0L
            1 -> 3000L
            2 -> 2000L
            3 -> 1200L
            else -> 800L
        }
        totalUpgradesBought++
        return true
    }

    // ── Achievement Check ─────────────────────────────────────────────────────

    /**
     * Check all achievement conditions and unlock any newly earned ones.
     * Returns a list of newly unlocked achievement IDs.
     */
    fun checkAchievements(): List<Int> {
        val newlyUnlockedNow = mutableListOf<Int>()

        // Helper: check and unlock a single achievement
        fun check(id: Int, condition: () -> Boolean) {
            if (isAchievementUnlocked(id)) return
            if (condition()) {
                unlockAchievement(id)
                newlyUnlockedNow.add(id)
            }
        }

        // ── Sneezes ──
        check(0) { totalSneezes >= 10 }
        check(1) { totalSneezes >= 100 }
        check(2) { totalSneezes >= 1_000 }
        check(3) { totalSneezes >= 10_000 }
        check(4) { totalSneezes >= 100_000 }

        // ── Items Destroyed ──
        check(5) { totalItemsDestroyed >= 1 }
        check(6) { totalItemsDestroyed >= 50 }
        check(7) { totalItemsDestroyed >= 500 }
        check(8) { totalItemsDestroyed >= 5_000 }

        // ── Coffee Drops ──
        check(9) { coffeeDrops >= 1_000 }
        check(10) { coffeeDrops >= 100_000 }
        check(11) { coffeeDrops >= 1_000_000 }

        // ── Zones ──
        check(12) { currentZone >= 1 }
        check(13) { currentZone >= 2 }
        check(14) { currentZone >= 3 }
        check(15) { currentZone >= 4 }
        check(16) { currentZone >= 5 }

        // ── Crits ──
        check(17) { totalCrits >= 10 }
        check(18) { totalCrits >= 500 }
        check(19) { totalCrits >= 5_000 }

        // ── Frenzy ──
        check(20) { frenzyActivations >= 5 }
        check(21) { frenzyActivations >= 50 }

        // ── Cosmetics ──
        check(22) { ownedCosmetics != 0 }
        check(23) { Integer.bitCount(ownedCosmetics) >= 5 }

        // ── Bosses ──
        check(24) { bossesDefeated >= 1 }
        check(25) { bossesDefeated >= 10 }
        check(26) { bossesDefeated >= 50 }

        // ── Upgrades ──
        check(27) { totalUpgradesBought >= 1 }
        check(28) { sneezePowerLevel >= 10 && maxStaminaLevel >= 10 && critChanceLevel >= 10 }

        // ── Stamina ──
        check(29) { staminaExhaustions >= 100 }
        check(30) { staminaExhaustions >= 1_000 }

        // Add to session list for popup notifications
        newlyUnlocked.addAll(newlyUnlockedNow)

        return newlyUnlockedNow
    }

    /** Check if an achievement is already unlocked. */
    fun isAchievementUnlocked(id: Int): Boolean =
        (unlockedAchievements and (1 shl id)) != 0

    /** Unlock an achievement and grant its rewards. */
    private fun unlockAchievement(id: Int) {
        val achievement = AchievementDatabase.getById(id) ?: return
        unlockedAchievements = unlockedAchievements or (1 shl id)
        coffeeDrops += achievement.coffeeReward
        tissues += achievement.tissueReward
    }

    // ── Offline Progress ──────────────────────────────────────────────────────

    /**
     * Calculate offline earnings based on time elapsed since last save.
     * Call this when the app resumes (after loading save data).
     *
     * Formula:
     * - Coffee: sneezePower * 0.5 per second (passive sneezing)
     * - Tissues: 1 per 60 seconds (passive tissue generation)
     * - Stamina fully restored
     * - Max offline time: 8 hours (28,800 seconds)
     */
    fun calculateOfflineEarnings() {
        val now = System.currentTimeMillis()
        val elapsedMs = (now - lastSaveTimestamp).coerceAtLeast(0L)
        val maxOfflineMs = 28_800_000L // 8 hours
        val cappedMs = elapsedMs.coerceAtMost(maxOfflineMs)
        val seconds = cappedMs / 1000L

        if (seconds < 10L) {
            // Less than 10 seconds offline — no earnings
            offlineCoffeeEarned = 0L
            offlineTissuesEarned = 0L
            offlineSeconds = 0L
            return
        }

        // Coffee: sneezePower * 0.5 per second (scales with upgrades)
        val coffeePerSecond = (sneezePower * 0.5f).toLong().coerceAtLeast(1L)
        val coffeeEarned = coffeePerSecond * seconds

        // Tissues: 1 per 60 seconds
        val tissuesEarned = seconds / 60L

        // Apply earnings
        coffeeDrops += coffeeEarned
        tissues += tissuesEarned

        // Store for display
        offlineCoffeeEarned = coffeeEarned
        offlineTissuesEarned = tissuesEarned
        offlineSeconds = seconds

        // Fully restore stamina
        currentStamina = maxStamina
        isRecharging = 0
    }

    /** Update the last save timestamp to now. Call before saving. */
    fun updateLastSaveTimestamp() {
        lastSaveTimestamp = System.currentTimeMillis()
    }

    /** Clear offline earnings display values. */
    fun clearOfflineEarnings() {
        offlineCoffeeEarned = 0L
        offlineTissuesEarned = 0L
        offlineSeconds = 0L
    }

    // ── Boss Battle ───────────────────────────────────────────────────────────

    /**
     * Initialize a boss fight. Called when a boss item is first encountered.
     * Sets up the first phase and attack timer.
     */
    fun initBossFight(phases: List<BossPhase>) {
        if (phases.isEmpty()) {
            bossCurrentPhase = -1
            return
        }
        bossCurrentPhase = 0
        applyBossPhase(phases[0])
        bossAttackTimer = phases[0].attackIntervalMs
    }

    /**
     * Apply the effects of a boss phase.
     */
    private fun applyBossPhase(phase: BossPhase) {
        bossAttackDescription = phase.phaseIndex
        when (phase.attackPattern) {
            BossAttackPattern.STAMINA_DRAIN -> {
                isBossShielded = 0
                isBossReflecting = 0
                bossHealAmount = 0f
                bossStaminaDrain = 3f + phase.phaseIndex * 2f
                bossShieldStrength = 1f
            }
            BossAttackPattern.DAMAGE_REFLECT -> {
                isBossShielded = 0
                isBossReflecting = 1
                bossHealAmount = 0f
                bossStaminaDrain = 0f
                bossShieldStrength = 1f
            }
            BossAttackPattern.SELF_HEAL -> {
                isBossShielded = 0
                isBossReflecting = 0
                bossHealAmount = 1f + phase.phaseIndex * 0.5f
                bossStaminaDrain = 0f
                bossShieldStrength = 1f
            }
            BossAttackPattern.ENRAGE -> {
                isBossShielded = 0
                isBossReflecting = 0
                bossHealAmount = 0f
                bossStaminaDrain = 5f + phase.phaseIndex * 3f
                bossShieldStrength = 1f
            }
            BossAttackPattern.SHIELD -> {
                isBossShielded = 1
                isBossReflecting = 0
                bossHealAmount = 0f
                bossStaminaDrain = 0f
                bossShieldStrength = (0.7f - phase.phaseIndex * 0.1f).coerceAtLeast(0.3f)
            }
        }
    }

    /**
     * Tick the boss attack timer. Call every 100ms during a boss fight.
     * Returns the boss attack description if the boss attacks this tick, null otherwise.
     */
    fun tickBossAttack(deltaMs: Long, phases: List<BossPhase>): String? {
        if (bossCurrentPhase < 0 || phases.isEmpty()) return null

        val currentPhase = phases.getOrNull(bossCurrentPhase) ?: return null
        bossAttackTimer -= deltaMs

        if (bossAttackTimer <= 0L) {
            // Boss attacks!
            bossAttackTimer = currentPhase.attackIntervalMs

            when (currentPhase.attackPattern) {
                BossAttackPattern.STAMINA_DRAIN -> {
                    val drain = bossStaminaDrain
                    currentStamina = (currentStamina - drain).coerceAtLeast(0f)
                    if (currentStamina <= 0f) {
                        isRecharging = 1
                    }
                    return "💧 Boss drained $drain stamina!"
                }
                BossAttackPattern.DAMAGE_REFLECT -> {
                    // Reflect: deal 1 stamina damage per sneeze power
                    val reflectDmg = (sneezePower * 0.5f).coerceAtLeast(1f)
                    currentStamina = (currentStamina - reflectDmg).coerceAtLeast(0f)
                    if (currentStamina <= 0f) {
                        isRecharging = 1
                    }
                    return "🛡️ Boss reflected ${reflectDmg.toInt()} stamina damage!"
                }
                BossAttackPattern.SELF_HEAL -> {
                    val heal = bossHealAmount * 10f // heal per tick
                    itemHp = (itemHp + heal).coerceAtMost(itemMaxHp)
                    return "🔄 Boss healed ${heal.toInt()} HP!"
                }
                BossAttackPattern.ENRAGE -> {
                    val drain = bossStaminaDrain
                    currentStamina = (currentStamina - drain).coerceAtLeast(0f)
                    if (currentStamina <= 0f) {
                        isRecharging = 1
                    }
                    return "🔥 Enraged! -${drain.toInt()} stamina!"
                }
                BossAttackPattern.SHIELD -> {
                    // Shield just stays active, no extra effect per tick
                    return "🛡️ Shield active! Damage reduced."
                }
            }
        }
        return null
    }

    /**
     * Check if the boss should transition to a new phase based on current HP.
     * Returns the new phase description if a transition happened, null otherwise.
     */
    fun checkBossPhaseTransition(phases: List<BossPhase>): String? {
        if (bossCurrentPhase < 0 || phases.isEmpty()) return null

        val hpFraction = if (itemMaxHp > 0f) itemHp / itemMaxHp else 0f

        // Find the highest phase that should be active at this HP level
        var newPhase = -1
        for (i in phases.indices) {
            if (hpFraction <= phases[i].hpThreshold) {
                newPhase = i
            }
        }

        if (newPhase > bossCurrentPhase) {
            bossCurrentPhase = newPhase
            val phase = phases[newPhase]
            applyBossPhase(phase)
            bossAttackTimer = phase.attackIntervalMs
            return "⚠️ PHASE ${newPhase + 1}! ${phase.attackDescription}"
        }
        return null
    }

    /**
     * Calculate damage with boss modifiers (shield, etc.).
     * Returns the actual damage dealt.
     */
    fun calculateBossDamage(rawDamage: Float): Float {
        var finalDamage = rawDamage
        if (isBossShielded == 1) {
            finalDamage *= bossShieldStrength
        }
        return finalDamage
    }

    /** End the boss fight (reset boss state). */
    fun endBossFight() {
        bossCurrentPhase = -1
        bossAttackTimer = 0L
        isBossShielded = 0
        isBossReflecting = 0
        bossHealAmount = 0f
        bossStaminaDrain = 0f
        bossShieldStrength = 1f
        bossAttackDescription = 0
    }

    // ── Save/Load ─────────────────────────────────────────────────────────────

    /** Restore all fields from a [GameSaveData] object (loaded from disk). */
    fun restoreFromSave(data: GameSaveData) {
        coffeeDrops = data.coffeeDrops
        tissues = data.tissues
        currentStamina = data.currentStamina
        maxStamina = data.maxStamina
        isRecharging = data.isRecharging
        currentItem = data.currentItem
        itemHp = data.itemHp
        itemMaxHp = data.itemMaxHp
        isItemDestroyed = data.isItemDestroyed
        sneezePower = data.sneezePower
        critChance = data.critChance
        totalSneezes = data.totalSneezes
        currentZone = data.currentZone
        sneezePowerLevel = data.sneezePowerLevel
        maxStaminaLevel = data.maxStaminaLevel
        critChanceLevel = data.critChanceLevel
        frenzyTimeRemaining = data.frenzyTimeRemaining
        ownedCosmetics = data.ownedCosmetics
        equippedHat = data.equippedHat
        equippedGlasses = data.equippedGlasses
        equippedSneezeColor = data.equippedSneezeColor
        soundEnabled = if (data.soundEnabled) 1 else 0
        soundVolume = data.soundVolume
        unlockedAchievements = data.unlockedAchievements
        totalItemsDestroyed = data.totalItemsDestroyed
        totalCrits = data.totalCrits
        frenzyActivations = data.frenzyActivations
        totalUpgradesBought = data.totalUpgradesBought
        staminaExhaustions = data.staminaExhaustions
        bossesDefeated = data.bossesDefeated
        totalCoffeeEarned = data.totalCoffeeEarned
        totalTissuesEarned = data.totalTissuesEarned
        playTimeSeconds = data.playTimeSeconds
        tutorialCompleted = if (data.tutorialCompleted) 1 else 0
        sneezesPerZone[0] = data.sneezesPerZone0
        sneezesPerZone[1] = data.sneezesPerZone1
        sneezesPerZone[2] = data.sneezesPerZone2
        sneezesPerZone[3] = data.sneezesPerZone3
        sneezesPerZone[4] = data.sneezesPerZone4
        lastSaveTimestamp = data.lastSaveTimestamp
        // ── New fields ──
        prestigeLevel = data.prestigeLevel
        prestigeMultiplier = data.prestigeMultiplier
        lastRewardDay = data.lastRewardDay
        dailyStreak = data.dailyStreak
        dailyRewardIndex = data.dailyRewardIndex
        autoSneezeLevel = data.autoSneezeLevel
        autoSneezeInterval = data.autoSneezeInterval
    }

    companion object {
        const val MAX_STAMINA = 50f
        const val STAMINA_COST_PER_SNEEZE = 2f
        const val STAMINA_RECHARGE_RATE = 8f // per second
        const val FRENZY_DURATION_MS = 15_000L // 15 seconds
        const val POLLEN_CLOUD_INTERVAL_MS = 20_000L // every ~20 seconds

        /** Daily rewards for a 7-day cycle. */
        val DAILY_REWARDS = listOf(
            DailyReward("☕ 100 Coffee", 100L, 0L),
            DailyReward("☕ 250 Coffee + 🧻 1 Tissue", 250L, 1L),
            DailyReward("☕ 500 Coffee", 500L, 0L),
            DailyReward("☕ 750 Coffee + 🧻 2 Tissues", 750L, 2L),
            DailyReward("☕ 1000 Coffee", 1000L, 0L),
            DailyReward("☕ 1500 Coffee + 🧻 3 Tissues", 1500L, 3L),
            DailyReward("☕ 3000 Coffee + 🧻 5 Tissues ⭐", 3000L, 5L)
        )
    }
}

/** Result of a sneeze attack. */
enum class SneezeResult {
    HIT,
    CRIT,
    DESTROYED,
    NO_STAMINA
}

/** Daily reward definition. */
data class DailyReward(
    val description: String,
    val coffeeAmount: Long,
    val tissueAmount: Long
)

/** Cosmetic slot types. */
enum class CosmeticSlot {
    HAT,
    GLASSES,
    SNEEZE_COLOR
}
