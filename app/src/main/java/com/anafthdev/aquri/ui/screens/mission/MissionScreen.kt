package com.anafthdev.aquri.ui.screens.mission

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anafthdev.aquri.core.mission.model.MissionCardModel
import com.anafthdev.aquri.core.mission.model.MissionStatus

@Composable
fun MissionScreen(
    modifier: Modifier = Modifier,
    viewModel: MissionViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    MissionScreenContent(
        state = state,
        onRefresh = viewModel::refresh,
        onMissionAction = viewModel::onMissionActionClick,
        modifier = modifier
    )
}

@Composable
private fun MissionScreenContent(
    state: MissionUiState,
    onRefresh: () -> Unit,
    onMissionAction: (MissionCardModel) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 20.dp)
                ) {
                    Text(
                        text = "MISSIONS",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Gamification Hub",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            item {
                XpCard(
                    level = state.level,
                    levelTitle = state.levelTitle,
                    totalXp = state.totalXp,
                    currentLevelXp = state.currentLevelXp,
                    nextLevelXp = state.nextLevelXp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                StreakShieldCard(
                    streakCount = state.streakCount,
                    shieldCount = state.shieldCount,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Daily Missions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Button(onClick = onRefresh, enabled = !state.isRefreshing) {
                        Text(if (state.isRefreshing) "Refreshing..." else "Refresh")
                    }
                }
            }

            items(state.dailyMissions, key = { it.definition.id }) { mission ->
                MissionCard(
                    mission = mission,
                    onAction = { onMissionAction(mission) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                MissionSectionHeader(
                    title = "Weekly Missions",
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            items(state.weeklyMissions, key = { it.definition.id }) { mission ->
                MissionCard(
                    mission = mission,
                    onAction = { onMissionAction(mission) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                MissionSectionHeader(
                    title = "One-Time Missions",
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            items(state.oneTimeMissions, key = { it.definition.id }) { mission ->
                MissionCard(
                    mission = mission,
                    onAction = { onMissionAction(mission) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                state.challengePreview?.let { challenge ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFEAF8FF)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Challenge Preview",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = challenge.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = challenge.description,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = challenge.rewardText,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
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
                    CircularProgressIndicator(modifier = Modifier.padding(horizontal = 16.dp))
                }
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun MissionSectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}

@Composable
private fun XpCard(
    level: Int,
    levelTitle: String,
    totalXp: Int,
    currentLevelXp: Int,
    nextLevelXp: Int,
    modifier: Modifier = Modifier
) {
    val denominator = (nextLevelXp - currentLevelXp).coerceAtLeast(1)
    val progress = ((totalXp - currentLevelXp).toFloat() / denominator.toFloat()).coerceIn(0f, 1f)

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text("Level $level - $levelTitle", fontWeight = FontWeight.Bold)
            LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
            Text("$totalXp XP", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}

@Composable
private fun StreakShieldCard(
    streakCount: Int,
    shieldCount: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Streak", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                Text("$streakCount days", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            Column {
                Text("Shields", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                Text("$shieldCount", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun MissionCard(
    mission: MissionCardModel,
    onAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    val actionLabel = when (mission.progress.status) {
        MissionStatus.Active -> "Tracking"
        MissionStatus.Completed -> "Claim"
        MissionStatus.Claimed -> "Claimed"
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = mission.definition.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = mission.definition.description,
                style = MaterialTheme.typography.bodyMedium
            )
            LinearProgressIndicator(
                progress = { mission.progress.progress.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = mission.definition.reward.toRewardLabel(),
                    color = MaterialTheme.colorScheme.primary
                )
                Button(
                    onClick = onAction,
                    enabled = mission.progress.status == MissionStatus.Completed
                ) {
                    Text(actionLabel)
                }
            }
        }
    }
}

private fun com.anafthdev.aquri.core.mission.model.MissionReward.toRewardLabel(): String {
    val parts = buildList {
        if (xp > 0) add("+$xp XP")
        if (coins > 0) add("+$coins Coins")
    }

    return parts.ifEmpty { listOf("No Reward") }.joinToString(" • ")
}
