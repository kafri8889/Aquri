package com.anafthdev.aquri.core.mission.evaluator

import com.anafthdev.aquri.core.mission.engine.MissionEvaluationContext
import com.anafthdev.aquri.core.mission.model.MissionDefinition
import com.anafthdev.aquri.core.mission.model.MissionTrigger
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

/**
 * Evaluates missions that require hydration logs inside a specific local-time window.
 */
class HydrationTimeWindowMissionEvaluator @Inject constructor() : BaseMissionEvaluator() {
    override fun supports(definition: MissionDefinition): Boolean {
        return definition.trigger is MissionTrigger.HydrationTimeWindow
    }

    override fun calculateProgress(
        definition: MissionDefinition,
        context: MissionEvaluationContext
    ): Float {
        val trigger = definition.trigger as MissionTrigger.HydrationTimeWindow
        val matchingLogs = context.todayLogs.count { log ->
            val localTime = Instant.ofEpochMilli(log.log.loggedAt)
                .atZone(ZoneId.systemDefault())
                .toLocalTime()

            !localTime.isBefore(LocalTime.of(trigger.startHourInclusive, 0)) &&
                localTime.isBefore(LocalTime.of(trigger.endHourExclusive, 0))
        }

        return matchingLogs / trigger.requiredLogs.toFloat()
    }
}

/**
 * Evaluates missions based on the number of hydration logs in the current day.
 */
class HydrationLogCountMissionEvaluator @Inject constructor() : BaseMissionEvaluator() {
    override fun supports(definition: MissionDefinition): Boolean {
        return definition.trigger is MissionTrigger.HydrationLogCount
    }

    override fun calculateProgress(
        definition: MissionDefinition,
        context: MissionEvaluationContext
    ): Float {
        val trigger = definition.trigger as MissionTrigger.HydrationLogCount
        return context.todayLogs.size / trigger.requiredLogs.toFloat()
    }
}

/**
 * Evaluates missions based on completion percentage of the user's daily goal.
 */
class DailyGoalPercentageMissionEvaluator @Inject constructor() : BaseMissionEvaluator() {
    override fun supports(definition: MissionDefinition): Boolean {
        return definition.trigger is MissionTrigger.DailyGoalPercentage
    }

    override fun calculateProgress(
        definition: MissionDefinition,
        context: MissionEvaluationContext
    ): Float {
        val trigger = definition.trigger as MissionTrigger.DailyGoalPercentage
        val totalMl = context.todaySummary?.totalMl ?: 0f
        val targetMl = (context.user.dailyGoalMl * trigger.targetPercentage).coerceAtLeast(1f)
        return totalMl / targetMl
    }
}

/**
 * Evaluates weekly consistency missions by counting goal-reached days in the current week.
 */
class WeeklyGoalDaysMissionEvaluator @Inject constructor() : BaseMissionEvaluator() {
    override fun supports(definition: MissionDefinition): Boolean {
        return definition.trigger is MissionTrigger.WeeklyGoalDays
    }

    override fun calculateProgress(
        definition: MissionDefinition,
        context: MissionEvaluationContext
    ): Float {
        val trigger = definition.trigger as MissionTrigger.WeeklyGoalDays
        val matchedDays = context.weekSummaries.count { it.goalReached }
        return matchedDays / trigger.requiredDays.toFloat()
    }
}

/**
 * Placeholder evaluator for future generic event-counter missions.
 *
 * It currently returns `0f` because the app does not yet persist or aggregate
 * generic event counts.
 */
class GenericCounterMissionEvaluator @Inject constructor() : BaseMissionEvaluator() {
    override fun supports(definition: MissionDefinition): Boolean {
        return definition.trigger is MissionTrigger.GenericCounter
    }

    override fun calculateProgress(
        definition: MissionDefinition,
        context: MissionEvaluationContext
    ): Float {
        return 0f
    }
}
