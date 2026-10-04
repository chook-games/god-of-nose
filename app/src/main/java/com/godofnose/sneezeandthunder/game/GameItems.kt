package com.godofnose.sneezeandthunder.game

/**
 * A boss phase with specific HP threshold, attack pattern, and rewards.
 *
 * @param phaseIndex Which phase (0 = first, 1 = second, etc.)
 * @param hpThreshold HP percentage (0.0–1.0) when this phase activates
 * @param attackPattern Type of boss attack during this phase
 * @param attackIntervalMs How often the boss attacks (ms)
 * @param attackDescription Description shown to player
 */
data class BossPhase(
    val phaseIndex: Int,
    val hpThreshold: Float,
    val attackPattern: BossAttackPattern,
    val attackIntervalMs: Long = 3000L,
    val attackDescription: String
)

/** Types of boss attacks. */
enum class BossAttackPattern {
    /** Drains stamina from the player. */
    STAMINA_DRAIN,
    /** Reflects a portion of damage back at the player (stamina cost). */
    DAMAGE_REFLECT,
    /** Boss heals itself over time. */
    SELF_HEAL,
    /** Boss becomes enraged — deals more stamina drain. */
    ENRAGE,
    /** Boss shields itself — reduces incoming damage. */
    SHIELD
}

/**
 * Data for a destroyable item in the game.
 *
 * @param id Unique identifier
 * @param name Display name
 * @param emoji Emoji representation of the item
 * @param maxHp How much damage it can take
 * @param coffeeReward Coffee Drops earned when destroyed
 * @param tissueReward Tissues earned (only bosses give these)
 * @param isBoss Whether this is a boss item
 * @param phases Boss phases (only used if isBoss = true)
 */
data class GameItem(
    val id: Int,
    val name: String,
    val emoji: String,
    val maxHp: Float,
    val coffeeReward: Long,
    val tissueReward: Long = 0L,
    val isBoss: Boolean = false,
    val phases: List<BossPhase> = emptyList()
)

/**
 * All items organized by zone/level.
 *
 * Zone 0: Home (Σπίτι)
 * Zone 1: Coffee Shop (Καφετέρια)
 * Zone 2: Mini Market (Μίνι Μάρκετ)
 * Zone 3: Office (Γραφείο)
 * Zone 4: Taverna & Street (Ταβέρνα & Δρόμος)
 */
object ItemDatabase {

    private var nextId = 0

