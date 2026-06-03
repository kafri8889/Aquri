package com.anafthdev.aquri.ui.screens.mission

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anafthdev.aquri.ui.components.ClayCard
import com.anafthdev.aquri.ui.components.ClayIconButton
import com.anafthdev.aquri.ui.theme.AquriTheme

@Composable
fun LevelLadderScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MissionViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LevelLadderContent(
        state = state,
        onNavigateBack = onNavigateBack,
        modifier = modifier
    )
}

@Composable
private fun LevelLadderContent(
    state: MissionUiState,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(
                        text = "Level Ladder",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Level ${state.level} - ${state.levelTitle}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(state.levelMilestones, key = { it.level }) { milestone ->
                LevelMilestoneItem(
                    milestone = milestone,
                    modifier = Modifier.padding(vertical = 5.dp)
                )
            }
        }
    }
}

@Composable
private fun LevelMilestoneItem(
    milestone: LevelMilestone,
    modifier: Modifier = Modifier
) {
    val isCurrent = milestone.status == LevelMilestoneStatus.Current
    val isUnlocked = milestone.status == LevelMilestoneStatus.Unlocked
    val iconColor = when {
        isCurrent -> Color(0xFFFFB300)
        isUnlocked -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f)
    }

    ClayCard(
        modifier = modifier.fillMaxWidth(),
        containerColor = when {
            isCurrent -> AquriTheme.clay.sunny.copy(alpha = 0.42f)
            isUnlocked -> AquriTheme.clay.mint
            else -> AquriTheme.clay.surfaceStrong
        },
        borderColor = if (isCurrent) Color(0xFFFFB300) else AquriTheme.clay.highlight,
        elevation = if (isCurrent) AquriTheme.clay.floatingElevation else AquriTheme.clay.cardElevation
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when {
                        isCurrent -> Icons.Default.Star
                        isUnlocked -> Icons.Default.CheckCircle
                        else -> Icons.Default.Lock
                    },
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(26.dp)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Level ${milestone.level} - ${milestone.title}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${milestone.requiredXp} XP - ${milestone.rewardLabel}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (milestone.status == LevelMilestoneStatus.Locked && milestone.progress > 0f) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(milestone.progress)
                                .fillMaxHeight()
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }
                }
            }

            Text(
                text = when (milestone.status) {
                    LevelMilestoneStatus.Unlocked -> "Unlocked"
                    LevelMilestoneStatus.Current -> "Current"
                    LevelMilestoneStatus.Locked -> "Locked"
                },
                style = MaterialTheme.typography.labelSmall,
                color = iconColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
