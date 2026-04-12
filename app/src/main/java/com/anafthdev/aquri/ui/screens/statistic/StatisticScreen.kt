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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anafthdev.aquri.ui.screens.statistic.components.BestWorstDaySection
import com.anafthdev.aquri.ui.screens.statistic.components.BeverageTypeCard
import com.anafthdev.aquri.ui.screens.statistic.components.DailyStatisticsSection
import com.anafthdev.aquri.ui.screens.statistic.components.HistoryComparisonCard
import com.anafthdev.aquri.ui.screens.statistic.components.StatisticFilterChips
import com.anafthdev.aquri.ui.screens.statistic.components.StatisticPeriodSelector
import com.anafthdev.aquri.ui.screens.statistic.components.WeeklyDailyGoalsCard
import com.anafthdev.aquri.ui.screens.statistic.components.rememberMarker
import com.anafthdev.aquri.ui.theme.AquriTheme
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.compose.cartesian.data.columnSeries
import com.patrykandpatrick.vico.compose.cartesian.layer.CartesianLayerPadding
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.ColumnCartesianLayerMarkerTarget
import com.patrykandpatrick.vico.compose.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.data.ExtraStore
import kotlinx.coroutines.runBlocking

@Composable
fun StatisticScreen(
    modifier: Modifier = Modifier,
    viewModel: StatisticViewModel = hiltViewModel()
) {
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val chartYData by viewModel.chartData.collectAsStateWithLifecycle()
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
                        DailyMainBarChart( // todo implement weekly chart
                            chartYData = mainChartYData,
                            peakActivityHour = peakActivityHour,
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

@Composable
private fun DailyMainBarChart(
    chartYData: List<Float>,
    peakActivityHour: Int?,
    modifier: Modifier = Modifier
) {

    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(chartYData) {
        modelProducer.runTransaction {
            columnSeries {
                series(chartYData)
            }
        }
    }

    if (LocalInspectionMode.current) {
        runBlocking {
            modelProducer.runTransaction {
                columnSeries {
                    series(chartYData)
                }
            }
        }
    }

    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val is24Hour = android.text.format.DateFormat.is24HourFormat(context)

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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.Start)
            ) {
                Text(
                    text = "Hourly Intake",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )

                val hourStr = remember(configuration, peakActivityHour, is24Hour) {
                    if (peakActivityHour == null) return@remember "-"

                    val locale = configuration.locales[0]

                    val formatHour: (Int) -> String = { h ->
                        if (is24Hour) {
                            String.format(locale, "%02d:00", h)
                        } else {
                            when {
                                h == 0 -> "12 AM"
                                h < 12 -> "$h AM"
                                h == 12 -> "12 PM"
                                else -> "${h - 12} PM"
                            }
                        }
                    }

                    val start = formatHour(peakActivityHour)
                    val next = (peakActivityHour + 1) % 24
                    val end = formatHour(next)
                    "$start - $end"
                }

                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = AquriTheme.colorScheme.lightText
                            ).toSpanStyle()
                        ) {
                            append("Peak activity at")
                            append(" ")
                        }

                        withStyle(
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            ).toSpanStyle()
                        ) {
                            append(hourStr)
                        }
                    }
                )
            }

            CartesianChartHost(
                modelProducer = modelProducer,
                chart = rememberCartesianChart(
                    rememberColumnCartesianLayer(),
                    startAxis = VerticalAxis.rememberStart(
                        valueFormatter = StartAxisValueFormatter
                    ),
                    bottomAxis = HorizontalAxis.rememberBottom(
                        guideline = null
                    ),
                    layerPadding = {
                        CartesianLayerPadding(
                            scalableStart = 8.dp,
                            scalableEnd = 8.dp
                        )
                    },
                    marker = rememberMarker(WaterVolumeValueFormatter)
                ),
                modifier = Modifier
                    .height(216.dp)
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

private val BottomAxisLabelKey = ExtraStore.Key<List<String>>()

private val StartAxisValueFormatter = CartesianValueFormatter { _, value, _ ->
    "${value.toInt()}ml"
}

private val BottomAxisValueFormatter = CartesianValueFormatter { context, x, _ ->
    context.model.extraStore[BottomAxisLabelKey][x.toInt()]
}

private val WaterVolumeValueFormatter =
    DefaultCartesianMarker.ValueFormatter { _, targets ->
        val column = (targets[0] as ColumnCartesianLayerMarkerTarget).columns[0]
        buildAnnotatedString {
            withStyle(SpanStyle(column.color)) {
                val value = column.entry.y.toInt().toString()
                append(value)
                append("ml")
            }
        }
    }
