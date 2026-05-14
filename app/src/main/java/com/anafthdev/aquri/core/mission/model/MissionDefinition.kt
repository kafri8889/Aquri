package com.anafthdev.aquri.core.mission.model

/**
 * Immutable mission definition consumed by the mission engine.
 *
 * This model is intended to map cleanly from a remote/backend payload.
 * It describes what the mission is, how often it resets, what reward it gives,
 * and which trigger family is responsible for evaluating progress.
 */
data class MissionDefinition(
    val id: String,
    val title: String,
    val description: String,
    val category: MissionCategory,
    val recurrence: MissionRecurrence,
    val reward: MissionReward,
    val trigger: MissionTrigger,
    val isActive: Boolean = true
)

/**
 * High-level mission grouping.
 *
 * The category is mostly descriptive at the moment, but it is intended to
 * support filtering, analytics, and future non-hydration mission families.
 */
enum class MissionCategory {
    Hydration,
    Wellness,
    Social,
    System
}

/**
 * Defines the reset cadence and dashboard grouping for a mission.
 *
 * Current product-supported types are `Daily`, `Weekly`, and `OneTime`.
 * `Monthly` and `Event` remain available for future expansion.
 */
enum class MissionRecurrence {
    Daily,
    Weekly,
    OneTime,
    Monthly,
    Event
}

/**
 * Reward payload granted after a mission is claimed.
 *
 * This is intentionally compact. More fields such as badges or items can be
 * added later without changing the evaluator contracts.
 *
 * A mission may grant XP, coins, or both at the same time.
 */
data class MissionReward(
    val xp: Int = 0,
    val coins: Int = 0
)

/**
 * Rule payload used by [com.anafthdev.aquri.core.mission.evaluator.MissionEvaluator]
 * implementations to calculate mission progress.
 *
 * Each subtype should map to a dedicated evaluator. This keeps rule logic
 * isolated and allows the backend to define missions declaratively.
 */
sealed interface MissionTrigger {

    /**
     * Requires one or more hydration logs inside a local-time window.
     */
    data class HydrationTimeWindow(
        val startHourInclusive: Int,
        val endHourExclusive: Int,
        val requiredLogs: Int = 1
    ) : MissionTrigger

    /**
     * Requires a certain number of hydration logs in the active period.
     */
    data class HydrationLogCount(
        val requiredLogs: Int
    ) : MissionTrigger

    /**
     * Requires the user to reach a percentage of their current daily goal.
     */
    data class DailyGoalPercentage(
        val targetPercentage: Float
    ) : MissionTrigger

    /**
     * Requires the user to hit their daily goal on a number of days within a week.
     */
    data class WeeklyGoalDays(
        val requiredDays: Int
    ) : MissionTrigger

    /**
     * Generic counter reserved for future non-hydration event streams.
     */
    data class GenericCounter(
        val eventName: String,
        val requiredCount: Int
    ) : MissionTrigger
}
