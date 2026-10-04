package com.godofnose.sneezeandthunder.engine

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.godofnose.sneezeandthunder.game.GameState
import kotlin.random.Random

/**
 * SoundEngine — Handles loading and playing game sound effects.
 *
 * Loads sneeze sounds (sneeze1..sneeze10.wav) from res/raw.
 * Provides methods for different game events:
 * - [playRandomSneeze] — normal sneeze (HIT)
 * - [playCrit] — critical hit
 * - [playDestroy] — item destroyed
 * - [playFrenzy] — pollen cloud / frenzy activation
 * - [playNoStamina] — out of stamina
 *
 * Each play randomizes pitch (0.7–1.3) and volume (0.8–1.0).
 * Respects [GameState.soundEnabled] and [GameState.soundVolume].
 */
class SoundEngine(context: Context) {

    private val soundPool: SoundPool
    private val sneezeIds = mutableListOf<Int>()
    private var critSoundId = 0
    private var destroySoundId = 0
    private var frenzySoundId = 0
    private var noStaminaSoundId = 0
    private var bossAttackSoundId = 0
    private var bossPhaseSoundId = 0
    private var achievementSoundId = 0
    private var buttonClickSoundId = 0
    private var loaded = false

    /** Reference to GameState for sound settings. Set externally. */
    var gameState: GameState? = null

    init {
        val attrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(6)
            .setAudioAttributes(attrs)
            .build()

        soundPool.setOnLoadCompleteListener { _, _, status ->
            if (status == 0) loaded = true
        }

        loadSounds(context)
    }

    private fun loadSounds(context: Context) {
        val pkg = context.packageName
        val res = context.resources

        // Load sneeze1..sneeze10.wav
        for (i in 1..10) {
            val resId = res.getIdentifier("sneeze$i", "raw", pkg)
            if (resId != 0) {
                sneezeIds.add(soundPool.load(context, resId, 1))
            }
        }

        // Load special sounds (optional — gracefully fall back to sneeze sounds)
        critSoundId = loadSingleSound(context, "crit")
        destroySoundId = loadSingleSound(context, "destroy")
        frenzySoundId = loadSingleSound(context, "frenzy")
        noStaminaSoundId = loadSingleSound(context, "nostamina")
        bossAttackSoundId = loadSingleSound(context, "boss_attack")
        bossPhaseSoundId = loadSingleSound(context, "boss_phase")
        achievementSoundId = loadSingleSound(context, "achievement")
        buttonClickSoundId = loadSingleSound(context, "button_click")
    }

    /** Load a single sound file, return 0 if not found. */
    private fun loadSingleSound(context: Context, name: String): Int {
        val resId = context.resources.getIdentifier(name, "raw", context.packageName)
        return if (resId != 0) soundPool.load(context, resId, 1) else 0
    }

    /** Whether sound is enabled according to GameState. */
    private val isSoundEnabled: Boolean
        get() = gameState?.soundEnabled == 1

    /** Master volume multiplier from GameState. */
    private val masterVolume: Float
        get() = (gameState?.soundVolume ?: 1f).coerceIn(0f, 1f)

    /** Play a random sneeze sound (for normal HIT). */
    fun playRandomSneeze() {
        if (!canPlay()) return
        playSound(sneezeIds[Random.nextInt(sneezeIds.size)])
    }

    /** Play critical hit sound. Falls back to sneeze if not available. */
    fun playCrit() {
        if (!canPlay()) return
        if (critSoundId != 0) playSound(critSoundId)
        else playRandomSneeze()
    }

    /** Play item destroyed sound. Falls back to sneeze if not available. */
    fun playDestroy() {
        if (!canPlay()) return
        if (destroySoundId != 0) playSound(destroySoundId)
        else playRandomSneeze()
    }

    /** Play frenzy activation sound. Falls back to sneeze if not available. */
    fun playFrenzy() {
        if (!canPlay()) return
        if (frenzySoundId != 0) playSound(frenzySoundId)
        else playRandomSneeze()
    }

    /** Play no-stamina sound. Falls back to sneeze if not available. */
    fun playNoStamina() {
        if (!canPlay()) return
        if (noStaminaSoundId != 0) playSound(noStaminaSoundId)
        else playRandomSneeze()
    }

    /** Play boss attack sound. Falls back to sneeze if not available. */
    fun playBossAttack() {
        if (!canPlay()) return
        if (bossAttackSoundId != 0) playSound(bossAttackSoundId)
        else playRandomSneeze()
    }

    /** Play boss phase transition sound. Falls back to sneeze if not available. */
    fun playBossPhase() {
        if (!canPlay()) return
        if (bossPhaseSoundId != 0) playSound(bossPhaseSoundId)
        else playRandomSneeze()
    }

    /** Play achievement unlock sound. Falls back to sneeze if not available. */
    fun playAchievement() {
        if (!canPlay()) return
        if (achievementSoundId != 0) playSound(achievementSoundId)
        else playRandomSneeze()
    }

    /** Play button click sound. Falls back to sneeze if not available. */
    fun playButtonClick() {
        if (!canPlay()) return
        if (buttonClickSoundId != 0) playSound(buttonClickSoundId)
        else playRandomSneeze()
    }

    /** Check if we can play a sound (loaded + enabled + has sounds). */
    private fun canPlay(): Boolean {
        if (!loaded) return false
        if (!isSoundEnabled) return false
        if (sneezeIds.isEmpty()) return false
        return true
    }

    /** Internal: play a sound ID with randomized pitch and volume. */
    private fun playSound(soundId: Int) {
        val rate = 0.7f + Random.nextFloat() * 0.6f
        val baseVolume = 0.8f + Random.nextFloat() * 0.2f
        val finalVolume = (baseVolume * masterVolume).coerceIn(0f, 1f)
        if (finalVolume > 0f) {
            soundPool.play(soundId, finalVolume, finalVolume, 1, 0, rate)
        }
    }

    /** Release all SoundPool resources. */
    fun release() {
        soundPool.release()
    }

    fun isLoaded(): Boolean = loaded
    fun soundCount(): Int = sneezeIds.size
}
