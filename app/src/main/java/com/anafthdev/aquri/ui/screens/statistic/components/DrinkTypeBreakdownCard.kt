package com.anafthdev.aquri.ui.screens.statistic.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.core.graphics.toColorInt
import com.anafthdev.aquri.ui.screens.statistic.BeverageBreakdownData
import com.anafthdev.aquri.ui.theme.AquriTheme

@Composable
fun DrinkTypeBreakdownCard(
    items: List<BeverageBreakdownData>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "DRINK TYPE BREAKDOWN",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                letterSpacing = 1.sp
            )

            if (items.isEmpty()) {
                Text(
                    text = "No data recorded for this week",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            } else {
                items.forEach { item ->
                    BreakdownItem(item)
                }
            }
        }
    }
}

@Composable
private fun BreakdownItem(item: BeverageBreakdownData) {
    val color = try { Color(item.hexColor.toColorInt()) } catch (e: Exception) { Color.Gray }
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (item.name.lowercase().contains("water")) Icons.Default.WaterDrop else Icons.Default.LocalDrink,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }

        // Details
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${String.format("%,.0f", item.totalMl)} ml",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }

        // Progress & Percentage
        Column(
            modifier = Modifier.width(100.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "${item.percentage.toInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray.copy(alpha = 0.2f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(item.percentage / 100f)
                        .height(6.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DrinkTypeBreakdownCardPreview() {
    AquriTheme {
        DrinkTypeBreakdownCard(
            items = listOf(
                BeverageBreakdownData("Pure Water", 8400f, 57f, "#00ACC1"),
                BeverageBreakdownData("Tea & Coffee", 3200f, 22f, "#EF6C00"),
                BeverageBreakdownData("Juice & Other", 3100f, 21f, "#26A69A")
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}
