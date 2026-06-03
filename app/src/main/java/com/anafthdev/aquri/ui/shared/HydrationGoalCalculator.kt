package com.anafthdev.aquri.ui.shared

import com.anafthdev.aquri.data.model.enum.ActivityLevel
import com.anafthdev.aquri.data.model.enum.Climate
import com.anafthdev.aquri.data.model.enum.Gender
import kotlin.math.roundToInt

fun calculateHydrationGoalMl(
    gender: Gender,
    weightKg: Float,
    activityLevel: ActivityLevel,
    climate: Climate
): Float {
    if (weightKg <= 0f) return 0f

    val safeWeightKg = weightKg.coerceIn(20f, 300f)
    val base = when (gender) {
        Gender.Male -> safeWeightKg * 35f
        Gender.Female -> safeWeightKg * 31f
        Gender.Other -> safeWeightKg * 33f
    }
    val activityBonus = when (activityLevel) {
        ActivityLevel.Sedentary -> 0f
        ActivityLevel.Moderate -> 350f
        ActivityLevel.Active -> 700f
    }
    val climateAdjustment = when (climate) {
        Climate.Cold -> -100f
        Climate.Mild -> 0f
        Climate.Hot -> 200f
    }

    return (base + activityBonus + climateAdjustment)
        .coerceIn(1500f, 5000f)
        .let { (it / 50f).roundToInt() * 50f }
}
