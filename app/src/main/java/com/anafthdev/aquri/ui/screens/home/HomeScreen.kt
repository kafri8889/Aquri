package com.anafthdev.aquri.ui.screens.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anafthdev.aquri.data.Constant
import com.anafthdev.aquri.data.model.entity.BottleEntity
import com.anafthdev.aquri.data.model.entity.DailySummaryEntity
import com.anafthdev.aquri.data.model.entity.HydrationLogWithBottle
import com.anafthdev.aquri.data.model.entity.UserEntity
import com.anafthdev.aquri.data.model.entity.UserGamificationEntity
import com.anafthdev.aquri.data.model.enum.DrinkType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onManageBottle: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {

    val user by viewModel.user.collectAsState()
    val dailySummary by viewModel.dailySummary.collectAsState()
    val gamification by viewModel.gamification.collectAsState()
    val histories by viewModel.recentLogs.collectAsStateWithLifecycle()
    val nextReminderTime by viewModel.nextReminderTime.collectAsStateWithLifecycle()
    val slotBottles by viewModel.slotBottles.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(bottom = 16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                HeroSection(
                    user = user,
                    dailySummary = dailySummary,
                    gamification = gamification
                )
            }

            item {
                QuickRefillSection(
                    bottles = slotBottles,
                    onCustomQuickRefillClicked = onManageBottle,
                    onDrink = viewModel::drink
                )
            }

            drinkHistorySection(
                nextReminderTime = nextReminderTime,
                histories = histories
            )
        }
    }
}

@Composable
private fun HeroSection(
    user: UserEntity?,
    dailySummary: DailySummaryEntity?,
    gamification: UserGamificationEntity?,
    modifier: Modifier = Modifier
) {
    val totalMl = dailySummary?.totalMl ?: 0f
    val goalMl = dailySummary?.goalMl?.takeIf { it > 0 } ?: user?.dailyGoalMl ?: Constant.MIN_DAILY_GOAL
    val progress = if (goalMl > 0) totalMl / goalMl else 0f
    val currentStreak = gamification?.currentStreak ?: 0

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(top = 24.dp)
    ) {
        // Hero background card
        Card(
            shape = RoundedCornerShape(48.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(288.dp, 320.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Wave Animation
                WaveAnimation(
                    modifier = Modifier.fillMaxSize(),
                    progress = progress,
                    color = MaterialTheme.colorScheme.primary,
                    amplitude = 20f,
                    frequency = 0.015f
                )

                // Content Overlay
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .size(192.dp, 128.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.background)
                        .align(Alignment.Center)
                ) {
                    Text(
                        text = "Current Intake".uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                    )

                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = totalMl.toInt().toString(),
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontSize = 60.sp,
                                letterSpacing = (-3).sp
                            ),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "ml",
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }
                    Text(
                        text = "Daily Goal: ${goalMl.toInt()}ml",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }

        // Floating Streak Card
//        Card(
//            shape = RoundedCornerShape(32.dp),
//            modifier = Modifier
//                .align(Alignment.BottomCenter)
//                .padding(bottom = 24.dp)
//                .height(90.dp)
//                .width(268.dp),
//            colors = CardDefaults.cardColors(
//                containerColor = MaterialTheme.colorScheme.surface
//            ),
//            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
//        ) {
//            Row(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(horizontal = 21.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Box(
//                    modifier = Modifier
//                        .size(48.dp)
//                        .background(MaterialTheme.colorScheme.tertiaryContainer, CircleShape),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.FlashOn,
//                        contentDescription = null,
//                        tint = MaterialTheme.colorScheme.onTertiaryContainer,
//                        modifier = Modifier.size(22.dp)
//                    )
//                }
//                Spacer(modifier = Modifier.width(16.dp))
//                Column {
//                    Text(
//                        text = "Hydration Streak",
//                        style = MaterialTheme.typography.bodyMedium,
//                        fontWeight = FontWeight.Bold,
//                        color = MaterialTheme.colorScheme.onSurface
//                    )
//                    Text(
//                        text = "$currentStreak Days of reaching your goal!",
//                        style = MaterialTheme.typography.bodySmall,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant
//                    )
//                }
//            }
//        }
    }
}

