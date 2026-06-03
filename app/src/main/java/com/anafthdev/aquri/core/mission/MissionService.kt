package com.anafthdev.aquri.core.mission

import com.anafthdev.aquri.core.gamification.GamificationService
import com.anafthdev.aquri.core.mission.engine.MissionEngine
import com.anafthdev.aquri.core.mission.engine.MissionEvaluationContext
import com.anafthdev.aquri.core.mission.model.MissionCardModel
import com.anafthdev.aquri.core.mission.model.MissionDashboard
import com.anafthdev.aquri.core.mission.model.MissionDefinition
import com.anafthdev.aquri.core.mission.model.MissionRecurrence
import com.anafthdev.aquri.core.mission.model.MissionStatus
import com.anafthdev.aquri.data.repository.HydrationRepository
import com.anafthdev.aquri.data.repository.UserRepository
import com.anafthdev.aquri.utils.DateTimeUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import java.time.Instant
import java.time.ZoneId
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Application-facing mission facade.
 *
 * This class bridges repositories, mission definitions, persisted mission state,
 * and the evaluation engine to produce a dashboard-ready stream for the feature layer.
 */
@Singleton
@OptIn(ExperimentalCoroutinesApi::class)
class MissionService @Inject constructor(
    private val missionDefinitionSource: MissionDefinitionSource,
    private val missionProgressStore: MissionProgressStore,
    private val missionEngine: MissionEngine,
    private val hydrationRepository: HydrationRepository,
    private val userRepository: UserRepository,
    private val gamificationService: GamificationService
) {

    /**
     * Observes a dashboard model that updates automatically when user data,
     * mission definitions, or claimed mission state changes.
     */
    fun observeDashboard(): Flow<MissionDashboard> = userRepository.getUser()
        .filterNotNull()
        .flatMapLatest { user ->
            combine(
                missionDefinitionSource.observeDefinitions(),
                missionProgressStore.observeClaimedMissionState(),
                hydrationRepository.getLogsWithBottle(user.id),
                hydrationRepository.getDailySummaries(user.id),
                userRepository.getGamification(user.id)
            ) { definitions, claimedState, logs, summaries, gamification ->
                val now = System.currentTimeMillis()
                val today = DateTimeUtils.getMidnight(now)
                val weekRange = DateTimeUtils.getWeekRange(now, Locale.getDefault())
                val activeClaimState = buildActiveClaimState(
                    definitions = definitions,
                    rawClaimedState = claimedState,
                    now = now
                )

                val context = MissionEvaluationContext(
                    user = user,
                    now = now,
                    locale = Locale.getDefault(),
                    todayLogs = logs.filter { it.log.logDate == today },
                    weekLogs = logs.filter { it.log.logDate in weekRange.first..weekRange.second },
                    allLogs = logs,
                    todaySummary = summaries.find { it.summaryDate == today },
                    weekSummaries = summaries.filter { it.summaryDate in weekRange.first..weekRange.second },
                    allSummaries = summaries,
                    gamification = gamification
                )

                val cards = missionEngine.evaluate(
                    definitions = definitions,
                    context = context,
                    claimedMissionState = activeClaimState
                )

                val level = gamification?.currentLevel ?: 1

                MissionDashboard(
                    level = level,
                    levelTitle = gamification?.levelTitle ?: "Hydro Initiate",
                    totalXp = gamification?.totalXp ?: 0,
                    coinBalance = gamification?.coinBalance ?: 0,
                    currentLevelXp = xpRequiredForLevel(level),
                    nextLevelXp = xpRequiredForLevel(level + 1),
                    isPro = user.isPro,
                    currentStreak = gamification?.currentStreak ?: 0,
                    shieldCount = if (user.isPro) {
                        gamification?.shieldCount ?: 0
                    } else {
                        (gamification?.shieldCount ?: 0).coerceAtMost(1)
                    },
                    dailyMissions = cards.filter { it.definition.recurrence == MissionRecurrence.Daily },
                    weeklyMissions = cards.filter { it.definition.recurrence == MissionRecurrence.Weekly },
                    oneTimeMissions = cards.filter { it.definition.recurrence == MissionRecurrence.OneTime },
                    challengePreview = null
                )
            }
        }

    /**
     * Refreshes mission definitions from the active definition source.
     */
    suspend fun refreshDefinitions() {
        missionDefinitionSource.refresh()
    }

    /**
     * Marks a completed mission as claimed.
     *
     * Reward granting is intentionally separate from this state write so future
     * backend synchronization can own the authoritative reward transaction.
     */
    suspend fun claimReward(mission: MissionCardModel) {
        if (mission.progress.status != MissionStatus.Completed) return
        val user = userRepository.getUser().firstOrNull() ?: return
        val isNewClaim = missionProgressStore.markClaimed(mission.definition.id)
        if (!isNewClaim) return

        gamificationService.awardMissionReward(
            user = user,
            xp = mission.definition.reward.xp,
            coins = mission.definition.reward.coins
        )
    }

    private fun xpRequiredForLevel(level: Int): Int {
        if (level <= 1) return 0
        return (level - 1) * (level - 1) * 80
    }

    private fun buildActiveClaimState(
        definitions: List<MissionDefinition>,
        rawClaimedState: Map<String, Long?>,
        now: Long
    ): Map<String, Long?> {
        return definitions.associate { definition ->
            val claimedAt = rawClaimedState[definition.id]
            val activeClaim = claimedAt?.takeIf {
                isClaimActiveForRecurrence(
                    recurrence = definition.recurrence,
                    claimedAt = it,
                    now = now
                )
            }
            definition.id to activeClaim
        }
    }

    private fun isClaimActiveForRecurrence(
        recurrence: MissionRecurrence,
        claimedAt: Long,
        now: Long
    ): Boolean {
        return when (recurrence) {
            MissionRecurrence.Daily -> DateTimeUtils.isSameDay(claimedAt, now)
            MissionRecurrence.Weekly -> {
                val currentWeek = DateTimeUtils.getWeekRange(now, Locale.getDefault())
                claimedAt in currentWeek.first..currentWeek.second
            }
            MissionRecurrence.OneTime -> true
            MissionRecurrence.Monthly -> {
                val claimDate = Instant.ofEpochMilli(claimedAt).atZone(ZoneId.systemDefault()).toLocalDate()
                val currentDate = Instant.ofEpochMilli(now).atZone(ZoneId.systemDefault()).toLocalDate()
                claimDate.year == currentDate.year && claimDate.month == currentDate.month
            }
            MissionRecurrence.Event -> true
        }
    }
}
