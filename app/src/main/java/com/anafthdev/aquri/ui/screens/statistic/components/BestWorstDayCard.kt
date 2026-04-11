package com.anafthdev.aquri.ui.screens.statistic.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anafthdev.aquri.ui.screens.statistic.DaySummaryData
import com.anafthdev.aquri.ui.theme.AquriTheme

@Composable
fun BestWorstDaySection(
    bestDay: DaySummaryData?,
    worstDay: DaySummaryData?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BestWorstDayCard(
            title = "BEST DAY",
            dayName = bestDay?.dayName ?: "-",
            amountMl = bestDay?.totalMl ?: 0f,
            icon = Icons.Default.MilitaryTech,
            accentColor = AquriTheme.colorScheme.success,
            modifier = Modifier.weight(1f).fillMaxHeight()
        )
        BestWorstDayCard(
            title = "WORST DAY",
            dayName = worstDay?.dayName ?: "-",
            amountMl = worstDay?.totalMl ?: 0f,
            icon = Icons.AutoMirrored.Filled.TrendingDown,
            accentColor = AquriTheme.colorScheme.error,
            modifier = Modifier.weight(1f).fillMaxHeight()
        )
    }
}

@Composable
private fun BestWorstDayCard(
    title: String,
    dayName: String,
    amountMl: Float,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxHeight()
        ) {
            // Left accent bar
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(accentColor)
            )
            
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
                
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    letterSpacing = 1.sp
                )
                
                Text(
                    text = dayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                
                Text(
                    text = "${String.format("%,.0f", amountMl)}ml",
                    style = MaterialTheme.typography.bodySmall,
                    color = accentColor,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BestWorstDaySectionPreview() {
    AquriTheme {
        BestWorstDaySection(
            bestDay = DaySummaryData("Wednesday", 2800f),
            worstDay = DaySummaryData("Monday", 1200f),
            modifier = Modifier.padding(16.dp)
        )
    }
}
