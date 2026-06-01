package com.anafthdev.aquri.ui.screens.mission

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anafthdev.aquri.core.mission.MissionService
import com.anafthdev.aquri.core.mission.model.ChallengePreview
import com.anafthdev.aquri.core.mission.model.LevelReward
import com.anafthdev.aquri.core.mission.model.MissionCardModel
import com.anafthdev.aquri.core.mission.model.MissionStatus
import com.anafthdev.aquri.data.model.entity.BadgeEntity
import com.anafthdev.aquri.data.model.enum.BadgeCategory
import com.anafthdev.aquri.data.model.enum.Rarity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MissionViewModel @Inject constructor(
    private val missionService: MissionService
) : ViewModel() {

    private val _uiState = MutableStateFlow(MissionUiState())
    val uiState: StateFlow<MissionUiState> = _uiState.asStateFlow()

    init {
        missionService.observeDashboard()
            .catch { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = error.message ?: "Failed to load missions"
                    )
                }
            }
            .onEach { dashboard ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = null,
                        level = dashboard.level,
                        levelTitle = dashboard.levelTitle,
                        totalXp = dashboard.totalXp,
                        currentLevelXp = dashboard.currentLevelXp,
                        nextLevelXp = dashboard.nextLevelXp,
                        streakCount = dashboard.currentStreak,
                        shieldCount = dashboard.shieldCount,
                        dailyMissions = dashboard.dailyMissions,
                        weeklyMissions = dashboard.weeklyMissions,
                        oneTimeMissions = dashboard.oneTimeMissions,
                        challengePreview = dashboard.challengePreview,
                        highlightedBadges = listOf(
                            BadgeEntity(name = "EARLY BIRD", description = "", iconUrl = "", category = BadgeCategory.Habit, rarity = Rarity.Common),
                            BadgeEntity(name = "FLOOD", description = "", iconUrl = "", category = BadgeCategory.Habit, rarity = Rarity.Common),
                            BadgeEntity(name = "OCEAN MARATHON", description = "", iconUrl = "", category = BadgeCategory.SpecialEvent, rarity = Rarity.Rare)
                        ),
                        levelRewards = listOf(
                            LevelReward(level = 16, title = "New Icon Pack", isUnlocked = true),
                            LevelReward(level = 18, title = "Mint Theme", isUnlocked = false),
                            LevelReward(level = 20, title = "Mystery Reward", isUnlocked = false)
                        )
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            runCatching { missionService.refreshDefinitions() }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isRefreshing = false,
                            errorMessage = error.message ?: "Failed to refresh missions"
                        )
                    }
                }
        }
    }

    fun onMissionActionClick(mission: MissionCardModel) {
        viewModelScope.launch {
            if (mission.progress.status == MissionStatus.Completed) {
                missionService.claimReward(mission.definition.id)
            }
        }
    }
}

data class MissionUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val level: Int = 1,
    val levelTitle: String = "",
    val totalXp: Int = 0,
    val currentLevelXp: Int = 0,
    val nextLevelXp: Int = 1,
    val streakCount: Int = 0,
    val shieldCount: Int = 0,
    val dailyMissions: List<MissionCardModel> = emptyList(),
    val weeklyMissions: List<MissionCardModel> = emptyList(),
    val oneTimeMissions: List<MissionCardModel> = emptyList(),
    val challengePreview: ChallengePreview? = null,
    val highlightedBadges: List<BadgeEntity> = emptyList(),
    val levelRewards: List<LevelReward> = emptyList()
)
