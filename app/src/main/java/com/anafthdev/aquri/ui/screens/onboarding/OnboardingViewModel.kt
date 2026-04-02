package com.anafthdev.aquri.ui.screens.onboarding

import androidx.compose.ui.util.fastCoerceAtLeast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import com.anafthdev.aquri.data.Constant
import com.anafthdev.aquri.data.repository.PreferenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun onGenderSelected(gender: Gender) {
        _uiState.update { it.copy(gender = gender).withRecalculatedGoal() }
    }

    fun onWeightChanged(weight: String) {
        val filtered = weight.filter { it.isDigit() || it == '.' }.take(6)
        _uiState.update { it.copy(weight = filtered).withRecalculatedGoal() }
    }

    fun onWeightUnitChanged(unit: WeightUnit) {
        _uiState.update { it.copy(weightUnit = unit).withRecalculatedGoal() }
    }

    fun onActivityLevelSelected(activityLevel: ActivityLevel) {
        _uiState.update { it.copy(activityLevel = activityLevel).withRecalculatedGoal() }
    }

    fun onClimateSelected(climate: Climate) {
        _uiState.update { it.copy(climate = climate).withRecalculatedGoal() }
    }

    fun saveGoal() {
        viewModelScope.launch {
            preferenceRepository.setDailyGoal(_uiState.value.dailyGoal)
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            preferenceRepository.setOnboardingCompleted(true)
        }
    }
}

// Extension function — kalkulasi murni, tanpa side effect
private fun OnboardingUiState.withRecalculatedGoal(): OnboardingUiState {
    return copy(dailyGoal = calculateDailyGoal().fastCoerceAtLeast(Constant.MIN_DAILY_GOAL))
}

fun OnboardingUiState.calculateDailyGoal(): Float {
    val rawWeight = weight.toFloatOrNull() ?: return 0f
    if (rawWeight <= 0f) return 0f

    val weightInKg = when (weightUnit) {
        WeightUnit.KG -> rawWeight
        WeightUnit.LBS -> rawWeight * 0.453592f
    }.coerceIn(20f, 300f) // clamp berat yang masuk akal

    // Base: berbeda per gender (WHO recommendation)
    val base = when (gender) {
        Gender.Male -> weightInKg * 35f
        Gender.Female -> weightInKg * 31f
        Gender.Other -> weightInKg * 33f
    }

    // Activity level adjustment
    val activityBonus = when (activityLevel) {
        ActivityLevel.Sedentary -> 0f
        ActivityLevel.Moderate -> 350f
        ActivityLevel.Active -> 700f
    }

    // Climate adjustment
    val climateAdjustment = when (climate) {
        Climate.Cold -> -100f
        Climate.Mild -> 0f
        Climate.Hot -> 200f
    }

    return (base + activityBonus + climateAdjustment)
        .coerceIn(1500f, 5000f) // clamp hasil akhir (ml)
        .let { (it / 50f).roundToInt() * 50f } // bulatkan ke 50ml terdekat
}

data class OnboardingUiState(
    val gender: Gender = Gender.Male,
    val weight: String = "",
    val weightUnit: WeightUnit = WeightUnit.KG,
    val activityLevel: ActivityLevel = ActivityLevel.Moderate,
    val climate: Climate = Climate.Mild,
    val dailyGoal: Float = 0f
)

enum class Gender { Male, Female, Other }
enum class WeightUnit { KG, LBS }
enum class ActivityLevel { Sedentary, Moderate, Active }
enum class Climate { Cold, Mild, Hot }
