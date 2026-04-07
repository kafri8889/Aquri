package com.anafthdev.aquri.ui.screens.statistic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anafthdev.aquri.data.model.entity.DailySummaryEntity
import com.anafthdev.aquri.data.model.entity.HydrationLogWithBottle
import com.anafthdev.aquri.data.model.enum.DrinkType
import com.anafthdev.aquri.data.repository.HydrationRepository
import com.anafthdev.aquri.data.repository.UserRepository
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

    private val _chartData = MutableStateFlow<List<Float>>(emptyList())
    val chartData: StateFlow<List<Float>> = _chartData.asStateFlow()

    private val _peakActivityHour = MutableStateFlow<Int?>(null)
    val peakActivityHour: StateFlow<Int?> = _peakActivityHour.asStateFlow()

    private val _logCount = MutableStateFlow(0)
    val logCount: StateFlow<Int> = _logCount.asStateFlow()

    private val _topBottleName = MutableStateFlow<String?>(null)
    val topBottleName: StateFlow<String?> = _topBottleName.asStateFlow()

    private val _beverageDistribution = MutableStateFlow<Map<DrinkType, Float>>(emptyMap())
    val beverageDistribution: StateFlow<Map<DrinkType, Float>> = _beverageDistribution.asStateFlow()

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
        summaries.find { isSameDay(it.summaryDate, date) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    init {
        combine(_selectedFilter, _selectedDate, dailySummaries, selectedDateLogs, allLogs) { filter, date, summaries, logs, allLogs ->
            val data = prepareData(filter, date, summaries, logs)
            _chartData.value = data
            
            _beverageDistribution.value = calculateBeverageDistribution(filter, date, allLogs)

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

    private fun calculateBeverageDistribution(
        filter: StatisticFilter,
        date: Long,
        logs: List<HydrationLogWithBottle>
    ): Map<DrinkType, Float> {
        val calendar = Calendar.getInstance().apply { timeInMillis = date }
        val filteredLogs = when (filter) {
            StatisticFilter.Daily -> logs.filter { isSameDay(it.log.logDate, date) }
            StatisticFilter.Weekly -> {
                val cal = calendar.clone() as Calendar
                cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
                val start = cal.timeInMillis
                cal.add(Calendar.DAY_OF_WEEK, 6)
                val end = cal.timeInMillis
                logs.filter { it.log.logDate in start..end }
            }
            StatisticFilter.Monthly -> {
                val cal = calendar.clone() as Calendar
                cal.set(Calendar.DAY_OF_MONTH, 1)
                val start = cal.timeInMillis
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
                val end = cal.timeInMillis
                logs.filter { it.log.logDate in start..end }
            }
            StatisticFilter.Yearly -> {
                val cal = calendar.clone() as Calendar
                cal.set(Calendar.DAY_OF_YEAR, 1)
                val start = cal.timeInMillis
                cal.set(Calendar.DAY_OF_YEAR, cal.getActualMaximum(Calendar.DAY_OF_YEAR))
                val end = cal.timeInMillis
                logs.filter { it.log.logDate in start..end }
            }
        }

        if (filteredLogs.isEmpty()) return emptyMap()

        val totalAmount = filteredLogs.sumOf { it.log.amountMl.toDouble() }.toFloat()
        return filteredLogs
            .groupBy { it.log.drinkType }
            .mapValues { (_, drinkLogs) ->
                val drinkAmount = drinkLogs.sumOf { it.log.amountMl.toDouble() }.toFloat()
                (drinkAmount / totalAmount) * 100f
            }
    }

    private fun List<Float>.indexOfMax(): Int? {
        if (isEmpty()) return null
        var maxIndex = 0
        for (i in indices) {
            if (this[i] > this[maxIndex]) maxIndex = i
        }
        return maxIndex
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
                val hourlyData = FloatArray(24)
                logs.forEach { logWithBottle ->
                    val logCal = Calendar.getInstance().apply { timeInMillis = logWithBottle.log.loggedAt }
                    val hour = logCal.get(Calendar.HOUR_OF_DAY)
                    hourlyData[hour] += logWithBottle.log.amountMl
                }
                hourlyData.toList()
            }
            StatisticFilter.Weekly -> {
                // Data for the 7 days of the selected week (Mon-Sun or Sun-Sat based on locale)
                val cal = calendar.clone() as Calendar
                cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
                val weekData = mutableListOf<Float>()
                repeat(7) {
                    val currentDay = cal.timeInMillis
                    val summary = summaries.find { isSameDay(it.summaryDate, currentDay) }
                    weekData.add(summary?.totalMl ?: 0f)
                    cal.add(Calendar.DAY_OF_YEAR, 1)
                }
                weekData
            }
            StatisticFilter.Monthly -> {
                // Data for all days in the selected month
                val cal = calendar.clone() as Calendar
                cal.set(Calendar.DAY_OF_MONTH, 1)
                val maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
                val monthData = mutableListOf<Float>()
                repeat(maxDay) {
                    val currentDay = cal.timeInMillis
                    val summary = summaries.find { isSameDay(it.summaryDate, currentDay) }
                    monthData.add(summary?.totalMl ?: 0f)
                    cal.add(Calendar.DAY_OF_YEAR, 1)
                }
                monthData
            }
            StatisticFilter.Yearly -> {
                // Monthly data for the selected year
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

    private fun isSameDay(date1: Long, date2: Long): Boolean {
        val cal1 = Calendar.getInstance().apply { timeInMillis = date1 }
        val cal2 = Calendar.getInstance().apply { timeInMillis = date2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    fun onFilterSelected(filter: StatisticFilter) {
        _selectedFilter.value = filter
        // Reset selected date to current when filter changes
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
