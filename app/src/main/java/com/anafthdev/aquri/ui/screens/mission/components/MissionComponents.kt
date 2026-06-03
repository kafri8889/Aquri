package com.anafthdev.aquri.ui.screens.mission.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anafthdev.aquri.core.mission.model.LevelReward
import com.anafthdev.aquri.core.mission.model.MissionCardModel
import com.anafthdev.aquri.core.mission.model.MissionStatus
import com.anafthdev.aquri.data.model.entity.BadgeEntity
import com.anafthdev.aquri.ui.components.AssetImage
import com.anafthdev.aquri.ui.components.ClayCard
import com.anafthdev.aquri.ui.components.ClayPrimaryButton
import com.anafthdev.aquri.ui.components.ClaySurface
import com.anafthdev.aquri.ui.theme.AquriTheme

@Composable
fun LevelStatusCard(
    level: Int,
    levelTitle: String,
    totalXp: Int,
    currentLevelXp: Int,
    nextLevelXp: Int,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val levelRangeXp = (nextLevelXp - currentLevelXp).coerceAtLeast(1)
    val levelProgressXp = (totalXp - currentLevelXp).coerceIn(0, levelRangeXp)

    ClayCard(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(AquriTheme.clay.radiusMedium),
        containerColor = Color.Transparent,
        borderColor = Color.White.copy(alpha = 0.32f),
        elevation = AquriTheme.clay.floatingElevation,
        onClick = onClick
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF0288D1), Color(0xFF00ACC1))
                    )
                )
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "CURRENT STATUS",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Level $level - $levelTitle",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "${String.format("%,d", totalXp)} XP",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${String.format("%,d", levelRangeXp - levelProgressXp)} XP TO LEVEL ${level + 1}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction = (levelProgressXp.toFloat() / levelRangeXp.toFloat()).coerceIn(0f, 1f))
                                .fillMaxHeight()
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                    }
                }
            }

            Icon(
                imageVector = Icons.Default.WaterDrop,
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .align(Alignment.TopEnd)
                    .padding(top = 8.dp),
                tint = Color.White.copy(alpha = 0.1f)
            )
        }
    }
}

@Composable
fun BadgeSection(
    badges: List<BadgeEntity>,
    onViewAllBadges: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Highlighted Badges",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            if (onViewAllBadges != null) {
                ClayPrimaryButton(
                    onClick = onViewAllBadges,
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        horizontal = 14.dp,
                        vertical = 8.dp
                    )
                ) {
                    Text(
                        text = "View All",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (badges.isEmpty()) {
            ClayCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(AquriTheme.clay.radiusMedium),
                containerColor = AquriTheme.clay.surfaceStrong,
                borderColor = AquriTheme.clay.highlight
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "No badges earned yet",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Complete missions to unlock real Aquri badges.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            return
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            badges.take(3).forEach { badge ->
                BadgeItem(
                    badge = badge,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun BadgeItem(
    badge: BadgeEntity,
    modifier: Modifier = Modifier
) {
    ClayCard(
        modifier = modifier.aspectRatio(1f),
        shape = RoundedCornerShape(AquriTheme.clay.radiusMedium),
        containerColor = AquriTheme.clay.surfaceStrong
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (badge.iconUrl.isNotEmpty()) {
                            Color.Transparent
                        } else {
                            when (badge.name) {
                                "EARLY BIRD" -> Color(0xFFFFE0B2)
                                "FLOOD" -> Color(0xFFE1F5FE)
                                "OCEAN MARATHON" -> Color(0xFFE0F2F1)
                                else -> Color(0xFFF5F5F5)
                            }
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (badge.iconUrl.isNotEmpty()) {
                    AssetImage(
                        assetPath = badge.iconUrl,
                        contentDescription = null,
                        modifier = Modifier.size(52.dp)
                    )
                } else {
                    Icon(
                        imageVector = when (badge.name) {
                            "EARLY BIRD" -> Icons.Default.ElectricBolt
                            "FLOOD" -> Icons.Default.WaterDrop
                            "OCEAN MARATHON" -> Icons.Default.FitnessCenter
                            else -> Icons.Default.Star
                        },
                        contentDescription = null,
                        tint = when (badge.name) {
                            "EARLY BIRD" -> Color(0xFFF57C00)
                            "FLOOD" -> Color(0xFF0288D1)
                            "OCEAN MARATHON" -> Color(0xFF00796B)
                            else -> Color.Gray
                        },
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = badge.name,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 12.sp,
                color = Color.DarkGray
            )
        }
    }
}

@Composable
fun StreakAndShieldRow(
    streakCount: Int,
    shieldCount: Int,
    isPro: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatsCard(
            label = "STREAK",
            value = "$streakCount days",
            icon = Icons.Default.LocalFireDepartment,
            iconColor = Color(0xFFE65100),
            modifier = Modifier.weight(1f)
        )
        StatsCard(
            label = "SHIELD ACTIVE",
            value = if (isPro) "$shieldCount" else "$shieldCount/1",
            icon = Icons.Default.Shield,
            iconColor = Color(0xFF0277BD),
            badgeText = if (isPro) "PRO" else "FREE",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun CoinBalancePill(
    coinBalance: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = CircleShape,
        color = AquriTheme.clay.sunny.copy(alpha = 0.52f),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = Color(0xFFFFB300),
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = String.format("%,d", coinBalance),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF8A5B00)
            )
        }
    }
}

@Composable
fun StatsCard(
    label: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    badgeText: String? = null,
    modifier: Modifier = Modifier
) {
    ClayCard(
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(AquriTheme.clay.radiusMedium),
        containerColor = AquriTheme.clay.surface
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
                if (badgeText != null) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFB3E5FC),
                        modifier = Modifier.size(width = 42.dp, height = 18.dp)
                    ) {
                        Text(
                            text = badgeText,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF01579B),
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 1.dp)
                        )
                    }
                }
            }
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun DailyQuestItem(
    mission: MissionCardModel,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    MissionCardItem(
        mission = mission,
        onClaim = onClick,
        modifier = modifier
    )
}

