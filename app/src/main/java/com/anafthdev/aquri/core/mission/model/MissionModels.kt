package com.anafthdev.aquri.core.mission.model

/**
 * User-specific mutable mission state produced by the engine.
 *
 * `progress` is normalized to the `0f..1f` range so UI and persistence layers
 * can work with a consistent value regardless of mission type.
 */
data class MissionProgress(
    val missionId: String,
    val progress: Float,
    val status: MissionStatus,
    val claimedAt: Long? = null
)

/**
 * Lifecycle of a user mission instance.
 */
enum class MissionStatus {
    Active,
    Completed,
    Claimed
}

/**
 * UI-friendly pairing between a mission definition and the user's current progress.
 */
data class MissionCardModel(
    val definition: MissionDefinition,
    val progress: MissionProgress
)

/**
 * Aggregated mission dashboard consumed by the Mission feature.
 *
 * The dashboard intentionally mixes gamification header data with grouped
 * mission cards so the feature layer can stay thin and render-oriented.
 */
data class MissionDashboard(
    val level: Int,
    val levelTitle: String,
    val totalXp: Int,
    val currentLevelXp: Int,
    val nextLevelXp: Int,
    val currentStreak: Int,
    val shieldCount: Int,
    val dailyMissions: List<MissionCardModel>,
    val weeklyMissions: List<MissionCardModel>,
    val oneTimeMissions: List<MissionCardModel>,
    val challengePreview: ChallengePreview?
)

/**
 * Optional hero content shown alongside the mission dashboard.
 */
data class ChallengePreview(
    val title: String,
    val description: String,
    val rewardText: String
)

data class LevelReward(
    val level: Int,
    val title: String,
    val iconResId: Int? = null,
    val isUnlocked: Boolean = false
)
