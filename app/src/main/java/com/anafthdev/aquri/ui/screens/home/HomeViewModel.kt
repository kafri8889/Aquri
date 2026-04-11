package com.anafthdev.aquri.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anafthdev.aquri.data.model.entity.BottleEntity
import com.anafthdev.aquri.data.model.entity.DailySummaryEntity
import com.anafthdev.aquri.data.model.entity.DrinkTypeEntity
import com.anafthdev.aquri.data.model.entity.HydrationLogEntity
import com.anafthdev.aquri.data.model.entity.HydrationLogWithBottle
import com.anafthdev.aquri.data.model.entity.UserEntity
import com.anafthdev.aquri.data.model.entity.UserGamificationEntity
import com.anafthdev.aquri.data.repository.HydrationRepository
import com.anafthdev.aquri.data.repository.UserRepository
import com.anafthdev.aquri.utils.DateTimeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val hydrationRepository: HydrationRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val midnightDate: Long
        get() = DateTimeUtils.getMidnight(System.currentTimeMillis())

    val user: StateFlow<UserEntity?> = userRepository.getUser()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val dailySummary: StateFlow<DailySummaryEntity?> = user
        .filterNotNull()
        .flatMapLatest { user ->
            hydrationRepository.getDailySummary(midnightDate)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val bottles: StateFlow<List<BottleEntity>> = hydrationRepository.getAllBottles()
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

    val slotBottles: StateFlow<List<BottleEntity?>> = combine(user.filterNotNull(), bottles) { user, bottles ->
        listOf(
            bottles.find { it.id == user.bottleSlot1 },
            bottles.find { it.id == user.bottleSlot2 },
            bottles.find { it.id == user.bottleSlot3 }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = listOf(null, null, null)
    )

    val recentLogs: StateFlow<List<HydrationLogWithBottle>> = user
        .filterNotNull()
        .flatMapLatest { user ->
            hydrationRepository.getLogsWithBottleByDate(midnightDate)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val gamification: StateFlow<UserGamificationEntity?> = userRepository.getUser()
        .filterNotNull()
        .flatMapLatest { user ->
            userRepository.getGamification(user.id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val reminderSettings = user
        .filterNotNull()
        .flatMapLatest { user ->
            userRepository.getReminderSettings(user.id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val nextReminderTime: StateFlow<Long?> = reminderSettings
        .flatMapLatest { settings ->
            kotlinx.coroutines.flow.flow {
//                if (settings?.enabled == true) {
//                    emit(System.currentTimeMillis() + (settings.intervalMinutes * 60 * 1000))
//                } else {
//                    emit(null)
//                }

                // Simple mock for next reminder: 1 hour from now or based on interval
                // In a real app, this would use the actual scheduled times or interval logic
                emit(System.currentTimeMillis() + (60 * 60 * 1000))
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun logDrink(bottle: BottleEntity, drinkType: DrinkTypeEntity, timestamp: Long) {
        viewModelScope.launch {
            val currentUser = user.value ?: return@launch
            
            val logDate = DateTimeUtils.getMidnight(timestamp)

            hydrationRepository.insertLog(
                HydrationLogEntity(
                    userId = currentUser.id,
                    bottleId = bottle.id,
                    amountMl = bottle.volumeMl,
                    bottleName = bottle.name,
                    drinkTypeId = drinkType.id,
                    loggedAt = timestamp,
                    logDate = logDate
                )
            )

            updateSummaryForDate(currentUser, logDate)
        }
    }

    fun updateLog(log: HydrationLogEntity, bottle: BottleEntity, drinkType: DrinkTypeEntity, timestamp: Long) {
        viewModelScope.launch {
            val currentUser = user.value ?: return@launch
            val oldLogDate = log.logDate
            
            val newLogDate = DateTimeUtils.getMidnight(timestamp)

            hydrationRepository.updateLog(
                log.copy(
                    bottleId = bottle.id,
                    amountMl = bottle.volumeMl,
                    bottleName = bottle.name,
                    drinkTypeId = drinkType.id,
                    loggedAt = timestamp,
                    logDate = newLogDate
                )
            )

            updateSummaryForDate(currentUser, newLogDate)
            if (newLogDate != oldLogDate) {
                updateSummaryForDate(currentUser, oldLogDate)
            }
        }
    }

    fun deleteLog(log: HydrationLogEntity) {
        viewModelScope.launch {
            val currentUser = user.value ?: return@launch
            val logDate = log.logDate
            
            hydrationRepository.deleteLog(log)
            updateSummaryForDate(currentUser, logDate)
        }
    }

    private suspend fun updateSummaryForDate(user: UserEntity, date: Long) {
        val logs = hydrationRepository.getLogsWithBottleByDate(date).first()
        val totalMl = logs.sumOf { it.log.amountMl.toDouble() }.toFloat()
        
        val currentSummary = hydrationRepository.getDailySummary(date).first() ?: DailySummaryEntity(
            userId = user.id,
            summaryDate = date,
            goalMl = user.dailyGoalMl
        )

        hydrationRepository.insertDailySummary(
            currentSummary.copy(
                totalMl = totalMl,
                completionPct = if (currentSummary.goalMl > 0) totalMl / currentSummary.goalMl else 0f,
                goalReached = totalMl >= currentSummary.goalMl
            )
        )
    }
}