    val zones: List<List<GameItem>> = listOf(
        // ── Zone 0: Home ──────────────────────────────────────────────────────
        listOf(
            GameItem(nextId++, "Paperclip", "📎", maxHp = 3f, coffeeReward = 5),
            GameItem(nextId++, "Cup", "☕", maxHp = 6f, coffeeReward = 10),
            GameItem(nextId++, "Clock", "🕐", maxHp = 12f, coffeeReward = 20),
            GameItem(nextId++, "Chair", "🪑", maxHp = 20f, coffeeReward = 35),
            GameItem(
                nextId++, "WARDROBE", "🗄️", maxHp = 50f, coffeeReward = 100, tissueReward = 1, isBoss = true,
                phases = listOf(
                    BossPhase(0, 1.0f, BossAttackPattern.STAMINA_DRAIN, 4000L, "💧 Stamina Drain"),
                    BossPhase(1, 0.5f, BossAttackPattern.DAMAGE_REFLECT, 5000L, "🛡️ Damage Reflect")
                )
            )
        ),
        // ── Zone 1: Coffee Shop ───────────────────────────────────────────────
        listOf(
            GameItem(nextId++, "Straw", "🥤", maxHp = 4f, coffeeReward = 8),
            GameItem(nextId++, "Glass", "🥛", maxHp = 8f, coffeeReward = 15),
            GameItem(nextId++, "Sugar Bowl", "🍯", maxHp = 15f, coffeeReward = 25),
            GameItem(
                nextId++, "ESPRESSO MACHINE", "☕", maxHp = 60f, coffeeReward = 150, tissueReward = 2, isBoss = true,
                phases = listOf(
                    BossPhase(0, 1.0f, BossAttackPattern.SELF_HEAL, 5000L, "🔄 Self Heal"),
                    BossPhase(1, 0.4f, BossAttackPattern.ENRAGE, 2500L, "🔥 Enrage!")
                )
            )
        ),
        // ── Zone 2: Mini Market ───────────────────────────────────────────────
        listOf(
            GameItem(nextId++, "Matches", "🔥", maxHp = 5f, coffeeReward = 10),
            GameItem(nextId++, "Can", "🥫", maxHp = 12f, coffeeReward = 20),
            GameItem(nextId++, "Bottle", "🍾", maxHp = 22f, coffeeReward = 40),
            GameItem(
                nextId++, "FRIDGE", "🧊", maxHp = 80f, coffeeReward = 200, tissueReward = 3, isBoss = true,
                phases = listOf(
                    BossPhase(0, 1.0f, BossAttackPattern.SHIELD, 4000L, "🛡️ Shielded!"),
                    BossPhase(1, 0.5f, BossAttackPattern.STAMINA_DRAIN, 3000L, "💧 Freeze Drain"),
                    BossPhase(2, 0.25f, BossAttackPattern.ENRAGE, 2000L, "❄️ Blizzard!")
                )
            )
        ),
        // ── Zone 3: Office ────────────────────────────────────────────────────
        listOf(
            GameItem(nextId++, "Stapler", "📌", maxHp = 7f, coffeeReward = 12),
            GameItem(nextId++, "Photocopier", "🖨️", maxHp = 18f, coffeeReward = 30),
            GameItem(
                nextId++, "BOSS DESK", "💼", maxHp = 100f, coffeeReward = 300, tissueReward = 4, isBoss = true,
                phases = listOf(
                    BossPhase(0, 1.0f, BossAttackPattern.DAMAGE_REFLECT, 4000L, "🛡️ Paperwork Shield"),
                    BossPhase(1, 0.5f, BossAttackPattern.SELF_HEAL, 6000L, "📋 Filing Heal"),
                    BossPhase(2, 0.25f, BossAttackPattern.ENRAGE, 2000L, "😡 Angry Boss!")
                )
            )
        ),
        // ── Zone 4: Taverna & Street ──────────────────────────────────────────
        listOf(
            GameItem(nextId++, "Plate", "🍽️", maxHp = 10f, coffeeReward = 18),
            GameItem(nextId++, "Table", "🪑", maxHp = 25f, coffeeReward = 45),
            GameItem(nextId++, "Motorcycle", "🏍️", maxHp = 50f, coffeeReward = 80),
            GameItem(
                nextId++, "CAR", "🚗", maxHp = 200f, coffeeReward = 500, tissueReward = 5, isBoss = true,
                phases = listOf(
                    BossPhase(0, 1.0f, BossAttackPattern.SHIELD, 3000L, "🛡️ Armored!"),
                    BossPhase(1, 0.6f, BossAttackPattern.STAMINA_DRAIN, 3000L, "💧 Exhaust Fumes"),
                    BossPhase(2, 0.3f, BossAttackPattern.ENRAGE, 1500L, "🔥 Engine Overheat!"),
                    BossPhase(3, 0.15f, BossAttackPattern.SELF_HEAL, 4000L, "🔧 Self-Repair")
                )
            )
        )
    )

    /** Get all items in a specific zone. */
    fun getZoneItems(zoneIndex: Int): List<GameItem> {
        return zones.getOrElse(zoneIndex) { zones.first() }
    }

    /** Get a specific item by zone and index. */
    fun getItem(zoneIndex: Int, itemIndex: Int): GameItem {
        val zone = getZoneItems(zoneIndex)
        return zone.getOrElse(itemIndex) { zone.first() }
    }

    /** Total number of zones. */
    val zoneCount: Int get() = zones.size
}
