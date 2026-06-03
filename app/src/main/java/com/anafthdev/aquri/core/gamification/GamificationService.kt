package com.anafthdev.aquri.core.gamification

import com.anafthdev.aquri.data.model.entity.DailySummaryEntity
import com.anafthdev.aquri.data.model.entity.UserEntity
import com.anafthdev.aquri.data.model.entity.UserGamificationEntity
import com.anafthdev.aquri.data.repository.HydrationRepository
import com.anafthdev.aquri.data.repository.UserRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.sqrt

@Singleton
class GamificationService @Inject constructor(
    private val userRepository: UserRepository,
    private val hydrationRepository: HydrationRepository
) {

    suspend fun awardMissionReward(user: UserEntity, xp: Int, coins: Int) {
        if (xp <= 0 && coins <= 0) return
        val current = currentGamification(user)
        saveProgress(
            user = user,
            gamification = current.copy(
                totalXp = current.totalXp + xp,
                coinBalance = current.coinBalance + coins
            )
        )
    }

    suspend fun updateAfterDailySummary(
        user: UserEntity,
        previousSummary: DailySummaryEntity,
        updatedSummary: DailySummaryEntity
    ) {
        if (!updatedSummary.goalReached && !previousSummary.goalReached) return

        val current = currentGamification(user)
        val summaries = hydrationRepository.getDailySummaries(user.id).first()
        val currentStreak = summaries.currentGoalStreak()
        val goalReachedForFirstTime = !previousSummary.goalReached && updatedSummary.goalReached
        val xpBonus = if (goalReachedForFirstTime) DAILY_GOAL_XP else 0
        val shieldBonus = if (goalReachedForFirstTime) 1 else 0
        val shieldCount = if (user.isPro) {
            current.shieldCount + shieldBonus
        } else {
            (current.shieldCount + shieldBonus).coerceAtMost(FREE_SHIELD_LIMIT)
        }

        saveProgress(
            user = user,
            gamification = current.copy(
                totalXp = current.totalXp + xpBonus,
                currentStreak = currentStreak,
                highestStreak = maxOf(current.highestStreak, currentStreak),
                shieldCount = shieldCount,
                lastActiveDate = updatedSummary.summaryDate
            )
        )
    }

    private suspend fun currentGamification(user: UserEntity): UserGamificationEntity {
        return userRepository.getGamification(user.id).first() ?: UserGamificationEntity(userId = user.id)
    }

    private suspend fun saveProgress(
        user: UserEntity,
        gamification: UserGamificationEntity
    ) {
        val cappedShield = if (user.isPro) {
            gamification.shieldCount
        } else {
            gamification.shieldCount.coerceAtMost(FREE_SHIELD_LIMIT)
        }
        val level = levelForXp(gamification.totalXp)

        userRepository.insertGamification(
            gamification.copy(
                currentLevel = level,
                levelTitle = titleForLevel(level),
                shieldCount = cappedShield
            )
        )
    }

    private fun List<DailySummaryEntity>.currentGoalStreak(): Int {
        return asSequence()
            .sortedByDescending { it.summaryDate }
            .takeWhile { it.goalReached }
            .count()
    }

    private fun levelForXp(totalXp: Int): Int {
        return (sqrt(totalXp.coerceAtLeast(0) / 80.0).toInt() + 1).coerceIn(1, 50)
    }

    private fun titleForLevel(level: Int): String {
        return when {
            level >= 45 -> "Ocean Legend"
            level >= 35 -> "Tide Master"
            level >= 25 -> "Hydro Champion"
            level >= 15 -> "Aqua Ranger"
            level >= 8 -> "Flow Builder"
            else -> "Hydro Initiate"
        }
    }

    private companion object {
        const val DAILY_GOAL_XP = 20
        const val FREE_SHIELD_LIMIT = 1
    }
}
