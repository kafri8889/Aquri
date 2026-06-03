package com.anafthdev.aquri.ui.screens.personal_information

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anafthdev.aquri.data.model.entity.UserEntity
import com.anafthdev.aquri.data.model.enum.ActivityLevel
import com.anafthdev.aquri.data.model.enum.Climate
import com.anafthdev.aquri.data.model.enum.Gender
import com.anafthdev.aquri.data.repository.UserRepository
import com.anafthdev.aquri.ui.shared.calculateHydrationGoalMl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PersonalInformationViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PersonalInformationUiState())
    val uiState: StateFlow<PersonalInformationUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val user = userRepository.getUser().first()
            _uiState.update { state ->
                state.copy(
                    user = user,
                    name = user?.name.orEmpty(),
                    email = user?.email.orEmpty(),
                    gender = user?.gender ?: Gender.Male,
                    weight = user?.weightKg?.takeIf { it > 0f }?.formatWeight().orEmpty(),
                    activityLevel = user?.activityLevel ?: ActivityLevel.Moderate,
                    climate = user?.climate ?: Climate.Mild
                ).withRecalculatedGoal()
            }
        }
    }

    fun onNameChanged(value: String) {
        _uiState.update { it.copy(name = value.take(80), message = null) }
    }

    fun onEmailChanged(value: String) {
        _uiState.update { it.copy(email = value.take(120), message = null) }
    }

    fun onGenderSelected(gender: Gender) {
        _uiState.update { it.copy(gender = gender, message = null).withRecalculatedGoal() }
    }

    fun onWeightChanged(value: String) {
        val filtered = value.filter { it.isDigit() || it == '.' }.take(6)
        _uiState.update { it.copy(weight = filtered, message = null).withRecalculatedGoal() }
    }

    fun onActivityLevelSelected(activityLevel: ActivityLevel) {
        _uiState.update { it.copy(activityLevel = activityLevel, message = null).withRecalculatedGoal() }
    }

    fun onClimateSelected(climate: Climate) {
        _uiState.update { it.copy(climate = climate, message = null).withRecalculatedGoal() }
    }

    fun saveChanges() {
        viewModelScope.launch {
            val state = _uiState.value
            val now = System.currentTimeMillis()
            val user = (state.user ?: UserEntity(createdAt = now)).copy(
                name = state.name.trim(),
                email = state.email.trim(),
                gender = state.gender,
                weightKg = state.weight.toFloatOrNull() ?: 0f,
                activityLevel = state.activityLevel,
                climate = state.climate,
                dailyGoalMl = state.dailyGoalMl,
                updatedAt = now
            )

            if (state.user == null) {
                userRepository.insertUser(user)
            } else {
                userRepository.updateUser(user)
            }

            _uiState.update {
                it.copy(
                    user = user,
                    message = "Personal information saved."
                )
            }
        }
    }
}

data class PersonalInformationUiState(
    val user: UserEntity? = null,
    val name: String = "",
    val email: String = "",
    val gender: Gender = Gender.Male,
    val weight: String = "",
    val activityLevel: ActivityLevel = ActivityLevel.Moderate,
    val climate: Climate = Climate.Mild,
    val dailyGoalMl: Float = 0f,
    val message: String? = null
) {
    val displayName: String = name.ifBlank { "-" }
    val displayEmail: String = email.ifBlank { "-" }
    val dailyGoalText: String = dailyGoalMl.takeIf { it > 0f }?.let { "${it.toInt()} ml" } ?: "-"
}

private fun PersonalInformationUiState.withRecalculatedGoal(): PersonalInformationUiState {
    val goal = calculateHydrationGoalMl(
        gender = gender,
        weightKg = weight.toFloatOrNull() ?: 0f,
        activityLevel = activityLevel,
        climate = climate
    )

    return copy(dailyGoalMl = goal)
}

private fun Float.formatWeight(): String {
    return if (this % 1f == 0f) {
        toInt().toString()
    } else {
        toString()
    }
}
