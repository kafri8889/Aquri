package com.anafthdev.aquri.ui.screens.mission

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anafthdev.aquri.core.mission.model.MissionCardModel
import com.anafthdev.aquri.core.mission.model.MissionCategory
import com.anafthdev.aquri.core.mission.model.MissionDefinition
import com.anafthdev.aquri.core.mission.model.MissionProgress
import com.anafthdev.aquri.core.mission.model.MissionRecurrence
import com.anafthdev.aquri.core.mission.model.MissionReward
import com.anafthdev.aquri.core.mission.model.MissionStatus
import com.anafthdev.aquri.core.mission.model.MissionTrigger
import com.anafthdev.aquri.data.model.entity.BadgeEntity
import com.anafthdev.aquri.data.model.enum.BadgeCategory
import com.anafthdev.aquri.data.model.enum.Rarity
import com.anafthdev.aquri.ui.components.ClayCard
import com.anafthdev.aquri.ui.components.ClayPrimaryButton
import com.anafthdev.aquri.ui.screens.mission.components.BadgeSection
import com.anafthdev.aquri.ui.screens.mission.components.CoinBalancePill
import com.anafthdev.aquri.ui.screens.mission.components.LevelStatusCard
import com.anafthdev.aquri.ui.screens.mission.components.MissionCardItem
import com.anafthdev.aquri.ui.screens.mission.components.StreakAndShieldRow
import com.anafthdev.aquri.ui.theme.AquriTheme

@Composable
fun MissionScreen(
    onViewAllMissions: () -> Unit,
    onViewBadges: () -> Unit,
    onViewLevelLadder: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MissionViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    MissionScreenContent(
        state = state,
        onViewAllMissions = onViewAllMissions,
        onViewBadges = onViewBadges,
        onViewLevelLadder = onViewLevelLadder,
        onMissionActionClick = viewModel::onMissionActionClick,
        modifier = Modifier
            .padding(vertical = 24.dp)
            .then(modifier)
    )
}

