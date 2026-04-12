package com.anafthdev.aquri.ui.screens.statistic.components

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
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
import com.patrykandpatrick.vico.compose.common.Fill
import com.patrykandpatrick.vico.compose.pie.PieChart
import com.patrykandpatrick.vico.compose.pie.PieChartHost
import com.patrykandpatrick.vico.compose.pie.data.PieChartModelProducer
import com.patrykandpatrick.vico.compose.pie.data.PieValueFormatter
import com.patrykandpatrick.vico.compose.pie.data.pieSeries
import com.patrykandpatrick.vico.compose.pie.rememberPieChart

@Composable
fun BeverageTypeCard(
    beverageDistribution: List<BeverageBreakdownData>,
    modifier: Modifier = Modifier
) {

    val yData = remember { mutableStateListOf<Float>().apply { add(0f) } }
    val chartColor = remember { mutableStateListOf<Color>().apply { add(Color.LightGray) } }
    val modelProducer = remember { PieChartModelProducer() }

    LaunchedEffect(beverageDistribution) {
        yData.clear()
        chartColor.clear()

        beverageDistribution.forEach {
            yData.add(it.totalMl)
            chartColor.add(Color(it.hexColor.toColorInt()))
        }

        modelProducer.runTransaction {
            pieSeries {
                series(yData)
            }
        }
    }

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
                text = "Beverage Types",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                PieChartHost(
                    modelProducer = modelProducer,
                    chart = rememberPieChart(
                        valueFormatter = PieValueFormatter { _, value, _ -> "" },
                        sliceProvider = PieChart.SliceProvider.series(
                            chartColor.mapIndexed { index, color ->
                                PieChart.Slice(
                                    fill = Fill(color)
                                )
                            }
                        ),
                    ),
                    modifier = Modifier
                        .size(128.dp)
                )

                // Legend (right side)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (beverageDistribution.isEmpty()) {
                        Text(
                            text = "No data recorded",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    } else {
                        beverageDistribution.forEach { item ->
                            BeverageLegendItem(item)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BeverageLegendItem(
    item: BeverageBreakdownData,
    modifier: Modifier = Modifier
) {
    val color = try { Color(item.hexColor.toColorInt()) } catch (e: Exception) { Color.Gray }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(color, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        modifier = Modifier.basicMarquee()
                    )
                    Text(
                        text = "${String.format("%,.0f", item.totalMl)} ml",
                        style = MaterialTheme.typography.labelSmall,
                        color = AquriTheme.colorScheme.lightText,
                        fontSize = 10.sp
                    )
                }
            }

            Text(
                text = "${item.percentage.toInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
        
        // Mini progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(CircleShape)
                .background(Color.LightGray.copy(alpha = 0.2f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(item.percentage / 100f)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BeverageTypeCardPreview() {
    AquriTheme {
        BeverageTypeCard(
            beverageDistribution = listOf(
                BeverageBreakdownData("Pure Water", 8400f, 57f, "#00ACC1"),
                BeverageBreakdownData("Tea & Coffee", 3200f, 22f, "#EF6C00"),
                BeverageBreakdownData("Juice & Other", 3100f, 21f, "#26A69A")
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}
