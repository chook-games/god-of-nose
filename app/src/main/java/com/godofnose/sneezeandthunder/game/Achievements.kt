package com.godofnose.sneezeandthunder.game

/**
 * Achievement definition for "God of Nose: Sneeze and Thunder".
 *
 * Each achievement has:
 * - [id] — Unique identifier (bit position in the bitmask)
 * - [title] — Display name
 * - [description] — How to unlock it
 * - [icon] — Emoji icon
 * - [coffeeReward] — Coffee Drops awarded on unlock
 * - [tissueReward] — Tissues awarded on unlock
 */
data class Achievement(
    val id: Int,
    val title: String,
    val description: String,
    val icon: String,
    val coffeeReward: Long = 0L,
    val tissueReward: Long = 0L
)

/**
 * Database of all achievements.
 *
 * Categories:
 * - Sneezes (total sneezes milestones)
 * - Items Destroyed (items destroyed milestones)
 * - Coffee Drops (currency milestones)
 * - Zones (zone progression)
 * - Crits (critical hit milestones)
 * - Frenzy (frenzy-related)
 * - Cosmetics (cosmetic-related)
 * - Bosses (boss kills)
 */
object AchievementDatabase {

    /** All achievements, indexed by id. */
    private val allAchievements: Map<Int, Achievement> = listOf(
        // ── Sneezes ───────────────────────────────────────────────────────────
        Achievement(0,  "Baby Sneeze",     "Sneeze 10 times",              "👶", coffeeReward = 10),
        Achievement(1,  "Getting Started",  "Sneeze 100 times",            "🤧", coffeeReward = 50),
        Achievement(2,  "Sneeze Machine",   "Sneeze 1,000 times",          "💨", coffeeReward = 200),
        Achievement(3,  "Sneeze Storm",     "Sneeze 10,000 times",         "🌪️", coffeeReward = 500, tissueReward = 5),
        Achievement(4,  "God of Sneeze",    "Sneeze 100,000 times",        "⚡", coffeeReward = 2000, tissueReward = 20),

        // ── Items Destroyed ───────────────────────────────────────────────────
        Achievement(5,  "First Blood",      "Destroy your first item",     "💀", coffeeReward = 10),
        Achievement(6,  "Trash Compactor",  "Destroy 50 items",            "🗑️", coffeeReward = 100),
        Achievement(7,  "Wrecking Ball",    "Destroy 500 items",           "🏗️", coffeeReward = 500),
        Achievement(8,  "Apocalypse",       "Destroy 5,000 items",         "☢️", coffeeReward = 2000, tissueReward = 10),

        // ── Coffee Drops ──────────────────────────────────────────────────────
        Achievement(9,  "Coffee Addict",    "Earn 1,000 Coffee Drops",     "☕", coffeeReward = 100),
        Achievement(10, "Coffee Baron",     "Earn 100,000 Coffee Drops",   "💰", coffeeReward = 1000, tissueReward = 5),
        Achievement(11, "Coffee Empire",    "Earn 1,000,000 Coffee Drops", "🏦", coffeeReward = 5000, tissueReward = 25),

        // ── Zones ─────────────────────────────────────────────────────────────
        Achievement(12, "Home Wrecker",     "Complete Zone 1 (Home)",      "🏠", coffeeReward = 50),
        Achievement(13, "Coffee Rush",      "Complete Zone 2 (Coffee Shop)","☕", coffeeReward = 100),
        Achievement(14, "Shopaholic",       "Complete Zone 3 (Mini Market)","🛒", coffeeReward = 200),
        Achievement(15, "Office Menace",    "Complete Zone 4 (Office)",    "🏢", coffeeReward = 500),
        Achievement(16, "Taverna Legend",   "Complete Zone 5 (Taverna)",   "🚗", coffeeReward = 1000, tissueReward = 10),

        // ── Crits ─────────────────────────────────────────────────────────────
        Achievement(17, "Lucky Sneeze",     "Land 10 critical hits",       "🍀", coffeeReward = 50),
        Achievement(18, "Critical Mass",    "Land 500 critical hits",      "💥", coffeeReward = 500),
        Achievement(19, "Perfect Storm",    "Land 5,000 critical hits",    "🌩️", coffeeReward = 2000, tissueReward = 10),

        // ── Frenzy ────────────────────────────────────────────────────────────
        Achievement(20, "Allergic!",        "Activate Frenzy mode 5 times","🌸", coffeeReward = 100),
        Achievement(21, "Pollen King",      "Activate Frenzy mode 50 times","👑", coffeeReward = 1000, tissueReward = 5),

        // ── Cosmetics ─────────────────────────────────────────────────────────
        Achievement(22, "Fashion Victim",   "Buy your first cosmetic",     "🎩", coffeeReward = 50),
        Achievement(23, "Style Guru",       "Buy 5 cosmetics",             "🕶️", coffeeReward = 500, tissueReward = 5),

        // ── Bosses ────────────────────────────────────────────────────────────
        Achievement(24, "Boss Slayer",      "Defeat your first boss",      "🗡️", coffeeReward = 200),
        Achievement(25, "Boss Hunter",      "Defeat 10 bosses",            "🏹", coffeeReward = 1000, tissueReward = 10),
        Achievement(26, "Boss Annihilator", "Defeat 50 bosses",            "💀", coffeeReward = 5000, tissueReward = 25),

        // ── Upgrades ──────────────────────────────────────────────────────────
        Achievement(27, "Power Up!",        "Buy your first upgrade",      "⬆️", coffeeReward = 50),
        Achievement(28, "Fully Loaded",     "Max out all upgrades",        "🏆", coffeeReward = 5000, tissueReward = 50),

        // ── Stamina ───────────────────────────────────────────────────────────
        Achievement(29, "Endurance",        "Run out of stamina 100 times","😮‍💥", coffeeReward = 100),
        Achievement(30, "Marathon Runner",  "Run out of stamina 1,000 times","🏃", coffeeReward = 1000, tissueReward = 5),
    ).associateBy { it.id }

    /** Total number of achievements. */
    val count: Int get() = allAchievements.size

    /** Get an achievement by id. */
    fun getById(id: Int): Achievement? = allAchievements[id]

    /** Get all achievements sorted by id. */
    fun getAll(): List<Achievement> = allAchievements.entries.sortedBy { it.key }.map { it.value }

    /** Get achievements by category (range of ids). */
    fun getByCategory(startId: Int, endId: Int): List<Achievement> =
        (startId..endId).mapNotNull { allAchievements[it] }
}
