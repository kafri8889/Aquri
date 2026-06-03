package com.anafthdev.aquri.ui.screens.mission

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anafthdev.aquri.data.model.entity.BadgeEntity
import com.anafthdev.aquri.data.model.enum.BadgeCategory
import com.anafthdev.aquri.data.model.enum.Rarity
import com.anafthdev.aquri.ui.components.AssetImage
import com.anafthdev.aquri.ui.components.ClayCard
import com.anafthdev.aquri.ui.components.ClayIconButton
import com.anafthdev.aquri.ui.theme.AquriTheme

@Composable
fun BadgesScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BadgesViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    BadgesContent(
        state = state,
        onNavigateBack = onNavigateBack,
        modifier = modifier
    )
}

@Composable
private fun BadgesContent(
    state: BadgesUiState,
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
                        text = "Badges",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Available Aquri achievements",
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
            item {
                BadgeSummaryCard(
                    badges = state.badges,
                    modifier = Modifier.padding(bottom = 14.dp)
                )
            }

            if (state.errorMessage != null) {
                item {
                    Text(
                        text = state.errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
            }

            if (state.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            if (state.badges.isEmpty() && !state.isLoading) {
                item {
                    ClayCard(
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = AquriTheme.clay.surfaceStrong
                    ) {
                        Text(
                            text = "No badges available yet.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            BadgeCategory.entries.forEach { category ->
                val badges = state.badges.filter { it.badge.category == category }
                if (badges.isNotEmpty()) {
                    item(key = category.name) {
                        Text(
                            text = category.label,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
                        )
                    }
                    items(
                        items = badges,
                        key = { it.badge.id.toString() }
                    ) { badge ->
                        BadgeListItem(
                            model = badge,
                            modifier = Modifier.padding(vertical = 5.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BadgeSummaryCard(
    badges: List<BadgeUiModel>,
    modifier: Modifier = Modifier
) {
    val earnedCount = badges.count { it.isEarned }

    ClayCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AquriTheme.clay.radiusMedium),
        containerColor = AquriTheme.clay.surfaceStrong
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BadgeIconBubble(
                icon = Icons.Default.Star,
                iconColor = Color(0xFFFFB300),
                containerColor = AquriTheme.clay.sunny.copy(alpha = 0.52f)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$earnedCount/${badges.size} earned",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Badges are unlocked from hydration habits, milestones, and events.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun BadgeListItem(
    model: BadgeUiModel,
    modifier: Modifier = Modifier
) {
    val badge = model.badge
    val rarityColor = badge.rarity.color

    ClayCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AquriTheme.clay.radiusMedium),
        containerColor = if (model.isEarned) AquriTheme.clay.mint else AquriTheme.clay.surfaceStrong,
        borderColor = if (model.isEarned) MaterialTheme.colorScheme.primary.copy(alpha = 0.42f) else AquriTheme.clay.highlight,
        elevation = if (model.isEarned) AquriTheme.clay.floatingElevation else AquriTheme.clay.cardElevation
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BadgeIconBubble(
                icon = if (model.isEarned) Icons.Default.CheckCircle else badge.category.icon,
                iconColor = if (model.isEarned) MaterialTheme.colorScheme.primary else rarityColor,
                containerColor = rarityColor.copy(alpha = 0.16f),
                badge = badge
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = badge.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    RarityChip(rarity = badge.rarity)
                }
                Text(
                    text = badge.description.ifBlank { "-" },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Row(
                    modifier = Modifier.padding(top = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BadgeStatusChip(isEarned = model.isEarned)
                    if (badge.isProOnly) {
                        ProChip()
                    }
                }
            }
            if (!model.isEarned) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f),
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun BadgeIconBubble(
    icon: ImageVector,
    iconColor: Color,
    containerColor: Color,
    modifier: Modifier = Modifier,
    badge: BadgeEntity? = null
) {
    Box(
        modifier = modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(containerColor),
        contentAlignment = Alignment.Center
    ) {
        if (badge?.iconUrl?.isNotEmpty() == true) {
            AssetImage(
                assetPath = badge.iconUrl,
                contentDescription = null,
                modifier = Modifier.size(44.dp)
            )
        } else {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun RarityChip(rarity: Rarity) {
    Surface(
        shape = CircleShape,
        color = rarity.color.copy(alpha = 0.16f)
    ) {
        Text(
            text = rarity.name,
            style = MaterialTheme.typography.labelSmall,
            color = rarity.color,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
        )
    }
}

@Composable
private fun BadgeStatusChip(isEarned: Boolean) {
    Surface(
        shape = CircleShape,
        color = if (isEarned) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.72f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.72f)
    ) {
        Text(
            text = if (isEarned) "Earned" else "Locked",
            style = MaterialTheme.typography.labelSmall,
            color = if (isEarned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
        )
    }
}

@Composable
private fun ProChip() {
    Surface(
        shape = CircleShape,
        color = AquriTheme.clay.sunny.copy(alpha = 0.66f)
    ) {
        Text(
            text = "PRO",
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF8A5B00),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
        )
    }
}

private val BadgeCategory.label: String
    get() = when (this) {
        BadgeCategory.Milestone -> "Milestone"
        BadgeCategory.Habit -> "Habit"
        BadgeCategory.SpecialEvent -> "Special Event"
        BadgeCategory.Social -> "Social"
    }

private val BadgeCategory.icon: ImageVector
    get() = when (this) {
        BadgeCategory.Milestone -> Icons.Default.WaterDrop
        BadgeCategory.Habit -> Icons.Default.Star
        BadgeCategory.SpecialEvent -> Icons.Default.Star
        BadgeCategory.Social -> Icons.Default.Star
    }

private val Rarity.color: Color
    get() = when (this) {
        Rarity.Common -> Color(0xFF00838F)
        Rarity.Rare -> Color(0xFF1E88E5)
        Rarity.Epic -> Color(0xFF7E57C2)
        Rarity.Legendary -> Color(0xFFE65100)
        Rarity.Mythic -> Color(0xFFD81B60)
    }
