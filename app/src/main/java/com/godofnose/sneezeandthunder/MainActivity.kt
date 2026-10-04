package com.godofnose.sneezeandthunder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.godofnose.sneezeandthunder.engine.SaveManager
import com.godofnose.sneezeandthunder.engine.SoundEngine
import com.godofnose.sneezeandthunder.game.GameState
import com.godofnose.sneezeandthunder.ui.navigation.AppNavigation
import com.godofnose.sneezeandthunder.ui.theme.GodOfNoseTheme

class MainActivity : ComponentActivity() {

    private var soundEngine: SoundEngine? = null
    private lateinit var saveManager: SaveManager
    private val gameState = GameState()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ── Edge-to-Edge ────────────────────────────────────────────────
        enableEdgeToEdge()

        // ── Immersive Mode: hide system bars ────────────────────────────
        setupImmersiveMode()

        // ── Initialize SaveManager & Load Game ──────────────────────────
        saveManager = SaveManager(this)
        saveManager.load(gameState)

        // ── Initialize SoundEngine ──────────────────────────────────────
        soundEngine = SoundEngine(this)
        soundEngine?.gameState = gameState

        // ── Compose UI ──────────────────────────────────────────────────
        setContent {
            GodOfNoseTheme {
                val engine = remember { soundEngine }
                val state = remember { gameState }
                val saveMgr = remember { saveManager }
                AppNavigation(
                    soundEngine = engine,
                    gameState = state,
                    saveManager = saveMgr
                )
            }
        }
    }

    override fun onPause() {
        super.onPause()
        // Auto-save when app goes to background
        saveManager.save(gameState)
    }

    override fun onStop() {
        super.onStop()
        // Extra safety save
        saveManager.save(gameState)
    }

    override fun onDestroy() {
        super.onDestroy()
        saveManager.save(gameState)
        soundEngine?.release()
        soundEngine = null
    }

    /**
     * Re-hide system bars whenever the window regains focus
     * (e.g. after a dialog, notification, or app switch).
     */
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            setupImmersiveMode()
        }
    }

    /**
     * Hides the status bar and navigation bar using WindowInsetsControllerCompat.
     * System bars will only reappear with a swipe gesture
     * (BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE) and auto-hide again.
     */
    private fun setupImmersiveMode() {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.systemBars())
    }
}