@Composable
private fun MissionScreenContent(
    state: MissionUiState,
    onViewAllMissions: () -> Unit,
    onViewBadges: () -> Unit,
    onViewLevelLadder: () -> Unit,
    onMissionActionClick: (MissionCardModel) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Missions",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    CoinBalancePill(
                        coinBalance = state.coinBalance
                    )
                }
            }

            item {
                LevelStatusCard(
                    level = state.level,
                    levelTitle = state.levelTitle,
                    totalXp = state.totalXp,
                    currentLevelXp = state.currentLevelXp,
                    nextLevelXp = state.nextLevelXp,
                    onClick = onViewLevelLadder,
                    modifier = Modifier.padding(16.dp)
                )
            }

            item {
                BadgeSection(
                    badges = state.highlightedBadges,
                    onViewAllBadges = onViewBadges,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                StreakAndShieldRow(
                    streakCount = state.streakCount,
                    shieldCount = state.shieldCount,
                    isPro = state.isPro,
                    modifier = Modifier.padding(16.dp)
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Mission Board",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    ClayPrimaryButton(
                        onClick = onViewAllMissions,
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "View All",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            missionSection(
                title = "Daily Quests",
                missions = state.dailyMissions,
                isLoading = state.isLoading,
                onMissionActionClick = onMissionActionClick
            )

            missionSection(
                title = "Weekly Missions",
                missions = state.weeklyMissions,
                isLoading = state.isLoading,
                onMissionActionClick = onMissionActionClick
            )

            missionSection(
                title = "Milestones",
                missions = state.oneTimeMissions,
                isLoading = state.isLoading,
                onMissionActionClick = onMissionActionClick
            )

            if (state.errorMessage != null) {
                item {
                    Text(
                        text = state.errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            if (state.isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.missionSection(
    title: String,
    missions: List<MissionCardModel>,
    isLoading: Boolean,
    onMissionActionClick: (MissionCardModel) -> Unit
) {
    val previewMissions = missions
        .filter { it.progress.status != MissionStatus.Claimed }
        .sortedWith(
            compareByDescending<MissionCardModel> { it.progress.status == MissionStatus.Completed }
                .thenByDescending { it.progress.progress }
                .thenByDescending { it.definition.reward.xp + it.definition.reward.coins }
        )
        .take(3)

    item {
        Spacer(modifier = Modifier.height(12.dp))
        MissionSectionHeader(
            title = title,
            missions = missions,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }

    if (previewMissions.isEmpty() && !isLoading) {
        item {
            ClayCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                containerColor = AquriTheme.clay.surfaceStrong
            ) {
                Text(
                    text = "No ${title.lowercase()} available right now.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }

    items(previewMissions, key = { "${title}_${it.definition.id}" }) { mission ->
        MissionCardItem(
            mission = mission,
            onClaim = { onMissionActionClick(mission) },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun MissionSectionHeader(
    title: String,
    missions: List<MissionCardModel>,
    action: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        val completedCount = missions.count { it.progress.status != MissionStatus.Active }
        if (action == null) {
            Text(
                text = "$completedCount/${missions.size} DONE",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF00838F),
                fontWeight = FontWeight.Bold
            )
        } else {
            action()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MissionScreenPreview() {
    AquriTheme {
        MissionScreenContent(
            state = MissionUiState(
                isLoading = false,
                level = 14,
                levelTitle = "Hydro Knight",
                totalXp = 2450,
                coinBalance = 180,
                currentLevelXp = 2080,
                nextLevelXp = 3000,
                streakCount = 12,
                shieldCount = 1,
                dailyMissions = listOf(
                    MissionCardModel(
                        definition = MissionDefinition(
                            id = "1",
                            title = "Pure Focus",
                            description = "Drink 500ml during work",
                            category = MissionCategory.Hydration,
                            recurrence = MissionRecurrence.Daily,
                            reward = MissionReward(xp = 50),
                            trigger = MissionTrigger.HydrationLogCount(1)
                        ),
                        progress = MissionProgress(
                            missionId = "1",
                            progress = 1f,
                            status = MissionStatus.Completed
                        )
                    ),
                    MissionCardModel(
                        definition = MissionDefinition(
                            id = "2",
                            title = "Post-Workout Hydration",
                            description = "Drink 300ml after gym",
                            category = MissionCategory.Hydration,
                            recurrence = MissionRecurrence.Daily,
                            reward = MissionReward(xp = 30),
                            trigger = MissionTrigger.HydrationLogCount(1)
                        ),
                        progress = MissionProgress(
                            missionId = "2",
                            progress = 0.4f,
                            status = MissionStatus.Active
                        )
                    )
                ),
                weeklyMissions = listOf(
                    MissionCardModel(
                        definition = MissionDefinition(
                            id = "3",
                            title = "Weekly Rhythm",
                            description = "Hit your daily goal on 3 days this week.",
                            category = MissionCategory.Hydration,
                            recurrence = MissionRecurrence.Weekly,
                            reward = MissionReward(xp = 90),
                            trigger = MissionTrigger.WeeklyGoalDays(3)
                        ),
                        progress = MissionProgress(
                            missionId = "3",
                            progress = 0.66f,
                            status = MissionStatus.Active
                        )
                    )
                ),
                oneTimeMissions = listOf(
                    MissionCardModel(
                        definition = MissionDefinition(
                            id = "4",
                            title = "Profile Ready",
                            description = "Complete the hydration profile Aquri uses.",
                            category = MissionCategory.Hydration,
                            recurrence = MissionRecurrence.OneTime,
                            reward = MissionReward(xp = 50),
                            trigger = MissionTrigger.ProfileCompletion
                        ),
                        progress = MissionProgress(
                            missionId = "4",
                            progress = 1f,
                            status = MissionStatus.Completed
                        )
                    )
                ),
                highlightedBadges = listOf(
                    BadgeEntity(name = "EARLY BIRD", description = "", iconUrl = "", category = BadgeCategory.Habit, rarity = Rarity.Common),
                    BadgeEntity(name = "FLOOD", description = "", iconUrl = "", category = BadgeCategory.Habit, rarity = Rarity.Common),
                    BadgeEntity(name = "OCEAN MARATHON", description = "", iconUrl = "", category = BadgeCategory.SpecialEvent, rarity = Rarity.Rare)
                )
            ),
            onViewAllMissions = {},
            onViewBadges = {},
            onViewLevelLadder = {},
            onMissionActionClick = {}
        )
    }
}
