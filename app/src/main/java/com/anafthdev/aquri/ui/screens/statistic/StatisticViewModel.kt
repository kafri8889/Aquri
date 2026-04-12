package com.anafthdev.aquri.ui.screens.statistic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anafthdev.aquri.data.Constant
import com.anafthdev.aquri.data.model.entity.DailySummaryEntity
import com.anafthdev.aquri.data.model.entity.DrinkTypeEntity
import com.anafthdev.aquri.data.model.entity.HydrationLogWithBottle
import com.anafthdev.aquri.data.model.entity.UserEntity
import com.anafthdev.aquri.data.repository.HydrationRepository
import com.anafthdev.aquri.data.repository.UserRepository
import com.anafthdev.aquri.utils.DateTimeUtils
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import java.util.Calendar
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class StatisticViewModel @Inject constructor(
    private val hydrationRepository: HydrationRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _selectedFilter = MutableStateFlow(StatisticFilter.Daily)
    val selectedFilter: StateFlow<StatisticFilter> = _selectedFilter.asStateFlow()

    private val _selectedDate = MutableStateFlow(System.currentTimeMillis())
    val selectedDate: StateFlow<Long> = _selectedDate.asStateFlow()

    val mainChartModelProducer = CartesianChartModelProducer()

    private val _chartData = MutableStateFlow<List<Float>>(emptyList())
    val chartData: StateFlow<List<Float>> = _chartData.asStateFlow()

    private val _goalData = MutableStateFlow<List<Float>>(emptyList())
    val goalData: StateFlow<List<Float>> = _goalData.asStateFlow()

    private val _targetGoal = MutableStateFlow(0f)
    val targetGoal: StateFlow<Float> = _targetGoal.asStateFlow()

    private val _peakActivityHour = MutableStateFlow<Int?>(null)
    val peakActivityHour: StateFlow<Int?> = _peakActivityHour.asStateFlow()

    private val _logCount = MutableStateFlow(0)
    val logCount: StateFlow<Int> = _logCount.asStateFlow()

    private val _topBottleName = MutableStateFlow<String?>(null)
    val topBottleName: StateFlow<String?> = _topBottleName.asStateFlow()

    private val _detailedBeverageDistribution = MutableStateFlow<List<BeverageBreakdownData>>(emptyList())
    val detailedBeverageDistribution: StateFlow<List<BeverageBreakdownData>> = _detailedBeverageDistribution.asStateFlow()

    private val _weeklyDailyGoals = MutableStateFlow<List<DailyGoalProgress>>(emptyList())
    val weeklyDailyGoals: StateFlow<List<DailyGoalProgress>> = _weeklyDailyGoals.asStateFlow()

    private val _weeklyBestDay = MutableStateFlow<DaySummaryData?>(null)
    val weeklyBestDay: StateFlow<DaySummaryData?> = _weeklyBestDay.asStateFlow()

    private val _weeklyWorstDay = MutableStateFlow<DaySummaryData?>(null)
    val weeklyWorstDay: StateFlow<DaySummaryData?> = _weeklyWorstDay.asStateFlow()

    private val _weeklyComparison = MutableStateFlow<WeeklyComparisonData?>(null)
    val weeklyComparison: StateFlow<WeeklyComparisonData?> = _weeklyComparison.asStateFlow()

    val user = userRepository.getUser()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val dailySummaries: StateFlow<List<DailySummaryEntity>> = user
        .filterNotNull()
        .flatMapLatest { user ->
            hydrationRepository.getDailySummaries(user.id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allLogs: StateFlow<List<HydrationLogWithBottle>> = user
        .filterNotNull()
        .flatMapLatest { user ->
            hydrationRepository.getLogsWithBottle(user.id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val drinkTypes: StateFlow<List<DrinkTypeEntity>> = hydrationRepository.getAllDrinkTypes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val selectedDateLogs: StateFlow<List<HydrationLogWithBottle>> = _selectedDate
        .flatMapLatest { date ->
            val midnight = Calendar.getInstance().apply {
                timeInMillis = date
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            hydrationRepository.getLogsWithBottleByDate(midnight)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val selectedDaySummary: StateFlow<DailySummaryEntity?> = combine(_selectedDate, dailySummaries) { date, summaries ->
        summaries.find { DateTimeUtils.isSameDay(it.summaryDate, date) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    init {
        combine(_selectedFilter, _selectedDate, dailySummaries, selectedDateLogs, allLogs, drinkTypes) { args ->
            val filter = args[0] as StatisticFilter
            val date = args[1] as Long
            val summaries = args[2] as List<DailySummaryEntity>
            val logs = args[3] as List<HydrationLogWithBottle>
            val allLogs = args[4] as List<HydrationLogWithBottle>
            val types = args[5] as List<DrinkTypeEntity>
            val user = user.value

            val data = prepareData(filter, date, summaries, logs)
            _chartData.value = data
            
            _goalData.value = calculateGoalYChartData(filter, date, summaries, user)
            _targetGoal.value = calculateTargetGoal(filter, date, summaries, user)
            
            _detailedBeverageDistribution.value = calculateDetailedBeverageBreakdown(filter, date, allLogs, types)

            if (filter == StatisticFilter.Weekly) {
                _weeklyDailyGoals.value = calculateWeeklyDailyGoals(date, summaries)
                
                val weekSummaries = getWeekSummaries(date, summaries)
                val bestDay = weekSummaries.maxByOrNull { it.totalMl }?.toDaySummaryData()
                val worstDay = weekSummaries.minByOrNull { it.totalMl }?.toDaySummaryData()

                _weeklyBestDay.value = bestDay
                _weeklyWorstDay.value = if (bestDay == worstDay) null else worstDay
                _weeklyComparison.value = calculateWeeklyComparison(date, summaries)
            } else {
                _weeklyDailyGoals.value = emptyList()
                _weeklyBestDay.value = null
                _weeklyWorstDay.value = null
                _weeklyComparison.value = null
            }

            if (filter == StatisticFilter.Daily) {
                _peakActivityHour.value = data.indexOfMax()?.takeIf { data[it] > 0 }
                _logCount.value = logs.size
                _topBottleName.value = logs
                    .groupBy { it.log.bottleName ?: it.bottle?.name ?: "Unknown" }
                    .maxByOrNull { it.value.size }
                    ?.key
            } else {
                _peakActivityHour.value = null
                _logCount.value = 0
                _topBottleName.value = null
            }
        }.launchIn(viewModelScope)
    }

    private fun List<Float>.indexOfMax(): Int? {
        if (isEmpty()) return null
        var maxIndex = 0
        for (i in indices) {
            if (this[i] > this[maxIndex]) maxIndex = i
        }
        return maxIndex
    }

    private fun getFilteredLogs(
        filter: StatisticFilter,
        date: Long,
        logs: List<HydrationLogWithBottle>
    ): List<HydrationLogWithBottle> {
        val calendar = Calendar.getInstance().apply { timeInMillis = date }
        return when (filter) {
            StatisticFilter.Daily -> logs.filter { DateTimeUtils.isSameDay(it.log.logDate, date) }
            StatisticFilter.Weekly -> {
                val (start, end) = DateTimeUtils.getWeekRange(date)
                // getWeekRange currently returns start of Sunday, so we make it inclusive of Sunday
                val inclusiveEnd = Calendar.getInstance().apply {
                    timeInMillis = end
                    set(Calendar.HOUR_OF_DAY, 23)
                    set(Calendar.MINUTE, 59)
                    set(Calendar.SECOND, 59)
                    set(Calendar.MILLISECOND, 999)
                }.timeInMillis
                logs.filter { it.log.logDate in start..inclusiveEnd }
            }
            StatisticFilter.Monthly -> {
                val cal = calendar.clone() as Calendar
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                val start = cal.timeInMillis
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
                cal.set(Calendar.HOUR_OF_DAY, 23)
                cal.set(Calendar.MINUTE, 59)
                cal.set(Calendar.SECOND, 59)
                cal.set(Calendar.MILLISECOND, 999)
                val end = cal.timeInMillis
                logs.filter { it.log.logDate in start..end }
            }
            StatisticFilter.Yearly -> {
                val cal = calendar.clone() as Calendar
                cal.set(Calendar.DAY_OF_YEAR, 1)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                val start = cal.timeInMillis
                cal.set(Calendar.DAY_OF_YEAR, cal.getActualMaximum(Calendar.DAY_OF_YEAR))
                cal.set(Calendar.HOUR_OF_DAY, 23)
                cal.set(Calendar.MINUTE, 59)
                cal.set(Calendar.SECOND, 59)
                cal.set(Calendar.MILLISECOND, 999)
                val end = cal.timeInMillis
                logs.filter { it.log.logDate in start..end }
            }
        }
    }

    private fun calculateWeeklyDailyGoals(
        date: Long,
        summaries: List<DailySummaryEntity>
    ): List<DailyGoalProgress> {
        val (start, _) = DateTimeUtils.getWeekRange(date)
        val calendar = Calendar.getInstance().apply { timeInMillis = start }
        
        val dayNames = listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su")
        
        return List(7) { i ->
            val currentDay = calendar.timeInMillis
            val summary = summaries.find { DateTimeUtils.isSameDay(it.summaryDate, currentDay) }
            val isSelectedDay = DateTimeUtils.isSameDay(currentDay, date)
            
            val progress = summary?.completionPct ?: 0f
            
            val goal = DailyGoalProgress(
                dayName = dayNames[i],
                progress = progress,
                isSelected = isSelectedDay
            )
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            goal
        }
    }

    private fun getWeekSummaries(date: Long, summaries: List<DailySummaryEntity>): List<DailySummaryEntity> {
        val (start, end) = DateTimeUtils.getWeekRange(date)

        Timber.i("makse star: $start")
        Timber.i("makse en: $end")

        return summaries.filter { it.summaryDate in start..end }
    }

    private fun calculateWeeklyComparison(date: Long, summaries: List<DailySummaryEntity>): WeeklyComparisonData {
        val (thisWeekStart, thisWeekEnd) = DateTimeUtils.getWeekRange(date)
        val thisWeekSummaries = summaries.filter { it.summaryDate in thisWeekStart..thisWeekEnd }
        val thisWeekTotal = thisWeekSummaries.sumOf { it.totalMl.toDouble() }.toFloat()
        val thisWeekGoal = thisWeekSummaries.sumOf { it.goalMl.toDouble() }.toFloat().takeIf { it > 0 } ?: (2500f * 7) // fallback to default
        
        // Last Week
        val (lastWeekStart, lastWeekEnd) = DateTimeUtils.getWeekRange(thisWeekStart - 24 * 60 * 60 * 1000)
        val lastWeekSummaries = summaries.filter { it.summaryDate in lastWeekStart..lastWeekEnd }
        val lastWeekTotal = lastWeekSummaries.sumOf { it.totalMl.toDouble() }.toFloat()
        val lastWeekGoal = lastWeekSummaries.sumOf { it.goalMl.toDouble() }.toFloat().takeIf { it > 0 } ?: (2500f * 7)
        
        val trend = if (lastWeekTotal > 0) ((thisWeekTotal - lastWeekTotal) / lastWeekTotal) * 100f else 0f
        
        // Average Daily
        val activeDays = thisWeekSummaries.filter { it.totalMl > 0 }.size
        val averageDaily = if (activeDays > 0) thisWeekTotal / activeDays else 0f

        return WeeklyComparisonData(
            thisWeekTotalLiters = thisWeekTotal / 1000f,
            thisWeekProgress = (thisWeekTotal / thisWeekGoal).coerceIn(0f, 1f),
            lastWeekTotalLiters = lastWeekTotal / 1000f,
            lastWeekProgress = (lastWeekTotal / lastWeekGoal).coerceIn(0f, 1f),
            trendPercentage = trend,
            averageDailyMl = averageDaily
        )
    }

    private fun calculateDetailedBeverageBreakdown(
        filter: StatisticFilter,
        date: Long,
        logs: List<HydrationLogWithBottle>,
        types: List<DrinkTypeEntity>
    ): List<BeverageBreakdownData> {
        val filteredLogs = getFilteredLogs(filter, date, logs)
        if (filteredLogs.isEmpty()) return emptyList()

        val totalAmount = filteredLogs.sumOf { it.log.amountMl.toDouble() }.toFloat()

        return filteredLogs
            .groupBy { it.log.drinkTypeId }
            .mapNotNull { (typeId, drinkLogs) ->
                val type = types.find { it.id == typeId } ?: return@mapNotNull null
                val drinkAmount = drinkLogs.sumOf { it.log.amountMl.toDouble() }.toFloat()
                BeverageBreakdownData(
                    name = type.name,
                    totalMl = drinkAmount,
                    percentage = (drinkAmount / totalAmount) * 100f,
                    hexColor = type.hexColor
                )
            }.sortedByDescending { it.percentage }
    }

    private fun DailySummaryEntity.toDaySummaryData(): DaySummaryData {
        val calendar = Calendar.getInstance().apply { timeInMillis = summaryDate }
        val dayName = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, java.util.Locale.getDefault()) ?: ""
        return DaySummaryData(dayName, totalMl)
    }

    /**
     * Mengembalikan target goal berdasarkan filter untuk penggunaan chart
     */
    private fun calculateGoalYChartData(
        filter: StatisticFilter,
        date: Long,
        summaries: List<DailySummaryEntity>,
        user: UserEntity?
    ): List<Float> {
        val defaultGoal = user?.dailyGoalMl ?: 2500f
        
        return when (filter) {
            StatisticFilter.Daily -> List(24) { defaultGoal }
            StatisticFilter.Weekly -> {
                val (start, _) = DateTimeUtils.getWeekRange(date)
                val cal = Calendar.getInstance().apply { timeInMillis = start }
                List(7) {
                    val currentDay = cal.timeInMillis
                    val summary = summaries.find { DateTimeUtils.isSameDay(it.summaryDate, currentDay) }
                    val goal = summary?.goalMl ?: defaultGoal
                    cal.add(Calendar.DAY_OF_YEAR, 1)
                    goal
                }
            }
            StatisticFilter.Monthly -> {
                val cal = Calendar.getInstance().apply { 
                    timeInMillis = date
                    set(Calendar.DAY_OF_MONTH, 1)
                }
                val maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
                List(maxDay) {
                    val currentDay = cal.timeInMillis
                    val summary = summaries.find { DateTimeUtils.isSameDay(it.summaryDate, currentDay) }
                    val goal = summary?.goalMl ?: defaultGoal
                    cal.add(Calendar.DAY_OF_YEAR, 1)
                    goal
                }
            }
            StatisticFilter.Yearly -> {
                val cal = Calendar.getInstance().apply { timeInMillis = date }
                val currentYear = cal.get(Calendar.YEAR)
                List(12) { month ->
                    val monthCal = Calendar.getInstance().apply {
                        set(Calendar.YEAR, currentYear)
                        set(Calendar.MONTH, month)
                        set(Calendar.DAY_OF_MONTH, 1)
                    }
                    val daysInMonth = monthCal.getActualMaximum(Calendar.DAY_OF_MONTH)
                    var monthGoal = 0f
                    repeat(daysInMonth) {
                        val dayTs = monthCal.timeInMillis
                        monthGoal += summaries.find { DateTimeUtils.isSameDay(it.summaryDate, dayTs) }?.goalMl ?: defaultGoal
                        monthCal.add(Calendar.DAY_OF_YEAR, 1)
                    }
                    monthGoal
                }
            }
        }
    }

    private fun calculateTargetGoal(
        filter: StatisticFilter,
        date: Long,
        summaries: List<DailySummaryEntity>,
        user: UserEntity?
    ): Float {
        val defaultGoal = user?.dailyGoalMl ?: Constant.MIN_DAILY_GOAL

        return when (filter) {
            StatisticFilter.Daily -> {
                summaries.find { DateTimeUtils.isSameDay(it.summaryDate, date) }?.goalMl ?: defaultGoal
            }
            StatisticFilter.Weekly -> {
                val (start, _) = DateTimeUtils.getWeekRange(date)
                val cal = Calendar.getInstance().apply { timeInMillis = start }
                var total = 0f
                repeat(7) {
                    val dayTs = cal.timeInMillis
                    total += summaries.find { DateTimeUtils.isSameDay(it.summaryDate, dayTs) }?.goalMl ?: defaultGoal
                    cal.add(Calendar.DAY_OF_YEAR, 1)
                }
                total
            }
            StatisticFilter.Monthly -> {
                val cal = Calendar.getInstance().apply { 
                    timeInMillis = date
                    set(Calendar.DAY_OF_MONTH, 1)
                }
                val maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
                var total = 0f
                repeat(maxDay) {
                    val dayTs = cal.timeInMillis
                    total += summaries.find { DateTimeUtils.isSameDay(it.summaryDate, dayTs) }?.goalMl ?: defaultGoal
                    cal.add(Calendar.DAY_OF_YEAR, 1)
                }
                total
            }
            StatisticFilter.Yearly -> {
                val cal = Calendar.getInstance().apply { timeInMillis = date }
                val currentYear = cal.get(Calendar.YEAR)
                var total = 0f
                repeat(12) { month ->
                    val monthCal = Calendar.getInstance().apply {
                        set(Calendar.YEAR, currentYear)
                        set(Calendar.MONTH, month)
                        set(Calendar.DAY_OF_MONTH, 1)
                    }
                    val daysInMonth = monthCal.getActualMaximum(Calendar.DAY_OF_MONTH)
                    repeat(daysInMonth) {
                        val dayTs = monthCal.timeInMillis
                        total += summaries.find { DateTimeUtils.isSameDay(it.summaryDate, dayTs) }?.goalMl ?: defaultGoal
                        monthCal.add(Calendar.DAY_OF_YEAR, 1)
                    }
                }
                total
            }
        }
    }

    private fun prepareData(
        filter: StatisticFilter,
        date: Long,
        summaries: List<DailySummaryEntity>,
        logs: List<HydrationLogWithBottle>
    ): List<Float> {
        val calendar = Calendar.getInstance().apply { timeInMillis = date }
        
        return when (filter) {
            StatisticFilter.Daily -> {
                // Hourly data (0-23 hours)
                val hourlyData = FloatArray(24) { 0f }
                logs.forEach { logWithBottle ->
                    val logCal = Calendar.getInstance().apply { timeInMillis = logWithBottle.log.loggedAt }
                    val hour = logCal.get(Calendar.HOUR_OF_DAY)
                    if (hour in 0..23) {
                        hourlyData[hour] += logWithBottle.log.amountMl
                    }
                }
                hourlyData.toList()
            }
            StatisticFilter.Weekly -> {
                val (start, _) = DateTimeUtils.getWeekRange(date)
                val cal = Calendar.getInstance().apply { timeInMillis = start }
                val weekData = mutableListOf<Float>()
                repeat(7) {
                    val currentDay = cal.timeInMillis
                    val summary = summaries.find { DateTimeUtils.isSameDay(it.summaryDate, currentDay) }
                    weekData.add(summary?.totalMl ?: 0f)
                    cal.add(Calendar.DAY_OF_YEAR, 1)
                }
                weekData
            }
            StatisticFilter.Monthly -> {
                val cal = calendar.clone() as Calendar
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                val maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
                val monthData = mutableListOf<Float>()
                repeat(maxDay) {
                    val currentDay = cal.timeInMillis
                    val summary = summaries.find { DateTimeUtils.isSameDay(it.summaryDate, currentDay) }
                    monthData.add(summary?.totalMl ?: 0f)
                    cal.add(Calendar.DAY_OF_YEAR, 1)
                }
                monthData
            }
            StatisticFilter.Yearly -> {
                val currentYear = calendar.get(Calendar.YEAR)
                val yearData = mutableListOf<Float>()
                repeat(12) { month ->
                    val totalMonthMl = summaries.filter {
                        val sCal = Calendar.getInstance().apply { timeInMillis = it.summaryDate }
                        sCal.get(Calendar.YEAR) == currentYear && sCal.get(Calendar.MONTH) == month
                    }.sumOf { it.totalMl.toDouble() }.toFloat()
                    yearData.add(totalMonthMl)
                }
                yearData
            }
        }
    }

    fun onFilterSelected(filter: StatisticFilter) {
        _selectedFilter.value = filter
        _selectedDate.value = System.currentTimeMillis()
    }

    fun onDateSelected(date: Long?) {
        if (date != null) {
            _selectedDate.value = date
        }
    }

    fun nextPeriod() {
        val calendar = Calendar.getInstance().apply { timeInMillis = _selectedDate.value }
        when (_selectedFilter.value) {
            StatisticFilter.Daily -> calendar.add(Calendar.DAY_OF_YEAR, 1)
            StatisticFilter.Weekly -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
            StatisticFilter.Monthly -> calendar.add(Calendar.MONTH, 1)
            StatisticFilter.Yearly -> calendar.add(Calendar.YEAR, 1)
        }
        _selectedDate.value = calendar.timeInMillis
    }

    fun previousPeriod() {
        val calendar = Calendar.getInstance().apply { timeInMillis = _selectedDate.value }
        when (_selectedFilter.value) {
            StatisticFilter.Daily -> calendar.add(Calendar.DAY_OF_YEAR, -1)
            StatisticFilter.Weekly -> calendar.add(Calendar.WEEK_OF_YEAR, -1)
            StatisticFilter.Monthly -> calendar.add(Calendar.MONTH, -1)
            StatisticFilter.Yearly -> calendar.add(Calendar.YEAR, -1)
        }
        _selectedDate.value = calendar.timeInMillis
    }
}

enum class StatisticFilter {
    Daily,
    Weekly,
    Monthly,
    Yearly
}

data class DailyGoalProgress(
    val dayName: String,
    val progress: Float,
    val isSelected: Boolean
)

data class DaySummaryData(
    val dayName: String,
    val totalMl: Float
)

data class WeeklyComparisonData(
    val thisWeekTotalLiters: Float,
    val thisWeekProgress: Float,
    val lastWeekTotalLiters: Float,
    val lastWeekProgress: Float,
    val trendPercentage: Float,
    val averageDailyMl: Float
)

data class BeverageBreakdownData(
    val name: String,
    val totalMl: Float,
    val percentage: Float,
    val hexColor: String
)
