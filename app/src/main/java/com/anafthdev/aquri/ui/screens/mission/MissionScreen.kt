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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.anafthdev.aquri.core.mission.model.ChallengePreview
import com.anafthdev.aquri.core.mission.model.LevelReward
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
import com.anafthdev.aquri.ui.screens.mission.components.BadgeSection
import com.anafthdev.aquri.ui.screens.mission.components.DailyQuestItem
import com.anafthdev.aquri.ui.screens.mission.components.EpicChallengeCard
import com.anafthdev.aquri.ui.screens.mission.components.LevelStatusCard
import com.anafthdev.aquri.ui.screens.mission.components.ProgressionPreviewSection
import com.anafthdev.aquri.ui.screens.mission.components.StreakAndShieldRow
import com.anafthdev.aquri.ui.theme.AquriTheme

@Composable
fun MissionScreen(
    modifier: Modifier = Modifier,
    viewModel: MissionViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    MissionScreenContent(
        state = state,
        onRefresh = viewModel::refresh,
        modifier = modifier
    )
}

@Composable
private fun MissionScreenContent(
    state: MissionUiState,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
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
                IconButton(onClick = onRefresh, enabled = !state.isRefreshing) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh"
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                LevelStatusCard(
                    level = state.level,
                    levelTitle = state.levelTitle,
                    totalXp = state.totalXp,
                    nextLevelXp = state.nextLevelXp,
                    modifier = Modifier.padding(16.dp)
                )
            }

            item {
                BadgeSection(
                    badges = state.highlightedBadges,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                StreakAndShieldRow(
                    streakCount = state.streakCount,
                    shieldCount = state.shieldCount,
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
                        text = "Daily Quests",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    val completedCount = state.dailyMissions.count { it.progress.status != MissionStatus.Active }
                    Text(
                        text = "$completedCount/${state.dailyMissions.size} DONE",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF00838F),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            items(state.dailyMissions, key = { it.definition.id }) { mission ->
                DailyQuestItem(
                    mission = mission,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Epic Challenges",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                
                state.challengePreview?.let { challenge ->
                     EpicChallengeCard(
                        title = challenge.title,
                        description = challenge.description,
                        progress = 14f / 30f, // Mock progress for Epic Challenge
                        daysLeft = 14,
                        totalDays = 30,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            item {
                ProgressionPreviewSection(
                    rewards = state.levelRewards,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
            }

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
                highlightedBadges = listOf(
                    BadgeEntity(name = "EARLY BIRD", description = "", iconUrl = "", category = BadgeCategory.Habit, rarity = Rarity.Common),
                    BadgeEntity(name = "FLOOD", description = "", iconUrl = "", category = BadgeCategory.Habit, rarity = Rarity.Common),
                    BadgeEntity(name = "OCEAN MARATHON", description = "", iconUrl = "", category = BadgeCategory.SpecialEvent, rarity = Rarity.Rare)
                ),
                levelRewards = listOf(
                    LevelReward(level = 16, title = "New Icon Pack", isUnlocked = true),
                    LevelReward(level = 18, title = "Mint Theme", isUnlocked = false),
                    LevelReward(level = 20, title = "Mystery Reward", isUnlocked = false)
                ),
                challengePreview = ChallengePreview(
                    title = "30-Day Hydration Sprint",
                    description = "Complete your daily goal for 30 days straight to win the 'Oceanic Overlord' badge.",
                    rewardText = "Oceanic Overlord Badge"
                )
            ),
            onRefresh = {}
        )
    }
}
