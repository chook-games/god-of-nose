package com.godofnose.sneezeandthunder.engine

import android.content.Context
import android.content.SharedPreferences
import com.godofnose.sneezeandthunder.game.GameState
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * SaveManager — Handles persisting and restoring [GameState] via SharedPreferences.
 *
 * Uses kotlinx.serialization to convert game state to/from JSON.
 * Auto-saves on pause/stop, auto-loads on app start.
 */
class SaveManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val json = Json {
        prettyPrint = false
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    // ── Save ──────────────────────────────────────────────────────────────────

    /** Save the current [GameState] to SharedPreferences. */
    fun save(gameState: GameState) {
        // Update timestamp before saving
        gameState.updateLastSaveTimestamp()

        val data = GameSaveData(
            coffeeDrops = gameState.coffeeDrops,
            tissues = gameState.tissues,
            currentStamina = gameState.currentStamina,
            maxStamina = gameState.maxStamina,
            isRecharging = gameState.isRecharging,
            currentItem = gameState.currentItem,
            itemHp = gameState.itemHp,
            itemMaxHp = gameState.itemMaxHp,
            isItemDestroyed = gameState.isItemDestroyed,
            sneezePower = gameState.sneezePower,
            critChance = gameState.critChance,
            totalSneezes = gameState.totalSneezes,
            currentZone = gameState.currentZone,
            sneezePowerLevel = gameState.sneezePowerLevel,
            maxStaminaLevel = gameState.maxStaminaLevel,
            critChanceLevel = gameState.critChanceLevel,
            frenzyTimeRemaining = gameState.frenzyTimeRemaining,
            ownedCosmetics = gameState.ownedCosmetics,
            equippedHat = gameState.equippedHat,
            equippedGlasses = gameState.equippedGlasses,
            equippedSneezeColor = gameState.equippedSneezeColor,
            soundEnabled = gameState.soundEnabled == 1,
            soundVolume = gameState.soundVolume,
            unlockedAchievements = gameState.unlockedAchievements,
            totalItemsDestroyed = gameState.totalItemsDestroyed,
            totalCrits = gameState.totalCrits,
            frenzyActivations = gameState.frenzyActivations,
            totalUpgradesBought = gameState.totalUpgradesBought,
            staminaExhaustions = gameState.staminaExhaustions,
            bossesDefeated = gameState.bossesDefeated,
            totalCoffeeEarned = gameState.totalCoffeeEarned,
            totalTissuesEarned = gameState.totalTissuesEarned,
            playTimeSeconds = gameState.playTimeSeconds,
            tutorialCompleted = gameState.tutorialCompleted == 1,
            sneezesPerZone0 = gameState.sneezesPerZone.getOrElse(0) { 0L },
            sneezesPerZone1 = gameState.sneezesPerZone.getOrElse(1) { 0L },
            sneezesPerZone2 = gameState.sneezesPerZone.getOrElse(2) { 0L },
            sneezesPerZone3 = gameState.sneezesPerZone.getOrElse(3) { 0L },
            sneezesPerZone4 = gameState.sneezesPerZone.getOrElse(4) { 0L },
            lastSaveTimestamp = gameState.lastSaveTimestamp,
            // ── New fields ──
            prestigeLevel = gameState.prestigeLevel,
            prestigeMultiplier = gameState.prestigeMultiplier,
            lastRewardDay = gameState.lastRewardDay,
            dailyStreak = gameState.dailyStreak,
            dailyRewardIndex = gameState.dailyRewardIndex,
            autoSneezeLevel = gameState.autoSneezeLevel,
            autoSneezeInterval = gameState.autoSneezeInterval
        )
        prefs.edit().putString(KEY_SAVE_DATA, json.encodeToString(data)).apply()
    }

    // ── Load ──────────────────────────────────────────────────────────────────

    /** Load saved data into [GameState]. Returns true if a save was found. */
    fun load(gameState: GameState): Boolean {
        val raw = prefs.getString(KEY_SAVE_DATA, null) ?: return false
        return try {
            val data = json.decodeFromString<GameSaveData>(raw)
            restore(gameState, data)
            // Calculate offline earnings after loading
            gameState.calculateOfflineEarnings()
            true
        } catch (e: Exception) {
            false
        }
    }

    /** Check if a save file exists. */
    fun hasSave(): Boolean = prefs.contains(KEY_SAVE_DATA)

    /** Delete the save file (for "Reset Progress"). */
    fun deleteSave() {
        prefs.edit().remove(KEY_SAVE_DATA).apply()
    }

    // ── Restore ───────────────────────────────────────────────────────────────

    private fun restore(gameState: GameState, data: GameSaveData) {
        gameState.restoreFromSave(data)
    }

    // ── Constants ─────────────────────────────────────────────────────────────

    companion object {
        private const val PREFS_NAME = "god_of_nose_save"
        private const val KEY_SAVE_DATA = "game_save_data"
    }
}

/**
 * Serializable data class mirroring all fields of [GameState].
 * This is what gets serialized to/from JSON.
 */
@Serializable
data class GameSaveData(
    val coffeeDrops: Long = 0L,
    val tissues: Long = 0L,
    val currentStamina: Float = 50f,
    val maxStamina: Float = 50f,
    val isRecharging: Int = 0,
    val currentItem: Int = 0,
    val itemHp: Float = 0f,
    val itemMaxHp: Float = 0f,
    val isItemDestroyed: Int = 0,
    val sneezePower: Float = 1f,
    val critChance: Float = 0.05f,
    val totalSneezes: Long = 0L,
    val currentZone: Int = 0,
    val sneezePowerLevel: Int = 0,
    val maxStaminaLevel: Int = 0,
    val critChanceLevel: Int = 0,
    val frenzyTimeRemaining: Long = 0L,
    val ownedCosmetics: Int = 0,
    val equippedHat: Int = 0,
    val equippedGlasses: Int = 0,
    val equippedSneezeColor: Int = 0,
    val soundEnabled: Boolean = true,
    val soundVolume: Float = 1.0f,
    // ── Achievement Tracking ──
    val unlockedAchievements: Int = 0,
    val totalItemsDestroyed: Long = 0L,
    val totalCrits: Long = 0L,
    val frenzyActivations: Int = 0,
    val totalUpgradesBought: Int = 0,
    val staminaExhaustions: Int = 0,
    val bossesDefeated: Int = 0,
    // ── Statistics ──
    val totalCoffeeEarned: Long = 0L,
    val totalTissuesEarned: Long = 0L,
    val playTimeSeconds: Long = 0L,
    val tutorialCompleted: Boolean = false,
    val sneezesPerZone0: Long = 0L,
    val sneezesPerZone1: Long = 0L,
    val sneezesPerZone2: Long = 0L,
    val sneezesPerZone3: Long = 0L,
    val sneezesPerZone4: Long = 0L,
    // ── Offline Progress ──
    val lastSaveTimestamp: Long = System.currentTimeMillis(),
    // ── Prestige ──
    val prestigeLevel: Int = 0,
    val prestigeMultiplier: Float = 1f,
    // ── Daily Rewards ──
    val lastRewardDay: Int = 0,
    val dailyStreak: Int = 0,
    val dailyRewardIndex: Int = 0,
    // ── Auto-Sneeze ──
    val autoSneezeLevel: Int = 0,
    val autoSneezeInterval: Long = 0L
)
