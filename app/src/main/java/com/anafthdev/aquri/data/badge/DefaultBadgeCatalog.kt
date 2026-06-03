package com.anafthdev.aquri.data.badge

import com.anafthdev.aquri.data.model.entity.BadgeEntity
import com.anafthdev.aquri.data.model.enum.BadgeCategory
import com.anafthdev.aquri.data.model.enum.Rarity
import java.nio.charset.StandardCharsets
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultBadgeCatalog @Inject constructor() {

    fun badges(): List<BadgeEntity> = listOf(
        badge(
            key = "first_drop",
            name = "FIRST DROP",
            description = "Log your first drink in Aquri.",
            category = BadgeCategory.Milestone,
            rarity = Rarity.Common,
            icon = "first_drop.png"
        ),
        badge(
            key = "profile_ready",
            name = "PROFILE READY",
            description = "Complete the hydration profile Aquri uses.",
            category = BadgeCategory.Milestone,
            rarity = Rarity.Common,
            icon = "profile_ready.png"
        ),
        badge(
            key = "goal_crusher",
            name = "GOAL CRUSHER",
            description = "Reach your daily hydration goal.",
            category = BadgeCategory.Habit,
            rarity = Rarity.Common,
            icon = "goal_crusher.png"
        ),
        badge(
            key = "three_day_flow",
            name = "THREE-DAY FLOW",
            description = "Build a 3-day hydration streak.",
            category = BadgeCategory.Habit,
            rarity = Rarity.Rare,
            icon = "three_day_flow.png"
        ),
        badge(
            key = "week_wave",
            name = "WEEK WAVE",
            description = "Build a 7-day hydration streak.",
            category = BadgeCategory.Habit,
            rarity = Rarity.Epic,
            icon = "week_wave.png"
        ),
        badge(
            key = "ocean_habit",
            name = "OCEAN HABIT",
            description = "Build a 30-day hydration streak.",
            category = BadgeCategory.Habit,
            rarity = Rarity.Legendary,
            icon = "ocean_habit.png"
        ),
        badge(
            key = "deep_hydration",
            name = "DEEP HYDRATION",
            description = "Drink 1,500 ml in one day.",
            category = BadgeCategory.Milestone,
            rarity = Rarity.Rare,
            icon = "deep_hydration.png"
        ),
        badge(
            key = "hundred_logs",
            name = "HUNDRED LOGS",
            description = "Log 100 hydration entries.",
            category = BadgeCategory.Milestone,
            rarity = Rarity.Epic,
            icon = "hundred_logs.png"
        ),
        badge(
            key = "early_bird",
            name = "EARLY BIRD",
            description = "Log a morning drink.",
            category = BadgeCategory.Habit,
            rarity = Rarity.Common,
            icon = "early_bird.png"
        ),
        badge(
            key = "power_refill",
            name = "POWER REFILL",
            description = "Log several large refills.",
            category = BadgeCategory.Habit,
            rarity = Rarity.Rare,
            icon = "power_refill.png"
        ),
        badge(
            key = "aquri_founder",
            name = "AQURI FOUNDER",
            description = "Special early Aquri badge.",
            category = BadgeCategory.SpecialEvent,
            rarity = Rarity.Mythic
        ),
        badge(
            key = "midnight_sip",
            name = "MIDNIGHT SIP",
            description = "Log a drink between 12 AM and 4 AM.",
            category = BadgeCategory.Habit,
            rarity = Rarity.Rare,
            icon = "midnight_sip.png"
        ),
        badge(
            key = "steady_stream",
            name = "STEADY STREAM",
            description = "Log at least 5 drinks in a single day.",
            category = BadgeCategory.Habit,
            rarity = Rarity.Common,
            icon = "steady_stream.png"
        ),
        badge(
            key = "hydration_hero",
            name = "HYDRATION HERO",
            description = "Reach your goal for 14 consecutive days.",
            category = BadgeCategory.Habit,
            rarity = Rarity.Epic,
            icon = "hydration_hero.png"
        ),
        badge(
            key = "liquid_legend",
            name = "LIQUID LEGEND",
            description = "Reach your goal for 100 consecutive days.",
            category = BadgeCategory.Habit,
            rarity = Rarity.Mythic,
            icon = "liquid_legend.png"
        ),
        badge(
            key = "water_warrior",
            name = "WATER WARRIOR",
            description = "Drink 3,000 ml in one day.",
            category = BadgeCategory.Milestone,
            rarity = Rarity.Epic,
            icon = "water_warrior.png"
        ),
        badge(
            key = "sea_of_logs",
            name = "SEA OF LOGS",
            description = "Log 500 hydration entries.",
            category = BadgeCategory.Milestone,
            rarity = Rarity.Legendary,
            icon = "sea_of_logs.png"
        ),
        badge(
            key = "perfect_week",
            name = "PERFECT WEEK",
            description = "Reach your daily goal every day for a full week.",
            category = BadgeCategory.Habit,
            rarity = Rarity.Rare,
            icon = "perfect_week.png"
        ),
        badge(
            key = "morning_ritual",
            name = "MORNING RITUAL",
            description = "Log a drink before 8 AM for 5 days in a row.",
            category = BadgeCategory.Habit,
            rarity = Rarity.Rare,
            icon = "morning_ritual.png"
        ),
        badge(
            key = "weekend_wave",
            name = "WEEKEND WAVE",
            description = "Reach your goal on both Saturday and Sunday.",
            category = BadgeCategory.Habit,
            rarity = Rarity.Common,
            icon = "weekend_wave.png"
        ),
        badge(
            key = "social_splasher",
            name = "SOCIAL SPLASHER",
            description = "Share your hydration progress with a friend.",
            category = BadgeCategory.Social,
            rarity = Rarity.Common,
            icon = "social_splasher.png"
        ),
        badge(
            key = "hydro_helper",
            name = "HYDRO HELPER",
            description = "Invite a friend to join Aquri.",
            category = BadgeCategory.Social,
            rarity = Rarity.Rare,
            icon = "hydro_helper.png"
        ),
        badge(
            key = "community_circle",
            name = "COMMUNITY CIRCLE",
            description = "Join your first hydration challenge.",
            category = BadgeCategory.Social,
            rarity = Rarity.Epic,
            icon = "community_circle.png"
        ),
        badge(
            key = "summer_splash",
            name = "SUMMER SPLASH",
            description = "Stay hydrated during the peak of summer.",
            category = BadgeCategory.SpecialEvent,
            rarity = Rarity.Rare,
            icon = "summer_splash.png"
        ),
        badge(
            key = "winter_well",
            name = "WINTER WELL",
            description = "Maintain hydration during the cold winter months.",
            category = BadgeCategory.SpecialEvent,
            rarity = Rarity.Rare,
            icon = "winter_well.png"
        ),
        badge(
            key = "new_year_new_flow",
            name = "NEW YEAR NEW FLOW",
            description = "Log a drink on New Year's Day.",
            category = BadgeCategory.SpecialEvent,
            rarity = Rarity.Common,
            icon = "new_year_new_flow.png"
        ),
        badge(
            key = "birthday_bottle",
            name = "BIRTHDAY BOTTLE",
            description = "Celebrate your birthday with a refreshing drink.",
            category = BadgeCategory.SpecialEvent,
            rarity = Rarity.Epic,
            icon = "birthday_bottle.png"
        ),
        badge(
            key = "consistency_king",
            name = "CONSISTENCY KING",
            description = "Log at least one drink every day for 6 months.",
            category = BadgeCategory.Habit,
            rarity = Rarity.Legendary,
            icon = "consistency_king.png"
        ),
        badge(
            key = "pro_hydrator",
            name = "PRO HYDRATOR",
            description = "Unlock all basic hydration features.",
            category = BadgeCategory.Milestone,
            rarity = Rarity.Rare,
            isProOnly = true,
            icon = "pro_hydrator.png"
        )
    )

    private fun badge(
        key: String,
        name: String,
        description: String,
        category: BadgeCategory,
        rarity: Rarity,
        isProOnly: Boolean = false,
        icon: String = ""
    ) = BadgeEntity(
        id = UUID.nameUUIDFromBytes("aquri_badge_$key".toByteArray(StandardCharsets.UTF_8)),
        name = name,
        description = description,
        iconUrl = if (icon.isNotEmpty()) "images/badges/$icon" else "",
        category = category,
        rarity = rarity,
        isProOnly = isProOnly
    )
}
