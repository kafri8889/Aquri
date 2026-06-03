package com.anafthdev.aquri.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anafthdev.aquri.data.model.entity.HydrationLogWithBottle
import com.anafthdev.aquri.data.model.entity.UserChallengeEntity
import com.anafthdev.aquri.data.model.entity.UserEntity
import com.anafthdev.aquri.data.model.entity.UserQuestEntity
import com.anafthdev.aquri.data.repository.AuthRepository
import com.anafthdev.aquri.data.repository.HydrationRepository
import com.anafthdev.aquri.data.repository.MissionRepository
import com.anafthdev.aquri.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    userRepository: UserRepository,
    hydrationRepository: HydrationRepository,
    missionRepository: MissionRepository
) : ViewModel() {

    private val user = userRepository.getUser()

    private val logs = user.flatMapLatest { currentUser ->
        if (currentUser == null) {
            flowOf(emptyList())
        } else {
            hydrationRepository.getLogsWithBottle(currentUser.id)
        }
    }

    private val quests = user.flatMapLatest { currentUser ->
        if (currentUser == null) {
            flowOf(emptyList())
        } else {
            missionRepository.getUserQuests(currentUser.id)
        }
    }

    private val challenges = user.flatMapLatest { currentUser ->
        if (currentUser == null) {
            flowOf(emptyList())
        } else {
            missionRepository.getUserChallenges(currentUser.id)
        }
    }

    val uiState = combine(
        user,
        authRepository.session,
        logs,
        quests,
        challenges
    ) { currentUser, session, hydrationLogs, userQuests, userChallenges ->
        currentUser.toProfileUiState(
            isGuest = session.isGuest,
            isAuthenticated = session.isAuthenticated,
            logs = hydrationLogs,
            quests = userQuests,
            challenges = userChallenges
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProfileUiState()
    )

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            authRepository.clearSession()
            onComplete()
        }
    }
}

data class ProfileUiState(
    val displayName: String = "-",
    val subtitle: String = "-",
    val isGuest: Boolean = false,
    val isAuthenticated: Boolean = false,
    val totalVolumeLiters: String = "0",
    val currentStreakDays: Int = 0,
    val missionsWon: Int = 0,
    val dailyGoalText: String = "-",
    val isPro: Boolean = false
)

private fun UserEntity?.toProfileUiState(
    isGuest: Boolean,
    isAuthenticated: Boolean,
    logs: List<HydrationLogWithBottle>,
    quests: List<UserQuestEntity>,
    challenges: List<UserChallengeEntity>
): ProfileUiState {
    val totalMl = logs.sumOf { it.log.amountMl.toDouble() }
    val totalLiters = totalMl / 1_000.0
    val displayName = this?.name?.takeIf { it.isNotBlank() } ?: "-"
    val email = this?.email?.takeIf { it.isNotBlank() }
    val subtitle = when {
        email != null -> email
        isGuest -> "Guest mode - limited access"
        else -> "-"
    }
    val dailyGoal = this?.dailyGoalMl
        ?.takeIf { it > 0f }
        ?.let { "${it.toInt()} ml" }
        ?: "-"

    return ProfileUiState(
        displayName = displayName,
        subtitle = subtitle,
        isGuest = isGuest,
        isAuthenticated = isAuthenticated,
        totalVolumeLiters = if (totalLiters == 0.0) "0" else String.format(Locale.US, "%.1f", totalLiters),
        currentStreakDays = logs.currentStreakDays(),
        missionsWon = quests.count { it.isCompleted } + challenges.count { it.isCompleted },
        dailyGoalText = dailyGoal,
        isPro = this?.isPro == true
    )
}

private fun List<HydrationLogWithBottle>.currentStreakDays(): Int {
    val uniqueDates = map { it.log.logDate }
        .distinct()
        .sortedDescending()

    if (uniqueDates.isEmpty()) return 0

    val dayMillis = 24L * 60L * 60L * 1_000L
    var expectedDate = uniqueDates.first()
    var streak = 0

    for (date in uniqueDates) {
        if (date == expectedDate) {
            streak += 1
            expectedDate -= dayMillis
        } else {
            break
        }
    }

    return streak
}