@Composable
fun MissionCardItem(
    mission: MissionCardModel,
    onClaim: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isCompleted = mission.progress.status == MissionStatus.Completed
    val isClaimed = mission.progress.status == MissionStatus.Claimed

    ClayCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AquriTheme.clay.radiusMedium),
        containerColor = if (isCompleted || isClaimed) AquriTheme.clay.mint else AquriTheme.clay.surfaceStrong,
        borderColor = if (isCompleted || isClaimed) MaterialTheme.colorScheme.primary.copy(alpha = 0.32f) else AquriTheme.clay.highlight
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.72f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when {
                        mission.definition.title.contains("Focus", true) -> Icons.Default.ElectricBolt
                        else -> Icons.Default.FitnessCenter
                    },
                    contentDescription = null,
                    tint = Color(0xFF00796B),
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = mission.definition.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = mission.definition.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                MissionRewardRow(mission = mission)
                if (mission.progress.status == MissionStatus.Active) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(6.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF5F5F5))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(fraction = mission.progress.progress)
                                    .fillMaxHeight()
                                    .clip(CircleShape)
                                    .background(Color(0xFF00ACC1))
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${(mission.progress.progress * 100).toInt().coerceIn(0, 100)}%",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00ACC1)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            if (isCompleted) {
                ClayPrimaryButton(
                    onClick = { onClaim?.invoke() },
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    )
                ) {
                    Text(
                        text = "Claim",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else if (isClaimed) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.72f)
                ) {
                    Text(
                        text = "Claimed",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MissionRewardRow(
    mission: MissionCardModel
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (mission.definition.reward.xp > 0) {
            RewardPill(
                text = "+${mission.definition.reward.xp} XP",
                containerColor = AquriTheme.clay.sunny,
                contentColor = Color(0xFF8A5B00)
            )
        }
        if (mission.definition.reward.coins > 0) {
            RewardPill(
                text = "+${mission.definition.reward.coins} coins",
                containerColor = AquriTheme.clay.coral,
                contentColor = Color(0xFF9A3E2E)
            )
        }
    }
}

@Composable
private fun RewardPill(
    text: String,
    containerColor: Color,
    contentColor: Color
) {
    Surface(
        shape = CircleShape,
        color = containerColor.copy(alpha = 0.78f)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
        )
    }
}

@Composable
fun EpicChallengeCard(
    title: String,
    description: String,
    progress: Float,
    daysLeft: Int,
    totalDays: Int,
    modifier: Modifier = Modifier
) {
    ClayCard(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(AquriTheme.clay.radiusLarge),
        containerColor = Color.Transparent,
        borderColor = Color.White.copy(alpha = 0.32f),
        elevation = AquriTheme.clay.floatingElevation
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF006064), Color(0xFF0097A7))
                    )
                )
        ) {
            // Placeholder for water ripple background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(Color.White.copy(alpha = 0.1f), Color.Transparent),
                            radius = 400f
                        )
                    )
            )

            Column(
                modifier = Modifier.fillMaxSize().padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFFF9800),
                        modifier = Modifier.height(24.dp)
                    ) {
                        Text(
                            text = "MONTHLY EVENT",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f),
                        maxLines = 2
                    )
                }

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "$daysLeft/$totalDays",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction = progress.coerceIn(0f, 1f))
                                .fillMaxHeight()
                                .clip(CircleShape)
                                .background(Color(0xFFFF9800))
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProgressionPreviewSection(
    rewards: List<LevelReward>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Progression Preview",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            rewards.forEach { reward ->
                RewardPreviewItem(
                    reward = reward,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun RewardPreviewItem(
    reward: LevelReward,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ClaySurface(
            modifier = Modifier
                .size(64.dp),
            shape = CircleShape,
            containerColor = if (reward.isUnlocked) AquriTheme.clay.mint else AquriTheme.clay.surfaceMuted,
            borderColor = if (reward.isUnlocked) MaterialTheme.colorScheme.primary.copy(alpha = 0.35f) else AquriTheme.clay.highlight
        ) {
            Icon(
                imageVector = when (reward.title) {
                    "New Icon Pack" -> Icons.Default.Star
                    "Mint Theme" -> Icons.Default.WaterDrop
                    else -> Icons.Default.Lock
                },
                contentDescription = null,
                tint = if (reward.isUnlocked) Color(0xFF00838F) else Color.Gray,
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.Center)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = reward.title,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = if (reward.isUnlocked) Color.Black else Color.Gray
        )
        Text(
            text = "LVL ${reward.level}",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = if (reward.isUnlocked) Color(0xFF00838F) else Color.Gray
        )
    }
}
