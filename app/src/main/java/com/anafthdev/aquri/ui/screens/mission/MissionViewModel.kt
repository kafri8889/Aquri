package com.anafthdev.aquri.ui.screens.mission

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anafthdev.aquri.core.mission.MissionService
import com.anafthdev.aquri.core.mission.model.MissionCardModel
import com.anafthdev.aquri.core.mission.model.MissionStatus
import com.anafthdev.aquri.data.model.entity.BadgeEntity
import com.anafthdev.aquri.data.model.entity.UserBadgeEntity
import com.anafthdev.aquri.data.repository.BadgeRepository
import com.anafthdev.aquri.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class MissionViewModel @Inject constructor(
    private val missionService: MissionService,
    private val badgeRepository: BadgeRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MissionUiState())
    val uiState: StateFlow<MissionUiState> = _uiState.asStateFlow()

    init {
        combine(
            missionService.observeDashboard(),
            observeHighlightedBadges()
        ) { dashboard, highlightedBadges ->
            dashboard to highlightedBadges
        }
            .catch { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to load missions"
                    )
                }
            }
            .onEach { (dashboard, highlightedBadges) ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = null,
                        level = dashboard.level,
                        levelTitle = dashboard.levelTitle,
                        totalXp = dashboard.totalXp,
                        coinBalance = dashboard.coinBalance,
                        currentLevelXp = dashboard.currentLevelXp,
                        nextLevelXp = dashboard.nextLevelXp,
                        isPro = dashboard.isPro,
                        streakCount = dashboard.currentStreak,
                        shieldCount = dashboard.shieldCount,
                        dailyMissions = dashboard.dailyMissions,
                        weeklyMissions = dashboard.weeklyMissions,
                        oneTimeMissions = dashboard.oneTimeMissions,
                        activeMissions = buildActiveMissions(
                            dashboard.dailyMissions + dashboard.weeklyMissions + dashboard.oneTimeMissions
                        ),
                        levelMilestones = buildLevelMilestones(
                            currentLevel = dashboard.level,
                            totalXp = dashboard.totalXp
                        ),
                        highlightedBadges = highlightedBadges
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onMissionActionClick(mission: MissionCardModel) {
        viewModelScope.launch {
            if (mission.progress.status == MissionStatus.Completed) {
                missionService.claimReward(mission)
            }
        }
    }

    private fun observeHighlightedBadges() = userRepository.getUser()
        .filterNotNull()
        .flatMapLatest { user ->
            combine(
                badgeRepository.getAllBadges(),
                badgeRepository.getUserBadges(user.id)
            ) { badges, userBadges ->
                val badgeById = badges.associateBy { it.id }
                userBadges
                    .sortedWith(
                        compareByDescending<UserBadgeEntity> { it.isFeatured }
                            .thenByDescending { it.earnedAt }
                    )
                    .mapNotNull { userBadge -> badgeById[userBadge.badgeId] }
                    .take(3)
            }
        }

    private fun buildActiveMissions(
        missions: List<MissionCardModel>
    ): List<MissionCardModel> {
        return missions
            .filter { it.progress.status != MissionStatus.Claimed }
            .sortedWith(
                compareByDescending<MissionCardModel> { it.progress.status == MissionStatus.Completed }
                    .thenByDescending { it.progress.progress }
                    .thenByDescending { it.definition.reward.xp + it.definition.reward.coins }
            )
            .take(5)
    }

    private fun buildLevelMilestones(
        currentLevel: Int,
        totalXp: Int
    ): List<LevelMilestone> {
        return (1..50).map { level ->
            val requiredXp = xpRequiredForLevel(level)
            LevelMilestone(
                level = level,
                title = titleForLevel(level),
                requiredXp = requiredXp,
                status = when {
                    level < currentLevel -> LevelMilestoneStatus.Unlocked
                    level == currentLevel -> LevelMilestoneStatus.Current
                    else -> LevelMilestoneStatus.Locked
                },
                rewardLabel = rewardForLevel(level),
                progress = if (level <= currentLevel) {
                    1f
                } else {
                    (totalXp.toFloat() / requiredXp.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
                }
            )
        }
    }

    private fun xpRequiredForLevel(level: Int): Int {
        if (level <= 1) return 0
        return (level - 1) * (level - 1) * 80
    }

    private fun titleForLevel(level: Int): String {
        return when {
            level >= 45 -> "Ocean Legend"
            level >= 35 -> "Tide Master"
            level >= 25 -> "Hydro Champion"
            level >= 15 -> "Aqua Ranger"
            level >= 8 -> "Flow Builder"
            else -> "Hydro Initiate"
        }
    }

    private fun rewardForLevel(level: Int): String {
        return when {
            level % 10 == 0 -> "Theme reward"
            level % 5 == 0 -> "Badge reward"
            level % 3 == 0 -> "Bonus coins"
            else -> "XP milestone"
        }
    }
}

data class MissionUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val level: Int = 1,
    val levelTitle: String = "",
    val totalXp: Int = 0,
    val coinBalance: Int = 0,
    val currentLevelXp: Int = 0,
    val nextLevelXp: Int = 1,
    val isPro: Boolean = false,
    val streakCount: Int = 0,
    val shieldCount: Int = 0,
    val activeMissions: List<MissionCardModel> = emptyList(),
    val dailyMissions: List<MissionCardModel> = emptyList(),
    val weeklyMissions: List<MissionCardModel> = emptyList(),
    val oneTimeMissions: List<MissionCardModel> = emptyList(),
    val levelMilestones: List<LevelMilestone> = emptyList(),
    val highlightedBadges: List<BadgeEntity> = emptyList()
)

data class LevelMilestone(
    val level: Int,
    val title: String,
    val requiredXp: Int,
    val status: LevelMilestoneStatus,
    val rewardLabel: String,
    val progress: Float
)

enum class LevelMilestoneStatus {
    Unlocked,
    Current,
    Locked
}
