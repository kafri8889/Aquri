package com.anafthdev.aquri.ui.screens.statistic

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anafthdev.aquri.ui.screens.statistic.components.BestWorstDaySection
import com.anafthdev.aquri.ui.screens.statistic.components.BeverageTypeCard
import com.anafthdev.aquri.ui.screens.statistic.components.DailyMainBarChart
import com.anafthdev.aquri.ui.screens.statistic.components.DailyStatisticsSection
import com.anafthdev.aquri.ui.screens.statistic.components.HistoryComparisonCard
import com.anafthdev.aquri.ui.screens.statistic.components.MonthlyMainBarChart
import com.anafthdev.aquri.ui.screens.statistic.components.StatisticFilterChips
import com.anafthdev.aquri.ui.screens.statistic.components.StatisticPeriodSelector
import com.anafthdev.aquri.ui.screens.statistic.components.WeeklyDailyGoalsCard
import com.anafthdev.aquri.ui.screens.statistic.components.WeeklyMainBarChart
import com.anafthdev.aquri.ui.theme.AquriTheme

@Composable
fun StatisticScreen(
    modifier: Modifier = Modifier,
    viewModel: StatisticViewModel = hiltViewModel()
) {
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val chartYData by viewModel.chartYData.collectAsStateWithLifecycle()
    val chartXData by viewModel.chartXData.collectAsStateWithLifecycle()
    val peakActivityHour by viewModel.peakActivityHour.collectAsStateWithLifecycle()
    val logCount by viewModel.logCount.collectAsStateWithLifecycle()
    val topBottleName by viewModel.topBottleName.collectAsStateWithLifecycle()
    val beverageDistribution by viewModel.detailedBeverageDistribution.collectAsStateWithLifecycle()
    val weeklyDailyGoals by viewModel.weeklyDailyGoals.collectAsStateWithLifecycle()
    val weeklyBestDay by viewModel.weeklyBestDay.collectAsStateWithLifecycle()
    val weeklyWorstDay by viewModel.weeklyWorstDay.collectAsStateWithLifecycle()
    val weeklyComparison by viewModel.weeklyComparison.collectAsStateWithLifecycle()
    val selectedDaySummary by viewModel.selectedDaySummary.collectAsStateWithLifecycle()

    StatisticScreenContent(
        selectedFilter = selectedFilter,
        selectedDate = selectedDate,
        mainChartYData = chartYData,
        mainChartXData = chartXData,
        peakActivityHour = peakActivityHour,
        logCount = logCount,
        topBottleName = topBottleName,
        beverageDistribution = beverageDistribution,
        weeklyDailyGoals = weeklyDailyGoals,
        weeklyBestDay = weeklyBestDay,
        weeklyWorstDay = weeklyWorstDay,
        weeklyComparison = weeklyComparison,
        totalMl = selectedDaySummary?.totalMl ?: 0f,
        goalMl = selectedDaySummary?.goalMl ?: 0f,
        onFilterSelected = viewModel::onFilterSelected,
        onPreviousPeriod = viewModel::previousPeriod,
        onNextPeriod = viewModel::nextPeriod,
        onDateSelected = viewModel::onDateSelected,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticScreenContent(
    selectedFilter: StatisticFilter,
    selectedDate: Long,
    mainChartYData: List<Float>,
    mainChartXData: List<String>,
    peakActivityHour: Int?,
    logCount: Int,
    topBottleName: String?,
    beverageDistribution: List<BeverageBreakdownData>,
    weeklyDailyGoals: List<DailyGoalProgress>,
    weeklyBestDay: DaySummaryData?,
    weeklyWorstDay: DaySummaryData?,
    weeklyComparison: WeeklyComparisonData?,
    totalMl: Float,
    goalMl: Float,
    onFilterSelected: (StatisticFilter) -> Unit,
    onPreviousPeriod: () -> Unit,
    onNextPeriod: () -> Unit,
    onDateSelected: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDateSelected(datePickerState.selectedDateMillis)
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                MaterialTheme.colorScheme.background
                            ),
                            start = Offset(Float.POSITIVE_INFINITY, 0f),
                            end = Offset(0f, Float.POSITIVE_INFINITY)
                        )
                    )
            )
            
            LazyColumn(
                contentPadding = PaddingValues(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .zIndex(1f)
            ) {
                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 24.dp)
                    ) {
                        Text(
                            text = "${selectedFilter.name.uppercase()} ANALYTICS",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Hydration Flow",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                item {
                    StatisticFilterChips(
                        selectedFilter = selectedFilter,
                        onFilterSelected = onFilterSelected,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                item {
                    StatisticPeriodSelector(
                        selectedFilter = selectedFilter,
                        selectedDate = selectedDate,
                        onPrevious = onPreviousPeriod,
                        onNext = onNextPeriod,
                        onTextClick = { showDatePicker = true },
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                }

                if (selectedFilter == StatisticFilter.Daily) {
                    item {
                        DailyStatisticsSection(
                            totalMl = totalMl,
                            goalMl = goalMl,
                            modifier = Modifier
                                .padding(vertical = 32.dp)
                                .animateItem()
                        )
                    }

                    item {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Min)
                                .padding(horizontal = 16.dp)
                                .animateItem()
                        ) {
                            DailyStatCard(
                                icon = Icons.Default.History,
                                iconColor = Color(0xFFFFA726),
                                label = "LOG COUNT",
                                value = "$logCount times",
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                            )
                            DailyStatCard(
                                icon = Icons.Default.LocalDrink,
                                iconColor = Color(0xFF00ACC1),
                                label = "TOP BOTTLE",
                                value = topBottleName ?: "-",
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    item {
                        DailyMainBarChart(
                            chartYData = mainChartYData,
                            chartXData = mainChartXData,
                            peakActivityHour = peakActivityHour,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .animateItem()
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (selectedFilter == StatisticFilter.Weekly) {
                    item {
                        WeeklyMainBarChart(
                            chartYData = mainChartYData,
                            chartXData = mainChartXData,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .animateItem()
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    item {
                        WeeklyDailyGoalsCard(
                            dailyGoals = weeklyDailyGoals,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .animateItem()
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    item {
                        BestWorstDaySection(
                            bestDay = weeklyBestDay,
                            worstDay = weeklyWorstDay,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .animateItem()
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    item {
                        HistoryComparisonCard(
                            data = weeklyComparison,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .animateItem()
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                if (selectedFilter == StatisticFilter.Monthly) {
                    item {
                        MonthlyMainBarChart(
                            chartYData = mainChartYData,
                            chartXData = mainChartXData,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .animateItem()
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                item {
                    BeverageTypeCard(
                        beverageDistribution = beverageDistribution,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .animateItem()
                    )
                }
            }
        }
    }
}

@Composable
private fun DailyStatCard(
    icon: ImageVector,
    iconColor: Color,
    label: String,
    value: String,
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
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
            
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray,
                letterSpacing = 1.sp
            )
            
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StatisticScreenPreview() {
    AquriTheme {
        StatisticScreenContent(
            selectedFilter = StatisticFilter.Weekly,
            mainChartYData = listOf(5f, 6f, 5f, 2f, 11f, 8f, 5f),
            mainChartXData = listOf("A", "B", "C", "D", "E", "F", "G"),
            peakActivityHour = 8,
            logCount = 7,
            topBottleName = "Glass Cup",
            beverageDistribution = listOf(
                BeverageBreakdownData("Pure Water", 8400f, 57f, "#00ACC1"),
                BeverageBreakdownData("Tea & Coffee", 3200f, 22f, "#EF6C00"),
                BeverageBreakdownData("Juice & Other", 3100f, 21f, "#26A69A")
            ),
            weeklyDailyGoals = listOf(
                DailyGoalProgress("Mo", 1f, false),
                DailyGoalProgress("Tu", 1f, false),
                DailyGoalProgress("We", 0.4f, true),
                DailyGoalProgress("Th", 0.6f, false),
                DailyGoalProgress("Fr", 0.8f, false),
                DailyGoalProgress("Sa", 0f, false),
                DailyGoalProgress("Su", 0f, false)
            ),
            weeklyBestDay = DaySummaryData("Wednesday", 2800f),
            weeklyWorstDay = DaySummaryData("Monday", 1200f),
            weeklyComparison = WeeklyComparisonData(14.7f, 0.85f, 13.1f, 0.75f, 12.2f, 2100f),
            totalMl = 1650f,
            goalMl = 2500f,
            selectedDate = System.currentTimeMillis(),
            onFilterSelected = {},
            onPreviousPeriod = {},
            onNextPeriod = {},
            onDateSelected = {}
        )
    }
}
