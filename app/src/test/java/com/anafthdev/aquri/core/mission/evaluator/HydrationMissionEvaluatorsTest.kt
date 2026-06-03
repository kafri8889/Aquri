package com.anafthdev.aquri.core.mission.evaluator

import com.anafthdev.aquri.core.mission.engine.MissionEvaluationContext
import com.anafthdev.aquri.core.mission.model.MissionCategory
import com.anafthdev.aquri.core.mission.model.MissionDefinition
import com.anafthdev.aquri.core.mission.model.MissionRecurrence
import com.anafthdev.aquri.core.mission.model.MissionReward
import com.anafthdev.aquri.core.mission.model.MissionStatus
import com.anafthdev.aquri.core.mission.model.MissionTrigger
import com.anafthdev.aquri.data.model.entity.DailySummaryEntity
import com.anafthdev.aquri.data.model.entity.DrinkTypeEntity
import com.anafthdev.aquri.data.model.entity.HydrationLogEntity
import com.anafthdev.aquri.data.model.entity.HydrationLogWithBottle
import com.anafthdev.aquri.data.model.entity.UserEntity
import com.anafthdev.aquri.data.model.entity.UserGamificationEntity
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale
import java.util.UUID

class HydrationMissionEvaluatorsTest {

    private val userId = UUID.fromString("00000000-0000-0000-0000-000000000111")
    private val water = DrinkTypeEntity.predefinedDrinkTypes.first { it.name == "Water" }
    private val tea = DrinkTypeEntity.predefinedDrinkTypes.first { it.name == "Tea" }

    @Test
    fun hydrationAmountMl_usesDailySummaryProgress() {
        val card = HydrationAmountMissionEvaluator().evaluate(
            definition = mission(
                trigger = MissionTrigger.HydrationAmountMl(requiredMl = 1000f),
                recurrence = MissionRecurrence.Daily
            ),
            context = context(todaySummary = summary(totalMl = 500f)),
            claimedAt = null
        )

        assertEquals(MissionStatus.Active, card.progress.status)
        assertEquals(0.5f, card.progress.progress, 0.001f)
    }

    @Test
    fun drinkTypeLogCount_matchesDrinkNameCaseInsensitive() {
        val card = DrinkTypeLogCountMissionEvaluator().evaluate(
            definition = mission(
                trigger = MissionTrigger.DrinkTypeLogCount(
                    drinkTypeName = "water",
                    requiredLogs = 2
                ),
                recurrence = MissionRecurrence.Daily
            ),
            context = context(
                todayLogs = listOf(
                    logWithDrink(water, amountMl = 200f),
                    logWithDrink(water, amountMl = 330f),
                    logWithDrink(tea, amountMl = 250f)
                )
            ),
            claimedAt = null
        )

        assertEquals(MissionStatus.Completed, card.progress.status)
        assertEquals(1f, card.progress.progress, 0.001f)
    }

    @Test
    fun largeDrinkCount_countsLogsAtOrAboveMinimum() {
        val card = LargeDrinkCountMissionEvaluator().evaluate(
            definition = mission(
                trigger = MissionTrigger.LargeDrinkCount(
                    minAmountMl = 500f,
                    requiredLogs = 2
                ),
                recurrence = MissionRecurrence.Daily
            ),
            context = context(
                todayLogs = listOf(
                    logWithDrink(water, amountMl = 499f),
                    logWithDrink(water, amountMl = 500f),
                    logWithDrink(water, amountMl = 750f)
                )
            ),
            claimedAt = null
        )

        assertEquals(MissionStatus.Completed, card.progress.status)
        assertEquals(1f, card.progress.progress, 0.001f)
    }

    @Test
    fun currentStreakDays_usesGamificationStreak() {
        val card = CurrentStreakDaysMissionEvaluator().evaluate(
            definition = mission(
                trigger = MissionTrigger.CurrentStreakDays(requiredDays = 10),
                recurrence = MissionRecurrence.OneTime
            ),
            context = context(
                gamification = UserGamificationEntity(
                    userId = userId,
                    currentStreak = 5
                )
            ),
            claimedAt = null
        )

        assertEquals(MissionStatus.Active, card.progress.status)
        assertEquals(0.5f, card.progress.progress, 0.001f)
    }

    @Test
    fun profileCompletion_usesLocalHydrationProfileFields() {
        val card = ProfileCompletionMissionEvaluator().evaluate(
            definition = mission(
                trigger = MissionTrigger.ProfileCompletion,
                recurrence = MissionRecurrence.OneTime
            ),
            context = context(
                user = UserEntity(
                    id = userId,
                    name = "Elena",
                    weightKg = 56f,
                    dailyGoalMl = 2050f
                )
            ),
            claimedAt = null
        )

        assertEquals(MissionStatus.Completed, card.progress.status)
        assertEquals(1f, card.progress.progress, 0.001f)
    }

    private fun mission(
        trigger: MissionTrigger,
        recurrence: MissionRecurrence
    ) = MissionDefinition(
        id = "test_mission",
        title = "Test Mission",
        description = "Test",
        category = MissionCategory.Hydration,
        recurrence = recurrence,
        reward = MissionReward(xp = 1),
        trigger = trigger
    )

    private fun context(
        user: UserEntity = UserEntity(id = userId, dailyGoalMl = 2000f),
        todayLogs: List<HydrationLogWithBottle> = emptyList(),
        todaySummary: DailySummaryEntity? = null,
        gamification: UserGamificationEntity? = null
    ) = MissionEvaluationContext(
        user = user,
        now = 0L,
        locale = Locale.US,
        todayLogs = todayLogs,
        weekLogs = todayLogs,
        allLogs = todayLogs,
        todaySummary = todaySummary,
        weekSummaries = todaySummary?.let { listOf(it) } ?: emptyList(),
        allSummaries = todaySummary?.let { listOf(it) } ?: emptyList(),
        gamification = gamification
    )

    private fun summary(totalMl: Float) = DailySummaryEntity(
        userId = userId,
        summaryDate = 0L,
        totalMl = totalMl,
        goalMl = 2000f,
        completionPct = totalMl / 2000f,
        goalReached = totalMl >= 2000f
    )

    private fun logWithDrink(
        drinkType: DrinkTypeEntity,
        amountMl: Float
    ) = HydrationLogWithBottle(
        log = HydrationLogEntity(
            userId = userId,
            amountMl = amountMl,
            drinkTypeId = drinkType.id,
            loggedAt = 0L,
            logDate = 0L
        ),
        bottle = null,
        drinkType = drinkType
    )
}
