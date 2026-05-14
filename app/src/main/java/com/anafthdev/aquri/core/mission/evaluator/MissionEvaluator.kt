package com.anafthdev.aquri.core.mission.evaluator

import com.anafthdev.aquri.core.mission.engine.MissionEvaluationContext
import com.anafthdev.aquri.core.mission.model.MissionCardModel
import com.anafthdev.aquri.core.mission.model.MissionDefinition
import com.anafthdev.aquri.core.mission.model.MissionProgress
import com.anafthdev.aquri.core.mission.model.MissionStatus

/**
 * Strategy interface for a family of mission triggers.
 *
 * Each evaluator decides whether it supports a mission definition and produces
 * a normalized progress result from the current evaluation context.
 */
interface MissionEvaluator {
    fun supports(definition: MissionDefinition): Boolean

    fun evaluate(
        definition: MissionDefinition,
        context: MissionEvaluationContext,
        claimedAt: Long?
    ): MissionCardModel
}

/**
 * Shared evaluator base that converts raw progress into a standard mission state.
 */
abstract class BaseMissionEvaluator : MissionEvaluator {

    final override fun evaluate(
        definition: MissionDefinition,
        context: MissionEvaluationContext,
        claimedAt: Long?
    ): MissionCardModel {
        val progress = calculateProgress(definition, context).coerceIn(0f, 1f)
        val status = when {
            claimedAt != null -> MissionStatus.Claimed
            progress >= 1f -> MissionStatus.Completed
            else -> MissionStatus.Active
        }

        return MissionCardModel(
            definition = definition,
            progress = MissionProgress(
                missionId = definition.id,
                progress = progress,
                status = status,
                claimedAt = claimedAt
            )
        )
    }

    protected abstract fun calculateProgress(
        definition: MissionDefinition,
        context: MissionEvaluationContext
    ): Float
}
