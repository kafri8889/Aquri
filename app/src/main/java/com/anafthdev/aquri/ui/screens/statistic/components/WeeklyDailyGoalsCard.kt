package com.anafthdev.aquri.ui.screens.statistic.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anafthdev.aquri.ui.screens.statistic.DailyGoalProgress
import com.anafthdev.aquri.ui.theme.AquriTheme

@Composable
fun WeeklyDailyGoalsCard(
    dailyGoals: List<DailyGoalProgress>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Daily goals",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                dailyGoals.forEach { goal ->
                    DailyGoalItem(
                        goal = goal,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun DailyGoalItem(
    goal: DailyGoalProgress,
    modifier: Modifier = Modifier
) {
    val inactiveColor = AquriTheme.colorScheme.lightText.copy(alpha = 0.32f)
    val activeColor = AquriTheme.colorScheme.lightText
    val errorColor = AquriTheme.colorScheme.error
    val successColor = AquriTheme.colorScheme.success

    val textColor = remember(goal) {
        if (goal.isSelected) {
            if (goal.progress * 100 < 100f) return@remember errorColor
            else return@remember successColor
        }

        activeColor
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "${(goal.progress * 100).toInt()}%",
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontWeight = if (goal.isSelected) FontWeight.Bold else FontWeight.Normal,
                color = textColor
            )
        )

        WaterDropProgress(
            progress = goal.progress,
            activeColor = MaterialTheme.colorScheme.primary,
            inactiveColor = inactiveColor,
            modifier = Modifier.size(32.dp)
        )

        Text(
            text = goal.dayName,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (goal.isSelected) FontWeight.Bold else FontWeight.Normal,
                color = textColor
            )
        )
    }
}

@Composable
private fun WaterDropProgress(
    progress: Float,
    activeColor: Color,
    inactiveColor: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        
        val dropPath = Path().apply {
            moveTo(width / 2f, 0f)
            cubicTo(
                width * 0.1f, height * 0.4f,
                0f, height * 0.7f,
                width / 2f, height
            )
            cubicTo(
                width, height * 0.7f,
                width * 0.9f, height * 0.4f,
                width / 2f, 0f
            )
            close()
        }

        // Draw background drop
        drawPath(
            path = dropPath,
            color = inactiveColor
        )

        // Draw filled drop using clipPath
        clipPath(dropPath) {
            val fillHeight = height * progress
            drawRect(
                color = activeColor,
                topLeft = androidx.compose.ui.geometry.Offset(0f, height - fillHeight),
                size = androidx.compose.ui.geometry.Size(width, fillHeight)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WeeklyDailyGoalsCardPreview() {
    AquriTheme {
        WeeklyDailyGoalsCard(
            dailyGoals = listOf(
                DailyGoalProgress("Mo", 1f, false),
                DailyGoalProgress("Tu", 1f, false),
                DailyGoalProgress("We", 0.4f, true),
                DailyGoalProgress("Th", 0.6f, false),
                DailyGoalProgress("Fr", 0.8f, false),
                DailyGoalProgress("Sa", 0f, false),
                DailyGoalProgress("Su", 0f, false)
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}
