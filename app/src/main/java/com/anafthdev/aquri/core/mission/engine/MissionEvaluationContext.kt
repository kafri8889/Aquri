package com.anafthdev.aquri.core.mission.engine

import com.anafthdev.aquri.core.mission.model.MissionEvent
import com.anafthdev.aquri.data.model.entity.DailySummaryEntity
import com.anafthdev.aquri.data.model.entity.HydrationLogWithBottle
import com.anafthdev.aquri.data.model.entity.UserEntity
import com.anafthdev.aquri.data.model.entity.UserGamificationEntity
import java.util.Locale

/**
 * Snapshot of user state and period data required to evaluate missions.
 *
 * The engine receives a fully prepared context so individual evaluators can stay
 * deterministic and focus only on rule calculation. This also makes evaluators
 * easier to unit test.
 */
data class MissionEvaluationContext(
    val user: UserEntity,
    val now: Long,
    val locale: Locale,
    val todayLogs: List<HydrationLogWithBottle>,
    val weekLogs: List<HydrationLogWithBottle> = emptyList(),
    val allLogs: List<HydrationLogWithBottle> = emptyList(),
    val todaySummary: DailySummaryEntity?,
    val weekSummaries: List<DailySummaryEntity>,
    val allSummaries: List<DailySummaryEntity> = emptyList(),
    val gamification: UserGamificationEntity? = null,
    val lastEvent: MissionEvent? = null
)
