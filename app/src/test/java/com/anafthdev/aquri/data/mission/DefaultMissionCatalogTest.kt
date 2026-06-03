package com.anafthdev.aquri.data.mission

import com.anafthdev.aquri.core.mission.evaluator.CurrentStreakDaysMissionEvaluator
import com.anafthdev.aquri.core.mission.evaluator.DailyGoalPercentageMissionEvaluator
import com.anafthdev.aquri.core.mission.evaluator.DrinkTypeLogCountMissionEvaluator
import com.anafthdev.aquri.core.mission.evaluator.HydrationAmountMissionEvaluator
import com.anafthdev.aquri.core.mission.evaluator.HydrationLogCountMissionEvaluator
import com.anafthdev.aquri.core.mission.evaluator.HydrationTimeWindowMissionEvaluator
import com.anafthdev.aquri.core.mission.evaluator.LargeDrinkCountMissionEvaluator
import com.anafthdev.aquri.core.mission.evaluator.MissionEvaluator
import com.anafthdev.aquri.core.mission.evaluator.ProfileCompletionMissionEvaluator
import com.anafthdev.aquri.core.mission.evaluator.WeeklyGoalDaysMissionEvaluator
import com.anafthdev.aquri.core.mission.model.MissionRecurrence
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DefaultMissionCatalogTest {

    private val catalog = DefaultMissionCatalog()
    private val evaluators: List<MissionEvaluator> = listOf(
        HydrationTimeWindowMissionEvaluator(),
        HydrationLogCountMissionEvaluator(),
        DailyGoalPercentageMissionEvaluator(),
        WeeklyGoalDaysMissionEvaluator(),
        HydrationAmountMissionEvaluator(),
        DrinkTypeLogCountMissionEvaluator(),
        LargeDrinkCountMissionEvaluator(),
        CurrentStreakDaysMissionEvaluator(),
        ProfileCompletionMissionEvaluator()
    )

    @Test
    fun missions_haveUniqueIds() {
        val missions = catalog.missions()

        assertEquals(missions.size, missions.map { it.id }.toSet().size)
    }

    @Test
    fun missions_haveExpectedRecurrenceCounts() {
        val missions = catalog.missions()

        assertEquals(12, missions.count { it.recurrence == MissionRecurrence.Daily })
        assertEquals(8, missions.count { it.recurrence == MissionRecurrence.Weekly })
        assertEquals(10, missions.count { it.recurrence == MissionRecurrence.OneTime })
    }

    @Test
    fun allDefaultMissionsHaveEvaluatorSupport() {
        val unsupported = catalog.missions().filter { mission ->
            evaluators.none { it.supports(mission) }
        }

        assertTrue(unsupported.joinToString { it.id }, unsupported.isEmpty())
    }
}
