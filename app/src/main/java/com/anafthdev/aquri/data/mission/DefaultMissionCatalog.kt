package com.anafthdev.aquri.data.mission

import com.anafthdev.aquri.core.mission.model.MissionCategory
import com.anafthdev.aquri.core.mission.model.MissionDefinition
import com.anafthdev.aquri.core.mission.model.MissionRecurrence
import com.anafthdev.aquri.core.mission.model.MissionReward
import com.anafthdev.aquri.core.mission.model.MissionTrigger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultMissionCatalog @Inject constructor() {

    fun missions(): List<MissionDefinition> {
        return dailyMissions() + weeklyMissions() + milestoneMissions()
    }

    private fun dailyMissions() = listOf(
        mission(
            id = "daily_first_sip",
            title = "First Sip",
            description = "Log your first drink today.",
            recurrence = MissionRecurrence.Daily,
            reward = MissionReward(xp = 10, coins = 3),
            trigger = MissionTrigger.HydrationLogCount(requiredLogs = 1)
        ),
        mission(
            id = "daily_three_logs",
            title = "Steady Flow",
            description = "Log 3 hydration entries today.",
            recurrence = MissionRecurrence.Daily,
            reward = MissionReward(xp = 18, coins = 5),
            trigger = MissionTrigger.HydrationLogCount(requiredLogs = 3)
        ),
        mission(
            id = "daily_six_logs",
            title = "Rhythm Keeper",
            description = "Log 6 hydration entries today.",
            recurrence = MissionRecurrence.Daily,
            reward = MissionReward(xp = 28, coins = 8),
            trigger = MissionTrigger.HydrationLogCount(requiredLogs = 6)
        ),
        mission(
            id = "daily_morning_window",
            title = "Morning Prime",
            description = "Log a drink between 06:00-10:00.",
            recurrence = MissionRecurrence.Daily,
            reward = MissionReward(xp = 18, coins = 5),
            trigger = MissionTrigger.HydrationTimeWindow(
                startHourInclusive = 6,
                endHourExclusive = 10
            )
        ),
        mission(
            id = "daily_afternoon_window",
            title = "Afternoon Balance",
            description = "Log a drink between 12:00-16:00.",
            recurrence = MissionRecurrence.Daily,
            reward = MissionReward(xp = 18, coins = 5),
            trigger = MissionTrigger.HydrationTimeWindow(
                startHourInclusive = 12,
                endHourExclusive = 16
            )
        ),
        mission(
            id = "daily_evening_window",
            title = "Evening Reset",
            description = "Log a drink between 18:00-21:00.",
            recurrence = MissionRecurrence.Daily,
            reward = MissionReward(xp = 20, coins = 5),
            trigger = MissionTrigger.HydrationTimeWindow(
                startHourInclusive = 18,
                endHourExclusive = 21
            )
        ),
        mission(
            id = "daily_500_ml",
            title = "Half-Liter Start",
            description = "Drink 500 ml today.",
            recurrence = MissionRecurrence.Daily,
            reward = MissionReward(xp = 15, coins = 4),
            trigger = MissionTrigger.HydrationAmountMl(requiredMl = 500f)
        ),
        mission(
            id = "daily_1500_ml",
            title = "Deep Hydration",
            description = "Drink 1,500 ml today.",
            recurrence = MissionRecurrence.Daily,
            reward = MissionReward(xp = 35, coins = 10),
            trigger = MissionTrigger.HydrationAmountMl(requiredMl = 1500f)
        ),
        mission(
            id = "daily_goal_50",
            title = "Halfway There",
            description = "Reach 50% of your daily goal.",
            recurrence = MissionRecurrence.Daily,
            reward = MissionReward(xp = 15, coins = 4),
            trigger = MissionTrigger.DailyGoalPercentage(targetPercentage = 0.5f)
        ),
        mission(
            id = "daily_goal_100",
            title = "Goal Crusher",
            description = "Reach your daily hydration goal.",
            recurrence = MissionRecurrence.Daily,
            reward = MissionReward(xp = 45, coins = 14),
            trigger = MissionTrigger.DailyGoalPercentage(targetPercentage = 1f)
        ),
        mission(
            id = "daily_large_drink",
            title = "Big Refill",
            description = "Log one drink of at least 500 ml.",
            recurrence = MissionRecurrence.Daily,
            reward = MissionReward(xp = 20, coins = 6),
            trigger = MissionTrigger.LargeDrinkCount(
                minAmountMl = 500f,
                requiredLogs = 1
            )
        ),
        mission(
            id = "daily_water_focus",
            title = "Water Focus",
            description = "Log 2 water entries today.",
            recurrence = MissionRecurrence.Daily,
            reward = MissionReward(xp = 20, coins = 6),
            trigger = MissionTrigger.DrinkTypeLogCount(
                drinkTypeName = "Water",
                requiredLogs = 2
            )
        )
    )

    private fun weeklyMissions() = listOf(
        mission(
            id = "weekly_goal_2_days",
            title = "Two-Day Spark",
            description = "Hit your daily goal on 2 days this week.",
            recurrence = MissionRecurrence.Weekly,
            reward = MissionReward(xp = 60, coins = 18),
            trigger = MissionTrigger.WeeklyGoalDays(requiredDays = 2)
        ),
        mission(
            id = "weekly_goal_3_days",
            title = "Weekly Rhythm",
            description = "Hit your daily goal on 3 days this week.",
            recurrence = MissionRecurrence.Weekly,
            reward = MissionReward(xp = 90, coins = 26),
            trigger = MissionTrigger.WeeklyGoalDays(requiredDays = 3)
        ),
        mission(
            id = "weekly_goal_5_days",
            title = "Consistency Core",
            description = "Hit your daily goal on 5 days this week.",
            recurrence = MissionRecurrence.Weekly,
            reward = MissionReward(xp = 130, coins = 38),
            trigger = MissionTrigger.WeeklyGoalDays(requiredDays = 5)
        ),
        mission(
            id = "weekly_7_liters",
            title = "Seven-Liter Week",
            description = "Drink 7,000 ml this week.",
            recurrence = MissionRecurrence.Weekly,
            reward = MissionReward(xp = 95, coins = 28),
            trigger = MissionTrigger.HydrationAmountMl(requiredMl = 7000f)
        ),
        mission(
            id = "weekly_10_liters",
            title = "Ten-Liter Week",
            description = "Drink 10,000 ml this week.",
            recurrence = MissionRecurrence.Weekly,
            reward = MissionReward(xp = 140, coins = 42),
            trigger = MissionTrigger.HydrationAmountMl(requiredMl = 10000f)
        ),
        mission(
            id = "weekly_20_logs",
            title = "Log Master",
            description = "Log 20 hydration entries this week.",
            recurrence = MissionRecurrence.Weekly,
            reward = MissionReward(xp = 110, coins = 32),
            trigger = MissionTrigger.HydrationLogCount(requiredLogs = 20)
        ),
        mission(
            id = "weekly_large_drinks",
            title = "Power Refills",
            description = "Log 5 drinks of at least 500 ml this week.",
            recurrence = MissionRecurrence.Weekly,
            reward = MissionReward(xp = 100, coins = 30),
            trigger = MissionTrigger.LargeDrinkCount(
                minAmountMl = 500f,
                requiredLogs = 5
            )
        ),
        mission(
            id = "weekly_tea_breaks",
            title = "Tea Breaks",
            description = "Log 3 tea entries this week.",
            recurrence = MissionRecurrence.Weekly,
            reward = MissionReward(xp = 70, coins = 20),
            trigger = MissionTrigger.DrinkTypeLogCount(
                drinkTypeName = "Tea",
                requiredLogs = 3
            )
        )
    )

    private fun milestoneMissions() = listOf(
        mission(
            id = "milestone_profile_ready",
            title = "Profile Ready",
            description = "Complete the hydration profile Aquri uses.",
            recurrence = MissionRecurrence.OneTime,
            reward = MissionReward(xp = 50, coins = 20),
            trigger = MissionTrigger.ProfileCompletion
        ),
        mission(
            id = "milestone_first_log",
            title = "First Drop",
            description = "Log your first drink in Aquri.",
            recurrence = MissionRecurrence.OneTime,
            reward = MissionReward(xp = 35, coins = 12),
            trigger = MissionTrigger.HydrationLogCount(requiredLogs = 1)
        ),
        mission(
            id = "milestone_first_goal",
            title = "First Goal",
            description = "Reach a daily goal for the first time.",
            recurrence = MissionRecurrence.OneTime,
            reward = MissionReward(xp = 70, coins = 24),
            trigger = MissionTrigger.WeeklyGoalDays(requiredDays = 1)
        ),
        mission(
            id = "milestone_3_day_streak",
            title = "Three-Day Flow",
            description = "Build a 3-day hydration streak.",
            recurrence = MissionRecurrence.OneTime,
            reward = MissionReward(xp = 90, coins = 30),
            trigger = MissionTrigger.CurrentStreakDays(requiredDays = 3)
        ),
        mission(
            id = "milestone_7_day_streak",
            title = "Week Wave",
            description = "Build a 7-day hydration streak.",
            recurrence = MissionRecurrence.OneTime,
            reward = MissionReward(xp = 160, coins = 50),
            trigger = MissionTrigger.CurrentStreakDays(requiredDays = 7)
        ),
        mission(
            id = "milestone_14_day_streak",
            title = "Fortnight Flow",
            description = "Build a 14-day hydration streak.",
            recurrence = MissionRecurrence.OneTime,
            reward = MissionReward(xp = 260, coins = 80),
            trigger = MissionTrigger.CurrentStreakDays(requiredDays = 14)
        ),
        mission(
            id = "milestone_30_day_streak",
            title = "Ocean Habit",
            description = "Build a 30-day hydration streak.",
            recurrence = MissionRecurrence.OneTime,
            reward = MissionReward(xp = 500, coins = 150),
            trigger = MissionTrigger.CurrentStreakDays(requiredDays = 30)
        ),
        mission(
            id = "milestone_10_liters",
            title = "Ten-Liter Legacy",
            description = "Drink 10,000 ml across your Aquri journey.",
            recurrence = MissionRecurrence.OneTime,
            reward = MissionReward(xp = 120, coins = 40),
            trigger = MissionTrigger.HydrationAmountMl(requiredMl = 10000f)
        ),
        mission(
            id = "milestone_50_liters",
            title = "Fifty-Liter Legend",
            description = "Drink 50,000 ml across your Aquri journey.",
            recurrence = MissionRecurrence.OneTime,
            reward = MissionReward(xp = 350, coins = 110),
            trigger = MissionTrigger.HydrationAmountMl(requiredMl = 50000f)
        ),
        mission(
            id = "milestone_100_logs",
            title = "Hundred Logs",
            description = "Log 100 hydration entries.",
            recurrence = MissionRecurrence.OneTime,
            reward = MissionReward(xp = 320, coins = 100),
            trigger = MissionTrigger.HydrationLogCount(requiredLogs = 100)
        )
    )

    private fun mission(
        id: String,
        title: String,
        description: String,
        recurrence: MissionRecurrence,
        reward: MissionReward,
        trigger: MissionTrigger,
        category: MissionCategory = MissionCategory.Hydration
    ) = MissionDefinition(
        id = id,
        title = title,
        description = description,
        category = category,
        recurrence = recurrence,
        reward = reward,
        trigger = trigger
    )
}
