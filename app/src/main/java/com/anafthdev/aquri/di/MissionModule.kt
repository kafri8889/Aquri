package com.anafthdev.aquri.di

import com.anafthdev.aquri.core.mission.MissionDefinitionSource
import com.anafthdev.aquri.core.mission.MissionProgressStore
import com.anafthdev.aquri.core.mission.evaluator.CurrentStreakDaysMissionEvaluator
import com.anafthdev.aquri.core.mission.evaluator.DailyGoalPercentageMissionEvaluator
import com.anafthdev.aquri.core.mission.evaluator.DrinkTypeLogCountMissionEvaluator
import com.anafthdev.aquri.core.mission.evaluator.GenericCounterMissionEvaluator
import com.anafthdev.aquri.core.mission.evaluator.HydrationAmountMissionEvaluator
import com.anafthdev.aquri.core.mission.evaluator.HydrationLogCountMissionEvaluator
import com.anafthdev.aquri.core.mission.evaluator.HydrationTimeWindowMissionEvaluator
import com.anafthdev.aquri.core.mission.evaluator.LargeDrinkCountMissionEvaluator
import com.anafthdev.aquri.core.mission.evaluator.MissionEvaluator
import com.anafthdev.aquri.core.mission.evaluator.ProfileCompletionMissionEvaluator
import com.anafthdev.aquri.core.mission.evaluator.WeeklyGoalDaysMissionEvaluator
import com.anafthdev.aquri.data.mission.InMemoryMissionDefinitionSource
import com.anafthdev.aquri.data.mission.RoomMissionProgressStore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class MissionBindingsModule {

    @Binds
    abstract fun bindMissionDefinitionSource(
        source: InMemoryMissionDefinitionSource
    ): MissionDefinitionSource

    @Binds
    abstract fun bindMissionProgressStore(
        store: RoomMissionProgressStore
    ): MissionProgressStore
}

@Module
@InstallIn(SingletonComponent::class)
object MissionModule {

    @Provides
    fun provideMissionEvaluators(
        hydrationTimeWindowMissionEvaluator: HydrationTimeWindowMissionEvaluator,
        hydrationLogCountMissionEvaluator: HydrationLogCountMissionEvaluator,
        dailyGoalPercentageMissionEvaluator: DailyGoalPercentageMissionEvaluator,
        weeklyGoalDaysMissionEvaluator: WeeklyGoalDaysMissionEvaluator,
        hydrationAmountMissionEvaluator: HydrationAmountMissionEvaluator,
        drinkTypeLogCountMissionEvaluator: DrinkTypeLogCountMissionEvaluator,
        largeDrinkCountMissionEvaluator: LargeDrinkCountMissionEvaluator,
        currentStreakDaysMissionEvaluator: CurrentStreakDaysMissionEvaluator,
        profileCompletionMissionEvaluator: ProfileCompletionMissionEvaluator,
        genericCounterMissionEvaluator: GenericCounterMissionEvaluator
    ): Set<MissionEvaluator> {
        return setOf(
            hydrationTimeWindowMissionEvaluator,
            hydrationLogCountMissionEvaluator,
            dailyGoalPercentageMissionEvaluator,
            weeklyGoalDaysMissionEvaluator,
            hydrationAmountMissionEvaluator,
            drinkTypeLogCountMissionEvaluator,
            largeDrinkCountMissionEvaluator,
            currentStreakDaysMissionEvaluator,
            profileCompletionMissionEvaluator,
            genericCounterMissionEvaluator
        )
    }
}
