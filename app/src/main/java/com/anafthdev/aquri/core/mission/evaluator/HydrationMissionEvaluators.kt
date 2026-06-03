package com.anafthdev.aquri.core.mission.evaluator

import com.anafthdev.aquri.core.mission.engine.MissionEvaluationContext
import com.anafthdev.aquri.core.mission.model.MissionDefinition
import com.anafthdev.aquri.core.mission.model.MissionRecurrence
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
        return logsForRecurrence(definition, context).size / trigger.requiredLogs.toFloat()
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
        val summaries = when (definition.recurrence) {
            MissionRecurrence.OneTime,
            MissionRecurrence.Monthly,
            MissionRecurrence.Event -> context.allSummaries
            MissionRecurrence.Daily,
            MissionRecurrence.Weekly -> context.weekSummaries
        }
        val matchedDays = summaries.count { it.goalReached }
        return matchedDays / trigger.requiredDays.toFloat()
    }
}

/**
 * Evaluates missions based on total hydration amount in the active period.
 */
class HydrationAmountMissionEvaluator @Inject constructor() : BaseMissionEvaluator() {
    override fun supports(definition: MissionDefinition): Boolean {
        return definition.trigger is MissionTrigger.HydrationAmountMl
    }

    override fun calculateProgress(
        definition: MissionDefinition,
        context: MissionEvaluationContext
    ): Float {
        val trigger = definition.trigger as MissionTrigger.HydrationAmountMl
        val totalMl = when (definition.recurrence) {
            MissionRecurrence.Daily -> context.todaySummary?.totalMl
                ?: context.todayLogs.sumOf { it.log.amountMl.toDouble() }.toFloat()
            MissionRecurrence.Weekly -> context.weekSummaries.sumOf { it.totalMl.toDouble() }.toFloat()
            MissionRecurrence.OneTime,
            MissionRecurrence.Monthly,
            MissionRecurrence.Event -> context.allSummaries
                .sumOf { it.totalMl.toDouble() }
                .toFloat()
                .takeIf { it > 0f }
                ?: context.allLogs.sumOf { it.log.amountMl.toDouble() }.toFloat()
        }

        return totalMl / trigger.requiredMl.coerceAtLeast(1f)
    }
}

/**
 * Evaluates missions that require a specific drink type by display name.
 */
class DrinkTypeLogCountMissionEvaluator @Inject constructor() : BaseMissionEvaluator() {
    override fun supports(definition: MissionDefinition): Boolean {
        return definition.trigger is MissionTrigger.DrinkTypeLogCount
    }

    override fun calculateProgress(
        definition: MissionDefinition,
        context: MissionEvaluationContext
    ): Float {
        val trigger = definition.trigger as MissionTrigger.DrinkTypeLogCount
        val matchingLogs = logsForRecurrence(definition, context).count { log ->
            log.drinkType?.name.equals(trigger.drinkTypeName, ignoreCase = true)
        }

        return matchingLogs / trigger.requiredLogs.coerceAtLeast(1).toFloat()
    }
}

/**
 * Evaluates missions that require large single-drink logs.
 */
class LargeDrinkCountMissionEvaluator @Inject constructor() : BaseMissionEvaluator() {
    override fun supports(definition: MissionDefinition): Boolean {
        return definition.trigger is MissionTrigger.LargeDrinkCount
    }

    override fun calculateProgress(
        definition: MissionDefinition,
        context: MissionEvaluationContext
    ): Float {
        val trigger = definition.trigger as MissionTrigger.LargeDrinkCount
        val matchingLogs = logsForRecurrence(definition, context).count { log ->
            log.log.amountMl >= trigger.minAmountMl
        }

        return matchingLogs / trigger.requiredLogs.coerceAtLeast(1).toFloat()
    }
}

/**
 * Evaluates missions against the persisted gamification streak.
 */
class CurrentStreakDaysMissionEvaluator @Inject constructor() : BaseMissionEvaluator() {
    override fun supports(definition: MissionDefinition): Boolean {
        return definition.trigger is MissionTrigger.CurrentStreakDays
    }

    override fun calculateProgress(
        definition: MissionDefinition,
        context: MissionEvaluationContext
    ): Float {
        val trigger = definition.trigger as MissionTrigger.CurrentStreakDays
        val currentStreak = context.gamification?.currentStreak ?: 0
        return currentStreak / trigger.requiredDays.coerceAtLeast(1).toFloat()
    }
}

/**
 * Evaluates whether the local hydration profile has enough data for Aquri.
 */
class ProfileCompletionMissionEvaluator @Inject constructor() : BaseMissionEvaluator() {
    override fun supports(definition: MissionDefinition): Boolean {
        return definition.trigger is MissionTrigger.ProfileCompletion
    }

    override fun calculateProgress(
        definition: MissionDefinition,
        context: MissionEvaluationContext
    ): Float {
        val completedFields = listOf(
            context.user.name.isNotBlank(),
            context.user.weightKg > 0f,
            context.user.dailyGoalMl > 0f,
            context.user.updatedAt > 0L
        ).count { it }

        return completedFields / 4f
    }
}

private fun logsForRecurrence(
    definition: MissionDefinition,
    context: MissionEvaluationContext
) = when (definition.recurrence) {
    MissionRecurrence.Daily -> context.todayLogs
    MissionRecurrence.Weekly -> context.weekLogs
    MissionRecurrence.OneTime,
    MissionRecurrence.Monthly,
    MissionRecurrence.Event -> context.allLogs
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
