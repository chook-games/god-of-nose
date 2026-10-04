package com.godofnose.sneezeandthunder.engine

import android.app.Activity
import android.util.Log

/**
 * GooglePlayGamesManager — Placeholder for Google Play Games Services integration.
 *
 * This class provides stub methods for:
 * - Sign-in / Sign-out
 * - Submitting scores to leaderboards
 * - Unlocking achievements
 * - Showing leaderboard / achievements UI
 *
 * TODO: Replace stubs with actual Google Play Games SDK calls when ready.
 */
class GooglePlayGamesManager(private val activity: Activity) {

    /** Whether the user is currently signed in. */
    private var signedIn = false

    /** Attempt to sign in. Stub — always "succeeds" silently. */
    fun signIn(onResult: (Boolean) -> Unit = {}) {
        Log.d(TAG, "signIn() — placeholder (would show Google sign-in UI)")
        signedIn = true
        onResult(true)
    }

    /** Sign out. Stub. */
    fun signOut() {
        Log.d(TAG, "signOut() — placeholder")
        signedIn = false
    }

    /** Whether user is signed in. */
    fun isSignedIn(): Boolean = signedIn

    /**
     * Submit a score to a leaderboard.
     * @param leaderboardId The leaderboard resource ID (e.g., "CgkI...")
     * @param score The score value
     */
    fun submitScore(leaderboardId: String, score: Long) {
        Log.d(TAG, "submitScore(leaderboardId=$leaderboardId, score=$score) — placeholder")
        // TODO: Games.getLeaderboardsClient(activity, GoogleSignIn.getLastSignedInAccount(activity))
        //       .submitScore(leaderboardId, score)
    }

    /**
     * Unlock an achievement.
     * @param achievementId The achievement resource ID (e.g., "CgkI...")
     */
    fun unlockAchievement(achievementId: String) {
        Log.d(TAG, "unlockAchievement(achievementId=$achievementId) — placeholder")
        // TODO: Games.getAchievementsClient(activity, GoogleSignIn.getLastSignedInAccount(activity))
        //       .unlock(achievementId)
    }

    /**
     * Show the leaderboard UI.
     * @param leaderboardId The leaderboard resource ID
     */
    fun showLeaderboard(leaderboardId: String) {
        Log.d(TAG, "showLeaderboard(leaderboardId=$leaderboardId) — placeholder")
        // TODO: Games.getLeaderboardsClient(activity, GoogleSignIn.getLastSignedInAccount(activity))
        //       .getLeaderboardIntent(leaderboardId)
        //       .addOnSuccessListener { intent -> activity.startActivityForResult(intent, RC_LEADERBOARD) }
    }

    /**
     * Show the achievements UI.
     */
    fun showAchievements() {
        Log.d(TAG, "showAchievements() — placeholder")
        // TODO: Games.getAchievementsClient(activity, GoogleSignIn.getLastSignedInAccount(activity))
        //       .getAchievementsIntent()
        //       .addOnSuccessListener { intent -> activity.startActivityForResult(intent, RC_ACHIEVEMENTS) }
    }

    companion object {
        private const val TAG = "GooglePlayGames"
        private const val RC_LEADERBOARD = 9001
        private const val RC_ACHIEVEMENTS = 9002
    }
}
