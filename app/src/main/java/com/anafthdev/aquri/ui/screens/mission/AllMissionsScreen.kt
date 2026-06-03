package com.anafthdev.aquri.ui.screens.mission

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anafthdev.aquri.core.mission.model.MissionCardModel
import com.anafthdev.aquri.core.mission.model.MissionStatus
import com.anafthdev.aquri.ui.components.ClayCard
import com.anafthdev.aquri.ui.components.ClayChip
import com.anafthdev.aquri.ui.components.ClayIconButton
import com.anafthdev.aquri.ui.screens.mission.components.MissionCardItem
import com.anafthdev.aquri.ui.theme.AquriTheme

@Composable
fun AllMissionsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MissionViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    AllMissionsContent(
        state = state,
        onNavigateBack = onNavigateBack,
        onMissionActionClick = viewModel::onMissionActionClick,
        modifier = modifier
    )
}

@Composable
private fun AllMissionsContent(
    state: MissionUiState,
    onNavigateBack: () -> Unit,
    onMissionActionClick: (MissionCardModel) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(MissionListTab.Daily) }
    val missions = when (selectedTab) {
        MissionListTab.Daily -> state.dailyMissions
        MissionListTab.Weekly -> state.weeklyMissions
        MissionListTab.Milestone -> state.oneTimeMissions
    }.sortedForAllMissions()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ClayIconButton(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    onClick = onNavigateBack
                )
                Text(
                    text = "All Missions",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 12.dp)
                )
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MissionListTab.entries.forEach { tab ->
                        ClayChip(
                            text = tab.label,
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab }
                        )
                    }
                }
            }

            item {
                MissionSummaryCard(
                    title = selectedTab.label,
                    missions = missions,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            if (missions.isEmpty() && !state.isLoading) {
                item {
                    ClayCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        containerColor = AquriTheme.clay.surfaceStrong
                    ) {
                        Text(
                            text = "No ${selectedTab.label.lowercase()} missions available.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            items(missions, key = { it.definition.id }) { mission ->
                MissionCardItem(
                    mission = mission,
                    onClaim = { onMissionActionClick(mission) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            if (state.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
private fun MissionSummaryCard(
    title: String,
    missions: List<MissionCardModel>,
    modifier: Modifier = Modifier
) {
    val completedCount = missions.count { it.progress.status != MissionStatus.Active }
    val claimableCount = missions.count { it.progress.status == MissionStatus.Completed }

    ClayCard(
        modifier = modifier.fillMaxWidth(),
        containerColor = AquriTheme.clay.surfaceStrong
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "$completedCount/${missions.size} complete - $claimableCount claimable",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private enum class MissionListTab(val label: String) {
    Daily("Daily"),
    Weekly("Weekly"),
    Milestone("Milestone")
}

private fun List<MissionCardModel>.sortedForAllMissions(): List<MissionCardModel> {
    return sortedWith(
        compareBy<MissionCardModel> {
            when (it.progress.status) {
                MissionStatus.Completed -> 0
                MissionStatus.Active -> 1
                MissionStatus.Claimed -> 2
            }
        }
            .thenByDescending { it.progress.progress }
            .thenByDescending { it.definition.reward.xp + it.definition.reward.coins }
    )
}
