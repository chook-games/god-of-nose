package com.godofnose.sneezeandthunder.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.godofnose.sneezeandthunder.engine.SaveManager
import com.godofnose.sneezeandthunder.engine.SoundEngine
import com.godofnose.sneezeandthunder.game.GameState
import com.godofnose.sneezeandthunder.ui.screens.AchievementsScreen
import com.godofnose.sneezeandthunder.ui.screens.CosmeticsShopScreen
import com.godofnose.sneezeandthunder.ui.screens.GameScreen
import com.godofnose.sneezeandthunder.ui.screens.MainMenuScreen
import com.godofnose.sneezeandthunder.ui.screens.SettingsScreen
import com.godofnose.sneezeandthunder.ui.screens.StatisticsScreen
import com.godofnose.sneezeandthunder.ui.screens.TutorialScreen

// ─── Route Definitions ───────────────────────────────────────────────────────

object Routes {
    const val MAIN_MENU = "main_menu"
    const val GAME = "game"
    const val SETTINGS = "settings"
    const val ACHIEVEMENTS = "achievements"
    const val SHOP = "shop"
    const val STATISTICS = "statistics"
    const val TUTORIAL = "tutorial"
}

// ─── Navigation Host ─────────────────────────────────────────────────────────

@Composable
fun AppNavigation(
    soundEngine: SoundEngine? = null,
    gameState: GameState,
    saveManager: SaveManager? = null,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.MAIN_MENU
    ) {
        composable(Routes.MAIN_MENU) {
            MainMenuScreen(
                gameState = gameState,
                onPlayClick = {
                    navController.navigate(Routes.GAME)
                },
                onSettingsClick = {
                    navController.navigate(Routes.SETTINGS)
                },
                onAchievementsClick = {
                    navController.navigate(Routes.ACHIEVEMENTS)
                },
                onShopClick = {
                    navController.navigate(Routes.SHOP)
                },
                onStatisticsClick = {
                    navController.navigate(Routes.STATISTICS)
                },
                onTutorialClick = {
                    navController.navigate(Routes.TUTORIAL)
                }
            )
        }

        composable(Routes.GAME) {
            GameScreen(
                gameState = gameState,
                soundEngine = soundEngine,
                onBackToMenu = {
                    navController.popBackStack(Routes.MAIN_MENU, inclusive = false)
                }
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                gameState = gameState,
                saveManager = saveManager,
                onBackToMenu = {
                    navController.popBackStack(Routes.MAIN_MENU, inclusive = false)
                }
            )
        }

        composable(Routes.ACHIEVEMENTS) {
            AchievementsScreen(
                gameState = gameState,
                onBackToMenu = {
                    navController.popBackStack(Routes.MAIN_MENU, inclusive = false)
                }
            )
        }

        composable(Routes.SHOP) {
            CosmeticsShopScreen(
                gameState = gameState,
                onBackToMenu = {
                    navController.popBackStack(Routes.MAIN_MENU, inclusive = false)
                }
            )
        }

        composable(Routes.STATISTICS) {
            StatisticsScreen(
                gameState = gameState,
                onBack = {
                    navController.popBackStack(Routes.MAIN_MENU, inclusive = false)
                }
            )
        }

        composable(Routes.TUTORIAL) {
            TutorialScreen(
                gameState = gameState,
                onComplete = {
                    navController.popBackStack(Routes.MAIN_MENU, inclusive = false)
                }
            )
        }
    }
}