@Composable
private fun QuickRefillSection(
    bottles: List<BottleEntity?>,
    onCustomQuickRefillClicked: () -> Unit,
    onDrink: (BottleEntity) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Quick Refill",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            IconButton(
                onClick = onCustomQuickRefillClicked
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
                    contentDescription = null
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            bottles.forEach { bottle ->
                QuickRefillButton(
                    bottle = bottle,
                    onClick = {
                        if (bottle != null) onDrink(bottle)
                        else onCustomQuickRefillClicked()
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun QuickRefillButton(
    bottle: BottleEntity?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val isSelected = false // We don't have selection state yet for drinking
    Card(
        shape = RoundedCornerShape(24.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = modifier
            .height(112.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (bottle != null) Icons.Default.FlashOn else Icons.Default.Add,
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = bottle?.volumeMl?.toInt()?.toString() ?: "Empty",
                style = MaterialTheme.typography.titleLarge,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            if (bottle != null) {
                Text(
                    text = "ml",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun LazyListScope.drinkHistorySection(
    nextReminderTime: Long?,
    histories: List<HydrationLogWithBottle>
) {
    item {
        Text(
            text = "Drink History",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        )
    }

    if (nextReminderTime != null) {
        item {
            TimelineItem(
                isFirst = true,
                isLast = histories.isEmpty(),
                isActive = true
            ) {
                NextReminderCard(
                    time = nextReminderTime,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    if (histories.isEmpty() && nextReminderTime == null) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No history available",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        items(
            items = histories,
            key = { it.log.id }
        ) { history ->
            val index = histories.indexOf(history)
            val isFirst = nextReminderTime == null && index == 0
            val isLast = index == histories.size - 1

            TimelineItem(
                isFirst = isFirst,
                isLast = isLast,
                isActive = false
            ) {
                DrinkHistoryItem(
                    history = history,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun TimelineItem(
    isFirst: Boolean,
    isLast: Boolean,
    isActive: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Row(
        modifier = modifier
            .padding(horizontal = 24.dp)
            .height(IntrinsicSize.Min)
    ) {
        TimelineNode(
            isFirst = isFirst,
            isLast = isLast,
            isActive = isActive,
            modifier = Modifier.fillMaxHeight()
        )
        
        Box(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .weight(1f)
        ) {
            content()
        }
    }
}

@Composable
private fun TimelineNode(
    isFirst: Boolean,
    isLast: Boolean,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.width(32.dp),
        contentAlignment = Alignment.Center
    ) {
        val lineColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        val activeColor = MaterialTheme.colorScheme.primary
        
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            
            if (!isFirst) {
                drawLine(
                    color = lineColor,
                    start = Offset(centerX, 0f),
                    end = Offset(centerX, centerY - (if (isActive) 12.dp.toPx() else 8.dp.toPx())),
                    strokeWidth = 2.dp.toPx()
                )
            }
            
            if (!isLast) {
                drawLine(
                    color = lineColor,
                    start = Offset(centerX, centerY + (if (isActive) 12.dp.toPx() else 8.dp.toPx())),
                    end = Offset(centerX, size.height),
                    strokeWidth = 2.dp.toPx()
                )
            }
        }
        
        Box(
            modifier = Modifier
                .size(if (isActive) 16.dp else 10.dp)
                .border(
                    width = 2.dp,
                    color = if (isActive) activeColor else lineColor,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isActive) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(activeColor, CircleShape)
                )
            }
        }
    }
}

@Composable
private fun NextReminderCard(
    time: Long,
    modifier: Modifier = Modifier
) {
    val timeFormatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val timeStr = timeFormatter.format(Date(time))
    
    Card(
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier.height(112.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Next Reminder",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = timeStr,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Keep it up! Your next hydration is coming up.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun DrinkHistoryItem(
    history: HydrationLogWithBottle,
    modifier: Modifier = Modifier
) {
    val timeFormatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val time = timeFormatter.format(Date(history.log.loggedAt))
    
    val name = history.log.bottleName ?: history.bottle?.name ?: history.log.drinkType.name
    val color = when (history.log.drinkType) {
        DrinkType.Water -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.secondary
    }
    val iconBg = color.copy(alpha = 0.1f)
    
    Card(
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier.height(74.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 17.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(iconBg, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FlashOn, // Placeholder
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = color
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = time,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Text(
                text = "+${history.log.amountMl.toInt()}ml",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}
