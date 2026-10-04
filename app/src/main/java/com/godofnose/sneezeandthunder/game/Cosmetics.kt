package com.godofnose.sneezeandthunder.game

import androidx.compose.ui.graphics.Color

/**
 * Data for a cosmetic item.
 *
 * @param id Unique ID (used as bitmask index)
 * @param name Display name
 * @param emoji Icon
 * @param costInTissues Price in Tissues
 * @param slot Which slot this belongs to
 * @param sneezeColor If slot is SNEEZE_COLOR, the color to use
 */
data class Cosmetic(
    val id: Int,
    val name: String,
    val emoji: String,
    val costInTissues: Long,
    val slot: CosmeticSlot,
    val sneezeColor: Color = Color(0xFFFFD700) // default gold
)

/**
 * Database of all available cosmetics.
 */
object CosmeticsDatabase {

    /** All cosmetics. */
    val all: List<Cosmetic> = listOf(
        // ── Hats ──────────────────────────────────────────────────────────────
        Cosmetic(1, "Party Hat", "🎉", costInTissues = 3, slot = CosmeticSlot.HAT),
        Cosmetic(2, "Crown", "👑", costInTissues = 5, slot = CosmeticSlot.HAT),
        Cosmetic(3, "Top Hat", "🎩", costInTissues = 8, slot = CosmeticSlot.HAT),
        Cosmetic(4, "Baseball Cap", "🧢", costInTissues = 10, slot = CosmeticSlot.HAT),

        // ── Glasses ───────────────────────────────────────────────────────────
        Cosmetic(5, "Sunglasses", "😎", costInTissues = 3, slot = CosmeticSlot.GLASSES),
        Cosmetic(6, "Nerd Glasses", "🤓", costInTissues = 5, slot = CosmeticSlot.GLASSES),
        Cosmetic(7, "Star Glasses", "⭐", costInTissues = 8, slot = CosmeticSlot.GLASSES),

        // ── Sneeze Colors ─────────────────────────────────────────────────────
        Cosmetic(8, "Green Sneeze", "💚", costInTissues = 4, slot = CosmeticSlot.SNEEZE_COLOR, sneezeColor = Color(0xFF4CAF50)),
        Cosmetic(9, "Blue Sneeze", "💙", costInTissues = 4, slot = CosmeticSlot.SNEEZE_COLOR, sneezeColor = Color(0xFF42A5F5)),
        Cosmetic(10, "Purple Sneeze", "💜", costInTissues = 4, slot = CosmeticSlot.SNEEZE_COLOR, sneezeColor = Color(0xFFAB47BC)),
        Cosmetic(11, "Rainbow Sneeze", "🌈", costInTissues = 10, slot = CosmeticSlot.SNEEZE_COLOR, sneezeColor = Color(0xFFFF4081)),
    )

    /** Get cosmetics for a specific slot. */
    fun getBySlot(slot: CosmeticSlot): List<Cosmetic> = all.filter { it.slot == slot }

    /** Get a cosmetic by ID. */
    fun getById(id: Int): Cosmetic? = all.find { it.id == id }
}
