package com.anafthdev.aquri.ui.screens.statistic.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anafthdev.aquri.ui.screens.statistic.WeeklyComparisonData
import com.anafthdev.aquri.ui.theme.AquriTheme

@Composable
fun HistoryComparisonCard(
    data: WeeklyComparisonData?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "HISTORY COMPARISON",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = AquriTheme.colorScheme.lightText,
                letterSpacing = 1.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Last Week
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "LAST WEEK",
                        style = MaterialTheme.typography.labelSmall,
                        color = AquriTheme.colorScheme.lightText
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(12.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray.copy(alpha = 0.3f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(data?.lastWeekProgress ?: 0f)
                                .height(12.dp)
                                .clip(CircleShape)
                                .background(AquriTheme.colorScheme.lightText.copy(alpha = 0.5f))
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${String.format("%.1f", data?.lastWeekTotalLiters ?: 0f)}L",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.CompareArrows,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )

                // This Week
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "THIS WEEK",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(12.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(data?.thisWeekProgress ?: 0f)
                                .height(12.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${String.format("%.1f", data?.thisWeekTotalLiters ?: 0f)}L",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.32f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .padding(end = 8.dp)
                ) {
                    Text(
                        text = "Overall Trend",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AquriTheme.colorScheme.lightText
                    )
                    Text(
                        text = "Daily Average: ${String.format("%.0f", data?.averageDailyMl ?: 0f)}ml",
                        style = MaterialTheme.typography.labelSmall,
                        color = AquriTheme.colorScheme.lightText,
                    )
                }

                val trendColor = if ((data?.trendPercentage ?: 0f) >= 0) Color(0xFF00695C) else Color(0xFFC62828)
                val trendText = if ((data?.trendPercentage ?: 0f) >= 0) "+${String.format("%.1f", data?.trendPercentage)}%" else "${String.format("%.1f", data?.trendPercentage)}%"

                Text(
                    text = "$trendText completion",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = trendColor,
                    maxLines = 1,
                    autoSize = TextAutoSize.StepBased(
                        maxFontSize = MaterialTheme.typography.titleMedium.fontSize,
                        minFontSize = 6.sp
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HistoryComparisonCardPreview() {
    AquriTheme {
        HistoryComparisonCard(
            data = WeeklyComparisonData(
                thisWeekTotalLiters = 14.7f,
                thisWeekProgress = 0.85f,
                lastWeekTotalLiters = 13.1f,
                lastWeekProgress = 0.75f,
                trendPercentage = 12.2f,
                averageDailyMl = 2100f
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}
