package com.anafthdev.aquri.data.mission

import com.anafthdev.aquri.core.mission.MissionDefinitionSource
import com.anafthdev.aquri.core.mission.model.MissionCategory
import com.anafthdev.aquri.core.mission.model.MissionDefinition
import com.anafthdev.aquri.core.mission.model.MissionRecurrence
import com.anafthdev.aquri.core.mission.model.MissionReward
import com.anafthdev.aquri.core.mission.model.MissionTrigger
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Temporary in-memory mission definition source.
 *
 * This class exists to validate the mission engine contract before a Room or
 * backend-backed implementation is introduced.
 */
@Singleton
class InMemoryMissionDefinitionSource @Inject constructor() : MissionDefinitionSource {

    private val definitions = MutableStateFlow(
        listOf(
            MissionDefinition(
                id = "daily_evening_window",
                title = "Drink between 18:00-21:00",
                description = "Any hydration log inside the evening window counts.",
                category = MissionCategory.Hydration,
                recurrence = MissionRecurrence.Daily,
                reward = MissionReward(xp = 20, coins = 5),
                trigger = MissionTrigger.HydrationTimeWindow(
                    startHourInclusive = 18,
                    endHourExclusive = 21,
                    requiredLogs = 1
                )
            ),
            MissionDefinition(
                id = "daily_log_count",
                title = "Log 6 hydration entries",
                description = "Build a steady drinking rhythm through the day.",
                category = MissionCategory.Hydration,
                recurrence = MissionRecurrence.Daily,
                reward = MissionReward(xp = 25, coins = 8),
                trigger = MissionTrigger.HydrationLogCount(requiredLogs = 6)
            ),
            MissionDefinition(
                id = "daily_goal_80",
                title = "Reach 80% of daily goal",
                description = "Stay close to your target before the day ends.",
                category = MissionCategory.Hydration,
                recurrence = MissionRecurrence.Daily,
                reward = MissionReward(xp = 15, coins = 4),
                trigger = MissionTrigger.DailyGoalPercentage(targetPercentage = 0.8f)
            ),
            MissionDefinition(
                id = "weekly_goal_days",
                title = "Hit daily goal 5 days this week",
                description = "Consistency over the whole week.",
                category = MissionCategory.Hydration,
                recurrence = MissionRecurrence.Weekly,
                reward = MissionReward(xp = 100, coins = 30),
                trigger = MissionTrigger.WeeklyGoalDays(requiredDays = 5)
            ),
            MissionDefinition(
                id = "onetime_first_evening_log",
                title = "First evening hydration",
                description = "Log a drink between 18:00-21:00 for the first time.",
                category = MissionCategory.Hydration,
                recurrence = MissionRecurrence.OneTime,
                reward = MissionReward(xp = 40, coins = 20),
                trigger = MissionTrigger.HydrationTimeWindow(
                    startHourInclusive = 18,
                    endHourExclusive = 21,
                    requiredLogs = 1
                )
            )
        )
    )

    override fun observeDefinitions(): Flow<List<MissionDefinition>> = definitions

    /**
     * Stub refresh entry point matching the production contract.
     */
    override suspend fun refresh() {
        delay(150)
    }
}
